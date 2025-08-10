package org.binance.springbot.analytic;
import org.binance.springbot.SpringBotApplication;
import org.binance.springbot.util.BinanceTa4jUtils;
import org.binance.springbot.util.BinanceUtil;
import org.ta4j.core.Bar;
import org.ta4j.core.BarSeries;
import org.ta4j.core.indicators.helpers.*;
import org.ta4j.core.num.Num;

import java.util.*;

public class OrderBlockFinder {

    public static Map<String, String[]> findOrderBlocks(BarSeries series, int maxIndex) {
        String[] sellBlock = {null,null,null};
        String[]  buyBlock = {null,null,null};
        ///  String [Low, High, Imb ]

        double avgResult = calculateAverageVolume(series, 100);


        // (Sell Order Block)
        for (int i = maxIndex-1; i >= 3; i--) {
            Bar next = series.getBar(i+1);
            Bar current = series.getBar(i);
            Bar previous = series.getBar(i - 1);
            Bar previous1 = series.getBar(i - 2);
            Bar previous2 = series.getBar(i - 3);
            Bar previous3 = series.getBar(i - 3);
            double shadow = (current.getHighPrice().doubleValue() - current.getLowPrice().doubleValue())/Math.abs(current.getOpenPrice().doubleValue() - current.getClosePrice().doubleValue());
            double resultPrevious = Math.abs(previous.getHighPrice().doubleValue() - previous.getLowPrice().doubleValue())/previous.getVolume().doubleValue();
            double resultCurent = Math.abs(current.getHighPrice().doubleValue() - current.getLowPrice().doubleValue())/current.getVolume().doubleValue();
            double resultNext = Math.abs(next.getHighPrice().doubleValue() - next.getLowPrice().doubleValue())/next.getVolume().doubleValue();
            if (     current.getVolume().isGreaterThan(next.getVolume())
                    && shadow > 1
                    && previous.getOpenPrice().isLessThan(previous.getClosePrice())
                    && next.getOpenPrice().isGreaterThan(next.getClosePrice())

                    //&& current.getOpenPrice().isLessThan(current.getClosePrice())

               //      &&  current.getVolume().isGreaterThan(previous.getVolume())
                     && avgResult  < current.getVolume().doubleValue()
                     && resultCurent > resultNext
                    && resultCurent > resultPrevious
             //       && resultCurent > avgResult*1.2
                    && checkPriceHigh(series, i)
                    /*previous1.getOpenPrice().isLessThan(previous1.getClosePrice())
                            && previous.getOpenPrice().isLessThan(previous.getClosePrice())
                            && current.getOpenPrice().isGreaterThan(current.getClosePrice())
                            && next.getHighPrice().isLessThan(previous.getLowPrice())
                                            && checkPriceHigh(series, i) */
                    //        &&  hasHighVolumeForOB(series, i - 1, 3, avgVolume, 1.5)
             //   && checkPriceHigh(series, i)
//            if (((previous.getHighPrice().doubleValue()-previous.getLowPrice().doubleValue())/(previous.getClosePrice().doubleValue()-previous.getOpenPrice().doubleValue())>1)
//                && previous.getOpenPrice().isLessThan(previous.getClosePrice())
//                && current.getOpenPrice().isGreaterThan(current.getClosePrice())
//                && previous.getLowPrice().isGreaterThan(current.getClosePrice())
//                && checkPriceHigh(series, i)
            ) {
                ///  String [Low, High, Imb ]
                sellBlock[0] = series.getBar(i).getClosePrice().toString();
                sellBlock[1] = series.getBar(i).getHighPrice().toString();
                sellBlock[2] = series.getBar(i -1 ).getLowPrice().toString();
                break;
            }
//            if (
//                    previous1.getOpenPrice().isLessThan(previous1.getClosePrice())
//                            && Math.abs(previous.getHighPrice().doubleValue() - previous.getOpenPrice().doubleValue()/Math.abs(previous.getOpenPrice().doubleValue() - previous.getClosePrice().doubleValue()))>50
//                            && current.getOpenPrice().isGreaterThan(current.getClosePrice())
//                            && next.getHighPrice().isLessThan(previous.getLowPrice())
//                                && checkPriceHigh(series, i)
//       //                     && hasHighVolumeForOB(series, i - 1, 3, avgVolume, 1.5)
//                        //    && hasHighVolume(previous, avgVolume )
//                // && checkPriceHigh(series, i)
//
//            ){
//                sellBlock[0] = series.getBar(i - 1).getLowPrice().toString();
//                sellBlock[1] = series.getBar(i - 1).getHighPrice().toString();
//                sellBlock[2] =  String.valueOf(calculateImbalance(series, i+1, 1,false));//series.getBar(i + 1).getHighPrice().toString();
//
//                break;
////BigDecimal.valueOf(Double.valueOf(symbolsDto.getLowBuy()))).setScale(BigDecimal.valueOf(Double.valueOf(symbolsDto.getLowSell())).scale(), RoundingMode.HALF_UP).toString();
//            }
        }
        // Buy Order Block
        for (int i = maxIndex-1; i >= 3; i--) {
            Bar next = series.getBar(i+1);
            Bar current = series.getBar(i);
            Bar previous = series.getBar(i - 1);
            Bar previous1 = series.getBar(i - 2);
            Bar previous2 = series.getBar(i - 3);
            Bar previous3 = series.getBar(i - 3);
            double shadow = (current.getHighPrice().doubleValue() - current.getLowPrice().doubleValue())/Math.abs(current.getOpenPrice().doubleValue() - current.getClosePrice().doubleValue());
            double resultPrevious = Math.abs(previous.getHighPrice().doubleValue() - previous.getLowPrice().doubleValue())/previous.getVolume().doubleValue();
            double resultCurent = Math.abs(current.getHighPrice().doubleValue() - current.getLowPrice().doubleValue())/current.getVolume().doubleValue();
            double resultNext = Math.abs(next.getHighPrice().doubleValue() - next.getLowPrice().doubleValue())/next.getVolume().doubleValue();
            if (
                    current.getVolume().isGreaterThan(next.getVolume())
//                            && current.getOpenPrice().isGreaterThan(current.getClosePrice())
                            && previous.getOpenPrice().isGreaterThan(previous.getClosePrice())
                            && next.getOpenPrice().isLessThan(next.getClosePrice())
                            && shadow > 1
                            && avgResult  < current.getVolume().doubleValue()
                            && resultCurent > resultNext
                            && resultCurent > resultPrevious
                            && checkPriceLow(series, i)
        /*            previous1.getOpenPrice().isGreaterThan(previous1.getClosePrice())
                            && previous.getOpenPrice().isGreaterThan(previous.getClosePrice())
                            && current.getOpenPrice().isLessThan(current.getClosePrice())
                            && next.getLowPrice().isGreaterThan(previous.getHighPrice())
                            && checkPriceLow(series, i) */
  //                          && hasHighVolumeForOB(series, i - 1, 3, avgVolume, 1.5)
                    //        && hasHighVolume(previous, avgVolume )
            ) {  /// +
                buyBlock[0] = series.getBar(i).getLowPrice().toString();
                buyBlock[1] = series.getBar(i).getClosePrice().toString();
                buyBlock[2] =  series.getBar(i - 1).getHighPrice().toString();

                break;
            }
//            if (
//                    previous1.getOpenPrice().isGreaterThan(previous1.getClosePrice())
//                            && Math.abs(previous.getHighPrice().doubleValue() - previous.getLowPrice().doubleValue()/Math.abs(previous.getOpenPrice().doubleValue() - previous.getClosePrice().doubleValue()))>50
//                            && current.getOpenPrice().isLessThan(current.getClosePrice())
//                            && next.getLowPrice().isGreaterThan(previous.getHighPrice())
//  //                          && hasHighVolumeForOB(series, i - 1, 3, avgVolume, 1.5)
//                            && checkPriceLow(series, i)
//                    //        && hasHighVolume(previous, avgVolume )
//            ) {
//                buyBlock[0] = series.getBar(i - 1).getLowPrice().toString();
//                buyBlock[1] = series.getBar(i - 1).getHighPrice().toString();
//                buyBlock[2] =  String.valueOf(calculateImbalance(series, i+1, 1,true));//series.getBar(i + 1).getLowPrice().toString();
//
//                break;
//            }

        }
        Map<String, String[]> orderBlocks = new HashMap<>();
//        if ((series.getBar(lastSellBlockIndex).getLowPrice().isLessThan(series.getBar(lastBuyBlockIndex).getHighPrice())) ||
//        (series.getBar(lastSellBlockIndex).getLowPrice().doubleValue() == series.getBar(lastBuyBlockIndex).getLowPrice().doubleValue())){
//            orderBlocks = findOrderBlocks(series, lastSellBlockIndex);
//        }
//        else {
        orderBlocks.put("SellOrderBlock", sellBlock);
        orderBlocks.put("BuyOrderBlock", buyBlock);
   //     }
        return orderBlocks;
    }

