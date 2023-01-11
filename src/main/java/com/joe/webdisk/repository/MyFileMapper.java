package com.joe.webdisk.repository;

import com.joe.webdisk.entity.FileStoreStatistics;
import com.joe.webdisk.entity.MyFile;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface MyFileMapper {

    FileStoreStatistics getCountStatistics(Integer fileStoreId);

    List<MyFile> getRootFilesByFileStoreId(Integer fileStoreId);

    List<MyFile> getFilesByParentFolderId(Integer parentFolderId);

    List<MyFile> getFilesByType(Integer storeId,Integer type);

    MyFile getFileByFileId(Integer myFileId);

    Integer addFileByFileStoreId(MyFile myFile);

    Integer updateFileByFileId(MyFile myFile);

    Integer deleteByFileId(Integer myFileId);

    Integer deleteByParentFolderId(Integer parentFolderId);

}
