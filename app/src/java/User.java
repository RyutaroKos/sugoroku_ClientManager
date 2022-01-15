 package cms;

 import javax.websocket.Session;

/**
 *ユーザのクラス
 *@author BP19025
 */
class User
{
    private String userName;
    private Session session;
    private int status; //0=ロビー外,１=ランダムロビー内,2=プライベートロビー内,3=ゲーム中
    private String lobbyID;

    User(String userName, Session session)
    {
        this.userName = userName;
        this.session = session;
        this.status = 0;
        this.lobbyID = null;
    }

    User(String userName, Session session, int status, String lobbyID)
    {
        this.userName = userName;
        this.session = session;
        this.status = status;
        this.lobbyID = lobbyID;
    }
	/**
	 * ユーザ名をゲットするメソッド
	 * @return userName ユーザ名
	 */
    public String getName()
    {
        return this.userName;
    }
	/**
	 * セッション名をゲットするメソッド
	 * @return session セッションのインスタンス
	 */
    public Session getSession()
    {
        return this.session;
    }
	/**
	 * セッションをセットするメソッド
	 * @param session セッションのインスタンス
	 */
    public void setSession(Session session)
    {
        this.session = session;
    }
	/**
	 * ステータスをセットするメソッド
	 * @param statusID ステータスID
	 */
    public void setStatus(int statusID)
    {
        this.status = statusID;
    }
	/**
	 * ステータスをゲットするメソッド
	 * @return statusID ステータスID
	 */
    public int getStatus()
    {
        return this.status;
    }
	/**
	 * ロビーIDをセットするメソッド
	 * @param LobbyID ロビーID
	 */
    public void setLobbyID(String LobbyID)
    {
        this.lobbyID = LobbyID;
    }
	/**
	 * ロビーIDをゲットするメソッド
	 * @return LobbyID ロビーID
	 */
    public String getLobbyID()
    {
        return this.lobbyID;
    }
	/**
	 * ロビーから退室するメソッド
	 */
    public void exitLobby()
    {
        setStatus(0);
        setLobbyID(null);
    }
};
