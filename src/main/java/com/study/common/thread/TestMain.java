package com.study.common.thread;

/*
  @Author yangx
 * @Description 描述
 * @Since create in
 * @Company 广州云趣信息科技有限公司
 */

import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.net.ConnectException;
import java.net.SocketTimeoutException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 *@author yangxu
 *@create 2024/1/16 16:15
 */
public class TestMain {
    public TestMain() throws IOException {
    }

    public static void main(String[] args) throws IOException {
        JSONObject json = new JSONObject();
        json.put("ID","ID");
        json.put("name","张三");
        String tt = setSex(json);
        System.out.println(json.toJSONString());

        String ex = "Unknown error Exception ";
        if(ex.contains("Exception")){
            System.out.println("111222");
        }

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalDateTime currentTime = LocalDateTime.now();
        LocalDateTime newTime = currentTime.plusMinutes(15);
        System.out.println(newTime.format(formatter));

        // 使用 DateTimeFormatter 解析日期字符串

        String yesNum = "02,12,13,17,26,32 11";
        String y_redNum = yesNum.split("\\s")[0];
        String y_blueNum = yesNum.split("\\s")[1];
        //获取昨天的号码红蓝球
        List<Integer> y_redArr  = Arrays.stream(y_redNum.split(","))
                .map(Integer::parseInt)
                .collect(Collectors.toList());
        y_redArr.forEach(System.out::println);

        List<Integer> y_blueArr  = Arrays.stream(y_blueNum.split(","))
                .map(Integer::parseInt)
                .collect(Collectors.toList());

        y_blueArr.forEach(System.out::println);


    }

    private static String setSex(JSONObject json) {
        json.put("sex","sex");
        return setSex2(json);
    }

    private static String setSex2(JSONObject json) {
        json.put("sex2","sex2");
        return "你好";
    }
}
