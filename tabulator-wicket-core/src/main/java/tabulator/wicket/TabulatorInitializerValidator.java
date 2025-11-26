package tabulator.wicket;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.hjson.JsonValue;
import org.hjson.Stringify;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

public class TabulatorInitializerValidator {

    private static final Logger log = LoggerFactory.getLogger(TabulatorInitializerValidator.class);
    private final ObjectMapper mapper = new ObjectMapper();

    public enum ValidationMode { STRICT, LENIENT }

    private final ValidationMode mode;

    private final Map<String, String> functionsMap = new LinkedHashMap<>();
    private int funcCounter = 0;

    private final Map<String, String> symbolsMap = new LinkedHashMap<>();
    private int symbolCounter = 0;
    
    public TabulatorInitializerValidator(ValidationMode mode) {
        this.mode = mode;
    }

    // ------------------------------------------------------------
    // 1) Novo extrator completo: preamble + jsonObject + postamble
    // ------------------------------------------------------------
    public TabulatorTemplateParts extractTemplateParts(String rendered) {

        int tabPos = rendered.indexOf("new Tabulator");
        if (tabPos < 0) {
            // fallback – não encontrou Tabulator; trata tudo como preamble
            return new TabulatorTemplateParts(rendered, "{}", "");
        }

        // acha o primeiro '{' após "new Tabulator("
        int parenOpen = rendered.indexOf('(', tabPos);
        int braceOpen = rendered.indexOf('{', parenOpen);

        if (braceOpen < 0) {
            // sem objeto — devolve tudo como preamble
            return new TabulatorTemplateParts(rendered, "{}", "");
        }

        int braceClose = findMatchingBrace(rendered, braceOpen);
        if (braceClose < 0) {
            if (mode == ValidationMode.STRICT) {
                throw new TabulatorWicketException("Chaves não balanceadas no template Tabulator.");
            }
            log.warn("Chaves não balanceadas.");
            return new TabulatorTemplateParts(rendered, "{}", "");
        }

        String preamble  = rendered.substring(0, braceOpen);
        String json      = rendered.substring(braceOpen, braceClose + 1);
        String postamble = rendered.substring(braceClose + 1);

        return new TabulatorTemplateParts(preamble, json, postamble);
    }

    // --------------------------------------------------------------------
    // 2) Parseia APENAS o jsonObject do Tabulator (extraído acima)
    // --------------------------------------------------------------------
    public ObjectNode parseJsonObject(String jsObjectRaw) {

        functionsMap.clear();
        funcCounter = 0;

        symbolsMap.clear();
        symbolCounter = 0;
        
        String withPlaceholders = replaceFunctionsWithPlaceholders(jsObjectRaw);
        String withSymbols = replaceJsSymbols(withPlaceholders);


        try {
            String jsonText = JsonValue.readHjson(withSymbols).toString(Stringify.PLAIN);
            return (ObjectNode) mapper.readTree(jsonText);

        } catch (Exception e) {
            String msg = "Erro parseando JSON5 do initializer";
            if (mode == ValidationMode.STRICT)
                throw new TabulatorWicketException(msg, e);
            log.warn("{}: {}", msg, e.getMessage());
            return mapper.createObjectNode();
        }
    }

    // --------------------------------------------------------------------
    // 3) Método antigo, agora compatível (mas usa o novo extrator)
    // --------------------------------------------------------------------
    public ObjectNode extractAndParseFromRenderedString(String rendered) {
        TabulatorTemplateParts parts = extractTemplateParts(rendered);
        return parseJsonObject(parts.jsonObject());
    }

    // --------------------------------------------------------------------
    // 4) Função de restauração
    // --------------------------------------------------------------------
    public String restoreFunctions(String jsonWithPlaceholders) {
        String out = jsonWithPlaceholders;
        for (var e : functionsMap.entrySet()) {
            out = out.replace(e.getKey(), e.getValue());
            // tentar sem aspas também
            String bare = e.getKey().replace("\"", "");
            out = out.replace(bare, e.getValue());
        }
        return out;
    }

    // --------------------------------------------------------------------
    // 5) Localizador e substituidor de funções JS
    // --------------------------------------------------------------------
    private String replaceFunctionsWithPlaceholders(String text) {
        StringBuilder sb = new StringBuilder();
        int idx = 0;
        int len = text.length();

        while (idx < len) {
            int funcPos = indexOfIgnoringStrings(text, "function", idx);
            int arrowPos = indexOfIgnoringStrings(text, "=>", idx);

            int found = -1;
            boolean isArrow = false;

            if (funcPos >= 0 && (arrowPos < 0 || funcPos < arrowPos)) {
                found = funcPos;
            } else if (arrowPos >= 0) {
                found = arrowPos;
                isArrow = true;
            }

            if (found < 0) {
                sb.append(text.substring(idx));
                break;
            }

            if (!isArrow) {
                // normal function(...)
                sb.append(text, idx, found);
                int braceOpen = text.indexOf('{', found);
                int braceClose = findMatchingBrace(text, braceOpen);
                String funcText = text.substring(found, braceClose + 1);
                String placeholder = "\"" + nextFuncPlaceholder() + "\"";
                functionsMap.put(placeholder, funcText);
                sb.append(placeholder);
                idx = braceClose + 1;
            } else {
                // arrow function
                int funcStart = backtrackToValueStart(text, found);
                sb.append(text, idx, funcStart);

                int braceOpen = text.indexOf('{', found);
                int braceClose = findMatchingBrace(text, braceOpen);

                String funcText = text.substring(funcStart, braceClose + 1);
                String placeholder = "\"" + nextFuncPlaceholder() + "\"";
                functionsMap.put(placeholder, funcText);
                sb.append(placeholder);
                idx = braceClose + 1;
            }
        }
        return sb.toString();
    }
    
