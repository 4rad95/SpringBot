package org.binance.springbot.task;


import com.binance.client.RequestOptions;
import com.binance.client.SyncRequestClient;
import com.binance.client.model.enums.*;
import com.binance.client.model.trade.Order;
import org.binance.springbot.entity.enums.Type;
import org.binance.springbot.util.BinanceUtil;

public class Position {
    private String symbol;
    private Long idBinance;
    private String type;
    private String quantity;
    private String startPrice;

    public Position(String symbol, String type, String quantity, String startPrice) {
        this.symbol = symbol;
       // this.idBinance = idBinance;
        this.type = type;
        this.quantity = quantity;
        this.startPrice = startPrice;
    }
    public Position(Long idBinance, String symbol){
        this.idBinance = idBinance;
        this.symbol = symbol;
    }

    public Long openPositionShort()
    {
        RequestOptions options = new RequestOptions();
        SyncRequestClient syncRequestClient = SyncRequestClient.create(BinanceUtil.getApiKey(), BinanceUtil.getApiSecret(),
                options);
        Order orderNew = (syncRequestClient.postOrder(symbol, OrderSide.SELL, PositionSide.SHORT, OrderType.LIMIT, TimeInForce.GTC,
                quantity, startPrice, null, null, null, null, null, null, null, null, NewOrderRespType.RESULT));

        this.idBinance = orderNew.getOrderId();
        return orderNew.getOrderId();
    }

    public Long openPositionLong() {

        RequestOptions options = new RequestOptions();
        SyncRequestClient syncRequestClient = SyncRequestClient.create(BinanceUtil.getApiKey(), BinanceUtil.getApiSecret(),
                options);
        Order orderNew = (syncRequestClient.postOrder(symbol, OrderSide.BUY, PositionSide.LONG, OrderType.LIMIT, TimeInForce.GTC,
                quantity, startPrice, null, null, null, null, null, null, null, null, NewOrderRespType.RESULT));

        this.idBinance = orderNew.getOrderId();
        return orderNew.getOrderId();
    }

    public Long[] stopPositionShort(String stopPrice, String profitPrice) {
        RequestOptions options = new RequestOptions();
        SyncRequestClient syncRequestClient = SyncRequestClient.create(BinanceUtil.getApiKey(), BinanceUtil.getApiSecret(),
                options);
        Order orderNew = syncRequestClient.getOrder(symbol, idBinance, null);
        orderNew = syncRequestClient.postOrder(
                symbol, // Торговая пара
                OrderSide.BUY, // Тип ордера - продажа
                PositionSide.SHORT, // Открытая позиция - длинная
                OrderType.STOP_MARKET, // Тип ордера - стоп-ордер
                TimeInForce.GTC, // Тайминг выполнения ордера: GTC (Good 'Till Canceled)
                String.valueOf(orderNew.getOrigQty()), // Количество актива, который ты продаешь
                null, // Цена исполнения ордера (не используется для STOP ордера)
                null, // Цена исполнения тейк-профита (если нужен)
                stopPrice , // Цена исполнения стоп-лимита (если нужен)
                stopPrice, // Стоп-цена (trigger price) - цена, при которой ордер будет активирован
                null , // Количество тейк-профита (если нужен)
                String.valueOf(orderNew.getOrigQty()), // Количество стоп-лимита (если нужен)
                null, // Отдельная стратегия исполнения, если есть
                null, // Время исполнения, если требуется
                null, // Рабочий процесс исполнения (например, если используешь OCO или другие сложные типы)
                NewOrderRespType.RESULT // Тип ответа, который ты ожидаешь (например, полное подтверждение о новом ордере)
        );
        //Lon stopClientId = orderNew.getClientOrderId();
        Long stopId = orderNew.getOrderId();
        orderNew = syncRequestClient.postOrder(
                symbol, // Торговая пара
                OrderSide.BUY, // Тип ордера - продажа
                PositionSide.SHORT, // Длинная позиция
                OrderType.TRAILING_STOP_MARKET, // Тип ордера - трейлинг стоп
                TimeInForce.GTC, // Время действия ордера: GTC
                String.valueOf(orderNew.getOrigQty()), // Количество актива
                null, // Цена не требуется для трейлинг стопа
                null , // Цена тейк-профита
                null, // Стоп-цена (не используется для трейлинг стопа)
                profitPrice, // Лимитная цена
                null, // Отклонение (callback rate указывается в следующем параметре)
                null, // Процент отступа
                null, // Дополнительные параметры (если требуются)
                WorkingType.MARK_PRICE, // Рабочий тип (например, MARK_PRICE)
                null, // Отступ callback rate (процент)
                NewOrderRespType.RESULT // Режим ответа
        );
        // String profitClientId = orderNew.getClientOrderId();
        Long profitId = orderNew.getOrderId();
        return new Long[] {stopId,profitId};
    }

