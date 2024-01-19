package com.study.common.redeem;

import cn.hutool.core.util.StrUtil;

public class Redeem {

	public static void main(String[] args) {
		//获取每天的
		getToday();
		//每天执行一次脚本
		action("1");
		//获取号码
//		action("2","tc");
		//自动兑奖

		//手动兑奖 传号码
	}

	public static void getToday(){
		action("1");
		action("2",null);
	}

	public static void action(String type){
		action(type,null);
	}

	public static void action(String type,String flag){
		switch (type) {
			case "1": //执行Python脚本获取最新开奖号码
				RunPython.run();
				break;
			case "2": //获取今晚中奖号码
				if(StrUtil.isNotEmpty(flag)){
					DreamNumer.getDreamNum(flag);
				}else DreamNumer.getDreamNum();
				break;
			case "3": //兑奖
				break;
		}
	}

}
