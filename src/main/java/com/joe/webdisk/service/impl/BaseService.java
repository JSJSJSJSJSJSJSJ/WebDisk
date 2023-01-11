package com.joe.webdisk.service.impl;

import com.joe.webdisk.repository.*;
import org.springframework.beans.factory.annotation.Autowired;

public class BaseService {
    @Autowired
    protected UserMapper userMapper;
    @Autowired
    protected FileStoreMapper fileStoreMapper;
    @Autowired
    protected FileFolderMapper fileFolderMapper;
    @Autowired
    protected MyFileMapper myFileMapper;
}
