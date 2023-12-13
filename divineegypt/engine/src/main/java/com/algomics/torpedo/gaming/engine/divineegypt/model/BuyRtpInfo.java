package com.algomics.torpedo.gaming.engine.divineegypt.model;

import lombok.Data;

import java.math.BigDecimal;

/**
 * The ROAGameConfiguration.
 */
@Data
public class BuyRtpInfo {
    String      label;
    String      rtp;
    int         id;
    BigDecimal  bet;
}
