package com.study.common.redeem;

import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

public class DreamNumer {

	private static final Random ran = new Random();
	private static final Set <Integer> redSet = new HashSet<>();
	private static final Set <Integer> blueSet = new HashSet<>();
	private static final String hisFilePath = "D:\\项目\\其他\\history.json";
	private static final String tcFilePath = "D:\\idea-workspace\\springbootProject\\dlt.json";
	private static final String fcFilePath = "D:\\idea-workspace\\springbootProject\\ssq.json";
	private static final int similarSize = 5; //定义相似度个数
	private static final int sameSize = 3; //定义兑奖相似个数



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
		zjNum = set.stream()
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
				} else {
					System.out.println("输入无效，请重新输入！");
				}
			}
			//把号码写入历史文件
			writeMyNumber(zjNum);
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
		if("tc".equals(zjType)) {
			jsonFilePath = tcFilePath;
		}else if("fc".equals(zjType)) {
			jsonFilePath = fcFilePath;
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

		List<Integer> a_blueArr = new ArrayList<>();
		if("tc".equals(zjType)){
			 a_blueArr = Arrays.stream(a_blueNum.split(","))
					.map(Integer::parseInt)
					.collect(Collectors.toList());
		}
		//跟所有的公开数据对比
		for (String key : openData.keySet()) {
			int count = 0;
			String data = openData.getString(key);
			String redNum = Arrays.stream(data.split("\\|"))
					.limit(5)
					.collect(Collectors.joining("|"));
			List<Integer> redArr = Arrays.stream(redNum.split("\\|"))
					.map(Integer::parseInt)
					.collect(Collectors.toList());

			String blueNum = "";
			for (Integer num : a_redArr) {
				if (redArr.contains(num)) {
					count++;
				}
			}
			if("tc".equals(zjType)){
				// 从倒数第二个字符串中提取数字
				blueNum = Arrays.stream(data.split("\\|"))
						.skip(Math.max(0, data.split("\\|").length - 2))
						.collect(Collectors.joining("|"));
				List<Integer> blueArr = Arrays.stream(blueNum.split("\\|"))
						.map(Integer::parseInt)
						.collect(Collectors.toList());
				for (Integer num : a_blueArr) {
					if (blueArr.contains(num)) {
						count++;
					}
				}
			}else{
				// 从倒数第一个字符串中提取数字
				blueNum = Arrays.stream(data.split("\\|"))
						.skip(Math.max(0, data.split("\\|").length - 1))
						.collect(Collectors.joining("|"));
				if(a_blueNum.equals(blueNum)){
					count++;
				}
			}
			if(count>=similarSize){
				similarNumber.add(data);
			}
		}
		return similarNumber;
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
    		date = getYesterdayDate();
    	}else {
    		date = getCurrentDate();
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

	// 获取当前日期并格式化为 "yyyy-MM-dd" 格式
	static String getCurrentDate() {
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
		Date currentDate = new Date();
		return dateFormat.format(currentDate);
	}
	/**
	 * @Author yangxu
	 * @Description 获取昨天的日期
	 * @Param:
	 * @Return: java.lang.String
	 * @Since create in 2024/2/20 9:11
	 * @Company 广州云趣信息科技有限公司
	 */
	private static String getYesterdayDate() {
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
		Calendar calendar = Calendar.getInstance();
		Date currentDate = new Date();
		calendar.setTime(currentDate);
		calendar.add(Calendar.DAY_OF_YEAR, -1);
		Date yesterdayDate = calendar.getTime();
		return dateFormat.format(yesterdayDate);
	}

	/**
	 * @Author yangxu
	 * @Description 号码比对 默认T-1 1.自动兑奖 2.手动传参兑奖
	 * @Param: [params]
	 * @Return: void
	 * @Since create in 2024/1/22 13:57
	 * @Company 广州云趣信息科技有限公司
	 */
	public static void redeem(String params) {
		System.out.println("开始执行Python脚本");
		RunPython.run();

		String date = getYesterdayDate(); //昨天的日期
		Calendar calendar=Calendar.getInstance();
		String filePath =  "";
		String zjType = "";
		int currentDay = calendar.get(Calendar.DAY_OF_WEEK)-1;
		if(currentDay==1  || currentDay==3 || currentDay==5 || currentDay==6) { //T-1 要换一下体彩福彩顺序
			filePath = fcFilePath;
			zjType = "fc";
		}
		if(currentDay==2  || currentDay==4 || currentDay==0) {
			filePath = tcFilePath;
			zjType = "tc";
		}
		//1.取出昨天的出奖号码
		String openNumber = filterJson(filePath).getString(date);
		if(StrUtil.isEmpty(openNumber)){
			System.out.println("未获取到昨天的中奖号，执行失败……");
			return ;
		}
		if(StrUtil.isEmpty(params)){ //
			//自动兑奖
			//1.获取昨天获取的号码
			JSONObject hisJson = filterJson(hisFilePath);
			String yesterdayNum  = hisJson.getString(date); // "07,12,20,21,31 06,12|04,06,14,29,34 01,09"
			if(StrUtil.isNotEmpty(yesterdayNum)){


			}else System.out.println("未获取到昨天的中奖号，开始对比历史购入号码……");


		}else{
			//手动兑奖 要将号码写到昨天的json文件中
		}

	}
}
