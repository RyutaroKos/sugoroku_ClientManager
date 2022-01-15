package cms;

import java.net.URI;

import javax.websocket.ContainerProvider;
import javax.websocket.Session;
import javax.websocket.WebSocketContainer;
import org.glassfish.tyrus.server.Server;

public class Main {
	static String contextRoot = "/app";
	static String protocol = "ws";
	static int port = 8080;

	static Session sessionToServer;
	static URI uri;
	static WebSocketContainer container;

	public static void main(String[] args) {
        Server server = new Server(protocol, port, contextRoot, null, ComManager.class);

		ClientManageServer cms = new ClientManageServer();
		ComManager.setCMS(cms);
		Thread trd = new Thread(cms);

		container = ContainerProvider.getWebSocketContainer();
        uri = URI.create("ws://localhost:8081/app/app");
        try {
            sessionToServer = container.connectToServer(ComManager.class ,uri);
            ComManager.setSession(sessionToServer);
        }catch(Exception e) {
            System.out.println("アプリケーションサーバが起動していません");
            System.exit(0);
        }
		//System.out.println("test");
		try
		{
			trd.start();
			server.start();
			System.in.read();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		finally
		{
            server.stop();
			trd.interrupt();
		}
	}
}