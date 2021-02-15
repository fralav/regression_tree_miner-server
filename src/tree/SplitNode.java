package tree;

import java.io.Serializable;
import java.util.List;
import java.util.ArrayList;

import data.Data;
import data.Attribute;

/**
 * Classe astratta che estende la classe {@link Node}, utile a modellare l'astrazione dell'entità nodo di split (continuo o discreto).
 * @author Francesco Lavecchia
 *
 */
abstract class SplitNode extends Node implements Comparable<SplitNode>, Serializable {

	/**
	 * Classe interna a {@link SplitNode} che aggrega tutte le informazioni riguardanti un nodo di split.
	 * @author franc
	 *
	 */
	class SplitInfo implements Serializable {
				
		/**
		 * Valore di tipo {@link Object} (di un attributo indipendente) che definisce uno split.
		 */
		private Object splitValue;
		
		/**
		 * Indice dell'estremo inferiore del sotto-insieme di training.
		 */
		private int beginIndex;
		
		/**
		 * Indice dell'estremo superiore del sotto-insieme di training.
		 */
		private int endIndex;
		
		/**
		 * Identificativo numerico del figlio del nodo corrente.
		 */
		private int numberChild;
		
		/**
		 * Comparatore che può essere {@literal '='} per i valori discreti, oppure {@literal '<'}, {@literal '<='},
		 * {@literal '>'}, {@literal '>='} per i valori continui.
		 */
		private String comparator = "=";
		
		/**
		 * Costruttore di classe: avvalora gli attributi di classe per split a valori discreti.
		 * @param splitValue Valore di tipo {@link Object} (di un attributo indipendente) che definisce uno split.
		 * @param beginIndex Indice dell'estremo inferiore del sotto-insieme di training.
		 * @param endIndex Indice dell'estremo superiore del sotto-insieme di training.
		 * @param numberChild Identificativo numerico del figlio del nodo corrente.
		 */
		SplitInfo(Object splitValue, int beginIndex, int endIndex, int numberChild) {
			this.splitValue = splitValue;
			this.beginIndex = beginIndex;
			this.endIndex = endIndex;
			this.numberChild = numberChild;
		}
		
		/**
		 * Costruttore di classe: avvalora gli attributi di classe per generici split (da usare per valori continui)
		 * @param splitValue Valore di tipo {@link Object} (di un attributo indipendente) che definisce uno split.
		 * @param beginIndex Indice dell'estremo inferiore del sotto-insieme di training.
		 * @param endIndex Indice dell'estremo superiore del sotto-insieme di training.
		 * @param numberChild Identificativo numerico del figlio del nodo corrente.
		 * @param comparator Comparatore che può essere '=' per i valori discreti, oppure {@literal '<'}, {@literal '<='},
		 * 					 {@literal '>'}, {@literal '>='} per i valori continui.
		 */
		SplitInfo(Object splitValue, int beginIndex, int endIndex, int numberChild, String comparator) {
			this.splitValue = splitValue;
			this.beginIndex = beginIndex;
			this.endIndex = endIndex;
			this.numberChild = numberChild;
			this.comparator = comparator;
		}
		
		/**
		 * Restituisce il valore dello split.
		 * @return Valore dello split {@code splitValue}.
		 */
		Object getSplitValue() {
			return splitValue;
		}
		
		/**
		 * Restituisce l'indice dell'estremo inferiore del sotto-insieme di training.
		 * @return Indice {@code beginIndex} del sotto-insieme di training.
		 */
		int getBeginIndex() {
			return beginIndex;
		}
		
		/**
		 * Restituisce l'indice dell'estremo superiore del sotto-insieme di training.
		 * @return Indice {@code endIndex} del sotto-insieme di training.
		 */
		int getEndIndex() {
			return endIndex;
		}
		
		/**
		 * Restituisce il comparatore utile a comparare un attributo a generici split.
		 * @return Valore del comparatore {@code comparator}.
		 */
		String getComparator() {
			return comparator;
		}
		
		@Override
		public String toString() {
			return "child " + numberChild + ", split value" + comparator + splitValue + " [Examples: " + beginIndex + "-" + endIndex + "]";
		}
	}
	
	/**
	 * Oggetto di tipo {@link Attribute} che modella l'attributo indipendente sul quale lo split è generato.
	 */
	Attribute attribute;
	
	/**
	 * Oggetto di tipo {@link List} utile a memorizzare gli split candidati in una struttura dati di dimensione pari ai possibili valori di test.
	 */
	List<SplitInfo> mapSplit = new ArrayList<SplitInfo>();
	
