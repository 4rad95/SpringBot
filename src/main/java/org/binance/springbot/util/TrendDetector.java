package org.binance.springbot.util;


import org.binance.springbot.SpringBotApplication;
import org.ta4j.core.*;
import org.ta4j.core.indicators.*;
import org.ta4j.core.indicators.helpers.ClosePriceIndicator;
import org.ta4j.core.num.Num;

import java.time.ZonedDateTime;
import java.util.*;

import static org.binance.springbot.SpringBotApplication.interval1;

public class TrendDetector {

    public static class Extremum {
        public String type;

        public int index;
        public double value;
        public ZonedDateTime time;

        public Extremum(String type, int index, double value, ZonedDateTime time) {
            this.type = type;

            this.index = index;
            this.value = value;
            this.time = time;
        }

        @Override
        public String toString() {
            return "Extremum{" +
                    "type='" + type + '\'' +
                    ", index=" + index +
                    ", value=" + value +
                    ", time=" + time +
                    '}';
        }
    }

    public static class TrendResult {
        public String trend;
        public int typeD;
        public String name;
        public List<Extremum> lowExtremes;
        public List<Extremum> highExtremes;

        public TrendResult(String trend,String name, int typeD, List<Extremum> lowExtremes, List<Extremum> highExtremes) {
            this.trend = trend;
            this.name = name;
            this.typeD = typeD;
            this.lowExtremes = lowExtremes;
            this.highExtremes = highExtremes;
        }

        @Override
        public String toString() {
            return  "TrendResult{" + name +
                    "   trend='" + trend + '\n' +
                    ", lowExtremes=" + lowExtremes + '\n'+
                    ", highExtremes=" + highExtremes + '\n'+
                    '}';
        }
    }
    public static Integer detectTrendWithMA25(BarSeries series){
        ClosePriceIndicator closePrice = new ClosePriceIndicator(series);

        EMAIndicator smaIndicator = new EMAIndicator(closePrice, 25);
        detectTrendWithStochRSI(series);
        if (smaIndicator.getValue(series.getEndIndex()).isLessThan(smaIndicator.getValue(series.getEndIndex()-1))) {
            return -1;
        }
        if (smaIndicator.getValue(series.getEndIndex()).isGreaterThan(smaIndicator.getValue(series.getEndIndex()-1))) {
            return 1;}
        return 0;
    }

    public static int detectTrendWithStochRSI(BarSeries series) {
        ClosePriceIndicator closePrice = new ClosePriceIndicator(series);
        RSIIndicator rsiIndicator = new RSIIndicator(closePrice,14);
        StochasticRSIIndicator stochRSIK = new StochasticRSIIndicator(rsiIndicator, 14);
        StochasticOscillatorDIndicator stochRSID = new StochasticOscillatorDIndicator(stochRSIK);
        SMAIndicator smaStochK = new SMAIndicator(stochRSIK,3);
        SMAIndicator smaStochD = new SMAIndicator(smaStochK,3);
        System.out.println( series.getName() + "  K=" + smaStochK.getValue(series.getEndIndex()) +"    D="+smaStochD.getValue(series.getEndIndex()) );
        if ((smaStochK.getValue(series.getEndIndex()).doubleValue() > 0.70) && (smaStochK.getValue(series.getEndIndex()).isLessThan(smaStochD.getValue(series.getEndIndex())))){
            return -1; }
        if ((smaStochK.getValue(series.getEndIndex()).doubleValue() < 0.30) && (smaStochK.getValue(series.getEndIndex()).isGreaterThan(smaStochD.getValue(series.getEndIndex())))){
            return 1; }
        return 0;
    }

    public static int trendDetect(String symbol) {
        BarSeries series = BinanceTa4jUtils.convertToTimeSeries(
                Objects.requireNonNull(BinanceUtil.getCandelSeries(symbol, SpringBotApplication.interval1.getIntervalId(), 100))
                , symbol, interval1.getIntervalId());
        ClosePriceIndicator closePrice = new ClosePriceIndicator(series);
        RSIIndicator rsiIndicator = new RSIIndicator(closePrice,14);
        StochasticRSIIndicator stochRSIK = new StochasticRSIIndicator(rsiIndicator, 14);
        SMAIndicator smaStochK = new SMAIndicator(stochRSIK,3);
        SMAIndicator smaStochD = new SMAIndicator(smaStochK,3);
        System.out.println( series.getName() + "  K=" + smaStochK.getValue(series.getEndIndex()) +"    D="+smaStochD.getValue(series.getEndIndex()) );

        int shortPeriod = 12; // Короткий EMA
        int longPeriod = 26;  // Длинный EMA
        int signalPeriod = 9; // Период сигнальной линии

        // MACD индикатор
        MACDIndicator macd = new MACDIndicator(closePrice, shortPeriod, longPeriod);

        // Сигнальная линия (SMA на MACD)
        SMAIndicator signalLine = new SMAIndicator(macd, signalPeriod);
        Num histogram = macd.getValue(series.getEndIndex()).minus(signalLine.getValue(series.getEndIndex()));
        System.out.println(String.format("%.4f",macd.getValue(series.getEndIndex()-1).minus(signalLine.getValue(series.getEndIndex()-1)).doubleValue()) + " , " +String.format("%.4f",histogram.doubleValue()) +
                "   diff = " + String.format("%.4f",histogram.doubleValue()-macd.getValue(series.getEndIndex()-1).minus(signalLine.getValue(series.getEndIndex()-1)).doubleValue()));

        if (((smaStochK.getValue(series.getEndIndex()).doubleValue() > 0) && (smaStochK.getValue(series.getEndIndex()).isLessThan(smaStochD.getValue(series.getEndIndex()))))
        || macd.getValue(series.getEndIndex()).isLessThan(signalLine.getValue(series.getEndIndex())))
        {
            return -1; }
        if ((smaStochK.getValue(series.getEndIndex()).doubleValue() < 1) && (smaStochK.getValue(series.getEndIndex()).isGreaterThan(smaStochD.getValue(series.getEndIndex())))
                || macd.getValue(series.getEndIndex()).isGreaterThan(signalLine.getValue(series.getEndIndex())))
        {
            return 1; }
        return 0;
    }

