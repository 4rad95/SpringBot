package org.binance.springbot;

import com.binance.api.client.domain.market.Candlestick;
import com.binance.api.client.domain.market.CandlestickInterval;
import com.binance.client.RequestOptions;
import com.binance.client.SyncRequestClient;
import com.binance.client.model.trade.MyTrade;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.apache.commons.lang3.StringUtils;
import org.binance.springbot.analytic.CandellaAnalyse;
import org.binance.springbot.analytic.ClosePosition;
import org.binance.springbot.analytic.OrderBlockFinder;
import org.binance.springbot.analytic.TrendDetector;
import org.binance.springbot.aspect.LoggingAspect;
import org.binance.springbot.dto.*;
import org.binance.springbot.entity.Monitor;
import org.binance.springbot.entity.OpenPosition;
import org.binance.springbot.entity.enums.Type;

import org.binance.springbot.service.*;

import java.math.BigDecimal;

import org.binance.springbot.util.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.ta4j.core.Bar;
import org.ta4j.core.BarSeries;

import java.io.IOException;
import java.math.RoundingMode;
import java.sql.Timestamp;
import java.util.*;
import java.util.logging.Level;
import java.util.stream.Collectors;

import static java.lang.System.currentTimeMillis;
import static java.lang.Thread.sleep;

import static org.binance.springbot.util.BinanceTa4jUtils.*;
import static org.binance.springbot.util.BinanceUtil.*;

import static org.binance.springbot.analytic.TrendDetector.trendDetect;


@SpringBootApplication
public class SpringBotApplication {

	@Autowired
	private  SymbolService symbolService;
	@Autowired
	private  LogUpdateService logUpdateService;
	@Autowired
	private OpenPositionService openPositionService;

	public static final Map<String, BarSeries> timeSeriesCache = Collections.synchronizedMap(new HashMap<>());
	public static final Map<String, BarSeries> timeSeriesCache_t1 = Collections.synchronizedMap(new HashMap<>());

	private static final List<String> badSymbols = new LinkedList<>();
	public static Boolean MAKE_LONG = true;
	public static Boolean MAKE_SHORT = true;
	public static Boolean MAKE_TRADE_AVG = true;
	public static String BLACK_LIST = "";
	public static Integer STOP_NO_LOSS = 100;
	public static Integer WAIT_LIMIT_ORDER = 15;
	public static Long timer = currentTimeMillis();

	private static Integer PAUSE_TIME_MINUTES = 5;
	private static Boolean DO_TRADES = true;
	private static Integer MAX_SIMULTANEOUS_TRADES = 0;
	private static Double TRADE_SIZE_BTC;
	private static Double TRADE_SIZE_USDT;

	private static Boolean BEEP = false;


	private static CandlestickInterval interval = null;
	public static CandlestickInterval interval1 = null;
	public static CandlestickInterval interval2 = null;
	public static BigDecimal startBalance;
	public static String exchangeInfo;

	private static final Logger log = LoggerFactory.getLogger(LoggingAspect.class);
    @Autowired
    private VariantService variantService;

    @Autowired
    private StatisticService statisticService;
	@Autowired
	private MonitorService monitorService;


	public static void main(String[] args) throws Exception {
		ApplicationContext context = SpringApplication.run(SpringBotApplication.class, args);
		SpringBotApplication app = context.getBean(SpringBotApplication.class);
		init();
	//	app.insertSymbols();
		app.process( app );
	}

	public void insertSymbols(SymbolsDto symbolsDto) throws Exception {
			symbolService.insertSymbols(symbolsDto);
	}
	public void insertVariant(VariantDto variantDto) throws Exception{
		variantService.insertVariant(variantDto);
	}
	public void insertOpenPosition(OpenPositionDto openPositionDto) throws Exception {
		openPositionService.insertOpenPosition(openPositionDto);
	}
	public void deleteSymbols(String symbol) throws Exception {
		symbolService.deleteBySymbol(symbol);
	}
	public void insertLogRecord(LogUpdateDto logUpdateDto) throws Exception {
		logUpdateService.insertLogUpdate(logUpdateDto);
	}
	public void deleteSymbolsAll() throws Exception {
		symbolService.deleteAll();
	}
	public void insertMonitor(MonitorDto monitorDto) throws Exception {
		monitorService.insertMonitor(monitorDto);
	}
	public void deleteMonitor(Long id) throws Exception {
		monitorService.deleteById(id);
	}

