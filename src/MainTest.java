
import java.io.IOException;
import java.sql.SQLException;

import database.DatabaseConnectionException;
import server.MultiServer;

/** Il software, partendo da un dataset memorizzato su un database, permette di generare un albero di regressione.
 * @author Francesco Lavecchia
 *
 */
public class MainTest {

	public static void main(String[] args) throws IOException, DatabaseConnectionException, SQLException {
		new MultiServer(8080);
	}	

}
