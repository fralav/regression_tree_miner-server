package database;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.DriverManager;

/**
 * La classe realizza l'accesso alla base di dati tramite il connettore JDBC.
 * @author Francesco Lavecchia
 *
 */
public class DbAccess {
	
	/**
	 * Nome della classe del driver MySQL.
	 */
	private static String DRIVER_CLASS_NAME = "com.mysql.cj.jdbc.Driver";
	
	/**
	 * Nome del DBMS.
	 */
	private final String DBMS = "jdbc:mysql";
	
	/**
	 * Indirizzo IP del server.
	 */
	private final String SERVER = "localhost";
	
	/**
	 * Nome del database.
	 */
	private final String DATABASE = "MapDB";
	
	/**
	 * Porta del database.
	 */
	private final String PORT = "3306";
	
	/**
	 * User ID per accedere al database.
	 */
	private final String USER_ID = "MapUser";
	
	/**
	 * Password per accedere al database.
	 */
	private final String PASSWORD = "map";
	
	/**
	 * Oggetto che identifica la connessione al database.
	 */
	private Connection conn;
	
	/**
	 * Istanzia un oggetto della classe.
	 */
	public DbAccess() {}
	
	/**
	 * Il metodo realizza la connessione al database conoscendo preventivamente (e forniti negli attributi statici di classe) DBMS, server, nome del database,
	 * porta, user ID e password del database.
	 * @throws DatabaseConnectionException L'eccezione viene sollevata quando si verifica un errore durante l'istanziazione del driver JDBC, quano si verifica un
	 * problema di accesso al database durante il caricamento oppure quando non è possibile caricare il driver.
	 */
	public void initConnection() throws DatabaseConnectionException {
		try {
			Class.forName(DRIVER_CLASS_NAME).newInstance();
		} catch (InstantiationException e) {
			throw new DatabaseConnectionException("[ERRORE] Impossibile caricare il driver.\n" + e.getMessage());
		} catch (IllegalAccessException e) {
			throw new DatabaseConnectionException("[ERRORE] Si è verificato un problema di accesso durante il caricamento.\n" + e.getMessage());
		} catch (ClassNotFoundException e) {
			throw new DatabaseConnectionException("[ERRORE] Impossibile istanziare il driver.\n" + e.getMessage());
		}
		try {
			String url = DBMS + "://" + SERVER + ":" + PORT + "/" + DATABASE + "?serverTimezone=UTC";
			conn = DriverManager.getConnection(url, USER_ID, PASSWORD);
		} catch (SQLException e) {
			throw new DatabaseConnectionException("[ERRORE] Impossibile stabilire la connessione al database.\n" + e.getMessage());
		}
	}
	
	/**
	 * Il metodo fornisce l'oggetto di tipo {@link Connection} che rappresenta la connessione al database.
	 * @return Oggetto {@code conn}, che rappresenta la connessione al database.
	 */
	public Connection getConnection() {
		return conn;
	}
	
	/**
	 * Il metodo si occupa di chiudere la connessione al database.
	 * @throws SQLException L'eccezione viene lanciata quando si verifica un problema durante la chiusura della connessione al database.
	 */
	public void closeConnection() throws SQLException {
		conn.close();
	}
}
