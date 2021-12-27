/*
EndPointSample.java
サーバー起動後の動作を記述する
 */
package server;
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

// エンドポイントは適宜変更する
@ServerEndpoint("/test")
public class EndpointTest {
	private static ArrayList<Session> Sessions = new ArrayList<>();
	static Queue<Message> queue = new ArrayDeque<>();

	private int privateIncrementTest = 0;
	private static int staticIncrementTest = 0;


    @OnOpen
    public void onOpen(Session session, EndpointConfig ec) {
    	Sessions.add(session);
        System.out.println("[WebSocketServerSample] onOpen:" + session.getId());
    }


    @OnMessage
    public void onMessage(final String message, final Session session) throws IOException {
        System.out.println("[WebSocketServerSample] onMessage from (session: " + session.getId() + ") msg: " + message);
        this.privateIncrementTest++;
        EndpointTest.staticIncrementTest++;
        
        //Messageインスタンスを作成
        Message receivedMessage = new Message(message,session);
	    //Messageインスタンスをキューに保存
        queue.add(receivedMessage);

        // 特定のグループに対しての送信（この列だと全体)
        sendBroadcastMessage(receivedMessage.getData().toString());
        // 送信してきた人に返信
        //sendMessage(session, message);
        System.out.println("[WebSocketServerSample]:privateIncrementTest:" + this.privateIncrementTest);
        System.out.println("[WebSocketServerSample]:staticInrementTest  :" + EndpointTest.staticIncrementTest);

    }


    @OnClose
    public void onClose(Session session) {
        System.out.println("[WebSocketServerSample] onClose:" + session.getId());
    	Sessions.remove(session);
    }

    @OnError
    public void onError(Session session, Throwable error) {
        System.out.println("[WebSocketServerSample] onError:" + session.getId());
    }

	public void sendMessage(Session session, String message) {
		System.out.println("[WebSocketServerSample] sendMessage(): " + message);
		try {
			// 同期送信（sync）
			session.getBasicRemote().sendText(message);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void sendBroadcastMessage(String message) {
		System.out.println("[WebSocketServerSample] sendBroadcastMessage(): " + message);
		Sessions.forEach(session -> {
			// 非同期送信（async）
			session.getAsyncRemote().sendText(message);
		});
	}
	
	public static Message deq() {
		return queue.poll();
	}
}