	public static void init() throws IOException {

		exchangeInfo = BinanceUtil.getExchangeInfo();
		String strPauseTimeMinutes = ConfigUtils
				.readPropertyValue(ConfigUtils.CONFIG_PAUSE_TIME_MINUTES);
		if (StringUtils.isNotEmpty(strPauseTimeMinutes)
				&& StringUtils.isNumeric(strPauseTimeMinutes)) {
			PAUSE_TIME_MINUTES = Integer.valueOf(strPauseTimeMinutes);
		}
		String beep = ConfigUtils
				.readPropertyValue(ConfigUtils.CONFIG_SYSTEM_BEEP);
		if ("true".equalsIgnoreCase(beep)
				|| "1".equals(beep)) {
			BEEP = true;
		}
		// Candle time frame
		String candleInterval = ConfigUtils
				.readPropertyValue(ConfigUtils.CONFIG_BINANCE_TICK_INTERVAL);
		String candleInterval1 = ConfigUtils
				.readPropertyValue(ConfigUtils.CONFIG_BINANCE_ADD1_INTERVAL);
		String candleInterval2 = ConfigUtils
				.readPropertyValue(ConfigUtils.CONFIG_BINANCE_ADD2_INTERVAL);
		CandlestickInterval[] intervals = CandlestickInterval.values();
		for (CandlestickInterval _interval : intervals) {
			if (_interval.getIntervalId().equalsIgnoreCase(candleInterval)) {
				log.info("Setting candlestick interval to: "
						+ candleInterval);
				interval = _interval;
			}
			if (_interval.getIntervalId().equalsIgnoreCase(candleInterval1)) {
				log.info("Setting add1 candlestick interval to: "
						+ candleInterval1);
				interval1 = _interval;
			}
			if (_interval.getIntervalId().equalsIgnoreCase(candleInterval2)) {
				log.info("Setting add2 candlestick interval to: "
						+ candleInterval2);
				interval2 = _interval;
			}
		}
		if (interval == null) {
			interval = CandlestickInterval.FOUR_HOURLY;
			log.info("Using default candlestick interval: "
					+ CandlestickInterval.FOUR_HOURLY.getIntervalId());
		}

		// Trading settings
		String strDoTrades = ConfigUtils
				.readPropertyValue(ConfigUtils.CONFIG_TRADING_DO_TRADES);
		if ("false".equalsIgnoreCase(strDoTrades) || "0".equals(strDoTrades)) {
			DO_TRADES = false;
		}
		if (DO_TRADES) {
			MAX_SIMULTANEOUS_TRADES = Integer
					.valueOf(ConfigUtils
							.readPropertyValue(ConfigUtils.CONFIG_TRADING_MAX_SIMULTANEOUS_TRADES));

			TRADE_SIZE_USDT = Double
					.valueOf(ConfigUtils
							.readPropertyValue(ConfigUtils.CONFIG_TRADING_TRADE_SIZE_USDT));
			TRADE_SIZE_BTC = Double
					.valueOf(ConfigUtils
							.readPropertyValue(ConfigUtils.CONFIG_TRADING_TRADE_SIZE_BTC));

			String makeLong = ConfigUtils
					.readPropertyValue(ConfigUtils.CONFIG_TRADING_LONG);
			MAKE_LONG = "true".equalsIgnoreCase(makeLong)
					|| "1".equals(makeLong);

			String makeShort = ConfigUtils
					.readPropertyValue(ConfigUtils.CONFIG_TRADING_SHORT);
			MAKE_SHORT = "true".equalsIgnoreCase(makeShort)
					|| "1".equals(makeShort);
			String makeAvg = ConfigUtils
					.readPropertyValue(ConfigUtils.CONFIG_TRADING_AVRG);
			MAKE_TRADE_AVG = "true".equalsIgnoreCase(makeAvg)
					|| "1".equals(makeAvg);
			BLACK_LIST = ConfigUtils
					.readPropertyValue(ConfigUtils.CONFIG_TRADING_BLACKLIST);
			String strStopNoLoss = ConfigUtils
					.readPropertyValue(ConfigUtils.CONFIG_TRADING_STOPNOLOSS);
			STOP_NO_LOSS = Integer.valueOf(strStopNoLoss);
			String waitLimitOrder = ConfigUtils
					.readPropertyValue(ConfigUtils.CONFIG_TRADING_WAIT_LIMIT);
			WAIT_LIMIT_ORDER = Integer.valueOf(waitLimitOrder);

		}
		try {

			BinanceUtil.init(ConfigUtils.readPropertyValue(ConfigUtils.CONFIG_BINANCE_API_KEY),
					ConfigUtils.readPropertyValue(ConfigUtils.CONFIG_BINANCE_API_SECRET));

			startBalance = printBalance();
//			Runnable InputString = new InputString();
//			Thread thread = new Thread(InputString);
//			thread.start();
		} catch (Exception e) {
			log.info("Unable to generate Binance clients!", e);
		}
	}

