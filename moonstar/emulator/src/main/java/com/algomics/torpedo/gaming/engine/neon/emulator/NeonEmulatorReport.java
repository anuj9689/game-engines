package com.algomics.torpedo.gaming.engine.neon.emulator;

import com.algomics.torpedo.gaming.engine.api.model.GamePlayReport;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

import static com.algomics.torpedo.gaming.engine.neon.emulator.NeonEmulatorRunner.winData;

@Data
public class NeonEmulatorReport implements GamePlayReport {
    long reSpinHits;
    Map<String, Long> bonusHits = new HashMap<>();
    Map<String, BigDecimal> bonusWins = new TreeMap<>();
    Map<String, BigDecimal> rtpTable = new TreeMap<>();

    Map<String, BigDecimal> StandardDeviation= new HashMap<>();
    Map<Object, Object> Static_Data= new HashMap<>();
    HashMap<BigDecimal,BigDecimal> win_value1 = new HashMap<BigDecimal,BigDecimal>();

    Map<String, BigDecimal> Variance = new HashMap<>();

    public static final String STANDARD_DEVIATION = "STANDARD_DEVIATION";

    public static final String VARIANCE = "VARIANCE";
//
//    @Override
    public NeonEmulatorReport accumulate(BigDecimal stake, GamePlayReport report, boolean group) {
        NeonEmulatorReport that = (NeonEmulatorReport) report;
        this.reSpinHits = reSpinHits + that.getReSpinHits();


        ((NeonEmulatorReport) report).getBonusHits().forEach((key, value) -> {
            this.bonusHits.merge(key, value, Long::sum);
        });

        ((NeonEmulatorReport) report).getRtpTable().forEach((key, value) -> {
            this.rtpTable.merge(key, value, BigDecimal::add);
        });

        ((NeonEmulatorReport) report).getStandardDeviation().forEach((key, value) -> {
            this.StandardDeviation.merge(key, value, BigDecimal::add);
        });

        ((NeonEmulatorReport) report).getVariance().forEach((key, value) -> {
            this.Variance.merge(key, value, BigDecimal::add);
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

    @Autowired
    public void staticData(){
        BigDecimal totalRTP = rtpTable.get("Total_RTP");
        BigDecimal bet= NeonEmulatorRunner.getBet();
        BigDecimal iterations = NeonEmulatorRunner.getIterations();
        Static_Data.put("Total_iteration", iterations);

        NeonEmulatorReport report = new NeonEmulatorReport();
        System.out.println("totalRTP:- "+ totalRTP + " iteration:- "+ iterations+" bet:- "+ bet);
        BigDecimal variance = BigDecimal.ZERO;
        ArrayList<String> abc1 = new ArrayList<>();
        abc1.addAll(winData.entrySet().stream().map(e->e.toString()).collect(Collectors.toList()));

        for(int i=0; i<abc1.size(); i++){
            String ab1[] = abc1.get(i).split("=");
            win_value1.put(new BigDecimal(ab1[0]),(new BigDecimal(ab1[1])));
        }
        for (BigDecimal key : win_value1.keySet()) {
            BigDecimal a = key.divide(bet,8, RoundingMode.HALF_UP);
            BigDecimal b = win_value1.get(key).divide(iterations,8, RoundingMode.HALF_UP);
            BigDecimal c = a.subtract(totalRTP.divide(BigDecimal.valueOf(100)));
            BigDecimal d = c.multiply(c);
            BigDecimal e = d.multiply(b);
            variance = variance.add(e);
        }
        BigDecimal standard_deviation = sqrt(variance, 8);
        Static_Data.put(STANDARD_DEVIATION, standard_deviation);
        Static_Data.put(VARIANCE, variance);
        System.out.println("STANDARD_DEVIATION: "+ standard_deviation);
    }

@Autowired
    public void Data() {
        BigDecimal bet = NeonEmulatorRunner.getBet();
        BigDecimal rtp = new BigDecimal(96.01);
        BigDecimal sd = new BigDecimal(10.131);
        BigDecimal for_95 = new BigDecimal(1.959963985);
        BigDecimal for_99 = new BigDecimal(2.575829304);
        BigDecimal iterations = NeonEmulatorRunner.getIterations();;

        BigDecimal RTP_Cal = BigDecimal.ZERO;
        for (Map.Entry<BigDecimal,BigDecimal> entry : win_value1.entrySet()) {
            BigDecimal a = entry.getKey().divide(bet,8, RoundingMode.HALF_UP);
            BigDecimal b = entry.getValue().divide(iterations,8, RoundingMode.HALF_UP);
            BigDecimal c = a.multiply(b);
            RTP_Cal = RTP_Cal.add(c);
        }

        Static_Data.put("RTP_CALCULATED", RTP_Cal);

        BigDecimal RTP_Percentage = RTP_Cal.multiply(new BigDecimal(100));
//        BigDecimal RTP_Percentage = RTP_Cal;
        BigDecimal first = sd.multiply(for_95);
        BigDecimal second = first.divide(sqrt(iterations,8),8, RoundingMode.HALF_UP);
        BigDecimal third = second.multiply(new BigDecimal(100));
        BigDecimal lo_95 =rtp.subtract(third);
        BigDecimal hi_95 =rtp.add(third);
        String Result = is_in_range(lo_95,RTP_Percentage,hi_95);
    Static_Data.put("Result", Result);
    Static_Data.put("LowerBound", lo_95.divide(new BigDecimal(1), 4, RoundingMode.CEILING));
    Static_Data.put("UpperBound", hi_95.divide(new BigDecimal(1), 4, RoundingMode.CEILING));
    }

    public static BigDecimal sqrt(BigDecimal A, int SCALE) {
        BigDecimal x0 = BigDecimal.ZERO;
        BigDecimal x1 = BigDecimal.valueOf(Math.sqrt(A.doubleValue()));
        while(!x0.equals(x1)) {
            x0 = x1;
            x1 = A.divide(x0, SCALE, RoundingMode.HALF_UP);
            x1 = x1.add(x0);
            x1 = x1.divide(BigDecimal.valueOf(2), SCALE, RoundingMode.HALF_UP);
        }
        return x1;
    }

    public static String is_in_range(BigDecimal lo, BigDecimal it, BigDecimal hi) {
        if(lo.compareTo(hi) > 0) throw new RuntimeException("is_in_range error: low must be lower than high!");
        if(lo.compareTo(it) > 0) return "TOO LOW";
        if(hi.compareTo(it) < 0) return "TOO HIGH";
        return "GOOD";
    }

    @Override
    public String toString() {

        staticData();
        Data();

        return "NeonEmulatorReport{,\n" +
                "reSpinHits=" + reSpinHits + ",\n" +
                "bonusHits=" + bonusHits + ",\n" +
                "bonusWins=" + bonusWins.entrySet().stream().map(e->e.toString()+"\n").collect(Collectors.toList()) + ",\n" +
                "rtpTable=" + rtpTable.entrySet().stream().map(e->e.toString()+"\n").collect(Collectors.toList()) + ",\n" +
//                "StandardDeviation=" + StandardDeviation + ",\n" +
//                "Variance=" + Variance + ",\n" +
                "Static_Data=" + Static_Data + ",\n" +
//                "WinData="  + WinData.entrySet().stream().map(e->e.toString()+"\n").collect(Collectors.toList()) + ",\n" +
//                "WinData="  + winData.entrySet().stream().map(e->e.toString()+"\n").collect(Collectors.toList()) + ",\n" +

                '}';
    }
}

