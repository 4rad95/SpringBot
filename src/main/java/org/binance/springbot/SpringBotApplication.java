package org.binance.springbot;

import com.binance.api.client.domain.market.Candlestick;
import com.binance.api.client.domain.market.CandlestickInterval;
import com.binance.client.RequestOptions;
import com.binance.client.SyncRequestClient;
import com.binance.client.model.trade.MyTrade;
import org.apache.commons.lang3.StringUtils;
import org.binance.springbot.aspect.LoggingAspect;
import org.binance.springbot.dto.OpenPositionDto;
import org.binance.springbot.dto.StatisticDto;
import org.binance.springbot.dto.SymbolsDto;
import org.binance.springbot.dto.VariantDto;
import org.binance.springbot.entity.OpenPosition;
import org.binance.springbot.entity.enums.Type;
import org.binance.springbot.repo.OpenPositionRepository;
import org.binance.springbot.repo.StatisticRepository;
import org.binance.springbot.repo.SymbolsRepository;

import org.binance.springbot.service.OpenPositionService;
import org.binance.springbot.service.StatisticService;
import org.binance.springbot.service.VariantService;
import org.binance.springbot.task.Position;
import org.binance.springbot.util.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.binance.springbot.service.SymbolService;
import org.springframework.context.ApplicationContext;
import org.ta4j.core.Bar;
import org.ta4j.core.BarSeries;
// import org.ta4j.core.BarSeries;

import javax.swing.text.html.parser.Entity;
import java.io.IOException;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.*;

import static com.fasterxml.jackson.databind.type.LogicalType.DateTime;
import static java.lang.System.currentTimeMillis;
import static java.lang.Thread.sleep;

import static org.binance.springbot.util.BinanceUtil.*;


@SpringBootApplication
public class SpringBotApplication {
	@Autowired
	public static SymbolsRepository symbolsRepository;
	@Autowired
	private  SymbolService symbolService;
	@Autowired
	private OpenPositionService openPositionService;

	public static final Map<String, Integer> frozenTrade = Collections.synchronizedMap(new HashMap<String, Integer>());
	// We will store time series for every symbol
	public static final Map<String, BarSeries> timeSeriesCache = Collections.synchronizedMap(new HashMap<String, BarSeries>());
	private static final Map<String, String> openTradesLong = Collections.synchronizedMap(new HashMap<String, String>());
	private static final Map<String, String> openTradesShort = Collections.synchronizedMap(new HashMap<String, String>());
	private static final List<String> ordersToBeClosed = Collections.synchronizedList(new LinkedList<String>());
//	private static final List<Position> closedPositions = Collections.synchronizedList(new LinkedList<Position>());
	private static final List<String> badSymbols = new LinkedList<String>();
	public static Boolean MAKE_LONG = true;
	public static Boolean MAKE_SHORT = true;
	public static Boolean MAKE_TRADE_AVG = true;
	public static String BLACK_LIST = "";
	public static Integer STOP_NO_LOSS = 100;
	public static Integer WAIT_LIMIT_ORDER = 15;
	public static Long timer = currentTimeMillis();
	public static Integer WAIT_FROZEN = 20;
	// Config params
	private static Integer PAUSE_TIME_MINUTES = 5;
	private static Boolean DO_TRADES = true;
	private static Integer MAX_SIMULTANEOUS_TRADES = 0;
	private static Double TRADE_SIZE_BTC;
	private static Double TRADE_SIZE_USDT;
	private static Double STOPLOSS_PERCENTAGE = 1.00;
	private static Boolean DO_TRAILING_STOP = false;
	private static String TRADING_STRATEGY;
	private static Boolean BEEP = false;

	private static Integer IDENT_LIMIT_ORDER = 20;
	private static Integer closedTrades = 0;
	private static Double totalProfit = 0.0;
	private static Double totalProfitLong = 0.0;
	private static Integer closedTradesLong = 0;
	private static Double totalProfitShort = 0.0;
	private static Integer closedTradesShort = 0;


	private static CandlestickInterval interval = null;
	private static CandlestickInterval interval1 = null;
	private static CandlestickInterval interval2 = null;
	private static BigDecimal startBalance;

