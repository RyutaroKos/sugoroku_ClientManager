import java.util.ArrayList;
import org.json.JSONObject;
import javax.websocket.Session;
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

	private static final String LOGIN = "LOGIN";
	private static final String SIGNUP = "SIGNUP";
	private static final String RAND_MATCH = "RANDOM_MATCH";
	private static final String PRI_MATCH = "PRIVATE_MATCH";
	private static final String CHECK_REC = "CHECK_RECORD";
	private static final String EXIT_LOB = "EXIT_LOBBY";
	private static final String SEND_CHAT = "SEND_CHAT";
	private static final String START_GAME = "START_GAME";
	private static final String MAKE_GAME = "MAKE_GAME";

	private static final String RES = "Result";
	private static final String STATUS = "Status";
	private static final String TRUE = "true";
	private static final String FALSE = "false";

	public ClientManageServer()
	{
		this.users = new ArrayList<User>();//sign in 管理用のユーザリスト
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
		String pwd;
		String lobbyID;

		msg = comManager.deq();
		session = msg.getSession();
		jsonObj = msg.getData();

		userID = this.searchSessionUserID(session.getID());
		request = jsonObj.getString("Request");


		switch(request)
		{
			case LOGIN:
				userID = jsonObj.getString("Username");
				pwd = jsonObj.getString("Password");
				this.signIn(userID, pwd, session);
				break;
			case SIGNUP:
				userID = jsonObj.getString("Username");
				pwd = jsonObj.getString("Password");
				this.signUp(userID, pwd, session);
				break;
			case RAND_MATCH:
				this.matchRandom(userID);
				break;
			case PRI_MATCH:
				lobbyID = jsonObj.getString("LobbyID");
				this.matchPrivate(userID, lobbyID);
				break;
			case CHECK_REC:

				break;
			case EXIT_LOB:
				this.exitLobby(userID);
				break;
			case SEND_CHAT:
				String chat = jsonObj.getString("Message");
				lobbyID = this.searchUser(userID).getLobbyID();
				this.castChat(userID, lobbyID, chat);
				break;
			case START_GAME:
				this.prepareGame(userID);
				break;
			case MAKE_GAME:
				lobbyID = jsonObj.getString("LobbyID");
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
	 * @param session セッション
	 */
	public void signIn(String userID, String pwd, Session session)
	{
		JSONObject jsonObj = new JSONObject();
    	jsonObj.put(RES, LOGIN);
		String msg;

		boolean isRegisteredUser = this.dbManager.searchUser(userID, pwd);//登録チェック
		if(isRegisteredUser)//登録されている場合
        {
            if(this.isSignedInUser(userID))
            {
                //signIn済み、失敗メッセージを追加
            	jsonObj.put(STATUS, FALSE);
            }
            else
            {
            	//signIn処理、成功メッセージを追加
                User user = new User(userID, session);
                this.users.add(user);//signIn
            	jsonObj.put(STATUS, TRUE);
            }
        }
        else
        {
            // 登録されていない、失敗メッセージを追加
        	jsonObj.put(STATUS, FALSE);
        }
		//メッセージ送信
    	msg = jsonObj.toString();
    	this.comManager.sendMessage(session, msg);
	}

	/**
	 * サインアップをするメソッド
	 * @param userID ユーザID
	 * @param pwd パスワード
	 * @param session セッション
	 */
	public void signUp(String userID, String pwd, Session session)
	{
		JSONObject jsonObj = new JSONObject();
    	jsonObj.put(RES, SIGNUP);
		String msg;

		boolean isRegisteredUser = this.dbManager.searchUser(userID, pwd);
		if(isRegisteredUser)
		{
            //失敗メッセージを追加
        	jsonObj.put(STATUS, FALSE);
		}
		else
		{
			//signUp処理
			this.dbManager.signUp(userID, pwd);
        	jsonObj.put(STATUS, TRUE);
		}
		//メッセージ送信
    	msg = jsonObj.toString();
    	this.comManager.sendMessage(session, msg);
	}

	/**
	 * サインアウトをするメソッド
	 * @author fumofumo3
	 * @param userID ユーザID
	 */
	public void signOut(String userID)
	{
		User user;
		for(int num = 0; num < this.users.size(); num++)
		{
			user = this.users.get(num);
			if(userID.equals(user.getName()))
			{
			    switch(user.getStatus())
			    {
                	case 1:
                		this.exitLobby(user.getName());
                		break;
                	case 2:
                		this.exitLobby(user.getName());
                		break;
                	default:
                		break;
			    }
			    this.users.remove(num);//sign out
			    break;
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
            lobby = new Lobby(lobbyID, false);
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
    	//互いを設定する
	    lobby.addUser(user);
		user.setLobbyID(lobby.getLobbyID());

		ArrayList<User> lobbyUsers = lobby.getUserList();
		JSONObject jsonObj = new JSONObject();
		Session session;
		String msg;

		//ランダムかプライベートかでメッセージを変更する
		if(lobby.isRandomLobby())
		{
	    	jsonObj.put(RES, RAND_MATCH);
		}
		else
		{
	    	jsonObj.put(RES, PRI_MATCH);
		}

		jsonObj.put(STATUS, TRUE);
		jsonObj.put("LobbyID", lobby.getLobbyID());

		//keyをPlayerList,valueをユーザ名リスト(JSONArray)に設定する
		JSONArray userNameJSA = new JSONArray();
		for(User lobUser : lobbyUsers)
		{
			JSONObject userNameJSO = new JSONObject();
			userNameJSO.put("Username", lobUser.getName());
			userNameJSA.put(userNameJSO);
		}
		jsonObj.put("PlayerList", userNameJSA);

		//メッセージ送信
    	msg = jsonObj.toString();
		for(User lobUser : lobbyUsers)
		{
			session = lobUser.getSession();
			this.comManager.sendMessage(session, msg);
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

	/**
	 * ロビーを退出するメソッド
	 * @param userID ユーザID
	 * @return boolean
	 * */
	public boolean exitLobby(String userID)
	{
		User user = this.searchUser(userID);
		String lobbyID = user.getLobbyID();
		Lobby lobby = this.searchLobby(lobbyID);
        //
        //通信を行い、失敗した場合がfalseを返すのがどう？
        //

		lobby.deleteUser(userID);
		user.setStatus(0);
		if(lobby.getTotalUserNum() == 0)
		{
			this.deleteLobby(lobby);
			return true;
		}

		JSONObject jsonObj = new JSONObject();
		Session session;
		String msg;

		jsonObj.put(RES, EXIT_LOB);

		//keyをPlayerList,valueをユーザ名リスト(JSONArray)に設定する
		JSONArray userNameJSA = new JSONArray();
		ArrayList<User> lobbyUsers = lobby.getUserList();
		for(User lobUser : lobbyUsers)
		{
			JSONObject userNameJSO = new JSONObject();
			userNameJSO.put("Username", lobUser.getName());
			userNameJSA.put(userNameJSO);
		}
		jsonObj.put("PlayerList", userNameJSA);

		//メッセージ送信
    	msg = jsonObj.toString();
		for(User lobUser : lobbyUsers)
		{
			session = lobUser.getSession();
			this.comManager.sendMessage(session, msg);
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

	    for(int i = 0; i < 100; i++)
	    {
	    	randLobbyID = String.format("%02d", i);
	    	lobby = this.searchLobby(randLobbyID);

	    	if(lobby == null)
	    	{
	    		return new Lobby(randLobbyID, true);
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

	public void prepareGame()
	{

	}

	public void startGame()
	{

		//開始時にステータスを変更

	}

	/**
	 * ユーザが所属するロビーにチャットを送るメソッド
	 * @param sender 送信者名
	 * @param lobbyID 送信先のロビーID
	 * @param chat チャット
	 */
	public void castChat(String sender, String lobbyID, String chat)
	{
		Lobby lobby = this.searchLobby(lobbyID);
		ArrayList<User> lobbyUsers = lobby.getUserList();

		JSONObject jsonObj = new JSONObject();
		Session session;
		String msg;

		jsonObj.put(RES, SEND_CHAT);
		jsonObj.put("Username", sender);
		jsonObj.put("Message", chat);

		//メッセージ送信
		msg = jsonObj.toString();
		for(User lobUser : lobbyUsers)
		{
			session = lobUser.getSession();
			this.comManager.sendMessage(session, msg);
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

	private String searchSessionUserID(String sessionID)
	{
		String userSesID;
		for(User user : this.users)
		{
			userSesID = user.getSession().getID();
			if(userSesID.equals(sessionID))
			{
				return user.getName();
			}
		}

		return null;
	}
}
