package org.binance.springbot.controllers;

import org.binance.springbot.dto.SymbolsDto;
import org.binance.springbot.entity.Statistic;
import org.binance.springbot.mapper.Mappers;
import org.binance.springbot.repo.StatisticRepository;
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

    @GetMapping ("/")
    public String home(Model model){
        model.addAttribute("title","Главная страница");
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

    @GetMapping ("/console")
    public String console(Model model){
        model.addAttribute("title","Главная страница");
        return "console";
    }



}
