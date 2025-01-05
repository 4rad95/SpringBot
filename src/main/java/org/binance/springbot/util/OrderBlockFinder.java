package org.binance.springbot.util;
import org.ta4j.core.Bar;
import org.ta4j.core.BarSeries;
import org.ta4j.core.indicators.helpers.HighPriceIndicator;
import org.ta4j.core.indicators.helpers.HighestValueIndicator;
import org.ta4j.core.indicators.helpers.LowPriceIndicator;
import org.ta4j.core.indicators.helpers.LowestValueIndicator;

import java.util.HashMap;
import java.util.Map;

public class OrderBlockFinder {

    public static Map<String, Integer> findOrderBlocks(BarSeries series, int maxIndex) {
        int lastSellBlockIndex = -1;
        int lastBuyBlockIndex = -1;

        int endIndex = series.getEndIndex();

        // (Sell Order Block)
        for (int i = maxIndex; i >= 3; i--) {
            Bar current = series.getBar(i);
            Bar previous = series.getBar(i - 1);
            Bar previous1 = series.getBar(i - 2);
            Bar previous2 = series.getBar(i - 3);
            Bar previous3 = series.getBar(i - 3);

            if (((previous.getHighPrice().doubleValue()-previous.getLowPrice().doubleValue())/(previous.getClosePrice().doubleValue()-previous.getOpenPrice().doubleValue())>2)
                && previous.getOpenPrice().isLessThan(previous.getClosePrice())
                && current.getOpenPrice().isGreaterThan(current.getClosePrice())
                && previous.getLowPrice().isGreaterThan(current.getClosePrice())
                && checkPriceHigh(series, i)
            ) {
                lastSellBlockIndex = i - 1;
                break;
            }
        }
        // Buy Order Block
        for (int i = maxIndex; i >= 3; i--) {
            Bar current = series.getBar(i);
            Bar previous = series.getBar(i - 1);
            Bar previous1 = series.getBar(i - 2);
            Bar previous2 = series.getBar(i - 3);
            Bar previous3 = series.getBar(i - 3);

            if (((previous.getHighPrice().doubleValue()-previous.getLowPrice().doubleValue())/(previous.getOpenPrice().doubleValue()-previous.getClosePrice().doubleValue())>2)
                    && previous.getOpenPrice().isGreaterThan(previous.getClosePrice())
                    && current.getOpenPrice().isLessThan(current.getClosePrice())
                    && previous.getHighPrice().isLessThan(current.getClosePrice())
                    && checkPriceLow(series, i)
            ) {
                lastBuyBlockIndex = i - 1;
                break;
            }
        }
        Map<String, Integer> orderBlocks = new HashMap<>();
        if ((series.getBar(lastSellBlockIndex).getLowPrice().isLessThan(series.getBar(lastBuyBlockIndex).getHighPrice())) ||
        (series.getBar(lastSellBlockIndex).getLowPrice().doubleValue() == series.getBar(lastBuyBlockIndex).getLowPrice().doubleValue())){
            orderBlocks = findOrderBlocks(series, lastSellBlockIndex);
        }
        else {
            orderBlocks.put("SellOrderBlock", lastSellBlockIndex);
            orderBlocks.put("BuyOrderBlock", lastBuyBlockIndex);
        }
        return orderBlocks;
    }

public static Double findeUperOB(BarSeries series, Double price) {
    double lastBlockIndex = -1.00;
    for (int i = series.getEndIndex(); i >= 3; i--) {
        Bar current = series.getBar(i);
        Bar previous = series.getBar(i - 1);
        if (((previous.getHighPrice().doubleValue() - previous.getLowPrice().doubleValue()) / (previous.getClosePrice().doubleValue() - previous.getOpenPrice().doubleValue()) > 2)
                && previous.getOpenPrice().isLessThan(previous.getClosePrice())
                && current.getOpenPrice().isGreaterThan(current.getClosePrice())
                && previous.getLowPrice().isGreaterThan(current.getClosePrice())
                && checkPriceHigh(series, i)
                && previous.getClosePrice().doubleValue()>price
        ) {
            lastBlockIndex = previous.getLowPrice().doubleValue();
            break;
        }
    }
    return lastBlockIndex;
}

    public static Double findeDownOB(BarSeries series, Double price) {
        double lastSellBlockIndex = -1.00;
        for (int i = series.getEndIndex(); i >= 3; i--) {
            Bar current = series.getBar(i);
            Bar previous = series.getBar(i - 1);
            if (((previous.getHighPrice().doubleValue()-previous.getLowPrice().doubleValue())/(previous.getOpenPrice().doubleValue()-previous.getClosePrice().doubleValue())>2)
                    && previous.getOpenPrice().isGreaterThan(previous.getClosePrice())
                    && current.getOpenPrice().isLessThan(current.getClosePrice())
                    && previous.getHighPrice().isLessThan(current.getClosePrice())
                    && checkPriceLow(series, i)
                    && previous.getClosePrice().doubleValue()<price
            ) {
                lastSellBlockIndex = previous.getHighPrice().doubleValue();
                break;
            }
        }
        return lastSellBlockIndex;
    }

   private static boolean checkPriceHigh(BarSeries series, int i) {
       for (int j = series.getEndIndex(); j > i  ; j--) {
            if (series.getBar(j).getClosePrice().isGreaterThan( series.getBar(i).getHighPrice())){
                return false;
            }
       }
        return true;
   }
    private static boolean checkPriceLow(BarSeries series, int i) {
        for (int j = series.getEndIndex(); j > i  ; j--) {
            if (series.getBar(j).getClosePrice().isLessThan( series.getBar(i).getLowPrice())){
                return false;
            }
        }
        return true;
    }

    public static void findSwingHighLow(BarSeries series, int period) {

        HighestValueIndicator swingHigh = new HighestValueIndicator(new HighPriceIndicator(series), period);
        LowestValueIndicator swingLow = new LowestValueIndicator(new LowPriceIndicator(series), period);
        System.out.println(series.getName());
        for (int i = period; i < series.getBarCount(); i++) {
            System.out.println("Bar: " + i
                    + " Swing High: " + swingHigh.getValue(i)
                    + " Swing Low: " + swingLow.getValue(i));
        }
    }
}