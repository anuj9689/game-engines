package com.algomics.torpedo.gaming.engine.divineegypt.bonus;

import com.algomics.torpedo.gaming.engine.api.bonus.GameBonus;

public class BuyFeature implements GameBonus {

    public BuyFeature() {
    }

    public static GameBonus createInstance() {
        return new WheelBonus();
    }

    @Override
    public boolean continueGameRound() {
        return false;
    }

    @Override
    public String getName() {
        return "BUY_FEATURE";
    }
}
