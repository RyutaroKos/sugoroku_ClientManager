package server;
import org.glassfish.tyrus.server.Server;

public class WebSocketServerTest{

	/* サーバ側のサンプルプログラム
	 * このメインメソッドの例ではサーバインスタンスの生成と起動のみを行っている．
	 */

	static String contextRoot = "/app";
	static String protocol = "ws";
	static int port = 8080;


    public static void main(String[] args) throws Exception {
        Server server = new Server(protocol, port, contextRoot, null, EndpointTest.class);

        try {
            server.start();
            System.in.read();
        } finally {
            server.stop();
            System.out.println("---queue resurt---");
		//キューの中身を正常に取り出せるかのチェック
            for(Message m:EndpointTest.queue) {
            	System.out.println(m);
            }
        }
    }
}

