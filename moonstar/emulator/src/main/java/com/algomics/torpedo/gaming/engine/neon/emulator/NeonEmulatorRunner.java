package com.algomics.torpedo.gaming.engine.neon.emulator;


import com.algomics.torpedo.gaming.engine.api.exception.GameEngineException;
import com.algomics.torpedo.gaming.engine.api.model.*;
import com.algomics.torpedo.gaming.engine.api.runner.BaseEmulatorRunner;
import com.algomics.torpedo.gaming.engine.api.service.GameEngineService;
import com.algomics.torpedo.gaming.engine.moonstar.bonus.NeonBonusContext;
import com.algomics.torpedo.gaming.engine.moonstar.bonus.NeonGameBonus;
import com.algomics.torpedo.gaming.engine.moonstar.model.dto.NeonGameEngineRequest;
import com.algomics.torpedo.gaming.engine.slots.api.dto.SlotsGameEngineRequest;
import com.algomics.torpedo.gaming.engine.slots.api.dto.SlotsGameEngineResponse;
import com.algomics.torpedo.gaming.engine.slots.api.model.SlotsGamePlayState;
import com.algomics.torpedo.gaming.engine.slots.model.SlotsSpinGameActivity;
import com.algomics.torpedo.gaming.engine.slots.pays.PayStep;
import lombok.Data;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;

@Component
@Data
@Slf4j
public class NeonEmulatorRunner extends BaseEmulatorRunner {

    public static BigDecimal bet;
    public static BigDecimal totalRTP;
    public static BigDecimal iterations;
    static BigDecimal sd = new BigDecimal(10.130992054);
    static BigDecimal rtp = new BigDecimal(96.01);
    static  BigDecimal for_95 = new BigDecimal(10.130992054);
    static BigDecimal for_99 = new BigDecimal(10.130992054);
    public static BigDecimal cycle;
    public static BigDecimal getBet() {
        return bet;
    }
    public static void setBet(BigDecimal bet) {
        NeonEmulatorRunner.bet = bet;
    }
    public static BigDecimal getIterations() {
        return iterations;
    }
    public static void setIterations(BigDecimal iterations) {
        NeonEmulatorRunner.iterations = iterations;
    }
    public static HashMap<BigDecimal, BigDecimal> winData = new HashMap<>();

    public static final String CASCADED = "_CASCADED";
    public static final String BURSTER = "_BURSTER";
    public static final String BURSTER_CASCADED = "_BURSTER_CASCADED";
    public static final String BASE_TOTAL = "BASE_TOTAL";
    public static final String BASE = "BASE";
    public static final String FREE_SPINS = "FREE_SPINS";

    @Autowired
    GameEngineService<NeonGameEngineRequest, SlotsGameEngineResponse> neonGameEngine;

    @Value("${rgs.games.player-bag.name:playerBag}")
    private String PLAYER_BAG;

    @Autowired
    @Qualifier("bursterMeter")
    Map<String, Object> bursterMeter;

