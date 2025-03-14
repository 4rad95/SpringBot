package org.binance.springbot.analytic;

import com.binance.api.client.domain.market.Candlestick;
import org.ta4j.core.Bar;
import org.ta4j.core.BarSeries;


public class CandellaAnalyse {

    private int trend;
    private double bullSpred = 0;
    private double bullVolume = 0;
    private double bearSpred = 0;
    private double bearVolume =0;
    private static int bullIndex = 0;
    private static int bearIndex = 0;

    public CandellaAnalyse() {
    }

    public static double trendDetect(BarSeries series, String LowLimit, String HighLimit) {
        int i = series.getEndIndex()-1;

        double volumeMax = 0;
        while (true) {
            if (series.getBar(i).getHighPrice().doubleValue() == Double.valueOf(HighLimit)
                && series.getBar(i).getLowPrice().doubleValue() == Double.valueOf(LowLimit)
            ){
                break;

            }
            if (series.getBar(i).getOpenPrice().doubleValue() < series.getBar(i).getClosePrice().doubleValue())   //Long
            { if (bullIndex == 0 || getSpread(series.getBar(i)) > getSpread(series.getBar(bullIndex)))
                { bullIndex = i;}
                }
            if (series.getBar(i).getOpenPrice().doubleValue() > series.getBar(i).getClosePrice().doubleValue())   // Short
            {   if (bearIndex == 0 || getSpread(series.getBar(i)) > getSpread(series.getBar(bearIndex)))
                { bearIndex = i;}}

         i--;

    }
        if (series.getBar(bearIndex).getVolume().doubleValue() > series.getBar(bullIndex).getVolume().doubleValue()) {return -1;}
        if (series.getBar(bearIndex).getVolume().doubleValue() < series.getBar(bullIndex).getVolume().doubleValue()) {return 1;}
        return 0;
    }
        private static double getSpread(Bar bar) {
            return  Math.abs(bar.getOpenPrice().doubleValue()-bar.getClosePrice().doubleValue());
        }

}


