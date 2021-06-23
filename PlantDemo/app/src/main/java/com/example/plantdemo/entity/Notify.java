package com.example.plantdemo.entity;

import com.example.plantdemo.R;

import org.litepal.crud.LitePalSupport;

import java.util.Date;

/**
 * Created by MXL on 2020/12/16
 * <br>类描述：<br/>
 *
 * @version 1.0
 * @since 1.0
 */
public  class Notify extends LitePalSupport {
    private int type; //通知类类型
    private int image_res;         //左边图标
    private int state;      //状态码
    private String time; //时间
    public final static  int TAB_WATER= R.drawable.water;
    public final static  int TAB_LIGHT= R.drawable.light;
    public Notify(int type) {
       setType(type);
       time=new Date().toString();
    }
    public Notify(int type,String time) {
        setType(type);
        this.time=time;
    }
    public Notify(int type,String time,int state) {
        setType(type);
        this.time=time;
        switch (type){
            case Notify.TAB_LIGHT:
                this.state=state;
                break;
            case Notify.TAB_WATER:
                this.state=(1<<1)|state;
             break;
        }
    }
    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
        this.image_res=type;
    }

    public int getImage_res() {
        return image_res;
    }

    public void setImage_res(int image_res) {
        this.image_res = image_res;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    @Override
    public String toString() {
       String tip=time+"\n";
        switch(state){
            case 0:  //00
              tip+="温度过低";
                break;
            case 1://01
                tip+="温度过高";
                break;
            case 2://10
                tip+="湿度过低";
                break;
            case 3://11
                tip+="湿度过高";
                break;
        }
        return tip;
    }
}
