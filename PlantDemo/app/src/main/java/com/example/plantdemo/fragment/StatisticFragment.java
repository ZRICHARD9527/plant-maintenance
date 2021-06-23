package com.example.plantdemo.fragment;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.plantdemo.R;
import com.example.plantdemo.entity.Data;
import com.example.plantdemo.http.BaseCallback;
import com.example.plantdemo.http.OkHttpHelper;
import com.example.plantdemo.utils.AnimUtil;
import com.google.android.material.tabs.TabLayout;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.suke.widget.SwitchButton;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.ViewPager;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by MXL on 2020/12/17
 * <br>类描述：<br/>
 *
 * @version 1.0
 * @since 1.0
 */
public class StatisticFragment extends LazyBaseFragment implements View.OnClickListener{
    private static final String TAG = "StatisticFragment";
    private static final int MSG_ADD=0;//增加数据
    private static final int MSG_ADD_INTERNET=1;//增加数据
    private static final int MSG_ORDER=2;//发送指令
    TabLayout tabLayout;
    ViewPager viewpager;
    int[] titles = new int[]{R.string.chart1, R.string.chart2};
    int[] typeIcons = new int[]{R.drawable.water_48, R.drawable.light_48};
    String[] typeLineColor=new String[]{"#5E8BDF","#ff0000"};
    int select_type=0;
    Fragment[] fragments;
    ImageButton changeType;
    //这里是测试数据
    int testNum=20;
    String[] dataX;
    float[] dataY;
    float[] potX;
    float[] potY;
    Timer intertimer;
    Button start;
    Button stop;
    SwitchButton temp;
    SwitchButton humi;
    TextView warn;
    boolean test=false;
    Timer btTimer;
    Handler TimerHandler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case MSG_ADD:
                        int random=(int)(Math.random()*3);
                        ((XdynamicChartFragment)fragments[1]).addData(0,new Data());
//                        nums[i].setNum(nums[i].getNum()+random);
                    break;
                case MSG_ADD_INTERNET:
                    Data.TYPE type= Data.TYPE.HUMI;
                    switch (select_type){
                        case 0:
                         type= Data.TYPE.HUMI;
                            break;
                        case 1:
                        type= Data.TYPE.TEMP;
                            break;
                    }
                    getNetworkData(type, new BaseCallback<String>() {
                        @Override
                        public void onRequestBefore() {

                        }

                        @Override
                        public void onFailure(Request request, Exception e) {
                            Log.d(TAG, "onFailure: ");
                        }

                        @Override
                        public void onSuccess(Response response, String s) {
                            Log.d(TAG, "onSuccess: "+s);
                           String value=null;
                            int code;
                            try {
                                JSONObject jsonObject=new JSONObject(s);
                                code=jsonObject.getInt("code");
                                if(Integer.valueOf(code)==-1) return;
                                value=jsonObject.getString("data");
                                jsonObject=new JSONObject(value);
                                value=jsonObject.getString("value");
                                HandlerCode(code);
                                if(select_type==0){ //湿度数值乘以100
                                    if (value==null||value.toLowerCase()=="null")value=0+"";
                                    value=Float.valueOf(value)*100+"";
                                }
                                ((XdynamicChartFragment)fragments[1]).addData(0,new Data(value));
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }

                        @Override
                        public void onError(Response response, int errorCode, Exception e) {
                            Log.d(TAG, "onError: "+errorCode);
                        }
                    });
                    break;
                case MSG_ORDER:
                    switch (msg.arg1){
                        case 0:temp.setEnabled(true); temp.setChecked(false);break;
                        case 1:humi.setEnabled(true); humi.setChecked(false);break;
                    }
                    break;
            }
        }
    };                   //创建一个Handler对象
    //测试方法
    void initTestdata(){
        dataX=new String[24];
        for(int i=0;i<24;i++){
            dataX[i]=i+"h";
        }
        dataY=new float[10];
        for(int i=0;i<10;i++){
            dataY[i]=(float) 10*i;
        }
        potX=new float[testNum];
        potY=new float[testNum];
        for(int i=0;i<testNum;i++){
            potX[i]=(float)(i+0.5);
            Random random=new Random(System.currentTimeMillis());
            potY[i]=(float)random.nextInt(100);
        }
    }
    void initXY(){
        dataX=new String[24];
        for(int i=0;i<24;i++){
            dataX[i]=i+"h";
        }
        dataY=new float[10];
        for(int i=0;i<10;i++){
            dataY[i]=(float)10*i;
        }
        potX=new float[testNum];
        potY=new float[testNum];
    }
    /**
     * 工厂方法
     *
     * @return
     */
    public static StatisticFragment newInstance() {
        //    Log.d("Fragment", "newInstance: ");
        StatisticFragment fragment = new StatisticFragment();
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_statistic, container, false);
        return rootView;
    }

    /*
     *  由于延迟加载调用在方法之前 每次数据在这里重新加载
     */
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Log.d(TAG, "onActivityCreated: ");
        initTestdata(); //测试
//        initXY();
        initData();
        initView();
