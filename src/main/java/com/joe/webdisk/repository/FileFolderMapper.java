package com.joe.webdisk.repository;

import com.joe.webdisk.entity.FileFolder;
import com.joe.webdisk.entity.MyFile;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface FileFolderMapper {
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
