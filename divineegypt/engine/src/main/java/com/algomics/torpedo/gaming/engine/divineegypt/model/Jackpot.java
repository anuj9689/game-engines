package com.algomics.torpedo.gaming.engine.divineegypt.model;

import com.algomics.torpedo.gaming.engine.slots.utils.Weighted;
import lombok.Data;

import java.math.BigDecimal;
@Data
public class Jackpot implements Weighted {
    int         id;
    String      label;
    String      type;
    BigDecimal  value;

    @Override
    public Integer getWeight() {
        return null;
    }

    @Override
    public void setWeight(Integer integer) {

    }
}
