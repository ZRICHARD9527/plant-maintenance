package com.example.plantdemo.fragment;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.plantdemo.R;
import com.example.plantdemo.adapter.MaintainNotiAdapter;
import com.example.plantdemo.entity.Notify;
import com.example.plantdemo.http.BaseCallback;
import com.example.plantdemo.http.OkHttpHelper;
import com.example.plantdemo.view.SwipeItemLayout;

import org.json.JSONException;
import org.json.JSONObject;
import org.litepal.LitePal;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by MXL on 2020/12/16
 * <br>类描述：通知界面<br/>
 *
 * @version 1.0
 * @since 1.0
 */
public class NotifyFragment extends LazyBaseFragment implements View.OnClickListener{
    private static final int MSG_NOTICE=0;
    private static final int NOTICE_GAP=1000;
    private RecyclerView recyclerView;
    private static final String TAG = "NotifyFragment";
    private MaintainNotiAdapter adapt;
    private List<Notify> notifies;
    private Button testadd;
    private Timer timer;
    boolean test=false;
    Handler handler=new Handler(){
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case MSG_NOTICE:
                 newNotice();
                    break;
            }
        }
    };
    /**
     * 工厂方法
     * @return
     */
    public static NotifyFragment newInstance() {
        //    Log.d("Fragment", "newInstance: ");
        NotifyFragment fragment = new NotifyFragment();
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
           View rootView = inflater.inflate(R.layout.fragment_notification, container, false);
           return rootView;
    }

    /*
     *  由于延迟加载调用在方法之前 每次数据在这里重新加载
     */
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Log.d(TAG, "onActivityCreated: ");
        initData();
        initView();
        if (!test){
           testadd.setVisibility(View.GONE);
        }
        startListener();
    }
    @Override
    public View initView() {
        fillRecycleview();
        testadd=getView().findViewById(R.id.bt_test);
        testadd.setOnClickListener(this);
        return null;
    }

    @Override
    public void initData() {
     notifies=new ArrayList<>();
     notifies = LitePal.findAll(Notify.class);
     adapt=new MaintainNotiAdapter(notifies,getActivity());
    }

    @Override
    protected void lazyLoad() {

    }

    /**
     * 装填适配器
     */
    public void fillRecycleview() {
        recyclerView=getView().findViewById(R.id.recyleview_maintainNotifi_fragment);
        if(recyclerView!=null){
            LinearLayoutManager m=new LinearLayoutManager(getContext()) ;
            m.setOrientation(LinearLayoutManager.VERTICAL);
            recyclerView.setLayoutManager(m);
            //完成左右滑动交互
            recyclerView.addOnItemTouchListener(new SwipeItemLayout.OnSwipeItemTouchListener(getContext()));
            recyclerView.setAdapter(adapt);
            recyclerView.addItemDecoration(new DividerItemDecoration(getContext(),LinearLayoutManager.VERTICAL));
        }
    }

    /**
     * 开启通知监听
     */
    private void startListener(){
        Timer timer=new Timer();
        TimerTask task=new TimerTask() {
            @Override
            public void run() {
                Message message=new Message();
                message.what=MSG_NOTICE;
                handler.sendMessage(message);
            }
        };
        timer.schedule(task,0,NOTICE_GAP);
    }
    /**
     * 获取新通知
     * 主线程调用
     */
    private void newNotice(){
        OkHttpHelper helper=OkHttpHelper.getinstance();
        helper.get(OkHttpHelper.URL_TEST + OkHttpHelper.URL_NOTICE, new BaseCallback<String>() {
            @Override
            public void onRequestBefore() {

            }

            @Override
            public void onFailure(Request request, Exception e) {

            }

            @Override
            public void onSuccess(Response response, String s) {
                Log.d(TAG, "onSuccess: "+s);
                try {
                    JSONObject jsonObject=new JSONObject(s);
                    String data= jsonObject.getString("data");
                    if(data==null||data.toLowerCase()=="null"){
                        return;
                    }
                    jsonObject=new JSONObject(data);
                    String type=jsonObject.getString("category"); //0 温度 1湿度
                    String time=jsonObject.getString("time"); //0 温度 1湿度
                    String state=jsonObject.getString("status"); //1过高 0过低
                    int types=0;
                    switch (Integer.valueOf(type.trim())){
                        case 0:types=Notify.TAB_LIGHT;break;
                        case 1:types=Notify.TAB_WATER;break;
                    }
                    Notify notify=new Notify(types,time,Integer.valueOf(state));
                    adapt.addData(notify);
                    notify.save();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            @Override
            public void onError(Response response, int errorCode, Exception e) {

            }
        });
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        //保存所有通知
//        for(Notify notify:notifies){
//            notify.save();
//        }
    }

    /**
     * 点击事件
     * @param v
     */
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.bt_test:
                adapt.addData(new Notify(Notify.TAB_LIGHT));
                adapt.addData(new Notify(Notify.TAB_WATER));
                break;
        }
    }
}
