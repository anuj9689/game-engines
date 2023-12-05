package com.algomics.torpedo.gaming.engine.moonstar.bonus;

import com.algomics.torpedo.gaming.engine.api.bonus.GameBonus;
import com.algomics.torpedo.gaming.engine.slots.api.bonus.types.FreeSpinsBonus;
import com.algomics.torpedo.gaming.engine.slots.api.bonus.types.NoneBonus;

public enum NeonGameBonus implements GameBonus   {

    NONE(NoneBonus.createInstance()),
    WILD_BURSTER(WildBursterBonus.createInstance()),
    FREE_SPINS(FreeSpinsBonus.createInstance()),
    BURSTER_1_PURCHASED(WildBursterBonus.createInstance("BURSTER_1_PURCHASED")),
    BURSTER_2_PURCHASED(WildBursterBonus.createInstance("BURSTER_2_PURCHASED")),
    BURSTER_3_PURCHASED(WildBursterBonus.createInstance("BURSTER_3_PURCHASED")),
    BURSTER_4_PURCHASED(WildBursterBonus.createInstance("BURSTER_4_PURCHASED")),
    BURSTER_5_PURCHASED(WildBursterBonus.createInstance("BURSTER_5_PURCHASED")),
    BURSTER_6_PURCHASED(WildBursterBonus.createInstance("BURSTER_6_PURCHASED")),
    BURSTER_7_PURCHASED(WildBursterBonus.createInstance("BURSTER_7_PURCHASED"));

    GameBonus bonus;

    NeonGameBonus(GameBonus bonus) {
        this.bonus = bonus;
    }

    @Override
    public boolean continueGameRound() {
        return this.bonus.continueGameRound();
    }

    @Override
    public String getName() {
        return this.bonus.getName();
    }
}
