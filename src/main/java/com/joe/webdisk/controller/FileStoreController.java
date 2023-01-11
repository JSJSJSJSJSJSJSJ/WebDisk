package com.joe.webdisk.controller;

import com.joe.webdisk.entity.FileFolder;
import com.joe.webdisk.entity.FileStore;
import com.joe.webdisk.entity.FileStoreStatistics;
import com.joe.webdisk.entity.MyFile;
import com.joe.webdisk.utils.FtpUtil;
import com.joe.webdisk.utils.LogUtils;
import com.joe.webdisk.utils.QRCodeUtil;
import org.slf4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.*;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Controller
public class FileStoreController extends BaseController{

    private Logger logger = LogUtils.getInstance(FileStoreController.class);

    @PostMapping("/addFolder")
    public String addFolder(FileFolder folder, Map<String, Object> map) {
        //设置文件夹信息
        folder.setFileStoreId(loginUser.getFileStoreId());
        folder.setTime(new Date());
        //获得当前目录下的所有文件夹,检查当前文件夹是否已经存在
        List<FileFolder> fileFolders;
        if (folder.getParentFolderId() == 0){
            //获取用户根目录下的所有文件夹
            fileFolders = fileFolderService.getRootFoldersByFileStoreId(loginUser.getFileStoreId());
        }else{
            //获取用户的其他目录下的所有文件夹
            fileFolders = fileFolderService.getFileFoldersByParentFolderId(folder.getParentFolderId());
        }
        for (FileFolder fileFolder : fileFolders) {
            if (fileFolder.getFileFolderName().equals(folder.getFileFolderName())) {
                logger.info("添加文件夹失败!文件夹已存在...");
                map.put("error", "添加文件夹失败!文件夹已存在...");
                return "redirect:/files?error=1&fId=" + folder.getParentFolderId();
            }
        }
        //向数据库写入数据
        if (fileFolderService.addFileFolder(folder) == 1) {
            logger.info("添加文件夹成功!"+folder);
        } else {
            logger.info("添加文件夹失败!");
            map.put("error", "添加文件夹失败!");
        }
        return "redirect:/files?fId="+folder.getParentFolderId();
    }

    @PostMapping("/updateFolder")
    public String updateFolder(FileFolder folder,Map<String, Object> map) {
        //获得文件夹的数据库信息
        FileFolder fileFolder = fileFolderService.getFileFolderByFileFolderId(folder.getFileFolderId());
        fileFolder.setFileFolderName(folder.getFileFolderName());
        //获得当前目录下的所有文件夹,用于检查文件夹是否已经存在
        List<FileFolder> fileFolders = fileFolderService.getFileFoldersByParentFolderId(fileFolder.getParentFolderId());
        for (FileFolder folder1 : fileFolders) {
            if (folder1.getFileFolderName().equals(folder.getFileFolderName()) && !folder1.getFileFolderId().equals(folder.getFileFolderId())) {
                logger.info("重命名文件夹失败!文件夹已存在...");
                map.put("error", "重命名文件夹失败!文件夹已存在...");
                return "redirect:/files?error=2&fId=" + fileFolder.getParentFolderId();
            }
        }
        //向数据库写入数据
        if (fileFolderService.updateFileFolderByFileFolderId(fileFolder) == 1) {
            logger.info("重命名文件夹成功!"+folder);
        } else {
            logger.info("重命名文件夹失败!");
            map.put("error", "重命名文件夹失败!");
        }
        return "redirect:/files?fId="+fileFolder.getParentFolderId();
    }

    @GetMapping("/deleteFolder")
    public String deleteFolder(@RequestParam Integer fId){
        FileFolder folder = fileFolderService.getFileFolderByFileFolderId(fId);
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        String dateStr = format.format(folder.getTime());
        String path = "1/"+dateStr+"/"+fId;
        //强制删除
        deleteFolderF(folder);
        if (folder.getParentFolderId() == 0) {
            FtpUtil.deleteFolder("1/"+dateStr);
        } else {
            FtpUtil.deleteFolder(path);
        }
        return folder.getParentFolderId() == 0?"redirect:/files":"redirect:/files?fId="+folder.getParentFolderId();
    }

