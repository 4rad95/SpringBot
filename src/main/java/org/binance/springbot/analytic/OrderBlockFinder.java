package org.binance.springbot.analytic;
import org.binance.springbot.SpringBotApplication;
import org.binance.springbot.util.BinanceTa4jUtils;
import org.binance.springbot.util.BinanceUtil;
import org.ta4j.core.Bar;
import org.ta4j.core.BarSeries;
import org.ta4j.core.indicators.helpers.*;

import java.util.*;

public class OrderBlockFinder {

    public static Map<String, String[]> findOrderBlocks(BarSeries series, int maxIndex) {
        String[] sellBlock = {null,null,null};
        String[]  buyBlock = {null,null,null};
        ///  String [Low, High, Imb ]

        double avgVolume = calculateAverageVolume(series, 20);


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
                             //&&  hasHighVolume(previous, avgVolume )
                // && checkPriceHigh(series, i)
//            if (((previous.getHighPrice().doubleValue()-previous.getLowPrice().doubleValue())/(previous.getClosePrice().doubleValue()-previous.getOpenPrice().doubleValue())>1)
//                && previous.getOpenPrice().isLessThan(previous.getClosePrice())
//                && current.getOpenPrice().isGreaterThan(current.getClosePrice())
//                && previous.getLowPrice().isGreaterThan(current.getClosePrice())
//                && checkPriceHigh(series, i)
            ) {
                sellBlock[0] = series.getBar(i - 1).getLowPrice().toString();
                sellBlock[1] = series.getBar(i - 1).getHighPrice().toString();
                sellBlock[2] = String.valueOf(calculateImbalance(series, i+1, 3,true));//series.getBar(i + 1).getHighPrice().toString();
                break;
            } if (
                    previous1.getOpenPrice().isLessThan(previous1.getClosePrice())
                            && Math.abs(previous.getHighPrice().doubleValue() - previous.getOpenPrice().doubleValue()/Math.abs(previous.getOpenPrice().doubleValue() - previous.getClosePrice().doubleValue()))>50
                            && current.getOpenPrice().isGreaterThan(current.getClosePrice())
                            && next.getHighPrice().isLessThan(previous.getLowPrice())
                                && checkPriceHigh(series, i)
                        //    && hasHighVolume(previous, avgVolume )
                // && checkPriceHigh(series, i)

            ){
                sellBlock[0] = series.getBar(i - 1).getLowPrice().toString();
                sellBlock[1] = series.getBar(i - 1).getHighPrice().toString();
                sellBlock[2] =  String.valueOf(calculateImbalance(series, i+1, 3,true));//series.getBar(i + 1).getHighPrice().toString();

                break;
//BigDecimal.valueOf(Double.valueOf(symbolsDto.getLowBuy()))).setScale(BigDecimal.valueOf(Double.valueOf(symbolsDto.getLowSell())).scale(), RoundingMode.HALF_UP).toString();
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
                    //        && hasHighVolume(previous, avgVolume )
            ) {  /// +
                buyBlock[0] = series.getBar(i - 1).getLowPrice().toString();
                buyBlock[1] = series.getBar(i - 1).getHighPrice().toString();
                buyBlock[2] =  String.valueOf(calculateImbalance(series, i+1, 3,false)); //series.getBar(i + 1).getLowPrice().toString();

                break;
            }  if (
                    previous1.getOpenPrice().isGreaterThan(previous1.getClosePrice())
                            && Math.abs(previous.getHighPrice().doubleValue() - previous.getLowPrice().doubleValue()/Math.abs(previous.getOpenPrice().doubleValue() - previous.getClosePrice().doubleValue()))>50
                            && current.getOpenPrice().isLessThan(current.getClosePrice())
                            && next.getLowPrice().isGreaterThan(previous.getHighPrice())
                            && checkPriceLow(series, i)
                    //        && hasHighVolume(previous, avgVolume )
            ) {
                buyBlock[0] = series.getBar(i - 1).getLowPrice().toString();
                buyBlock[1] = series.getBar(i - 1).getHighPrice().toString();
                buyBlock[2] =  String.valueOf(calculateImbalance(series, i+1, 3,false));//series.getBar(i + 1).getLowPrice().toString();

                break;
            }

        }
        Map<String, String[]> orderBlocks = new HashMap<>();
