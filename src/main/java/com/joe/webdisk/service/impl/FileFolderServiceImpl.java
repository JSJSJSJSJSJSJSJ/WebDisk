package com.joe.webdisk.service.impl;

import com.joe.webdisk.entity.FileFolder;
import com.joe.webdisk.entity.MyFile;
import com.joe.webdisk.service.FileFolderService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FileFolderServiceImpl extends BaseService implements FileFolderService {
    @Override
    public Integer getFileFolderCountByFileStoreId(Integer fileStoreId) {
        return fileFolderMapper.getFileFolderCountByFileStoreId(fileStoreId);
    }

    @Override
    public List<FileFolder> getRootFoldersByFileStoreId(Integer fileStoreId) {
        return fileFolderMapper.getRootFoldersByFileStoreId(fileStoreId);
    }

    @Override
    public FileFolder getFileFolderByFileFolderId(Integer fileFolderId) {
        return fileFolderMapper.getFileFolderByFileFolderId(fileFolderId);
    }

    @Override
    public List<FileFolder> getFileFoldersByParentFolderId(Integer parentFolderId) {
        return fileFolderMapper.getFileFoldersByParentFolderId(parentFolderId);
    }

    @Override
    public Integer addFileFolder(FileFolder fileFolder) {
        return fileFolderMapper.addFileFolder(fileFolder);
    }

    @Override
    public Integer updateFileFolderByFileFolderId(FileFolder fileFolder) {
        return fileFolderMapper.updateFileFolderByFileFolderId(fileFolder);
    }

    @Override
    public List<MyFile> getFileByFileFolder(Integer fileFolderId) {
        return fileFolderMapper.getFileByFileFolder(fileFolderId);
    }

    @Override
    public Integer deleteFileFolderById(Integer fileFolderId) {
        return fileFolderMapper.deleteFileFolderById(fileFolderId);
    }

    @Override
    public Integer deleteFileFolderByParentFolderId(Integer parentFolderId) {
        return fileFolderMapper.deleteFileFolderByParentFolderId(parentFolderId);
    }

    @Override
    public Integer deleteFileFolderByFileStoreId(Integer fileStoreId) {
        return fileFolderMapper.deleteFileFolderByFileStoreId(fileStoreId);
    }

    @Override
    public List<FileFolder> getFileFolderByFileStoreId(Integer fileStoreId) {
        return fileFolderMapper.getFileFolderByFileStoreId(fileStoreId);
    }
}
