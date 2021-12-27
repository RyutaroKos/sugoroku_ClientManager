package cliant;
import java.io.IOException;
import java.util.Random;

import javax.websocket.DeploymentException;

import org.json.JSONObject;

public class WebSocketClientTest implements Runnable  {

	static WebSocketManagerTest wsManager;
	/*
	 *  サーバ側のエンドポイントと合わせる．2箇所確認する．
	 *  1. mainメソッド内でserverインスタンスを生成する際のContextRoot
	 *     サンプル例）static String contextRoot = "/app";
	 *  2. エンドポイントクラスのアノテーション（@ServerEndpoint("エンドポイント名")）
	 *     サンプル例）@ServerEndpoint("/sample")
	 *  この場合指定すべきエンドポイントは上記2つとサーバのアドレス、ポート番号、プロトコルから決定され、
	 *  例えば、"ws://localhost:8080/app/sample"といった形になる。
	 *  この例ではプロトコルはWebSocket（ws），ポートは8080，サーバアドレスはlocalhost
	 *  使い分けるときは適宜Stringとして分割して定義し結合すれば良い．
	*/
	static String serverEndpoint = "ws://localhost:8080/app/test";


	static int id = 1;
	static String password = "password";

    static int sampleIncrement = 0;


	public static void main(String[] args) throws IOException, DeploymentException {

		wsManager = new WebSocketManagerTest(serverEndpoint);
		wsManager.connect();

		new WebSocketClientTest();
	}

	WebSocketClientTest() {
		new Thread(this).start();
		// 一例として乱数値をIDにしてみる。複数起動できるかの例示のために使っている。
		Random random = new Random();
		WebSocketClientTest.id = random.nextInt(100);
	}

	public void run() {
		while(true) {
			if(wsManager.isConnected()) {
				System.out.println("sendMessage()");
				// 試しにJSONObjectのインスタンスを作ってみる
				JSONObject sendMessage = new JSONObject();
				sendMessage.put("id", id);
				sendMessage.put("password", password);
				sendMessage.put("no" , "sample message, no."+ sampleIncrement++);
				// 変換後の書式を表示してみる。（JSON）
				//System.out.println(sendMessage);
				wsManager.sendMessage(sendMessage.toString());
			}
			try {
				Thread.sleep(3000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
}
