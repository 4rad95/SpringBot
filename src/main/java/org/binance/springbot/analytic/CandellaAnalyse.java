package org.binance.springbot.analytic;

import org.ta4j.core.BarSeries;

public class CandellaAnalyse {

    private int trend;

    public CandellaAnalyse() {
    }

    public static double trendDetect(BarSeries series, String LowLimit, String HighLimit) {
        int i =0;
        int trend = 0;
        double volumeMax = 0;
        while (true) {
            if (series.getBar(series.getEndIndex()-i).getHighPrice().doubleValue() == Double.valueOf(HighLimit)
                && series.getBar(series.getEndIndex()-i).getLowPrice().doubleValue() == Double.valueOf(LowLimit)
            ){

                return volumeMax;
            }
            if (Math.abs(volumeMax) < series.getBar(series.getEndIndex()-i).getVolume().doubleValue()) {
                volumeMax = series.getBar(series.getEndIndex()-i).getVolume().doubleValue();
                if (series.getBar(series.getEndIndex()-i).getOpenPrice().doubleValue() > series.getBar(series.getEndIndex()-i).getClosePrice().doubleValue()){
                    volumeMax =  - volumeMax;
                }
            }
         i++;
        }

    }

}
