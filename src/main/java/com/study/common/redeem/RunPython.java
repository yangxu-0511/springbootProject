package com.study.common.redeem;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;


public class RunPython {

	private static String pythonScriptPath = "D:\\workspace\\DreamNumer\\zj.py";
	private static String pythonexe = "C:\\Program Files\\WindowsApps\\PythonSoftwareFoundation.Python.3.10_3.10.3056.0_x64__qbz5n2kfra8p0\\python3.10.exe";

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
