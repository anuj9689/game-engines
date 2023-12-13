package com.algomics.torpedo.gaming.engine.neon.emulator;

import com.algomics.torpedo.gaming.engine.api.model.GamePlayReport;
import lombok.Data;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

@Data
public class NeonEmulatorReport implements GamePlayReport {
    long reSpinHits;
    Map<String, Long> bonusHits = new HashMap<>();
    Map<String, BigDecimal> bonusWins = new TreeMap<>();
    Map<String, BigDecimal> rtpTable = new TreeMap<>();

    @Override
    public NeonEmulatorReport accumulate(BigDecimal stake, GamePlayReport report, boolean group) {
        NeonEmulatorReport that = (NeonEmulatorReport) report;
        this.reSpinHits = reSpinHits + that.getReSpinHits();


        ((NeonEmulatorReport) report).getBonusHits().forEach((key, value) -> {
            this.bonusHits.merge(key, value, Long::sum);
        });

        ((NeonEmulatorReport) report).getRtpTable().forEach((key, value) -> {
            this.rtpTable.merge(key, value, BigDecimal::add);
        });

        ((NeonEmulatorReport) report).getBonusWins().forEach((key, value) -> {
            this.bonusWins.merge(key, value, BigDecimal::add);
        });

        if (group) {

            this.bonusWins.forEach((key, wins) -> {
                this.rtpTable.put(key + "_RTP", wins
                        .divide(stake, MathContext.DECIMAL128)
                        .multiply(BigDecimal.valueOf(100)).setScale(4, RoundingMode.HALF_UP));
            });
        }
        return this;
    }


    @Override
    public String toString() {
        return "NeonEmulatorReport{,\n" +
               "reSpinHits=" + reSpinHits + ",\n" +
               "bonusHits=" + bonusHits + ",\n" +
               "bonusWins=" + bonusWins.entrySet().stream().map(e->e.toString()+"\n").collect(Collectors.toList()) + ",\n" +
               "rtpTable=" + rtpTable.entrySet().stream().map(e->e.toString()+"\n").collect(Collectors.toList()) + ",\n" +
               '}';
    }
}
