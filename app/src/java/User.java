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

class User
{
    private String userName;
    private String webSocketID;
    private int status; //　0=ロビー外　１=ランダムロビー内　2=プライベートロビー内　3=ゲーム中
    private String lobbyID;

    User(String userName,String webSocketID)
    {
        this.userName=userName;
        this.webSocketID=webSocketID;
        this.status=outLobby;
        this.lobbyID=null;
    }

    User(String userName,String webSocketID,int status,String lobbyID)
    {
        this.userName=userName;
        this.webSocketID=webSocketID;
        this.status=status;
        this.lobbyID=lobbyID;
    }

    public String getName()
    {
        return this.userName;
    }

    public String getWebSocketID()
    {
        return this.webSocketID;
    }

    public void setWebSocketID(String SocketID)
    {
        this.webSocketID=SocketID;
    }

    public void setStatus(int statusID)
    {
        this.status=statusID;
    }

    public int getStatus()
    {
        return this.status;
    }

    public void setLobbyID(String LobbyID)
    {
        this.lobbyID=LobbyID;
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
