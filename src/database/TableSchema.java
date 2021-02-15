package database;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

/**
 * Classe che modella lo schema di una tabella nel database relazionale.
 * @author Francesco Lavecchia
 *
 */
public class TableSchema implements Iterable<Column> {
	
	/**
	 * Lista di oggetti di tipo {@link Column} che rappresenta lo schema del database.
	 */
	private List<Column> tableSchema = new ArrayList<Column>();
	
	/**
	 * Il metodo si occupa di modellare lo schema del database relazionale.
	 * @param db Oggetto che rappresenta la connessione al database.
	 * @param tableName Nome della tabella.
	 * @throws SQLException Eccezione propagata in presenza di errori nella esecuzione della query.
	 */
	public TableSchema(DbAccess db, String tableName) throws SQLException {
		HashMap<String, String> mapSQL_JAVATypes = new HashMap<String, String>();
		
		mapSQL_JAVATypes.put("CHAR", "string");
		mapSQL_JAVATypes.put("VARCHAR", "string");
		mapSQL_JAVATypes.put("LONGVARCHAR", "string");
		mapSQL_JAVATypes.put("BIT", "string");
		mapSQL_JAVATypes.put("SHORT", "number");
		mapSQL_JAVATypes.put("INT", "number");
		mapSQL_JAVATypes.put("LONG", "number");
		mapSQL_JAVATypes.put("FLOAT", "number");
		mapSQL_JAVATypes.put("DOUBLE", "number");
		
		Connection con = db.getConnection();
		DatabaseMetaData meta = con.getMetaData();
		ResultSet res = meta.getColumns(null, null, tableName, null);
	
		while (res.next()) {
			if (mapSQL_JAVATypes.containsKey(res.getString("TYPE_NAME"))) {
				tableSchema.add(new Column(res.getString("COLUMN_NAME"), mapSQL_JAVATypes.get(res.getString("TYPE_NAME"))));
			}
		}
		res.close();
	}
	
	/**
	 * Il metodo restituisce il numero degli attributi indipendenti nella tabella corrente del database relazionale.
	 * @return Dimensione di {@code tableSchema}, ovvero il numero degli attributi indipendenti nella tabella.
	 */
	public int getNumberOfAttributes() {
		return tableSchema.size();
	}
	
	/**
	 * Il metodo restituisce la {@code i}-esima colonna della tabella nel database relazionale.
	 * @param index Identificatore numerico della colonna.
	 * @return {@code i}-esima colonna della tabella modellata.
	 */
	public Column getColumn(int index) {
		return tableSchema.get(index);
	}
	
	@Override
	public Iterator<Column> iterator() {
		return tableSchema.iterator();
	}

}
