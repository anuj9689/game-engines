package com.algomics.torpedo.gaming.engine.divineegypt.jackson;

import com.algomics.torpedo.gaming.engine.divineegypt.bonus.NeonBonusContext;
import com.algomics.torpedo.gaming.engine.divineegypt.bonus.NeonGameBonus;
import com.algomics.torpedo.gaming.engine.divineegypt.model.dto.NeonGameEngineRequest;
import com.algomics.torpedo.gaming.engine.divineegypt.symbol.NeonSymbol;
import com.algomics.torpedo.gaming.engine.api.bonus.BonusContext;
import com.algomics.torpedo.gaming.engine.api.bonus.GameBonus;
import com.algomics.torpedo.gaming.engine.api.model.GameActivity;
import com.algomics.torpedo.gaming.engine.api.model.GameEngineRequest;
import com.algomics.torpedo.gaming.engine.api.model.GameEngineResponse;
import com.algomics.torpedo.gaming.engine.api.model.GamePlayState;
import com.algomics.torpedo.gaming.engine.api.module.GameEngineModule;
import com.algomics.torpedo.gaming.engine.api.symbol.Symbol;
import com.algomics.torpedo.gaming.engine.slots.api.dto.SlotsGameEngineResponse;
import com.algomics.torpedo.gaming.engine.slots.api.model.SlotsGamePlayState;
import com.algomics.torpedo.gaming.engine.slots.model.SlotsSpinGameActivity;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.KeyDeserializer;

import java.io.IOException;

public class NeonModule extends GameEngineModule {


    public NeonModule() {
        super(NeonModule.class.getSimpleName());
        this.addAbstractTypeMapping(GameEngineRequest.class, NeonGameEngineRequest.class);
        this.addAbstractTypeMapping(GameEngineResponse.class, SlotsGameEngineResponse.class);
        this.addAbstractTypeMapping(GamePlayState.class, SlotsGamePlayState.class);
        this.addAbstractTypeMapping(GameActivity.class, SlotsSpinGameActivity.class);
        this.addAbstractTypeMapping(Symbol.class, NeonSymbol.class);
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
