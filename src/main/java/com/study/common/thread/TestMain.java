package com.study.common.thread;

/*
  @Author yangx
 * @Description 描述
 * @Since create in
 * @Company 广州云趣信息科技有限公司
 */

import cn.hutool.core.date.DateUtil;
import com.alibaba.fastjson.JSONObject;
import com.study.common.utils.Sm4Util;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

/**
 *@author yangxu
 *@create 2024/1/16 16:15
 */
public class TestMain {
    public TestMain() throws IOException {
    }

    public static void main(String[] args) throws Exception {
        String versionNum = "V12";

        versionNum = Optional.ofNullable(versionNum)
                .filter(StringUtils::isNotBlank)
                .map(version -> {
                    String versionSuffix = version.substring(version.lastIndexOf("V") + 1);
                    int newVersion = Integer.parseInt(versionSuffix) + 1;
                    return String.format("V%d",  newVersion);
                })
                .orElseGet(() -> String.format("V1"));
        System.out.println("版本号是: " + versionNum);

//        Map<Integer,Integer> tmp = new HashMap<>();
//        tmp.put(5,2);
//        tmp.put(6,1);
//        tmp.entrySet().stream().sorted(Map.Entry.comparingByKey(Comparator.reverseOrder())).forEach(System.out::println);
        String reporter = "SC00200";
        reporter = reporter.substring(0, reporter.length()-2);
        System.out.println(reporter);

        String sysReporter = "JT005012,JT00503,JT00502";
        String reporters = "JT00501";
        boolean ifExist = Arrays.stream(sysReporter.split(","))
                .anyMatch(element -> element.equals(reporters));

        boolean ifExist2 = Arrays.stream(sysReporter.split(","))
                .collect(Collectors.toList()).contains(reporters);
        System.out.println("==="+ifExist);
        System.out.println("===2"+ifExist2);


        String type = "99010401";

        // 获取字符串的最后一位数字
        String lastDigit = type.substring(type.length() - 1);

        System.out.println("最后一位数字是: " + lastDigit);

        String[] arr1 = new String[]{"3","4","5"};
        System.out.println(arr1[0]);
        System.out.println(arr1[2]);

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

    public static  List<String> generateMonthList(String startDate, String endDate) throws ParseException {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMM");
        List<String> months = new ArrayList<>();
        Calendar start = Calendar.getInstance();
        start.setTime(dateFormat.parse(startDate));
        Calendar end = Calendar.getInstance();
        end.setTime(dateFormat.parse(endDate));

        while (!start.after(end)) {
            months.add(dateFormat.format(start.getTime()));
            start.add(Calendar.MONTH, 1);
        }
        for (int i = 0; i < months.size(); i++){
            System.out.println("月份："+months.get(i));
        }
        return months;
    }
}
