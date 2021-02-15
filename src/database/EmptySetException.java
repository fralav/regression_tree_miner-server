package database;

/**
 * La classe modella l'eccezione che si solleva quando viene restituito un result set vuoto.
 * @author Francesco Lavecchia
 *
 */
public class EmptySetException extends Exception {
	
	public EmptySetException() {
        super();
    }

    public EmptySetException(String message) {
        super(message);
    }
}
