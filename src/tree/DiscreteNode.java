package tree;

import java.util.Iterator;
import java.util.ArrayList;
import java.io.Serializable;

import data.Attribute;
import data.DiscreteAttribute;
import data.Data;

/**
 * Classe utile a modellare l'entità nodo di split relativo ad un attributo indipendente discreto.
 * @author Francesco Lavecchia
 *
 */
class DiscreteNode extends SplitNode implements Serializable {

	/**
	 * Istanzia un oggetto invocando il costruttore della superclasse con il parametro {@code attribute}, che è un attributo discreto.
	 * @param trainingSet Training set complessivo
	 * @param beginExampleIndex Indice dell'estremo inferiore del sotto-insieme di training
	 * @param endExampleIndex Indice dell'estremo superiore del sotto-insieme di training
	 * @param attribute Attributo indipendente sul quale si definisce lo split
	 */
	public DiscreteNode(Data trainingSet, int beginExampleIndex, int endExampleIndex, DiscreteAttribute attribute) {
		super(trainingSet, beginExampleIndex, endExampleIndex, attribute);
	}
	
	/**
	 * Istanzia oggetti {@link SplitInfo} (definita come inner class di {@link SplitNode}) con ciascuno dei valori discreti dell'attributo relativamente
	 * al sotto-insieme di training corrente (ossia la porzione di {@code trainingSet} compresa tra {@code beginExampleIndex} e {@code endExampleIndex}),
	 * quindi popola {@code mapSplit} con tali oggetti.
	 */
	@Override
	void setSplitInfo(Data trainingSet, int beginExampleIndex, int endExampleIndex, Attribute attribute) {
		Iterator<String> attributeValues = ((DiscreteAttribute) attribute).iterator();
		ArrayList<Object> presentValues = new ArrayList<Object>();
		for (int i=beginExampleIndex; i<=endExampleIndex; i++) {
			if (!presentValues.contains(trainingSet.getExplanatoryValue(i, attribute.getIndex()))) {
				presentValues.add(trainingSet.getExplanatoryValue(i, attribute.getIndex()));
			}
		}
		for(int i=0; i<presentValues.size(); i++) {
			int begin = i == 0 ? beginExampleIndex : mapSplit.get(i - 1).getEndIndex() + 1;
			int end = begin;
			while (end < endExampleIndex && trainingSet.getExplanatoryValue(end+1, attribute.getIndex()).equals(presentValues.get(i))) {
				end++;
			}
			mapSplit.add(new SplitInfo(attributeValues.next(), begin, end, i));
		}
	}

	/**
	 * Effettua il confronto del valore in input rispetto al valore contenuto nell'attributo {@code splitValue} di ciascuno degli oggetti {@link SplitInfo}
	 * collezionati in {@code mapSplit} e restituisce l'identificativo numerico dello split (indice della posizione nella lista {@code mapSplit}) con cui
	 * il test è positivo.
	 * @param value Valore discreto dell'attributo che si vuole testare rispetto a tutti gli split.
	 * @return Numero del ramo di split.
	 */
	@Override
	int testCondition(Object value) {
		int index = -1;
		for (int i=0; i<mapSplit.size(); i++) {
			if (value.equals(mapSplit.get(i).getSplitValue())) {
				index=i;
			}
		}
		return index;
	}
	
	@Override
	public String toString() {
		return "DISCRETE SPLIT: attribute="+attribute+" "+super.toString();
	}

}
