package com.hasakiii.iot.util;


import java.sql.Connection;
import java.sql.SQLException;
import java.util.LinkedList;

/**
 * @Author: Z.Richard
 * @CreateTime: 2020/12/6 10:36
 * @Description:
 **/

public class IotdbPool {

    private static LinkedList<Connection> pool;
    private static Integer MAX_NUM = 5;

    //从连接池获取连接
    public static Connection getConnection() throws SQLException {
        if (pool == null) {
            pool = new LinkedList<>();
            for (int i = 0; i < MAX_NUM; i++) {
                pool.add(IotdbUtil.getConnection());
            }
        }
        if (pool.size() <= 0) {
            //创建零时连接，可以防止出现大量空闲连接
            return IotdbUtil.getConnection();
        }
        //返回第一个连接，并从列表中去除
        return pool.remove();
    }

    //返还连接
    public static void remove(Connection connection) {
        pool.add(connection);
    }
}
