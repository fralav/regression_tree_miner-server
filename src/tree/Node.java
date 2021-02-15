package tree;

import java.io.Serializable;

import data.Data;

/**
 * Classe astratta che permette di modellare l'astrazione dell'entità nodo (fogliare o intermedio) dell'albero di decisione.
 * @author Francesco Lavecchia
 *
 */
abstract class Node implements Serializable {
	
	/**
	 * Contatore dei nodi generati dall'albero.
	 */
	static int idNodeCount = 0;
	
	/**
	 * Identificativo numerico del nodo.
	 */
	private int idNode;
	
	/**
	 * Indice nel Set del training set del primo esempio coperto dal nodo corrente. {@code beginExampleIndex} e {@code endExampleIndex} individuano un
	 * sottoinsieme di training.
	 */
	private int beginExampleIndex;
	
	/**
	 * Indice nel Set del training set dell'ultimo esempio coperto dal nodo corrente. {@code beginExampleIndex} e {@code endExampleIndex} individuano un
	 * sottoinsieme di training.
	 */
	private int endExampleIndex;
	
	/**
	 * Valore dello <b>SSE</b> calcolato, rispetto all'attributo di classe, nel sotto-insieme di training del nodo corrente.
	 */
	private double variance;
	
	
	/**
	 * Costruttore di classe: avvalora gli attributi primitivi di classe, inclusa la varianza che viene calcolata rispetto all'attributo da predire nel
	 * sotto-insieme di training coperto dal nodo corrente.
	 * @param trainingSet Oggetto di classe {@link Data} contenente il training set completo.
	 * @param beginExampleIndex Indice di estremo inferiore che identifica il sotto-insieme di training coperto dal nodo corrente.
	 * @param endExampleIndex Indice di estremo superiore che identifica il sotto-insieme di training coperto dal nodo corrente.
	 */
	Node(Data trainingSet, int beginExampleIndex, int endExampleIndex) {
		idNode = idNodeCount;
		idNodeCount++;
		this.beginExampleIndex = beginExampleIndex;
		this.endExampleIndex = endExampleIndex;
		double sumOfSquares = 0.0;
		double squareOfSums = 0.0;
		for (int i = beginExampleIndex; i<=endExampleIndex; i++) {
			sumOfSquares += Math.pow(trainingSet.getClassValue(i), 2);
			squareOfSums += trainingSet.getClassValue(i);
		}
		squareOfSums *= squareOfSums;
		variance = sumOfSquares - (squareOfSums / (endExampleIndex - beginExampleIndex + 1));
	}
	
	/**
	 * Restituisce l'identificativo numerico del nodo
	 * @return Valore del membro {@code idNode}.
	 */
	int getIdNode() {
		return idNode;
	}
	
	/**
	 * Restituisce l'indice del primo esempio del sotto-insieme rispetto al training set complessivo.
	 * @return Valore del membro {@code beginExampleIndex}.
	 */
	int getBeginExampleIndex() {
		return beginExampleIndex;
	}
	
	/**
	 * Restituisce l'indice dell'ultimo esempio del sotto-insieme rispetto al training set complessivo.
	 * @return Valore del membro {@code endExampleIndex}.
	 */
	int getEndExampleIndex() {
		return endExampleIndex;
	}
	
	/**
	 * Restituisce il valore dello SSE dell'attributo da predire rispetto al nodo corrente.
	 * @return Valore del membro {@code variance}
	 */
	double getVariance() {
		return variance;
	}
	
	/**
	 * È un metodo astratto la cui implementazione riguarda i nodi di tipo test ({@link SplitNode}) dai quali si possono generare figli, uno per ogni split
	 * prodotto. Restituisce il numero di tali nodi figli.
	 * @return Numero dei nodi figli del nodo corrente.
	 */
	abstract int getNumberOfChildren();
	
	@Override
	public String toString() {
		return "Nodo: [Examples: " + this.beginExampleIndex + "-" + this.endExampleIndex + "]  variance: " + this.variance;
	}
}
