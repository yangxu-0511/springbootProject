package com.study.common.utils;

import java.io.DataInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.study.common.listener.ExcelListener;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.ExcelReader;
import com.alibaba.excel.metadata.data.ReadCellData;
import com.alibaba.excel.read.builder.ExcelReaderBuilder;
import com.alibaba.excel.read.metadata.ReadSheet;
import com.alibaba.excel.util.DateUtils;
import org.apache.commons.lang3.time.DateFormatUtils;


public class ExcelUtils {

    public static final String SPECTYPESIGN = "#@#";
    // 字母
    private static final String MATCH_LETTERS = "[a-zA-Z]+";
    // 匹配字母开头
    private static final String LETTER_START_REGEX = "^[a-zA-Z]*$";
    // 提取单元格的行数
    private static final String ROW_REGEX = "[\\d]+";
    // 匹配中文
    private static final String CHINESETYPE_REGEX = ".*[\\u4e00-\\u9fa5].*";
    // 单元格数值类型
    private static final String NUMBERTYPE_REGEX = "[0|.]+";
    // 匹配除了,.数字之前外的所有符号,0_ 代表整数
    private static final String EXCLUDE_SPECIFIC_REGEX = ".*[^,.#\\d].*";
    private static final String CELL_REGEX = "[A-Z]+[1-9][\\d]*";

    private static final Pattern date_ptrn1 = Pattern.compile("^\\[\\$\\-.*?\\]");
    private static final Pattern date_ptrn2 = Pattern.compile("^\\[[a-zA-Z]+\\]");
    private static final Pattern date_ptrn4 = Pattern.compile("^\\[([hH]+|[mM]+|[sS]+)\\]");
    private static final Pattern date_ptrn5 = Pattern.compile("^\\[DBNum(1|2|3)\\]");

    /**
     * 获取excel文件所有的sheet页名称
     *
     * @param ins
     * @return
     */
    public static List<String> getSheetList(InputStream ins) {
        List<String> resList = new ArrayList<>();
        ExcelReaderBuilder excelReaderBuilder = EasyExcel.read(ins);
        ExcelReader excelReader = excelReaderBuilder.build();
        List<ReadSheet> sheetList = excelReader.excelExecutor().sheetList();
        sheetList.forEach(e -> resList.add(e.getSheetName()));
        return resList;
    }
    /**
     * 获取sheet种的内容，前rowNum行，null则查询全部
     * @param ins
     * @param sheetName
     * @param rowNum
     * @return
     */
    public static List<List<String>> getSheetData(InputStream ins, String sheetName, Integer rowNum) {
        if (rowNum != null && rowNum == 0){
            return null;
        }
        InputStream is = null;
        is = new DataInputStream(ins);
        ExcelListener listen = new ExcelListener();
        // 设置读取的行数
        listen.setEndRowNum(rowNum);

        ExcelReaderBuilder read = EasyExcel.read(is, listen);
        read.sheet(sheetName).doRead();
        // 读取sheet中最大的列数
        int maxColumnNum = listen.getMaxColumnNum();
        // 第一行数据(也就是表头数据)，下标从0开始
        Map<Integer, String> headMap = listen.getHeadMap();
        // 其余数据，下标从0开始
        List<Map<Integer, String>> valList = listen.getValList();

        // 为还原excel原始样式，以最大列数为约束，遍历headMap、valList，获取不到的数据以空填充
        // 如果rowNum不为空，开始填充数据
        rowNum = rowNum == null?listen.getMaxRowNum():rowNum;
        List<List<String>> resList = new ArrayList<List<String>>();
        for (int i = 0; i < rowNum; i++) {
            List<String> list = new ArrayList<>();
            for (int j = 0; j < maxColumnNum; j++) {
                if (i == 0){
                    // 如果不存在默认返回空
                    list.add(headMap.getOrDefault(j,""));
                }else {
                    list.add(valList.get(i-1).getOrDefault(j,""));
                }
            }
            resList.add(list);
        }
        return resList;
    }

