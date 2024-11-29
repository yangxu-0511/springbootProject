package com.study.common.thread;

/*
  @Author yangx
 * @Description 描述
 * @Since create in
 * @Company 广州云趣信息科技有限公司
 */

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.study.common.utils.Sm4Util;
import com.yq.busi.common.util.DateUtil;
import org.apache.commons.lang3.StringUtils;
import org.easitline.common.utils.kit.RandomKit;

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
        String tmpJStr="{\"fees\":\"10\",\"data\":\"5\",\"valid_period\":\"长期有效\",\"action_type\":\"D\",\"applicable_area\":\"7990,7010,7970,7980,7950,7960,7930,7940,7910,7920,7900\",\"channel\":\"线下渠道，中国移动APP，10086热线\",\"fees_unit\":\"元/月\",\"offline_day\":\"20991231\",\"tariff_attr\":\"2\",\"duration\":\"无\",\"applicable_people\":\"省内\",\"responsibility\":\"无 \",\"unsubscribe\":\"线下移动营业厅或者10086热线\",\"sms\":\"0\",\"orient_traffic\":\"0\",\"others\":\"1、功能费10元；2、含5GB全国流量（不含港澳台），订购立即生效，按月续订，流量可结转、不可共享、不可转赠；3、享5G臻享服务（下行最高3Gbps，上行最高200Mbps） \",\"seq_no\":\"JX22024102426\",\"type2\":\"1\",\"reporter\":\"JX2\",\"orient_traffic_unit\":\"MB\",\"type1\":\"1\",\"online_day\":\"20240527\",\"data_unit\":\"GB\",\"call\":\"0\",\"name\":\"5G-A标准流量包（10元5G流量月包）\",\"other_content\":\"1、功能费10元；2、含5GB全国流量（不含港澳台），订购立即生效，按月\n" +
                "续订，流量可结转、不可共享、不可转赠；3、享5G臻享服务（下行最高3Gbps，上行最高200Mbps）\"}";
        JSONObject data = JSONObject.parseObject(tmpJStr);
        List<String> delRequiredFields = Arrays.asList("seq_no", "reporter", "action_type", "origin_report_no");
            for (String field : delRequiredFields) {
                String val = data.getString(field);
                if (!StringUtils.isNotBlank(val)) {
                    System.out.println("'" + field + "'必填参数为空");
                }
            }

        String reportt = "SC1";
        System.out.println("sss="+reportt.substring(reportt.length() - 1));

        List<String> allowedKeys = Arrays.asList("pageIndex", "pageSize", "orderNo");


        System.out.println(String.format("%06d", 1));
        String dataJ = "{\"pageIndex\":1,\"pageSize\":500,\"orderNo\":\"\",\"appealPhone\":\"\",\"queryTime\":[\"2024-08-28 18:00:00\",\"2024-08-28 18:10:59\"]}";
        JSONObject dj =  JSONObject.parseObject(dataJ);
        System.out.println("szie= "+dj.getJSONArray("queryTime").size());

        for (String key : dj.keySet()) {
            if (!allowedKeys.contains(key)) {
                System.out.println("Invalid key found: " + key);
            }
        }


        DateTimeFormatter ff = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
        String currentDateString = LocalDateTime.now().format(ff);
        System.out.println(currentDateString);  // 用于调试


        String versionNum = null;
        versionNum = Optional.ofNullable(versionNum)
                .filter(StringUtils::isNotBlank)
                .map(version -> {
                    String versionSuffix = version.substring(version.lastIndexOf("V") + 1);
                    int newVersion = Integer.parseInt(versionSuffix) + 1;
                    return String.format("V%d",  newVersion);
                })
                .orElseGet(() -> String.format("V1"));
        System.out.println("版本号是: " + versionNum);

        double hsyzl = 494.8;
        int tmp = (int)Math.round(hsyzl);
        System.out.println("hsyzl="+tmp);


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