    @SneakyThrows
    public Mono<EmulatorResult> emulateGamePlay(Integer run) {
        Map<String, Object> meter = (Map<String, Object>) bursterMeter.get("bursterMeter");
        List<GameActivity> activities = new ArrayList<>();
        //log.info("meter start {} {}", run, meter);
        boolean saveReport = Boolean.parseBoolean(getOptions().getOrDefault("report", "false"));

        StakeSettings stakeSettings = new StakeSettings();
        stakeSettings.setMinMaxLines(new Integer[]{10, 10});
        GamePlay gamePlay = null;
        gamePlay = neonGameEngine.startGame(getGameConfiguration());
//        System.out.println(gamePlay+"GamePlayy");
        NeonGameEngineRequest request = new NeonGameEngineRequest();
        request.setNoOfLines(10);
        request.setBuyFeature(Boolean.parseBoolean(getOptions().getOrDefault("buyFeature","false")));
        request.setId(Integer.parseInt(getOptions().getOrDefault("id","1")));
        request.setStakePerLine(new BigDecimal(getOptions().getOrDefault("stakePerLine", "0.02")));
        gamePlay.getGamePlayState().getData().put(PLAYER_BAG, meter);
//        System.out.println(request.isBuyFeature()+"   /////// " +request.getId());
        if (request.getStakePerLine().compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Invalid stakePerLine :" + request.getStakePerLine());
        }

        SlotsGameEngineResponse response;
        NeonEmulatorReport report = new NeonEmulatorReport();
        Map<String, BigDecimal> bonusWins = new HashMap<>();
        if (saveReport) {
            report.setBonusWins(bonusWins);
        }
        SlotsGamePlayState slotsGamePlay = null;
        SlotsSpinGameActivity slotsSpinGameActivity = null;
        String game = request.isBuyFeature() ? "BURSTER_"+request.getId()+"_PURCHASED" : "BASE";
        response = neonGameEngine.validate(request, stakeSettings, gamePlay);
        do {
            try {

                response = neonGameEngine.play(request, response.getGamePlay());
                response = neonGameEngine.processWinnings(request, response.getGamePlay(), response.getGameActivity());
                gamePlay = response.getGamePlay();
                slotsSpinGameActivity =(SlotsSpinGameActivity) response.getGameActivity();
                activities.add(slotsSpinGameActivity);
                //put base spin payout in BASE game wins.
                BigDecimal baseSpin = BigDecimal.ZERO;
                BigDecimal cascadedWins = BigDecimal.ZERO;
                BigDecimal bursterWins = BigDecimal.ZERO;
                BigDecimal bursterCascadewins = BigDecimal.ZERO;
                if (saveReport) {
                    if(slotsSpinGameActivity.getPaySteps().size()>1) {
                        //Base Game Wins. put if absent (don't add on free spin)
                        if (slotsSpinGameActivity.getPaySteps().size() > 1) {
                            PayStep payStep;
                            for (int i = 0; i < slotsSpinGameActivity.getPaySteps().size(); i++) {
                                payStep = slotsSpinGameActivity.getPaySteps().get(i);
//                                System.out.println(payStep+"paystepss");
                                boolean burster = (boolean) payStep.getData().getOrDefault("burster", false);
                                boolean burster_cascaded = (boolean) payStep.getData().getOrDefault("burster_cascaded", false);
                                boolean cascaded = (boolean) payStep.getData().getOrDefault("cascaded", false);

                                if (burster){
                                    bursterWins = bursterWins.add(payStep.getStepWins());
                                }
                                else if (burster_cascaded){
                                    bursterCascadewins = bursterCascadewins.add(payStep.getStepWins());
                                }
                                else if(cascaded){
                                    cascadedWins = cascadedWins.add(payStep.getStepWins());
                                }
                                else{
                                    baseSpin = baseSpin.add(payStep.getStepWins());
                                }
                            }
                        }
                        report.getBonusWins().put(game,report.getBonusWins().getOrDefault(game,BigDecimal.ZERO).add(baseSpin));
                        report.getBonusWins().put(game + CASCADED,report.getBonusWins().getOrDefault(game + CASCADED,BigDecimal.ZERO).add(cascadedWins));
                        report.getBonusWins().put(game + BURSTER,report.getBonusWins().getOrDefault(game + BURSTER,BigDecimal.ZERO).add(bursterWins));
                        report.getBonusWins().put(game + BURSTER_CASCADED,report.getBonusWins().getOrDefault(game + BURSTER_CASCADED,BigDecimal.ZERO).add(bursterCascadewins));
                    }
                }

            } catch (GameEngineException e) {
                e.printStackTrace();
                return Mono.error(new GameEngineException("Internal Error"));
            }
            NeonBonusContext neonBonusContext = (NeonBonusContext) slotsSpinGameActivity.getBonusContext();
            slotsGamePlay = (SlotsGamePlayState) gamePlay.getGamePlayState();
            if(game.equals("BASE") && slotsSpinGameActivity.getBonusAwarded()==NeonGameBonus.FREE_SPINS) {
                game = FREE_SPINS;
                report.getBonusHits().put(FREE_SPINS + "_HIT_"+neonBonusContext.getFreeSpinsContext().getFreeSpinsAwarded(), 1L);
            }
            else if(game.equals(FREE_SPINS) && slotsSpinGameActivity.getBonusAwarded()==NeonGameBonus.FREE_SPINS) {
                game = FREE_SPINS;
                report.getBonusHits().put(FREE_SPINS + "_RE-TRIGGER_HIT",report.getBonusHits().getOrDefault(FREE_SPINS + "_RE-TRIGGER_HIT", 0L) + 1);
            }
        } while (slotsGamePlay.getGameStatus() != GameStatus.COMPLETED);

        Map<String, Object> meterAccumulated = (Map<String, Object>) response.getGamePlay().getGamePlayState().getData().get(PLAYER_BAG);
        meter.putAll(meterAccumulated);
        //log.info("meter end {} {}", run, meter);

        EmulatorResult result = new EmulatorResult();
        result.setRun(run);
        result.setStake(response.getTotalBet());
        result.setWinnings(response.getTotalPayout());
        report.setBonusWins(bonusWins);
        result.setReport(report);

        log.debug("stake: {}, wins: {} ", response.getTotalBet(), response.getTotalPayout());
        if (saveReport && response.getTotalPayout().doubleValue()>0) {
            BigDecimal freeSpinsWins = report.getBonusWins().getOrDefault(FREE_SPINS, BigDecimal.ZERO);
            BigDecimal freeSpinsCascaded = report.getBonusWins().getOrDefault(FREE_SPINS + CASCADED, BigDecimal.ZERO);
            BigDecimal freeSpinsBurster = report.getBonusWins().getOrDefault(FREE_SPINS + BURSTER, BigDecimal.ZERO);
            BigDecimal freeSpinsBursterCascaded = report.getBonusWins().getOrDefault(FREE_SPINS + BURSTER_CASCADED, BigDecimal.ZERO);
            BigDecimal freeSpinWinsTotal = freeSpinsWins.add(freeSpinsCascaded).add(freeSpinsBursterCascaded).add(freeSpinsBurster);


            BigDecimal burster = report.getBonusWins().getOrDefault("BURSTER_"+request.getId()+"_PURCHASED", BigDecimal.ZERO);
            BigDecimal bursterSpinsCascaded = report.getBonusWins().getOrDefault("BURSTER_"+request.getId()+"_PURCHASED" + CASCADED, BigDecimal.ZERO);
            BigDecimal bursterSpinsBurster = report.getBonusWins().getOrDefault("BURSTER_"+request.getId()+"_PURCHASED" + BURSTER, BigDecimal.ZERO);
            BigDecimal bursterSpinsBursterCascaded = report.getBonusWins().getOrDefault("BURSTER_"+request.getId()+"_PURCHASED" + BURSTER_CASCADED, BigDecimal.ZERO);
            BigDecimal bursterTotal = burster.add(bursterSpinsCascaded).add(bursterSpinsBurster).add(bursterSpinsBursterCascaded);


            BigDecimal baseWins = report.getBonusWins().getOrDefault(BASE, BigDecimal.ZERO);
            BigDecimal baseCascaded = report.getBonusWins().getOrDefault(BASE + CASCADED, BigDecimal.ZERO);
            BigDecimal baseBurster = report.getBonusWins().getOrDefault(BASE+ BURSTER, BigDecimal.ZERO);
            BigDecimal baseBursterCascaded = report.getBonusWins().getOrDefault(BASE + BURSTER_CASCADED, BigDecimal.ZERO);
            BigDecimal baseTotal = baseWins.add(baseCascaded).add(baseBurster).add(baseBursterCascaded);

            BigDecimal totalWin = baseTotal.add(freeSpinWinsTotal).add(bursterTotal);
            BigDecimal RTP_Cal= BigDecimal.ZERO;
            winData.putIfAbsent(totalWin, BigDecimal.ZERO);
            winData.put(totalWin, winData.get(totalWin).add(BigDecimal.valueOf(1)));

            report.getBonusWins().put("BASE_TOTAL", baseTotal);
            report.getBonusWins().put("FREE_SPINS_TOTAL", freeSpinWinsTotal);
            report.getBonusWins().put("BURSTER_TOTAL", bursterTotal);
            report.getBonusWins().put("Total", totalWin);
            bet = response.getTotalBet();
        }
       /* if(result.getWinnings().doubleValue()>0)
            System.out.println(result.toString());*/
        return Mono.just(result);
    }


    @Override
    public String getGameConfiguration() {
        String gc = "neon";
        return this.getOptions() == null ? gc : this.getOptions().getOrDefault("gc", gc);
    }


    @Override
    public Flux<EmulatorResult> start(Integer runs, Integer batchSize, Map<String, String> options, CountDownLatch latch) {
        setOptions(options);
        iterations = BigDecimal.valueOf(runs);
        return Flux.range(0, runs)
                .flatMap(run -> {
                    latch.countDown();
                    try {
                        return this.emulateGamePlay(run);
                    } catch (GameEngineException e) {
                        e.printStackTrace();
                        return Mono.error(new GameEngineException("Game emulation failed", e));
                    }
                });
    }
}
