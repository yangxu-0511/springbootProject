package com.study.common.listener;
/**
 * @Author yangx
 * @Description 描述
 * @Since create in 11
 * @Company 广州云趣信息科技有限公司
 * 11
 */

import com.alibaba.excel.enums.CellDataTypeEnum;
import com.alibaba.excel.event.AnalysisEventListener;

import java.util.*;

import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.event.AnalysisEventListener;
import com.alibaba.excel.metadata.Cell;
import com.alibaba.excel.metadata.data.ReadCellData;
import com.study.common.utils.ExcelUtils;

/**
 * @author yangxu
 * @create 2023/10/24 15:22
 */
public class ExcelListener extends AnalysisEventListener<Map<Integer, String>> {
    private Map<Integer, String> headMap = new HashMap<>();
    private List<Map<Integer, String>> valList = new ArrayList<>();
    private Map<String, String> headValAndTypeMap = new HashMap<>();
    private List<Map<String, String>> contentValAndTypeList = new ArrayList<>();
    private boolean nameAndTypeFlag;
    private Integer startColumnNum;
    private Integer startRowNum;
    private Integer endColumnNum;
    private Integer endRowNum;
    // sheet中最大的列号,默认为0,不可为null
    private int maxColumnNum;
    // sheet中行数，默认为1，因为从第二行开始读取
    private int maxRowNum = 1;

    @Override
    public void invoke(Map<Integer, String> integerStringMap, AnalysisContext analysisContext) {
        if (integerStringMap == null || integerStringMap.size()==0){
            return;
        }
        // 获取当前行数analysisContext.readRowHolder().getRowIndex() 从1开始(0行进的head方法)
        Integer currentRowNum = analysisContext.readRowHolder().getRowIndex();
        if(currentRowNum<startRowNum-1){
            return;
        }
        // 扫描excel全部内容获取此excel最大列数
        maxColumnNum = Math.max(maxColumnNum,integerStringMap.size());
        // 最大行数
        maxRowNum++;

        // 起始列，Map中小于startColumnNum-1都不需要
        integerStringMap.entrySet().removeIf(entry -> entry.getKey() < startColumnNum-1);

        // 移除值为null的数据
        integerStringMap.entrySet().removeIf(entry -> entry.getValue() == null);

        // 格式化单元格中的数据
        formatExcelValByCellType(integerStringMap,analysisContext.readRowHolder().getCellMap());
        // 本方法从excel第二行开始读,为防止起始不是excel第一行，多读入一行数据
        if (nameAndTypeFlag && (contentValAndTypeList.size() == 0 || contentValAndTypeList.size() == 1)){
            // 如果获取名称和类型,只获取一行数据
            contentValAndTypeList.add(getCellType(integerStringMap,analysisContext.readRowHolder().getCellMap()));
        } else if (endRowNum == null){
            // 未设置结束单元格无结束行数，则读取全部
            valList.add(integerStringMap);
        } else if (valList.size() < endRowNum-1){
            valList.add(integerStringMap);
        }

    }

    @Override
    public void invokeHead(Map<Integer, ReadCellData<?>> headMap, AnalysisContext context) {
        Set<Map.Entry<Integer, ReadCellData<?>>> entrieSet = headMap.entrySet();
        for (Map.Entry<Integer, ReadCellData<?>> entry : entrieSet) {
            String val = entry.getValue().getType()== CellDataTypeEnum.EMPTY?"":entry.getValue().getStringValue();
            this.headMap.put(entry.getKey(),val);
        }
        if (startRowNum==1 && nameAndTypeFlag && headValAndTypeMap.size() == 0){
            // 如果获取名称和类型,只获取一行数据
            headValAndTypeMap = getCellType(this.headMap,context.readRowHolder().getCellMap());
        }
    }

    @Override
    public void doAfterAllAnalysed(AnalysisContext analysisContext) {

    }

    private void formatExcelValByCellType(Map<Integer, String> integerStringMap, Map<Integer, Cell> cellMap){
        for (Integer key : integerStringMap.keySet()) {
            ReadCellData cell = (ReadCellData) cellMap.get(key);
            String newVal = ExcelUtils.getOtherDateFormat(cell,cell.getDataFormatData().getFormat());
            if (newVal!=null && !"".equals(newVal)){
                integerStringMap.put(key,newVal);
            }
        }
    }

    /**
     * eg: 0:张三  0_type:String
     * @param integerStringMap
     * @param cellMap
     * @return
     */
    private Map<String, String> getCellType(Map<Integer, String> integerStringMap, Map<Integer, Cell> cellMap){
        Map<String, String> nameAndTypeMap = new HashMap<>();
        // key取值是 0 1 2 3....
        for (Integer key : integerStringMap.keySet()) {
            nameAndTypeMap.put(String.valueOf(key),integerStringMap.get(key));
            ReadCellData cell = (ReadCellData) cellMap.get(key);
            String cellType = ExcelUtils.getCellType(cell,integerStringMap.get(key));
            if (cellType!=null && !"".equals(cellType)){
                nameAndTypeMap.put(key+"_type",cellType);
            }
        }
        return nameAndTypeMap;
    }

    public Map<Integer, String> getHeadMap() {
        return headMap;
    }

    public void setHeadMap(Map<Integer, String> headMap) {
        this.headMap = headMap;
    }

    public List<Map<Integer, String>> getValList() {
        return valList;
    }

    public void setValList(List<Map<Integer, String>> valList) {
        this.valList = valList;
    }

    public Integer getStartColumnNum() {
        return startColumnNum;
    }

    public void setStartColumnNum(Integer startColumnNum) {
        this.startColumnNum = startColumnNum;
    }

    public Integer getStartRowNum() {
        return startRowNum;
    }

    public void setStartRowNum(Integer startRowNum) {
        this.startRowNum = startRowNum;
    }

    public Integer getEndColumnNum() {
        return endColumnNum;
    }

    public void setEndColumnNum(Integer endColumnNum) {
        this.endColumnNum = endColumnNum;
    }

    public Integer getEndRowNum() {
        return endRowNum;
    }

    public void setEndRowNum(Integer endRowNum) {
        this.endRowNum = endRowNum;
    }

    public int getMaxColumnNum() {
        return maxColumnNum;
    }

    public void setMaxColumnNum(int maxColumnNum) {
        this.maxColumnNum = maxColumnNum;
    }

    public int getMaxRowNum() {
        return maxRowNum;
    }

    public void setMaxRowNum(int maxRowNum) {
        this.maxRowNum = maxRowNum;
    }

    public boolean isNameAndTypeFlag() {
        return nameAndTypeFlag;
    }

    public void setNameAndTypeFlag(boolean nameAndTypeFlag) {
        this.nameAndTypeFlag = nameAndTypeFlag;
    }

    public Map<String, String> getHeadValAndTypeMap() {
        return headValAndTypeMap;
    }

    public void setHeadValAndTypeMap(Map<String, String> headValAndTypeMap) {
        this.headValAndTypeMap = headValAndTypeMap;
    }

    public List<Map<String, String>> getContentValAndTypeList() {
        return contentValAndTypeList;
    }

    public void setContentValAndTypeList(List<Map<String, String>> contentValAndTypeList) {
        this.contentValAndTypeList = contentValAndTypeList;
    }
}
