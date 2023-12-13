package com.algomics.torpedo.gaming.engine.divineegypt.bonus;

import com.algomics.torpedo.gaming.engine.divineegypt.model.WheelOption;
import com.algomics.torpedo.gaming.engine.divineegypt.symbol.NeonSymbol;
import com.algomics.torpedo.gaming.engine.slots.model.SlotsBonusContext;
import lombok.Data;
import org.springframework.util.LinkedMultiValueMap;

import java.math.BigDecimal;
import java.util.List;

@Data
public class NeonBonusContext extends SlotsBonusContext {
    List<WheelOption>                                       wheelOptions;
    WheelOption                                             wheelStop;
    int                                                     scatterCount;
    LinkedMultiValueMap<NeonSymbol, List<List<Integer>>>    specialSymbolPositions;
    boolean                                                 buyFreeGame;
    BigDecimal                                              wheelWin;
    BigDecimal                                              WildCount;
    public NeonBonusContext() {
        super(NeonGameBonus.NONE);
    }
    public NeonBonusContext(NeonBonusContext bonusContext) {
        super(bonusContext);
    }
}
