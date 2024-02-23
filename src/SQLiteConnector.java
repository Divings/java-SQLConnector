import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.security.NoSuchAlgorithmException;
import java.security.InvalidKeyException;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * MySQLデータベースにアクセスするクラス
 * 
 * @owner Koga
 */
public class SQLiteConnector {
	private static Connection connection;

	public static Connection connect() {

	    String user = InputUtils.input("\n DB User >> ");
	    String url = "jdbc:mysql://localhost:3306/mypay";
	    String password = InputUtils.InputPassword("\n DB Password >> ");	
	    if (user=="" || user==null){
	    	user="";// Default Username
	    }
	    try {
	        Class.forName("com.mysql.cj.jdbc.Driver");
	        connection = DriverManager.getConnection(url, user, password);
	        System.out.println("\nMySQLデータベースに接続しました。");
            Sleep(3);
	        
	        // トランザクション内での処理と排他処理
	        connection.setAutoCommit(false);
	        createTablesIfNotExist();
	        connection.commit();
	        connection.setAutoCommit(true);

	        return connection;
	    } catch (ClassNotFoundException | SQLException e) {
	        System.out.println("エラー: 依存関係の解決に失敗しました");
	        e.printStackTrace();
	        return null;
	    }
	}

	private static void createTransactionsTable() {
	    try (Statement statement = connection.createStatement()) {
	        String createTableSQL = "CREATE TABLE IF NOT EXISTS transactions ("
	                + "id INT AUTO_INCREMENT PRIMARY KEY," // AUTO_INCREMENTを使用して自動増分するIDを指定
	                + "username VARCHAR(255) NOT NULL," // TEXTの代わりにVARCHARを使用
	                + "type VARCHAR(255)," // TEXTの代わりにVARCHARを使用
	                + "amount DOUBLE," // REALの代わりにDOUBLEを使用
	                + "description TEXT,"
	                + "date VARCHAR(255))"; // TEXTの代わりにVARCHARを使用
	        statement.executeUpdate(createTableSQL);
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
	}

	private static void createUsersTable() {
	    try (Statement statement = connection.createStatement()) {
	        String createTableSQL = "CREATE TABLE IF NOT EXISTS users ("
	                + "id INT AUTO_INCREMENT PRIMARY KEY," // AUTO_INCREMENTを使用して自動増分するIDを指定
	                + "username VARCHAR(255) UNIQUE NOT NULL," // TEXTの代わりにVARCHARを使用、UNIQUEを追加
	                + "password TEXT NOT NULL,"
	                + "account_number VARCHAR(255),"
	                + "account_description TEXT)";
	        statement.executeUpdate(createTableSQL);
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
	}

	private static void createAccountsTable() {
	    try (Statement statement = connection.createStatement()) {
	        String createTableSQL = "CREATE TABLE IF NOT EXISTS accounts ("
	                + "id INT AUTO_INCREMENT PRIMARY KEY," // AUTO_INCREMENTを使用して自動増分するIDを指定
	                + "username VARCHAR(255) UNIQUE NOT NULL," // TEXTの代わりにVARCHARを使用、UNIQUEを追加
	                + "balance DOUBLE NOT NULL," // REALの代わりにDOUBLEを使用
	                + "description TEXT)";
	        statement.executeUpdate(createTableSQL);
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
	}

	private static void createTablesIfNotExist() {
		createTransactionsTable();
		createUsersTable();
		createAccountsTable();
	}

	public static void disconnect() {
		try {
			if (connection != null) {
				connection.close();
				System.out.println("Disconnected from MySQL database.");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	private static String createData() {
		String userDirectory = System.getProperty("user.home");
		String folderName = "FinancialManagement";
		String folderPath = userDirectory + File.separator + folderName;
		File folder = new File(folderPath);

		if (!folder.exists()) {
			if (folder.mkdir()) {
				return folderPath + "/";
			} else {
				return "";
			}
		} else {
			return folderPath + "/";
		}
	}

    private static void Sleep(int seconds) {
        try {
            // 指定された秒数だけスリープ
            Thread.sleep(seconds * 1000);
        } catch (InterruptedException e) {
            // インタラプトが発生した場合の例外処理
            e.printStackTrace();
        }
    }
}
