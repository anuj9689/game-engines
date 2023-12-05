package com.algomics.torpedo.gaming.engine.moonstar.config;

import com.algomics.torpedo.gaming.engine.moonstar.bonus.NeonGameBonus;
import com.algomics.torpedo.gaming.engine.moonstar.model.RtpInfo;
import com.algomics.torpedo.gaming.engine.moonstar.symbol.NeonSymbol;
import com.algomics.torpedo.gaming.engine.slots.config.SlotsBaseConfiguration;
import com.algomics.torpedo.gaming.engine.slots.config.SlotsReelConfiguration;
import lombok.Data;
import org.springframework.util.LinkedMultiValueMap;

import java.math.BigDecimal;

/**
 * The ROAGameConfiguration.
 */
@Data
public class NeonGameConfiguration extends SlotsBaseConfiguration {

    SlotsReelConfiguration<NeonSymbol, NeonGameBonus> reelLayoutConfiguration;

    private LinkedMultiValueMap<NeonSymbol, BigDecimal> symbolStakeMultipliers;

    
    private NeonBonusGameConfiguration bonusGameConfiguration;


    String [] buyBurstersWithCredits;

    RtpInfo rtpInfo;



}
