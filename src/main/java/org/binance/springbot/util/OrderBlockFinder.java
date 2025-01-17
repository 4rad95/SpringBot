package org.binance.springbot.util;
import org.binance.springbot.SpringBotApplication;
import org.ta4j.core.Bar;
import org.ta4j.core.BarSeries;
import org.ta4j.core.indicators.helpers.*;

import java.util.*;

import static org.binance.springbot.SpringBotApplication.interval1;
import static org.binance.springbot.SpringBotApplication.interval2;

public class OrderBlockFinder {

    public static Map<String, Integer> findOrderBlocks(BarSeries series, int maxIndex) {
        int lastSellBlockIndex = -1;
        int lastBuyBlockIndex = -1;

        int endIndex = series.getEndIndex();

        // (Sell Order Block)
        for (int i = maxIndex-1; i >= 3; i--) {
            Bar next = series.getBar(i+1);
            Bar current = series.getBar(i);
            Bar previous = series.getBar(i - 1);
            Bar previous1 = series.getBar(i - 2);
            Bar previous2 = series.getBar(i - 3);
            Bar previous3 = series.getBar(i - 3);

            if (
                 previous.getOpenPrice().isLessThan(previous.getClosePrice())
                 && current.getOpenPrice().isGreaterThan(current.getClosePrice())
                 && next.getHighPrice().isLessThan(previous.getLowPrice())
                 && checkPriceHigh(series, i)
//            if (((previous.getHighPrice().doubleValue()-previous.getLowPrice().doubleValue())/(previous.getClosePrice().doubleValue()-previous.getOpenPrice().doubleValue())>1)
//                && previous.getOpenPrice().isLessThan(previous.getClosePrice())
//                && current.getOpenPrice().isGreaterThan(current.getClosePrice())
//                && previous.getLowPrice().isGreaterThan(current.getClosePrice())
//                && checkPriceHigh(series, i)
            ) {
                lastSellBlockIndex = i - 1;
                break;
            }
        }
        // Buy Order Block
        for (int i = maxIndex-1; i >= 3; i--) {
            Bar next = series.getBar(i+1);
            Bar current = series.getBar(i);
            Bar previous = series.getBar(i - 1);
            Bar previous1 = series.getBar(i - 2);
            Bar previous2 = series.getBar(i - 3);
            Bar previous3 = series.getBar(i - 3);

            if (
                    previous.getOpenPrice().isGreaterThan(previous.getClosePrice())
                    && current.getOpenPrice().isLessThan(current.getClosePrice())
                    && next.getLowPrice().isGreaterThan(previous.getHighPrice())
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
    double uperOB = -1.00;
    for (int i = series.getEndIndex(); i >= 3; i--) {
        Bar current = series.getBar(i);
        Bar previous = series.getBar(i - 1);
        if (((previous.getHighPrice().doubleValue() - previous.getLowPrice().doubleValue()) / (previous.getClosePrice().doubleValue() - previous.getOpenPrice().doubleValue()) > 1)
                && previous.getOpenPrice().isLessThan(previous.getClosePrice())
                && current.getOpenPrice().isGreaterThan(current.getClosePrice())
                && previous.getLowPrice().isGreaterThan(current.getClosePrice())
                && checkPriceHigh(series, i)
                && previous.getClosePrice().doubleValue()>price
        ) {
            uperOB = previous.getLowPrice().doubleValue();
            break;
        }
    }
    return uperOB;
}

    public static Double findeUperIMB(BarSeries series, Double price) {
        double uperIMB = -1.00;
        double uperOB = -1.00;
        for (int i = series.getEndIndex()-2; i >= 3; i--) {
            Bar current = series.getBar(i);
            Bar previous = series.getBar(i - 1);
            Bar next = series.getBar(i+1);
            if (
                    next.getHighPrice().isLessThan(previous.getLowPrice())
                    && checkImbHigh(series, i)
                    && previous.getClosePrice().doubleValue()>price
            ) {
                uperIMB = previous.getLowPrice().doubleValue();

                System.out.println(series.getName() + "  UP "+  uperIMB);

                return uperIMB;
            }}
        return uperIMB;
    }

    public static Double findeDownOB(BarSeries series, Double price) {
        double downOB = -1.00;
        for (int i = series.getEndIndex(); i >= 3; i--) {
            Bar current = series.getBar(i);
            Bar previous = series.getBar(i - 1);
            if (((previous.getHighPrice().doubleValue()-previous.getLowPrice().doubleValue())/(previous.getOpenPrice().doubleValue()-previous.getClosePrice().doubleValue())>1)
                    && previous.getOpenPrice().isGreaterThan(previous.getClosePrice())
                    && current.getOpenPrice().isLessThan(current.getClosePrice())
                    && previous.getHighPrice().isLessThan(current.getClosePrice())
                    && checkPriceLow(series, i)
                    && previous.getClosePrice().doubleValue()<price
            ) {
                downOB = previous.getHighPrice().doubleValue();
                break;
            }
        }
        return downOB;
    }

    public static Double findeDownIMB(BarSeries series, Double price) {
        double downIMB = -1.00;
        double downOB;
        for (int i = series.getEndIndex()-2; i >= 3; i--) {
            Bar current = series.getBar(i);
            Bar previous = series.getBar(i - 1);
            Bar next = series.getBar(i+1);

            if (//((previous.getHighPrice().doubleValue()-previous.getLowPrice().doubleValue())/(previous.getOpenPrice().doubleValue()-previous.getClosePrice().doubleValue())>1)
//                    previous.getOpenPrice().isGreaterThan(previous.getClosePrice())
//                    && current.getOpenPrice().isLessThan(current.getClosePrice())
//                    && previous.getHighPrice().isLessThan(current.getClosePrice())
//                    && checkPriceLow(series, i)
//                    && previous.getClosePrice().doubleValue()<price
                   next.getLowPrice().isGreaterThan(previous.getHighPrice())
                   && checkImbLow(series, i)

            ) {
                downIMB = previous.getHighPrice().doubleValue();
                downOB = previous.getHighPrice().doubleValue();
               // if (downIMB > downOB) {
                System.out.println(series.getName() + "  DOWN "+  downIMB);
                    return downIMB;
                //}
               //   return downOB;
            }
        }
        return downIMB;
        }

   private static boolean checkPriceHigh(BarSeries series, int i) {
       for (int j = series.getEndIndex(); j > i  ; j--) {
            if (series.getBar(j).getClosePrice().isGreaterThan( series.getBar(i).getHighPrice())
                ){
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
    private static boolean checkImbHigh(BarSeries series, int i) {
        for (int j = series.getEndIndex(); j > i  ; j--) {
            if (series.getBar(j).getHighPrice().isGreaterThan( series.getBar(i+1).getHighPrice())
            ){
                return false;
            }
        }
        return true;
    }
    private static boolean checkImbLow(BarSeries series, int i) {
        for (int j = series.getEndIndex(); j > i  ; j--) {
            if (series.getBar(j).getLowPrice().isLessThan( series.getBar(i+1).getLowPrice())){
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

    public static Double  findDownImbStop(String symbol) {
        BarSeries series = BinanceTa4jUtils.convertToTimeSeries(
                Objects.requireNonNull(BinanceUtil.getCandelSeries(symbol, SpringBotApplication.interval2.getIntervalId(), 200))
                , symbol, SpringBotApplication.interval2.getIntervalId());
        ClosePriceIndicator closePrice = new ClosePriceIndicator(series);
        double price = closePrice.getValue(series.getEndIndex()).doubleValue();
        return findeDownIMB(series,price);
    }
    public static Double  findUpImbStop(String symbol) {
        BarSeries series = BinanceTa4jUtils.convertToTimeSeries(
                Objects.requireNonNull(BinanceUtil.getCandelSeries(symbol, SpringBotApplication.interval2.getIntervalId(), 200))
                , symbol, SpringBotApplication.interval2.getIntervalId());
        ClosePriceIndicator closePrice = new ClosePriceIndicator(series);
        double price = closePrice.getValue(series.getEndIndex()).doubleValue();
        return findeUperIMB(series,price);
    }


    public static Map<Integer, OrderBlock> findAllOrderBlocks(BarSeries series) {
        int maxIndex = series.getEndIndex();

        Map<Integer, OrderBlock> orderBlocks = new TreeMap<>(Comparator.reverseOrder());

        for (int i=maxIndex-1; i >=3; i--) {

            Bar next = series.getBar(i+1);
            Bar current = series.getBar(i);
            Bar previous = series.getBar(i - 1);
            Bar previous1 = series.getBar(i - 2);
            Bar previous2 = series.getBar(i - 3);
            Bar previous3 = series.getBar(i - 3);

            // (Sell Order Block)
            if (            previous.getOpenPrice().isLessThan(previous.getClosePrice())
                            && current.getOpenPrice().isGreaterThan(current.getClosePrice())
                            && next.getHighPrice().isLessThan(previous.getLowPrice())
                            && checkPriceHigh(series, i)

            ) {
                OrderBlock orderBlock = new OrderBlock(-1,i-1,series.getBar(i-1));
                orderBlocks.put((i - 1), orderBlock);
            }
        // Buy Order Block
            if (
                    previous.getOpenPrice().isGreaterThan(previous.getClosePrice())
                            && current.getOpenPrice().isLessThan(current.getClosePrice())
                            && next.getLowPrice().isGreaterThan(previous.getHighPrice())
                            && checkPriceLow(series, i)

            ) {
                OrderBlock orderBlock = new OrderBlock(1,i-1,series.getBar(i-1));
                orderBlocks.put((i - 1), orderBlock);

            }
        }
        return sortData(orderBlocks);
    }
    private static Map<Integer, OrderBlock> sortData(Map<Integer, OrderBlock> orderBlocks) {
        Set<Integer> seenMoves = new HashSet<>();

        // Итератор для безопасного удаления
        Iterator<Map.Entry<Integer, OrderBlock>> iterator = orderBlocks.entrySet().iterator();

        while (iterator.hasNext()) {
            Map.Entry<Integer, OrderBlock> entry = iterator.next();
            int move = entry.getValue().getMove();

            // Если move уже встречался, удаляем запись
            if (seenMoves.contains(move)) {
                iterator.remove();
            } else {
                // Если move ещё не встречался, добавляем его в Set
                seenMoves.add(move);
            }
        }

        return orderBlocks;
    }


}