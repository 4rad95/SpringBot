package org.binance.springbot.util;
import org.ta4j.core.Bar;
import org.ta4j.core.BarSeries;

import java.util.HashMap;
import java.util.Map;

public class OrderBlockFinder {

    public static Map<String, Integer> findOrderBlocks(BarSeries series) {
        int lastSellBlockIndex = -1; // Индекс верхнего ордерблока (Sell)
        int lastBuyBlockIndex = -1;  // Индекс нижнего ордерблока (Buy)

        int endIndex = series.getEndIndex();

        // Поиск верхнего ордерблока (Sell Order Block)
        for (int i = endIndex; i >= 2; i--) {
            Bar current = series.getBar(i);
            Bar previous = series.getBar(i - 1);

            // Условие для Sell Order Block: последняя бычья свеча перед падением
            if (previous.getClosePrice().isGreaterThan(previous.getOpenPrice()) && // Бычья свеча
                    current.getClosePrice().isLessThan(previous.getLowPrice())) {      // Цена падает ниже Low предыдущей свечи
                lastSellBlockIndex = i - 1;
                break;
            }
        }

        // Поиск нижнего ордерблока (Buy Order Block)
        for (int i = endIndex; i >= 2; i--) {
            Bar current = series.getBar(i);
            Bar previous = series.getBar(i - 1);

            // Условие для Buy Order Block: последняя медвежья свеча перед ростом
            if (previous.getClosePrice().isLessThan(previous.getOpenPrice()) && // Медвежья свеча
                    current.getClosePrice().isGreaterThan(previous.getHighPrice())) { // Цена растёт выше High предыдущей свечи
                lastBuyBlockIndex = i - 1;
                break;
            }
        }

        // Возвращаем индексы ордерблоков
        Map<String, Integer> orderBlocks = new HashMap<>();
        orderBlocks.put("SellOrderBlock", lastSellBlockIndex);
        orderBlocks.put("BuyOrderBlock", lastBuyBlockIndex);

        return orderBlocks;
    }
}