package com.algomics.torpedo.gaming.engine.divineegypt.config;

import com.algomics.torpedo.gaming.engine.divineegypt.bonus.NeonGameBonus;
import com.algomics.torpedo.gaming.engine.divineegypt.model.WheelOption;
import com.algomics.torpedo.gaming.engine.divineegypt.symbol.NeonSymbol;
import com.algomics.torpedo.gaming.engine.slots.config.SlotsBonusGameConfiguration;
import lombok.Data;

import java.util.List;

/**
 * The ROAGameConfiguration.
 */
@Data
public class NeonBonusGameConfiguration extends SlotsBonusGameConfiguration<NeonSymbol, NeonGameBonus> {

 List<WheelOption> jackpotWheelOptions;
}
