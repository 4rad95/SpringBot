package org.binance.springbot.controllers;

import org.binance.springbot.entity.LogUpdate;
import org.binance.springbot.entity.OpenPosition;
import org.binance.springbot.entity.Statistic;
import org.binance.springbot.entity.Symbols;
import org.binance.springbot.repo.LogUpdateRepository;
import org.binance.springbot.repo.OpenPositionRepository;
import org.binance.springbot.repo.StatisticRepository;
import org.binance.springbot.repo.SymbolsRepository;
import org.binance.springbot.service.SymbolService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;


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
        model.addAttribute("symbols",symbols);
        return "home";
    }
    @Autowired
    private StatisticRepository statisticRepository;
    @GetMapping ("/statistic")
    public String statistic(Model model){
        Iterable<Statistic> symbols = statisticRepository.findAll();
        model.addAttribute("symbols",symbols);
        return "statistic";
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
