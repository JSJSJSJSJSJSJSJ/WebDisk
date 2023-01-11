package com.joe.webdisk.repository;

import com.joe.webdisk.entity.FileStore;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface FileStoreMapper {

    Integer addFileStore(FileStore fileStore);

    FileStore getFileStoreByUserId(Integer userId);

    FileStore getFileStoreByFileStoreId(Integer fileStoreId);

    Integer addSize(Integer id, Integer size);

    Integer subSize(Integer id, Integer size);

    Integer updatePermission(Integer id,Integer permission,Integer size);

    Integer deleteById(Integer id);

}
