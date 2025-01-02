package org.binance.springbot.task;

import com.binance.client.RequestOptions;
import com.binance.client.SyncRequestClient;
import com.binance.client.model.trade.Order;
import org.binance.springbot.SpringBotApplication;
import org.binance.springbot.util.BinanceUtil;

public class PositionStatus {
    private String symbol;
    private String binanceId;
    private String stopId;
    private String profitId;
    private com.binance.client.model.trade.Order order;
    private com.binance.client.model.trade.Order stopOrder;
    private Order profitOrder;

    public PositionStatus(String symbol, String binanceId, String stopId, String profitId) {
        this.symbol = symbol;
        this.binanceId = binanceId;
        this.stopId = stopId;
        this.profitId = profitId;
    }

    private void initOrder() {
        RequestOptions options = new RequestOptions();
        SyncRequestClient syncRequestClient = SyncRequestClient.create(BinanceUtil.getApiKey(), BinanceUtil.getApiSecret(),
                options);
        order = syncRequestClient.getOrder(symbol,Long.valueOf(binanceId),null);
        stopOrder =  syncRequestClient.getOrder(symbol,Long.valueOf(stopId),null);
        profitOrder = syncRequestClient.getOrder(symbol,Long.valueOf(profitId),null);
        System.out.println(order.toString());
        System.out.println(profitOrder.toString());
        System.out.println(stopOrder.toString());
    }

    public String getSymbol() {
        return symbol;
    }

    public String getBinanceId() {
        return binanceId;
    }

    public String getStopId() {
        return stopId;
    }

    public String getProfitId() {
        return profitId;
    }

    public Order getOrder() {
        return order;
    }

    public Order getStopOrder() {
        return stopOrder;
    }

    public Order getProfitOrder() {
        return profitOrder;
    }
}