	public  void process( SpringBotApplication app ) {
		try {
			LogUpdateDto logUpdateDto = LogUpdateDto.builder().msg("Start application").time(BinanceUtil.dateTimeFormat(currentTimeMillis())).build();
			insertLogRecord(logUpdateDto);
			List<String> symbols = BinanceUtil.getBitcoinSymbols();
			generateTimeSeriesCache( symbols);
			Long timeToWait = PAUSE_TIME_MINUTES * 60 * 1000L;
			if (timeToWait < 0) {
				timeToWait = 5 * 60 * 1000L;
			}
			Runnable r = () -> {
                try {
                    mainProcess(symbols);
                } catch (Exception e) {
					System.out.println("-----");
                }
            };
			Runnable update = () -> {
				try {
					updateAll(symbols);
				} catch (Exception e) {
				System.out.println("-----");
			}
			};
			Runnable checkMonitor = () -> {
				try {
					checkMonitorCoins();
				} catch (Exception e) {
					System.out.println("-----");
				}
			};
			int wait = 0;
			while (true) {
				try {
					wait++;
					checkClosePosition();

					sleep(5000);
					Thread mainThread = new Thread(r, "Search thread");
					mainThread.start();
					Thread checkMonitorC = new  Thread(checkMonitor,"chek");
					checkMonitorC.start();
					if (wait >= 20) {
						Thread updateThread = new Thread(update, "Update symbols");
						updateThread.start();
						wait = 1;
						sleep(300000);
					}

					sleep(timeToWait);

				} catch (Exception e) {
					System.out.println("Error in 1 : " + e);
				}
			}

		} catch (Exception e) {
			log.info("Unable to get symbols", e);
		}
	}

