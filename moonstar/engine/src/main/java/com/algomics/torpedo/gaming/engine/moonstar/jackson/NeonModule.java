package com.algomics.torpedo.gaming.engine.moonstar.jackson;

import com.algomics.torpedo.gaming.engine.api.bonus.BonusContext;
import com.algomics.torpedo.gaming.engine.api.bonus.GameBonus;
import com.algomics.torpedo.gaming.engine.api.model.GameActivity;
import com.algomics.torpedo.gaming.engine.api.model.GameEngineRequest;
import com.algomics.torpedo.gaming.engine.api.model.GameEngineResponse;
import com.algomics.torpedo.gaming.engine.api.model.GamePlayState;
import com.algomics.torpedo.gaming.engine.api.module.GameEngineModule;
import com.algomics.torpedo.gaming.engine.api.symbol.Symbol;
import com.algomics.torpedo.gaming.engine.moonstar.model.dto.NeonGameEngineRequest;
import com.algomics.torpedo.gaming.engine.moonstar.symbol.NeonSymbol;
import com.algomics.torpedo.gaming.engine.slots.api.dto.SlotsGameEngineResponse;
import com.algomics.torpedo.gaming.engine.slots.api.model.SlotsGamePlayState;
import com.algomics.torpedo.gaming.engine.slots.model.SlotsSpinGameActivity;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.KeyDeserializer;
import com.algomics.torpedo.gaming.engine.moonstar.bonus.NeonBonusContext;
import com.algomics.torpedo.gaming.engine.moonstar.bonus.NeonGameBonus;

import java.io.IOException;
import java.util.LinkedHashMap;

public class NeonModule extends GameEngineModule {


    public NeonModule() {
        super(NeonModule.class.getSimpleName());
        this.addAbstractTypeMapping(GameEngineRequest.class, NeonGameEngineRequest.class);
        this.addAbstractTypeMapping(GameEngineResponse.class, SlotsGameEngineResponse.class);
        this.addAbstractTypeMapping(GamePlayState.class, SlotsGamePlayState.class);
        this.addAbstractTypeMapping(GameActivity.class, SlotsSpinGameActivity.class);
        this.addAbstractTypeMapping(Symbol.class, NeonSymbol.class);
        //this.addAbstractTypeMapping(SlotReel.class, SlotDynamicReel.class);
        this.addAbstractTypeMapping(BonusContext.class, NeonBonusContext.class);
        this.addAbstractTypeMapping(GameBonus.class, NeonGameBonus.class);

        this.addKeyDeserializer(GameBonus.class, new KeyDeserializer() {
            @Override
            public Object deserializeKey(String key, DeserializationContext ctxt) throws IOException {
                return Enum.valueOf(NeonGameBonus.class, key);
            }
        });
    }
}
