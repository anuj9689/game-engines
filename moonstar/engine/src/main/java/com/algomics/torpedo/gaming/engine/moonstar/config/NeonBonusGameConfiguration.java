package com.algomics.torpedo.gaming.engine.moonstar.config;

import com.algomics.torpedo.gaming.engine.moonstar.bonus.NeonGameBonus;
import com.algomics.torpedo.gaming.engine.moonstar.model.WildBursterMeter;
import com.algomics.torpedo.gaming.engine.moonstar.symbol.NeonSymbol;
import com.algomics.torpedo.gaming.engine.slots.config.SlotsBonusGameConfiguration;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * The ROAGameConfiguration.
 */
@Data
public class NeonBonusGameConfiguration extends SlotsBonusGameConfiguration<NeonSymbol, NeonGameBonus> {

 WildBursterMeter wildBursterMeter;
 
}