	/**
	 * Attributo che contiene il valore di varianza a seguito del partizionamento indotto dallo split corrente.
	 */
	double splitVariance;
	
	/**
	 * Metodo astratto utile a generare le informazioni necessarie per ciascuno degli split candidati (in {@code mapSplit}).
	 * @param trainingSet Training set complessivo.
	 * @param beginExampleIndex Indice di estremo inferiore del sotto-insieme di training.
	 * @param endExampleIndex Indice di estremo superiore del sotto-insieme di training.
	 * @param attribute Attributo indipendente sul quale si definisce lo split.
	 */
	abstract void setSplitInfo(Data trainingSet, int beginExampleIndex, int endExampleIndex, Attribute attribute);
	
	/**
	 * Metodo astratto utile per modellare la condizione di test (ad ogni valore di test c'è un ramo dello split).
	 * @param value Valore dell'attributo che si vuole testare rispetto a tutti gli split.
	 * @return Condizione di test tra l'oggetto corrente o l'oggetto definito da {@code value}
	 */
	abstract int testCondition(Object value);
	
	/**
	 * Costruttore di classe: invoca il costruttore della superclasse, ordina i valori dell'attributo di input per gli esempi {@code beginExampleIndex} -
	 * {@code endExampleIndex} e sfrutta questo ordinamento per determinare i possibili split e popolare {@code mapSplit}, computa lo SSE
	 * ({@code splitVariance}) per l'attributo usato nello split sulla base del partizionamento indotto dallo split (lo stesso è la somma degli SSE calcolati
	 * su ciascuno {@link SplitInfo} collezionato in {@code mapSplit}).
	 * @param trainingSet Training set complessivo.
	 * @param beginExampleIndex Indice di estremo inferiore del sotto-insieme di training.
	 * @param endExampleIndex Indice di estremo superiore del sotto-insieme di training.
	 * @param attribute Attributo indipendente sul quale si definisce lo split.
	 */
	
	SplitNode(Data trainingSet, int beginExampleIndex, int endExampleIndex, Attribute attribute) {
		super(trainingSet, beginExampleIndex, endExampleIndex);
		this.attribute = attribute;
		trainingSet.sort(attribute, beginExampleIndex, endExampleIndex);
		setSplitInfo(trainingSet, beginExampleIndex, endExampleIndex, attribute);
		splitVariance=0;
		for (int i=0; i<mapSplit.size(); i++) {
			double localVariance = new LeafNode(trainingSet, mapSplit.get(i).getBeginIndex(), mapSplit.get(i).getEndIndex()).getVariance();
			splitVariance += localVariance;
		}
	}

	/**
	 * Restituisce l'oggetto per l'attributo usato per lo split.
	 * @return Attributo {@code attribute} usato per lo split.
	 */
	Attribute getAttribute() {
		return attribute;
	}
	
	/**
	 * Restituisce l'information gain per lo split corrente.
	 * @return {@code splitVariance} dello split corrente.
	 */
	@Override
	double getVariance() {
		return splitVariance;
	}
	
	/**
	 * Restituisce il numero di figli generabili dal nodo di split corrente.
	 * @return Dimensione di {@code mapSplit}.
	 */
	@Override
	int getNumberOfChildren() {
		return mapSplit.size();
	}
	
	/**
	 * Restituisce le informazioni di split di uno dei figli del nodo di split identificato da {@code child}.
	 * @param child Identificativo numerico del figlio.
	 * @return Oggetto contenente le informazioni di split del figlio con ID {@code child} identificato dall'{@code i}-esimo elemento di {@code mapSplit}.
	 */
	SplitInfo getSplitInfo(int child) {
		return mapSplit.get(child);
	}
	
	/**
	 * Concatena le informazioni di ciascun test (attributo, operatore e valore) in una {@link String} finale: è necessario per la predizione di nuovi
	 * esempi.
	 * @return Query utile alla predizione di nuovi esempi.
	 */
	String formulateQuery() {
		String query = "";
		for (int i=0; i<mapSplit.size(); i++) {
			query += (i + ":" + attribute + mapSplit.get(i).getComparator() + mapSplit.get(i).getSplitValue()) + "\n";
		}
		return query;
	}
	
	@Override
	public String toString() {
		String v = super.toString() + " Split Variance: " + getVariance() + "\n";
		for (int i=0; i<mapSplit.size(); i++) {
			v += "\t" + mapSplit.get(i) + "\n";
		}
		return v;
	}
	
	@Override
	public int compareTo(SplitNode o) {
		if (this.getVariance() < o.getVariance()) {
			return -1;
		} else if (this.getVariance() > o.getVariance()) {
			return 1;
		} else {
			return 0;
		}
	}
}
