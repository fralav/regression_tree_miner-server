package data;

import java.util.Iterator;
import java.io.Serializable;
import java.util.Set;
import java.util.TreeSet;

/**
 * Estende la classe {@link Attribute} e rappresenta un attributo discreto.
 * @author Francesco Lavecchia
 *
 */
public class DiscreteAttribute extends Attribute implements Iterable<String>, Serializable {
	
	/**
	 * {@link Set} di oggetti di tipo {@link String}, uno per ciascun valore discreto che l'attributo può assumere.
	 */
	private Set<String> values = new TreeSet<String>();
	
	/**
	 * Invoca il costruttore della superclasse tramite gli argomenti {@code name} e {@code index} e avvalora {@code values}
	 * con i valori discreti in input.
	 * @param name Nome simbolico dell'attributo discreto.
	 * @param index Identificativo numerico dell'attributo discreto.
	 * @param values {@link Set} di oggetti {@link String}, uno per ciascun valore che l'attributo può assumere.
	 */
	public DiscreteAttribute(String name, int index, Set<String> values) {
		super(name, index);
		this.values = values;
	}
	
	/**
	 * Restituisce il numero dei valori che l'attributo può assumere
	 * @return Numero dei valori che l'attributo può assumere
	 */
	public int getNumberOfDistinctValues() {
		return values.size();
	}
	
	@Override
	public Iterator<String> iterator() {
		return values.iterator();
	}
}
