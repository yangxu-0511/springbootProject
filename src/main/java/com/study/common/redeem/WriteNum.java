package com.study.common.redeem;/*
  @Author yangx
 * @Description 写号码
 * @Since create in 2024-4-24 11:27:34
 * @Company 广州云趣信息科技有限公司
 */

import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSONObject;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.study.common.base.AppBaseNum;
import com.study.common.base.Constants;
import com.study.common.utils.DateUtils;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Stream;

/**
 *@author yangxu
 *@create 2024/4/24 11:27
 */
public class WriteNum extends AppBaseNum {


    public static void writeMyNumber(String number) {
        writeMyNumber(number,1);
    }

    /**
     * @Author yangxu
     * @Description 把生成的号码写入到history.json
     * @Param: [number, type]
     * @Return: void
     * @Since create in 2024/1/17 14:02
     * @Company 广州云趣信息科技有限公司
     */
    public static void writeMyNumber(String number,int type) {
        String date;
        if(type==2) {
            date = DateUtils.getYesterdayDate();
        }else {
            date = DateUtils.getCurrentDate();
        }
        //判断号码是否存在
        File file = new File(Constants.getHisFilePath());
        // 创建JSON对象并设置键值对
        JSONObject jsonObject = new JSONObject();
        if(file.exists()) { //文件已经存在就取出来然后重新写入
            jsonObject = filterJson(Constants.getHisFilePath());
            assert jsonObject != null;
            String hisNum =jsonObject.getString(date);
            if(StrUtil.isNotEmpty(hisNum)) {//取出历史号码
                if(hisNum.contains(number)){
                    System.out.println("该号码已经存在……");
                    return;
                }
                hisNum = hisNum+"|"+number;
                jsonObject.put(date, hisNum);
            }else{
                jsonObject.put(date, number);
            }
        }else{
            jsonObject.put(date, number);
        }
        // 指定 JSON 文件路径
        try {
            // 创建 FileWriter 对象
            FileWriter fileWriter = new FileWriter(Constants.getHisFilePath());
            // 将 JSON 对象写入文件
            fileWriter.write(jsonObject.toJSONString());
            // 关闭 FileWriter
            fileWriter.close();
            System.out.println("号码已成功写入历史号码文件。");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /*
     * @Author yangxu
     * @Description 启用的号码也保存一下
     * @Param: [number]
     * @Return: void
     * @Since create in 2024/5/14 14:54
     * @Company 广州云趣信息科技有限公司
     */
    public static void writeNotBuyNumber(String number) {
        String date = DateUtils.getCurrentDate();
        //判断号码是否存在
        File file = new File(Constants.getNotBuyPath());
        // 创建JSON对象并设置键值对
        JSONObject jsonObject = new JSONObject();
        if(file.exists()) { //文件已经存在就取出来然后重新写入
            jsonObject = filterJson(Constants.getNotBuyPath());
            assert jsonObject != null;
            String hisNum =jsonObject.getString(date);
            if(StrUtil.isNotEmpty(hisNum)) {//取出历史号码
                if(hisNum.contains(number)){
                    System.out.println("该号码已经存在……");
                    return;
                }
                hisNum = hisNum+"|"+number;
                jsonObject.put(date, hisNum);
            }else{
                jsonObject.put(date, number);
            }
        }else{
            jsonObject.put(date, number);
        }
        // 指定 JSON 文件路径
        try {
            // 创建 FileWriter 对象
            FileWriter fileWriter = new FileWriter(Constants.getNotBuyPath());
            // 将 JSON 对象写入文件
            fileWriter.write(jsonObject.toJSONString());
            // 关闭 FileWriter
            fileWriter.close();
            System.out.println("号码已成功写入未买号码文件。");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * @Author yangxu
     * @Description 给json文件里面的内容排序
     * @Param: []
     * @Return: void
     * @Since create in 2024/8/6 11:10
     * @Company 广州云趣信息科技有限公司
     */
    public static void sortJson() {
        List<String> pathList = new ArrayList<>();
        pathList.add(Constants.getHisFilePath());
        pathList.add(Constants.getTcFilePath());
        pathList.add(Constants.getFcFilePath());
        pathList.add(Constants.getNotBuyPath());

        List<String> outList = new ArrayList<>();
        outList.add(Constants.getHisOutFilePath());
        outList.add(Constants.getTcFileOutPath());
        outList.add(Constants.getFcFileOutPath());
        outList.add(Constants.getNotBuyOutPath());

        Gson gson = new Gson();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat fileDate = new SimpleDateFormat("yyyyMMdd");


        Stream.iterate(0, i -> i + 1).limit(pathList.size()).forEach(i -> {
            String hisPath = pathList.get(i);
            String outPath = outList.get(i);
            try (FileReader reader = new FileReader(hisPath)) {

                Type mapType = new TypeToken<Map<String, String>>() {
                }.getType();
                Map<String, String> data = gson.fromJson(reader, mapType);

                List<Map.Entry<String, String>> entries = new ArrayList<>(data.entrySet());
                entries.sort((e1, e2) -> {
                    try {
                        return sdf.parse(e2.getKey()).compareTo(sdf.parse(e1.getKey()));
                    } catch (ParseException e) {
                        throw new RuntimeException(e);
                    }
                });

                Map<String, String> sortedData = new LinkedHashMap<>();
                for (Map.Entry<String, String> entry : entries) {
                    sortedData.put(entry.getKey(), entry.getValue());
                }
                try (FileWriter writer = new FileWriter(outPath)) {
                    gson.toJson(sortedData, writer);
                }
                System.out.println("JSON data has been sorted and written to " + outPath);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        Stream.iterate(0, i -> i + 1).limit(pathList.size()).forEach(i -> {
            String hisPath = pathList.get(i);
            String outPath = outList.get(i);

            File oriFile = new File(hisPath);
            File outFile = new File(outPath);
            String todayDate = fileDate.format(new Date());

            File newFile = new File(Constants.getBasePath(), oriFile.getName().split("\\.")[0]+"_"+todayDate+".json");
            File newOutFile = new File(Constants.getBasePath(), oriFile.getName());

            if (oriFile.renameTo(newFile)) {
                System.out.println(oriFile.getName()+" renamed to " + newFile.getName());
            } else {
                System.out.println("Failed to rename "+oriFile.getName());
            }
            if (outFile.renameTo(newOutFile)) {
                System.out.println(outFile.getName()+" renamed to " + newOutFile.getName());
            } else {
                System.out.println("Failed to rename "+outFile.getName());
            }
        });


    }
}