//        TestchangeData();
        changeData();
        if(!test)
        startTimerTest();
    }

    @Override
    public View initView() {
        //viewpager
        viewpager = getView().findViewById(R.id.viewpager);
        fragments = new Fragment[titles.length];
        for (int i = 0; i < titles.length; i++) {
            if(i==0) fragments[0]=XfixedChartFragment.newInstance(dataX,dataY,potX,potY);
            else  if(i==1)fragments[1]=new XdynamicChartFragment();
            else
            fragments[i] = BlankFragment.newInstance(getString(titles[i]));
        }
        viewpager.setAdapter(new FragmentStatePagerAdapter(getChildFragmentManager()) {
            @NonNull
            @Override
            public Fragment getItem(int position) {
                return fragments[position];
            }

            @Override
            public int getCount() {
                return fragments.length;
            }
        });
        //Tablayout
        tabLayout=getView().findViewById(R.id.tablayout);
        for (int i = 0; i < titles.length; i++) {
            tabLayout.addTab(tabLayout.newTab());
        }
        //关联tablayout和viewpager
        tabLayout.setupWithViewPager(viewpager);
        for (int i = 0; i < titles.length; i++) {
            tabLayout.getTabAt(i).setText(getString(titles[i]));
        }
//        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
//            @Override
//            public void onTabSelected(TabLayout.Tab tab) {
//                updataText(tab.getPosition());
//            }
//
//            @Override
//            public void onTabUnselected(TabLayout.Tab tab) {
//                updataText(tab.getPosition());
//            }
//
//            @Override
//            public void onTabReselected(TabLayout.Tab tab) {
//
//            }
//        });
        //button
        changeType=getView().findViewById(R.id.changeType);
        changeType.setOnClickListener(this);
        //test
        start=getView().findViewById(R.id.start);
        stop=getView().findViewById(R.id.stop);
        start.setOnClickListener(this);
        stop.setOnClickListener(this);
        if(!test){
           start.setVisibility(View.GONE);
           stop.setVisibility(View.GONE);
        }
        warn=getView().findViewById(R.id.tv_warn);
        temp=getView().findViewById(R.id.temp);
        humi=getView().findViewById(R.id.water);
        temp.setOnCheckedChangeListener(new SwitchButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(SwitchButton view, boolean isChecked) {
                  btAnim(temp,3000, Data.TYPE.TEMP);
            }
        });
        humi.setOnCheckedChangeListener(new SwitchButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(SwitchButton view, boolean isChecked) {
               btAnim(humi,3000, Data.TYPE.HUMI);
            }
        });
