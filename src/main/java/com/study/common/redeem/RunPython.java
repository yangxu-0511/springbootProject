package com.study.common.redeem;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSONObject;

import java.io.*;
import java.util.ArrayList;
import java.util.List;


public class RunPython {

	private static final String pythonScriptPath = "D:\\idea-workspace\\springbootProject\\zj.py";
	private static final String pythonexe = "C:\\Program Files\\WindowsApps\\PythonSoftwareFoundation.Python.3.10_3.10.3056.0_x64__qbz5n2kfra8p0\\python3.10.exe";
    private static final String ifRunPath = "D:\\项目\\其他\\runPython.json";

	/*
	 * @Author yangxu
	 * @Description 爬虫生成最新的开奖号码
	 * @Param:
	 * @Return: void
	 * @Since create in 2024/1/15 14:11
	 * @Company 广州云趣信息科技有限公司
	 */
	public static void run() {
        try {
            //判断今天有没有执行过Python
            File file = new File(ifRunPath);
            String date = new DreamNumer().getCurrentDate();
            // 创建JSON对象并设置键值对
            JSONObject jsonObject = new JSONObject();
            if(file.exists()) { //文件已经存在就取出来然后重新写入
                jsonObject = new DreamNumer().filterJson(ifRunPath);
                Boolean flag =jsonObject.getBoolean(date);
                if(flag!=null && flag){
                    System.out.println("今天已经执行过python脚本了不再执行脚本。");
                    return ;
                }
            }
            jsonObject.put(date,true);
            // 创建 FileWriter 对象
            FileWriter fileWriter = new FileWriter(ifRunPath);
            // 将 JSON 对象写入文件
            fileWriter.write(jsonObject.toJSONString());
            // 关闭 FileWriter
            fileWriter.close();
            System.out.println("开始启动python脚本...");

            // 创建 ProcessBuilder 对象，设置要执行的命令
            List<String> commandList = new ArrayList<>();
            commandList.add(pythonexe);
            commandList.add(pythonScriptPath);
            ProcessBuilder pb = new ProcessBuilder(commandList);
            
            // 启动进程并等待脚本执行完毕
            Process process = pb.start();
            int exitCode = process.waitFor();
            
            // 获取脚本输出结果
            InputStream inputStream = process.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
            String line;
            while ((line = reader.readLine()) != null) {
                System.out.println(line);
            }
            // 打印脚本执行结果
            if (exitCode == 0) {
                System.out.println("Python script executed successfully.");
            } else {
                System.out.println("Python script execution failed with exit code: " + exitCode);
            }
            
        } catch (IOException | InterruptedException e) {
            System.out.println("执行Python脚本出错了……-->"+e.getMessage());
        }
		
	}

}
