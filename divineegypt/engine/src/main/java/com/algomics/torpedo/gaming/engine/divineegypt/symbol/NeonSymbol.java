package com.algomics.torpedo.gaming.engine.divineegypt.symbol;

import com.algomics.torpedo.gaming.engine.api.symbol.Symbol;
import com.algomics.torpedo.gaming.engine.api.symbol.SymbolType;
import com.algomics.torpedo.gaming.engine.slots.utils.Weighted;

import java.util.Arrays;
import java.util.List;

/**
 * The NeonSymbol.
 */
public enum NeonSymbol implements Symbol, Weighted {
    WC("WC"     ,    SymbolType.WILD,    SymbolType.SPECIAL ),
     BG("BG"     ,    SymbolType.SPECIAL, SymbolType.SPECIAL ),
    SC("SC"     ,    SymbolType.SCATTER, SymbolType.SPECIAL ),
    AA("AA"     ,    SymbolType.NORMAL                      ),
    BB("BB"     ,    SymbolType.NORMAL                      ),
    CC("CC"     ,    SymbolType.NORMAL                      ),
    DD("DD"     ,    SymbolType.NORMAL                      ),
    EE("EE"     ,    SymbolType.NORMAL                      ),
    FF("FF"       ,    SymbolType.NORMAL                    ),
    GG("GG"       ,    SymbolType.NORMAL                    ),
    HH("HH"       ,    SymbolType.NORMAL                    ),
    JJ("JJ"       ,    SymbolType.NORMAL                    ),
    KK("KK"       ,    SymbolType.NORMAL                    ),
    LL("LL"       ,    SymbolType.NORMAL                    );

    private final String code;

    private List<SymbolType> symbolType;

    private int weight;


    NeonSymbol(String code, SymbolType... type) {
        this.code = code;
        this.symbolType = Arrays.asList(type);
    }

    NeonSymbol(String code, int weight, SymbolType... type) {
        this.code = code;
        this.symbolType = Arrays.asList(type);
        this.weight = weight;
    }

    NeonSymbol(String code, int weight) {
        this.code = code;
        this.weight = weight;
    }

    @Override
    public List<SymbolType> getSymbolType() {
        return symbolType;
    }

    @Override
    public String getCode() {
        return code;
    }

    @Override
    public Integer getWeight() {
        return weight;
    }

    @Override
    public void setWeight(Integer weight) {
        this.weight = weight;
    }
}
