import java.util.ArrayList;
import org.json.JSONObject;

/**
 *クライアントを管理するクラス
 *@author fumofumo3
 */
class ClientManageServer
{
	private ArrayList<User> users;
	private ArrayList<Lobby> lobbys;
	private DatabaseManager dbManager;
	private ComManager comManager;


	public ClientManageServer()
	{
		this.users = new ArrayList<User>();
		this.lobbys = new ArrayList<Lobby>();
		this.dbManager = new DatabaseManager();
		this.comManager = new ComManager();
	}

	/**
	 *WIP メッセージを処理して実行するメソッド
	 *@author fumofumo3
	 */
	public void handleMessage()
	{
		Message msg;
		Session session;
		JSONObject jsonObj;
		String request;
		String userID;


		msg = comManager.deq();
		session = msg.getSession();
		jsonObj = msg.getData();

		userID = this.searchSessionUserID(session.getID());
		request = jsonObj.getString("Request");
		switch(request)
		{
			case "LOGIN":
				userID = jsonObj.getString("Username");
				String pwd = jsonObj.getString("Password");
				this.signIn(userID, pwd);
				break;
			case "SIGNUP":
				userID = jsonObj.getString("Username");
				String pwd = jsonObj.getString("Password");
				this.signUp(userID, pwd);
				break;
			case "RANDOM_MATCH":
				this.matchRandom(userID);
				break;
			case "PRIVATE_MATCH":
				String lobbyID = jsonObj.getString("LobbyID");
				this.matchPrivate(userID, pwd);
				break;
			case "CHECK_RECORD":

				break;
			case "EXIT_LOBBY":
				this.exitLobby(userID);
				break;
			case "SEND_CHAT":
				String chat = jsonObj.getString("Message");
				this.castChat(userID, chat);
				break;
			case "START_GAME":
				this.prepareGame(userID);
				break;
			case "MAKE_GAME":
				String lobbyID = jsonObj.getString("LobbyID");
				this.startGame(lobbyID);
				break;
			default:
				break;
		}
	}

	/**
	 * サインインをするメソッド
	 * @author fumofumo3
	 * @param userID ユーザID
	 * @param pwd パスワード
	 */
	public void signIn(String userID, String pwd)
	{
		boolean isRegisteredUser = dbManager.searchUser(userID, pwd);//登録チェック
		if(isRegisteredUser)//登録されている場合
        {
            if(this.isSignedInUser(userID))
            {
                //既にsignIn中、失敗メッセージを返す
            }
            else//signIn処理を行う
            {
                User user = new User();
                this.users.add(user);
                comManager.sendMessage();//str
            }
        }
        else
        {
            // 登録されていない 失敗メッセージ
        }

	}

	/**
	 * サインアップをするメソッド
	 *
	 * @param userID ユーザID
	 * @param pwd パスワード
	 */
	public void signUp(String userID, String pwd)
	{
		boolean isRegisteredUser = dbManager.searchUser(userID, pwd);//signUpで成功失敗の判定でもいいのでは

		if(isRegisteredUser)
		{
            //失敗メッセージを返す。
		}
		else
		{
			this.dbManager.signUp(userID, pwd);
			this.comManager.sendMessage();//要msgの形式確認
		}
	}

	/**
	 * サインアウトをするメソッド
	 * @author fumofumo3
	 * @param userID ユーザID
	 * @param pwd パスワード
	 */
	public void signOut(String userID)
	{
		String id;
		for(int num = 0; num < this.users.size(); num++)
		{
			id = this.users.get(num).getName();
			if(userID.equals(id))
			{
				this.users.remove(num);
			}
		}
	}

	/**
	 * ランダムでマッチングをするメソッド
	 * @param userID ユーザID
	 */
	public void matchRandom(String userID)
	{
		Lobby lobby = this.decideRandomLobby();
		User user = this.searchUser(userID);
        user.setStatus(1);
        match(lobby, user);
	}

	/**
	 * プライベートでマッチングをするメソッド
	 * @param userID ユーザID
	 * @param lobbyID ロビーID
	 */
	public void matchPrivate(String userID, String lobbyID)
	{
        Lobby lobby = searchLobby(lobbyID);
        if(lobby == null)
        {
            lobby = new Lobby(lobbyID, null, false);//pwd??
        }
        User user = this.searchUser(userID);
        user.setStatus(2);
        match(lobby, user);
	}

