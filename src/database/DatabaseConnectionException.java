package database;

/**
 * Classe utile a modellare il fallimento nella connessione al database.
 * @author Francesco Lavecchia
 *
 */
public class DatabaseConnectionException extends Exception {
	
	public DatabaseConnectionException() {
		super();
	}
	
	public DatabaseConnectionException(String message) {
		super(message);
	}
}
