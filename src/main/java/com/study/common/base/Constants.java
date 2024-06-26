package com.study.common.base;/*
 * @Author yangx
 * @Description 常量类
 * @Since create in 2024-4-24 11:10:51
 * @Company 广州云趣信息科技有限公司
 */

import java.util.HashMap;
import java.util.Map;

/**
 *@author yangxu
 *@create 2024/4/24 11:10
 */
public class Constants {
    private static final String hisFilePath = "D:\\idea-workspace\\springbootProject\\history.json";
    private static final String notBuyPath = "D:\\idea-workspace\\springbootProject\\notBuy.json";
    private static final String tcFilePath  = "D:\\idea-workspace\\springbootProject\\dlt.json";
    private static final String fcFilePath  = "D:\\idea-workspace\\springbootProject\\ssq.json";
    public static final int similarSize = 5; //定义相似度个数
    public static final int sameRedSize = 4; //定义红球命中个数
    public static final int sameHisSize = 3; //历史命中数
    private static final Map<String, String> tcMap = new HashMap<>();
    private static final Map<String, String> fcMap = new HashMap<>();
    static {
        tcMap.put("5-2", "恭喜你成为百万富翁……历史性的一刻！！！中奖金额>=500万");
        tcMap.put("5-1", "恭喜中了二等奖 卸下了很大一部分负担！预计奖金30万");
        tcMap.put("5-0", "恭喜中了三等奖 一笔可观的意外之财! 奖金=1万");
        tcMap.put("4-2", "恭喜中了四等奖 一个月生活费！奖金=3000");
        tcMap.put("4-1", "恭喜中了五等奖 买一版刮刮乐吧！奖金=300");
        tcMap.put("3-2", "恭喜中了六等奖 买四张刮刮乐吧！奖金=200");
        tcMap.put("4-0", "恭喜中了七等奖 买两张刮刮乐吧！奖金=100");
        tcMap.put("3-1", "恭喜中了八等奖 买一张刮刮乐吧！奖金=15");
        tcMap.put("2-2", "恭喜中了八等奖 买一张刮刮乐吧！奖金=15");
        tcMap.put("3-0", "恭喜中了九等奖 买一张刮刮乐吧！奖金=5");
        tcMap.put("1-2", "恭喜中了九等奖 买一张刮刮乐吧！奖金=5");
        tcMap.put("2-1", "恭喜中了九等奖 买一张刮刮乐吧！奖金=5");
        tcMap.put("0-2", "恭喜中了九等奖 买一张刮刮乐吧！奖金=5");

        fcMap.put("6-1", "恭喜你成为百万富翁……历史性的一刻！！！中奖金额>=500万");
        fcMap.put("6-0", "恭喜中了二等奖 卸下了很大一部分负担！预计奖金30万");
        fcMap.put("5-1", "恭喜中了三等奖 一笔可观的意外之财! 奖金=1万");
        fcMap.put("5-0", "恭喜中了四等奖 买四张刮刮乐吧！奖金=200");
        fcMap.put("4-1", "恭喜中了四等奖 买四张刮刮乐吧！奖金=200");
        fcMap.put("4-0", "恭喜中了五等奖 买一张刮刮乐吧！奖金=10");
        fcMap.put("3-1", "恭喜中了五等奖 买一张刮刮乐吧！奖金=10");
        fcMap.put("2-1", "恭喜中了六等奖 买一张刮刮乐吧！奖金=5");
        fcMap.put("1-1", "恭喜中了六等奖 买一张刮刮乐吧！奖金=5");
        fcMap.put("0-1", "恭喜中了六等奖 买一张刮刮乐吧！奖金=5");
    }
    public static Map<String, String> getTcMap() {
        return tcMap;
    }

    public static Map<String, String> getFcMap() {
        return fcMap;
    }

    public static String getHisFilePath() {
        return hisFilePath;
    }
    public static String getTcFilePath() {
        return tcFilePath;
    }
    public static String getFcFilePath() {
        return fcFilePath;
    }
    public static String getNotBuyPath() {
        return notBuyPath;
    }
}