	/**
	 * マッチングをするメソッド
	 * @param lobby ロビーのインスタンス
	 * @param user ユーザのインスタンス
	 */
    public void match(Lobby lobby, User user)//コードの再利用
	{
	    lobby.addUser(user);
		user.setLobbyID(lobby.getLobbyID());

		ArrayList<User> lobbyUsers = lobby.getUserList();
		String sockID;
		for(int num = 0; num < lobbyUsers.size(); num++)
		{
			sockID = lobbyUsers.get(num).getWebSocketID();
			//msgはどうするか
		}
	}

	public boolean isExistLobby(String lobbyID)
	{
		String id;
		for(int num = 0; num < this.lobbys.size(); num++)
		{
			id = this.lobbys.get(num).getLobbyID();
			if(lobbyID.equals(id))
			{
				return true;
			}
		}

		return false;
	}

	//保留、必要性は要確認
	public Lobby createLobby(String lobbyID, boolean isRandom)
	{
		return new Lobby(lobbyID, pass, isRandom);//passいらないのでは
	}

	/**
	 * ロビーを退出するメソッド
	 * @param userID ユーザID
	 * @return boolean
	 * */
	public boolean exitLobby(String userID)
	{
		User user = this.searchUser(userID);
		if(user == null)
		{
			return false;
		}

		String lobbyID = user.getLobbyID();
		Lobby lobby = this.searchLobby(lobbyID);
		if(lobby == null)
		{
			return false;
		}

		lobby.deleteUser(userID);
		if(lobby.getTotalUserNum() == 0)
		{
			this.deleteLobby(lobby);
		}

		return true;
	}

	/**
	 * ランダムマッチ用のロビーを作成するメソッド
	 * @return lobby
	 */
	public Lobby decideRandomLobby()
	{
	    Lobby lobby;
	    String randLobbyID;

	    for(int i = 0; i < 10000; i++)
	    {
	    	randLobbyID = String.format("%05d", i);
	    	lobby = this.searchLobby(randLobbyID);

	    	if(lobby == null)
	    	{
	    		return createLobby(randLobbyID, true);
	    	}
	    	else
	    	{
	            if(lobby.isRandomLobby() && lobby.getTotalUserNum() < 4)
	            {
	            	return lobby;
	            }
	    	}
	    }

	    //ランダムロビー数上限に到達
	}

	public void startGame(String str)
	{

		//開始時にステータスを変更
	}

	/**
	 * ユーザが所属するロビーにチャットを送るメソッド
	 * @param userID ユーザID
	 * @param chat チャット
	 */
	public void castChat(String userID, String chat)
	{
		User user = this.searchUser(userID);
		String lobbyID = user.getLobbyID();
		Lobby lobby = this.searchLobby(lobbyID);
		AllayList<User> lobbyUsers = lobby.getUserList();

		String sockID;
		for(int num = 0; num < lobbyUsers.size(); num++)
		{
			sockID = lobbyUsers.get(num).getWebSocketID();
			//msgはどうするか
		}
	}

	private boolean isSignedInUser(String userID)
	{
		User user = this.searchUser(userID);
		if(user == null)
		{
			return false;
		}
		else
		{
			return true;
		}
	}

	private User searchUser(String userID)
	{
		String id;
		for(int num = 0; num < this.users.size(); num++)
		{
			id = this.users.get(num).getName();
			if(userID.equals(id))
			{
				return this.users.get(num);
			}
		}

		return null;
	}

	private Lobby searchLobby(String lobbyID)
	{
		String id;
		for(int num = 0; num < this.lobbys.size(); num++)
		{
			id = this.lobbys.get(num).getLobbyID();
			if(lobbyID.equals(id))
			{
				return this.lobbys.get(num);
			}
		}

		return null;
	}

	private void deleteLobby(Lobby targetLobby)
	{
		for(int num = 0; num < this.lobbys.size(); num++)
		{
			if(targetLobby == this.lobbys.get(num))
			{
				this.lobbys.remove(num);
				return;
			}
		}
	}

	private String searchSessionUserID(String sockID)
	{
		String id;
		for(User user : this.users)
		{
			id = user.getWebSocketID();
			if(id.equals(sockID))
			{
				return user.getName();
			}
		}

		return null;
	}
}
