package com.algomics.torpedo.gaming.engine.moonstar.model.dto;

import com.algomics.torpedo.gaming.engine.slots.api.dto.SlotsGameEngineRequest;
import lombok.Data;

import java.util.List;

@Data
public class NeonGameEngineRequest extends SlotsGameEngineRequest {

    int     id;
    boolean buyFeature;
    private List<Integer> forceData;

}
