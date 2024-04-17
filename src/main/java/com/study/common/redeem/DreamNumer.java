package com.study.common.redeem;

import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.study.common.utils.DateUtils;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

public class DreamNumer {

	private static final Random ran = new Random();
	private static final Set <Integer> redSet = new HashSet<>();
	private static final Set <Integer> blueSet = new HashSet<>();
	private static final String hisFilePath = "D:\\idea-workspace\\springbootProject\\history.json";
	private static final String tcFilePath  = "D:\\idea-workspace\\springbootProject\\dlt.json";
	private static final String fcFilePath  = "D:\\idea-workspace\\springbootProject\\ssq.json";
	private static final int similarSize = 5; //定义相似度个数
	private static final int sameRedSize = 5; //定义红球命中个数
	private static final int sameHisSize = 3; //历史命中数
	private static Map<String, String> tcMap = new HashMap<>();
	private static Map<String, String> fcMap = new HashMap<>();
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


	public static void getDreamNum() {
		//1-35 1-12 1 3 6 5+2 大乐透
		//1-33 1-16 2 4 7 6+1 双色球
		String zjType = getZjType();
		getDreamNum(zjType);
	}

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
		String jsonFilePath = "";
		if("tc".equals(zjType)) {
			jsonFilePath = tcFilePath;
		}else if("fc".equals(zjType)) {
			jsonFilePath = fcFilePath;
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
					String txt = (num < 10) ? "0" + num : String.valueOf(num);
					return txt;
				})
				.collect(Collectors.joining("|"));
		set.clear();
		set.addAll(blueSet);
		zjNum = zjNum +"|"+ set.stream()
					.map(num -> {
						String txt = (num < 10) ? "0" + num : String.valueOf(num);
						return txt;
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
							String txt = (num < 10) ? "0" + num : String.valueOf(num);
							return txt;
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
	    	System.out.println("今晚的中奖号码历史未出现 请查收您的一千万中奖号码^^ "+zjNum);
			//现在开始执行比对生成的号码在历史中奖信息中相似度
			List<String> similarNumber = comparisonNum(zjNum, zjType);
			if(similarNumber.size()>0){
				System.out.println("但是该号码存在相似度高的历史中奖号码共:"+similarNumber.size()+"注-->");
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
					writeMyNumber(zjNum);
				} else {
					System.out.println("输入无效，请重新输入！");
				}
			}else{
				writeMyNumber(zjNum);
			}
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("系统崩溃了……"+e.getMessage());
		}
	}


	/**
	 * @Author yangxu
	 * @Description 开始比对生成的号码在历史中奖信息中相似度
	 * @Param: [zjNum]
	 * @Return: void
	 * @Since create in 2024/1/17 14:08
	 * @Company 广州云趣信息科技有限公司
	 * @return
	 */
	private static List<String> comparisonNum(String zjNum,  String zjType) {
		String jsonFilePath = "";
		int redSize = 0;
		int blueSize = 0;
		if("tc".equals(zjType)) {
			jsonFilePath = tcFilePath;
			redSize = 5;
			blueSize = 2;
		}else if("fc".equals(zjType)) {
			jsonFilePath = fcFilePath;
			redSize = 6;
			blueSize = 1;
		}else {
			System.out.println("无效类型！");
			return null;
		}

		JSONObject openData = filterJson(jsonFilePath);
		String a_redNum =  zjNum.split("\\s")[0];
		String a_blueNum =  zjNum.split("\\s")[1];
		List<String> similarNumber = new ArrayList<>();

		// 从第一个字符串中提取数字
		List<Integer> a_redArr = Arrays.stream(a_redNum.split(","))
								.map(Integer::parseInt)
								.collect(Collectors.toList());

		List<Integer> a_blueArr = Arrays.stream(a_blueNum.split(","))
				.map(Integer::parseInt)
				.collect(Collectors.toList());

		//跟所有的公开数据对比
		for (String key : openData.keySet()) {
			int count = 0;
			String data = openData.getString(key);
			String redNum = Arrays.stream(data.split("\\|"))
					.limit(redSize)
					.collect(Collectors.joining("|"));
			List<Integer> redArr = Arrays.stream(redNum.split("\\|"))
					.map(Integer::parseInt)
					.collect(Collectors.toList());
			for (Integer num : a_redArr) {
				if (redArr.contains(num)) {
					count++;
				}
			}
			String blueNum = Arrays.stream(data.split("\\|"))
					.skip(Math.max(0, data.split("\\|").length - blueSize))
					.collect(Collectors.joining("|"));
			List<Integer> blueArr = Arrays.stream(blueNum.split("\\|"))
					.map(Integer::parseInt)
					.collect(Collectors.toList());
			for (Integer num : a_blueArr) {
				if (blueArr.contains(num)) {
					count++;
				}
			}
			if(count>=similarSize){
				similarNumber.add(data);
			}
		}
		return similarNumber;
	}


	/**
	 * @Author yangxu
	 * @Description 统计开奖号码历史重复数
	 * @Param: [redArr, blueArr, redSize, blueSize,openData]
	 * @Return: void
	 * @Since create in 2024/4/15 15:42
	 * @Company 广州云趣信息科技有限公司
	 */
	public static void comparisonOpenNum(List<Integer> redArr, List<Integer> blueArr, int redSize, int blueSize, JSONObject openData) {
		//跟所有的公开数据对比
		for (String key : openData.keySet()) {
			String openNumber = openData.getString(key);
			int redCount = 0;
			int blueCount = 0;
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
			for (Integer num : openRedArr) {
				if (redArr.contains(num)) {
					redCount++;
				}
			}
			for (Integer num : openBlueArr) {
				if (blueArr.contains(num)) {
					blueCount++;
				}
			}
			if(redCount+blueCount == 7){
				continue;
			}
			if(redCount+blueCount > sameHisSize){
				System.out.println("该号码在历史中奖信息中总重复数为:"+(redCount+blueCount)+" 其中重复"+redCount+"个红球和"+blueCount+"个蓝球---->"+openNumber);
			}
		}
	}



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
    	String date = "";
    	if(type==2) {
    		date = DateUtils.getYesterdayDate();
    	}else {
    		date = DateUtils.getCurrentDate();
    	}
		//判断号码是否存在
		File file = new File(hisFilePath);
		// 创建JSON对象并设置键值对
		JSONObject jsonObject = new JSONObject();
		if(file.exists()) { //文件已经存在就取出来然后重新写入
			jsonObject = filterJson(hisFilePath);
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
            FileWriter fileWriter = new FileWriter(hisFilePath);
            // 将 JSON 对象写入文件
            fileWriter.write(jsonObject.toJSONString());
            // 关闭 FileWriter
            fileWriter.close();
            System.out.println("号码已成功写入历史号码文件。");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

	/**
	 * @Author yangxu
	 * @Description 解析json数据
	 * @Param: [jsonFilePath]
	 * @Return: com.alibaba.fastjson.JSONObject
	 * @Since create in 2024/1/15 11:15
	 * @Company 广州云趣信息科技有限公司
	 */
	public static JSONObject filterJson(String jsonFilePath) {
		Path path = Paths.get(jsonFilePath);
		byte[] jsonData;
		try {
			jsonData = Files.readAllBytes(path);
			String jsonString = new String(jsonData);
			// 使用Jackson库解析JSON
			ObjectMapper objectMapper = new ObjectMapper();
			JsonNode jsonNode = objectMapper.readTree(jsonString);
			return JSONObject.parseObject(jsonNode.toString());
		}catch (Exception e) {
			System.out.println("系统崩溃了……"+e.getMessage());
			return null;
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
			filePath = tcFilePath;
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
			filePath = fcFilePath;
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
		JSONObject hisJson = filterJson(hisFilePath);
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
						if (tcMap.containsKey(key)) {
							System.out.println(tcMap.get(key));
						}
					}else{
						if (fcMap.containsKey(key)) {
							System.out.println(fcMap.get(key));
						}
					}
				}
			}
		}else{
			System.out.println("当天尚未购彩----");
		}
		System.out.println("当天号码对比结束---->end");
		System.out.println("开始统计历史购彩记录有无中奖信息（只统计红球>="+sameRedSize+")--->start");
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

					if(redCount>=sameRedSize){ //小奖不统计
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
			if (tcMap.containsKey(key)) {
				System.out.println("你曾经在"+date+"购买的这注彩票 目前已经出奖！-->"+hisNum);
				System.out.println(tcMap.get(key));
			}
		}else{
			if (fcMap.containsKey(key)) {
				System.out.println("你曾经在"+date+"购买的这注彩票 目前已经出奖！-->"+hisNum);
				System.out.println(fcMap.get(key));
			}
		}
	}
}
