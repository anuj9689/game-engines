package com.algomics.torpedo.gaming.engine.moonstar.model;

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
