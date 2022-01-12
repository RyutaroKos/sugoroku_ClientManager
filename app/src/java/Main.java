//package cms;
import org.glassfish.tyrus.server.Server;

public class Main {
	static String contextRoot = "/app";
	static String protocol = "ws";
	static int port = 8080;

	public static void main(String[] args) {
        Server server = new Server(protocol, port, contextRoot, null, ComManager.class);

		ClientManageServer cms = new ClientManageServer();
		ComManager.setCMS(cms);
		Thread trd = new Thread(cms);

		//System.out.println("test");
		try
		{
			trd.start();
			server.start();
		}
		catch(Exception e)
		{
			System.out.println(e);
		}
		finally
		{
            server.stop();
			trd.interrupt();
		}
	}
}