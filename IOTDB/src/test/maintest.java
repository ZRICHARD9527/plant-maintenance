package test;

import java.math.BigDecimal;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import Bean.Message;
import HTTP.Http45;
import HTTP.server;

public class maintest {

	private static String ipaddress="http://192.168.43.43:8080/insert";
	private static String ip = "http://192.168.43.146/ai_value/slot_0";
	public static void main(String[] args) {

		String res = "{\"AIVal\":[{\"Ch\":0,\"En\":1,\"Rng\":328,\"Val\":1750,\"EgF\":1750.000,\"Evt\":0,\"LoA\":0,\"HiA\":0,\"HVal\":1798,\"HEgF\":1798.000,\"LVal\":1614,\"LEgF\":1614.000,\"SVal\":10000,\"ClrH\":0,\"ClrL\":0},{\"Ch\":1,\"En\":1,\"Rng\":328,\"Val\":37,\"EgF\":37.000,\"Evt\":0,\"LoA\":0,\"HiA\":0,\"HVal\":48,\"HEgF\":48.000,\"LVal\":21,\"LEgF\":21.000,\"SVal\":10000,\"ClrH\":0,\"ClrL\":0},{\"Ch\":2,\"En\":0,\"Rng\":65535,\"Val\":0,\"EgF\":0.000,\"Evt\":32,\"LoA\":0,\"HiA\":0,\"HVal\":0,\"HEgF\":0.000,\"LVal\":10000,\"LEgF\":0.000,\"SVal\":0,\"ClrH\":0,\"ClrL\":0}]}";

		server myServer=new server();
		myServer.start();
		
		
		for (int i=0;true;i++ ) {
			try {
				res = Http45.doGet(ip, "Authorization", "root:00000000");
				JSONObject jsonObject = JSONObject.parseObject(res);
				JSONArray jsonArray = jsonObject.getJSONArray("AIVal");
				float[] V = new float[jsonArray.size()];
				for (int j = 0; j < 2; j++) {
					Message json = JSONObject.parseObject(jsonArray.getString(j), Message.class);
					V[j] = json.getVal();
				}
				V[0]=(4200-V[0])/100;
				V[1]/=3600;
				if(V[1]>1) {
					V[1]=1;
				}
				else {
					BigDecimal b =new BigDecimal(V[1]);
					V[1]=b.setScale(2,BigDecimal.ROUND_HALF_UP).floatValue();
				}

				sendMes(V[0], V[1]);
								if(i%5==0) {
					System.out.println("温度："+V[0]+" ,湿度："+V[1]);
				}
			} catch (Exception e) {
				// TODO: handle exception
			}
			

			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	private static void sendMes(float v1, float v2) {
		
		
		String jsonString="{\"time\":"+System.currentTimeMillis() +",\"temperature\":"+v1+",\"humidity\":"+v2+"}";
		Http45.doPost(ipaddress, jsonString);
	}
	// {"time":1608215759589,"temperature":28.01,"humidity":0.13}
}
