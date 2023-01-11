package com.joe.webdisk.service;

import com.joe.webdisk.entity.FileFolder;
import com.joe.webdisk.entity.MyFile;

import java.util.List;

public interface FileFolderService {

    Integer getFileFolderCountByFileStoreId(Integer fileStoreId);

    List<FileFolder> getFileFolderByFileStoreId(Integer fileStoreId);

    List<FileFolder> getRootFoldersByFileStoreId(Integer fileStoreId);

    FileFolder getFileFolderByFileFolderId(Integer fileFolderId);

    List<FileFolder> getFileFoldersByParentFolderId(Integer parentFolderId);

    List<MyFile> getFileByFileFolder(Integer fileFolderId);

    Integer addFileFolder(FileFolder fileFolder);

    Integer updateFileFolderByFileFolderId(FileFolder fileFolder);

    Integer deleteFileFolderById(Integer fileFolderId);

    Integer deleteFileFolderByParentFolderId(Integer parentFolderId);

    Integer deleteFileFolderByFileStoreId(Integer fileStoreId);
}
