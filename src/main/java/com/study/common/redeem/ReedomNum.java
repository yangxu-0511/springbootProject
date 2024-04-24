package com.study.common.redeem;/*
 * @Author yangx
 * @Description 描述
 * @Since create in 2024-4-24 11:34:05
 * @Company 广州云趣信息科技有限公司
 */

import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSONObject;
import com.study.common.base.AppBaseNum;
import com.study.common.base.Constants;
import com.study.common.utils.DateUtils;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 *@author yangxu
 *@create 2024/4/24 11:34
 */
public class ReedomNum extends AppBaseNum {


    /**
     * @Author yangxu
     * @Description 号码比对
     * @Param: [params]
     * @Return: void
     * @Since create in 2024/1/22 13:57
     * @Company 广州云趣信息科技有限公司
     */
    public static void redeem(String params) {
        RunPython.run();

        String openDate = "";
        String buyDate = "";
        String filePath =  "";
        String zjType = "";
        int currentDay= -1;
        int blueSize = -1;
        int redSize = -1;
        if(StrUtil.isEmpty(params)){
            openDate = DateUtils.getYesterdayDate(); //昨天的日期
            buyDate = DateUtils.getYesterdayDate(); //昨天的日期
            // 获取昨天的日期
            LocalDate yesterday = LocalDate.now().minusDays(1);
            // 计算昨天是星期几
            currentDay = yesterday.getDayOfWeek().getValue();
        }else{
            openDate = params;
            buyDate = params;
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            LocalDate localDate = LocalDate.parse(params, formatter);
            currentDay = localDate.getDayOfWeek().getValue();
        }

        if(currentDay==1  || currentDay==3 || currentDay==5 || currentDay==6) {
            filePath = Constants.getTcFilePath();
            zjType = "tc";
            blueSize = 2;
            redSize = 5;
            if(currentDay == 6){ //获取周五的购彩
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                LocalDate localDate = LocalDate.parse(buyDate, formatter);
                LocalDate previousDay = localDate.minusDays(1);
                buyDate = previousDay.format(formatter);
            }
        }
        if(currentDay==2  || currentDay==4 || currentDay==0 || currentDay==7) {
            filePath = Constants.getFcFilePath();
            zjType = "fc";
            blueSize = 1;
            redSize = 6;

            if(currentDay == 7){ //获取周五的购彩
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                LocalDate localDate = LocalDate.parse(buyDate, formatter);
                LocalDate previousDay = localDate.minusDays(2);
                buyDate = previousDay.format(formatter);
            }


        }
        //1.取出昨天的出奖号码
        JSONObject openData =  filterJson(filePath);
        String openNumber = openData.getString(openDate);
        if(StrUtil.isEmpty(openNumber)){
            System.out.println("未获取到昨天的中奖号，执行失败……");
            return ;
        }
        System.out.println("开奖奖项是："+("tc".equals(zjType)?"大乐透":"双色球")+"号码为："+openNumber);
        String redNum = Arrays.stream(openNumber.split("\\|"))
                .limit(redSize)
                .collect(Collectors.joining("|"));
        List<Integer> openRedArr  = Arrays.stream(redNum.split("\\|"))
                .map(Integer::parseInt)
                .collect(Collectors.toList());

        String blueNum = Arrays.stream(openNumber.split("\\|"))
                .skip(Math.max(0, openNumber.split("\\|").length - blueSize))
                .collect(Collectors.joining("|"));
        List<Integer> openBlueArr  = Arrays.stream(blueNum.split("\\|"))
                .map(Integer::parseInt)
                .collect(Collectors.toList());
        //自动兑奖
        //1.获取昨天获取的号码
        JSONObject hisJson = filterJson(Constants.getHisFilePath());
        String yesterdayNum  = hisJson.getString(buyDate); // "02,12,13,17,26,32 11|07,12,20,21,31 06,12|04,06,14,29,34 01,09"
        if(StrUtil.isNotEmpty(yesterdayNum)){
            //优先对比昨天的号码昨天
            System.out.println("开始对比当天购买的号码---->start");
            String[] yesterdayNumsArr = yesterdayNum.split("\\|");
            List<Integer> y_redArr = new ArrayList<>();
            List<Integer> y_blueArr = new ArrayList<>();
            for (String yesNum:yesterdayNumsArr) {
                if(yesNum.split("\\s")[1].split(",").length==blueSize){ //剔除不满足的号码
                    int redCount = 0;
                    int blueCount = 0;
                    String y_redNum = yesNum.split("\\s")[0];
                    String y_blueNum = yesNum.split("\\s")[1];
                    //获取昨天的号码红蓝球
                    y_redArr = Arrays.stream(y_redNum.split(","))
                            .map(Integer::parseInt)
                            .collect(Collectors.toList());

                    y_blueArr = Arrays.stream(y_blueNum.split(","))
                            .map(Integer::parseInt)
                            .collect(Collectors.toList());

                    for (Integer num : y_redArr) {
                        if (openRedArr.contains(num)) {
                            redCount++;
                        }
                    }
                    //redeemSize
                    for (Integer num : y_blueArr) {
                        if (openBlueArr.contains(num)) {
                            blueCount++;
                        }
                    }
                    String key = redCount + "-" + blueCount;
                    if("tc".equals(zjType)){
                        if (Constants.getTcMap().containsKey(key)) {
                            System.out.println(Constants.getTcMap().get(key));
                        }
                    }else{
                        if (Constants.getFcMap().containsKey(key)) {
                            System.out.println(Constants.getFcMap().get(key));
                        }
                    }
                }
            }
        }else{
            System.out.println("当天尚未购彩----");
        }
        System.out.println("当天号码对比结束---->end");
        System.out.println("开始统计历史购彩记录有无中奖信息（只统计红球>="+Constants.sameRedSize+")--->start");
        List<Integer> his_redArr = new ArrayList<>();
        List<Integer> his_blueArr = new ArrayList<>();
        for (String key : hisJson.keySet()) {
            String hisNumStr = hisJson.getString(key);
            String[] hisNumArr = hisNumStr.split("\\|");
            for(String hisNum:hisNumArr){
                if(hisNum.split("\\s")[1].split(",").length==blueSize){ //剔除不满足的号码
                    int redCount = 0;
                    int blueCount = 0;
                    String his_redNum = hisNum.split("\\s")[0];
                    String his_blueNum = hisNum.split("\\s")[1];
                    //获取昨天的号码红蓝球
                    his_redArr = Arrays.stream(his_redNum.split(","))
                            .map(Integer::parseInt)
                            .collect(Collectors.toList());

                    his_blueArr = Arrays.stream(his_blueNum.split(","))
                            .map(Integer::parseInt)
                            .collect(Collectors.toList());

                    for (Integer num : his_redArr) {
                        if (openRedArr.contains(num)) {
                            redCount++;
                        }
                    }
                    //redeemSize
                    for (Integer num : his_blueArr) {
                        if (openBlueArr.contains(num)) {
                            blueCount++;
                        }
                    }

                    if(redCount>=Constants.sameRedSize){ //小奖不统计
                        outReedmInfo(zjType,redCount,blueCount,key,hisNum);
                    }
                }
            }
        }
        System.out.println("历史号码统计结束---->end");
        //统计开奖号码跟历史开奖号码相似程度
        System.out.println("统计开奖号码跟历史开奖号码相似程度-->");
        comparisonOpenNum(openRedArr,openBlueArr,redSize,blueSize,openData);
    }

