package com.study.common.redeem;/*
  @Author yangx
 * @Description 写号码
 * @Since create in 2024-4-24 11:27:34
 * @Company 广州云趣信息科技有限公司
 */

import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSONObject;
import com.study.common.base.AppBaseNum;
import com.study.common.base.Constants;
import com.study.common.utils.DateUtils;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

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

}