    public String restoreSymbols(String json) {
        String result = json;
        for (Map.Entry<String, String> e : symbolsMap.entrySet()) {
            String quotedPlaceholder = e.getKey(); // ex: "__SYMBOL_1__" mas com aspas
            String symbol = e.getValue();         // ex: statusContextMenu
            // substitui o placeholder (incl. aspas) pelo símbolo cru
            result = result.replace(quotedPlaceholder, symbol);
        }
        return result;
    }


    /**
     * Substitui valores que são identificadores JS puros (ex: statusContextMenu)
     * por placeholders entre aspas "__SYMBOL_n__" para que Jackson/HJSON não os transforme em strings.
     *
     * Observação: recebe o texto já com funções substituídas (placeholders) para evitar confundir
     * arrow functions e parâmetros.
     */
    private String replaceJsSymbols(String text) {
        // Regex: captura um identificador que aparece como value após ':' e antes de ',', '}' ou ']'
        // (?<=:\s*)  -> assertiva lookbehind: estamos logo após ':'
        // ([A-Za-z_$][A-Za-z0-9_$]*) -> captura identificador JS válido
        // (?=\s*[,}\]]) -> assertiva lookahead: seguido por vírgula, } ou ]
        Pattern p = Pattern.compile("(?<=:\\s*)([A-Za-z_$][A-Za-z0-9_$]*)(?=\\s*[,}\\]])");
        Matcher m = p.matcher(text);
        StringBuffer sb = new StringBuffer();

        while (m.find()) {
            String symbol = m.group(1);

            // ignore boolean/null/number-like keywords (só por segurança)
            if ("true".equals(symbol) || "false".equals(symbol) || "null".equals(symbol)) {
                continue;
            }

            // cria placeholder e armazena
            String placeholder = "\"__SYMBOL_" + (++symbolCounter) + "__\"";
            symbolsMap.put(placeholder, symbol);

            // substitui o token encontrado pelo placeholder (mantendo espaços e pontuação)
            m.appendReplacement(sb, Matcher.quoteReplacement(placeholder));
        }
        m.appendTail(sb);
        return sb.toString();
    }

    
    private int backtrackToValueStart(String text, int pos) {
        int c = findPreviousColon(text, pos - 1);
        if (c >= 0) {
            int i = c + 1;
            while (i < pos && Character.isWhitespace(text.charAt(i))) i++;
            return i;
        }
        int i = pos;
        while (i > 0 && Character.isWhitespace(text.charAt(i - 1))) i--;
        while (i > 0 && (Character.isJavaIdentifierPart(text.charAt(i - 1)) 
                || text.charAt(i - 1) == '(' || text.charAt(i - 1) == ')'))
            i--;
        return i;
    }

    private int findPreviousColon(String text, int from) {
        boolean inS = false, inD = false;
        for (int i = from; i >= 0; i--) {
            char c = text.charAt(i);
            if (c == '"' && !inS) inD = !inD;
            else if (c == '\'' && !inD) inS = !inS;
            else if (!inS && !inD && c == ':') return i;
        }
        return -1;
    }

    private int indexOfIgnoringStrings(String text, String token, int from) {
        boolean inS = false, inD = false;
        for (int i = from; i <= text.length() - token.length(); i++) {
            char c = text.charAt(i);
            if (c == '"' && !inS) inD = !inD;
            else if (c == '\'' && !inD) inS = !inS;
            else if (!inS && !inD && text.startsWith(token, i)) return i;
        }
        return -1;
    }

    private int findMatchingBrace(String text, int startIndex) {
        int depth = 0;
        boolean inS = false, inD = false;

        for (int i = startIndex; i < text.length(); i++) {
            char c = text.charAt(i);
            if (c == '"' && !inS) inD = !inD;
            else if (c == '\'' && !inD) inS = !inS;
            else if (!inS && !inD) {
                if (c == '{') depth++;
                else if (c == '}') {
                    depth--;
                    if (depth == 0) return i;
                }
            }
        }
        return -1;
    }
    
    /**
     * Encontra o próximo ':' no texto começando de 'start', ignorando ':' que estejam dentro de
     * strings (aspas simples ou duplas) e ignorando escapes.
     *
     * @param text  texto a ser examinado
     * @param start índice inicial da busca
     * @return índice do ':' válido ou -1 se não houver
     */
    private int findNextColon(String text, int start) {
        boolean inSingle = false;
        boolean inDouble = false;
        for (int i = start; i < text.length(); i++) {
            char c = text.charAt(i);
            if (c == '"' && !inSingle) {
                inDouble = !inDouble;
                continue;
            }
            if (c == '\'' && !inDouble) {
                inSingle = !inSingle;
                continue;
            }
            if (!inSingle && !inDouble && c == ':') {
                return i;
            }
            if (c == '\\') {
                // pula o próximo char (escape)
                i++;
            }
        }
        return -1;
    }

    private String nextFuncPlaceholder() {
        funcCounter++;
        return "__FUNC_" + funcCounter + "__";
    }
}
