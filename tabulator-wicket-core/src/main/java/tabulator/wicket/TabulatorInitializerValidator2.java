package tabulator.wicket;

import java.util.LinkedHashMap;
import java.util.Map;

import org.hjson.JsonValue;
import org.hjson.Stringify;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

/**
 * Validator/Extractor para os templates Tabulator com suporte a funções JS.
 *
 * Fluxo:
 * 1) Recebe o template já interpolado (initializer.generateScript(...))
 * 2) Substitui funções JS por placeholders ("__FUNC_n__")
 * 3) Usa json5-java para parsear o conteúdo em JSON válido
 * 4) Retorna um ObjectNode (Jackson)
 * 5) restoreFunctions() recoloca as funções originais no script final
 */
public class TabulatorInitializerValidator2 {

    private static final Logger log = LoggerFactory.getLogger(TabulatorInitializerValidator2.class);
    private final ObjectMapper mapper = new ObjectMapper();

    public enum ValidationMode {
        STRICT,   // lança exception em caso de erro
        LENIENT   // apenas loga e retorna vazio
    }

    private final ValidationMode mode;

    // Armazena funções JS substituídas por placeholders
    private final Map<String, String> functionsMap = new LinkedHashMap<>();
    private int funcCounter = 0;

    public TabulatorInitializerValidator2(ValidationMode mode) {
        this.mode = mode;
    }

    /**
     * Extrai o objeto JS principal do template (primeiro '{' balanceado),
     * substitui funções e faz o parse via JSON5.
     */
    public ObjectNode extractAndParseFromRenderedString(String rendered) {
        functionsMap.clear();
        funcCounter = 0;

     // Extrai o objeto de opções após "new Tabulator("
        int open = -1;
        int close = -1;
        int tabulatorPos = rendered.indexOf("new Tabulator");

        if (tabulatorPos >= 0) {
            // procura o primeiro '{' depois do parêntese de abertura do Tabulator
            int parenOpen = rendered.indexOf('(', tabulatorPos);
            int parenClose = rendered.indexOf(')', parenOpen + 1);
            open = rendered.indexOf('{', parenOpen);
        } else {
            // fallback: primeiro '{' global
            open = rendered.indexOf('{');
        }

        if (open >= 0) {
            close = findMatchingBrace(rendered, open);
        }

        if (open < 0 || close < 0) {
            if (mode == ValidationMode.STRICT)
                throw new TabulatorWicketException("Não foi possível encontrar o objeto de opções no template Tabulator.");
            log.error("Falha ao localizar objeto { ... } no template Tabulator.");
            return mapper.createObjectNode();
        }

        String jsObject = rendered.substring(open, close + 1).trim();
        
        //log.debug("Extracted JS object snippet:\n{}",jsObject);

        // Substitui funções JS por placeholders
        String withPlaceholders = replaceFunctionsWithPlaceholders(jsObject);

        try {
        	
            String jsonText = JsonValue.readHjson(withPlaceholders).toString(Stringify.PLAIN);


            //log.debug("Replaced JS object snippet:\n{}", jsonText);


            // Converte para ObjectNode (Jackson)
            return (ObjectNode) mapper.readTree(jsonText);

        } catch (Exception e) {
            String msg = "Erro parseando JSON5 do initializer";
            if (mode == ValidationMode.STRICT)
                throw new TabulatorWicketException(msg, e);
            log.warn("{}: {}", msg, e.getMessage());
            return mapper.createObjectNode();
        }
    }