    /**
     * 部分格式数据处理
     * @param cell
     * @param formatVal
     * @return
     */
    public static String getOtherDateFormat(ReadCellData cell, String formatVal){

        String newFormatStr = MyExcelTypeEnum.getFormatType(formatVal);

        if (cell.getDataFormatData().getIndex() == 22) {// excel显示格式为：2012/1/20 23:00
            return DateFormatUtils.format(doubleToDate(cell.getNumberValue().doubleValue()), "yyyy/M/d H:mm");
        } else if (cell.getDataFormatData().getIndex() == 30) {
            return DateFormatUtils.format(doubleToDate(cell.getNumberValue().doubleValue()), "M/d/yy");
        }
        if (StringUtils.isEmpty(newFormatStr)){
            return null;
        }
        String dateStr = DateFormatUtils.format(doubleToDate(cell.getNumberValue().doubleValue()),newFormatStr);

        if (StringUtils.contains(dateStr,SPECTYPESIGN)){
            // 二〇〇五年一月十五日
            return convertNumberToChineseDate(dateStr,SPECTYPESIGN);
        } else if (StringUtils.contains(newFormatStr," aa")) {
            // 2011/1/3 6:00 AM
            return DateFormatUtils.format(doubleToDate(cell.getNumberValue().doubleValue()), newFormatStr, Locale.ENGLISH);
        } else if (StringUtils.contains(newFormatStr,"MMM")) {
            // J 、J-23
            return getEnglishDate(cell.getNumberValue().doubleValue(),newFormatStr);
        }
        return dateStr;
    }


