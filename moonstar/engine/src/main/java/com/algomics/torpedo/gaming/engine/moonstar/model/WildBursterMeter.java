package com.algomics.torpedo.gaming.engine.moonstar.model;

import lombok.Data;

import java.util.List;

@Data
public class WildBursterMeter {

    double wager;
    double winBG;
    double winFG;

    int meterThreshold;


    List<BursterOption> bonusOptions;
}
