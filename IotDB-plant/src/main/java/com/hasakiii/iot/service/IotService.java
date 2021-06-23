package com.hasakiii.iot.service;

import com.alibaba.fastjson.JSONObject;
import com.hasakiii.iot.dto.Result;
import com.hasakiii.iot.dao.IotDao;
import com.hasakiii.iot.dto.Warn;
import org.apache.http.HttpEntity;
import org.apache.http.ParseException;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * @Author: Z.Richard
 * @CreateTime: 2020/12/10 21:36
 * @Description:
 **/
@Service
public class IotService {


    @Resource
    IotDao iotDao;

    /**
     * 获取温度
     *
     * @return
     */
    public Result getTemp(long questTime) {
        Map<String, Object> map = iotDao.getTemp(questTime);
        Float tem = (Float) map.get("value");
        Integer suit = Warn.isTempSuit(tem);
        System.out.println("实时温度：" + map.toString());
        System.out.println("时   间：" + questTime);
        switch (suit) {
            case 0:
                return new Result(0, "温度适宜", map);
            case 1:
                return new Result(1, "温度过高", map);
            case 2:
                return new Result(2, "温度过低", map);
            default:
                return new Result(-1, "未知错误", null);
        }
    }

    /**
     * 获取湿度
     *
     * @return
     */
    public Result getHum(long time) {
        Map<String, Object> map = iotDao.getHum(time);
        Float hum = (Float) map.get("value");
        Integer suit = Warn.isHumSuit(hum);

        System.out.println("实时湿度：" + hum);
        System.out.println("时   间 ： " + time);
        switch (suit) {
            case 0:
                return new Result(0, "湿度适宜", map);
            case 3:
                return new Result(3, "湿度过高", map);
            case 4:
                return new Result(4, "湿度过低", map);
            default:
                return new Result(-1, "未知错误", map);
        }
    }

    /**
     * 获取当天所有温度数据
     *
     * @return
     */
    public Result allTem(Long gap) {
        List<Map<String, Object>> data = iotDao.allTem(gap);
        if (data.size() == 0) {
            return new Result(-1, "未知错误", null);
        } else {
            return new Result(0, "", data);
        }
    }

    /**
     * 获取当天所有湿度数据
     *
     * @return
     */
    public Result allHum(Long gap) {
        Object data = iotDao.allHum(gap);
        if (data == null) {
            return new Result(-1, "未知错误", null);
        } else {
            return new Result(0, "", data);
        }
    }

    /**
     * 获取历史情况
     *
     * @return
     */

    private static boolean beginFlag = true;
    private static int initStatus = -1;
    private static int initCategory = -1;

    public Result getNotice() {

        Map<String, Object> data = iotDao.getNotice();

        if (beginFlag) {
            if (data.get("status") != null) {
                initStatus = Integer.valueOf(String.valueOf(data.get("status")));
                initCategory = Integer.valueOf(String.valueOf(data.get("category")));
                beginFlag = false;
                return new Result(0, "", data);
            }
        } else {
            //判断状态是否与前一个相同
            if (initStatus == Integer.valueOf(String.valueOf(data.get("status"))) && initCategory == Integer.valueOf(String.valueOf(data.get("category")))) {
                //相同则返回null
                return new Result(0, "", null);
            } else {
                //不相同时替换比较状态
                initStatus = Integer.valueOf(String.valueOf(data.get("status")));
                initCategory = Integer.valueOf(String.valueOf(data.get("category")));
                return new Result(0, "", data);
            }
        }

        return new Result(0, "", data);

    }


    /**
     * *****************************************************************************************************
     */


    /**
     * 存取温湿度
     * 01温度过高，00温度过低  11湿度过高，10湿度过低
     *
     * @param time
     * @param tem
     * @param hum
     * @return
     */

    public Result insert(Long time, Float tem, Float hum) {

        int t = Warn.isTempSuit(tem);
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


        if (iotDao.insert(time, tem, hum)) {
            return new Result(0, "存取成功", null);
        } else {
            return new Result(-1, "存取失败", null);

        }

    }

    /**
     * 发送指令
     * 01温度过高、降温  00温度过低、升温  11湿度过高、降湿  10湿度过低、加湿
     * 0 控温  1控湿
     *
     * @return
     */
    public Result order(Integer order) {
        // 获得Http客户端(可以理解为:你得先有一个浏览器;注意:实际上HttpClient与浏览器是不一样的)
        CloseableHttpClient httpClient = HttpClientBuilder.create().build();

        // 创建Post请求
        HttpPost httpPost = new HttpPost("http://192.168.43.112:8765/order");
        // 响应模型
        CloseableHttpResponse response = null;
        //json参数
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("order", order);
        try {
            StringEntity jsonParam = new StringEntity(jsonObject.toString());
            jsonParam.setContentEncoding("UTF-8");//发送数据编码为utf-8
            jsonParam.setContentType("application/json");//发送json数据需要设置contentType


            // 由客户端执行(发送)Post请求
            httpPost.setEntity(jsonParam);
            response = httpClient.execute(httpPost);
            // 从响应模型中获取响应实体
            HttpEntity responseEntity = response.getEntity();

            if (response.getStatusLine().getStatusCode() != 200) {
                //请求失败则返回
                return new Result(-1, "操作失败", response.getStatusLine());
            }
            if (responseEntity != null) {
                System.out.println("响应内容为:" + JSONObject.parseObject(EntityUtils.toString(responseEntity)).getInteger("data"));
            }
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                // 释放资源
                if (httpClient != null) {
                    httpClient.close();
                }
                if (response != null) {
                    response.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            return new Result(0, "", null);
        }


    }

}
