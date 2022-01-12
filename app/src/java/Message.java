//メッセージの内容とセッション情報をペアで保存するためのクラス
package cms;

import javax.websocket.Session;

import org.json.JSONObject;

public class Message {
	private Session session;
	private JSONObject data;

	public Message(String message , Session s) {
		session = s;
		data = new JSONObject(message);
	}
	/**
	 * セッション名をゲットするメソッド
	 * @return session セッションのインスタンス
	 */
	public Session getSession() {
		return session;
	}
	/**
	 * データをゲットするメソッド
	 * @return data データ
	 */
	public JSONObject getData() {
		return data;
	}
	/**
	 * データをゲットするメソッド
	 * @return String 送信するメッセージ
	 */
	public String toString() {
		return "SessionID : "+ session.getId() + "data" + data.toString();
	}

}
