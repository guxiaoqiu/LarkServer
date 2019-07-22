package com.github.hollykunge.service;

import com.github.hollykunge.entity.FileManageInf;

import java.util.List;
import java.util.Map;

public interface FileMangeService {
    FileManageInf queryById(String fileId);
    int insert(FileManageInf fileManageInf);
    int update(FileManageInf fileManageInf);
    int deleteById(String fileId);
    int fileUpdate(String fileId, String level, String type);
    String getFileSizeByDB(String queryType, String queryDate, String returnUnit);
    List<Map<String,String>> getFileSizeRangeByDB(String queryType, String queryDateBegin, String queryDateEnd, String returnUnit);
}