	private  Integer generateTimeSeriesCache(List<String> symbols) {
		Integer count = 0;
		for (String symbol : symbols) {
		//	if (check(symbol)) {
			//	log.info( "Generating time series for " + symbol);
				try {
					int limit = 500;
					BarSeries series = null;
					if (timeSeriesCache.get(symbol) == null) {
						List<Candlestick> candlesticks = BinanceUtil.getCandelSeries(symbol, interval.getIntervalId(), limit);
						series = BinanceTa4jUtils.convertToTimeSeries(candlesticks, symbol, interval.getIntervalId());
					} else {
						series = timeSeriesCache.get(symbol);
					}
					timeSeriesCache.put(symbol, series);
//					PivotCalculator.PivotPoints pivotPoints = calculatePivotPoints(series);
//					System.out.println(series.getName() +" " +pivotPoints);

 					Map<String, String[]> orderBlocks = OrderBlockFinder.findOrderBlocks(series , series.getEndIndex());
		//			Map<String, Integer> orderBlocks = new HashMap<>();
				//	Map<Integer, OrderBlock> allOrderBlocks  = findAllOrderBlocks(series);
//					Integer[] keys = allOrderBlocks.keySet().toArray(new Integer[0]);
//					OrderBlock[] values = allOrderBlocks.values().toArray(new OrderBlock[0]);
//					if (values[0].getMove() > 0 ) {
//						orderBlocks.put("SellOrderBlock",keys[1]);
//						orderBlocks.put("BuyOrderBlock",keys[0]);
//					} else {
//						orderBlocks.put("SellOrderBlock",keys[0]);
//						orderBlocks.put("BuyOrderBlock",keys[1]);
//
//					}
//					if ((orderBlocks.get("BuyOrderBlock").doubleValue()<0.00)
//						||(orderBlocks.get("SellOrderBlock").doubleValue() <0.00)){
//						timeSeriesCache.remove(symbol);
//						candlesticks = BinanceUtil.getCandelSeries(symbol, interval.getIntervalId(), limit*2);
//						series = BinanceTa4jUtils.convertToTimeSeries(candlesticks, symbol, interval.getIntervalId());
//						timeSeriesCache.put(symbol, series);
//						orderBlocks = OrderBlockFinder.findOrderBlocks(series , series.getEndIndex());
//					}
					///  String [Low, High, Imb ]
//					String imbBuy  = BigDecimal.valueOf(series.getBar(orderBlocks.get("BuyOrderBlock")+2).getLowPrice().doubleValue()).toString();
//					String imbSell = BigDecimal.valueOf(series.getBar(orderBlocks.get("SellOrderBlock")+2).getHighPrice().doubleValue()).toString();
					String lowBuy = orderBlocks.get("BuyOrderBlock")[0].toString();
					String highBuy = orderBlocks.get("BuyOrderBlock")[1].toString();
					String imbBuy = orderBlocks.get("BuyOrderBlock")[2].toString();

					SymbolsDto symbolDto = SymbolsDto.builder()
							.symbols(symbol)
							.highBuy(orderBlocks.get("BuyOrderBlock")[1].toString())
							.lowBuy(orderBlocks.get("BuyOrderBlock")[0].toString()).
							imbBuy(orderBlocks.get("BuyOrderBlock")[2].toString()).
							highSell(orderBlocks.get("SellOrderBlock")[1].toString()).
							lowSell(orderBlocks.get("SellOrderBlock")[0].toString()).
							imbSell(orderBlocks.get("SellOrderBlock")[2].toString()).
							build();

//							SymbolsDto symbolDto = SymbolsDto.builder().symbols(symbol).highBuy(BigDecimal.valueOf(series.getBar(orderBlocks.get("BuyOrderBlock")).getHighPrice().doubleValue()).toString()).
//							lowBuy(BigDecimal.valueOf(series.getBar(orderBlocks.get("BuyOrderBlock")).getLowPrice().doubleValue()).toString()).
//							imbBuy(imbBuy).
//							highSell(BigDecimal.valueOf(series.getBar(orderBlocks.get("SellOrderBlock")).getHighPrice().doubleValue()).toString()).
//							lowSell(BigDecimal.valueOf(series.getBar(orderBlocks.get("SellOrderBlock")).getLowPrice().doubleValue()).toString()).
//							imbSell(imbSell).
//							build();

					insertSymbols(symbolDto);
					count++;
				} catch (Exception e) {


					System.out.println("\u001B[32m" + symbol + "  -  Not used symbol !!! \u001B[0m");
					badSymbols.add(symbol);
				}
			}
//		}
		return count;
	}
public  void mainProcess(List<String> symbols) throws Exception {

//	try {
		Long t0 = currentTimeMillis();
//		int seconds = (int) ((t0 - SpringBotApplication.timer) / 1000);
//		int minutes = seconds / 60;
//		int hours = minutes / 60;
//		minutes = minutes - hours * 60;
//		seconds = seconds - minutes * 60;
//		String formattedTime = String.format("%d:%02d:%02d", hours, minutes, seconds);
//		sleep(300);
//		log.info("--------------------------------------------------------------------------------------------------------------------");
//		log.info( "\u001B[36m SpringBot 1.00 ( Beta 0.01!)    \u001B[0m");
//		log.info("--------------------------------------------------------------------------------------------------------------------");
//		log.info("\u001B[32m Start time       : " + new Date(timer) + " \u001B[0m ");
//		log.info("\u001B[32m Execute time     : " + formattedTime + " \u001B[0m ");
//		log.info("\u001B[32m Start Balance    : " + startBalance + "               Current  Balance : " + printBalance() + " \u001B[0m ");
//		log.info("--------------------------------------------------------------------------------------------------------------------");
//
//	} catch (Exception e) {
//		System.out.println(e);
//	}
//	 t0 = currentTimeMillis();
	List<SymbolsDto> listSymbols =  symbolService.getAll();
	System.out.println( "\u001B[36m  !---  SpringBot  ---!  \u001B[0m");
	variantService.deleteAllAndResetSequence();

	for (SymbolsDto symbol : listSymbols) {
		try {
			updateSymbol(symbol.getSymbols());
			checkSymbols(symbol);
		} catch (Exception e) {
			log.info( "Error checking symbol "
					+ symbol, e);
		}
	}
	Long t1 = currentTimeMillis() - t0;
	log.info("\u001B[32m --- All symbols analyzed, time elapsed: "
			+ (t1 / 1000.0) + " seconds. \u001B[0m");
	LogUpdateDto logUpdateDto = LogUpdateDto.builder().msg(" All symbols analyzed, time elapsed: "  + (t1 / 1000.0) + " seconds. ").time(BinanceUtil.dateTimeFormat(currentTimeMillis())).build();
	insertLogRecord(logUpdateDto);
	// updateAll();
}
	private <GeneralException extends Throwable> void updateSymbol(String symbol) throws Exception {

			Long t0 = currentTimeMillis();
            List<Candlestick> latestCandlesticks = BinanceUtil.getLatestCandlestickBars(symbol, interval);
            BarSeries series = timeSeriesCache.get(symbol);
            if (BinanceTa4jUtils.isSameTick(latestCandlesticks.get(1), series.getLastBar())) {
                updateLastTick(symbol, latestCandlesticks.get(1));
            } else {
                updateLastTick(symbol, latestCandlesticks.get(0));
                series.addBar(BinanceTa4jUtils.convertToTa4jTick(latestCandlesticks.get(1)));
            }
	}

