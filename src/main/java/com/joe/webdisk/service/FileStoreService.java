package com.joe.webdisk.service;

import com.joe.webdisk.entity.FileStore;

public interface FileStoreService {
    boolean addFileStore(FileStore fileStore);

    FileStore getFileStoreByFileStoreId(Integer fileStoreId);

    FileStore getFileStoreByUserId(Integer userId);

    Integer addSize(Integer id, Integer size);

    Integer subSize(Integer id, Integer size);

    Integer updatePermission(Integer id,Integer permission,Integer size);

    Integer deleteById(Integer id);

}
