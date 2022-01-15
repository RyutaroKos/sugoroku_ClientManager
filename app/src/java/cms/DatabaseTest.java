package cms;

public class DatabaseTest {
	public static void main(String[] args)
	/**
	 * DatabaseManagerクラスをテストするサンプルプログラム(学内ネットワークまたはSRAS-VPNが必要)
	 * @author 福嶋大智
	 */
	{
		DatabaseManager dbManager = new DatabaseManager();

		//Delete Test
		System.out.println("Delete result(0 -> success):" + dbManager.deleteTable());

		//Create Test
		System.out.println("Create result(0 -> success):" + dbManager.makeTabel());

		//signup Test
		System.out.println("signUp result(1 -> success):" + dbManager.signUp("00001","hoge"));

		//confirmRecord Test
		System.out.println("confirmRecord result(0,0 -> success)" + dbManager.confirmRecord("00001"));

		//signup Test2 MySQLIntegrityConstraintViolationException
		//System.out.println("signUp2 result(-1 -> failed):" + dbManager.signup("00001","foo"));

		//seachUser Test
		System.out.println("searchUser result1(true -> success):" + dbManager.searchUser("00001","hoge"));
		System.out.println("searchUser result2(false -> failed):" + dbManager.searchUser("00001","foo"));
		System.out.println("searchUser result3(false -> failed):" + dbManager.searchUser("00002","hoge"));

		//Connection closed
		dbManager.close();
	}

}