	private static void updateLastTick(String symbol, Candlestick candlestick) {
		BarSeries series = timeSeriesCache.get(symbol);
		List<Bar> seriesTick = series.getBarData();
		seriesTick.remove(series.getEndIndex());
		seriesTick.add(BinanceTa4jUtils.convertToTa4jTick(candlestick));
	}

	private void checkSymbols(SymbolsDto symbolsDto) throws Exception {


		if (Double.valueOf(symbolsDto.getLowSell())>Double.valueOf(symbolsDto.getImbSell())
			&& Double.valueOf(symbolsDto.getHighBuy())< Double.valueOf(symbolsDto.getImbBuy())){
		//	Double price = timeSeriesCache.get(symbolsDto.getSymbols()).getLastBar().getClosePrice().doubleValue(); // BinanceTa4jUtils.getCurrentPrice(symbolsDto.getSymbols()).doubleValue();
			Bar curentBar =  timeSeriesCache.get(symbolsDto.getSymbols()).getLastBar();
			if  (!( curentBar.getHighPrice().doubleValue() > Double.valueOf(symbolsDto.getHighSell()))
					&&
					!(curentBar.getLowPrice().doubleValue() < Double.valueOf(symbolsDto.getLowBuy()))
			)
			{ return;}
			if (!openPositionService.getOpenPositionSymbol(symbolsDto.getSymbols()))  {

		//	int trend = trendDetect(timeSeriesCache.get(symbolsDto.getSymbols()));
		//		int trend = detectTrendWithOB(timeSeriesCache.get(symbolsDto.getSymbols()));
		// 			TrendDetector.TrendResult result =  TrendDetector.detectTrendWithExtremes(timeSeriesCache.get(symbolsDto.getSymbols()), 150,5);
    	//	 if (trend == 0) { return;}

		if (
				curentBar.getLowPrice().doubleValue()  < Double.valueOf(symbolsDto.getLowBuy()))
			{
			CandellaAnalyse candellaAnalyse = new CandellaAnalyse(timeSeriesCache.get(symbolsDto.getSymbols()), symbolsDto.getLowBuy(),symbolsDto.getHighBuy());
			if (candellaAnalyse.getTrend() > 0) {
					newMonitorCoin("LONG",symbolsDto.getSymbols(),symbolsDto.getHighBuy(),  symbolsDto.getLowBuy(),candellaAnalyse.getPointHigh());}
		}

		else if ( // trend < 0
				curentBar.getHighPrice().doubleValue()  > Double.valueOf(symbolsDto.getHighSell()))
				{

     		CandellaAnalyse candellaAnalyse = new CandellaAnalyse(timeSeriesCache.get(symbolsDto.getSymbols()), symbolsDto.getLowSell(),symbolsDto.getHighSell());
			if (candellaAnalyse.getTrend()  < 0) {
					newMonitorCoin("SHORT",symbolsDto.getSymbols(),symbolsDto.getLowSell(), symbolsDto.getHighSell(), candellaAnalyse.getPointLow());}
			}

		}}
	}

