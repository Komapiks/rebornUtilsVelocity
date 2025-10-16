package ru.komap.rebornUtilsVelocity.logger;

import org.slf4j.Logger;

public class LoggerTerminal {
    private Logger logger;
    public LoggerTerminal(Logger logger) {
        this.logger = logger;
    }
    public void Debug(String message) {
        logger.debug(message);
    }
    public void Info(String message) {
        logger.info(message);
    }
}
