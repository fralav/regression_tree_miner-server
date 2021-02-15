package data;

/**
 * Definisce un eccezione per gestire il caso di acquisizione errata del Training Set (file inesistente, schema mancante,
 * training set vuoto o training set privo di variabile target numerica).
 * @author Francesco Lavecchia
 *
 */
public class TrainingDataException extends Exception {

	public TrainingDataException() {
        super();
    }

    public TrainingDataException(String message) {
        super(message);
    }
}
