package com.sun.net.httpserver;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import com.alibaba.fastjson.JSONObject;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

public class TestHttpHandler implements HttpHandler {
    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String response = "test message";
        exchange.sendResponseHeaders(200, 0);
        
        
       OutputStream os = exchange.getResponseBody();
       InputStream inputStream =exchange.getRequestBody();
       
       int len=inputStream.available();
       byte[] receive=new byte[len];
       inputStream.read(receive);
       String re =new String(receive);
       //System.out.println(re);
       
       
       JSONObject jsonObject = JSONObject.parseObject(re);
		if((int)jsonObject.getIntValue("order")==0) {
			System.out.println("********************************************");
			System.out.println("***             控 制 温 度              ***");
			System.out.println("********************************************");
		}
		else {
			System.out.println("********************************************");
			System.out.println("***             控 制 湿 度              ***");
			System.out.println("********************************************");		}
       os.write(response.getBytes("UTF-8"));
       os.close();
       inputStream.close();
    }
}