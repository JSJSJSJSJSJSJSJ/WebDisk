package com.joe.webdisk.controller;

import com.joe.webdisk.entity.FileFolder;
import com.joe.webdisk.entity.FileStoreStatistics;
import com.joe.webdisk.entity.MyFile;
import com.joe.webdisk.utils.LogUtils;
import org.slf4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@Controller
public class SystemController extends BaseController{
    Logger logger = LogUtils.getInstance(SystemController.class);

    @GetMapping("/index")
    public String index(Map<String, Object> map) {
        //获取统计信息
        FileStoreStatistics statistics = myFileService.getCountStatistics(loginUser.getFileStoreId());
        statistics.setFileStore(fileStoreService.getFileStoreByFileStoreId(loginUser.getFileStoreId()));
        map.put("statistics", statistics);
        return "main/index";
    }

    @GetMapping("/files")
    public String toFileStorePage(Integer fId, String fName, Integer error, Map<String, Object> map) {
        //判断是否包含错误信息
        if (error != null) {
            if (error == 1) {
                map.put("error", "添加失败！当前已存在同名文件夹");
            }
            if (error == 2) {
                map.put("error", "重命名失败！文件夹已存在");
            }
        }
        //包含的子文件夹
        List<FileFolder> folders;
        //包含的文件
        List<MyFile> files;
        //当前文件夹信息
        FileFolder nowFolder;
        //当前文件夹的相对路径
        List<FileFolder> location = new ArrayList<>();
        if (fId == null || fId <= 0) {
            //代表当前为根目录
            fId = 0;
            folders = fileFolderService.getRootFoldersByFileStoreId(loginUser.getFileStoreId());
            files = myFileService.getRootFilesByFileStoreId(loginUser.getFileStoreId());
            nowFolder = FileFolder.builder().fileFolderId(fId).build();
            location.add(nowFolder);
        } else {
            //当前为具体目录,访问的文件夹不是当前登录用户所创建的文件夹
            FileFolder folder = fileFolderService.getFileFolderByFileFolderId(fId);
            if (folder.getFileStoreId() - loginUser.getFileStoreId() != 0){
                return "redirect:/error401Page";
            }
            //当前为具体目录，访问的文件夹是当前登录用户所创建的文件夹
            folders = fileFolderService.getFileFoldersByParentFolderId(fId);
            files = myFileService.getFilesByParentFolderId(fId);
            nowFolder = fileFolderService.getFileFolderByFileFolderId(fId);
            //遍历查询当前目录
            FileFolder temp = nowFolder;
            while (temp.getParentFolderId() != 0) {
                temp = fileFolderService.getFileFolderByFileFolderId(temp.getParentFolderId());
                location.add(temp);
            }
        }
        Collections.reverse(location);
        //获得统计信息
        FileStoreStatistics statistics = myFileService.getCountStatistics(loginUser.getFileStoreId());
        map.put("statistics", statistics);
        map.put("permission", fileStoreService.getFileStoreByUserId(loginUser.getUserId()).getPermission());
        map.put("folders", folders);
        map.put("files", files);
        map.put("nowFolder", nowFolder);
        map.put("location", location);
        logger.info("网盘页面域中的数据:" + map);
        return "main/files";
    }

    @GetMapping("/upload")
    public String toUploadPage(Integer fId, String fName, Map<String, Object> map) {
        //包含的子文件夹
        List<FileFolder> folders;
        //当前文件夹信息
        FileFolder nowFolder;
        //当前文件夹的相对路径
        List<FileFolder> location = new ArrayList<>();
        if (fId == null || fId <= 0) {
            //代表当前为根目录
            fId = 0;
            folders = fileFolderService.getRootFoldersByFileStoreId(loginUser.getFileStoreId());
            nowFolder = FileFolder.builder().fileFolderId(fId).build();
            location.add(nowFolder);
        } else {
            //当前为具体目录
            folders = fileFolderService.getFileFoldersByParentFolderId(fId);
            nowFolder = fileFolderService.getFileFolderByFileFolderId(fId);
            //遍历查询当前目录
            FileFolder temp = nowFolder;
            while (temp.getParentFolderId() != 0) {
                temp = fileFolderService.getFileFolderByFileFolderId(temp.getParentFolderId());
                location.add(temp);
            }
        }
        Collections.reverse(location);
        //获得统计信息
        FileStoreStatistics statistics = myFileService.getCountStatistics(loginUser.getFileStoreId());
        map.put("statistics", statistics);
        map.put("folders", folders);
        map.put("nowFolder", nowFolder);
        map.put("location", location);
        logger.info("网盘页面域中的数据:" + map);
        return "main/upload";
    }

    @GetMapping("/doc-files")
    public String toDocFilePage( Map<String, Object> map) {
        List<MyFile> files = myFileService.getFilesByType(loginUser.getFileStoreId(),1);
        //获得统计信息
        FileStoreStatistics statistics = myFileService.getCountStatistics(loginUser.getFileStoreId());
        map.put("statistics", statistics);
        map.put("files", files);
        map.put("permission", fileStoreService.getFileStoreByUserId(loginUser.getUserId()).getPermission());
        return "main/doc-files";
    }

    @GetMapping("/image-files")
    public String toImageFilePage( Map<String, Object> map) {
        List<MyFile> files = myFileService.getFilesByType(loginUser.getFileStoreId(),2);
        //获得统计信息
        FileStoreStatistics statistics = myFileService.getCountStatistics(loginUser.getFileStoreId());
        map.put("statistics", statistics);
        map.put("files", files);
        map.put("permission", fileStoreService.getFileStoreByUserId(loginUser.getUserId()).getPermission());
        return "main/image-files";
    }

    @GetMapping("/video-files")
    public String toVideoFilePage( Map<String, Object> map) {
        List<MyFile> files = myFileService.getFilesByType(loginUser.getFileStoreId(),3);
        //获得统计信息
        FileStoreStatistics statistics = myFileService.getCountStatistics(loginUser.getFileStoreId());
        map.put("statistics", statistics);
        map.put("files", files);
        map.put("permission", fileStoreService.getFileStoreByUserId(loginUser.getUserId()).getPermission());
        return "main/video-files";
    }

    @GetMapping("/music-files")
    public String toMusicFilePage( Map<String, Object> map) {
        List<MyFile> files = myFileService.getFilesByType(loginUser.getFileStoreId(),4);
        //获得统计信息
        FileStoreStatistics statistics = myFileService.getCountStatistics(loginUser.getFileStoreId());
        map.put("statistics", statistics);
        map.put("files", files);
        map.put("permission", fileStoreService.getFileStoreByUserId(loginUser.getUserId()).getPermission());
        return "main/music-files";
    }

    @GetMapping("/other-files")
    public String toOtherFilePage( Map<String, Object> map) {
        List<MyFile> files = myFileService.getFilesByType(loginUser.getFileStoreId(),5);
        //获得统计信息
        FileStoreStatistics statistics = myFileService.getCountStatistics(loginUser.getFileStoreId());
        map.put("statistics", statistics);
        map.put("files", files);
        map.put("permission", fileStoreService.getFileStoreByUserId(loginUser.getUserId()).getPermission());
        return "main/other-files";
    }
}
