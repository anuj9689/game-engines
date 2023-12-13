package com.algomics.torpedo.gaming.engine.divineegypt.config;

import com.algomics.torpedo.gaming.engine.divineegypt.bonus.NeonGameBonus;
import com.algomics.torpedo.gaming.engine.divineegypt.model.Jackpot;
import com.algomics.torpedo.gaming.engine.divineegypt.model.RtpInfo;
import com.algomics.torpedo.gaming.engine.divineegypt.model.ExpandSelection;
import com.algomics.torpedo.gaming.engine.divineegypt.symbol.NeonSymbol;
import com.algomics.torpedo.gaming.engine.slots.config.SlotsBaseConfiguration;
import com.algomics.torpedo.gaming.engine.slots.config.SlotsReelConfiguration;
import lombok.Data;
import org.springframework.util.LinkedMultiValueMap;

import java.math.BigDecimal;
import java.util.List;

/**
 * The ROAGameConfiguration.
 */
@Data
public class NeonGameConfiguration extends SlotsBaseConfiguration {

    SlotsReelConfiguration<NeonSymbol, NeonGameBonus> reelLayoutConfiguration;

    
    private LinkedMultiValueMap<NeonSymbol, BigDecimal> symbolStakeMultipliers;

    
    private NeonBonusGameConfiguration bonusGameConfiguration;

    RtpInfo rtpInfo;

    BigDecimal buyFeatureBetMultiplier;

    List<Jackpot> jackpotInfo;
}
