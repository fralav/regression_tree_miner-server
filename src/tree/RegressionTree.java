package tree;

import java.util.TreeSet;
import java.io.Serializable;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.ObjectInputStream;
import java.io.FileOutputStream;
import java.io.FileInputStream;

import data.Data;
import data.DiscreteAttribute;
import data.ContinuousAttribute;
import utility.Keyboard;
import server.UnknownValueException;

/**
 * Classe utile a modellare l'entità dell'intero albero di decisione come insieme di sotto-alberi.
 * @author Francesco Lavecchia
 *
 */
public class RegressionTree implements Serializable {
	
	/**
	 * Radice del sotto-albero corrente.
	 */
	private Node root;
	
	/**
	 * Array di sotto-alberi originati nel nodo {@code root}: vi è un elemento nell'array per ogni figlio del nodo.
	 */
	private RegressionTree childTree[];
	
	/**
	 * Istanzia un sotto-albero dell'intero albero.
	 */
	private RegressionTree() {}
	
	/**
	 * Instazia un sotto-albero dell'intero albero e avvia l'induzione dell'albero dagli esempi di trianing in input.
	 * @param trainingSet Training Set complessivo.
	 */
	public RegressionTree(Data trainingSet) {
		learnTree(trainingSet, 0, trainingSet.getNumberOfExamples()-1, trainingSet.getNumberOfExamples()*10/100);
	}
	
	/**
	 * Verifica se il sotto-insieme corrente può essere coperto da un nodo foglia controllando che il numero di esempi del training set compresi
	 * tra {@code begin} ed {@code end} sia minore uguale di {@code numberOfExamplesPerLeaf}.<br><b>N.B.</b>: {@code isLeaf()} è chiamato da
	 * {@code learnTree()} che è chiamato dal costruttore di {@link RegressionTree} dove {@code numberOfExamplesPerLeaf} è fissato al 10%
	 * della dimensione del training set.
	 * @param trainingSet Training Set complessivo.
	 * @param begin Indice dell'estremo inferiore del sotto-insieme di training.
	 * @param end Indice dell'estremo superiore del sotto-insieme di training.
	 * @param numberOfExamplesPerLeaf Numero minimo che una foglia deve contenere.
	 * @return Esito sulle condizioni per i nodi fogliari.
	 */
	boolean isLeaf(Data trainingSet, int begin, int end, int numberOfExamplesPerLeaf) {
		return end - begin <= numberOfExamplesPerLeaf;
	}
	
	/**
	 * Per ciascuno attributo indipendente istanzia il {@link DiscreteNode} associato e seleziona il nodo di split con minore varianza tra i
	 * {@link DiscreteNode} istanziati. Ordina la porzione di {@code trainingSet} corrente (tra {@code begin} ed {@code end}) rispetto
	 * all'attributo indipendente del nodo di split selezionato. Restituisce il nodo selezionato.
	 * @param trainingSet Training set complessivo.
	 * @param begin Indice dell'estremo inferiore del sotto-insieme di training.
	 * @param end Indice dell'estremo superiore del sotto-insieme di training.
	 * @return Nodo di split migliore per il sotto-insieme di training.
	 */
	private SplitNode determineBestSplitNode(Data trainingSet, int begin, int end) {
		TreeSet<SplitNode> ts = new TreeSet<SplitNode>();
		for (int i=0; i<trainingSet.getNumberOfExplanatoryAttributes(); i++) {
			if (trainingSet.getExplanatoryAttribute(i) instanceof DiscreteAttribute) {
				ts.add(new DiscreteNode(trainingSet, begin, end, (DiscreteAttribute) trainingSet.getExplanatoryAttribute(i)));
			} else {
				ts.add(new ContinuousNode(trainingSet, begin, end, (ContinuousAttribute) trainingSet.getExplanatoryAttribute(i)));
			}
		}
		trainingSet.sort(ts.first().getAttribute(), begin, end);
		return ts.first();
	}
	
