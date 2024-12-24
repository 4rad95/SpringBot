package org.binance.springbot.util;

import com.binance.api.client.domain.market.Candlestick;
import org.ta4j.core.*;
import org.ta4j.core.Bar;
import org.ta4j.core.BaseBar;
import org.ta4j.core.indicators.ATRIndicator;
import org.ta4j.core.indicators.RSIIndicator;
import org.ta4j.core.indicators.SMAIndicator;
import org.ta4j.core.indicators.StochasticRSIIndicator;
import org.ta4j.core.indicators.helpers.*;
import org.ta4j.core.num.DoubleNum;
import org.ta4j.core.num.Num;

import java.time.Duration;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.LinkedList;
import java.util.List;

import static org.binance.springbot.SpringBotApplication.timeSeriesCache;

//import static org.binance.springbot.SpringBotApplication.timeSeriesCache;


public class BinanceTa4jUtils {


    public static BarSeries convertToTimeSeries(
            List<Candlestick> candlesticks, String symbol, String period) {
        List<Bar> ticks = new LinkedList<Bar>();
        for (Candlestick candlestick : candlesticks) {
            ticks.add(convertToTa4jTick(candlestick));
        }
        return new BaseBarSeries(symbol + "_" + period, ticks);
    }

    public static Bar convertToTa4jTick(Candlestick candlestick) {
        Num zero = DoubleNum.valueOf(0);

        ZonedDateTime closeTime = getZonedDateTime(candlestick.getCloseTime());
        Duration candleDuration = Duration.ofMillis(candlestick.getCloseTime() - candlestick.getOpenTime());
        Num openPrice = DoubleNum.valueOf(
                candlestick.getOpen().substring(1, candlestick.getOpen().length() - 2)
        );
        Num closePrice = DoubleNum.valueOf(
                candlestick.getClose().substring(1, candlestick.getClose().length() - 2)
        );
        Num highPrice = DoubleNum.valueOf(
                candlestick.getHigh().substring(1, candlestick.getHigh().length() - 2)
        );
        Num lowPrice = DoubleNum.valueOf(
                candlestick.getLow().substring(1, candlestick.getLow().length() - 2)
        );
        Num volume;
        Num amount;

        try {
            volume = DoubleNum.valueOf(
                    candlestick.getVolume().substring(1, candlestick.getVolume().length() - 2)
            );
        } catch (Exception e) {
            volume = zero;
        }

        try {
            amount = DoubleNum.valueOf(
                    candlestick.getQuoteAssetVolume().substring(1, candlestick.getQuoteAssetVolume().length() - 2)
            );
        } catch (Exception e) {
            amount = zero;
        }
        return new BaseBar(candleDuration, closeTime, openPrice, highPrice, lowPrice, closePrice, volume, amount);
    }

    public static ZonedDateTime getZonedDateTime(Long timestamp) {
        return ZonedDateTime.ofInstant(Instant.ofEpochMilli(timestamp),
                ZoneId.systemDefault());
    }

