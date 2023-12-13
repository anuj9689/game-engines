package com.algomics.torpedo.gaming.engine.divineegypt.model;

import lombok.Data;

import java.util.List;

/**
 * The ROAGameConfiguration.
 */
@Data
public class RtpInfo {
    String mainGame;
    List<BuyRtpInfo> buyFeature;
}