    public static Num findeUperOB(BarSeries series, Double price) {
        Num uperOB = null;
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
                uperOB = previous.getLowPrice();
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

    public static Num findeDownOB(BarSeries series, Double price) {
        Num downOB = null;
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
                downOB = previous.getHighPrice();
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


    public static Double  findDownImbStop(String symbol) {
        BarSeries series = BinanceTa4jUtils.convertToTimeSeries(
                Objects.requireNonNull(BinanceUtil.getCandelSeries(symbol, SpringBotApplication.interval1.getIntervalId(), 200))
                , symbol, SpringBotApplication.interval2.getIntervalId());
        ClosePriceIndicator closePrice = new ClosePriceIndicator(series);
        double price = closePrice.getValue(series.getEndIndex()).doubleValue();
        return findeDownIMB(series,price);
    }

    public static Double  findUpImbStop(String symbol) {
        BarSeries series = BinanceTa4jUtils.convertToTimeSeries(
                Objects.requireNonNull(BinanceUtil.getCandelSeries(symbol, SpringBotApplication.interval1.getIntervalId(), 200))
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
        int count = 1;

        for (int i = 0; i < lookbackPeriod && i < series.getBarCount(); i++) {
            Bar bar = series.getBar(series.getEndIndex() - i);
            totalVolume += bar.getVolume().doubleValue();
            count++;
        }

        return   (totalVolume /(count-1));
    }

    private static boolean hasHighVolume(Bar bar, double threshold) {
        return bar.getVolume().doubleValue() > threshold;
    }


    private static String calculateImbalance(BarSeries series, int startIndex, int lookback, boolean isSell) {
        String maxPrice = series.getBar(startIndex).getHighPrice().toString() ;
        String minPrice = series.getBar(startIndex).getLowPrice().toString();


        for (int j = 0; j <= lookback && startIndex + j < series.getBarCount(); j++) {
            Bar bar = series.getBar(startIndex + j);

            if (!isContinuationTrend(bar, isSell) ) { //&& hasHighVolume(bar, calculateAverageVolume(series, 5) * 1.1)) {
                maxPrice = bar.getHighPrice().toString();
                minPrice = bar.getLowPrice().toString();
                break;
            }
        }
        return !isSell ? maxPrice : minPrice;
    }


    private static boolean isContinuationTrend(Bar bar, boolean isSell) {
        if (isSell) {
            return bar.getClosePrice().isGreaterThan(bar.getOpenPrice());
        } else {
            return bar.getClosePrice().isLessThan(bar.getOpenPrice());
        }
    }

}
