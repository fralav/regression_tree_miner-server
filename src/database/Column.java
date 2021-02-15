package database;

/**
 * Classe utile a modellare una colonna del database.
 * @author Francesco Lavecchia
 *
 */
public class Column {
	
	/**
	 * Nome della colonna.
	 */
	private String name;
	
	/**
	 * Tipo di dato che la colonna può contenere (numerico o stringa).
	 */
	private String type;
	
	/**
	 * Inizializza i valori dei membri {@code name} e {@code type}.
	 * @param name Nome della colonna.
	 * @param type Tipo di dato che la colonna può contenere (numerico o stringa).
	 */
	Column(String name, String type) {
		this.name = name;
		this.type = type;
	}
	
	/**
	 * Restituisce il nome della colonna.
	 * @return Nome della colonna definito da {@code name}.
	 */
	public String getColumnName() {
		return name;
	}
	
	/**
	 * Stabilisce se la colonna corrente appartiene a un tipo numerico o no.
	 * @return {@code True} se la colonna è di tipo numerico, {@code False} altrimenti.
	 */
	public boolean isNumber() {
		return type.equals("number");
	}
	
	@Override
	public String toString() {
		return name + ":" + type;
	}
}
