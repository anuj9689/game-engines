package com.algomics.torpedo.gaming.engine.divineegypt.model.utils;

import com.algomics.torpedo.gaming.engine.divineegypt.symbol.NeonSymbol;
import com.algomics.torpedo.gaming.engine.api.exception.GameEngineException;
import com.algomics.torpedo.gaming.engine.api.symbol.Symbol;
import org.springframework.util.LinkedMultiValueMap;
import java.util.ArrayList;
import java.util.List;

public class FeatureUtils {
    public static LinkedMultiValueMap<Integer, String> convertSymbolGridToStringGrid(LinkedMultiValueMap<Integer, Symbol> symbolGrid) {
        LinkedMultiValueMap<Integer, String> stringSymbolGrid = new LinkedMultiValueMap<>();
        for(int i= 0; i < symbolGrid.size(); i++) {
            List<String> symList = new ArrayList<>();
            for(int k=0; k < symbolGrid.get(i).size(); k++) {
                symList.add( symbolGrid.get(i).get(k).toString() );
            }
            stringSymbolGrid.put(i, symList);
        }
        return stringSymbolGrid;
    }

    public static LinkedMultiValueMap<Integer, String> createStackingSymbolDisplayGrid(LinkedMultiValueMap<Integer, Symbol> symbolGrid, List<String> stackSettings, int reels, int display) {
        LinkedMultiValueMap<Integer, String> featureGrid = convertSymbolGridToStringGrid(symbolGrid);
        for (String setting: stackSettings) {
            int sreel = setting.length(); if(sreel > reels) throw new GameEngineException("Invalid reel stacking - stacking request is higher than reelSize:"+reels);
            if(sreel == 1) {
                int col = Integer.parseInt(setting); if(col < 0 || col > reels) throw new GameEngineException("Invalid option for stacking - reel:"+col);
                if(col > 0) col -= 1;

                NeonSymbol symbol      = (NeonSymbol) symbolGrid.get(col).get(0);
                String symbolStr       = symbol.toString();
                for(int r = 0; r < display; r++) {
                    featureGrid.get(col).set(r, symbolStr+"_"+(r+1));
                    symbolGrid.get(col).set(r, symbol);
                }
            } else {
                int reelToStart = Integer.parseInt(setting.substring(0,1)) - 1;
                int labelStart  = Integer.parseInt(setting.substring(1, sreel));
                if(reelToStart < 0 || reelToStart >= reels || (reelToStart+sreel) > reels) throw new GameEngineException("Invalid reel stacking - starting reel is out of range:"+ reelToStart);

                NeonSymbol symbol      = (NeonSymbol) symbolGrid.get(reelToStart).get(0);
                String     symbolStr   = symbol.toString();
                int seq         = 0;
                for(int c = reelToStart; c < ((reelToStart+sreel)); c++ ) {
                    for(int r = 0; r < display; r++) {
                        featureGrid.get(c).set(r, symbolStr+"_"+(labelStart+seq));
                        symbolGrid.get(c).set(r, symbol);
                        seq++;
                    }
                }
            }
        }
        return featureGrid;
    }

    public static int getFeatureSymbolCount(LinkedMultiValueMap<Integer, Symbol> symbolGrid, NeonSymbol symbol) {
        int cnt = 0;
        for(int i= 0; i < symbolGrid.size(); i++) {
            for(int k=0; k < symbolGrid.get(i).size(); k++) {
                if(symbolGrid.get(i).get(k).equals(symbol)) cnt++;
            }
        }
        if(cnt==0)cnt=1;
        return cnt;
    }

    public static void expandReelsFeature(LinkedMultiValueMap<Integer, Symbol> symbolGrid, int[] expandConfig) {
        if(symbolGrid.size() != expandConfig.length) throw new GameEngineException("Invalid expand config size:"+ expandConfig.length);
        for(int i=0; i < symbolGrid.size(); i++) {
            if( expandConfig[i] == 1 )
                for(int k=0; k < symbolGrid.get(i).size(); k++) symbolGrid.get(i).set(k, NeonSymbol.WC);
        }
    }
}