//        temp.setOnClickListener(this);
//        humi.setOnClickListener(this);
       return null;
    }

    /**
     * 更新字体
     * @param position
     */
    void updataText(int position){

    }

    /**
     * 切换图标
     */
    void updataIcon(){
        if(changeType!=null){
            AnimUtil.FlipAnimatorXViewShow(changeType,200,typeIcons[select_type]);
            Log.d(TAG, "updataIcon: "+select_type);
        }
    }

    /**
     * 更换测试数据
     */
    private  void TestchangeData(){
         initTestdata();
        ((XfixedChartFragment)fragments[0]).setColor(typeLineColor[select_type]);
        ((XfixedChartFragment)fragments[0]).replaceLine(0,potX,potY);
    }
    /**
     * 更换数据
     */
    private  void  changeData(){
        Data.TYPE type= Data.TYPE.HUMI;
        switch (select_type){
            case 0:type= Data.TYPE.HUMI;
            break;
            case 1:type= Data.TYPE.TEMP;
            break;
        }
        getNetworkData(type,new BaseCallback<String>() {
            @Override
            public void onRequestBefore() {

            }

            @Override
            public void onFailure(Request request, Exception e) {
                Log.d(TAG, "onFailure: "+e);
            }

            @Override
            public void onSuccess(Response response, String s) {
                Log.d(TAG, "onSuccess: "+s);
                try {
                    JSONObject jsonObject=new JSONObject(s);
                    String data=jsonObject.getString("data");
                    List<Data> datas=new Gson().fromJson(data,new TypeToken<List<Data>>(){}.getType());
                    Log.d(TAG, "onSuccess: size="+datas.size());
                    float h[]=new float[datas.size()]; //x坐标
                    float y[]=new float[datas.size()]; //y坐标
                    StringBuilder builder=new StringBuilder();
                    for (int i=0;i<datas.size();i++){
                      //  Log.d(TAG, "onSuccess: 解析"+i);
                        int hour=(Integer)(Data.DataUtil.parseTime(datas.get(i).getTime()))[3];
                        int minute=(Integer)(Data.DataUtil.parseTime(datas.get(i).getTime()))[4];
                        h[i]= (int)(hour+minute*1.0/60);
                        if(datas.get(i).getValue()==null)y[i]=0;
                        else y[i]=Float.valueOf(datas.get(i).getValue());
                        builder.append(y[i]+" ");
                    }
                    Log.d(TAG, "onSuccess: "+builder.toString());
//                    changeData();
                    ((XfixedChartFragment)fragments[0]).setColor(typeLineColor[select_type]);
                    ((XfixedChartFragment)fragments[0]).replaceLine(0,h,y);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
//                Data.DataUtil.parseTime(s);
            }

            @Override
            public void onError(Response response, int errorCode, Exception e) {
                Log.d(TAG, "onError: "+errorCode);
            }
        },30*60+"");
    }

    /**
     * 开启定时器获取数据
     * 测试方法
     */
    void startTimerTest(){
        intertimer = new Timer();                    //创建一个定时器对象
        TimerTask task = new TimerTask()        //创建定时器任务对象，必须实现run方法，在该方法中定义用户任务
        {
            @Override
            public void run()
            {
                Log.d(TAG, "startTimerTest: 获取一次");
                Message msg=new Message();
                if(test)
                msg.what=MSG_ADD;
                else
                msg.what=MSG_ADD_INTERNET;
                TimerHandler.sendMessage(msg);
            }
        };
        intertimer.schedule(task,0,1000);                //启动定时器
    }

    /**
     * 停止计时器
     */
    void stopTimerTest(){
        intertimer.cancel();
    }

    /**
     *  发送指令
     */
    void sendOrder(Data.TYPE type){
        OkHttpHelper helper=OkHttpHelper.getinstance();
        Map map=new HashMap();
        int data=0;
        switch (type){
            case HUMI:
                data=1;
                break;
            case TEMP:
                data=0;
                break;
        }
        map.put("order",data+"");
        helper.post(OkHttpHelper.URL_TEST + OkHttpHelper.URL_CONTROL, new Gson().toJson(map), new BaseCallback<String>() {
            @Override
            public void onRequestBefore() {
            }

            @Override
            public void onFailure(Request request, Exception e) {
                Log.d(TAG, "onFailure: order");
            }

            @Override
            public void onSuccess(Response response, String s) {
                Toast.makeText(getActivity(),"操作成功",Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onError(Response response, int errorCode, Exception e) {
                Log.d(TAG, "onError: order"+errorCode);
            }
        });
    }
    @Override
    public void initData() {

    }

    @Override
    protected void lazyLoad() {

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.changeType:
                select_type=1-select_type;
                updataIcon();
                changeData();
                stopTimerTest();
                ((XdynamicChartFragment)fragments[1]).restart();
                startTimerTest();
//                TestchangeData();
                break;
            case R.id.start:
                startTimerTest();
                break;
            case R.id.stop:
                stopTimerTest();
                break;
//            case R.id.temp:
//                sendOrder(Data.TYPE.TEMP);
//                break;
//            case R.id.water:
//                sendOrder(Data.TYPE.HUMI);
//                break;
        }
    }


    /**
     * 一次性拿到当天全部数据
     * @param callback
     * @param gap
     */
   private   void  getNetworkData(Data.TYPE type,BaseCallback callback, String gap){
       OkHttpHelper helper=OkHttpHelper.getinstance();
       String url=OkHttpHelper.URL_TEST;
       switch (type){
           case HUMI:
               url=url+OkHttpHelper.URL_ALL_HUMI+"?gap="+gap;
               break;
           case TEMP:
               url=url+OkHttpHelper.URL_ALL_TEMP+"?gap="+gap;
               break;
       }
       helper.get(url, callback);
   }
    /**
     * 实时拿到数据
     * @param callback
     * @param
     */
    private   void  getNetworkData(Data.TYPE type,BaseCallback callback){
        OkHttpHelper helper=OkHttpHelper.getinstance();
        String url=OkHttpHelper.URL_TEST;
        switch (type){
            case HUMI:
                url=url+OkHttpHelper.URL_NOW_HUMI;
                break;
            case TEMP:
                url=url+OkHttpHelper.URL_NOW_TEMP;
                break;
        }
        Map map=new HashMap();
        map.put("time",System.currentTimeMillis()+"");
        helper.post(url,new Gson().toJson(map),callback);
    }
    /*
     * 处理数据状态
     */
   public void  HandlerCode(int code){
       String tip=null;
       switch (code){
           case 1:
               tip="温度过高";
           break;
           case 2:
               tip="温度过低";
               break;
           case 3:
               tip="湿度过高";
               break;
           case 4:
               tip="湿度不够";
               break;
           default:
               warn.setVisibility(View.GONE);
               return;
       }
       warn.setVisibility(View.VISIBLE);
       warn.setText(tip);
//       Toast.makeText(getActivity(),tip,Toast.LENGTH_SHORT).show();
   }

    /**
     * 按钮禁用一段时间后恢复
     */
   void btAnim(SwitchButton button,long delay,Data.TYPE type){
       if(btTimer==null) btTimer=new Timer();
       if(button.isChecked()){
           sendOrder(type);
           button.setEnabled(false);
           btTimer.schedule(new TimerTask() {
               @Override
               public void run() {
                   int arg=0;
                   switch (type){
                       case TEMP:
                           arg=0;
                           break;
                       case HUMI:
                           arg=1;
                           break;
                   }
                   Message message=new Message();
                   message.what=MSG_ORDER;
                   message.arg1=arg;
                   TimerHandler.sendMessage(message);
               }
           },3000);
       }
   }
}
