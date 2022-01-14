/*
EndPointSample.java
サーバー起動後の動作を記述する
 */
package cms;

import java.io.IOException;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Queue;

import javax.websocket.EndpointConfig;
import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;

import org.json.JSONObject;

// エンドポイントは適宜変更する
@ServerEndpoint("/sample")
public class ComManager {
	private static ArrayList<Session> Sessions = new ArrayList<>();
	static Queue<Message> queue = new ArrayDeque<>();
	static ClientManageServer cms;

    @OnOpen
    public void onOpen(Session session, EndpointConfig ec) {
    	Sessions.add(session);
        System.out.println("[WebSocketServerSample] onOpen:" + session.getId());
    }


    @OnMessage
    public void onMessage(final String message, final Session session) throws IOException {
        System.out.println("[WebSocketServerSample] onMessage from (session: " + session.getId() + ") msg: " + message);

        //Messageインスタンスを作成
        Message receivedMessage = new Message(message,session);
	    //Messageインスタンスをキューに保存
        queue.add(receivedMessage);
        cms.ntf();
    }


    @OnClose
    public void onClose(Session session) {
        Sessions.remove(session);

    	//ログアウト時の処理をするメッセージを追加
    	JSONObject jso = new JSONObject();
    	jso.put("Request", "LOGOUT");
    	String msg = jso.toString();
        System.out.println("[WebSocketServerSample] onClose from (session: " + session.getId() + ") msg: " + msg);
    	Message closeMsg = new Message(msg,session);
    	queue.add(closeMsg);
    	cms.ntf();
    }

    @OnError
    public void onError(Session session, Throwable error) {
        System.out.println("[WebSocketServerSample] onError:" + session.getId());
    }

	public static void sendMessage(Session session, String message) {
		System.out.println("[WebSocketServerSample] sendMessage(): " + message);
		try {
			// 同期送信（sync）
			session.getBasicRemote().sendText(message);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void sendBroadcastMessage(String message) {
		System.out.println("[WebSocketServerSample] sendBroadcastMessage(): " + message);
		Sessions.forEach(session -> {
			// 非同期送信（async）
			session.getAsyncRemote().sendText(message);
		});
	}

	public static Message deq() {
		return queue.poll();
	}

	public static void setCMS(ClientManageServer clientManSer)
	{
		cms = clientManSer;
	}
}