    public Long[] stopPositionLong(String stopPrice, String profitPrice) {
        RequestOptions options = new RequestOptions();
        SyncRequestClient syncRequestClient = SyncRequestClient.create(BinanceUtil.getApiKey(), BinanceUtil.getApiSecret(),
                options);
        Order orderNew = syncRequestClient.getOrder(symbol, idBinance, null);
        orderNew = syncRequestClient.postOrder(
                symbol, // Торговая пара
                OrderSide.SELL, // Тип ордера - продажа
                PositionSide.LONG, // Открытая позиция - длинная
                OrderType.STOP_MARKET, // Тип ордера - стоп-ордер
                TimeInForce.GTC, // Тайминг выполнения ордера: GTC (Good 'Till Canceled)
                String.valueOf(orderNew.getOrigQty()), // Количество актива, который ты продаешь
                null, // Цена исполнения ордера (не используется для STOP ордера)
                null, // Цена исполнения тейк-профита (если нужен)
                stopPrice , // Цена исполнения стоп-лимита (если нужен)
                stopPrice, // Стоп-цена (trigger price) - цена, при которой ордер будет активирован
                null , // Количество тейк-профита (если нужен)
                String.valueOf(orderNew.getOrigQty()), // Количество стоп-лимита (если нужен)
                null, // Отдельная стратегия исполнения, если есть
                null, // Время исполнения, если требуется
                null, // Рабочий процесс исполнения (например, если используешь OCO или другие сложные типы)
                NewOrderRespType.RESULT // Тип ответа, который ты ожидаешь (например, полное подтверждение о новом ордере)
        );

        Long stopId = orderNew.getOrderId();
        orderNew = syncRequestClient.postOrder(
                symbol, // Торговая пара
                OrderSide.SELL, // Тип ордера - продажа
                PositionSide.LONG, // Длинная позиция
                OrderType.TRAILING_STOP_MARKET, // Тип ордера - трейлинг стоп
                TimeInForce.GTC, // Время действия ордера: GTC
                String.valueOf(orderNew.getOrigQty()), // Количество актива
                null, // Цена не требуется для трейлинг стопа
                null , // Цена тейк-профита
                null, // Стоп-цена (не используется для трейлинг стопа)
                profitPrice, // Лимитная цена
                null, // Отклонение (callback rate указывается в следующем параметре)
                null, // Процент отступа
                null, // Дополнительные параметры (если требуются)
                WorkingType.MARK_PRICE, // Рабочий тип (например, MARK_PRICE)
                null, // Отступ callback rate (процент)
                NewOrderRespType.RESULT // Режим ответа
        );
        Long profitId = orderNew.getOrderId();
        return new Long[] {stopId,profitId};

    }
    public boolean getStatus(Long idBinance,String symbol){
        RequestOptions options = new RequestOptions();
        SyncRequestClient syncRequestClient = SyncRequestClient.create(BinanceUtil.getApiKey(), BinanceUtil.getApiSecret(),
                options);
        Order orderNew = syncRequestClient.getOrder(symbol, idBinance, null);
        if (orderNew.getStatus() == "NEW" ) {return false;}
        else return true;
    }

    public Long getIdBinance() {
        return idBinance;
    }

    public void setIdBinance(Long idBinance) {
        this.idBinance = idBinance;
    }
}
