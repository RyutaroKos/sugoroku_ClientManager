package cliant;
import javax.websocket.ClientEndpoint;
import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;

import org.json.JSONObject;


@ClientEndpoint
public class WebSocketEndpointTest {

	@OnOpen
	public void onOpen(Session session) {
		System.out.println("[client] onOpen" + session.getId());
	}

	@OnMessage
	public void onMessage(String message) {

		// 受信した生のメッセージ
		System.out.println("[client] onMessage: " + message);

        // 変換：String -> JSONObject
        JSONObject receivedMessage = new JSONObject(message);

        // 各要素を見てみる
        System.out.println("id:" + receivedMessage.getInt("id"));
        System.out.println("password:" + receivedMessage.getString("password"));
        System.out.println("no:" + receivedMessage.getString("no"));

	}

	@OnError
	public void onError(Throwable t) {
		System.out.println("[client] onError");
		System.out.println(t);
	}

	@OnClose
	public void onClose(Session session) {
		System.out.println("[client] onClose: " + session.getId());
	}

}