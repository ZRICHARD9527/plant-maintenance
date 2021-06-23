package HTTP;
import java.io.IOException;
import java.net.InetSocketAddress;

import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.TestHttpHandler;

public class server extends Thread {

	private static HttpServer server;
	
	private static void StartServer() throws IOException {
		server=HttpServer.create(new InetSocketAddress(8765), 0);
		server.createContext("/order",new TestHttpHandler());
		server.start();
	}
	
	@Override
	public void run() {
		// TODO Auto-generated method stub
		try {
			System.out.println("Strat runing");
			StartServer();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
