package database;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * La classe modella una singola transazione letta dalla base di dati.
 * @author Francesco Lavecchia
 */
public class Example implements Comparable<Example>, Iterable<Object> {
	
	/**
	 * Lista di dimensioni pari al numero delle colonne del database, che modella una tupla del database.
	 */
	private List<Object> example=new ArrayList<Object>();
	
	/**
	 * Istanzia un oggetto della classe.
	 */
	Example() {}

	/**
	 * Il metodo si occupa di allocare un elemento del database alla tupla corrente.
	 * @param o Elemento da allocare alla tupla corrente.
	 */
	public void add(Object o) {
		example.add(o);
	}
	
	/**
	 * Il metodo si occupa di restituire l'elemento {@code i}-esimo della tupla corrente.
	 * @param i Elemento della tupla corrente che si vuole restituire.
	 * @return {@code i}-esimo elemento della tupla rappresentata da {@code example}.
	 */
	public Object get(int i) {
		return example.get(i);
	}

	@Override
	public int compareTo(Example ex) {
		int i = 0;
		for (Object o : ex.example) {
			if (!o.equals(this.example.get(i))) {
				return ((Comparable) o).compareTo(example.get(i));
			}
			i++;
		}
		return 0;
	}
	
	@Override
	public String toString() {
		String str = "";
		for(Object o : example) {
			str += o.toString() + " ";
		}
		return str;
	}


	@Override
	public Iterator<Object> iterator() {
		return null;
	}
}