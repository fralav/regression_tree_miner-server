package server;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.io.IOException;

/**
 * La classe modella la comunicazione client-server.
 * @author Francesco Lavecchia
 *
 */
public class MultiServer {
	
	/**
	 * Porta dove il server è in ascolto.
	 */
	private int PORT = 8080;
	
	/**
	 * Inizializza l'attributo {@code PORT} con il valore passato in input. L'attributo {@code PORT} di default vale {@code 8080} ed esegue il metodo
	 * {@code run()}.
	 * @param port Porta dove il server è in ascolto.
	 * @throws IOException Eccezione propagata quando si verifica un problema di I/O, o quando vengono interrotte operazioni di I/O.
	 */
	public MultiServer(int port) throws IOException {
		this.PORT = port;
		run();
	}
	
	/**
	 * Il metodo crea una {@link ServerSocket} e rimane in attesa di una richiesta di connessione. Quando un client si connette, crea un'istanza di {@link ServerOneClient} e gli associa la socket generata.
	 * @throws IOException Eccezione propagata quando si verifica un problema di I/O, o quando vengono interrotte operazioni di I/O.
	 */
	private void run() throws IOException {
		ServerSocket serverSocket = new ServerSocket();
		InetSocketAddress serverAddress = new InetSocketAddress(InetAddress.getLocalHost().getHostAddress(), PORT);
		serverSocket.bind(serverAddress);
		System.out.println(serverSocket.toString());
		while (true) {
			Socket socket = serverSocket.accept();
			new ServerOneClient(socket);	
		}
	}
}
