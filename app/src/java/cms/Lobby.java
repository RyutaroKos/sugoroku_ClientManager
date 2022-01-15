package cms;

import java.util.ArrayList;

/**
 *ロビーのクラス
 *@author 福嶋大智
 */
class Lobby
{
	private ArrayList<User> users;
	private String lobbyID;
	private boolean isRandom;
	private ArrayList<Boolean> readyList;


	public Lobby(String id, boolean randomFlag)//private or random
	{
	this.users = new ArrayList<>();
    	this.lobbyID = id;
    	this.isRandom = randomFlag;
	this.readyList = new ArrayList<>();
	}

	/**
	*ロビーIDを取得
	*@author 福嶋大智
	*/
	public String getLobbyID()
	{
		return this.lobbyID;
	}
	
	/**
	*ロビー内のユーザ数を取得する
	*@author 福嶋大智
	*/
	public int getTotalUserNum()
	{
		return this.users.size();
	}

	/**
	*該当するユーザIDを持つユーザのインデックスを返す
	*@author 福嶋大智
	*@param userid ユーザID
	*/
	public int getUserNum(String userid)
	{
		int upos;
		String idtmp;

		for(upos = 0; upos < this.getTotalUserNum(); upos++)
        {
			idtmp = this.users.get(upos).getName();
			if(idtmp.equals(userid))
			{
				break;
			}
		}

		return upos;
	}

	/**
	*ユーザのリストを取得
	*@author 福嶋大智
	*/
	public ArrayList<User> getUserList()
	{
		return this.users;
	}

	/**
	*ユーザ単位で準備完了
	*@author 福嶋大智
	*/
	public void setReady(String userid)
	{
		int pos = this.getUserNum(userid);
		this.readyList.set(pos,true);
	}

	/**
	*ロブー内の全ユーザが準備完了であるか
	*@author 福嶋大智
	*/
	public boolean isReady()
	{
		if(this.isRandom)
		{
			if(4 != this.users.size())
			{
				return false;
			}
		}
		else
		{
			if(this.users.size() < 2)
			{
				return false;
			}
		}

		for(boolean ready : readyList)
		{
			if(!ready)
			{
				return false;
			}
		}

		return true;
	}

	/**
	*ユーザをリストに追加する
	*@author 福嶋大智
	*/
	public void addUser(User user)
	{
		this.users.add(user);
		this.readyList.add(false);
	}

	/**
	*リストのユーザを削除
	*@author 福嶋大智
	*/
	public void deleteUser(String userid)
	{
		int pos = this.getUserNum(userid);
		this.users.get(pos).exitLobby();
		this.users.remove(pos);
		this.readyList.remove(pos);
	}

	/**
	*ランダムロビーかどうか
	*@author 福嶋大智
	*/
	public boolean isRandomLobby()
	{
	    return isRandom;
	}
}