    public void deleteFolderF(FileFolder folder){
        //获得当前文件夹下的所有子文件夹
        List<FileFolder> folders = fileFolderService.getFileFoldersByParentFolderId(folder.getFileFolderId());
        //删除当前文件夹的所有的文件
        List<MyFile> files = myFileService.getFilesByParentFolderId(folder.getFileFolderId());
        if (files.size()!=0){
            for (MyFile file : files) {
                Integer fileId = file.getMyFileId();
                boolean b = FtpUtil.deleteFile(file.getMyFilePath(), file.getMyFileName() + file.getPostfix());
                if (b) {
                    myFileService.deleteByFileId(fileId);
                    fileStoreService.subSize(folder.getFileStoreId(), file.getSize());
                }
            }
        }
        if (folders.size()!=0){
            deleteFolderN(folder);
            for (FileFolder fileFolder : folders) {
                deleteFolderF(fileFolder);
            }
        } else {
            deleteFolderN(folder);
        }
        fileFolderService.deleteFileFolderById(folder.getFileFolderId());
    }

    public void deleteFolderN(FileFolder folder) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        String dateStr = format.format(folder.getTime());
        String path = "1/"+dateStr+"/"+folder.getFileFolderId();
        FtpUtil.deleteFolder(path);
    }

    @ResponseBody
    @PostMapping("/uploadFile")
    public Map<String, Object> uploadFile(@RequestParam("file") MultipartFile files) {
        Map<String, Object> map = new HashMap<>();
        if (fileStoreService.getFileStoreByUserId(loginUser.getUserId()).getPermission() != 0){
            logger.error("用户没有上传文件的权限!上传失败...");
            map.put("code", 499);
            return map;
        }
        FileStore store = fileStoreService.getFileStoreByUserId(loginUser.getUserId());
        Integer folderId = Integer.valueOf(request.getHeader("id"));
        String name = files.getOriginalFilename().replaceAll(" ","");
        //获取当前目录下的所有文件，用来判断是否已经存在
        List<MyFile> myFiles = null;
        if (folderId == 0){
            //当前目录为根目录
            myFiles = myFileService.getRootFilesByFileStoreId(loginUser.getFileStoreId());
        }else {
            //当前目录为其他目录
            myFiles = myFileService.getFilesByParentFolderId(folderId);
        }
        for (MyFile myFile : myFiles) {
            if ((myFile.getMyFileName() + myFile.getPostfix()).equals(name)) {
                logger.error("当前文件已存在!上传失败...");
                map.put("code", 501);
                return map;
            }
        }
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        String dateStr = format.format(new Date());
        String path = loginUser.getUserId()+"/"+dateStr +"/"+folderId;
        if (!checkTarget(name)){
            logger.error("上传失败!文件名不符合规范...");
            map.put("code", 502);
            return map;
        }
        Integer sizeInt = Math.toIntExact(files.getSize() / 1024);
        //是否仓库放不下该文件
        if(store.getCurrentSize()+sizeInt > store.getMaxSize()){
            logger.error("上传失败!仓库已满。");
            map.put("code", 503);
            return map;
        }
        //处理文件大小
        String size = String.valueOf(files.getSize()/1024.0);
        int indexDot = size.lastIndexOf(".");
        size = size.substring(0,indexDot);
        int index = name.lastIndexOf(".");
        String tempName = name;
        String postfix = "";
        int type = 4;
        if (index!=-1){
            tempName = name.substring(index);
            name = name.substring(0,index);
            //获得文件类型
            type = getType(tempName.toLowerCase());
            postfix = tempName.toLowerCase();
        }
        try {
            //提交到FTP服务器
            boolean b = FtpUtil.uploadFile("/"+path, name + postfix, files.getInputStream());
            if (b){
                //上传成功
                logger.info("文件上传成功!"+files.getOriginalFilename());
                //向数据库文件表写入数据
                myFileService.addFileByFileStoreId(
                        MyFile.builder()
                                .myFileName(name).fileStoreId(loginUser.getFileStoreId()).myFilePath(path)
                                .downloadTime(0).uploadTime(new Date()).parentFolderId(folderId).
                                size(Integer.valueOf(size)).type(type).postfix(postfix).build());
                //更新仓库表的当前大小
                fileStoreService.addSize(store.getFileStoreId(),Integer.valueOf(size));
                try {
                    Thread.sleep(5000);
                    map.put("code", 200);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }else{
                logger.error("文件上传失败!"+files.getOriginalFilename());
                map.put("code", 504);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return map;
    }

    @GetMapping("/downloadFile")
    public String downloadFile(@RequestParam Integer fId){
        if (fileStoreService.getFileStoreByUserId(loginUser.getUserId()).getPermission() == 2){
            logger.error("用户没有下载文件的权限!下载失败...");
            return "redirect:/error401Page";
        }
        //获取文件信息
        MyFile myFile = myFileService.getFileByFileId(fId);
        String remotePath = myFile.getMyFilePath();
        String fileName = myFile.getMyFileName()+myFile.getPostfix();
        try {
            //去FTP上拉取
            OutputStream os = new BufferedOutputStream(response.getOutputStream());
            response.setCharacterEncoding("UTF-8");
            // 设置返回类型
            response.setContentType("multipart/form-data");
            // 文件名转码一下，不然会出现中文乱码
            response.setHeader("Content-Disposition", "attachment;fileName=" + URLEncoder.encode(fileName, "UTF-8"));
            boolean flag = FtpUtil.downloadFile(remotePath, fileName, os);
            if (flag) {
                myFileService.updateFileByFileId(
                        MyFile.builder().myFileId(myFile.getMyFileId()).downloadTime(myFile.getDownloadTime() + 1).build());
                os.flush();
                os.close();
                logger.info("文件下载成功!" + myFile);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "success";
    }

    @GetMapping("/deleteFile")
    public String deleteFile(@RequestParam Integer fId, Integer folder){
        boolean b = false;
        //获得文件信息
        MyFile myFile = myFileService.getFileByFileId(fId);
        String remotePath = myFile.getMyFilePath();
        String fileName = myFile.getMyFileName()+myFile.getPostfix();
        //判断该文件是否为根目录文件，如果是则判断该目录下是否有其它文件和文件夹：没有则删除此目录，有则只删除此文件
        if (0==myFile.getParentFolderId()) {
            FileStoreStatistics countStatistics = myFileService.getCountStatistics(myFile.getFileStoreId());
            if (countStatistics.getFileCount() == 1 && countStatistics.getFolderCount() == 0) {
                b = FtpUtil.deleteFile(remotePath, fileName);
                if (b) {
                    if (FtpUtil.deleteFolder(remotePath)) {
                        if (FtpUtil.deleteFolder(remotePath.substring(0,12))) {
                            FtpUtil.deleteFolder(String.valueOf(loginUser.getUserId()));
                        }
                    }
                }
            }
        } else {
            //从FTP文件服务器上删除文件
            b = FtpUtil.deleteFile(remotePath, fileName);
        }
        if (b) {
            //删除成功,返回空间
            fileStoreService.subSize(myFile.getFileStoreId(), myFile.getSize());
            //删除文件表对应的数据
            myFileService.deleteByFileId(fId);
        }
        logger.info("删除文件成功!"+myFile);
        return "redirect:/files?fId="+folder;
    }

    @PostMapping("/updateFileName")
    public String updateFileName(MyFile file, Map<String, Object> map) {
        MyFile myFile = myFileService.getFileByFileId(file.getMyFileId());
        if (myFile != null){
            String oldName = myFile.getMyFileName();
            String newName = file.getMyFileName();
            if (!oldName.equals(newName)){
                boolean b = FtpUtil.reNameFile(myFile.getMyFilePath() + "/" + oldName+myFile.getPostfix(), myFile.getMyFilePath() + "/" + newName+myFile.getPostfix());
                if (b){
                    Integer integer = myFileService.updateFileByFileId(
                            MyFile.builder().myFileId(myFile.getMyFileId()).myFileName(newName).build());
                    if (integer == 1){
                        logger.info("修改文件名成功!原文件名:"+oldName+"  新文件名:"+newName);
                    }else{
                        logger.error("修改文件名失败!原文件名:"+oldName+"  新文件名:"+newName);
                    }
                }
            }
        }
        return "redirect:/files?fId="+myFile.getParentFolderId();
    }

    @GetMapping("getQrCode")
    @ResponseBody
    public Map<String,Object> getQrCode(@RequestParam Integer id, @RequestParam String url){
        Map<String,Object> map = new HashMap<>();
        map.put("imgPath","https://i.loli.net/2021/06/07/NPelJFEaMAOQork.jpg");
        if (id != null){
            MyFile file = myFileService.getFileByFileId(id);
            if (file != null){
                try {
                    String path = request.getSession().getServletContext().getRealPath("/user_img/");
                    url = url+"/file/share?t="+ UUID.randomUUID().toString().substring(0,10) +"&f="+file.getMyFileId()+"&p="+file.getUploadTime().getTime()+""+file.getSize()+"&flag=1";
                    File targetFile = new File(path, "");
                    if (!targetFile.exists()) {
                        targetFile.mkdirs();
                    }
                    File f = new File(path, id + ".jpg");
                    if (!f.exists()){
                        //文件不存在,开始生成二维码并保存文件
                        OutputStream os = new FileOutputStream(f);
                        QRCodeUtil.encode(url, "/static/img/logo.jpg", os, true);
                        os.close();
                    }
                    map.put("imgPath","user_img/"+id+".jpg");
                    map.put("url",url);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        return map;
    }

    @GetMapping("/file/share")
    public String shareFile(Integer fId, String p, String t, Integer flag){
        String fileNameTemp;
        String remotePath;
        String fileName;
        int times = 0;
        if (flag == null || fId == null || p == null || t == null){
            logger.info("下载分享文件失败，参数错误");
            return "redirect:/error400Page";
        }
        if(flag == 1){
            //获取文件信息
            MyFile myFile = myFileService.getFileByFileId(fId);
            if (myFile == null){
                return "redirect:/error404Page";
            }
            String pwd = myFile.getUploadTime().getTime()+""+myFile.getSize();
            if (!pwd.equals(p)){
                return "redirect:/error400Page";
            }
            remotePath = myFile.getMyFilePath();
            fileName = myFile.getMyFileName()+myFile.getPostfix();
        } else {
            return "redirect:/error400Page";
        }
        fileNameTemp = fileName;
        try {
            //解决下载文件时 中文文件名乱码问题
            boolean isMSIE = isMSBrowser(request);
            if (isMSIE) {
                //IE浏览器的乱码问题解决
                fileNameTemp = URLEncoder.encode(fileNameTemp, "UTF-8");
            } else {
                //万能乱码问题解决
                fileNameTemp = new String(fileNameTemp.getBytes(StandardCharsets.UTF_8), StandardCharsets.ISO_8859_1);
            }
            //去FTP上拉取
            OutputStream os = new BufferedOutputStream(response.getOutputStream());
            response.setCharacterEncoding("utf-8");
            // 设置返回类型
            response.setContentType("multipart/form-data");
            // 文件名转码一下，不然会出现中文乱码
            response.setHeader("Content-Disposition", "attachment;fileName=" + fileNameTemp);
            if (FtpUtil.downloadFile("/" + remotePath, fileName, os)) {
                myFileService.updateFileByFileId(
                        MyFile.builder().myFileId(fId).downloadTime(times + 1).build());
                os.flush();
                os.close();
                logger.info("文件下载成功!");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "success";
    }

    public boolean checkTarget(String name) {
        final String format = "[^\\u4E00-\\u9FA5\\uF900-\\uFA2D\\w-_.]";
        Pattern pattern = Pattern.compile(format);
        Matcher matcher = pattern.matcher(name);
        return !matcher.find();
    }

    public int getType(String type){
        if (".chm".equals(type)||".txt".equals(type)||".xmind".equals(type)||".xlsx".equals(type)||".xls".equals(type)
                ||".doc".equals(type)||".docx".equals(type)||".pptx".equals(type)||".md".equals(type)
                ||".wps".equals(type)||".word".equals(type)||".html".equals(type)||".pdf".equals(type)){
            return  1;
        }else if (".bmp".equals(type)||".gif".equals(type)||".jpg".equals(type)||".ico".equals(type)||".vsd".equals(type)
                ||".pic".equals(type)||".png".equals(type)||".jepg".equals(type)||".jpeg".equals(type)||".webp".equals(type)
                ||".svg".equals(type)){
            return 2;
        } else if (".avi".equals(type)||".mov".equals(type)||".qt".equals(type)
                ||".asf".equals(type)||".rm".equals(type)||".navi".equals(type)||".wav".equals(type)
                ||".mp4".equals(type)||".mkv".equals(type)||".webm".equals(type)){
            return 3;
        } else if (".mp3".equals(type)||".wma".equals(type)){
            return 4;
        } else {
            return 5;
        }
    }

    public static boolean isMSBrowser(HttpServletRequest request) {
        String[] IEBrowserSignals = {"MSIE", "Trident", "Edge"};
        String userAgent = request.getHeader("User-Agent");
        for (String signal : IEBrowserSignals) {
            if (userAgent.contains(signal)){
                return true;
            }
        }
        return false;
    }

}
