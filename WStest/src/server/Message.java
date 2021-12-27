//メッセージの内容とセッション情報をペアで保存するためのクラス
package server;

import javax.websocket.Session;

import org.json.JSONObject;

public class Message {
	private Session session;
	private JSONObject data;

	public Message(String message , Session s) {
		session = s;
		data = new JSONObject(message);
	}
	
	public Session getSession() {
		return session;
	}
	
	public JSONObject getData() {
		return data;
	}
	
	public String toString() {
		return "SessionID : "+ session.getId() + "data" + data.toString();
	}

}
