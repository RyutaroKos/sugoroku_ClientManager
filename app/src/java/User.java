/**
 *ユーザのクラス
 *@author BP19025
 */

//user status
 #define outLobby 0
 #define inRandom 1
 #define inPrivate 2
 #define inGame 3
 //

 import javax.websocket.Session;

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
        this.status = outLobby;
        this.lobbyID = null;
    }

    User(String userName, Session session, int status, String lobbyID)
    {
        this.userName = userName;
        this.session = session;
        this.status = status;
        this.lobbyID = lobbyID;
    }

    public String getName()
    {
        return this.userName;
    }

    public Session getSession()
    {
        return this.session;
    }

    public void setSession(Session session)
    {
        this.session = session;
    }

    public void setStatus(int statusID)
    {
        this.status = statusID;
    }

    public int getStatus()
    {
        return this.status;
    }

    public void setLobbyID(String LobbyID)
    {
        this.lobbyID = LobbyID;
    }

    public String getLobbyID()
    {
        return this.lobbyID;
    }

    public void exitLobby()
    {
        setStatus(outLobby);
        setLobbyID(null);
    }
};