    /*
     * @Author yangxu
     * @Description
     * 体彩：
        一等奖：投注号码与当期开奖号码全部相同(顺序不限，下同)，即中奖； (浮动 >=500万 5+2)
        二等奖：投注号码与当期开奖号码中的五个前区号码及任意一个后区号码相同，即中奖；(浮动30万左右 5+1)
        三等奖：投注号码与当期开奖号码中的五个前区号码相同，即中奖；(=10000 5+0)
        四等奖：投注号码与当期开奖号码中的任意四个前区号码及两个后区号码相同，即中奖；(=3000 4+2)
        五等奖：投注号码与当期开奖号码中的任意四个前区号码及任意一个后区号码相同，即中奖；(=300 4+1)
        六等奖：投注号码与当期开奖号码中的任意三个前区号码及两个后区号码相同，即中奖；(=200 3+2)
        七等奖：投注号码与当期开奖号码中的任意四个前区号码相同，即中奖；(=100 4+0)
        八等奖：投注号码与当期开奖号码中的任意三个前区号码及任意一个后区号码相同，或者任意两个前区号码及两个后区号码相同，即中奖；(=15 3+1 || 2+2)
        九等奖：投注号码与当期开奖号码中的任意三个前区号码相同，或者任意一个前区号码及两个后区号码相同，或者任意两个前区号码及任意一个后区号码相同，或者两个后区号码相同，即中奖。
        (=5 3+0 || 1+2 || 0+2)
     * 福彩：
        一等奖：投注号码与当期开奖号码全部相同（顺序不限，下同），即中奖；(浮动 >=500万 6+1)
        二等奖：投注号码与当期开奖号码中的6个红色球号码相同，即中奖；	(浮动 30万左右 6+0)
        三等奖：投注号码与当期开奖号码中的任意5个红色球号码和1个蓝色球号码相同，即中奖；(=10000 5+1)
        四等奖：投注号码与当期开奖号码中的任意5个红色球号码相同，或与任意4个红色球号码和1个蓝色球号码相同，即中奖；(=200 5+0 || 4+1)
        五等奖：投注号码与当期开奖号码中的任意4个红色球号码相同，或与任意3个红色球号码和1个蓝色球号码相同，即中奖；(=10 4+0 || 3+1)
        六等奖：投注号码与当期开奖号码中的1个蓝色球号码相同，即中奖。(=5 2+1 || 0+1)
     * @Param: [zjType, redCount, blueCount]
     * @Return: void
     * @Since create in 2024/3/20 11:52
     * @Company 广州云趣信息科技有限公司
     */
    private static void outReedmInfo(String zjType, int redCount, int blueCount,String date,String hisNum) {
        String key = redCount + "-" + blueCount;
        if("tc".equals(zjType)){
            if (Constants.getTcMap().containsKey(key)) {
                System.out.println("你曾经在"+date+"购买的这注彩票 目前已经出奖！-->"+hisNum);
                System.out.println(Constants.getTcMap().get(key));
            }
        }else{
            if (Constants.getFcMap().containsKey(key)) {
                System.out.println("你曾经在"+date+"购买的这注彩票 目前已经出奖！-->"+hisNum);
                System.out.println(Constants.getFcMap().get(key));
            }
        }
    }
}
