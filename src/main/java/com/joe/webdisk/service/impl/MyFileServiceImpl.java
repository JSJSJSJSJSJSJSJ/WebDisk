package com.joe.webdisk.service.impl;

import com.joe.webdisk.entity.FileStoreStatistics;
import com.joe.webdisk.entity.MyFile;
import com.joe.webdisk.service.MyFileService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MyFileServiceImpl extends BaseService implements MyFileService {

    @Override
    public FileStoreStatistics getCountStatistics(Integer fileStoreId) {
        FileStoreStatistics statistics = myFileMapper.getCountStatistics(fileStoreId);
        statistics.setFolderCount(fileFolderMapper.getFileFolderCountByFileStoreId(fileStoreId));
        return statistics;
    }

    @Override
    public List<MyFile> getRootFilesByFileStoreId(Integer fileStoreId) {
        return myFileMapper.getRootFilesByFileStoreId(fileStoreId);
    }

    @Override
    public List<MyFile> getFilesByParentFolderId(Integer parentFolderId) {
        return myFileMapper.getFilesByParentFolderId(parentFolderId);
    }

    @Override
    public List<MyFile> getFilesByType(Integer storeId, Integer type) {
        return myFileMapper.getFilesByType(storeId, type);
    }

    @Override
    public MyFile getFileByFileId(Integer myFileId) {
        return myFileMapper.getFileByFileId(myFileId);
    }

    @Override
    public Integer addFileByFileStoreId(MyFile myFile) {
        return myFileMapper.addFileByFileStoreId(myFile);
    }

    @Override
    public Integer updateFileByFileId(MyFile myFile) {
        return myFileMapper.updateFileByFileId(myFile);
    }

    @Override
    public Integer deleteByFileId(Integer myFileId) {
        return myFileMapper.deleteByFileId(myFileId);
    }

    @Override
    public Integer deleteByParentFolderId(Integer parentFolderId) {
        return myFileMapper.deleteByParentFolderId(parentFolderId);
    }
}
