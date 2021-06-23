package com.hasakiii.iot.dao;

import com.hasakiii.iot.util.IotdbPool;
import com.hasakiii.iot.util.IotdbUtil;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.sql.*;
import java.util.*;

/**
 * @Author: Z.Richard
 * @CreateTime: 2020/12/9 22:18
 * @Description: 数据连接对象，处理数据库
 **/
@Component
public class IotDao {

    private Connection connection = null;
    private PreparedStatement pstmt = null;

    /**
     * 读取最近温度
     *
     * @return
     */
    public Map<String, Object> getTemp(long questTime) {
        long gap = 1000 * 3;//3s
        long begin = questTime - gap;

        //选择前30秒平均值
        String sql = "select avg(temperature) from root.plant.d1 where time <? and time>?";

        //选择10秒内最近一次数据输出
//        String sql = "select temperature from root.plant.d1 where time = ? fill(float[previous,10s])";
        //选择最近的
//        String sql = "select last temperature from root.plant.d1 ";

        Float temperature = null;
        try {
            connection = IotdbPool.getConnection();
            pstmt = connection.prepareStatement(sql);
            pstmt.setLong(1, questTime);
            pstmt.setLong(2, begin);
            ResultSet resultSet = pstmt.executeQuery();


            while (resultSet.next()) {
                //time = resultSet.getTimestamp("Time");

                //最近值
                //temperature = Float.parseFloat(resultSet.getString("value"));
                //最近十秒最新值
                //temperature = resultSet.getFloat("root.plant.d1.temperature");
                //最近30秒平均值
                temperature = Float.parseFloat(resultSet.getString("avg(root.plant.d1.temperature)"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            //释放回连接池
            IotdbPool.remove(connection);
            Map<String, Object> map = new HashMap<>();
//            map.put("time", questTime);
            map.put("value", temperature);
            return map;
        }
    }


    /**
     * 读取最近湿度
     *
     * @return
     */
    public Map<String, Object> getHum(long questTime) {

        //选择最近一次数据输出
        String sql = "select avg(humidity) from root.plant.d1 where time <? and time>?";
        long gap = 1000 * 3;
        long begin = questTime - gap;
        Float hum = null;
        try {
            connection = IotdbPool.getConnection();
            pstmt = connection.prepareStatement(sql);
            pstmt.setLong(1, questTime);
            pstmt.setLong(2, begin);
            ResultSet resultSet = pstmt.executeQuery();

//            IotdbUtil.outputResult(resultSet);

            while (resultSet.next()) {
                hum = Float.parseFloat(resultSet.getString("avg(root.plant.d1.humidity)"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            //释放回连接池
            IotdbPool.remove(connection);
            Map<String, Object> map = new HashMap<>();
            //map.put("time", questTime);
            map.put("value", hum);
            return map;
        }
    }

    /**
     * 获取当天每个小时平均温度
     *
     * @return
     */
    public List<Map<String, Object>> allTem(Long gap) {


        long now = System.currentTimeMillis();
        long dayBegin = now - (now + 60 * 60 * 8 * 1000) % (60 * 60 * 24 * 1000);


        List<Map<String, Object>> list = new ArrayList<>();
        String sql = " select avg(temperature) from root.plant.d1 group by([?, ?), ?s,?s)";
        try {
            connection = IotdbPool.getConnection();
            pstmt = connection.prepareStatement(sql);
            pstmt.setLong(1, dayBegin);
            pstmt.setLong(2, now);
            pstmt.setLong(3, gap);
            pstmt.setLong(4, gap);
            ResultSet resultSet = pstmt.executeQuery();
//            IotdbUtil.outputResult(resultSet);
            Timestamp time = null;
            String temperature = null;
            while (resultSet.next()) {
                Map<String, Object> map = new HashMap<>();
                time = resultSet.getTimestamp("Time");
                map.put("time", time);

                temperature = resultSet.getString("avg(root.plant.d1.temperature)");
                if (StringUtils.hasLength(temperature)) {
                    map.put("value", Float.parseFloat(temperature));
                } else {
                    map.put("value", temperature);
                }
                list.add(map);
            }
            IotdbUtil.outputResult(resultSet);
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            IotdbPool.remove(connection);
            return list;
        }

    }

    /**
     * 获取当天每个小时平均湿度
     *
     * @return
     */
    public List<Map<String, Object>> allHum(Long gap) {


        long now = System.currentTimeMillis();
        long dayBegin = now - (now + 60 * 60 * 8 * 1000) % (60 * 60 * 24 * 1000);


        List<Map<String, Object>> list = new ArrayList<>();
        String sql = " select avg(humidity) from root.plant.d1 group by([?, ?), ?s,?s)";
        try {
            connection = IotdbPool.getConnection();
            pstmt = connection.prepareStatement(sql);
            pstmt.setLong(1, dayBegin);
            pstmt.setLong(2, now);
            pstmt.setLong(3, gap);
            pstmt.setLong(4, gap);

            ResultSet resultSet = pstmt.executeQuery();

            String humidity = null;
            Timestamp time = null;

            //IotdbUtil.outputResult(resultSet);
            while (resultSet.next()) {
                Map<String, Object> map = new HashMap<>();
                time = resultSet.getTimestamp("Time");
                map.put("time", time);
                humidity = resultSet.getString("avg(root.plant.d1.humidity)");
                if (StringUtils.hasLength(humidity)) {
                    map.put("value", Float.parseFloat(humidity) * 100 / 1);
                } else {
                    map.put("value", humidity);
                }

                list.add(map);
            }
            IotdbUtil.outputResult(resultSet);
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            IotdbPool.remove(connection);
            return list;
        }

    }

    /**
     * 获取历史信息
     *
     * @return
     */
    public Map<String, Object> getNotice() {
        String sql = "select notice from root.plant.d1 order by time desc limit 1";
        int notice = 0;
        Map<String, Object> map = new HashMap<>();

        try {
            connection = IotdbPool.getConnection();
            pstmt = connection.prepareStatement(sql);
            ResultSet resultSet = pstmt.executeQuery();

            while (resultSet.next()) {

                map.put("time", resultSet.getTimestamp("Time"));
                notice = resultSet.getInt("root.plant.d1.notice");
                map.put("status", notice % 10);//0过低，1过高
                map.put("category", notice / 10);//0温度，1湿度
            }

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            IotdbPool.remove(connection);
            return map;
        }
    }

/**
 * **********************************************************************************************************************
 */
    /**
     * 插入温度和干湿度
     *
     * @param time
     * @return
     */
    public boolean insert(Long time, Float tem, Float humidity) {
        String sql = "insert into root.plant.d1(timestamp ,temperature, humidity) values (?,?,?)";
        int row = 0;
        try {
            connection = IotdbPool.getConnection();
            pstmt = connection.prepareStatement(sql);
            pstmt.setLong(1, time);
            pstmt.setFloat(2, tem);
            pstmt.setFloat(3, humidity);
            row = pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            IotdbPool.remove(connection);
            return row == 0;
        }
    }

    public boolean addNotice(Long time, int status) {
        String sql = "insert into root.plant.d1(timestamp,notice) values(?,?)";
        int row = -1;
        try {
            connection = IotdbPool.getConnection();
            pstmt = connection.prepareStatement(sql);
            pstmt.setLong(1, time);
            pstmt.setInt(2, status);
            row = pstmt.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            return row == 0;
        }

    }


}