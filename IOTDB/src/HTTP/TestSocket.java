package HTTP;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;

import com.alibaba.fastjson.JSONObject;

 
public class TestSocket extends Thread {
  static String ip = "192.168.43.112";
  static int port = 8765;
  static  ServerSocket serverSocket;
  static Socket clientsocket;
 
 
  public static void StartServer() {
	try {
		serverSocket =new ServerSocket(port);
		while (true) {
			clientsocket=serverSocket.accept();
			receiveMess(clientsocket);
		}
		
	} catch (UnknownHostException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	} catch (IOException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
}
  private static void receiveMess(Socket clientSocket) {
	  try {
		BufferedReader socketIn = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
		String meString=socketIn.readLine();
//		System.out.println(meString);
		JSONObject jsonObject = JSONObject.parseObject(meString);
		if((int)jsonObject.getIntValue("order")==0) {
			System.out.println("控制温度");
		}
		else {
			System.out.println("控制湿度");
		}
	} catch (IOException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
}
  
  @Override
	public void run() {
		// TODO Auto-generated method stub
	  System.out.println("Start runing");
		StartServer();
	}
 
 
}