	/**
	 * Genera un sotto-albero con il sotto-insieme di input istanziando un nodo fogliare ({@code isLeaf()}) o un nodo di split. In tal caso
	 * determina il miglior nodo rispetto al sotto-insieme di input ({@code determineBestSplitNode()}), ed a tale nodo esso associa un
	 * sotto-albero avente radice il nodo medesimo ({@code root}) e avente un numero di rami pari al numero dei figli determinati dallo
	 * split ({@code childTree[]}).<br>Ricorsivamente, per ogni oggetto {@link RegressionTree} in {@code childTree[]} sarà re-invocato 
	 * il metodo {@code learnTree()} per l'apprendimento su un insieme ridotto del sotto-insieme attuale ({@code begin}-{@code end}). 
	 * Nella condizione in cui il nodo di split non origina figli, il nodo diventa fogliare.
	 * @param trainingSet Training set complessivo.
	 * @param begin Indice dell'estremo inferiore del sotto-insieme di training.
	 * @param end Indice dell'estremo superiore del sotto-insieme di training.
	 * @param numberOfExamplesPerLeaf Numero minimo che una foglia deve contenere.
	 */
	void learnTree(Data trainingSet,int begin, int end,int numberOfExamplesPerLeaf) {
		if (isLeaf(trainingSet, begin, end, numberOfExamplesPerLeaf)) {
			root = new LeafNode(trainingSet,begin,end);
		} else 	{
			root = determineBestSplitNode(trainingSet, begin, end);
			if (root.getNumberOfChildren() > 1) {
				childTree = new RegressionTree[root.getNumberOfChildren()];
				for (int i=0;i<root.getNumberOfChildren();i++) {
					childTree[i] = new RegressionTree();
					childTree[i].learnTree(trainingSet, ((SplitNode) root).getSplitInfo(i).getBeginIndex(), ((SplitNode) root).getSplitInfo(i).getEndIndex(), numberOfExamplesPerLeaf);
				}
			} else {
				root = new LeafNode(trainingSet, begin, end);
			}
		}
	}
	
	/**
	 * Il metodo stampa le informazioni dell'intero albero (compresa una intestazione).
	 */
	public void printTree() {
		System.out.println("********* TREE **********\n");
		System.out.println(toString());
		System.out.println("*************************\n");
	}

	@Override
	public String toString() {
		String tree=root.toString()+"\n";
		if(root instanceof LeafNode) {
			
		}
		else { // Split node
			for(int i=0; i<childTree.length; i++) {
				tree+=childTree[i];
			}
		}
		return tree;
	}
	
	/**
	 * Scandisce ciascun ramo dell'albero completo dalla radice alla foglia concatenando le informazioni dei nodi di split fino al nodo foglia. In
	 * particolare per ogni sotto-albero (oggetto {@link RegressionTree}) in {@code childTree[]} concatena le informazioni del nodo {@code root}:
	 * se è di split discende ricorsivamente l'albero per ottenere le informazioni del nodo sottostante (necessario per ricostruire le condizioni
	 * in AND) di ogni ramo-regola, se è di foglia ({@link LeafNode}) termina l'attraversamento visualizzando la regola.
	 */
	public void printRules() {
		Node currentNode = root;
		String current = "";
		String finalString = "********* RULES **********\n";
		if (currentNode instanceof LeafNode) {
			System.out.println(((LeafNode) currentNode).toString() + "");
		} else {
			current += ((SplitNode) currentNode).getAttribute();
			for (int i=0; i<childTree.length; i++) {
				finalString += childTree[i].printRules(current + ((SplitNode) currentNode).getSplitInfo(i).getComparator() + ((SplitNode) currentNode).getSplitInfo(i).getSplitValue());
			}
		}
		finalString += "**************************\n";
		System.out.println(finalString);
	}
	
	/**
	 * Supporta il metodo {@code printRules()}. Concatena alle informazioni in {@code current} del precedente nodo quelle del nodo {@code root}
	 * del corrente sotto-albero (oggetto {@link RegressionTree}): se il nodo corrente è di split il metodo viene invocato ricorsivamente con
	 * {@code current} e le informazioni del nodo corrente, se è fogliare ({@link LeafNode}) visualizza tutte le informazioni concatenate.
	 * @param current Informazioni sotto forma di stringa del precedente nodo
	 * @return	Se il nodo corrente è fogliare, visualizza tutte le informazioni concatenate fin'ora, altrimenti il metodo viene invocato ricorsivamente
	 * 		    affinché non viene trovata una classe foglia.
	 */
	private String printRules(String current) {
		Node currentNode = root;
		String finalString = "";
		if (currentNode instanceof LeafNode) { //Sono ad un nodo foglia. termino la riga e la ritorno
			return current + "  ==> Class = " + ((LeafNode) currentNode).getPredictedClassValue() + "\n";
		}  else {
			current += " AND " + ((SplitNode) currentNode).getAttribute().getName();
			for (int i=0; i<childTree.length; i++) {
				finalString += childTree[i].printRules(current + ((SplitNode) currentNode).getSplitInfo(i).getComparator() + ((SplitNode) currentNode).getSplitInfo(i).getSplitValue());
			}
		}
		return finalString;
	}
	
