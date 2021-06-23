package com.hasakiii.iot.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Author: Z.Richard
 * @CreateTime: 2020/12/10 21:55
 * @Description:
 **/
@Data
@NoArgsConstructor
public class Warn {

    public static final Float MaxTemperature = 30.0f;
    public static final Float MinTemperature = 10.0f;
    public static final Float MaxHumidity = 0.7f;
    public static final Float MinHumidity = 0.4f;


    //判断温湿度是否适宜
    public static Integer isSuit(Float tem, Float humidity) {
        if (tem == null || humidity == null) {
            return -1;
        }
        if (tem >= MaxTemperature) {
            return 1;
        } else if (tem <= MinTemperature) {
            return 2;
        } else if (humidity >= MaxHumidity) {
            return 3;
        } else if (humidity <= MinHumidity) {
            return 4;
        } else {
            return 0;//适宜
        }

    }

    //温度是否适宜
    public static Integer isTempSuit(Float tem) {
        if (tem == null) {
            return -1;
        }
        if (tem >= MaxTemperature) {
            return 1;
        } else if (tem <= MinTemperature) {
            return 2;
        } else {
            return 0;
        }
    }

    //湿度是否适宜
    public static Integer isHumSuit(Float humidity) {
        if (humidity == null) {
            return -1;
        }
        if (humidity >= MaxHumidity) {
            return 3;
        } else if (humidity <= MinHumidity) {
            return 4;
        } else {
            return 0;//适宜
        }
    }
}