    public static TrendResult detectTrendWithExtremes(BarSeries series, int range, int filterRange) {
        List<Extremum> allMinima = new ArrayList<>();
        List<Extremum> allMaxima = new ArrayList<>();
        String name = series.getName();
        int endIndex = series.getEndIndex();
        boolean[] excluded = new boolean[endIndex + 1];
        if (endIndex < range) {
            return new TrendResult("Insufficient Data",name,0, new ArrayList<>(),new ArrayList<>());
        }

        // Сбор всех минимумов и максимумов
        for (int i = endIndex - range + 1; i <= endIndex; i++) {
            if (excluded[i]) {
                continue; // Пропускаем свечи, попадающие в фильтр
            }
            double currentHigh = series.getBar(i).getHighPrice().doubleValue();
            double currentLow = series.getBar(i).getLowPrice().doubleValue();
            ZonedDateTime currentTime = series.getBar(i).getEndTime();

            if (isSwingHigh(series, i)) {
                allMaxima.add(new Extremum("High", i, currentHigh, currentTime));
                excludeRange(excluded, i, filterRange);
            }
            if (isSwingLow(series, i)) {
                allMinima.add(new Extremum("Low", i, currentLow, currentTime));
                excludeRange(excluded, i, filterRange);
            }
        }

        // Сортируем минимумы и максимумы по значению
        allMinima.sort(Comparator.comparingDouble(e -> e.value));
        allMaxima.sort((e1, e2) -> Double.compare(e2.value, e1.value)); // Обратная сортировка для максимумов

        // Берем 3 самых значимых экстремума
        List<Extremum> keyMinima = allMinima.subList(0, Math.min(3, allMinima.size()));
        List<Extremum> keyMaxima = allMaxima.subList(0, Math.min(3, allMaxima.size()));

        // Определяем тренд
        String trend = determineTrend(keyMinima, keyMaxima);
        int typeD = 0;
        if (trend == "Uptrend")  {typeD = 1;}
        if (trend == "Downtrend") {typeD = -1;}
        // Объединяем ключевые экстремумы
        List<Extremum> lowExtremes = new ArrayList<>();
        lowExtremes.addAll(keyMinima);
        List<Extremum> highExtremes = new ArrayList<>();
        highExtremes.addAll(keyMaxima);

        // Возвращаем результат
        return new TrendResult(trend, name, typeD, lowExtremes, highExtremes);
    }

    private static void excludeRange(boolean[] excluded, int index, int filterRange) {
        for (int i = Math.max(0, index - filterRange); i <= Math.min(excluded.length - 1, index + filterRange); i++) {
            excluded[i] = true;
        }
    }
    private static boolean isSwingHigh(BarSeries series, int index) {
        if (index < 2 || index > series.getEndIndex() - 2) {
            return false;
        }
        double currentHigh = series.getBar(index).getHighPrice().doubleValue();
        double leftHigh = series.getBar(index - 1).getHighPrice().doubleValue();
        double rightHigh = series.getBar(index + 1).getLowPrice().doubleValue();
        return currentHigh > leftHigh && currentHigh > rightHigh;
    }

    private static boolean isSwingLow(BarSeries series, int index) {
        if (index < 2 || index > series.getEndIndex() - 2) {
            return false;
        }
        double currentLow = series.getBar(index).getLowPrice().doubleValue();
        double leftLow = series.getBar(index - 1).getHighPrice().doubleValue();
        double rightLow = series.getBar(index + 1).getLowPrice().doubleValue();
        return currentLow < leftLow && currentLow < rightLow;
    }

    private static String determineTrend(List<Extremum> minima, List<Extremum> maxima) {
        if (minima.isEmpty() || maxima.isEmpty()) {
            return "Undefined";
        }

        // Сравниваем индексы ключевых минимумов и максимумов
        int minIndex = minima.get(0).index;
        int maxIndex = maxima.get(0).index;

        if (minIndex < maxIndex) {
            return "Uptrend";
        } else {
            return "Downtrend";
        }
    }
}