	/**
	 * Visualizza le informazioni di ciascuno split dell'albero ({@link SplitNode}{@code .formulateQuery()}) e per il corrispondente attributo
	 * acquisisce il valore dell'esempio dapredire da tastiera. Se il nodo {@code root} corrente è {@link LeafNode}, termina l'acquisizione e
	 * visualizza la predizione per l'attributo classe, altrimenti invoca ricorsivamente sul figlio di {@code root} in {@code childTree[]}
	 * individuato dal valore acquisito da tastiera.
	 * @return Oggetto {@link Double} contenente il valore di classe predetto per l'esempio acquisito.
	 * @throws UnknownValueException Eccezione utile a gestire il caso di acquisizione di un valore mancante o fuori range di un attributo di un
	 * 								 nuovo esempio da classificare.
	 */
	public Double predictClass() throws UnknownValueException {
		if (root instanceof LeafNode) {
			return ((LeafNode) root).getPredictedClassValue();
		} else {
			int risp;
			System.out.println(((SplitNode) root).formulateQuery());
			risp = Keyboard.readInt();
			if (risp == -1 || risp >= root.getNumberOfChildren()) {
				throw new UnknownValueException("La risposta deve essere un numero intero tra 0 e " + (root.getNumberOfChildren() - 1) + "!");
			} else {
				return childTree[risp].predictClass();
			}
		}
	}
	
	/**
	 * Serializza l'albero in un file.
	 * @param nomeFile Nome del file in cui salvare l'albero.
	 * @throws FileNotFoundException Eccezione che viene sollevata durante un tentativo fallito causato dall'apertura del file indicato da un
	 * 								 percorso specificato.
	 * @throws IOException Eccezione sollevata quando si verifica un errore I/O di qualche tipo.
	 */
	public void salva(String nomeFile) throws FileNotFoundException, IOException {
		FileOutputStream outFile = new FileOutputStream(nomeFile);
		ObjectOutputStream outStream = new ObjectOutputStream(outFile);
		outStream.writeObject(this);
		outStream.close();
	}
	
	/**
	 * Carica un albero di regressione salvato in un file.
	 * @param nomeFile Nome del file.
	 * @return L'albero contenuto nel file.
	 * @throws FileNotFoundException Eccezione che viene sollevata durante un tentativo fallito causato dall'apertura del file indicato da un
	 *								 percorso specificato.
	 * @throws IOException Eccezione sollevata quando si verifica un errore I/O di qualche tipo.
	 * @throws ClassNotFoundException Eccezione sollevata quando si verifica un errore nel caricare il file in un oggetto di tipo {@link RegressionTree}.
	 */
	public static RegressionTree carica(String nomeFile) throws FileNotFoundException, IOException, ClassNotFoundException {
		FileInputStream inFile = new FileInputStream(nomeFile);
		ObjectInputStream outStream = new ObjectInputStream(inFile);
		RegressionTree rTree = (RegressionTree) outStream.readObject();
		outStream.close();
		return rTree;
	}
	
	/**
	 * Visualizza le informazioni di ciascuno split dell'albero ({@link SplitNode}{@code .formulateQuery()}) e per il corrispondente attributo
	 * acquisisce il valore dell'esempio da predire da tastiera. Se il nodo {@code root} corrente è {@link LeafNode}, termina l'acquisizione
	 * e visualizza la predizione per l'attributo classe, altrimenti invoca ricorsivamente sul figlio di {@code root} in {@code childTree[]}
	 * individuato dal valore acquisito da tastiera.
	 * @param objectOutputStream Stream su cui vengono scritti i valori:<br><ul><li>'OK' seguito dal valore target se si è giunti ad un nodo foglia;
	 * 							 </li><li>'QUERY' seguito dalle opzioni se si è in presenza di uno split.</li></ul>.
	 * @param objectInputStream Stream da cui vengono lette le risposte del client (path da eseguire sui nodi di split).
	 * @param host Stringa che rappresenta IP, data e ora del client che ha richiesto l'operazione.
	 * @throws IOException Eccezione sollevata quando si verifica un errore I/O di qualche tipo.
	 * @throws UnknownValueException Eccezione utile a gestire il caso di acquisizione di un valore mancante o fuori range di un attributo di un
	 * 								 nuovo esempio da classificare.

	 * @throws ClassNotFoundException Eccezione sollevata quando si verifica un errore nel caricare il file in un oggetto di tipo {@link RegressionTree}.
	 */
	public void predictClass(ObjectOutputStream objectOutputStream, ObjectInputStream objectInputStream, String host) throws IOException, UnknownValueException, ClassNotFoundException {
		if (root instanceof LeafNode) {
			objectOutputStream.writeObject("OK");
			objectOutputStream.writeObject(((LeafNode) root).getPredictedClassValue().toString());
		} else {
			int risp;
			objectOutputStream.writeObject("QUERY");
			objectOutputStream.writeObject(((SplitNode) root).formulateQuery());
			objectOutputStream.writeObject(root.getNumberOfChildren());
			risp = Integer.parseInt(objectInputStream.readObject().toString());
			System.out.println(host + "     Il client ha scelto: " + risp);
			if (risp < 0 || risp >= root.getNumberOfChildren())
				throw new UnknownValueException("The answer should be an integer between 0 and " + (root.getNumberOfChildren() - 1) + "!");
			else
				childTree[risp].predictClass(objectOutputStream, objectInputStream, host);
		}
	}
	
}
