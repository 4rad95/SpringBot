package org.binance.springbot.analytic;

import com.binance.api.client.domain.market.Candlestick;
import org.ta4j.core.Bar;
import org.ta4j.core.BarSeries;


public class CandellaAnalyse {


    public CandellaAnalyse() {
    }

//    public static double trendDetect(BarSeries series, String LowLimit, String HighLimit) {
//        int i = series.getEndIndex()-1;
//
//        double volumeMax = 0;
//        while (true) {
//            if (series.getBar(i).getHighPrice().doubleValue() == Double.valueOf(HighLimit)
//                && series.getBar(i).getLowPrice().doubleValue() == Double.valueOf(LowLimit)
//            ){
//                break;
//
//            }
//            if (series.getBar(i).getOpenPrice().doubleValue() < series.getBar(i).getClosePrice().doubleValue())   //Long
//            { if (bullIndex == 0 || getSpread(series.getBar(i)) > getSpread(series.getBar(bullIndex)))
//                { bullIndex = i;}
//                }
//            if (series.getBar(i).getOpenPrice().doubleValue() > series.getBar(i).getClosePrice().doubleValue())   // Short
//            {   if (bearIndex == 0 || getSpread(series.getBar(i)) > getSpread(series.getBar(bearIndex)))
//                { bearIndex = i;}}
//
//         i--;
//
//    }
//        if (series.getBar(bearIndex).getVolume().doubleValue() > series.getBar(bullIndex).getVolume().doubleValue()) {return -1;}
//        if (series.getBar(bearIndex).getVolume().doubleValue() < series.getBar(bullIndex).getVolume().doubleValue()) {return 1;}
//        return 0;
//    }
//        private static double getSpread(Bar bar) {
//            return  Math.abs(bar.getOpenPrice().doubleValue()-bar.getClosePrice().doubleValue());
//        }
//


    public static double trendDetectHigh(BarSeries series, String LowLimit, String HighLimit) {
        int i = series.getEndIndex()-1;
        int endIndex = i;
        int startIndex = 0;
        int highIndex = 0;
        while (true) {
            if (series.getBar(i).getHighPrice().doubleValue() == Double.valueOf(HighLimit)
                    && series.getBar(i).getLowPrice().doubleValue() == Double.valueOf(LowLimit)
            ){
                startIndex = i;
                break;
            }
            if (series.getBar(i).getHighPrice().doubleValue() > series.getBar(highIndex).getHighPrice().doubleValue()) {
                highIndex = i;
            }
            i--;
        }

        if (highIndex-startIndex <  series.getEndIndex()-1 - highIndex ) {return 1;}
        if (highIndex-startIndex >  series.getEndIndex()-1 - highIndex) {return - 1;}
        return 0;
    }

    public static double trendDetectLow(BarSeries series, String LowLimit, String HighLimit) {
        int i = series.getEndIndex()-1;
        int endIndex = i;
        int startIndex = 0;
        int lowIndex = 0;

        while (true) {
            if (series.getBar(i).getHighPrice().doubleValue() == Double.valueOf(HighLimit)
                    && series.getBar(i).getLowPrice().doubleValue() == Double.valueOf(LowLimit)
            ){
                startIndex = i;
                break;
            }
            if (series.getBar(i).getLowPrice().doubleValue() < series.getBar(lowIndex).getHighPrice().doubleValue()) {
                lowIndex = i;
            }
            i--;
        }

        if (lowIndex-startIndex <  series.getEndIndex()-1 - lowIndex ) {return - 1;}
        if (lowIndex-startIndex >  series.getEndIndex()-1 - lowIndex) {return  1;}
        return 0;
    }

}