	public Map<String,Long> startPosition(VariantDto variantDto) throws InterruptedException, JsonProcessingException {
		String quality = BinanceUtil.getAmount(variantDto.getSymbol(), Double.valueOf(variantDto.getPrice()),TRADE_SIZE_USDT);

		org.binance.springbot.task.Position position = new org.binance.springbot.task.Position(variantDto.getSymbol(),variantDto.getType(),quality ,variantDto.getEnterPrice());
		Map<String,Long> mapa = new HashMap<String,Long>();
		if (variantDto.getType() == "SHORT") {
			Long idPosition = position.openPositionShort();
			while (!position.getStatus(idPosition, variantDto.getSymbol())) {

				sleep(1000);
			}
			org.binance.springbot.task.Position positionSP = new org.binance.springbot.task.Position(idPosition,variantDto.getSymbol());
			Long[] idSP = positionSP.stopPositionShort(variantDto.getStop(),variantDto.getProffit(),variantDto.getEnterPrice());
			mapa.put("id",idPosition);
			mapa.put("stop",idSP[0]);
			mapa.put("profit",idSP[1]);
			mapa.put("profit2",idSP[2]);
		}
		if (variantDto.getType() == "LONG") {
			Long idPosition = position.openPositionLong();
			while (!position.getStatus(idPosition, variantDto.getSymbol())) {

			   sleep(1000);
			}
			org.binance.springbot.task.Position positionSP = new org.binance.springbot.task.Position(idPosition,variantDto.getSymbol());
			Long[] idSP = positionSP.stopPositionLong(variantDto.getStop(),variantDto.getProffit(),variantDto.getEnterPrice());
			mapa.put("id",idPosition);
			mapa.put("stop",idSP[0]);
			mapa.put("profit",idSP[1]);
			mapa.put("profit2",idSP[2]);
		}
		return mapa;
	}
	public void checkClosePosition(){
		List<OpenPosition> openPositionDtoList = openPositionService.getAll();
		if (!openPositionDtoList.isEmpty()) {
			RequestOptions options = new RequestOptions();
			SyncRequestClient syncRequestClient = SyncRequestClient.create(getApiKey(), getApiSecret(), options);

			for (OpenPosition entity : openPositionDtoList) {
				List<MyTrade> trades = syncRequestClient.getAccountTrades(entity.getSymbol(), null, null, null, 100);

				MyTrade lastTrade = trades.get(trades.size() - 1);

				if (lastTrade.getRealizedPnl().doubleValue() != 0) {
					long lastTime = lastTrade.getTime();

					List<MyTrade> filteredTrades = trades.stream()
							.filter(trade -> trade.getTime() == lastTime)
							.collect(Collectors.toList());

					BigDecimal totalPnl = filteredTrades.stream()
							.map(trade -> BigDecimal.valueOf(trade.getRealizedPnl().doubleValue()))
							.reduce(BigDecimal.ZERO, BigDecimal::add);

					BigDecimal totalCommission = filteredTrades.stream()
							.map(trade -> BigDecimal.valueOf(trade.getCommission().doubleValue()))
							.reduce(BigDecimal.ZERO, BigDecimal::add);


					StatisticDto statisticDto = StatisticDto.builder()
							.pnl(totalPnl.toString())
							.symbols(lastTrade.getSymbol())
							.comission(totalCommission.toString())
							.type(Type.valueOf(lastTrade.getPositionSide()))
							.startDateTime(convertTimestampToDate(lastTime))
							.duration(BinanceUtil.timeFormat(lastTime - trades.get(trades.size() - 2).getTime()))
							.build();

				statisticService.insertStatistic(statisticDto);
				openPositionService.deleteById(entity.getId());


				try {
					syncRequestClient.cancelOrder(entity.getSymbol(), entity.getProfitId(), null);
				} catch (Exception e) {
					System.err.println("Failed to cancel profit order: " + e.getMessage());
				}

				try {
					syncRequestClient.cancelOrder(entity.getSymbol(), entity.getStopId(), null);
				} catch (Exception e) {
					System.err.println("Failed to cancel stop order: " + e.getMessage());
				}
					ClosePosition closePosition =  new ClosePosition(timeSeriesCache.get(entity.getSymbol()),entity.getType(),lastTrade.getOrderId());
					System.out.println( entity.getSymbol() + "  " + closePosition.checkPosition());

				}
		}
	}}


