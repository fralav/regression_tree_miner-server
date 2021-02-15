package data;

/**
 * Estende la classe {@link Attribute} e rappresenta un attributo continuo.
 * @author Francesco Lavecchia
 *
 */
public class ContinuousAttribute extends Attribute {
	
	/**
	 * Invoca il costruttore della superclasse tramite gli argomenti {@code name} e {@code index}.
	 * @param name Nome simbolico dell'attributo continuo.
	 * @param index Identificativo numerico dell'attributo continuo.
	 */
	public ContinuousAttribute(String name, int index) {
		super(name, index);
	}
}
