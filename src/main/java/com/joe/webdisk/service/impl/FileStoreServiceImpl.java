package com.joe.webdisk.service.impl;

import com.joe.webdisk.entity.FileStore;
import com.joe.webdisk.service.FileStoreService;
import org.springframework.stereotype.Service;

@Service
public class FileStoreServiceImpl extends BaseService implements FileStoreService {

    @Override
    public boolean addFileStore(FileStore fileStore) {
        return fileStoreMapper.addFileStore(fileStore) == 1;
    }

    @Override
    public FileStore getFileStoreByFileStoreId(Integer fileStoreId) {
        return fileStoreMapper.getFileStoreByFileStoreId(fileStoreId);
    }

    @Override
    public FileStore getFileStoreByUserId(Integer userId) {
        return fileStoreMapper.getFileStoreByUserId(userId);
    }

    @Override
    public Integer addSize(Integer id, Integer size) {
        return fileStoreMapper.addSize(id,size);
    }

    @Override
    public Integer subSize(Integer id, Integer size) {
        return fileStoreMapper.subSize(id, size);
    }

    @Override
    public Integer updatePermission(Integer id, Integer permission, Integer size) {
        return fileStoreMapper.updatePermission(id, permission, size);
    }

    @Override
    public Integer deleteById(Integer id) {
        return fileStoreMapper.deleteById(id);
    }
}
