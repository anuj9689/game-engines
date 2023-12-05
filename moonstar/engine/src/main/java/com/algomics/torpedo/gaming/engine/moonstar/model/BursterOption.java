package com.algomics.torpedo.gaming.engine.moonstar.model;

import com.algomics.torpedo.gaming.engine.slots.utils.Weighted;
import lombok.Data;

@Data
public class BursterOption implements Weighted {

    int index;

    int weight;

    int[] positions;

    @Override
    public Integer getWeight() {
        return this.weight;
    }

    @Override
    public void setWeight(Integer weight) {
        this.weight = weight;
    }
}
