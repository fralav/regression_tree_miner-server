package data;

import java.util.Set;
import java.util.stream.Collectors;
import java.util.List;
import java.util.LinkedList;
import java.util.ArrayList;
import java.util.Iterator;
import java.sql.SQLException;

import database.*;

/**
 * Classe che modella l'insieme di esempi di training.
 * @author Francesco Lavecchia
 *
 */
public class Data {
	
	/**
	 * Lista di oggetti di tipo {@link Example} che contiene il training set, il quale è organizzato come una matrice a dimensioni
	 * [{@code (numero di esempi) x (numero di attributi)}].
	 */
	private List<Example> data = new ArrayList<Example>();
	
	/**
	 * Cardinalità del training set.
	 */
	private int numberOfExamples;
	
	/**
	 * Lista di oggetti di tipo {@link Attribute} per rappresentare gli attributi indipendenti.
	 */
	private List<Attribute> explanatorySet = new LinkedList<Attribute>();
	
	/**
	 * Oggetto per modellare l'attributo di classe (target attribute). L'attributo di classe è un attributo numerico.
	 */
	private ContinuousAttribute classAttribute;
	
	/**
	 * Si occupa di caricare i dati (schema ed esempi) di addestramento da una tabella di nome {@code tableName} della base di dati.
	 * @param tableName Nome della tabella del database dalla quale prelevare il data set.
	 * @throws TrainingDataException Eccezione sollevata quando la connessione al database fallisce, la tabella non esiste, la tabella
	 * 		   ha meno di due colonne, la tabella ha zero tuple o l'attributo corrispondente all'ultima colonna della tabella non è numerico.
	 * 								 
	 * @throws SQLException Eccezione catturata quando si verificano problemi di accesso al database. Durante l'esecuzione del programma,
	 * 						se c'è un problema di questo tipo, viene catturata l'eccezione {@code SQLException} e sollevata l'eccezione
	 * 						{@link TrainingDataException}.
	 */
	public Data(String tableName) throws TrainingDataException, SQLException {
		
		// Accesso al database
		DbAccess db = new DbAccess();
		try {
			db.initConnection();
		} catch (DatabaseConnectionException e) {
			throw new TrainingDataException("[ERRORE] Connessione al database non riuscita.\n" + e.toString());
		}
		
		// Accesso alla tabella
		TableSchema tableSchema = null;
		try {
			tableSchema = new TableSchema(db, tableName);
		} catch (SQLException e) {
			throw new TrainingDataException("[ERRORE] Non è stato possibile trovare la tabella '"+tableName+"'.\n" + e.toString());
		}
		
		// Popolare training set
		TableData tableData = new TableData(db);
		Iterator<Example> transazioni = null;
		try {
			transazioni = tableData.getTransazioni(tableName).iterator();
		} catch (SQLException | EmptySetException e) {
			throw new TrainingDataException("[ERRORE] Non è stato trovato nessun Data Set.\n" + e.toString());
		}
		
		int i = 0;
		while (transazioni.hasNext()) {
			data.add(transazioni.next());
			i++;
		}
		numberOfExamples = i;
		
		Iterator<Column> columns = tableSchema.iterator();
		Column column = null;
		i = 0;
		while (columns.hasNext()) {
			column = columns.next();
			if (column.isNumber()) {
				explanatorySet.add(new ContinuousAttribute(column.getColumnName(), i));
			} else {
				Set<String> discreteValues = null;
				try {
					discreteValues = tableData.getDistinctColumnValues(tableName, column).stream().map(o -> o.toString()).collect(Collectors.toSet());
				} catch (SQLException | EmptySetException e) {
					throw new TrainingDataException("[ERRORE] Impossibile istanziare gli attributi.\n" + e.toString());
				}
				explanatorySet.add(new DiscreteAttribute(column.getColumnName(), i, discreteValues));
			}
			i++;
		}
		
		if (i<2) throw new TrainingDataException("[ERRORE] Numero colonne troppo basso (minimo 2).");
		
		explanatorySet.remove(explanatorySet.size() - 1);
		classAttribute = new ContinuousAttribute(column.getColumnName(), explanatorySet.size());
	
		db.closeConnection();
	}
	
	/**
	 * Restituisce il numero delle tuple contenute nel dataset.
	 * @return Numero di tuple del training set definito da {@code numberOfExamples}.
	 */
	public int getNumberOfExamples() {
		return numberOfExamples;
	}
	
	/**
	 * Restituisce il numero degli attributi indipendenti del dataset.
	 * @return Dimensione di {@code explanatorySet} che identifica il numero degli attributi indipendenti del dataset.
	 */
	public int getNumberOfExplanatoryAttributes() {
		return explanatorySet.size();
	}
	
	/**
	 * Restituisce l'attributo target di classe nell'esempio {@code exampleIndex}.
	 * @param exampleIndex Indice che indica la riga dove risiede l'attributo di classe da restituire.
	 * @return Attributo target di classe nella tupla con ID {@code exampleIndex}
	 */
	public Double getClassValue(int exampleIndex) {
		return (Double) data.get(exampleIndex).get(explanatorySet.size());
	}
	
	/**
	 * Restituisce il valore di un elemento del training set all'interno della riga {@code exampleIndex} e colonna {@code attributeIndex}.
	 * @param exampleIndex Indice di riga del traininig set.
	 * @param attributeIndex Indice di colonna del training set.
	 * @return Valore di un elemento del training set {@code data} nella riga {@code exampleIndex} e colonna {@code attributeIndex}.
	 */
	public Object getExplanatoryValue(int exampleIndex, int attributeIndex) {
		return data.get(exampleIndex).get(attributeIndex);
	}
	
