package org.canos.es_server;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ServerLogger {

    private static final Logger logger = LogManager.getLogger(ServerLogger.class.getName());

    private ServerLogger() {
    }

    public static void debug(String msg) {
        logger.debug(msg);
    }

    public static void trace(String msg) {
        logger.trace(msg);
    }

    public static void error(String msg) {
        logger.error(msg);
    }

    public static void info(String msg) {
        logger.info(msg);
    }
}