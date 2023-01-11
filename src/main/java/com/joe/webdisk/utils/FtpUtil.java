package com.joe.webdisk.utils;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPReply;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

@Component
public class FtpUtil {

    /**
     * FTP服务器hostname
     */
    private static String HOST;
    /**
     * FTP服务器端口
     */
    private static int PORT;
    /**
     * FTP登录账号
     */
    private static String USERNAME;
    /**
     * FTP登录密码
     */
    private static String PASSWORD;
    /**
     * FTP服务器基础目录
     */
    private static String PATH;
    /**
     * FTP客户端
     */
    private static FTPClient ftp;

    @Value("${ftp.ip}")
    private void setHOST(String ip) {
        HOST = ip;
    }
    @Value("${ftp.port}")
    private void setPORT(int port) {
        PORT = port;
    }
    @Value("${ftp.username}")
    private void setUSERNAME(String username) {
        USERNAME = username;
    }
    @Value("${ftp.password}")
    private void setPASSWORD(String password) {
        PASSWORD = password;
    }
    @Value("${ftp.remote-path}")
    private void setPATH(String path) {
        PATH = path;
    }

    public static boolean initFtpClient() {
        ftp = new FTPClient();
        int reply;
        try {
            // 连接FTP服务器
            ftp.connect(HOST, PORT);
            //登录, 如果采用默认端口，可以使用ftp.connect(host)的方式直接连接FTP服务器
            ftp.login(USERNAME, PASSWORD);
            ftp.setBufferSize(10240);
            //设置传输超时时间为60秒
            ftp.setDataTimeout(600000);
            //连接超时为60秒
            ftp.setConnectTimeout(600000);
            //设置文件类型
            ftp.setFileType(FTPClient.BINARY_FILE_TYPE);
            // 设置中文编码集，防止中文乱码
            ftp.setControlEncoding("UTF-8");
            reply = ftp.getReplyCode();
            if (!FTPReply.isPositiveCompletion(reply)) {
                ftp.disconnect();
                return true;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    public static boolean uploadFile(String filePath, String filename, InputStream input) {
        boolean result = false;
        try {
            filePath = new String(filePath.getBytes(StandardCharsets.UTF_8),StandardCharsets.ISO_8859_1);
            filename = new String(filename.getBytes(StandardCharsets.UTF_8),StandardCharsets.ISO_8859_1);
            if (initFtpClient()){
                return false;
            }
            //切换到上传目录
            ftp.enterLocalPassiveMode();
            if (!ftp.changeWorkingDirectory(PATH+filePath)) {
                //如果目录不存在创建目录
                String[] dirs = filePath.split("/");
                for (int i = 0; i < dirs.length; i++) {
                    System.out.println(dirs[i]);
                };
                String tempPath = PATH;
                for (String dir : dirs) {
                    if (null == dir || "".equals(dir)){
                        continue;
                    }
                    tempPath += "/" + dir;
                    if (!ftp.changeWorkingDirectory(tempPath)) {
                        if (!ftp.makeDirectory(tempPath)) {
                            System.out.println("makeDirectory...........");
                            return false;
                        } else {
                            ftp.changeWorkingDirectory(tempPath);
                        }
                    }
                }
            }
            //FTP以二进制形式传输
            ftp.setFileType(FTP.BINARY_FILE_TYPE);
            //通知服务器开通给一个端口，防止挂死
            ftp.enterLocalPassiveMode();
            //上传文件
            if (!ftp.storeFile(filename, input)) {
                return false;
            }
            input.close();
            ftp.logout();
            result = true;
        }
        catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (ftp.isConnected()) {
                try {
                    ftp.disconnect();
                } catch (IOException ignored) {
                }
            }
        }
        return result;
    }

    public static boolean downloadFile(String remotePath, String fileName, OutputStream outputStream) {
        boolean result = false;
        try {
            if (initFtpClient()){
                return false;
            }
            // 转移到FTP服务器目录
            ftp.changeWorkingDirectory(remotePath);
            // 通知服务器开通给一个端口，防止挂死
            ftp.enterLocalPassiveMode();
            // 取得指定文件夹下文件列表
            FTPFile[] fs = ftp.listFiles();
            for (FTPFile ff : fs) {
                // 取得指定文件并下载
                if (ff.getName().equals(fileName)) {
                    ftp.enterLocalPassiveMode();
                    ftp.retrieveFile(
                            new String(fileName.getBytes(StandardCharsets.UTF_8),StandardCharsets.ISO_8859_1),
                            outputStream);
                    result = true;
                }
            }
            ftp.logout();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (ftp.isConnected()) {
                try {
                    ftp.disconnect();
                } catch (IOException ioe) {
                    ioe.printStackTrace();
                }
            }
        }
        return result;
    }

    public static boolean deleteFile( String remotePath,String fileName){
        boolean flag = false;
        try {
            if (initFtpClient()){
                return false;
            }
            // 转移到FTP服务器目录
            ftp.changeWorkingDirectory(remotePath);
            ftp.enterLocalPassiveMode();
            FTPFile[] fs = ftp.listFiles();
            for (FTPFile ff : fs) {
                if ("".equals(fileName)){
                    return false;
                }
                if (ff.getName().equals(fileName)){
                    ftp.deleteFile(new String(fileName.getBytes(StandardCharsets.UTF_8),StandardCharsets.ISO_8859_1));
                    flag = true;
                }
            }
            ftp.logout();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (ftp.isConnected()) {
                try {
                    ftp.disconnect();
                } catch (IOException ioe) {
                    ioe.printStackTrace();
                }
            }
        }
        return flag;
    }

    public static boolean deleteFolder( String remotePath){
        boolean flag = false;
        try {
            if (initFtpClient()){
                return false;
            }
            // 转移到FTP服务器目录
            ftp.changeWorkingDirectory(remotePath);
            ftp.enterLocalPassiveMode();
            FTPFile[] fs = ftp.listFiles();
            if (fs.length==0){
                remotePath = PATH + "/" +remotePath;
                flag = ftp.removeDirectory(remotePath);
            }
            ftp.logout();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (ftp.isConnected()) {
                try {
                    ftp.disconnect();
                } catch (IOException ioe) {
                    ioe.printStackTrace();
                }
            }
        }
        return flag;
    }

    public static boolean reNameFile( String oldAllName,String newAllName){
        boolean flag = false;
        try {
            oldAllName = new String(oldAllName.getBytes(StandardCharsets.UTF_8),StandardCharsets.ISO_8859_1);
            newAllName = new String(newAllName.getBytes(StandardCharsets.UTF_8),StandardCharsets.ISO_8859_1);
            if (initFtpClient()){
                return false;
            }
            ftp.enterLocalPassiveMode();
            ftp.rename(oldAllName,newAllName);
            flag = true;
            ftp.logout();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (ftp.isConnected()) {
                try {
                    ftp.disconnect();
                } catch (IOException ioe) {
                    ioe.printStackTrace();
                }
            }
        }
        return flag;
    }

}
