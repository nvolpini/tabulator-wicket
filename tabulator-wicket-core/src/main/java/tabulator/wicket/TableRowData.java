package tabulator.wicket;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.AbstractMap;
import java.util.Collections;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TableRowData extends AbstractMap<String, Object> {
	
	private static final Logger log = LoggerFactory.getLogger(TableRowData.class);

	final Map<String, Object> rowData;
	
	String rowId;
	
	Integer rowIndex;
	
	//final String rowIdColumnName;

	public TableRowData(Map<String, Object> rowData) {
		this.rowData = rowData;
	}
	
	public TableRowData(String rowId, Integer rowIndex, Map<String, Object> rowData) {
		this(rowData);
		this.rowId = rowId;
		this.rowIndex = rowIndex;
	}
	
	@Override
	public Set<Entry<String, Object>> entrySet() {
		return rowData.entrySet();
	}

	public Optional<String> getRowId() {
		return Optional.ofNullable(rowId);
	}
	
	public Optional<Long> getLong(String columnName) {
		
		try {
			Long r = Optional.ofNullable(rowData.get(columnName)).map(l -> Long.parseLong(l.toString())).orElse(null);
			return Optional.ofNullable(r);
		} catch (Exception e) {
			log.error("Error converting to Long, column: {}, value: {}", columnName, rowData.get(columnName));
		}

		return Optional.empty();
	}

	public Optional<BigDecimal> getBigDecimal(String columnName) {
		
		try {
			BigDecimal r = Optional.ofNullable(rowData.get(columnName)).map(l -> new BigDecimal(l.toString())).orElse(null);
			return Optional.ofNullable(r);
		} catch (Exception e) {
			log.error("Error converting to BigDecimal, column: {}, value: {}", columnName, rowData.get(columnName));
		}

		return Optional.empty();
	}


	public Optional<String> getString(String columnName) {
		
		try {
			String r = Optional.ofNullable(rowData.get(columnName)).map(l -> l.toString()).orElse(null);
			return Optional.ofNullable(r);
		} catch (Exception e) {
			log.error("Error converting to String, column: {}, value: {}", columnName, rowData.get(columnName));
		}

		return Optional.empty();
	}
	

	public Optional<Integer> getInt(String columnName) {
		
		try {
			Integer r = Optional.ofNullable(rowData.get(columnName)).map(l -> Integer.parseInt(l.toString())).orElse(null);
			return Optional.ofNullable(r);
		} catch (Exception e) {
			log.error("Error converting to Integer, column: {}, value: {}", columnName, rowData.get(columnName));
		}

		return Optional.empty();
	}
	
	public Optional<LocalDate> getLocalDate(String columnName) {
		return getString(columnName).flatMap(ds->parseLocalDateISO(ds, true));
	}
	
	public Map<String, Object> getRowData() {
		return Collections.unmodifiableMap(rowData);
	}
	
	/*public String getRowIdColumnName() {
		return rowIdColumnName;
	}
	
	public Optional<Long> getRowIdLong() {
		return getLong(getRowIdColumnName());
	}*/
	
	public static Optional<LocalDate> parseLocalDateISO(String data, boolean tryDateTime) {

		try {

			LocalDate res = LocalDate.parse(data, DateTimeFormatter.ofPattern("yyyy-MM-dd"));

			return Optional.of(res);

		} catch (DateTimeParseException e) {
			//tenta parsear com date time
			
			return tryDateTime ? 
					
					Optional.ofNullable(parseLocalDateTimeISO(data).map(dt->dt.toLocalDate()).orElse(null))
					: Optional.empty()
					;
			
			//return Optional.empty();
		}
	}

	public static Optional<LocalDateTime> parseLocalDateTimeISO(String data) {

		try {

			LocalDateTime res = LocalDateTime.parse(data, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

			return Optional.of(res);

		} catch (DateTimeParseException e) {

			return Optional.empty();
		}
	}

	public Optional<Object> getOr(String columnName) {
		return Optional.ofNullable(get(columnName));
	}
}
