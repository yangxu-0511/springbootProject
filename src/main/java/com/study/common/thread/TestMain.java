package com.study.common.thread;/**
 * @Author yangx
 * @Description 描述
 * @Since create in
 * @Company 广州云趣信息科技有限公司
 */

import com.study.common.model.Dog;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

/**
 *@author yangxu
 *@create 2024/1/16 16:15
 */
public class TestMain {
    public static void main(String[] args) {
        String dateStr = "20240317";

        // 使用 DateTimeFormatter 解析日期字符串
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");
        DateTimeFormatter formatter2 = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        String params = "20240320"; // 假设这是你的日期参数
        LocalDate localDate = LocalDate.parse(params, formatter);
        LocalDate previousDay = localDate.minusDays(1);
        System.out.println("前一天的日期：" + previousDay.format(formatter2));

        String yesNum = "02,12,13,17,26,32 11";
        String y_redNum = yesNum.split("\\s")[0];
        String y_blueNum = yesNum.split("\\s")[1];
        //获取昨天的号码红蓝球
        List<Integer> y_redArr  = Arrays.stream(y_redNum.split(","))
                .map(Integer::parseInt)
                .collect(Collectors.toList());

        List<Integer> y_blueArr  = Arrays.stream(y_blueNum.split(","))
                .map(Integer::parseInt)
                .collect(Collectors.toList());

        y_blueArr.forEach(System.out::println);


    }
}
