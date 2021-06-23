package com.example.plantdemo.entity;

/**
 * Created by MXL on 2020/12/20
 * <br>类描述：传感器数据<br/>
 *
 * @version 1.0
 * @since 1.0
 */
public class Data {

    public  enum TYPE{
        HUMI,TEMP
    }
   public Data(){
        value=Math.random()*100+"";
    }
    public Data(String value){
        this.value=value;
    }
    String time;
    String value;

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
    public static class DataUtil{
       /**
        * 返回 年月日 时分秒
        * @param date
        * @return
        */
        public  static Object[] parseTime(String date){
            int year,mouth,day,hour,minute,second;
            String str1,str2;
            String[] tmp1,tmp2;
            date=date.trim();
            int spanIdx=date.indexOf(" ");
            str1=date.substring(0,spanIdx);
            str2=date.substring(spanIdx+1);
            tmp1=str1.split("-");
            tmp2=str2.split(":");
            return  new Object[]{Integer.parseInt(tmp1[0]),Integer.parseInt(tmp1[1]),
                    Integer.parseInt(tmp1[2]),Integer.parseInt(tmp2[0]),Integer.parseInt(tmp2[1])
                    ,Integer.parseInt(tmp2[2])};
        }
   }
}
