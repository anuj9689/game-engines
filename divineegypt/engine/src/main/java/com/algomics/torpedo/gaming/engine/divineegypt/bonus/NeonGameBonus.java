package com.algomics.torpedo.gaming.engine.divineegypt.bonus;

import com.algomics.torpedo.gaming.engine.api.bonus.GameBonus;
import com.algomics.torpedo.gaming.engine.slots.api.bonus.types.FreeSpinsBonus;
import com.algomics.torpedo.gaming.engine.slots.api.bonus.types.NoneBonus;

public enum NeonGameBonus implements GameBonus {

    NONE(NoneBonus.createInstance()),
    FREE_SPINS(FreeSpinsBonus.createInstance()),
    BUY_FREE_SPINS(BuyFreeSpinsBonus.createInstance()),
    WHEEL_BONUS(WheelBonus.createInstance()),
    BUY_FEATURE(BuyFeature.createInstance());
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
