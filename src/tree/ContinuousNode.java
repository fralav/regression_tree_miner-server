package tree;

import java.util.ArrayList;
import java.util.List;
import java.io.Serializable;

import data.Attribute;
import data.ContinuousAttribute;
import data.Data;

/**
 * La classe rappresenta un nodo corrispondente ad un attributo continuo.
 * @author Francesco Lavecchia
 *
 */
public class ContinuousNode extends SplitNode implements Serializable {

	/**
	 * Istanzia un oggetto invocando il costruttore della superclasse con il parametro {@code attribute}, che è un attributo continuo.
	 * @param trainingSet Training set complessivo
	 * @param beginExampleIndex Indice dell'estremo inferiore del sotto-insieme di training
	 * @param endExampleIndex Indice dell'estremo superiore del sotto-insieme di training
	 * @param attribute Attributo indipendente sul quale si definisce lo split
	 */
	public ContinuousNode(Data trainingSet, int beginExampleIndex, int endExampleIndex, ContinuousAttribute attribute) {
		super(trainingSet, beginExampleIndex, endExampleIndex, attribute);
	}

	/**
	 * Istanzia oggetti {@link SplitInfo} (definita come inner class di {@link SplitNode}) all'interno della lista {@code bestMapSplit}.
	 * Si determina lo split migliore in base ai valori continui presenti nella porzione di data set definita nell'intervallo
	 * {@code beginExampleIndex}-{@code endExampleIndex}, quindi genera tutti gli split possibili per sceglierne quello con la varianza
	 * minore.
	 */
	@Override
	void setSplitInfo(Data trainingSet, int beginExampleIndex, int endExampleIndex, Attribute attribute) {
		Double currentSplitValue = (Double) trainingSet.getExplanatoryValue(beginExampleIndex, attribute.getIndex());
		double bestInfoVariance = 0;
		List<SplitInfo> bestMapSplit = null;
		
		for (int i=beginExampleIndex+1; i<=endExampleIndex; i++) {
			Double value = (Double) trainingSet.getExplanatoryValue(i, attribute.getIndex());
			if (value.doubleValue() != currentSplitValue.doubleValue()) {
				double localVariance = new LeafNode(trainingSet, beginExampleIndex, i - 1).getVariance();
				double candidateSplitVariance = localVariance;
				localVariance = new LeafNode(trainingSet, i, endExampleIndex).getVariance();
				candidateSplitVariance += localVariance;
				if (bestMapSplit == null) {
					bestMapSplit = new ArrayList<SplitInfo>();
					bestMapSplit.add(new SplitInfo(currentSplitValue, beginExampleIndex, i-1, 0, "<="));
					bestMapSplit.add(new SplitInfo(currentSplitValue, i, endExampleIndex, 1, ">"));
					bestInfoVariance = candidateSplitVariance;
				} else {
					if (candidateSplitVariance < bestInfoVariance) {
						bestInfoVariance = candidateSplitVariance;
						bestMapSplit.set(0, new SplitInfo(currentSplitValue, beginExampleIndex, i-1, 0, "<="));
						bestMapSplit.set(1, new SplitInfo(currentSplitValue, i, endExampleIndex, 1, ">"));
					}
				}
				currentSplitValue=value;
			}
		}
		if (bestMapSplit != null) {
			mapSplit = bestMapSplit;
			if (mapSplit.get(1).getBeginIndex() == mapSplit.get(1).getEndIndex()) {
				mapSplit.remove(1);
			}
		}
	}

	@Override
	int testCondition(Object value) {
		return 0;
	}
	
	@Override
	public String toString() {
		return "CONTINUOUS SPLIT: attribute=" + attribute + " " + super.toString();
	}
}
