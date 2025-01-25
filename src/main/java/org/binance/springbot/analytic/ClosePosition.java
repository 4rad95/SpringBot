package org.binance.springbot.analytic;

import com.binance.client.RequestOptions;
import com.binance.client.SyncRequestClient;
import com.binance.client.model.enums.NewOrderRespType;
import com.binance.client.model.enums.OrderSide;
import com.binance.client.model.enums.OrderType;
import com.binance.client.model.enums.PositionSide;
import com.binance.client.model.trade.Order;
import org.binance.springbot.aspect.LoggingAspect;
import org.binance.springbot.util.BinanceUtil;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.ta4j.core.Bar;
import org.ta4j.core.BarSeries;
import org.ta4j.core.num.Num;

import java.math.BigDecimal;
import java.util.function.Function;


import static java.lang.Thread.sleep;
import static org.ta4j.core.num.DoubleNum.valueOf;

public class ClosePosition {
    private BarSeries series;
    private Long id;
    String type;
    private static final Logger log = LoggerFactory.getLogger(LoggingAspect.class);

    public ClosePosition( BarSeries series, String type, Long id) {
        this.series = series;
        this.type = type;
        this.id = id;
    }

    public boolean checkPosition () {
        Bar current = series.getBar(series.getEndIndex());
        Bar previous = series.getBar(series.getEndIndex() - 1);
        boolean highVolume = current.getVolume().doubleValue() > previous.getVolume().doubleValue() * 1.5;

        Num longThreshold = current.getHighPrice().minus(current.getHighPrice().minus(current.getLowPrice()).multipliedBy(valueOf(0.25)));
        Num shortThreshold = current.getLowPrice().plus(current.getHighPrice().minus(current.getLowPrice()).multipliedBy(valueOf(0.25)));

        boolean weakCloseLong = current.getClosePrice().isLessThan(longThreshold) && highVolume;
        boolean weakCloseShort = current.getClosePrice().isGreaterThan(shortThreshold) && highVolume;

        switch (type) {
            case "LONG": {
                if (weakCloseLong) {
                    return true;
                }
                break;
            }
            case "SHORT": {
                if (weakCloseShort) {
                    return true;
                }
                break;
            }
        }
        return false;
    }

    public void stopPosition (){
        try {
            RequestOptions options = new RequestOptions();
            SyncRequestClient syncRequestClient = SyncRequestClient.create(BinanceUtil.getApiKey(), BinanceUtil.getApiSecret(),
                    options);
            Order orderNew = syncRequestClient.getOrder(series.getName(), id, null);
            switch (type) {
                case "SHORT": {
                    orderNew = syncRequestClient.postOrder(series.getName(),
                            OrderSide.BUY, PositionSide.SHORT, OrderType.MARKET, null, orderNew.getOrigQty().toString(),
                            null, null, null, null, null, null, null, null, null,
                            NewOrderRespType.RESULT);
                    break;
                }
                case "LONG": {
                    orderNew = syncRequestClient.postOrder(series.getName(), ,
                            OrderSide.SELL, PositionSide.LONG, OrderType.MARKET, null, orderNew.getOrigQty().toString(),
                            null, null, null, null, null, null, null, null, null,
                            NewOrderRespType.RESULT);
                    break;
                }
            }
            log.info( "Created CLOSE order: " + id + " " + orderNew.getSymbol() + " ");
            sleep(1000);

        } catch (Exception e) {
            System.out.println(" --------------------------- " + series.getName() + "   closed");
        }
    }
}