	private static final Logger log = LoggerFactory.getLogger(LoggingAspect.class);
    @Autowired
    private VariantService variantService;
    @Autowired
    private static OpenPositionRepository openPositionRepository;
    @Autowired
    private StatisticRepository statisticRepository;
    @Autowired
    private StatisticService statisticService;

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
// public List<OpenPositionDto>


	public static void init() throws IOException {
		// Pause time
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
			STOPLOSS_PERCENTAGE = Double
					.valueOf(ConfigUtils
							.readPropertyValue(ConfigUtils.CONFIG_TRADING_STOPLOSS_PERCENTAGE));
			TRADE_SIZE_USDT = Double
					.valueOf(ConfigUtils
							.readPropertyValue(ConfigUtils.CONFIG_TRADING_TRADE_SIZE_USDT));
			TRADE_SIZE_BTC = Double
					.valueOf(ConfigUtils
							.readPropertyValue(ConfigUtils.CONFIG_TRADING_TRADE_SIZE_BTC));

			String strDoTrailingStop = ConfigUtils
					.readPropertyValue(ConfigUtils.CONFIG_TRADING_DO_TRAILING_STOP);
			if ("true".equalsIgnoreCase(strDoTrailingStop)
					|| "1".equals(strDoTrailingStop)) {
				DO_TRAILING_STOP = true;
			}
			TRADING_STRATEGY = ConfigUtils
					.readPropertyValue(ConfigUtils.CONFIG_TRADING_STRATEGY);

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
			String waiFrozen = ConfigUtils
					.readPropertyValue(ConfigUtils.CONFIG_TRADING_WAIT_FROZEN);
			WAIT_FROZEN = Integer.valueOf(waiFrozen);
			String identLimit = ConfigUtils
					.readPropertyValue(ConfigUtils.CONFIG_TRADING_IDENT_LIMIT);
			IDENT_LIMIT_ORDER = Integer.valueOf(identLimit);

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

			List<String> symbols = BinanceUtil.getBitcoinSymbols();
			generateTimeSeriesCache( symbols);
			Long timeToWait = PAUSE_TIME_MINUTES * 60 * 1000L;
			if (timeToWait < 0) {
				timeToWait = 5 * 60 * 1000L;
			}
//			{
//				Frozen frozen = new Frozen(WAIT_FROZEN);
//				Thread thread = new Thread(frozen);
//				frozen.thisThread = thread;
//				thread.start();
//			}
			List<String> finalSymbols = symbols;
			Runnable r = () -> mainProcess(finalSymbols);
			int wait = 0;
//
			while (true) {
				try {
					wait++;
//					List<String> finalSymbols = symbols;
//					Runnable r = () -> mainProcess(finalSymbols);
//					Thread myThread = new Thread(r, "Search thread");
					Thread mainThread = new Thread(r, "Search thread");
					mainThread.start();
					checkClosePosition();
					if (wait == 20 ) {
						updateAll();
						wait = 0;
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

	private  void generateTimeSeriesCache(List<String> symbols) {

		for (String symbol : symbols) {
		//	if (check(symbol)) {
				log.info( "Generating time series for " + symbol);
				try {
					List<Candlestick> candlesticks = BinanceUtil.getCandelSeries(symbol, interval.getIntervalId(), 1000);
					BarSeries series = BinanceTa4jUtils.convertToTimeSeries(candlesticks, symbol, interval.getIntervalId());
//					PivotCalculator.PivotPoints pivotPoints = calculatePivotPoints(series);
//					System.out.println(series.getName() +" " +pivotPoints);

					Map<String, Integer> orderBlocks = OrderBlockFinder.findOrderBlocks(series , series.getEndIndex());
					System.out.println("Sell Order Block Index: " + orderBlocks.get("SellOrderBlock"));
					System.out.println("Buy Order Block Index: " + orderBlocks.get("BuyOrderBlock"));
					String imbBuy = series.getBar(orderBlocks.get("BuyOrderBlock")+2).getLowPrice().toString();
					String imbSell =series.getBar(orderBlocks.get("SellOrderBlock")+2).getLowPrice().toString();

							SymbolsDto symbolDto = SymbolsDto.builder().symbols(symbol).highBuy(series.getBar(orderBlocks.get("BuyOrderBlock")).getHighPrice().toString()).
							lowBuy(series.getBar(orderBlocks.get("BuyOrderBlock")).getLowPrice().toString()).
							imbBuy(imbBuy).
							highSell(series.getBar(orderBlocks.get("SellOrderBlock")).getHighPrice().toString()).
							lowSell(series.getBar(orderBlocks.get("SellOrderBlock")).getLowPrice().toString()).
							imbSell(imbSell).
							build();

					insertSymbols(symbolDto);
					timeSeriesCache.put(symbol, series);

				} catch (Exception e) {
					System.out.println("\u001B[32m" + symbol + "  -  Not used symbol !!! \u001B[0m");
					badSymbols.add(symbol);
				}
			}
//		}
	}
public  void mainProcess(List<String> symbols) {

	try {
		Long t0 = currentTimeMillis();

		int seconds = (int) ((t0 - SpringBotApplication.timer) / 1000);
		int minutes = seconds / 60;
		int hours = minutes / 60;
		minutes = minutes - hours * 60;
		seconds = seconds - minutes * 60;
		String formattedTime = String.format("%d:%02d:%02d", hours, minutes, seconds);
		log.info("--------------------------------------------------------------------------------------------------------------------");
		log.info( "\u001B[36m CopyBot 2.00 ( test Edition mit Spring!)    \u001B[0m");
		//		Log.info(CopyBot.class, "\u001B[36m Using new re-Made Trade Strategy  \u001B[0m");
		log.info(" Open trades LONG: " + openTradesLong.keySet().size() + " SHORT:" + openTradesShort.keySet().size());
		log.info(" LONG:  " + openTradesLong.keySet());
		log.info(" SHORT: " + openTradesShort.keySet());
		log.info("--------------------------------------------------------------------------------------------------------------------");
		log.info("\u001B[32m Start time       : " + new Date(timer) + " \u001B[0m ");
		log.info("\u001B[32m Execute time     : " + formattedTime + " \u001B[0m ");
		log.info("\u001B[32m Start Balance    : " + startBalance + "               Current  Balance : " + printBalance() + " \u001B[0m ");
		//	Log.info(CopyBot.class, "--------------------------------------------------------------------------------------------------------------------");
		log.info("\u001B[32m Max. Position:   : " + MAX_SIMULTANEOUS_TRADES + "                         USDT Size : " + TRADE_SIZE_USDT + " \u001B[0m ");
		log.info("--------------------------------------------------------------------------------------------------------------------");
//		//	Log.info(CopyBot.class, "|Start time          | Work time | Symbol        | Open price       | Current price    | Stop loss        |  Profit");
//		//	outputPosition();
		if (DO_TRADES && closedTrades > 0) {
			log.info(
					"\u001B[32mClosed trades: " + closedTrades + " Long: " + closedTradesLong + " Short: " + closedTradesShort
							+ ", total profit: " + String.format("%.8f", totalProfit)
							+ ", LONG: " + String.format("%.2f", totalProfitLong)
							+ ", SHORT: " + String.format("%.2f", totalProfitShort) + "\u001B[0m ");
			log.info("--------------------------------------------------------------------------------------------------------------------");

		}
////            if ((openTradesLong.keySet().size() + openTradesShort.keySet().size()) >= MAX_SIMULTANEOUS_TRADES) {
////                // We will not continue trading... avoid checking
////                checkStrategyOpenPosition(openTradesLong);
////                checkStrategyOpenPosition(openTradesShort);
////            }
//
	} catch (Exception e) {
		System.out.println(e);
	}
	Long t0 = currentTimeMillis();
	List<SymbolsDto> listSymbols =  symbolService.getAll();
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
	log.info("All symbols analyzed, time elapsed: "
			+ (t1 / 1000.0) + " seconds.");

}

	private <GeneralException extends Throwable> void updateSymbol(String symbol) throws Exception {

		if (frozenTrade.get(symbol) == null) {

			Long t0 = currentTimeMillis();
            List<Candlestick> latestCandlesticks = BinanceUtil.getLatestCandlestickBars(symbol, interval);
            BarSeries series = timeSeriesCache.get(symbol);
            if (BinanceTa4jUtils.isSameTick(latestCandlesticks.get(1), series.getLastBar())) {
                // We are still in the same tick - just update the last tick with the fresh data
                updateLastTick(symbol, latestCandlesticks.get(1));
            } else {
                // We have just got a new tick - update the previous one and include the new tick
                updateLastTick(symbol, latestCandlesticks.get(0));
                series.addBar(BinanceTa4jUtils.convertToTa4jTick(latestCandlesticks.get(1)));
            }
            // Now check the TA strategy with the refreshed time series
            int endIndex = series.getEndIndex();
			// --
	//		updateSQLSymbol(symbol);

            //---------------------------------------------------

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
		if (openPositionService.getCount() < MAX_SIMULTANEOUS_TRADES && !(openPositionService.getOpenPositionSymbol(symbolsDto.getSymbols()))) {
		Double price = BinanceTa4jUtils.getCurrentPrice(symbolsDto.getSymbols()).doubleValue();


		if (price < Double.valueOf(symbolsDto.getImbBuy())
				&& price > Double.valueOf(symbolsDto.getHighBuy())) {

			TrendDetector.TrendResult result = TrendDetector.detectTrendWithExtremes(timeSeriesCache.get(symbolsDto.getSymbols()), 150,5);
			int move = TrendDetector.detectTrendWithMA25(timeSeriesCache.get(symbolsDto.getSymbols()));
			if (move> 0
						&& result.typeD > 0) {
			String enterPrice = String.valueOf(roundToDecimalPlaces(0.5*(Double.valueOf(symbolsDto.getImbBuy())+Double.valueOf(symbolsDto.getLowBuy())),countDecimalPlaces(price)));
			System.out.println( "[LONG] " +symbolsDto.getSymbols() + " " + price );
			if (Double.valueOf(enterPrice)>price) {
					VariantDto variantDto = VariantDto.builder().time(Timestamp.valueOf(java.time.LocalDateTime.now())).symbol(symbolsDto.getSymbols())
						.type("LONG").price(price.toString()).stop(symbolsDto.getLowBuy()).proffit(symbolsDto.getLowSell()).enterPrice(enterPrice)
						.build();
			insertVariant(variantDto);
			Map<String, Long> id =  startPosition(variantDto);
			OpenPositionDto openPositionDto = OpenPositionDto.builder().symbol(symbolsDto.getSymbols()).idBinance(id.get("id")).stopId(id.get("stop")).profitId(id.get("profit")).type("LONG").time(Timestamp.valueOf(java.time.LocalDateTime.now())).build();
			insertOpenPosition(openPositionDto);
		}}}
		if (price > Double.valueOf(symbolsDto.getLowSell())
				&& price < Double.valueOf(symbolsDto.getHighSell())){

			TrendDetector.TrendResult result = TrendDetector.detectTrendWithExtremes(timeSeriesCache.get(symbolsDto.getSymbols()), 150,5);
			int move = TrendDetector.detectTrendWithMA25(timeSeriesCache.get(symbolsDto.getSymbols()));
			if (move < 0 && result.typeD > 0) {
			String enterPrice = String.valueOf(roundToDecimalPlaces(0.5*(Double.valueOf(symbolsDto.getLowSell())+Double.valueOf(symbolsDto.getLowSell())),countDecimalPlaces(price)));
			System.out.println( "[SHORT] "+symbolsDto.getSymbols() + " " + price);
			if (Double.valueOf(enterPrice)< price){
					VariantDto variantDto = VariantDto.builder().time(Timestamp.valueOf(java.time.LocalDateTime.now())).symbol(symbolsDto.getSymbols())
						.type("SHORT").price(price.toString()).stop(symbolsDto.getHighSell()).proffit(symbolsDto.getHighBuy()).enterPrice(enterPrice)
						.build();
					insertVariant(variantDto);
  					Map<String, Long> id =  startPosition(variantDto);
					OpenPositionDto openPositionDto = OpenPositionDto.builder().symbol(symbolsDto.getSymbols()).idBinance(id.get("id")).stopId(id.get("stop")).profitId(id.get("profit")).type("SHORT").time(Timestamp.valueOf(java.time.LocalDateTime.now())).build();
					insertOpenPosition(openPositionDto);
		}}}}}
	}

	public Map<String,Long> startPosition(VariantDto variantDto) throws InterruptedException {
		String quality = BinanceUtil.getAmount(Double.valueOf(variantDto.getPrice()),TRADE_SIZE_USDT);
		org.binance.springbot.task.Position position = new org.binance.springbot.task.Position(variantDto.getSymbol(),variantDto.getType(),quality ,variantDto.getEnterPrice());
		Map<String,Long> mapa = new HashMap<String,Long>();
		if (variantDto.getType() == "SHORT") {
			Long idPosition = position.openPositionShort();
			while (!position.getStatus(idPosition, variantDto.getSymbol())) {

				sleep(1000);
			}
			org.binance.springbot.task.Position positionSP = new org.binance.springbot.task.Position(idPosition,variantDto.getSymbol());
			Long[] idSP = positionSP.stopPositionShort(variantDto.getStop(),variantDto.getProffit());
			mapa.put("id",idPosition);
			mapa.put("stop",idSP[0]);
			mapa.put("profit",idSP[1]);
		}
		if (variantDto.getType() == "LONG") {
			Long idPosition = position.openPositionLong();
			while (!position.getStatus(idPosition, variantDto.getSymbol())) {

			   sleep(1000);
			}
			org.binance.springbot.task.Position positionSP = new org.binance.springbot.task.Position(idPosition,variantDto.getSymbol());
			Long[] idSP = positionSP.stopPositionLong(variantDto.getStop(),variantDto.getProffit());
			mapa.put("id",idPosition);
			mapa.put("stop",idSP[0]);
			mapa.put("profit",idSP[1]);
		}
		return mapa;
	}
	public void checkClosePosition(){
		List<OpenPosition> openPositionDtoList = openPositionService.getAll();
		if (openPositionDtoList.size() > 0) {
			RequestOptions options = new RequestOptions();
			SyncRequestClient syncRequestClient = SyncRequestClient.create(getApiKey(), getApiSecret(),
					options);
			for (OpenPosition entity: openPositionDtoList){
						List<MyTrade> trades = syncRequestClient.getAccountTrades(entity.getSymbol(), null, null, null, 100);
				if ((trades.get(trades.size() - 1).getRealizedPnl().doubleValue())!= 0 ){
					StatisticDto statisticDto = StatisticDto.builder().pnl(trades.get(trades.size() - 1).getRealizedPnl().toString()).symbols(trades.get(trades.size()-1).getSymbol())
							.comission(trades.get(trades.size()-1).getCommission().add(trades.get(trades.size()-2).getCommission()).toString())
							.type(Type.valueOf(trades.get(trades.size()-1).getPositionSide()))
							.startDateTime(convertTimestampToDate(trades.get(trades.size()-1).getTime().longValue()))
							.duration(convertTimestampToDate(trades.get(trades.size()-1).getTime().longValue()-trades.get(trades.size()-2).getTime().longValue()))
							.build();
					statisticService.insertStatistic(statisticDto);
					openPositionService.deleteById(entity.getId());
					try {
						syncRequestClient.cancelOrder(trades.get(trades.size()-1).getSymbol(),entity.getProfitId(),null); }
					catch (Exception e) {
						System.out.println();
					}
					try {
					syncRequestClient.cancelOrder(trades.get(trades.size()-1).getSymbol(),entity.getStopId(),null);}
					catch (Exception e) {
						System.out.println();
					}
				}
		}
	}}

	public void updateAll() throws Exception {
		for (Map.Entry<String, BarSeries> entry : timeSeriesCache.entrySet()) {
			updateSQLSymbol(entry.getKey());
	}}

	public void updateSQLSymbol(String symbol) throws Exception {
		BarSeries series = timeSeriesCache.get(symbol);
		Map<String, Integer> orderBlocks = OrderBlockFinder.findOrderBlocks(series , series.getEndIndex());
		System.out.println("Update "+symbol);

		String imbBuy = series.getBar(orderBlocks.get("BuyOrderBlock")+2).getLowPrice().toString();
		String imbSell =series.getBar(orderBlocks.get("SellOrderBlock")+2).getLowPrice().toString();

		SymbolsDto symbolDto = SymbolsDto.builder().symbols(symbol).highBuy(series.getBar(orderBlocks.get("BuyOrderBlock")).getHighPrice().toString()).
				lowBuy(series.getBar(orderBlocks.get("BuyOrderBlock")).getLowPrice().toString()).
				imbBuy(imbBuy).
				highSell(series.getBar(orderBlocks.get("SellOrderBlock")).getHighPrice().toString()).
				lowSell(series.getBar(orderBlocks.get("SellOrderBlock")).getLowPrice().toString()).
				imbSell(imbSell).
				build();
		deleteSymbols(symbol);
		insertSymbols(symbolDto);
		timeSeriesCache.put(symbol, series);
	}

}
