package cms;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**DB通信管理クラス
 * @author 福嶋大智
 */
class DatabaseManager
{
	//Server URL
	private static final String url = "jdbc:mysql://sql.yamazaki.se.shibaura-it.ac.jp";
	private static final String sqlDriverName = "com.mysql.jdbc.Driver";
	//DB Name
	private static final String sqlDatabaseName = "db_group_c";
	//Port Number
	private static final String sqlServerPort = "13308";
	//User ID
	private static final String sqlUserId = "group_c";
	//Password
	private static final String sqlPassword = "group_c";

	private Connection connection;
	private Statement stmt;

	//Table Name
	private static final String tableName = "userinfo";
	//Param List
	private static final String params = "id VARCHAR(10), pwd VARCHAR(30), battles INTEGER, wins INTEGER, PRIMARY KEY (id)";

	DatabaseManager()
	{
		try {
			Class.forName(sqlDriverName);
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
			//System.exit(0);
		}
		this.connect();
	}

	/**接続を確立する
	 * @author 福嶋大智
	 */
	private void connect()
	{
		try {
			String target = url + ":" + sqlServerPort + "/" + sqlDatabaseName;
			this.connection = DriverManager.getConnection(target, sqlUserId, sqlPassword);
			this.stmt = this.connection.createStatement();
		} catch(SQLException e) {
			e.printStackTrace();
		}
	}

	/**引数のID,パスワードに該当するユーザがいるかどうかを返す
	 * @author 福嶋大智
	 * @param id ユーザID, pwd パスワード
	 */
	public boolean searchUser(String id, String pwd)
	{
		String tmp = this.getPassword(id);
		return pwd.equals(tmp);
	}

	/**ユーザを登録する．idは主キーなので，重複するとMySQLIntegrityConstraintViolationExceptionがスローされる
	 * @author 福嶋大智
	 */
	public int signUp(String id, String pwd)
	{
		int success;
		try {
			String queryString = "INSERT INTO " + tableName + " VALUES ('" + id + "', '" + pwd + "', 0, 0);";
			success = this.stmt.executeUpdate(queryString);
		} catch(SQLException e) {
			success = -1;
			e.printStackTrace();
		}
		return success;
	}

	/**IDに対応するユーザのパスワードを返す
	 * @author 福嶋大智
	 * @param id ユーザID
	 */
	private String getPassword(String id)
	{
		String pwd = "aaa";
		try {
			String queryString = "SELECT pwd FROM " + tableName + " WHERE id = '" + id + "';";
			ResultSet rs = this.stmt.executeQuery(queryString);
				while(rs.next())
				{
					pwd = rs.getString(1);
				}
		} catch(SQLException e) {
			pwd = "no_exist_such_user";
			e.printStackTrace();
		}
		return pwd;
	}

	/**対戦数,勝利数　の形で文字列として返すメソッド．間は半角カンマで区切られている．戦績の計算は別途必要
	 * @author 福嶋大智
	 */
	public String confirmRecord(String id)
	{
		String numBattlesAndWins = "aaa";
		try {
			String queryString = "SELECT battles FROM " + tableName + " WHERE id = '" + id + "';";
			ResultSet rs = this.stmt.executeQuery(queryString);
			while(rs.next())
			{
				numBattlesAndWins = rs.getString(1);
			}
			queryString = "SELECT wins FROM " + tableName + " WHERE id = '" + id + "';";
			rs = this.stmt.executeQuery(queryString);
			while(rs.next())
			{
				numBattlesAndWins = numBattlesAndWins + "," + rs.getString(1);
			}
		} catch(SQLException e) {
			numBattlesAndWins = "no_exist_such_user";
			e.printStackTrace();
		}
		return numBattlesAndWins;
	}

	/**テーブルを新たに作成する
	 * @author 福嶋大智
	 */
	public int makeTabel()
	{
		int result = -1;
		try {
			String queryString = "CREATE TABLE "+ tableName + " ( " + params + " );";
			result = this.stmt.executeUpdate(queryString);
		} catch(SQLException e) {
			result = -1;
			e.printStackTrace();
		}
		return result;
	}

	/**作成済みのテーブルを消去する
	 * @author 福嶋大智
	 */
	public int deleteTable()
	{
		int result = -1;
		try {
			String queryString = "DROP TABLE "+ tableName;
			result = this.stmt.executeUpdate(queryString);

		} catch(SQLException e) {
			result = -1;
			e.printStackTrace();
		}
		return result;
	}

	/**接続を終了する
	 * @author 福嶋大智
	 *
	 */
	public void close()
	{
		try {
			if (connection != null)
			{
				connection.close();
			}
		} catch(SQLException e){
			e.printStackTrace();
		}
	}
}
