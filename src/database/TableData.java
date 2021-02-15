package database;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.HashSet;

/**
 * La classe modella l'insieme di transazioni collezionate in una tabella. La singola transazione è modellata dalla classe {@link Example}.
 * @author Francesco Lavecchia
 *
 */
public class TableData {
	
	/**
	 * Oggetto che rappresenta la connessione al database.
	 */
	private DbAccess db;
		
	/**
	 * Inizializza l'oggetto {@link DbAccess} con il parametro in input.
	 * @param db Oggetto che rappresenta la connessione al database.
	 */
	public TableData(DbAccess db) {
		this.db = db;
	}

	/**
	 * Ricava lo schema dalla tabella con nome {@code table}. Esegue un'interrogazione per estrarre le tuple <i>distinte</i> da tale tabella. Per ogni tupla
	 * del result set si crea un oggetto, istanza della classe {@link Example}, il cui riferimento va incluso nella lista da restituire. In particolare,
	 * per la tupla corrente nel result set, si estraggono i valori dei singoli campi (usando {@code getFloat()} o {@code getString()}), e li si aggiungono
	 * all'oggetto istanza della classe {@link Example} che si sta costruendo. 
	 * @param table Nome della tabella nel database.
	 * @return Lista di transazioni memorizzate nella tabella.
	 * @throws SQLException Eccezione propagata in presenza di errori nella esecuzione della query.
	 * @throws EmptySetException Eccezione propagata quando il result test risulta vuoto.
	 */
	public List<Example> getTransazioni(String table) throws SQLException, EmptySetException {
		LinkedList<Example> transSet = new LinkedList<Example>();
		Statement statement;
		TableSchema tSchema = new TableSchema(db, table);
				
		String query = "select ";
		
		for (int i=0; i<tSchema.getNumberOfAttributes(); i++) {
			Column c = tSchema.getColumn(i);
			if (i > 0) {
				query += ",";
			}
			query += c.getColumnName();
		}
		if (tSchema.getNumberOfAttributes() == 0) {
			throw new SQLException();
		}
		query += (" FROM " + table);
		
		statement = db.getConnection().createStatement();
		ResultSet rs = statement.executeQuery(query);
		boolean empty = true;
		while (rs.next()) {
			empty = false;
			Example currentTuple = new Example();
			for (int i=0; i<tSchema.getNumberOfAttributes(); i++) {
				if (tSchema.getColumn(i).isNumber()) {
					currentTuple.add(rs.getDouble(i + 1));
				} else {
					currentTuple.add(rs.getString(i + 1));
				}
			}
			transSet.add(currentTuple);
		}
		rs.close();
		statement.close();
		if (empty) {
			throw new EmptySetException();	
		}
		return transSet;
	}
	
	/**
	 * Formula ed esegue un'interrogazione SQL per estrarre i valori distinti ordinati di {@code column} e popolare un insieme da restituire.
	 * @param table Nome della tabella.
	 * @param column Nome della colonna nella tabella.
	 * @return Insieme di valori distinti ordinati in modalità ascendente che l'attributo identificato da nome {@code column} assume nella tabella
	 * 		   identificata dal nome {@code table}.
	 * @throws SQLException Eccezione propagata in presenza di errori nella esecuzione della query.
	 * @throws EmptySetException Eccezione propagata quando il result test risulta vuoto.
	 */
	public Set<Object> getDistinctColumnValues(String table, Column column) throws SQLException, EmptySetException {
		Set<Object> transSet = new HashSet<>();
		Statement statement;
		
		TableSchema tSchema = new TableSchema(db, table);
		
		if (tSchema.getNumberOfAttributes() == 0) {
			throw new SQLException();
		}
		String query = "SELECT DISTINCT " + column.getColumnName() + " FROM " + table + " ORDER BY " + column.getColumnName() + " ASC";
		
		statement = db.getConnection().createStatement();
		ResultSet rs = statement.executeQuery(query);
		boolean empty = true;
		while (rs.next()) {
			empty = false;
			transSet.add(rs.getObject(1));
		}
		rs.close();
		statement.close();
		if (empty) {
			throw new EmptySetException();
		}
		return transSet;
	}
}
