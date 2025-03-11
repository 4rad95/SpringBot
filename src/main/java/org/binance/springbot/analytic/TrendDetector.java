package org.binance.springbot.analytic;


import org.ta4j.core.*;
import org.ta4j.core.indicators.*;
import org.ta4j.core.indicators.helpers.ClosePriceIndicator;
import org.ta4j.core.num.Num;

import java.time.ZonedDateTime;
import java.util.*;

import static org.binance.springbot.analytic.OrderBlockFinder.findAllOrderBlocks;

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
        if ((smaStochK.getValue(series.getEndIndex()).doubleValue() > 0.75)
             //   && (smaStochK.getValue(series.getEndIndex()).isLessThan(smaStochD.getValue(series.getEndIndex())))
                &&  rsiIndicator.getValue(series.getEndIndex()).doubleValue() > 70){
            return -1; }
        if ((smaStochK.getValue(series.getEndIndex()).doubleValue() < 0.25)
           //     && (smaStochK.getValue(series.getEndIndex()).isGreaterThan(smaStochD.getValue(series.getEndIndex())))
                &&  rsiIndicator.getValue(series.getEndIndex()).doubleValue() <30){
            return 1; }
        return 0;
    }

    public static int trendDetect(BarSeries series) {
        //BarSeries series = BinanceUtil.getSeriesT1(symbol);
        ClosePriceIndicator closePrice = new ClosePriceIndicator(series);
        RSIIndicator rsiIndicator = new RSIIndicator(closePrice,14);
        StochasticRSIIndicator stochRSIK = new StochasticRSIIndicator(rsiIndicator, 14);
        SMAIndicator smaStochK = new SMAIndicator(stochRSIK,3);
        SMAIndicator smaStochD = new SMAIndicator(smaStochK,3);


        Indicator<Num> shortEma = new EMAIndicator(closePrice, 25); // Короткая EMA
        Indicator<Num> longEma = new EMAIndicator(closePrice, 50); // Длинная EMA

        boolean isBullishTrend = shortEma.getValue(series.getEndIndex()).isGreaterThan(longEma.getValue(series.getEndIndex()));
        boolean isBearishTrend = shortEma.getValue(series.getEndIndex()).isLessThan(longEma.getValue(series.getEndIndex()));

        int shortPeriod = 12;
        int longPeriod = 26;
        int signalPeriod = 9;

        MACDIndicator macd = new MACDIndicator(closePrice, shortPeriod, longPeriod);

        SMAIndicator signalLine = new SMAIndicator(macd, signalPeriod);
        Num histogram = macd.getValue(series.getEndIndex()).minus(signalLine.getValue(series.getEndIndex()));
        Double diff = histogram.doubleValue()-macd.getValue(series.getEndIndex()-1).minus(signalLine.getValue(series.getEndIndex()-1)).doubleValue();

        if (//(smaStochK.getValue(series.getEndIndex()).doubleValue() < 0.7)
            // macd.getValue(series.getEndIndex()).isLessThan(signalLine.getValue(series.getEndIndex()))
             diff < 0
             && isBearishTrend)
        {
            System.out.print("\u001B[31m" + series.getName() + "  K=" + smaStochK.getValue(series.getEndIndex()) +"    D="+smaStochD.getValue(series.getEndIndex()) + "  ");
            System.out.println( " TREND_DOWN");
            System.out.print("\u001B[0m");
            return -1; }
        if (//(smaStochK.getValue(series.getEndIndex()).doubleValue() > 0.3)
               // macd.getValue(series.getEndIndex()).isGreaterThan(signalLine.getValue(series.getEndIndex()))
                diff > 0
                && isBullishTrend)
             {
            System.out.print( "\u001B[32m" +series.getName() + "  K=" + smaStochK.getValue(series.getEndIndex()) +"    D="+smaStochD.getValue(series.getEndIndex()) + "  ");
            System.out.println( " TREND_UP");
            System.out.print("\u001B[0m");
            return 1; }

        return 0;
    }

    public static int trendDetectFull(BarSeries series) {
        //BarSeries series = BinanceUtil.getSeriesT1(symbol);
        ClosePriceIndicator closePrice = new ClosePriceIndicator(series);
        RSIIndicator rsiIndicator = new RSIIndicator(closePrice,14);
        StochasticRSIIndicator stochRSIK = new StochasticRSIIndicator(rsiIndicator, 14);
        SMAIndicator smaStochK = new SMAIndicator(stochRSIK,3);
        SMAIndicator smaStochD = new SMAIndicator(smaStochK,3);


        Indicator<Num> shortEma = new EMAIndicator(closePrice, 25); // Короткая EMA
        Indicator<Num> longEma = new EMAIndicator(closePrice, 50); // Длинная EMA

        boolean isBullishTrend = shortEma.getValue(series.getEndIndex()).isGreaterThan(longEma.getValue(series.getEndIndex()));
        boolean isBearishTrend = shortEma.getValue(series.getEndIndex()).isLessThan(longEma.getValue(series.getEndIndex()));

        int shortPeriod = 12;
        int longPeriod = 26;
        int signalPeriod = 9;

        MACDIndicator macd = new MACDIndicator(closePrice, shortPeriod, longPeriod);

        SMAIndicator signalLine = new SMAIndicator(macd, signalPeriod);
        Num histogram = macd.getValue(series.getEndIndex()).minus(signalLine.getValue(series.getEndIndex()));
        Double diff = histogram.doubleValue()-macd.getValue(series.getEndIndex()-1).minus(signalLine.getValue(series.getEndIndex()-1)).doubleValue();
        {
            //-- Print
            if (isBullishTrend) { System.out.print(String.format("%-20s%-7s","\u001B[32m"+ series.getName(),"  SMA  "));  }
            else { System.out.print(String.format("%-20s%-7s","\u001B[31m"+ series.getName(),"  SMA  "));  }
            if (smaStochK.getValue(series.getEndIndex()).doubleValue() > smaStochD.getValue(series.getEndIndex()).doubleValue()) { System.out.print("\u001B[32m"+ "StochRSI   ");  }
            else { System.out.print("\u001B[31m"+ "StochRSI   ");  }
//            if (smaStochK.getValue(series.getEndIndex()).doubleValue() < 0.5) { System.out.print("\u001B[32m"+ " StochRSI UP ");  }
//            else { System.out.print("\u001B[31m"+ " StochRSI Down ");  }
            if ( macd.getValue(series.getEndIndex()).isGreaterThan(signalLine.getValue(series.getEndIndex()))) { System.out.print("\u001B[32m"+ "MACD   ");  }
            else { System.out.print("\u001B[31m"+ "MACD   ");  }
            if (diff.doubleValue() > 0) { System.out.print("\u001B[32m"+ "diff MACD   ");  }
            else { System.out.print("\u001B[31m"+ "diff MACD   ");  }
            System.out.print("\u001B[0m");
        }


        if (//smaStochK.getValue(series.getEndIndex()).doubleValue() < smaStochD.getValue(series.getEndIndex()).doubleValue()
          diff.doubleValue() <0
  //           &&   (smaStochK.getValue(series.getEndIndex()).doubleValue() > 0.5)
              //macd.getValue(series.getEndIndex()).isLessThan(signalLine.getValue(series.getEndIndex()))
             &&   isBearishTrend)
        {
      //      System.out.print("\u001B[31m" + series.getName() + "  K=" + smaStochK.getValue(series.getEndIndex()) +"    D="+smaStochD.getValue(series.getEndIndex()) + "  ");
            System.out.println("\u001B[31m" +  " TREND_DOWN");
            System.out.print("\u001B[0m");
            return -1; }
        if (//smaStochK.getValue(series.getEndIndex()).doubleValue() > smaStochD.getValue(series.getEndIndex()).doubleValue()
 //            &&  (smaStochK.getValue(series.getEndIndex()).doubleValue() < 0.5)
                diff.doubleValue() >0
              //  macd.getValue(series.getEndIndex()).isGreaterThan(signalLine.getValue(series.getEndIndex()))
             &&   isBullishTrend)
        {
    //        System.out.print( "\u001B[32m" +series.getName() + "  K=" + smaStochK.getValue(series.getEndIndex()) +"    D="+smaStochD.getValue(series.getEndIndex()) + "  ");
            System.out.println("\u001B[32m" + " TREND_UP");
            System.out.print("\u001B[0m");
            return 1; }
        System.out.println("\u001B[0m");
        return 0;
    }

    public static int trendDetectLight(BarSeries series) {
        //BarSeries series = BinanceUtil.getSeriesT1(symbol);
        ClosePriceIndicator closePrice = new ClosePriceIndicator(series);
        RSIIndicator rsiIndicator = new RSIIndicator(closePrice,14);
        StochasticRSIIndicator stochRSIK = new StochasticRSIIndicator(rsiIndicator, 14);
        SMAIndicator smaStochK = new SMAIndicator(stochRSIK,3);
        SMAIndicator smaStochD = new SMAIndicator(smaStochK,3);


        Indicator<Num> shortEma = new EMAIndicator(closePrice, 25); // Короткая EMA
        Indicator<Num> longEma = new EMAIndicator(closePrice, 50); // Длинная EMA

        boolean isBullishTrend = shortEma.getValue(series.getEndIndex()).isGreaterThan(longEma.getValue(series.getEndIndex()));
        boolean isBearishTrend = shortEma.getValue(series.getEndIndex()).isLessThan(longEma.getValue(series.getEndIndex()));

        int shortPeriod = 12;
        int longPeriod = 26;
        int signalPeriod = 9;

        MACDIndicator macd = new MACDIndicator(closePrice, shortPeriod, longPeriod);

        SMAIndicator signalLine = new SMAIndicator(macd, signalPeriod);
        Num histogram = macd.getValue(series.getEndIndex()).minus(signalLine.getValue(series.getEndIndex()));
        Double diff = histogram.doubleValue()-macd.getValue(series.getEndIndex()-1).minus(signalLine.getValue(series.getEndIndex()-1)).doubleValue();
        {
            //-- Print
            if (isBullishTrend) { System.out.print(String.format("%-20s%-7s","\u001B[32m"+ series.getName(),"  SMA  "));  }
            else { System.out.print(String.format("%-20s%-7s","\u001B[31m"+ series.getName(),"  SMA  "));  }
            if (smaStochK.getValue(series.getEndIndex()).doubleValue() > smaStochD.getValue(series.getEndIndex()).doubleValue()) { System.out.print("\u001B[32m"+ "StochRSI   ");  }
            else { System.out.print("\u001B[31m"+ "StochRSI   ");  }
//            if (smaStochK.getValue(series.getEndIndex()).doubleValue() < 0.5) { System.out.print("\u001B[32m"+ " StochRSI UP ");  }
//            else { System.out.print("\u001B[31m"+ " StochRSI Down ");  }
            if ( macd.getValue(series.getEndIndex()).isGreaterThan(signalLine.getValue(series.getEndIndex()))) { System.out.print("\u001B[32m"+ "MACD   ");  }
            else { System.out.print("\u001B[31m"+ "MACD   ");  }
            System.out.print("\u001B[0m");
        }


        if (//smaStochK.getValue(series.getEndIndex()).doubleValue() < smaStochD.getValue(series.getEndIndex()).doubleValue()
                //           &&   (smaStochK.getValue(series.getEndIndex()).doubleValue() > 0.5)
            //    && macd.getValue(series.getEndIndex()).isLessThan(signalLine.getValue(series.getEndIndex()))
                   isBearishTrend)
        {
            //      System.out.print("\u001B[31m" + series.getName() + "  K=" + smaStochK.getValue(series.getEndIndex()) +"    D="+smaStochD.getValue(series.getEndIndex()) + "  ");
            System.out.println("\u001B[31m" +  " TREND_DOWN");
            System.out.print("\u001B[0m");
            return -1; }
        if (//smaStochK.getValue(series.getEndIndex()).doubleValue() > smaStochD.getValue(series.getEndIndex()).doubleValue()
                //            &&  (smaStochK.getValue(series.getEndIndex()).doubleValue() < 0.5)
             //   &&   macd.getValue(series.getEndIndex()).isGreaterThan(signalLine.getValue(series.getEndIndex()))
                   isBullishTrend)
        {
            //        System.out.print( "\u001B[32m" +series.getName() + "  K=" + smaStochK.getValue(series.getEndIndex()) +"    D="+smaStochD.getValue(series.getEndIndex()) + "  ");
            System.out.println("\u001B[32m" + " TREND_UP");
            System.out.print("\u001B[0m");
            return 1; }
        System.out.println("\u001B[0m");
        return 0;
    }


    public static int detectTrendWithOB(BarSeries series) {
        Map<Integer, OrderBlock> allOrderBlocks  = findAllOrderBlocks(series);
        Integer[] keys = allOrderBlocks.keySet().toArray(new Integer[0]);
        OrderBlock[] values = allOrderBlocks.values().toArray(new OrderBlock[0]);
        if (values[0].getMove() < 0) {
            double min1 = values[1].getBar().getLowPrice().doubleValue();
            double min2 = values[3].getBar().getLowPrice().doubleValue();
            if (min1 < min2) { return  -1; }
            else return 1;
        }
        if (values[0].getMove() > 0) {
            double max1 = values[1].getBar().getHighPrice().doubleValue();
            double max2 = values[3].getBar().getHighPrice().doubleValue();
            if (max1 > max2) { return  1; }
            else return -1;
        }
        return 0;
    }


}