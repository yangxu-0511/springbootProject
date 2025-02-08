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
import org.apache.commons.collections4.CollectionUtils;
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

        String dateIdss = "20250122";
        String fileName = "JS2-20250122-result.zip";
        String endsWith = "-" + dateIdss + "-result.zip";
        String entCode = "2";
        String tariffProvinceCode = "";

        String startWith = tariffProvinceCode + entCode + "-"; // 返回省份代码和企业代码的组合

        if (!fileName.endsWith(endsWith) || !fileName.startsWith(startWith)) {
            System.out.println("不满足===");
        }else{
            System.out.println("满足==");
        }

        String feeName = " XX套餐(国内) (香港)   资费  ";
        String formattedFeeName = formatFeeName(feeName);
        System.out.println(formattedFeeName);  // 输出: XX套餐（国内）（香港）资费

        // 构建 List<JSONObject>
        List<JSONObject> result = new ArrayList<>();
        List<JSONObject> auditResult = new ArrayList<>();

        // 模拟数据
        String date = "2024-12-03";
        int telecom = 42;
        int mobile = 0;
        int unicom = 216;
        int broad = 0;
        int allTotal = 258;
        // 创建 JSON 对象
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("DATE_ID", date);
        jsonObject.put("TELECOM", telecom);
        jsonObject.put("MOBILE", mobile);
        jsonObject.put("UNICOM", unicom);
        jsonObject.put("BROAD", broad);
        jsonObject.put("MAX_DATE", date);
        jsonObject.put("MIN_DATE", date);
        jsonObject.put("ALLTOTAL", allTotal);

        System.out.println("dateId="+jsonObject.containsKey("date_id")+",DATE_ID="+jsonObject.containsKey("DATE_ID"));

        // 创建 JSON 对象
        JSONObject jsonObject2 = new JSONObject();
        jsonObject2.put("DATE_ID", "平均(去重)");
        jsonObject2.put("TELECOM", 42);
        jsonObject2.put("MOBILE", 0);
        jsonObject2.put("UNICOM", 216);
        jsonObject2.put("BROAD", 0);
        jsonObject2.put("MAX_DATE", "2024-12-03");
        jsonObject2.put("MIN_DATE", "2024-12-03");
        jsonObject2.put("ALLTOTAL", 258);
        // 添加到结果列表
        result.add(jsonObject);
        result.add(jsonObject2);

        JSONObject jsonObject3 = new JSONObject();
        jsonObject3.put("ENT", 1);
        jsonObject3.put("TARIFF_AUDIT_DATE", "20241203");

        JSONObject jsonObject4 = new JSONObject();
        jsonObject4.put("ENT", 2);
        jsonObject4.put("TARIFF_AUDIT_DATE", "20241203");

        JSONObject jsonObject5 = new JSONObject();
        jsonObject5.put("ENT", 3);
        jsonObject5.put("TARIFF_AUDIT_DATE", "20241203");
        auditResult.add(jsonObject3);
        auditResult.add(jsonObject4);
        auditResult.add(jsonObject5);

        for (JSONObject obj : result) {
            if (obj == null) continue;
            // 批量处理所有字段
            String[] fields = {"MOBILE", "UNICOM", "TELECOM", "BROAD", "ALLTOTAL"};
            for (String field : fields) {
                Integer value = obj.getInteger(field);
                obj.put(field, (value == null || value == 0) ? "" : value);
            }
        }

        // 预处理 auditResult 数据,构建查找Map
        Map<String, Map<String, String>> auditMap = new HashMap<>();
        for (JSONObject audit : auditResult) {
            String auditDate = audit.getString("TARIFF_AUDIT_DATE");
            String ent = audit.getString("ENT");
            auditMap.computeIfAbsent(auditDate, k -> new HashMap<>()).put(ent, "0");
        }

        // 使用Map优化查找和更新
        for (JSONObject resultItem : result) {
            String dateId = resultItem.getString("DATE_ID");
            if ("平均(去重)".equals(dateId)) {
                continue;
            }

            String maxDate = resultItem.getString("MAX_DATE");
            if (StringUtils.isBlank(maxDate)) {
                continue;
            }

            String formattedMaxDate = maxDate.replaceAll("-", "");
            Map<String, String> entMap = auditMap.get(formattedMaxDate);

            if (entMap != null) {
                // 使用Map直接映射字段名
                Map<String, String> fieldMap = new HashMap<>();
                fieldMap.put("1", "TELECOM");
                fieldMap.put("2", "MOBILE");
                fieldMap.put("3", "UNICOM");
                fieldMap.put("5", "BROAD");

                // 批量更新字段
                entMap.forEach((ent, value) -> {
                    String field = fieldMap.get(ent);
                    if (field != null && "".equals(resultItem.getString(field))) {
                        resultItem.put(field, 0);
                    }
                });
            }
        }

        for(JSONObject obj : result){
            System.out.println(obj.toJSONString());
        }

    }


    public static boolean isValidDate(String dateStr) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
        sdf.setLenient(false);  // 设置为不宽松的日期解析
        try {
            sdf.parse(dateStr);  // 尝试解析日期
            return true;  // 如果没有异常，日期有效
        } catch (ParseException e) {
            return false;  // 如果抛出异常，说明日期无效
        }
    }

    public static String formatFeeName(String feeName) {
        // 将英文括号替换为中文括号
        String formattedName = feeName.replace("(", "（").replace(")", "）");

        // 去掉所有空格（包括中间空格和首尾空格）
        formattedName = formattedName.replaceAll("\\s+", "");

        return formattedName;
    }

}
