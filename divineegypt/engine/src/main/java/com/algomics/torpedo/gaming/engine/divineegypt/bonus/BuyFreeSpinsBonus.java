package com.algomics.torpedo.gaming.engine.divineegypt.bonus;

import com.algomics.torpedo.gaming.engine.api.bonus.GameBonus;
import com.algomics.torpedo.gaming.engine.slots.api.bonus.types.FreeSpinsBonus;

public class BuyFreeSpinsBonus implements GameBonus {
    public BuyFreeSpinsBonus() {
    }

    public static GameBonus createInstance() {
        return new BuyFreeSpinsBonus();
    }

    public boolean continueGameRound() {
        return true;
    }

    public String getName() {
        return "BUY_FREE_SPINS";
    }
}

