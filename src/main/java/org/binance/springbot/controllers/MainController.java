package org.binance.springbot.controllers;

import org.binance.springbot.entity.*;
import org.binance.springbot.repo.*;
import org.binance.springbot.service.SymbolService;
import org.binance.springbot.task.BotInfo;
import org.binance.springbot.task.PositionStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.ArrayList;
import java.util.List;

import static java.lang.Double.sum;


@Controller
public class MainController {

    private final SymbolService symbolService;

    public MainController(SymbolService symbolService) {
        this.symbolService = symbolService;
    }

    @Autowired
    private OpenPositionRepository openPositionRepository;
    @GetMapping ("/")
    public String home(Model model){
        Iterable<OpenPosition> symbols = openPositionRepository.findAll();
        BotInfo botInfo = new BotInfo();
        List<PositionStatus> positionStatuses = new ArrayList<>();
        for ( OpenPosition symbol: symbols) {
            PositionStatus positionStatus = new PositionStatus(symbol.getSymbol(),symbol.getIdBinance(),symbol.getStopId().toString(),symbol.getProfitId().toString());
            positionStatuses.add(positionStatus);
        }
        model.addAttribute("symbols",symbols);
        model.addAttribute("botInfo",botInfo);
        model.addAttribute("positionStatus",positionStatuses);
        return "home";
    }
    @Autowired
    private StatisticRepository statisticRepository;
    @GetMapping ("/statistic")
    public String statistic(Model model){
        Iterable<Statistic> symbols = statisticRepository.findAll();
        double[] resultD = {0.0,0.0,0.0};
        for (Statistic symbol : symbols) {
            Double pnl = symbol.getPnl() != null ? Double.valueOf(symbol.getPnl()) : 0.0;
            Double commission = symbol.getComission() != null ? Double.valueOf(symbol.getComission()) : 0.0;

            resultD[0] += pnl;          // Добавляем PNL
            resultD[1] += commission;   // Добавляем комиссию
        }
        resultD[2] = resultD[0]-resultD[1];
        model.addAttribute("symbols",symbols);
        model.addAttribute("resultD",resultD);
        return "statistic";
    }
    @Autowired
    private VariantRepository variantRepository;
    @GetMapping ("/variant")
    public String variant(Model model) {
        Iterable<Variant> symbols = variantRepository.findAll();
        List<String> calculatedResults = new ArrayList<>();
        for (Variant symbol : symbols) {
            if (symbol.getType() == "LONG") {
                double k = (Double.valueOf(symbol.getProffit()) - Double.valueOf(symbol.getPrice())) / (Double.valueOf(symbol.getPrice()) - Double.valueOf(symbol.getStop()));
                calculatedResults.add(String.format("%.3f", k));
            }else {
                double k = (Double.valueOf(symbol.getPrice()) - Double.valueOf(symbol.getProffit())) / (Double.valueOf(symbol.getStop()) - Double.valueOf(symbol.getPrice()));
            calculatedResults.add(String.format("%.3f", k));}

        }
        model.addAttribute("symbols",symbols);
        model.addAttribute("calculatedResults", calculatedResults);
        return "variant";
    }
    @Autowired
    private LogUpdateRepository logUpdateRepository;
    @GetMapping ("/logview")
    public String logview(Model model){
        Iterable<LogUpdate> symbols = logUpdateRepository.findAll();
        model.addAttribute("symbols",symbols);
        return "logview";
    }
    @Autowired
    private SymbolsRepository symbolsRepository;
    @GetMapping ("/allsymbols")
    public String allsymbols(Model model) {
        Iterable<Symbols> symbols = symbolsRepository.findAll();
        model.addAttribute("symbols", symbols);
        return "allsymbols";
    }

}
