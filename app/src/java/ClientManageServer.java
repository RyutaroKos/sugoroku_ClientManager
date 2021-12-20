import java.util.ArrayList;

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
		switch(msg)
		{
			default:
				break;
		}
	}

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
	 *WIP サインアップをするメソッド
	 * @author fumofumo3
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

	public void matchRandom(String userID)
	{
		Lobby lobby = this.decideRandomLobby();
        user.setStatus(1);
        match(lobby,userID);
	}

	public void matchPrivate(String userID,String lobbyID)
	{
        Lobby lobby = searchLobby(String lobbyID);
        if(lobby == null)
        {
            lobby = new Lobby(lobbyID, null, false);//pwd??
        }
        user.setStatus(2);
        match(lobby,userID);
	}

    public void match(Lobby lobby,String userID)//コードの再利用
	{
	    lobby.addUser(userID);
		User user = this.searchUser(userID);
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
		if(lobby.getUserNum() == 0)
		{
			this.deleteLobby(lobby);
		}

		return true;
	}

	public Lobby decideRandomLobby()
	{
	    int random_num = 0;//ランダムロビーの数
	    for(int i = 0; i < lobbys.size(); i++)
        {
            if(lobbys.get(i).checkRandom()==true&&lobbys.get(i).getUserNum()<4)
            {
                return lobbys.get(i);
            }
            else if(lobbys.get(i).checkRandom()==true&&lobbys.get(i).getUserNum()==4)
            {
                random_num++;
            }
        }
        //id 変更点
        return createLobby(0000+random_num,true);//ロビーID微妙
        //
		//randomLobbyを探す
		//空きがあったら返す
		//空きが無かったらcreate
		//
	}

	public void startGame(String str)
	{

		//開始時にステータスを変更
	}

	public void castChat(String chat)
	{
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

}
