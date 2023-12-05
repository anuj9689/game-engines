package com.algomics.torpedo.gaming.engine.moonstar.bonus;


import com.algomics.torpedo.gaming.engine.api.bonus.GameBonus;

public class WildBursterBonus implements GameBonus {

    String burster;

    public WildBursterBonus() {
    }

    public WildBursterBonus(String burster) {
        this.burster = burster;
    }

    public static GameBonus createInstance() {
        return new WildBursterBonus();
    }

    public static GameBonus createInstance(String code) {
        return new WildBursterBonus(code);
    }

    public boolean continueGameRound() {
        return false;
    }

    public String getName() {
        return this.burster!=null?this.burster:"WILD_BURSTER";
    }
}