    public static boolean isSameTick(Candlestick candlestick, Bar tick) {
        return tick.getEndTime().equals(
                getZonedDateTime(candlestick.getCloseTime()));
    }
//
//    public static Boolean checkStrategyLong(TimeSeries series) {
//        if (series == null) {
//            throw new IllegalArgumentException("Series cannot be null");
//        }
//        ClosePriceIndicator closePrice = new ClosePriceIndicator(series);
//
//        PlusDMIndicator plusDM = new PlusDMIndicator(series);
//        MinusDMIndicator minusDM = new MinusDMIndicator(series);
//        SMAIndicator smoothedPlusDM = new SMAIndicator(plusDM, 14);
//        SMAIndicator smoothedMinusDM = new SMAIndicator(minusDM, 14);
//
//        RSIIndicator rsi = new RSIIndicator(closePrice, 14);
//        StochasticRSIIndicator stochRsi = new StochasticRSIIndicator(rsi, 14);
//        SMAIndicator smoothedStochRsi = new SMAIndicator(stochRsi, 3);
//        SMAIndicator stochRsiD = new SMAIndicator(smoothedStochRsi, 3);
//        int maxIndex = series.getEndIndex();
//        ATRIndicator atr = new ATRIndicator(series, 14);
//        if (((smoothedPlusDM.getValue(maxIndex).doubleValue() / atr.getValue(maxIndex).doubleValue()) * 100 < (smoothedMinusDM.getValue(maxIndex).doubleValue() / atr.getValue(maxIndex).doubleValue()) * 100)
//                && ((stochRsiD.getValue(maxIndex).doubleValue() < smoothedStochRsi.getValue(maxIndex).doubleValue()))) {
//    //        log.info("[LONG]:" + series.getName() + "  Ok!");
//            return true;
//        }
//    //    Log.info(BinanceTa4jUtils.class, "[LONG]:" + series.getName() + "  Cancel!");
//        return false;
//    }
//
//
//    public static Boolean checkStrategyShort(TimeSeries series) {
//        if (series == null) {
//            throw new IllegalArgumentException("Series cannot be null");
//        }
//        ClosePriceIndicator closePrice = new ClosePriceIndicator(series);
//        RSIIndicator rsi = new RSIIndicator(closePrice, 14);
//        StochasticRSIIndicator stochRsi = new StochasticRSIIndicator(rsi, 14);
//        SMAIndicator smoothedStochRsi = new SMAIndicator(stochRsi, 3);
//        SMAIndicator stochRsiD = new SMAIndicator(smoothedStochRsi, 3);
//        int maxIndex = series.getEndIndex();
//        PlusDMIndicator plusDM = new PlusDMIndicator(series);
//        MinusDMIndicator minusDM = new MinusDMIndicator(series);
//        SMAIndicator smoothedPlusDM = new SMAIndicator(plusDM, 14);
//        SMAIndicator smoothedMinusDM = new SMAIndicator(minusDM, 14);
//        ATRIndicator atr = new ATRIndicator(series, 14);
//
//        if (((smoothedPlusDM.getValue(maxIndex).doubleValue() / atr.getValue(maxIndex).doubleValue()) * 100 < (smoothedMinusDM.getValue(maxIndex).doubleValue() / atr.getValue(maxIndex).doubleValue()) * 100)
//                && (stochRsiD.getValue(maxIndex).doubleValue() > smoothedStochRsi.getValue(maxIndex).doubleValue())) {
//
//    //        log.info("[SHORT]:" + series.getName() + "  Ok!");
//            return true;
//        }
////        Log.info(BinanceTa4jUtils.class, "[SHORT]:" + series.getName() + "  Cancel!");
//        return false;
//    }
//
//    public static Decimal getATR(TimeSeries series) {
//        if (series == null) {
//            throw new IllegalArgumentException("Series cannot be null");
//        }
//        ATRIndicator atrIndicator = new ATRIndicator(series, 14);
//
//        return atrIndicator.getValue(series.getEndIndex());
//    }
//
//    public static Double getStopPriceLong(TimeSeries series) {
//        int currentIndex = series.getEndIndex();
//
//        // Проходим серию с конца, ищем локальный минимум
//        for (int i = currentIndex - 1; i >= 1; i--) {
//            double prevLow = series.getBar(i - 1).getMinPrice().doubleValue();
//            double currentLow = series.getBar(i).getMinPrice().doubleValue();
//            double nextLow = series.getBar(i + 1).getMinPrice().doubleValue();
//
//            // Проверяем условие локального минимума
//            if (currentLow < prevLow && currentLow < nextLow) {
//                return currentLow;
//            }
//        }
//
//        MinPriceIndicator minPriceIndicator = new MinPriceIndicator(series);
//        return minPriceIndicator.getValue(series.getEndIndex() - 1).doubleValue();
//
//
//
//    }
//
//    public static Double getEnterPriceShort(TimeSeries series) {
//        int currentIndex = series.getEndIndex();
//
//        // Проходим серию с конца, ищем локальный максимум
//        for (int i = currentIndex - 1; i >= 1; i--) {
//            double prevHigh = series.getBar(i - 1).getClosePrice().doubleValue();
//            double currentHigh = series.getBar(i).getClosePrice().doubleValue();
//            double nextHigh = series.getBar(i + 1).getClosePrice().doubleValue();
//
//            // Проверяем условие локального максимума
//            if (currentHigh > prevHigh && currentHigh > nextHigh) {
//                return currentHigh;
//            }
//        }
//
//        MaxPriceIndicator maxPriceIndicator = new MaxPriceIndicator(series);
//
//        return maxPriceIndicator.getValue(series.getEndIndex() - 1).doubleValue();
//    }
//    public static Double getEnterPriceLong(TimeSeries series) {
//        int currentIndex = series.getEndIndex();
//
//        // Проходим серию с конца, ищем локальный минимум
//        for (int i = currentIndex - 1; i >= 1; i--) {
//            double prevLow = series.getBar(i - 1).getClosePrice().doubleValue();
//            double currentLow = series.getBar(i).getClosePrice().doubleValue();
//            double nextLow = series.getBar(i + 1).getClosePrice().doubleValue();
//
//            // Проверяем условие локального минимума
//            if (currentLow < prevLow && currentLow < nextLow) {
//                return currentLow;
//            }
//        }
//
//        MinPriceIndicator minPriceIndicator = new MinPriceIndicator(series);
//        return minPriceIndicator.getValue(series.getEndIndex() - 1).doubleValue();
//
//
//
//    }
//
//    public static Double getStopPriceShort(TimeSeries series) {
//        int currentIndex = series.getEndIndex();
//
//        // Проходим серию с конца, ищем локальный максимум
//        for (int i = currentIndex - 1; i >= 1; i--) {
//            double prevHigh = series.getBar(i - 1).getMaxPrice().doubleValue();
//            double currentHigh = series.getBar(i).getMaxPrice().doubleValue();
//            double nextHigh = series.getBar(i + 1).getMaxPrice().doubleValue();
//
//            // Проверяем условие локального максимума
//            if (currentHigh > prevHigh && currentHigh > nextHigh) {
//                return currentHigh;
//            }
//        }
//
//        MaxPriceIndicator maxPriceIndicator = new MaxPriceIndicator(series);
//
//        return maxPriceIndicator.getValue(series.getEndIndex() - 1).doubleValue();
//    }
//    public static double findPreviousHigh(TimeSeries series) {
//        int currentIndex = series.getEndIndex();
//        int count = 0; // Счетчик найденных максимумов
//
//        // Проходим серию с конца, ищем локальные максимумы
//        for (int i = currentIndex - 1; i >= 1; i--) {
//            double prevHigh = series.getBar(i - 1).getMaxPrice().doubleValue();
//            double currentHigh = series.getBar(i).getMaxPrice().doubleValue();
//            double nextHigh = series.getBar(i + 1).getMaxPrice().doubleValue();
//
//            // Проверка на локальный максимум
//            if (currentHigh > prevHigh && currentHigh > nextHigh) {
//                count++;
//                // Если найден второй максимум, возвращаем его
//                if (count == 2) {
//                    return currentHigh;
//                }
//            }
//        }
//
//        // Если предпоследний максимум не найден, возвращаем null
//        return Double.NaN;
//    }
//
//
//    // Метод для поиска предыдущего локального минимума
//    public static double findPreviousLow(TimeSeries series) {
//        int currentIndex = series.getEndIndex();
//        int count = 0; // Счетчик найденных минимумов
//
//        // Проходим серию с конца, ищем локальные минимумы
//        for (int i = currentIndex - 1; i >= 1; i--) {
//            double prevLow = series.getBar(i - 1).getMinPrice().doubleValue();
//            double currentLow = series.getBar(i).getMinPrice().doubleValue();
//            double nextLow = series.getBar(i + 1).getMinPrice().doubleValue();
//
//            // Проверка на локальный минимум
//            if (currentLow < prevLow && currentLow < nextLow) {
//                count++;
//                // Если найден второй минимум, возвращаем его
//                if (count == 2) {
//                    return currentLow;
//                }
//            }
//        }
//        return Double.NaN;
//    }

public static Num getCurrentPrice(String symbol) {

    BarSeries series = timeSeriesCache.get(symbol);
    Num currentPrice = series.getLastBar().getClosePrice();
    return currentPrice;
}
}
