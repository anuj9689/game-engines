package com.algomics.torpedo.gaming.engine.divineegypt.model.dto;

import com.algomics.torpedo.gaming.engine.slots.api.dto.SlotsGameEngineRequest;
import lombok.Data;

@Data
public class NeonGameEngineRequest extends SlotsGameEngineRequest {
    int     id;
    boolean buyFeature;
}