	public void updateAll(List<String> symbols) throws Exception {
		sleep(180000);
		Long t0 = currentTimeMillis();
		timeSeriesCache_t1.clear();
		timeSeriesCache.clear();
		deleteSymbolsAll();
		int i = generateTimeSeriesCache( symbols);
		LogUpdateDto logUpdateDto = LogUpdateDto.builder()
				.msg("Update all symbols time elapsed: " + BinanceUtil.timeFormat(currentTimeMillis()-t0) +".  "+ i+" Symbols add.")
				.time(BinanceUtil.dateTimeFormat(currentTimeMillis()))
				.build();
		insertLogRecord(logUpdateDto);
	}

	private static double calculateTakeProfit(double entry, double stopLoss, double riskRewardRatio, boolean isSell) {
		double risk = Math.abs(entry - stopLoss);

		if (isSell) return entry - (risk * riskRewardRatio);
		return entry + (risk * riskRewardRatio);
	}


	private void newMonitorCoin (String type, String symbol, String start, String stop, String profit) throws Exception {

			List<Monitor> monitorCoins = monitorService.getAll();
				for (Monitor entry : monitorCoins) {
					if (entry.getSymbol() == symbol) {
						return;
					}
			}
		MonitorDto monitorDto = MonitorDto.builder().type(type).symbol(symbol).start(start).stop(stop).profit(profit).build();
		insertMonitor(monitorDto);


	}
	private void checkMonitorCoins() throws Exception {
		List<Monitor> monitorCoins = monitorService.getAll();
		for (Monitor entry : monitorCoins) {

			Bar curentBar =  timeSeriesCache.get(entry.getSymbol()).getLastBar();
			SymbolsDto symbolsDto = symbolService.getSymbol(entry.getSymbol());

			if (entry.getType() == "LONG") {
				if (curentBar.getClosePrice().doubleValue() < Double.valueOf(entry.getStart())) {
					log.info("OPEN LONG "+ entry.getSymbol());

	    //		String proffit = OrderBlockFinder.findUpImbStop(symbolsDto.getSymbols()).toString();
		//		String proffit = String.valueOf(roundToDecimalPlaces(calculateTakeProfit(Double.valueOf(curentBar.getClosePrice().doubleValue()),Double.valueOf(symbolsDto.getLowBuy()),3, false),countDecimalPlaces(curentBar.getClosePrice().doubleValue())));
					String proffit = entry.getProfit();
//					if (Double.valueOf(proffit) <0) {
//					proffit =symbolsDto.getImbBuy();
//				}
				String stop   = BigDecimal.valueOf(Double.valueOf(symbolsDto.getLowBuy())).multiply(new BigDecimal("0.98")).setScale(BigDecimal.valueOf(Double.valueOf(symbolsDto.getLowSell())).scale(), RoundingMode.HALF_UP).toString();

				VariantDto variantDto = VariantDto.builder().time(Timestamp.valueOf(java.time.LocalDateTime.now())).symbol(symbolsDto.getSymbols())
						.type("LONG").price(curentBar.getClosePrice().toString()).stop(stop).proffit(proffit).enterPrice(curentBar.getClosePrice().toString())
						.build();

				if (Double.valueOf(proffit) > 0) {
	     				insertVariant(variantDto);
					    double k = (Double.valueOf(proffit)-curentBar.getClosePrice().doubleValue())/(curentBar.getClosePrice().doubleValue()-Double.valueOf(symbolsDto.getLowBuy()));
						if ((openPositionService.getCount() < MAX_SIMULTANEOUS_TRADES )&&(k > 0)) {
						Map<String, Long> id =  startPosition(variantDto);
						OpenPositionDto openPositionDto =OpenPositionDto.builder().symbol(symbolsDto.getSymbols()).idBinance(id.get("id")).stopId(id.get("stop")).profitId(id.get("profit")).profit2Id(id.get("profit2")).type("LONG").time(Timestamp.valueOf(java.time.LocalDateTime.now())).build();
						log.info(" [LONG] " + variantDto.toString());
						log.info(" [LONG] " + openPositionDto.toString());
			insertOpenPosition(openPositionDto);
			deleteMonitor(entry.getId());
		}
			}

				}
				if (Double.valueOf(entry.getStop()) > curentBar.getClosePrice().doubleValue()) {
					deleteMonitor(entry.getId());
					log.info("[CLOSE LONG] "+ entry.getSymbol());
				}

			}
			if (entry.getType() == "SHORT") {
				if (curentBar.getClosePrice().doubleValue() <  Double.valueOf(entry.getStart())) {
					log.info("[OPEN SHORT] "+ entry.getSymbol());

				//	String proffit = OrderBlockFinder.findDownImbStop(symbolsDto.getSymbols()).toString();
				// String proffit = String.valueOf(roundToDecimalPlaces(calculateTakeProfit(curentBar.getClosePrice().doubleValue(),Double.valueOf(symbolsDto.getHighSell()),3, true),countDecimalPlaces(curentBar.getClosePrice().doubleValue())));
					String proffit = entry.getProfit();
//				if (Double.valueOf(proffit) <0) {
//					proffit =symbolsDto.getImbSell();
//				}
				String stop   = BigDecimal.valueOf(Double.valueOf(symbolsDto.getHighSell())).multiply(new BigDecimal("1.02")).setScale(BigDecimal.valueOf(Double.valueOf(symbolsDto.getHighSell())).scale(), RoundingMode.HALF_UP).toString();
				VariantDto variantDto = VariantDto.builder().time(Timestamp.valueOf(java.time.LocalDateTime.now())).symbol(symbolsDto.getSymbols())
						.type("SHORT").price(curentBar.getClosePrice().toString()).stop(stop).proffit(proffit).enterPrice(curentBar.getClosePrice().toString())
						.build();

				if (Double.valueOf(proffit) > 0) {
			    		insertVariant(variantDto);
						double k = (curentBar.getClosePrice().doubleValue()-curentBar.getClosePrice().doubleValue())/(Double.valueOf(symbolsDto.getHighSell())-curentBar.getClosePrice().doubleValue());
						if ((openPositionService.getCount() < MAX_SIMULTANEOUS_TRADES ) && ( k > 0)) {
							Map<String, Long> id =  startPosition(variantDto);
							OpenPositionDto openPositionDto = OpenPositionDto.builder().symbol(symbolsDto.getSymbols()).idBinance(id.get("id")).stopId(id.get("stop")).profitId(id.get("profit")).profit2Id(id.get("profit2")).type("SHORT").time(Timestamp.valueOf(java.time.LocalDateTime.now())).build();
							log.info(" [SHORT] " + variantDto.toString());
							log.info(" [SHORT] " + openPositionDto.toString());
							insertOpenPosition(openPositionDto);
							deleteMonitor(entry.getId());
						}
						}
				}
				if (Double.valueOf(entry.getStop()) < curentBar.getClosePrice().doubleValue()) {
					deleteMonitor(entry.getId());
					log.info("[CLOSE SHORT] "+ entry.getSymbol());
				}
			}

		}
	}
}
