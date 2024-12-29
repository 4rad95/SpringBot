package org.binance.springbot.util;

import com.binance.api.client.domain.general.SymbolInfo;
import com.binance.api.client.domain.market.Candlestick;
import com.binance.api.client.domain.market.CandlestickInterval;
import com.binance.client.RequestOptions;
import com.binance.client.SyncRequestClient;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.apache.commons.lang3.StringUtils;
import org.binance.springbot.SpringBotApplication;
import org.binance.springbot.aspect.LoggingAspect;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.URL;
import java.net.URLConnection;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class BinanceUtil {

    private static String API_KEY;
    private static String API_SECRET;

    private static final Logger log = LoggerFactory.getLogger(LoggingAspect.class);

    public static List<String> getBitcoinSymbols() throws Exception {

        URL url = new URL("https://www.binance.com/fapi/v1/premiumIndex");
        URLConnection urc = url.openConnection();

        BufferedReader in = new BufferedReader(
                new InputStreamReader(urc.getInputStream()));

        String inputLine;
        inputLine = in.readLine();
        in.close();

        List<String> symbols = new LinkedList<String>();
        symbols = parserJsonMy(inputLine);

        return symbols;
    }
    public static List<String> parserJsonMy(String string) throws Exception {

        List<String> names = new ArrayList<>();

        try {
            JSONArray array = new JSONArray(string);

            for (int i = 0; i < array.length(); i++) {
                JSONObject objectInArray = array.getJSONObject(i);
                String temp = objectInArray.getString("symbol");
                if (temp.contains("USDT")) {
                    names.add(temp);
                }
            }
        } catch (Exception e) {
            System.out.print(e);
        }

        return names;

    }
    public static void init(String binanceApiKey, String binanceApiSecret) throws Exception {
        if(StringUtils.isEmpty(binanceApiKey) || StringUtils.isEmpty(binanceApiSecret)) {
            throw new Exception("Binance API params cannot be empty; please check the config properties file");
        }
        API_KEY = binanceApiKey;
        API_SECRET = binanceApiSecret;
    }

    public static String getApiKey() {
        return API_KEY;
    }

    public static String getApiSecret() {
        return API_SECRET;
    }

    public static BigDecimal printBalance() {
        try {
            RequestOptions options = new RequestOptions();
            SyncRequestClient syncRequestClient = SyncRequestClient.create(getApiKey(), getApiSecret(),
                    options);
            return syncRequestClient.getBalance().get(5).getBalance();
        } catch (Exception e) {
            log.info(e.toString());
            return BigDecimal.valueOf(0.00);
        }

    }

    public static List<Candlestick> getCandelSeries(String symbol, String interval, Integer limit) {
        try {
            URL url = new URL("https://www.binance.com/fapi/v1/klines?symbol=" + symbol + "&interval=" + interval + "&limit=" + limit);
            URLConnection urc = url.openConnection();
            BufferedReader in = new BufferedReader(
                    new InputStreamReader(urc.getInputStream()));
            String inputLine;
            inputLine = in.readLine();
            JSONArray array = new JSONArray(inputLine);
            List<Candlestick> candela = new ArrayList<>();
            for (int i = 0; i < array.length(); i++) {
                String str = String.valueOf(array.get(i)).substring(1, String.valueOf(array.get(i)).length() - 2);
                String[] strArray = str.split(",");
                Candlestick candlestick = new Candlestick();
                candlestick.setOpenTime(Long.valueOf(strArray[0]));
                candlestick.setCloseTime(Long.valueOf(strArray[6]));
                candlestick.setOpen(strArray[1]);
                candlestick.setHigh(strArray[2]);
                candlestick.setLow(strArray[3]);
                candlestick.setClose(strArray[4]);
                if (strArray[5] == "null") {
                    candlestick.setVolume(" 0.00 ");
                } else {
                    candlestick.setVolume(strArray[5]);
                }
                candlestick.setVolume(strArray[7]);

                candela.add(candlestick);
            }
            in.close();
            return candela;

        } catch (Exception ex) {
            System.out.println(" connected false " + ex);
            return null;
        }
    }
    public static List<Candlestick> getLatestCandlestickBars(String symbol,
                                                             CandlestickInterval interval) {
        try {
            return getCandelSeries(symbol, interval.getIntervalId(), 2);//getRestClient().getCandlestickBars(symbol, interval, 2,
//					null, null);
        } catch (Exception e) {
            //throw new GeneralException(e);
            System.out.print("---" + symbol);
            return null;
        }
    }

    public static int countDecimalPlaces(double number) {
        String text = Double.toString(number);
        if (text.contains(".")) {
            return text.length() - text.indexOf('.') - 1;
        }
        return 0;
    }

    public static double roundToDecimalPlaces(double value, int decimalPlaces) {
        BigDecimal bd = BigDecimal.valueOf(value);
        bd = bd.setScale(decimalPlaces, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }

    public static String getAmount(String symbol, Double price, Double usdtAmount) throws JsonProcessingException {
        // This method should be refactored... there is a method in Binance API to get symbol info
        Double rawAmount = usdtAmount / price;
        int precision =  getPrecision(SpringBotApplication.exchangeInfo,symbol);
        switch (precision) {
            case 0:
                return StringUtils.replaceAll(String.format("%.0f", rawAmount), ",", ".");
            case 1:
                return StringUtils.replaceAll(String.format("%.1f", rawAmount), ",", ".");
            case 2:
                return StringUtils.replaceAll(String.format("%.2f", rawAmount), ",", ".");
            case 3:
                return StringUtils.replaceAll(String.format("%.3f", rawAmount), ",", ".");
            case 4:
                return StringUtils.replaceAll(String.format("%.4f", rawAmount), ",", ".");
            case 5:
                return StringUtils.replaceAll(String.format("%.5f", rawAmount), ",", ".");
            case 6:
                return StringUtils.replaceAll(String.format("%.6f", rawAmount), ",", ".");
            case 7:
                return StringUtils.replaceAll(String.format("%.7f", rawAmount), ",", ".");
            default:
                return StringUtils.replaceAll(String.format("%.8f", rawAmount), ",", ".");
        }
//        if (rawAmount > 1) {
//            Integer iAmount = Integer.valueOf(rawAmount.intValue());
//            return "" + iAmount;
//        } else if (rawAmount < 1 && rawAmount >= 0.05) {
//            return StringUtils.replaceAll(String.format("%.2f", rawAmount), ",", ".");
//        } else {
//            return StringUtils.replaceAll(String.format("%.4f", rawAmount), ",", ".");
//        }

    }
    public static String convertTimestampToDate(long timestamp) {
        Instant instant = Instant.ofEpochMilli(timestamp);

        ZoneId zoneId = ZoneId.of("UTC");
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
                .withZone(zoneId);
        return formatter.format(instant);
    }
    public static String timeFormat(Long mmsec) {
        int seconds = (int) (mmsec / 1000);
        int minutes = seconds / 60;
        int hours = minutes / 60;
        minutes = minutes - hours * 60;
        seconds = seconds - minutes * 60;
        return String.format("%d:%02d:%02d", hours, minutes, seconds);
    }

    public static String getExchangeInfo() throws IOException {
        URL url = new URL("https://www.binance.com/fapi/v1/exchangeInfo");
        URLConnection urc = url.openConnection();

        BufferedReader in = new BufferedReader(
                new InputStreamReader(urc.getInputStream()));
        String inputLine;
        inputLine = in.readLine();
        in.close();
        return inputLine;
    }

    public static int getPrecision(String jsonResponse, String symbol) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        JsonNode rootNode = mapper.readTree(jsonResponse);
        JsonNode symbolsNode = rootNode.get("symbols");
        if (symbolsNode == null || !symbolsNode.isArray()) {
            throw new IllegalArgumentException("Invalid JSON: 'symbols' array not found");
        }
        for (JsonNode symbolNode : symbolsNode) {
            if (symbol.equalsIgnoreCase(symbolNode.get("symbol").asText())) {
                return symbolNode.get("pricePrecision").asInt();
            }
        }
        throw new IllegalArgumentException("Symbol not found: " + symbol);
    }
}
