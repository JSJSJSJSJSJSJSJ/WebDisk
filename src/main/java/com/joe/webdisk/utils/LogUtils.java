package com.joe.webdisk.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LogUtils {
    private static Logger logger;
    public static Logger getInstance(Class c) {
        return logger = LoggerFactory.getLogger(c);
    }
    public LogUtils() {}
}