    /**
     * 将yyyy年-MM月-dd日 格式日期转换成中文格式
     * 例：2000-1-1 --> 二〇〇〇年一月一日
     */
    public static String convertNumberToChineseDate(String date,String splitStr) {
        if (date == null || "".equals(date)) {
            return date;
        }

        try {
            String[] dates = date.split(splitStr);
            StringBuilder chineseDateSbu = new StringBuilder();
            for (int i = 0; i < dates.length; i++) {
                chineseDateSbu.append(formatDigit(dates[i]));
            }

            return chineseDateSbu.toString();
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 截取后的年月日转换为中文
     * 例1：2000 --> 二〇〇〇
     * 例1：10 --> 十
     */
    public static String formatDigit(String sign) {
        if (sign == null || "".equals(sign)) {
            return null;
        }

        char[] signChars = sign.toCharArray();
        StringBuilder chinese = new StringBuilder();
        if (signChars.length > 4 || signChars.length < 3) {
            for (char c : signChars) {
                chinese.append(MyChineseNumberEnum.getValue(String.valueOf(c)));
            }
        } else {
            if (sign.startsWith("0")){
                for (char c : signChars) {
                    chinese.append(MyChineseNumberEnum.getValue(String.valueOf(c)));
                }
            }else {
                if (sign.equals(MyChineseNumberEnum.getValue(sign))){
                    String subStr = sign.substring(0,sign.length()-1);
                    String unitStr = sign.substring(sign.length()-1);
                    chinese.append(MyChineseNumberEnum.getValue(subStr)+unitStr);
                }else {
                    chinese.append(MyChineseNumberEnum.getValue(sign));
                }
            }
        }

        return chinese.toString();
    }

    public static String getCellType(ReadCellData cell, String cellVal){
        if (isStringTypeFromCell(cell,cellVal)){
            return "STRING";
        }
        switch (cell.getType()) {
            case NUMBER:
                String formatVal = cell.getDataFormatData().getFormat();
                // 将excel的类型处理成java可以识别的类型
                String newFormat = MyExcelTypeEnum.getFormatType(formatVal);
                if (newFormat == null || "".equals(newFormat)){
                    newFormat = formatJavaType(formatVal);
                }

                if (DateUtils.isADateFormat(cell.getDataFormatData().getIndex(),formatVal)) {
                    // 将数据格式化
                    String originVal = getOtherDateFormat(cell,formatVal);
                    if (!StringUtils.isEmpty(formatVal)
                            && (isChineseType(newFormat) || isChineseType(originVal) )) {
                        return "STRING";
                    }
                    // 暂时简单处理，如果是包含h:mm:ss 默认按照时间格式处理，剩下默认按照时间戳处理
                    if (StringUtils.contains(formatVal, "mm:ss")) {
                        return "TIMESTAMP";
                    } else {
                        return "DATE";
                    }
                } else if (StringUtils.equalsIgnoreCase("General",newFormat)) {
                    return "STRING";
                } else {
                    // 自定义的事件类型
                    String otherDateType = getOtherDateType(cell.getDataFormatData().getIndex());
                    if (!StringUtils.isEmpty(otherDateType)){
                        return otherDateType;
                    }
                    if (!StringUtils.isEmpty(newFormat) && newFormat.matches(NUMBERTYPE_REGEX)) {
                        if (StringUtils.equals("0", newFormat) || !newFormat.contains(".")) {
                            return "NUMBER";
                        } else {
                            return "DOUBLE";
                        }
                    } else if (!StringUtils.isEmpty(newFormat) && newFormat.matches(EXCLUDE_SPECIFIC_REGEX)) {
                        // 解决货币之类的数据类型判断如 ￥14
                        return "STRING";
                    } else {
                        return "DOUBLE";
                    }
                }
            case STRING:
                // 首行如果是表头汉字，全部都是string
                String format = cell.getDataFormatData().getFormat();
                if (StringUtils.equalsIgnoreCase("General", format)) {
                    return "STRING";
                } else if (!StringUtils.isEmpty(format) && format.matches(NUMBERTYPE_REGEX)) {
                    if (StringUtils.equals("0", format)) {
                        return "NUMBER";
                    } else {
                        return "DOUBLE";
                    }
                } else if (isChineseType(MyExcelTypeEnum.getFormatType((format)))) {
                    return "STRING";
                } else if (StringUtils.containsIgnoreCase(format, "mm:ss")) {
                    return "TIMESTAMP";
                } else if (StringUtils.containsIgnoreCase(format, "yyyy")
                        || StringUtils.containsIgnoreCase(format, "mm")
                        || StringUtils.containsIgnoreCase(format, "dd")) {
                    return "DATE";
                } else {
                    return "STRING";
                }
            case BOOLEAN:
                return "BOOLEAN";
            default:
                return "STRING";
        }
    }

    /**
     * 用于解析excel表头及类型
     * @param ins
     * @param sheetName
     * @param startCell 起始单元格 默认A1
     * @param endCell 结束单元格
     * @return
     */
    public static List<Map<String,String>> getSheetColumnNameAndType(InputStream ins, String sheetName, String startCell, String endCell){
        List<Map<String,String>> resList = new ArrayList<>();
        // 提起起始行数列数，结束行数列数

        Integer startColumnNum = excelCellToColumnNum(startCell);
        Integer startRowNum = excelCellToRowNum(startCell);
        Integer endColumnNum = excelCellToColumnNum(endCell);
        Integer endRowNum = excelCellToColumnNum(endCell);

        InputStream is = null;
        is = new DataInputStream(ins);
        ExcelListener listen = new ExcelListener();
        // 获取字段名称和类型
        listen.setNameAndTypeFlag(true);
        // 设置读取的列片区
        listen.setStartColumnNum(startColumnNum);
        listen.setEndColumnNum(endColumnNum);
        // 设置读取的行片区
        listen.setStartRowNum(startRowNum);
        listen.setEndRowNum(endRowNum);

        ExcelReaderBuilder read = EasyExcel.read(is, listen);
        read.sheet(sheetName).doRead();

        // 如果endColumnNum==null则无结束单元格，maxColumn就是excel最大的列，反之则取endColumnNum
        Integer maxColumn = endColumnNum != null ? endColumnNum : listen.getMaxColumnNum();

        // 组装首行内容 （受到其实单元格及结束单元格约束） headMap从0开始
        // 第一行数据(也就是表头数据)，下标从0开始，startRowNum默认1（A1）
        Map<String, String> headMap = listen.getHeadValAndTypeMap();
        // 第二行数据，下标从0开始。包含内容及类型
        List<Map<String, String>>  valList = listen.getContentValAndTypeList();

        Map<String, String> resHeadMap = null;
        String type = "STRING";
        for (int i = startColumnNum-1; i < maxColumn; i++) {
            resHeadMap = new HashMap<>();
            if (startRowNum==1){
                // 起始行数是1 head就会有数据，且是需要的数据
                if (valList.size()>0 && !StringUtils.isEmpty(valList.get(0).get(i+"_type"))){
                    type = valList.get(0).get(i+"_type");
                } else if (!StringUtils.isEmpty(headMap.get(i+"_type"))) {
                    type = headMap.get(i+"_type");
                }
                resHeadMap.put("columnName",headMap.getOrDefault(String.valueOf(i),""));
                resHeadMap.put("columnType",type);
                resList.add(resHeadMap);
            }else if(startRowNum>1 && valList.size()>0){
                // 起始行数是1 head中的数据并不是需要的数据，则从valList取 0 和 1 分别做 头和内容
                if (valList.size()>1 && valList.get(1)!=null && !StringUtils.isEmpty(valList.get(1).get(i+"_type"))){
                    type = valList.get(1).get(i+"_type");
                } else if (!StringUtils.isEmpty(valList.get(0).get(i+"_type"))) {
                    type = valList.get(0).get(i+"_type");
                }
                resHeadMap.put("columnName",valList.get(0).getOrDefault(String.valueOf(i),""));
                resHeadMap.put("columnType",type);
                resList.add(resHeadMap);
            }

        }

        return resList;
    }

    public static String checkExcelCellString(String startCell, String endCell) {

        // 起始单元格校验
        if (!StringUtils.isEmpty(startCell)) {
            if (!checkExcelCellSpecs(startCell)) {
                // 起始单元格不符合规范
                return "起始单元格不符合规范";
            }
        } else {
            // 起始单元格不得为空
            return "起始单元格不得为空";
        }

        // 结束单元格但如果不为空，则需要合法校验
        if (!StringUtils.isEmpty(endCell)) {
            if (!checkExcelCellSpecs(endCell)) {
                // 结束单元格不符合规范
                return "结束单元格不符合规范";
            }
        }

        // 单元格全部合法后，进行逻辑约束校验，起始单元格后的数字要小于等于结束单元格后缀
        // 列数校验 结束列数 >= 起始列数
        Integer startColumnNum = excelCellToColumnNum(startCell);
        Integer endColumnNum = excelCellToColumnNum(endCell);

        // 行数校验 结束行数 >= 起始行数
        Integer startRowNum = excelCellToRowNum(startCell);
        Integer endRowNum = excelCellToRowNum(endCell);

        if (endColumnNum != null) {
            if (startColumnNum > endColumnNum){
                return "起始单元格列必须小于等于结束起始单元格列";
            }else if (startRowNum > endRowNum){
                return "起始单元格行必须小于等于结束起始单元格行";
            }
        }
        return "";
    }

    /**
     * 检验单元格合法性
     * @param excelCell
     * @return
     */
    private static boolean checkExcelCellSpecs(String excelCell) {

        return !StringUtils.isEmpty(excelCell) && excelCell.matches(CELL_REGEX);
    }

    /*
     * 提取单元格字母对应列,如果colStr==null，则返回null
     * A1 -> 1、B1 -> 2、C1 -> 3
     */
    public static Integer excelCellToColumnNum(String colStr) {

        Integer result = null;
        if (!StringUtils.isEmpty(colStr)) {
            result = 0;
            int length = colStr.length();
            int j = 0;
            int num = 0;
            for (int i = 0; i < length; i++) {
                char ch = colStr.charAt(length - i - 1);
                if (String.valueOf(ch).matches(LETTER_START_REGEX)) {
                    num = ch - 'A' + 1;
                    num *= Math.pow(26, j);
                    j++;
                    result += num;
                }
            }
        }
        return result;
    }

    /**
     * 提取单元格对应行返回值：Integer
     * 提取单元格字母对应行,如果colStr==null，则返回null
     * A1 -> 1、B2 -> 2、C3 -> 3
     */
    public static Integer excelCellToRowNum(String colStr) {
        String numStr = excelCellToRowString(colStr);
        if (StringUtils.isEmpty(numStr)) {
            return null;
        }
        return Integer.parseInt(numStr);
    }
    /**
     * 提取单元格对应行返回值：string
     * @param colStr
     * @return
     */
    public static String excelCellToRowString(String colStr) {
        String res = null;
        if (!StringUtils.isEmpty(colStr)) {
            Matcher matcher = Pattern.compile(ROW_REGEX).matcher(colStr);
            if (matcher.find()) {
                res = matcher.group();
            }
        }
        return res;
    }

    /**
     * 英文缩写的日期处理
     * @param numericCellValue
     * @param formatVal
     * @return
     */
    private static String getEnglishDate(double numericCellValue, String formatVal){
        String dateStr = DateFormatUtils.format(doubleToDate(numericCellValue), formatVal, Locale.ENGLISH);
        if (StringUtils.equals(formatVal,"MMMMM")){
            // excel显示格式为：F
            return StringUtils.substring(dateStr,0,1);
        } else if (StringUtils.equals(formatVal,"MMMMM-yy")) {
            // excel显示格式为：F-23
            String letters = null;
            Matcher matcher = Pattern.compile(MATCH_LETTERS).matcher(dateStr);
            if (matcher.find()) {
                letters = matcher.group();
            }
            if (!StringUtils.isEmpty(letters) && !StringUtils.isEmpty(dateStr)){
                return StringUtils.replace(dateStr,letters,StringUtils.substring(dateStr,0,1));
            }
            return dateStr;
        }
        return dateStr;
    }

    private static boolean isStringTypeFromCell(ReadCellData cell, String cellValue){
        // 如果数据中含有中文，则直接返回string格式
        if (!StringUtils.isEmpty(cellValue) && cellValue.matches(CHINESETYPE_REGEX)){
            return true;
        }
        String newFormatStr = MyExcelTypeEnum.getFormatKey(cell.getDataFormatData().getFormat());
        if (StringUtils.containsIgnoreCase(newFormatStr,"EN") || StringUtils.containsIgnoreCase(cellValue,"AM")
                || StringUtils.containsIgnoreCase(cellValue,"PM")){
            return true;
        }
        short shortNum = cell.getDataFormatData().getIndex();


        switch (shortNum){
            case 46:
                // 1184426:00:00
                return true;
        }
        return false;
    }

    private static boolean isChineseType(String param){
        if (!StringUtils.isEmpty(param)){
            if(param.matches(CHINESETYPE_REGEX)
                    || param.contains("E")
                    || param.contains("MMMM")){
                return true;
            }
        }
        return false;
    }

    private static String getOtherDateType(short shortNum){
        switch (shortNum) {
            case 14:
            case 30:
                return "DATE";
            case 31:
            case 57:
                return "TIMESTAMP";
        }
        return null;
    }


    private static String formatJavaType(String formatVal){
        String fs = formatVal;
        int length = formatVal.length();
        StringBuilder sb = new StringBuilder(length);

        int separatorIndex;
        for(separatorIndex = 0; separatorIndex < length; ++separatorIndex) {
            char c = fs.charAt(separatorIndex);
            if (separatorIndex < length - 1) {
                char nc = fs.charAt(separatorIndex + 1);
                if (c == '\\') {
                    switch (nc) {
                        case ' ':
                        case ',':
                        case '-':
                        case '.':
                        case '\\':
                            continue;
                    }
                } else if (c == ';' && nc == '@') {
                    ++separatorIndex;
                    continue;
                }
            }

            sb.append(c);
        }

        fs = sb.toString();
        // excel设置单元格格式 使用数值
        fs = StringUtils.replace(fs,"0_ ","0");
        fs = StringUtils.replace(fs,"0_)","0");

        if (date_ptrn4.matcher(fs).matches()) {
            return fs;
        } else {
            fs = date_ptrn5.matcher(fs).replaceAll("");
            fs = date_ptrn1.matcher(fs).replaceAll("");
            fs = date_ptrn2.matcher(fs).replaceAll("");
            separatorIndex = fs.indexOf(59);
            if (0 < separatorIndex && separatorIndex < fs.length() - 1) {
                fs = fs.substring(0, separatorIndex);
            }
            return fs;
        }
    }

    private static Date doubleToDate(Double date){
        Calendar base = Calendar.getInstance();
        base.set(1899, 11, 30, 0, 0, 0);
        base.add(Calendar.DATE, date.intValue());
        base.add(Calendar.MILLISECOND,(int)((date % 1) * 24 * 60 * 60 * 1000));
        return base.getTime();
    }



}

