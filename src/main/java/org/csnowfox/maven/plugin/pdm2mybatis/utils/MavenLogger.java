package org.csnowfox.maven.plugin.pdm2mybatis.utils;

import org.apache.maven.plugin.logging.Log;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @ClassName: MavenLogger
 * @Description 日志类，处理maven编译日志
 * @Author Csnowfox
 * @Date 2019/4/27 17:47
 **/
public class MavenLogger {

    private static Log log;

    public static void info(String msg) {
        if (MavenLogger.log == null) {
            System.out.println(msg);
        } else {
            MavenLogger.log.info(msg);
        }
    }

    public static void error(String msg, Exception e) {
        if (MavenLogger.log == null) {
            System.out.println(msg);
            e.printStackTrace();
        } else {
            MavenLogger.log.info(msg);
        }
    }

    public static void error(String msg) {
        if (MavenLogger.log == null) {
            System.out.println(msg);
        } else {
            MavenLogger.log.info(msg);
        }
    }

    public static void init(Log log) {
        MavenLogger.log = log;
    }
}
