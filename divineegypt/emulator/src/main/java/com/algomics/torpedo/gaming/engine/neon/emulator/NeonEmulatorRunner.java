package com.algomics.torpedo.gaming.engine.neon.emulator;


import com.algomics.torpedo.gaming.engine.api.exception.GameEngineException;
import com.algomics.torpedo.gaming.engine.api.model.*;
import com.algomics.torpedo.gaming.engine.api.runner.BaseEmulatorRunner;
import com.algomics.torpedo.gaming.engine.api.service.GameEngineService;
import com.algomics.torpedo.gaming.engine.divineegypt.bonus.NeonBonusContext;
import com.algomics.torpedo.gaming.engine.divineegypt.bonus.NeonGameBonus;
import com.algomics.torpedo.gaming.engine.divineegypt.model.dto.NeonGameEngineRequest;
import com.algomics.torpedo.gaming.engine.slots.api.dto.SlotsGameEngineResponse;
import com.algomics.torpedo.gaming.engine.slots.api.model.SlotsGamePlayState;
import com.algomics.torpedo.gaming.engine.slots.model.SlotsSpinGameActivity;
import lombok.Data;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
@Component
@Data
@Slf4j
public class NeonEmulatorRunner extends BaseEmulatorRunner {

    public static final String WHEEL_BONUS = "WHEEL_BONUS";
    public static final String BASE_TOTAL = "BASE_TOTAL";
    public static final String BASE = "BASE";
    public static final String FREE_SPINS = "FREE_SPINS";


    @Autowired
    GameEngineService<NeonGameEngineRequest, SlotsGameEngineResponse> neonGameEngine;


    @SneakyThrows
    public Mono<EmulatorResult> emulateGamePlay(Integer run) {

        List<GameActivity> activities = new ArrayList<>();
        boolean saveReport = Boolean.parseBoolean(getOptions().getOrDefault("report", "false"));

        StakeSettings stakeSettings = new StakeSettings();
        stakeSettings.setMinMaxLines(new Integer[]{150, 150});
        GamePlay gamePlay = null;
        gamePlay = neonGameEngine.startGame(getGameConfiguration());
        NeonGameEngineRequest request = new NeonGameEngineRequest();
        request.setNoOfLines(150);
        String game = "BASE";
//          String game = "BUY_FEATURE";
//        request.setBuyFeature(true);
//        request.setId(1);


        request.setStakePerLine(new BigDecimal(getOptions().getOrDefault("stakePerLine", "0.02")));

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

        response = neonGameEngine.validate(request, stakeSettings, gamePlay);
        do {
            try {
                response = neonGameEngine.play(request, response.getGamePlay());
                response = neonGameEngine.processWinnings(request, response.getGamePlay(), response.getGameActivity());
                gamePlay = response.getGamePlay();
                slotsSpinGameActivity =(SlotsSpinGameActivity) response.getGameActivity();
                activities.add(slotsSpinGameActivity);
                BigDecimal baseSpin = BigDecimal.ZERO;
                if (saveReport) {
                    if ( response.getSpinPayout().compareTo(BigDecimal.valueOf(0))==1) {
                        //Base Game Wins. put if absent (don't add on free spin)
                        baseSpin = response.getSpinPayout();
                    }
                }
                report.getBonusWins().put(game,report.getBonusWins().getOrDefault(game,BigDecimal.ZERO).add(baseSpin));
//                    }
//                }
            } catch (GameEngineException e) {
                e.printStackTrace();
                return Mono.error(new GameEngineException("Internal Error"));
            }
            NeonBonusContext neonBonusContext = (NeonBonusContext) slotsSpinGameActivity.getBonusContext();
            slotsGamePlay = (SlotsGamePlayState) gamePlay.getGamePlayState();

//            WheelOption stopOption = neonBonusContext.getWheelStop();
            if(game.equals("BASE") && slotsSpinGameActivity.getBonusAwarded()==NeonGameBonus.FREE_SPINS) {
                game = FREE_SPINS;
                report.getBonusHits().put(FREE_SPINS + "_HIT_"+neonBonusContext.getFreeSpinsContext().getFreeSpinsAwarded(), 1L);
            }
            else if(game.equals(FREE_SPINS) && slotsSpinGameActivity.getBonusAwarded()==NeonGameBonus.FREE_SPINS) {
                game = FREE_SPINS;
                report.getBonusHits().put(FREE_SPINS + "_RE-TRIGGER_HIT",report.getBonusHits().getOrDefault(FREE_SPINS + "_RE-TRIGGER_HIT", 0L) + 1);
            }
            else if(game.equals("WHEEL_BONUS") && slotsSpinGameActivity.getBonusAwarded()==NeonGameBonus.FREE_SPINS) {
                game = FREE_SPINS;
                report.getBonusHits().put(FREE_SPINS + "_RE-TRIGGER_HIT",report.getBonusHits().getOrDefault(FREE_SPINS + "_RE-TRIGGER_HIT", 0L) + 1);
            }

        } while (slotsGamePlay.getGameStatus() != GameStatus.COMPLETED);
        EmulatorResult result = new EmulatorResult();
        result.setRun(run);
        result.setStake(response.getTotalBet());

        result.setWinnings(response.getTotalPayout());
        report.setBonusWins(bonusWins);
        result.setReport(report);
        log.debug("stake: {}, wins: {} ", response.getTotalBet(), response.getTotalPayout());
        if (saveReport && response.getTotalPayout().doubleValue()>0) {
            BigDecimal freeSpinTotal = report.getBonusWins().getOrDefault(FREE_SPINS, BigDecimal.ZERO);
            BigDecimal baseTotal = report.getBonusWins().getOrDefault(BASE, BigDecimal.ZERO);
            BigDecimal bonusTotal = report.getBonusWins().getOrDefault(WHEEL_BONUS, BigDecimal.ZERO);
            BigDecimal totalWin = baseTotal.add(freeSpinTotal).add(bonusTotal) ;
            report.getBonusWins().put("TOTAL", totalWin);
        }
        return Mono.just(result);
    }

    @Override
    public String getGameConfiguration() {
        String gc = "degypt96";
        return this.getOptions() == null ? gc : this.getOptions().getOrDefault("gc", gc);
    }

    @Override
    public Flux<EmulatorResult> start(Integer runs, Integer batchSize, Map<String, String> options, CountDownLatch latch) {
        setOptions(options);
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
