package data;

import java.io.Serializable;

/**
 * Classe astratta che modella un generico attributo del dataset discreto o continuo.
 * @author Francesco Lavecchia
 */
public abstract class Attribute implements Serializable {
	
	/**
	 * Nome simbolico dell'attributo.
	 */
	private String name;
	
	/**
	 * Identificativo numerico dell'attributo.
	 */
	private int index;
	
	/**
	 * Inizializza i valori dei membri {@code name} e {@code index}.
	 * @param name Nome simbolico dell'attributo.
	 * @param index Identificativo numerico dell'attributo.
	 */
	public Attribute(String name, int index) {
		this.name = name;
		this.index = index;
	}
	
	/**
	 * Restituisce il valore nel membro {@code name}.
	 * @return Nome simbolico dell'attributo {@code name} (di tipo {@link String}).
	 */
	public String getName() {
		return name;
	}
	
	/** 
	 * Restituisce il valore nel membro {@code index}.
	 * @return Identificativo numerico dell'attributo {@code index} (di tipo {@code int}).
	 */
	public int getIndex() {
		return index;
	}
	
	@Override
	public String toString() {
		return getName();
	}
	
}
