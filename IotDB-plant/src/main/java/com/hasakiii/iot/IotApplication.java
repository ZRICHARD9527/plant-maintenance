package com.hasakiii.iot;

import com.hasakiii.iot.dao.IotDao;
import com.hasakiii.iot.dto.Warn;
import com.hasakiii.iot.service.IotService;
import org.jruby.internal.runtime.ThreadService;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import javax.annotation.Resource;
import java.util.Random;

@SpringBootApplication
public class IotApplication {


    public static void main(String[] args) {
        SpringApplication.run(IotApplication.class, args);
        //插入线程，模拟数据
        //insert();

//        IotDao iotDao = new IotDao();
//        while (true) {
//            Long time = System.currentTimeMillis();
//            Random r = new Random(time);
//            int tem = r.nextInt(20) + 10;
//            Float hum = r.nextInt(100) / 100.0f;
//            iotDao.insert(time, (float) tem, hum);
//        }

    }

    public static void insert() {
        final long timeInterval = 1;// 5秒运行一次
        IotDao iotDao = new IotDao();
        Runnable runnable = new Runnable() {
            public void run() {
                while (true) {
                    // ------- code for task to run
                    Long time = System.currentTimeMillis();
                    Random r = new Random(time);
                    int tem = r.nextInt(20) + 10;
                    Float hum = r.nextInt(100) / 100.0f;

                    int t = Warn.isTempSuit((float) tem);
                    int h = Warn.isHumSuit(hum);
                    if (t == 1) {
                        iotDao.addNotice(time, 1);
                    } else if (t == 2) {
                        iotDao.addNotice(time, 0);
                    }
                    if (h == 3) {
                        iotDao.addNotice(time, 11);
                    } else if (h == 4) {
                        iotDao.addNotice(time, 10);
                    }
                    iotDao.insert(time, (float) tem, hum);
                    // ------- ends here
                    try {
                        Thread.sleep(timeInterval);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        };
        Thread thread = new Thread(runnable);
        thread.start();
    }

}
