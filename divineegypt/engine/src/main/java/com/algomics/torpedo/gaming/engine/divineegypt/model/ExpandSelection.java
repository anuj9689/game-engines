package com.algomics.torpedo.gaming.engine.divineegypt.model;

import com.algomics.torpedo.gaming.engine.slots.utils.Weighted;
import lombok.Data;
import java.util.List;

@Data
public class ExpandSelection implements Weighted {
    int             index;
    int             weight;
    int[]           value;

    @Override
    public Integer getWeight() {
        return this.weight;
    }

    @Override
    public void setWeight(Integer weight) {
        this.weight = weight;
    }
}
