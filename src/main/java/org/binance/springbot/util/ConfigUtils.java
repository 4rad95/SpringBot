package org.binance.springbot.util;

//import org.apache.commons.io.IOUtils;
//import org.apache.commons.lang3.StringUtils;

import org.apache.commons.lang3.StringUtils;
import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.binance.springbot.aspect.LoggingAspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

public class ConfigUtils {

    public static final String CONFIG_PAUSE_TIME_MINUTES = "thread.pauseTimeInMinutes";
    public static final String CONFIG_BINANCE_TICK_INTERVAL = "tick.timeFrame";
    public static final String CONFIG_BINANCE_ADD1_INTERVAL = "add1.timeFrame";
    public static final String CONFIG_BINANCE_ADD2_INTERVAL = "add2.timeFrame";
    public static final String CONFIG_SYSTEM_BEEP = "system.beep";

    public static final String CONFIG_BINANCE_API_KEY = "binance.apiKey";
    public static final String CONFIG_BINANCE_API_SECRET = "binance.apiSecret";
    public static final String CONFIG_TRADING_DO_TRADES = "trading.doTrades";
    public static final String CONFIG_TRADING_STOPLOSS_PERCENTAGE = "trading.stopLossPercentage";
    public static final String CONFIG_TRADING_TRADE_SIZE_BTC =  "trading.tradeSizeBTC";
    public static final String CONFIG_TRADING_TRADE_SIZE_USDT = "trading.tradeSizeUSDT";
    public static final String CONFIG_TRADING_MAX_SIMULTANEOUS_TRADES = "trading.maxSimultaneousTrades";
    public static final String CONFIG_TRADING_DO_TRAILING_STOP = "trading.doTrailingStop";
    public static final String CONFIG_TRADING_STRATEGY = "trading.strategy";
    public static final String CONFIG_TRADING_LONG = "trading.makeLong";
    public static final String CONFIG_TRADING_SHORT = "trading.makeShort";
    public static final String CONFIG_TRADING_AVRG = "trading.makeAveraging";
    public static final String CONFIG_TRADING_BLACKLIST = "trading.blackList";
    public static final String CONFIG_TRADING_STOPNOLOSS = "trading.stopNoLoss";
    public static final String CONFIG_TRADING_WAIT_LIMIT = "trading.waitOrderMinute";
    public static final String CONFIG_TRADING_WAIT_FROZEN = "trading.frozen";
    public static final String CONFIG_TRADING_IDENT_LIMIT = "trading.indentationLimitOrderPercent";
    private static String systemConfigFilePath = "";

    private static final Logger log = LoggerFactory.getLogger(LoggingAspect.class);

    public static void setSystemConfigFilePath(String path) {
        log.info( "Setting config file path to: " + path);
        systemConfigFilePath = path;
    }

    public static String readPropertyValue(String property) throws IOException {
        if (StringUtils.isNotEmpty(systemConfigFilePath)) {
            return readPropertyValue(property, true);
        }

        return readPropertyValue(property, false);
    }

    private static String readPropertyValue(String property, boolean useExternalFile) throws IOException {
        FileInputStream file = null;

        String propertyValue = "";

        // load application's properties
        Properties configProperties = new Properties();

        // config.properties file will be in the JAR location
        String configFilePath = "./config.properties";


        if (!useExternalFile) {

        }

        if (StringUtils.isNotEmpty(systemConfigFilePath)) {
            configFilePath = systemConfigFilePath;
        }
        // load the file handle for main.properties
        try {
            file = new FileInputStream(configFilePath);
            configProperties.load(file);

            propertyValue = configProperties.getProperty(property);
        } catch (FileNotFoundException e) {
            log.info("Unable to locate config properties file", e);
        } catch (IOException e) {
            log.info( "Unable to open config properties file", e);
        } finally {
            IOUtils.closeQuietly(file);
        }

        return propertyValue;
    }

}