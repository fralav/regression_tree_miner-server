package server;

import java.io.EOFException;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.SocketException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.LinkedList;

import data.Data;
import data.TrainingDataException;
import database.DatabaseConnectionException;
import database.DbAccess;
import tree.RegressionTree;

public class ServerOneClient extends Thread {
	
	/**
	 * Rappresenta l'ID del task che preleva i nomi delle tabelle dal database.
	 */
    private static final int TASK_GET_TABLES_FROM_DB = 1;
    
    /**
     * Rappresenta l'ID del task che preleva i nomi dei file dall'archivio.
     */
    private static final int TASK_GET_FILES_FROM_ARCHIVE = 2;
    
    /**
     * Rappresenta l'ID del task che apprende un albero da un training set del database.
     */
    private static final int TASK_LEARN_TREE_FROM_DB = 3;
    
    /**
     * Rappresenta l'ID del task che preleva un albero precedentemente serializzato su file.
     */
    private static final int TASK_GET_TREE_FROM_FILE = 4;
    
    /**
     * Rappresenta l'ID del task che fornisce al client la rappresentazione in {@link String} dell'albero.
     */
    private static final int TASK_PRINT_TREE = 5;
    
    /**
     * Rappresenta l'ID del task che fornisce la predizione dell'albero al client.
     */
    private static final int TASK_PREDICT_TREE = 6;
	
    /**
     * Viene inviato al client quando un operazione va a buon fine.
     */
    private static final String OK = "ok";
    
    /**
     * Viene inviato al client quando si verifica un errore in {@code Data}.
     */
    private static final String DATA_ERROR = "dataError";
    
    /**
     * Viene inviato al client quando non ci sono tabelle nel database.
     */
    private static final String TABLE_NOT_FOUND = "tableNotFound";
    
    /**
     * Viene inviato al client quando non ci sono file nell'archivio.
     */
    private static final String FILE_NOT_FOUND = "fileNotFound";
    
    /**
     * Viene inviato al client quando la tabella selezionata non è presenta nel database.
     */
    private static final String NO_TABLES_FOUND = "NoTablesFound";
    
    /**
     * Viene inviato al client quando il file selezionato non è presente nell'archivio.
     */
    private static final String NO_FILES_FOUND = "NoFilesFound";
	
    /**
     * Server socket
     */
	private Socket socket;
	
    /**
     * Rappresenta lo stream in input del socket.
     */
    private static ObjectInputStream in ;
    
    /**
     * Rappresenta lo stream in output del socket.
     */
    private static ObjectOutputStream out;
    
	/**
	 * Appena un client cerca di connettersi al server, questo accetta la connessione e resta in ascolto per eventuali richieste del client.
	 * @param socket Socket del Client
     * @throws IOException Può essere sollevata durante un errore di comunicazione con il client.
	 */
	public ServerOneClient(Socket socket) throws IOException {
		this.socket = socket;
		in = new ObjectInputStream(socket.getInputStream());
		out = new ObjectOutputStream(socket.getOutputStream());
		new Thread(this::run).start();
	}
	
