package com.study.common.redeem;

import cn.hutool.core.util.StrUtil;

public class AppbaseController {

    public static void main(String[] args) {
        //获取每天的号码
        getToday();
        //每天执行一次脚本
        //获取号码
//		action("2","fc");
//		自动兑奖
//		action("3","");
        //指定日期兑奖=T-1 如果日期是周六=T-2
//		action("3","2024-08-31");
        //写入号码 购买日期默认为昨天的日期
//		action("4","07,08,10,18,19 05,08");
        //把json文件排序
//      action("5",null);

    }

    /*
     * @Author yangxu
     * @Description 1：执行python脚本获取最新号码 2：获取今晚号码 3：号码比对
     * @Param: [type, flag]
     * @Return: void
     * @Since create in 2024/1/22 13:42
     * @Company 广州云趣信息科技有限公司
     */
    public static void action(String type, String params) {
        switch (type) {
            case "1": //执行Python脚本获取最新开奖号码
                RunPython.run();
                break;
            case "2": //获取今晚中奖号码
                if (StrUtil.isNotEmpty(params)) {
                    DreamNumer.getDreamNum(params);
                } else DreamNumer.getDreamNum();
                break;
            case "3": //兑奖
                ReedomNum.redeem(params);
                break;
            case "4": //写入号码
                WriteNum.writeMyNumber(params, 2);
                break;
            case "5": //写入号码
                WriteNum.sortJson();
                break;
            default:
                break;
        }
    }

    public static void action(String type) {
        action(type, null);
    }

    public static void getToday() {
        action("1");
        action("2", null);
    }

}
