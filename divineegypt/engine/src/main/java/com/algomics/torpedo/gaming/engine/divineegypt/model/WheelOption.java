package com.algomics.torpedo.gaming.engine.divineegypt.model;

import com.algomics.torpedo.gaming.engine.slots.utils.Weighted;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class WheelOption implements Weighted {

    int         index;
    int         weight;
    String      type;
    BigDecimal  value;

    @Override
    public Integer getWeight() {
        return this.weight;
    }

    @Override
    public void setWeight(Integer weight) {
        this.weight = weight;
    }
}
