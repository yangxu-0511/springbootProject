package com.study.common.redeem;

import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSONObject;
import com.study.common.base.AppBaseNum;
import com.study.common.base.Constants;

import java.util.*;
import java.util.stream.Collectors;

public class DreamNumer extends AppBaseNum {

	private static final Random ran = new Random();
	private static final Set <Integer> redSet = new HashSet<>();
	private static final Set <Integer> blueSet = new HashSet<>();

	//1-35 1-12 1 3 6 5+2 大乐透
	//1-33 1-16 2 4 7 6+1 双色球
	public static void getDreamNum() {
		String zjType = getZjType();
		getDreamNum(zjType);
	}
	//1-35 1-12 1 3 6 5+2 大乐透
	//1-33 1-16 2 4 7 6+1 双色球
	public static void getDreamNum(String zjType) {
		redSet.clear();
		blueSet.clear();
		if(StrUtil.isNotEmpty(zjType)){
			if("tc".equals(zjType)) {
				tcDays();
			}else if("fc".equals(zjType)) {
				fcDays();
			}else {
				System.out.println("无效类型！");
				return ;
			}
		}
		filterData(zjType);
	}

	private static String getZjType() {
		Calendar calendar=Calendar.getInstance();
		String zjType = "";
		int currentDay = calendar.get(Calendar.DAY_OF_WEEK)-1;
		if(currentDay==1  || currentDay==3 || currentDay==5 || currentDay==6) {
			tcDays();
			zjType = "tc";
		}
		if(currentDay==2  || currentDay==4 || currentDay==0) {
			fcDays();
			zjType = "fc";
		}
		return zjType;
	}

	/**
	 *
	 * @Author yangx
	 * @Description 筛选历史中奖信息
	 * @Since create in 2023年10月26日
	 * @Company 广州云趣信息科技有限公司
	 * @param zjType 中奖的开奖类型
	 */
	private static void filterData(String zjType) {
		String jsonFilePath;
		int redSize;
		int blueSize;
		if("tc".equals(zjType)) {
			jsonFilePath = Constants.getTcFilePath();
			redSize = 5;
			blueSize = 2;
		}else if("fc".equals(zjType)) {
			jsonFilePath = Constants.getFcFilePath();
			redSize = 6;
			blueSize = 1;
		}else {
			System.out.println("无效类型！");
			return ;
		}
		TreeSet<Integer> set = new TreeSet<>(new Comparator<Integer>() {
            @Override
            public int compare(Integer t1, Integer t2) {
                //t1.compareTo(t2)  是从小到大正序排序,同理t2 . t1 就是倒序排序;
                return t1.compareTo(t2);
            }
        });
		set.addAll(redSet);
		String zjNum = "";
		zjNum = set.stream()
				.map(num -> {
					return (num < 10) ? "0" + num : String.valueOf(num);
				})
				.collect(Collectors.joining("|"));
		set.clear();
		set.addAll(blueSet);
		zjNum = zjNum +"|"+ set.stream()
					.map(num -> {
						return (num < 10) ? "0" + num : String.valueOf(num);
					})
				.collect(Collectors.joining("|"));
		try {
		    JSONObject historyData = filterJson(jsonFilePath);
		    boolean isExist = false;
		    for (String key : historyData.keySet()) {
		    	if(zjNum.equals(historyData.get(key))) {
		    		isExist = true;
		    		break;
		    	}
		    }
		    if(isExist) {
		    	System.out.println("号码跟中奖重复啦，晚生成一步，我重新生成一个新的中奖号码 ^^ "+zjNum);
		    	getDreamNum(zjType);
		    }else {
		    	set.clear();
		    	set.addAll(redSet);
				zjNum = set.stream()
						.map(num -> {
							return (num < 10) ? "0" + num : String.valueOf(num);
						})
						.collect(Collectors.joining(","));
				set.clear();
			    set.addAll(blueSet);
			    int index = 0;
			    Iterator<Integer> blueIt2 = set.iterator();
			    while(blueIt2.hasNext()){
			    	Integer num = blueIt2.next();
			    	String txt = "";
			    	if(num<10) {
		    			txt = "0"+num;
		    		}else {
		    			txt = num+"";
		    		}
			    	if(index==0) {
			    		zjNum = zjNum+" "+txt;
			    	}else {
			    		zjNum = zjNum+","+txt;
			    	}
					index++;
			    }
		    }

			String a_redNum =  zjNum.split("\\s")[0];
			String a_blueNum =  zjNum.split("\\s")[1];
			// 从第一个字符串中提取数字
			List<Integer> a_redArr = Arrays.stream(a_redNum.split(","))
					.map(Integer::parseInt)
					.collect(Collectors.toList());

			List<Integer> a_blueArr = Arrays.stream(a_blueNum.split(","))
					.map(Integer::parseInt)
					.collect(Collectors.toList());

	    	System.out.println("今晚的中奖号码历史未出现 请查收您的一千万中奖号码^^ "+zjNum);
			//现在开始执行比对生成的号码在历史中奖信息中相似度
			List<String> similarNumber = comparisonNum(a_redArr,a_blueArr,redSize,blueSize,historyData);
			if(similarNumber.size()>0){
				System.out.println("该号码存在相似个数>="+Constants.similarSize+"的历史中奖号码共:"+similarNumber.size()+"注-->");
				similarNumber.forEach(System.out::println);
				System.out.print("请在控制台输入yes/y(需要）或no/n(不需要）来确定是否需要这注号码：");
				Scanner scanner = new Scanner(System.in);
				String input = scanner.nextLine();
				if ("no".equals(input) || "n".equals(input) || "不需要".equals(input)) {
					System.out.println("您输入了no，重新生成号码...");
					getDreamNum(zjType);
				} else if ("yes".equals(input) || "y".equals(input)  || "需要".equals(input)) {
					System.out.println("您输入了yes，不再重新生成号码...");
					//把号码写入历史文件
					WriteNum.writeMyNumber(zjNum);
					comparisonOpenNum(a_redArr,a_blueArr,redSize,blueSize,historyData);
				} else {
					System.out.println("输入无效，请重新输入！");
				}
			}else{
				WriteNum.writeMyNumber(zjNum);
				comparisonOpenNum(a_redArr,a_blueArr,redSize,blueSize,historyData);
			}
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("系统崩溃了……"+e.getMessage());
		}
	}
	/**
	 * 大乐透
	 */
	private static void tcDays() {
		while(redSet.size()==5?false:true){
			int num = ran.nextInt(36); ////1-35 1-12 1 3 6 5+2 大乐透
			if(num == 0) {
				continue;
			}
			redSet.add(num);
		}
		while(blueSet.size()==2?false:true){
			int num = ran.nextInt(13);
			if(num == 0) {
				continue;
			}
			blueSet.add(num);
		}

	}

	/**
	 * 双色球
	 */
	private static void fcDays() {
		while(redSet.size()==6?false:true){//1-33 1-16 2 4 7 6+1 双色球
			int num = ran.nextInt(34);
			if(num == 0) {
				continue;
			}
			redSet.add(num);
		}
		while(blueSet.size()==1?false:true){
			int num = ran.nextInt(17);
			if(num == 0) {
				continue;
			}
			blueSet.add(num);
		}
	}

}
