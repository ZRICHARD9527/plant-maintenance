package com.hasakiii.iot.controller;

import com.alibaba.fastjson.JSONObject;
import com.hasakiii.iot.dto.Result;
import com.hasakiii.iot.service.IotService;
import com.hasakiii.iot.util.IotdbPool;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.sql.Connection;
import java.sql.SQLException;

import static java.lang.String.valueOf;

/**
 * @Author: Z.Richard
 * @CreateTime: 2020/12/6 11:05
 * @Description:
 **/
@Api(tags = "客户端接口文档")
@RestController
public class Controller {


    @Resource
    IotService iotService;

    @GetMapping("/hello")
    public String hello() {
        try {
            Connection c1 = IotdbPool.getConnection();
            System.out.println(c1.toString());
            IotdbPool.remove(c1);
            System.out.println(IotdbPool.getConnection().toString());
            System.out.println(IotdbPool.getConnection().toString());
            System.out.println(IotdbPool.getConnection().toString());
            System.out.println(IotdbPool.getConnection().toString());
            System.out.println(IotdbPool.getConnection().toString());
            System.out.println(IotdbPool.getConnection().toString());

            return "连接成功";
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return "连接失败";

    }

    @ApiOperation(value = "获取实时温度", notes = "获取近30秒平均温度")
//    @ApiImplicitParams({
//            @ApiImplicitParam(name = "time" ,value = "时间戳",required = true,paramType = "body")
//    })
    @PostMapping("/temperature")
    public Result getTemp(@RequestBody JSONObject jsonObject) {
        long time = Long.parseLong(valueOf(jsonObject.get("time")));
        return iotService.getTemp(time);
    }

    @PostMapping("/humidity")
    public Result getHum(@RequestBody JSONObject jsonObject) {
        return iotService.getHum(jsonObject.getLong("time"));
    }

    @GetMapping("/allTem")
    public Result allTem(@RequestParam("gap") Long gap) {
        return iotService.allTem(gap);
    }

    @GetMapping("/allHum")
    public Result allHum(@RequestParam("gap") Long gap) {
        return iotService.allHum(gap);
    }

    @GetMapping("/notice")
    public Result getNotice() {
        return iotService.getNotice();
    }

    @PostMapping("/order")
    public Result order(@RequestBody JSONObject jsonObject) {
        Integer i = Integer.valueOf(String.valueOf(jsonObject.get("order")));
        System.out.println(jsonObject.toString());
        return iotService.order(i);
    }

    @PostMapping("/orderTest")
    public Result orderTest(@RequestBody JSONObject jsonObject) {
        System.out.println(jsonObject.toString());
        return new Result(0, "测试", jsonObject.getInteger("order"));
    }

    /**
     * ********************************************************************************************************
     */

    @PostMapping("/insert")
    public Result insert(@RequestBody JSONObject jsonObject) {
//        System.out.println(jsonObject.toString());
        Long time = null;
        Float tem = null;
        Float humidity = null;
        try {
            time = Long.parseLong(valueOf(jsonObject.get("time")));
            tem = Float.parseFloat(valueOf(jsonObject.get("temperature")));
            humidity = Float.parseFloat(valueOf(jsonObject.get("humidity")));
        } catch (ClassCastException e) {
            e.printStackTrace();
            return new Result(-1, "插入参数有误", null);
        }
        return iotService.insert(time, tem, humidity);
    }

}