	@Override
	public void run() {
		System.out.println(getHost() + " - Connesso.");
		
		RegressionTree tree = null;
		Data trainingSet = null;
		Integer decision;
		String tableName = "";
		boolean connected = true;
		
		try {
			while (connected) {
				System.out.println(getHost() + "   Sto ascoltando...");
				decision = Integer.parseInt(in.readObject().toString());
				switch (decision) {		
				
				case TASK_GET_TABLES_FROM_DB:
					System.out.println(getHost() + " - Restituisco al client la lista delle tabelle.");
					DbAccess db = new DbAccess();
					LinkedList<String> tables = new LinkedList<String>();
					try {
						db.initConnection();
						Connection connection = db.getConnection();
						Statement statement = connection.createStatement();
						ResultSet resultSet = statement.executeQuery("show tables");
						while (resultSet.next()) {
							tables.add(resultSet.getString(1));
						}
						if (tables.size() <= 0) {
							tables.add(NO_TABLES_FOUND);
						}
						out.writeObject(tables);
						statement.close();
					} catch (DatabaseConnectionException e) {
						e.printStackTrace();
					} catch (SQLException e) {
						e.printStackTrace();
					}
					finally {
						try {
							db.closeConnection();
						} catch (SQLException e) {
							e.printStackTrace();
						}
					}
					break;
					
				case TASK_GET_FILES_FROM_ARCHIVE:
					System.out.println(getHost() + " - Restituisco al client la lista dei file.");
					LinkedList<String> files = new LinkedList<String>();
					File directory = new File(".");
					File[] filesArray = directory.listFiles((dir, name) -> name.endsWith(".dmp"));
					for (int i=0; i<filesArray.length; i++) {
						files.add(filesArray[i].getName().toString().replace(".dmp", ""));
					}
					if (files.size() <= 0) {
						files.add(NO_FILES_FOUND);
					}
					out.writeObject(files);
					break;
					
				case TASK_LEARN_TREE_FROM_DB:
					System.out.println(getHost() + " - Prelevo il training set dal database per apprenderlo.");
					tableName = in.readObject().toString();
					try {
						trainingSet = new Data(tableName);
						tree = new RegressionTree(trainingSet);
						tree.salva(tableName + ".dmp");
						out.writeObject(OK);
					} catch (TrainingDataException e) {
						out.writeObject(DATA_ERROR);
					} catch (SQLException e) {
						out.writeObject(TABLE_NOT_FOUND);
					}
					break;
					
				case TASK_GET_TREE_FROM_FILE:
					System.out.println(getHost() + " - Prelevo l'albero precedentemente appreso su file.");
					tableName = in.readObject().toString();
					try {
						tree = RegressionTree.carica(tableName + ".dmp");
						out.writeObject(OK);
					} catch (FileNotFoundException e) {
						out.writeObject(FILE_NOT_FOUND);
					}
					break;
					
					case TASK_PRINT_TREE:
						System.out.println(getHost() + " - Invio l'albero al client.");
						out.writeObject(tree.toString());
						break;
						
					case TASK_PREDICT_TREE:
						System.out.println(getHost() + " - Invio al client la predizione dell'albero.");
						try {
							tree.predictClass(out, in, getHost());
						} catch (UnknownValueException e) {
							e.printStackTrace();
						}
						break;
										
					default:
						break;
				}
			}
		} catch (SocketException | EOFException e) {
			connected = false;
			System.out.println(getHost() + " - Disconnesso.");
		} catch (IOException | ClassNotFoundException e) {
			e.printStackTrace();
		} finally {
			try {
				socket.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		  
	}
	
	
	
	
	
	
	
	
	
	
	
	
	private String getHost() {
		String timeStamp = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(Calendar.getInstance().getTime());
		return "[" + socket.getInetAddress().getHostAddress() + " | " + timeStamp +"]";
	}
	
	
	
	
	
	
	
	
	
	
	
}
	
	
	
	
	
	
	/*@Override
	public void run() {
		System.out.println("Connessione con il client "+socket.getInetAddress()+" stabilita!");
		
		RegressionTree tree;
		Data trainingSet;
		boolean enabled = true;
		
		try {
			while(enabled) {
				final int decision = Integer.parseInt(in.readObject().toString().toString());
				switch(decision) {
				
				
				
				case TASK_GET_TABLES:
					DbAccess db = new DbAccess();
					LinkedList<String> tables = new LinkedList<String>();
					try {
						db.initConnection();
						Connection conn = db.getConnection();
						Statement stmt = conn.createStatement();
						ResultSet rs = stmt.executeQuery("show tables");
						System.out.println("Tabelle database: ");
						while(rs.next()) {
							tables.add(rs.getString(1));
						}
						out.writeObject(tables);
						stmt.close();
					} catch (DatabaseConnectionException | SQLException e) {
						e.printStackTrace();
					} finally {
						try {
							db.closeConnection();
						} catch (SQLException e) {
							e.printStackTrace();
						}
					}
					break;
					
					
					
				case TASK_LEARN_FROM_DB:
					String tableName = in.readObject().toString();
					try {
						trainingSet = new Data(tableName);
						out.writeObject(OK);
					} catch (SQLException e) {
						out.writeObject(TABLE_NOT_FOUND);
					} catch (TrainingDataException e) {
						out.writeObject(DATA_ERROR);
					}
					break;
					
					
					
					
				}
			}
		} catch (IOException | ClassNotFoundException e) {
			e.printStackTrace();
		}
	}
	
}
	*/