//        if ((series.getBar(lastSellBlockIndex).getLowPrice().isLessThan(series.getBar(lastBuyBlockIndex).getHighPrice())) ||
//        (series.getBar(lastSellBlockIndex).getLowPrice().doubleValue() == series.getBar(lastBuyBlockIndex).getLowPrice().doubleValue())){
//            orderBlocks = findOrderBlocks(series, lastSellBlockIndex);
//        }
//        else {
        orderBlocks.put("SellOrderBlock", sellBlock);
        orderBlocks.put("BuyOrderBlock", buyBlock);
        //}
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
            if (series.getBar(j).getHighPrice().isGreaterThan( series.getBar(i).getHighPrice())
            ){
                return false;
            }
        }
        return true;
    }
    private static boolean checkPriceLow(BarSeries series, int i) {
        for (int j = series.getEndIndex(); j > i  ; j--) {
            if (series.getBar(j).getLowPrice().isLessThan( series.getBar(i).getLowPrice())){
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
                    && checkPriceHigh(series, i-1)

            ) {
                OrderBlock orderBlock = new OrderBlock(-1,i-1,series.getBar(i-1));
                orderBlocks.put((i - 1), orderBlock);
            }
            // Buy Order Block
            if (
                    previous.getOpenPrice().isGreaterThan(previous.getClosePrice())
                            && current.getOpenPrice().isLessThan(current.getClosePrice())
                            && next.getLowPrice().isGreaterThan(previous.getHighPrice())
                            && checkPriceLow(series, i-1)

            ) {
                OrderBlock orderBlock = new OrderBlock(1,i-1,series.getBar(i-1));
                orderBlocks.put((i - 1), orderBlock);

            }
        }
        return orderBlocks;
    }
    private static Map<Integer, OrderBlock> sortData(Map<Integer, OrderBlock> orderBlocks) {
        Set<Integer> seenMoves = new HashSet<>();

        Iterator<Map.Entry<Integer, OrderBlock>> iterator = orderBlocks.entrySet().iterator();

        while (iterator.hasNext()) {
            Map.Entry<Integer, OrderBlock> entry = iterator.next();
            int move = entry.getValue().getMove();

            if (seenMoves.contains(move)) {
                iterator.remove();
            } else {
                seenMoves.add(move);
            }
        }

        return orderBlocks;
    }

    private static double calculateAverageVolume(BarSeries series, int lookbackPeriod) {
        double totalVolume = 0;
        int count = 0;

        for (int i = 0; i < lookbackPeriod && i < series.getBarCount(); i++) {
            Bar bar = series.getBar(series.getEndIndex() - i);
            totalVolume += bar.getVolume().doubleValue();
            count++;
        }

        return count > 0 ? totalVolume / count : 0;
    }

    private static boolean hasHighVolume(Bar bar, double threshold) {
        return bar.getVolume().doubleValue() > threshold;
    }
    private static double calculateImbalance(BarSeries series, int startIndex, int lookback, boolean isSell) {
        double imbalance = isSell ? Double.MIN_VALUE : Double.MAX_VALUE;

        for (int j = 1; j <= lookback && startIndex + j < series.getBarCount(); j++) {
            Bar bar = series.getBar(startIndex + j);

            if (isSell) {
                if (!isContinuationTrend(bar, isSell)) {
                    imbalance = Math.max(imbalance, bar.getHighPrice().doubleValue());
                }
            } else {
                if (!isContinuationTrend(bar, isSell)) {
                    imbalance = Math.min(imbalance, bar.getLowPrice().doubleValue());
                }
            }

            // Защита от мусорных значений
            if (Double.isInfinite(imbalance) || Double.isNaN(imbalance)) {
                imbalance = isSell ? series.getBar(startIndex).getHighPrice().doubleValue() : series.getBar(startIndex).getLowPrice().doubleValue();
            }
        }

        return imbalance;
    }
    private static boolean isContinuationTrend(Bar bar, boolean isSell) {
        if (isSell) {
            return bar.getClosePrice().isGreaterThan(bar.getOpenPrice());
        } else {
            return bar.getClosePrice().isLessThan(bar.getOpenPrice());
        }
    }
}


