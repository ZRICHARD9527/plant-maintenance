package com.hasakiii.iot.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Author: Z.Richard
 * @CreateTime: 2020/12/10 16:59
 * @Description:
 **/
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Result {
    /**
     * -1表示获取失败，0表示成功，1表示温度过高，2温度过低，3表示过湿，4表示湿度不够
     */
    private Integer code;
    
    /**
     * 携带信息
     */
    private String msg;
    
    /**
     * 携带数据
     */
    private Object data;
}
