package server;

/**
 * Eccezione utile a gestire il caso di acquisizione di un valore mancante o fuori range di un attributo di un nuovo esempio da classificare.
 */
public class UnknownValueException extends Exception {

	public UnknownValueException() {
        super();
    }
	
    public UnknownValueException(String message) {
        super(message);
    }
}
