package tree;

import java.io.Serializable;
import data.Data;

/**
 * Classe che estende la superclasse {@link Node} utile a modellare l'entità nodo fogliare.
 * @author Francesco Lavecchia
 *
 */
class LeafNode extends Node implements Serializable {
	
	/**
	 * Valore dell'attributo di classe espresso nella foglia corrente.
	 */
	private Double predictedClassValue;
	
	/**
	 * Istanzia un oggetto invocando il costruttore della superclasse e avvalora l'attributo {@code predictedClassValue} (come media dei valori
	 * dell'attributo di classe che ricadono nella partizione corrente, ossia la porzione di {@code trainingSet} compresa tra 
	 * {@code beginExampleIndex} e {@code endExampleIndex}).
	 * @param trainingSet Training set complessivo.
	 * @param beginExampleIndex Indice dell'estremo inferiore del sotto-insieme di training.
	 * @param endExampleIndex Indice dell'estremo superiore del sotto-insieme di training.
	 */
	LeafNode(Data trainingSet, int beginExampleIndex, int endExampleIndex) {
		super(trainingSet, beginExampleIndex, endExampleIndex);
		double average = 0.0;
		for (int i = beginExampleIndex; i <= endExampleIndex; i++) {
			average += trainingSet.getClassValue(i);
		}
		predictedClassValue = average / (endExampleIndex - beginExampleIndex + 1);
	}
	
	/**
	 * Restituisce il valore del membro {@code predictedClassValue}.
	 * @return Valore del membro {@code predictedClassValue}.
	 */
	Double getPredictedClassValue() {
		return predictedClassValue;
	}	
	
	/**
	 * Restituisce il numero di split originati dal nodo foglia, ovvero 0.
	 */
	@Override
	int getNumberOfChildren() {
		return 0;
	}
	
	@Override
	public String toString() {
		return "LEAF: Class=" + predictedClassValue + " " + super.toString();
	}

}