	/**
	 * Restituisce il nome dell'attributo di ID {@code index}.
	 * @param index Identificativo numerico dell'attributo.
	 * @return Nome dell'attributo con identificativo {@code index}, definito dall'{@code index}-esimo elemento di {@code explanatorySet}.
	 */
	public Attribute getExplanatoryAttribute(int index) {
		return explanatorySet.get(index);
	}
	
	/**
	 * Restituisce l'attributo target di classe del training set corrente.
	 * @return Attributo di classe definito da {@code classAttribute}.
	 */
	ContinuousAttribute getClassAttribute() {
		return classAttribute;
	}
	
	/**
	 * Restituisce il training set complessivo sotto forma di stringa.
	 * @return Training set complessivo in forma di {@link String}
	 */
	public String toString() {
		String value = "";
		for (int i=0; i<getNumberOfExamples(); i++) {
			for (int j=0; j<getNumberOfExplanatoryAttributes(); j++) {
				value += getExplanatoryValue(i, j) + ",";
			}
			value += data.get(i).get(getNumberOfExplanatoryAttributes()) + "\n";
		}
		return value;
	}
	
	/**
	 * Ordina il sottoinsieme di esempi compresi nell'intervallo [{@code inf}-{@code sup}] in {@code data} rispetto allo specifico attributo {@code attribute}.
	 * Usa l'algoritmo quicksort per l'ordinamento di un array di interi usando come relazione d'ordine '≤'. L'array, in questo caso, è dato dai valori assunti
	 * dall'attributo passato in input.
	 * @param attribute Attributo indipente.
	 * @param beginExampleIndex Estremo inferiore {@code inf} della porzione di training set.
	 * @param endExampleIndex Estremo superiore {@code sup} della porzione di training set.
	 */
	public void sort(Attribute attribute, int beginExampleIndex, int endExampleIndex) {
		quicksort(attribute, beginExampleIndex, endExampleIndex);
	}
	
	/**
	 * Effettua uno scambio tra due tuple del training set.
	 * @param i Identificativo numerico della prima tupla da scambiare.
	 * @param j Identificativo numerico della seconda tupla da scambiare.
	 */
	private void swap(int i, int j) {
		Object temp1 = data.get(i);
		Object temp2 = data.get(j);
		data.remove(i);
		data.add(i, (Example) temp2);
		data.remove(j);
		data.add(j, (Example) temp1);
	}
	
	/**
	 * Partiziona il training set nell'intervallo [{@code inf}-{@code sup}] rispetto all'elemento {@code x} e restituisce il punto di separazione.
	 * Questo metodo è definito per gli attributi discreti.
	 * @param attribute Attributo sul quale definire la partizione.
	 * @param inf Estremo inferiore del sotto-insieme di training.
	 * @param sup Estremo superiore del sotto-insieme di training.
	 * @return Elemento di separazione tra le due partizioni.
	 */
	private int partition(DiscreteAttribute attribute, int inf, int sup) {
		int i = inf;
		int j = sup;
		int med = (inf + sup) / 2;
		String x = (String) getExplanatoryValue(med, attribute.getIndex());
		swap(inf, med);
		while (true) {
			while (i <= sup && ((String) getExplanatoryValue(i, attribute.getIndex())).compareTo(x) <= 0) {
				i++;
			}
			while (((String) getExplanatoryValue(j, attribute.getIndex())).compareTo(x) > 0) {
				j--;
			}
			if (i<j) {
				swap(i, j);
			} else {
				break;
			}
		}
		swap(inf, j);
		return j;
	}
	
	/**
	 * Partiziona il training set nell'intervallo [{@code inf}-{@code sup}] rispetto all'elemento {@code x} e restituisce il punto di separazione.
	 * Questo metodo è definito per gli attributi continui.
	 * @param attribute Attributo sul quale definire la partizione.
	 * @param inf Estremo inferiore del sotto-insieme di training.
	 * @param sup Estremo superiore del sotto-insieme di training.
	 * @return Elemento di separazione tra le due partizioni.
	 */
	private int partition(ContinuousAttribute attribute, int inf, int sup) {
		int i = inf;
		int j = sup;
		int	med = (inf + sup) / 2;
		Double x = (Double) getExplanatoryValue(med, attribute.getIndex());
		swap(inf, med);
		while (true) {
			while (i <= sup && ((Double) getExplanatoryValue(i, attribute.getIndex())).compareTo(x) <= 0) { 
				i++; 
			}
			while (((Double) getExplanatoryValue(j, attribute.getIndex())).compareTo(x) > 0) {
				j--;
			}
			if (i<j) { 
				swap(i, j);
			} else {
				break;
			}
		}
		swap(inf, j);
		return j;
	}
	
	/**
	 * Algoritmo di quicksort per l'ordinamento del training set.
	 * @param attribute Attributo sul quale definire l'ordinamento.
	 * @param inf Estremo inferiore del sotto-insieme di training.
	 * @param sup Estremo superiore del sotto-insieme di training.
	 */
	private void quicksort(Attribute attribute, int inf, int sup) {
		if (sup >= inf) {
			int pos;
			if (attribute instanceof DiscreteAttribute) {
				pos = partition((DiscreteAttribute) attribute, inf, sup);
			} else {
				pos = partition((ContinuousAttribute) attribute, inf, sup);
			}
			if ((pos - inf) < (sup - pos + 1)) {
				quicksort(attribute, inf, pos - 1);
				quicksort(attribute, pos + 1, sup);
			} else {
				quicksort(attribute, pos + 1, sup);
				quicksort(attribute, inf, pos - 1);
			}
		}
	}
}
