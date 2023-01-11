package com.joe.webdisk.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
@Builder
@AllArgsConstructor
public class FileFolder implements Serializable {

    /**
     * 文件夹ID
     */
    private Integer fileFolderId;
    /**
     * 文件夹名称
     */
    private String fileFolderName;
    /**
     * 父文件夹ID
     */
    private Integer parentFolderId;
    /**
     * 所属文件仓库ID
     */
    private Integer fileStoreId;
    /**
     * 创建时间
     */
    private Date time;
}
