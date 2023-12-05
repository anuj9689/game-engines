package com.algomics.torpedo.gaming.engine.moonstar.bonus;

import com.algomics.torpedo.gaming.engine.slots.model.SlotsBonusContext;
import lombok.Data;

@Data
public class NeonBonusContext extends SlotsBonusContext {


    int[] bursterPositions;

    int bursterOption;

    double previousMeter;

    double meter;

    int meterPercentage;

    public NeonBonusContext() {
        super(NeonGameBonus.NONE);
    }

    public NeonBonusContext(NeonBonusContext bonusContext) {
        super(bonusContext);
    }
}
