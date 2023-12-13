package com.algomics.torpedo.gaming.engine.divineegypt.bonus;

import com.algomics.torpedo.gaming.engine.api.bonus.GameBonus;
import com.algomics.torpedo.gaming.engine.slots.api.bonus.types.FreeSpinsBonus;

public class WheelBonus implements GameBonus {

    public WheelBonus() {
    }

    public static GameBonus createInstance() {
        return new WheelBonus();
    }

    @Override
    public boolean continueGameRound() {
        return true;
    }

    @Override
    public String getName() {
        return "WHEEL_BONUS";
    }
}