    /**
     * Substitui funções JS (function/arrow) por placeholders "__FUNC_n__" entre aspas.
     */
    private String replaceFunctionsWithPlaceholders(String text) {
        StringBuilder sb = new StringBuilder();
        int idx = 0, len = text.length();

        while (idx < len) {
            int funcPos = indexOfIgnoringStrings(text, "function", idx);
            int arrowPos = indexOfIgnoringStrings(text, "=>", idx);
            int found = -1;
            boolean isArrow = false;

            if (funcPos >= 0 && (arrowPos < 0 || funcPos < arrowPos)) {
                found = funcPos;
                isArrow = false;
            } else if (arrowPos >= 0) {
                found = arrowPos;
                isArrow = true;
            }

            if (found < 0) {
                sb.append(text.substring(idx));
                break;
            }

            if (!isArrow) {
                // normal function: copy until 'function' and parse from there
                sb.append(text, idx, found);
                int braceOpen = text.indexOf('{', found);
                if (braceOpen < 0) {
                    sb.append(text.substring(found));
                    break;
                }
                int braceClose = findMatchingBrace(text, braceOpen);
                if (braceClose < 0) {
                    sb.append(text.substring(found));
                    break;
                }
                String funcText = text.substring(found, braceClose + 1);
                String placeholder = "\"" + nextFuncPlaceholder() + "\"";
                functionsMap.put(placeholder, funcText);
                sb.append(placeholder);
                idx = braceClose + 1;
            } else {
                // arrow function: retrocede para o começo do valor e substitui tudo
                int funcStart = backtrackToValueStart(text, found);
                // copia tudo até funcStart (isso evita deixar "(cell) " sobrando)
                sb.append(text, idx, funcStart);
                int braceOpen = text.indexOf('{', found);
                if (braceOpen < 0) {
                    // fallback se não achar '{'
                    sb.append(text.substring(funcStart));
                    break;
                }
                int braceClose = findMatchingBrace(text, braceOpen);
                if (braceClose < 0) {
                    sb.append(text.substring(funcStart));
                    break;
                }
                String funcText = text.substring(funcStart, braceClose + 1);
                String placeholder = "\"" + nextFuncPlaceholder() + "\"";
                functionsMap.put(placeholder, funcText);
                sb.append(placeholder);
                idx = braceClose + 1;
            }
        }

        return sb.toString();
    }
    /**
     * Retrocede do índice 'pos' (onde encontra '=>') até o início da expressão de valor.
     * Tenta encontrar ':' antes; se achar, retorna char logo após ':'.
     * Senão, retrocede até começo do token/expressão.
     */
    private int backtrackToValueStart(String text, int pos) {
        // procura ':' antes do pos (ignora strings)
        int colon = findPreviousColon(text, pos - 1);
        if (colon >= 0) {
            int start = colon + 1;
            while (start < pos && Character.isWhitespace(text.charAt(start))) start++;
            return start;
        }
        // sem colon: volta até primeiro caractere não space
        int i = pos;
        while (i > 0 && Character.isWhitespace(text.charAt(i - 1))) i--;
        // tenta retroceder até começo do token (identificador ou parêntese)
        while (i > 0 && (Character.isJavaIdentifierPart(text.charAt(i - 1)) || text.charAt(i - 1) == '(' || text.charAt(i - 1) == ')')) {
            i--;
        }
        return i;
    }

    private int findPreviousColon(String text, int from) {
        boolean inSingle = false, inDouble = false;
        for (int i = from; i >= 0; i--) {
            char c = text.charAt(i);
            if (c == '"' && !inSingle) inDouble = !inDouble;
            else if (c == '\'' && !inDouble) inSingle = !inSingle;
            else if (!inSingle && !inDouble && c == ':') return i;
            else if (c == '\\') i--; // skip escaped
        }
        return -1;
    }


    private String nextFuncPlaceholder() {
        funcCounter++;
        return "__FUNC_" + funcCounter + "__";
    }

    /**
     * Busca índice de token ignorando trechos dentro de aspas simples/dobradas.
     */
    private int indexOfIgnoringStrings(String text, String token, int from) {
        boolean inSingle = false, inDouble = false;
        for (int i = from; i <= text.length() - token.length(); i++) {
            char c = text.charAt(i);
            if (c == '"' && !inSingle) inDouble = !inDouble;
            else if (c == '\'' && !inDouble) inSingle = !inSingle;
            else if (!inSingle && !inDouble && text.startsWith(token, i))
                return i;
            if (c == '\\') i++;
        }
        return -1;
    }

    /**
     * Localiza o '}' correspondente ao '{' inicial, ignorando strings e escapes.
     */
    private int findMatchingBrace(String text, int startIndex) {
        int depth = 0;
        boolean inSingle = false, inDouble = false;

        for (int i = startIndex; i < text.length(); i++) {
            char c = text.charAt(i);
            if (c == '"' && !inSingle) inDouble = !inDouble;
            else if (c == '\'' && !inDouble) inSingle = !inSingle;
            else if (!inSingle && !inDouble) {
                if (c == '{') depth++;
                else if (c == '}') {
                    depth--;
                    if (depth == 0) return i;
                }
            }
            if (c == '\\') i++;
        }
        return -1;
    }

    /**
     * Restaura funções originais (sem aspas) nos lugares dos placeholders.
     */
    public String restoreFunctions(String jsonWithPlaceholders) {
        String result = jsonWithPlaceholders;
        for (Map.Entry<String, String> e : functionsMap.entrySet()) {
            String quoted = e.getKey();
            String funcText = e.getValue();
            result = result.replace(quoted, funcText);
            if (quoted.startsWith("\"") && quoted.endsWith("\"")) {
                String bare = quoted.substring(1, quoted.length() - 1);
                result = result.replace(bare, funcText);
            }
        }
        return result;
    }
}