//package org.binance.springbot.analytic;
//
//import org.ta4j.core.Bar;
//import org.ta4j.core.BarSeries;
//import org.ta4j.core.*;
//import org.ta4j.core.indicators.ATRIndicator;
//import org.ta4j.core.indicators.helpers.ClosePriceIndicator;
//import org.ta4j.core.num.Num;
//
//import java.util.HashMap;
//import java.util.Map;
//
//public class OrderBlockFinder {
//
//    // Метод для расчета стоп-лосса на основе ATR
//    public static double calculateAtrStopLoss(BarSeries series, int atrPeriod, double multiplier, boolean isSell) {
//        ATRIndicator atr = new ATRIndicator(series, atrPeriod);
//        double lastAtr = atr.getValue(series.getEndIndex()).doubleValue();
//        double currentPrice = series.getBar(series.getEndIndex()).getClosePrice().doubleValue();
//
//        if (isSell) {
//            return currentPrice + (lastAtr * multiplier); // Для sell-позиций стоп выше цены
//        } else {
//            return currentPrice - (lastAtr * multiplier); // Для buy-позиций стоп ниже цены
//        }
//    }
//
//    // Метод для расчета тейк-профита на основе risk/reward ratio
//    public static double calculateTakeProfit(double entryPrice, double stopLoss, double riskRewardRatio, boolean isSell) {
//        double risk = Math.abs(entryPrice - stopLoss);
//        double reward = risk * riskRewardRatio;
//
//        if (isSell) {
//            return entryPrice - reward; // Для sell-позиций тейк-профит ниже цены входа
//        } else {
//            return entryPrice + reward; // Для buy-позиций тейк-профит выше цены входа
//        }
//    }
//
//    // Метод для поиска order blocks (обновленный)
//    public static Map<String, Double[]> findOrderBlocks(BarSeries series, int maxIndex) {
//        Double[] sellBlock = new Double[3];
//        Double[] buyBlock = new Double[3];
//
//        for (int i = maxIndex - 1; i >= 3; i--) {
//            Bar next = series.getBar(i + 1);
//            Bar current = series.getBar(i);
//            Bar previous = series.getBar(i - 1);
//
//            // Поиск sell-order block
//            if (
//                    previous.getOpenPrice().isLessThan(previous.getClosePrice()) &&
//                            current.getOpenPrice().isGreaterThan(current.getClosePrice()) &&
//                            next.getHighPrice().isLessThan(previous.getLowPrice()) &&
//                            checkPriceHigh(series, i - 1)
//            ) {
//                sellBlock[0] = previous.getLowPrice().doubleValue(); // Low
//                sellBlock[1] = previous.getHighPrice().doubleValue(); // High
//                sellBlock[2] = next.getHighPrice().doubleValue(); // Imb
//                break;
//            }
//
//            // Поиск buy-order block
//            if (
//                    previous.getOpenPrice().isGreaterThan(previous.getClosePrice()) &&
//                            current.getOpenPrice().isLessThan(current.getClosePrice()) &&
//                            next.getLowPrice().isGreaterThan(previous.getHighPrice()) &&
//                            checkPriceLow(series, i - 1)
//            ) {
//                buyBlock[0] = previous.getLowPrice().doubleValue(); // Low
//                buyBlock[1] = previous.getHighPrice().doubleValue(); // High
//                buyBlock[2] = next.getLowPrice().doubleValue(); // Imb
//                break;
//            }
//        }
//
//        Map<String, Double[]> result = new HashMap<>();
//        result.put("SellOrderBlock", sellBlock);
//        result.put("BuyOrderBlock", buyBlock);
//
//        return result;
//    }
//
//    // Вспомогательные методы
//    private static boolean checkPriceHigh(BarSeries series, int i) {
//        for (int j = series.getEndIndex(); j > i; j--) {
//            if (series.getBar(j).getHighPrice().isGreaterThan(series.getBar(i).getHighPrice())) {
//                return false;
//            }
//        }
//        return true;
//    }
//
//    private static boolean checkPriceLow(BarSeries series, int i) {
//        for (int j = series.getEndIndex(); j > i; j--) {
//            if (series.getBar(j).getLowPrice().isLessThan(series.getBar(i).getLowPrice())) {
//                return false;
//            }
//        }
//        return true;
//    }
//}