//package com.algomics.torpedo.gaming.engine.moonstar;
//
//import com.algomics.torpedo.gaming.engine.api.exception.GameEngineException;
//import com.algomics.torpedo.gaming.engine.api.model.*;
//import com.algomics.torpedo.gaming.engine.moonstar.bonus.NeonBonusContext;
//import com.algomics.torpedo.gaming.engine.moonstar.bonus.NeonGameBonus;
//import com.algomics.torpedo.gaming.engine.moonstar.config.NeonGameConfiguration;
//import com.algomics.torpedo.gaming.engine.moonstar.model.BursterOption;
//import com.algomics.torpedo.gaming.engine.moonstar.model.dto.NeonGameEngineRequest;
//import com.algomics.torpedo.gaming.engine.moonstar.service.NeonGameEngine;
//import com.algomics.torpedo.gaming.engine.moonstar.symbol.NeonSymbol;
//import com.algomics.torpedo.gaming.engine.slots.api.dto.SlotsGameEngineResponse;
//import com.algomics.torpedo.gaming.engine.slots.aspects.ForceReelLayoutSelector;
//import com.algomics.torpedo.gaming.engine.slots.aspects.ForceReelSpinner;
//import com.algomics.torpedo.gaming.engine.slots.model.SlotsSpinGameActivity;
//import com.algomics.torpedo.gaming.engine.slots.pays.PayStep;
//import com.algomics.torpedo.gaming.engine.slots.reels.ReelLayout;
//import com.algomics.torpedo.gaming.engine.slots.reels.SlotReel;
//import org.junit.jupiter.api.RepeatedTest;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.junit.jupiter.params.ParameterizedTest;
//import org.junit.jupiter.params.provider.ValueSource;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.test.context.ContextConfiguration;
//import org.springframework.test.context.junit.jupiter.SpringExtension;
//import java.io.*;
//import java.math.BigDecimal;
//import java.nio.file.Path;
//import java.util.Arrays;
//import java.util.HashMap;
//import java.util.List;
//import java.util.stream.Collectors;
//import static org.junit.jupiter.api.Assertions.*;
//
///**
// * The ROAGameEngineTest.
// */
//@ExtendWith({SpringExtension.class})
//@ContextConfiguration(classes = {TestConfig.class})
//class NeonGameEngineTest {
//
//
//
//    @Value("${rgs.games.player-bag.name:playerBag}")
//    private String PLAYER_BAG;
//
//    public static final String GAME_CONFIGURATION = "neon";
//
//    @Autowired
//    NeonGameEngine neonGameEngine;
//    @RepeatedTest(1)
//    void test_game_play() throws GameEngineException {
//        GamePlay gamePlay = neonGameEngine.startGame(GAME_CONFIGURATION);
//        SlotsGameEngineResponse response = neonGameEngine.validate(getGameRequest(), getStakeSettings(), gamePlay);
//        response = neonGameEngine.play(getGameRequest(), response.getGamePlay());
//        assertNotNull(response);
//    }
//
//
//    @Test
//    void test_force_reelset_1() throws GameEngineException {
//        int reel = 1;
//
//        ForceReelLayoutSelector.set(reel-1);
//        ForceReelSpinner.set(List.of(0,0,0,0,0,0,0));
//        GamePlay gamePlay = neonGameEngine.startGame(GAME_CONFIGURATION);
//        SlotsGameEngineResponse response = neonGameEngine.validate(getGameRequest(), getStakeSettings(), gamePlay);
//        response = neonGameEngine.play(getGameRequest(), response.getGamePlay());
//        SlotsSpinGameActivity gameActivity = (SlotsSpinGameActivity) response.getGameActivity();
//
//        assertEquals(0, gameActivity.getSelectedReel());
//        assertEquals("AA,AA,AA,EE,EE,EE,BB", gameActivity.getPaySteps().get(0).getStepSymbolGrid().getSymbolGrid().get(0).stream().map(Object::toString).reduce((s, s2) -> s+","+s2).get());
//        assertEquals(0, gameActivity.getLastPayStep().getClusters().size());
//
//        ReelLayout<NeonSymbol, NeonGameBonus> reelSet = getSlotReels(reel-1);
//
//        assertEquals(50, reelSet.getWeight());
//        assertEquals(reelset1, getReelSet(reelSet.getReels()));
//    }
//
//    /*@Test
//    void test_force_reelset_1_wins() throws GameEngineException {
//        int reel = 1;
//
//        ForceReelLayoutSelector.set(reel-1);
//        ForceReelSpinner.set(List.of(15, 33, 12, 21, 11, 5, 14));
//        GamePlay gamePlay = neonGameEngine.startGame(GAME_CONFIGURATION);
//        SlotsGameEngineResponse response = neonGameEngine.validate(getGameRequest(), getStakeSettings(), gamePlay);
//        response = neonGameEngine.play(getGameRequest(), response.getGamePlay());
//
//        BigDecimal total = BigDecimal.ZERO;
//        while (response.getGamePlay().getGamePlayState().getGameStatus() != GameStatus.COMPLETED){
//            response = neonGameEngine.validate(getGameRequest(), getStakeSettings(), gamePlay);
//            response = neonGameEngine.play(getGameRequest(), response.getGamePlay());
//            total = total.add(response.getSpinPayout());
//        }
//
//        assertEquals(5, total.doubleValue());
//    }*/
//
//    @Test
//    void test_force_reelset_2() throws GameEngineException {
//        int reel = 2;
//        ForceReelLayoutSelector.set(reel-1);
//        ForceReelSpinner.set(List.of(0,0,0,0,0,0,0));
//
//        GamePlay gamePlay = neonGameEngine.startGame(GAME_CONFIGURATION);
//        SlotsGameEngineResponse response = neonGameEngine.validate(getGameRequest(), getStakeSettings(), gamePlay);
//        response = neonGameEngine.play(getGameRequest(), response.getGamePlay());
//        SlotsSpinGameActivity gameActivity = (SlotsSpinGameActivity) response.getGameActivity();
//
//        assertEquals(1, gameActivity.getSelectedReel());
//        assertEquals("AA,AA,AA,EE,EE,EE,BB", gameActivity.getPaySteps().get(0).getStepSymbolGrid().getSymbolGrid().get(0).stream().map(Object::toString).reduce((s, s2) -> s+","+s2).get());
//
//        ReelLayout<NeonSymbol, NeonGameBonus> reelSet = getSlotReels(reel-1);
//        assertEquals(20, reelSet.getWeight());
//        assertEquals(reelset2, getReelSet(reelSet.getReels()));
//    }
//
//    @Test
//    void test_force_reelset_3() throws GameEngineException {
//        int reel = 3;
//
//        ForceReelLayoutSelector.set(reel-1);
//        ForceReelSpinner.set(List.of(0,0,0,0,0,0,0));
//
//        GamePlay gamePlay = neonGameEngine.startGame(GAME_CONFIGURATION);
//        SlotsGameEngineResponse response = neonGameEngine.validate(getGameRequest(), getStakeSettings(), gamePlay);
//        response = neonGameEngine.play(getGameRequest(), response.getGamePlay());
//        SlotsSpinGameActivity gameActivity = (SlotsSpinGameActivity) response.getGameActivity();
//
//        assertEquals(2, gameActivity.getSelectedReel());
//        assertEquals("AA,AA,AA,EE,EE,EE,BB", gameActivity.getPaySteps().get(0).getStepSymbolGrid().getSymbolGrid().get(0).stream().map(Object::toString).reduce((s, s2) -> s+","+s2).get());
//
//        ReelLayout<NeonSymbol, NeonGameBonus> reelSet = getSlotReels(reel-1);
//        assertEquals(20, reelSet.getWeight());
//        assertEquals(reelset3, getReelSet(reelSet.getReels()));
//    }
//
//
//    @Test
//    void test_force_reelset_4() throws GameEngineException {
//        int reel = 4;
//        ForceReelLayoutSelector.set(reel-1);
//        ForceReelSpinner.set(List.of(0,0,0,0,0,0,0));
//
//        GamePlay gamePlay = neonGameEngine.startGame(GAME_CONFIGURATION);
//        SlotsGameEngineResponse response = neonGameEngine.validate(getGameRequest(), getStakeSettings(), gamePlay);
//        response = neonGameEngine.play(getGameRequest(), response.getGamePlay());
//        SlotsSpinGameActivity gameActivity = (SlotsSpinGameActivity) response.getGameActivity();
//
//        assertEquals(3, gameActivity.getSelectedReel());
//        assertEquals("AA,AA,AA,EE,EE,EE,BB", gameActivity.getPaySteps().get(0).getStepSymbolGrid().getSymbolGrid().get(0).stream().map(Object::toString).reduce((s, s2) -> s+","+s2).get());
//
//        ReelLayout<NeonSymbol, NeonGameBonus> reelSet = getSlotReels(reel-1);
//        assertEquals(70, reelSet.getWeight());
//        assertEquals(reelset4, getReelSet(reelSet.getReels()));
//    }
//
//    @Test
//    void test_force_reelset_5() throws GameEngineException {
//        int reel = 5;
//        ForceReelLayoutSelector.set(reel-1);
//        ForceReelSpinner.set(List.of(0,0,0,0,0,0,0));
//
//        GamePlay gamePlay = neonGameEngine.startGame(GAME_CONFIGURATION);
//        SlotsGameEngineResponse response = neonGameEngine.validate(getGameRequest(), getStakeSettings(), gamePlay);
//        response = neonGameEngine.play(getGameRequest(), response.getGamePlay());
//        SlotsSpinGameActivity gameActivity = (SlotsSpinGameActivity) response.getGameActivity();
//
//        assertEquals(4, gameActivity.getSelectedReel());
//        assertEquals("AA,AA,AA,EE,EE,EE,BB", gameActivity.getPaySteps().get(0).getStepSymbolGrid().getSymbolGrid().get(0).stream().map(Object::toString).reduce((s, s2) -> s+","+s2).get());
//
//        ReelLayout<NeonSymbol, NeonGameBonus> reelSet = getSlotReels(reel-1);
//        assertEquals(70, reelSet.getWeight());
//        assertEquals(reelset5, getReelSet(reelSet.getReels()));
//
//    }
//
//
//    @RepeatedTest(1)
//    void test_buy_BURSTER_1() throws GameEngineException {
//
//        GamePlay gamePlay = neonGameEngine.startGame(GAME_CONFIGURATION);
//        NeonGameEngineRequest request = getGameRequest();
//        request.setBuyFeature(true);
//        request.setId(1);
//
//        SlotsGameEngineResponse response = neonGameEngine.validate(request, getStakeSettings(), gamePlay);
//        response = neonGameEngine.play(request, response.getGamePlay());
//
//        SlotsSpinGameActivity slotsSpinGameActivity = (SlotsSpinGameActivity) response.getGameActivity();
//        assertEquals(NeonGameBonus.BURSTER_1_PURCHASED,slotsSpinGameActivity.getBonusAwarded());
//    }
//
//    @RepeatedTest(1)
//    void test_buy_BURSTER_2() throws GameEngineException {
//
//        GamePlay gamePlay = neonGameEngine.startGame(GAME_CONFIGURATION);
//        NeonGameEngineRequest request = getGameRequest();
//        request.setBuyFeature(true);
//        request.setId(2);
//
//        SlotsGameEngineResponse response = neonGameEngine.validate(request, getStakeSettings(), gamePlay);
//        response = neonGameEngine.play(request, response.getGamePlay());
//
//        SlotsSpinGameActivity slotsSpinGameActivity = (SlotsSpinGameActivity) response.getGameActivity();
//        assertEquals(NeonGameBonus.BURSTER_2_PURCHASED,slotsSpinGameActivity.getBonusAwarded());
//    }
//
//    /*@Test
//    void test_force_reelset_free_spins() throws GameEngineException {
//        int reel = 6;
//        ForceReelLayoutSelector.set(reel-1);
//        //ForceReelSpinner.set(List.of(0,0,0,0,0,0,0));
//
//        GamePlay gamePlay = neonGameEngine.startGame(GAME_CONFIGURATION);
//        SlotsGameEngineResponse response = neonGameEngine.validate(getGameRequest(), getStakeSettings(), gamePlay);
//        response = neonGameEngine.play(getGameRequest(), response.getGamePlay());
//        SlotsSpinGameActivity gameActivity = (SlotsSpinGameActivity) response.getGameActivity();
//
//        assertEquals(reel-1, gameActivity.getSelectedReel());
//        ReelLayout<NeonSymbol, NeonGameBonus> reelSet = getSlotReels(reel-1);
//        assertEquals(0, reelSet.getWeight());
//        assertEquals(freeSpinsReel, getReelSet(reelSet.getReels()));
//
//    }*/
//
//
//
//   /* @Test
//    void test_force_reelset_free_spins_buster_feature() throws GameEngineException {
//        int reel = 7;
//        ForceReelLayoutSelector.set(reel-1);
//        //ForceReelSpinner.set(List.of(0,0,0,0,0,0,0));
//
//        GamePlay gamePlay = neonGameEngine.startGame(GAME_CONFIGURATION);
//        SlotsGameEngineResponse response = neonGameEngine.validate(getGameRequest(), getStakeSettings(), gamePlay);
//        response = neonGameEngine.play(getGameRequest(), response.getGamePlay());
//        SlotsSpinGameActivity gameActivity = (SlotsSpinGameActivity) response.getGameActivity();
//
//        assertEquals(reel-1, gameActivity.getSelectedReel());
//        ReelLayout<NeonSymbol, NeonGameBonus> reelSet = getSlotReels(reel-1);
//        assertEquals(0, reelSet.getWeight());
//        assertEquals(freeSpinsReel, getReelSet(reelSet.getReels()));
//
//    }*/
//
//    @Test
//    void test_cluster_without_cascade_pays() throws GameEngineException {
//        ForceReelLayoutSelector.set(0);
//        ForceReelSpinner.set(List.of(0,11,0,0,0,0,0));
//
//        GamePlay gamePlay = neonGameEngine.startGame(GAME_CONFIGURATION);
//        SlotsGameEngineResponse response = neonGameEngine.validate(getGameRequest(), getStakeSettings(), gamePlay);
//        response = neonGameEngine.play(getGameRequest(), response.getGamePlay());
//        SlotsSpinGameActivity gameActivity = (SlotsSpinGameActivity) response.getGameActivity();
//
//        assertEquals(0, gameActivity.getSelectedReel());
//
//        PayStep payStep = gameActivity.getPaySteps().get(0);
//        assertEquals("AA,AA,AA,EE,EE,EE,BB", gameActivity.getPaySteps().get(0).getStepSymbolGrid().getSymbolGrid().get(0).stream().map(Object::toString).reduce((s, s2) -> s+","+s2).get());
//
//        assertEquals(NeonSymbol.AA, payStep.getClusters().get(0).getSymbol());
//        assertEquals(7, payStep.getClusters().get(0).getSymbolCount());
//        assertEquals(0.3, payStep.getClusters().get(0).getMultiplier().doubleValue());
//        assertEquals(NeonSymbol.EE, payStep.getClusters().get(1).getSymbol());
//        assertEquals(8, payStep.getClusters().get(1).getSymbolCount());
//        assertEquals(0.7, payStep.getClusters().get(1).getMultiplier().doubleValue());
//
//
//        //cascade test
//        String finalSymbolGrid = """
//                        \nDD,CC,DD,FF,AA,FF,FF
//                        HH,CC,HH,CC,AA,CC,CC
//                        DD,CC,BB,GG,AA,GG,GG
//                        HH,FF,BB,GG,EE,GG,GG
//                        HH,FF,BB,CC,EE,CC,CC
//                        HH,FF,AA,CC,BB,CC,CC
//                        BB,DD,AA,CC,BB,CC,CC\n"""
//                .replaceAll("[\\t ]", "");
//
//
//        //cascade test
//        payStep = gameActivity.getLastPayStep();
//
//        assertEquals(finalSymbolGrid, payStep.getStepSymbolGrid().toString());
//    }
//
//
//    @Test
//    void test_cluster_cascading_with_wild_contributing() throws GameEngineException {
//        ForceReelLayoutSelector.set(1);
//        ForceReelSpinner.set(List.of(18,8,5,12,12,11,11));
//
//        GamePlay gamePlay = neonGameEngine.startGame(GAME_CONFIGURATION);
//        StakeSettings settings = getStakeSettings();
//
//        SlotsGameEngineResponse response = neonGameEngine.validate(getGameRequest(), settings, gamePlay);
//        response = neonGameEngine.play(getGameRequest(), response.getGamePlay());
//        response.getGameActivity().setTotalWinnings(BigDecimal.ZERO);
//        response = neonGameEngine.processWinnings(getGameRequest(), response.getGamePlay(), response.getGameActivity());
//        SlotsSpinGameActivity gameActivity = (SlotsSpinGameActivity) response.getGameActivity();
//
//        gameActivity.getPaySteps().forEach(payStep -> {
//            System.out.println(payStep.getStepSymbolGrid().toString());
//            System.out.println(payStep.getClusters());
//        });
//
//
//        assertEquals(54.0, gameActivity.getTotalWinnings().doubleValue());
//
//       /* assertEquals(2, payStep1.getClusters().size());
//        assertEquals(NeonSymbol.AA, payStep1.getClusters().get(0).getSymbol());
//        assertEquals(12, payStep1.getClusters().get(0).getSymbolCount());
//        assertEquals(0.4, payStep1.getClusters().get(0).getWinnings().doubleValue());
//
//        assertEquals(NeonSymbol.AA, payStep1.getClusters().get(1).getSymbol());
//        assertEquals(9, payStep1.getClusters().get(1).getSymbolCount());
//        assertEquals(0.14, payStep1.getClusters().get(1).getWinnings().doubleValue());
//
//
//        assertEquals(1, payStep2.getClusters().size());
//        assertEquals(NeonSymbol.FF, payStep2.getClusters().get(0).getSymbol());
//        assertEquals(14, payStep2.getClusters().get(0).getSymbolCount());
//        assertEquals(0.6, payStep2.getClusters().get(0).getWinnings().doubleValue());
//
//        assertEquals(1, payStep3.getClusters().size());
//        assertEquals(NeonSymbol.CC, payStep3.getClusters().get(0).getSymbol());
//        assertEquals(9, payStep3.getClusters().get(0).getSymbolCount());
//        assertEquals(0.16, payStep3.getClusters().get(0).getWinnings().doubleValue());
//
//        assertEquals(0, payStep4.getClusters().size());*/
//
//        assertEquals(1, gameActivity.getSelectedReel());
//    }
//
//
//    @ParameterizedTest()
//    @ValueSource(strings = {"src/test/resources/reel-stops-reel-1.csv",
//            "src/test/resources/reel-stops-reel-2.csv",
//            "src/test/resources/reel-stops-reel-3.csv",
//            "src/test/resources/reel-stops-reel-4.csv",
//            "src/test/resources/reel-stops-reel-5.csv"})
//
//    void test_reels_stops_wins(String fileName) throws GameEngineException, FileNotFoundException {
//
//        File reelStopsFile = Path.of(fileName).toFile();
//        if(!reelStopsFile.exists())
//            return;
//
//        BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(reelStopsFile)));
//
//
//        br.lines().forEach(line -> {
//            //RANDOM STOPS,ClusterWin,CascadeWin,WildCount
//            int reel = Integer.parseInt(reelStopsFile.getName().split("[.]")[0].split("-")[3]) - 1;
//            assertFalse(line.trim().isBlank());
//            ForceReelLayoutSelector.set(reel);
//            String[] parts = line.split(",");
//            assertEquals(4, parts.length);
//            String reelStops = parts[0];
//            BigDecimal baseWins = new BigDecimal(parts[1]);
//            BigDecimal cascadeWins = new BigDecimal(parts[2]);
//            BigDecimal total = baseWins.add(cascadeWins);
//            //long noOfWilds = Long.parseLong(parts[3]);
//
//            String[] reelStopsStr = reelStops.trim().split("-");
//
//            assertEquals(7, reelStopsStr.length);
//
//            ForceReelSpinner.set(Arrays.stream(reelStopsStr)
//                    .map(Integer::parseInt).collect(Collectors.toList()));
//
//            GamePlay gamePlay = neonGameEngine.startGame(GAME_CONFIGURATION);
//            StakeSettings settings = getStakeSettings();
//
//            SlotsGameEngineResponse response = neonGameEngine.validate(getGameRequest(), settings, gamePlay);
//            response = neonGameEngine.play(getGameRequest(), response.getGamePlay());
//            response = neonGameEngine.processWinnings(getGameRequest(), response.getGamePlay(), response.getGameActivity());
//
//
//            SlotsSpinGameActivity gameActivity = (SlotsSpinGameActivity) response.getGameActivity();
//
//            BigDecimal baseWinsActual = gameActivity.getPaySteps().get(0).getStepWins();
//            BigDecimal clusterWinsActual = gameActivity.getWinnings().subtract(baseWinsActual);
//
//            boolean equals = total.doubleValue() == gameActivity.getWinnings().doubleValue();
//            if(!equals)
//                System.out.printf("Payout mismatch %s expected %s actual %s%n",Arrays.toString(reelStopsStr),
//                        total.doubleValue(), gameActivity.getWinnings().doubleValue() );
//            assertTrue(equals);
//            assertEquals(baseWins.doubleValue(), baseWinsActual.doubleValue());
//            assertEquals(cascadeWins.doubleValue(), clusterWinsActual.doubleValue());
//        });
//    }
//
//
//    @ParameterizedTest()
//    @ValueSource(ints = {0})
//    void test_cluster_win_with_buster_feature(int option) throws GameEngineException {
//        ForceReelLayoutSelector.set(0);
//
//        ForceReelSpinner.set(List.of(0,0,0,0,0,0,0));
//
//        GamePlay gamePlay = neonGameEngine.startGame(GAME_CONFIGURATION);
//
//
//        NeonGameConfiguration gc = neonGameEngine.getConfigurationMap().get(GAME_CONFIGURATION);
//        BursterOption bursterOption = gc.getBonusGameConfiguration().getWildBursterMeter().getBonusOptions().get(option);
//        for (int i = 0; i < gc.getBonusGameConfiguration().getWildBursterMeter().getBonusOptions().size(); i++) {
//            if(i == option)
//                gc.getBonusGameConfiguration().getWildBursterMeter().getBonusOptions().get(i).setWeight(1);
//            else
//                gc.getBonusGameConfiguration().getWildBursterMeter().getBonusOptions().get(i).setWeight(0);
//        }
//
//        StakeSettings settings = getStakeSettings();
//        NeonGameEngineRequest request = getGameRequest();
//        SlotsGameEngineResponse response = neonGameEngine.validate(request, settings, gamePlay);
//
//        GamePlayState gamePlayState = response.getGamePlay().getGamePlayState();
//        gamePlayState.setData(new HashMap<>());
//
//        String externalStateKey = getExternalStateKey(request.getStakePerLine().doubleValue(),gamePlay);
//
//        HashMap<String, Double> map = new HashMap<>();
//
//
//        map.put(externalStateKey, 545.0);
//        gamePlayState.getData().put(PLAYER_BAG,map);
//
//        response = neonGameEngine.play(getGameRequest(), response.getGamePlay());
//        response = neonGameEngine.processWinnings(getGameRequest(), response.getGamePlay(), response.getGameActivity());
//        SlotsSpinGameActivity gameActivity = (SlotsSpinGameActivity) response.getGameActivity();
//        gamePlay = response.getGamePlay();
//        NeonBonusContext bonusContext = (NeonBonusContext) gameActivity.getBonusContext();
//
//        assertEquals(1D, bonusContext.getBonusMultiplier().doubleValue());
//        assertArrayEquals(bursterOption.getPositions(), bonusContext.getBursterPositions());
//        assertEquals(NeonGameBonus.WILD_BURSTER, gameActivity.getBonusContext().getBonusAwarded());
//        map = (HashMap<String, Double>) gamePlay.getGamePlayState().getData().get(PLAYER_BAG);
//        assertEquals(0,map.get(externalStateKey));
//        assertEquals(0, bonusContext.getPreviousMeter());
//        assertTrue(bonusContext.getMeter()>=550);
//
//        assertEquals(GameStatus.COMPLETED, gamePlayState.getGameStatus());
//    }
///*
//    @RepeatedTest(1)
//    void test_free_spins_10() throws GameEngineException {
//        ForceReelLayoutSelector.set(1);
//        ForceReelSpinner.set(List.of(18,4,5,15,0,0,15));
//
//        GamePlay gamePlay = neonGameEngine.startGame(GAME_CONFIGURATION);
//
//        StakeSettings settings = getStakeSettings();
//
//        SlotsGameEngineResponse response = neonGameEngine.validate(getGameRequest(), settings, gamePlay);
//
//        GamePlayState gamePlayState = response.getGamePlay().getGamePlayState();
//        gamePlayState.setData(new HashMap<>());
//
//        HashMap<String, Double> map = new HashMap<>();
//        map.put("neon_0_02", 102.6);
//        gamePlayState.getData().put(PLAYER_BAG,map);
//
//        response = neonGameEngine.play(getGameRequest(), response.getGamePlay());
//        response = neonGameEngine.processWinnings(getGameRequest(), response.getGamePlay(), response.getGameActivity());
//        SlotsSpinGameActivity gameActivity = (SlotsSpinGameActivity) response.getGameActivity();
//
//        NeonBonusContext bonusContext = (NeonBonusContext) gameActivity.getBonusContext();
//
//        assertEquals(GameStatus.INPROGRESS, gamePlayState.getGameStatus());
//        assertEquals(1D, bonusContext.getBonusMultiplier().doubleValue());
//        assertEquals(NeonGameBonus.FREE_SPINS, gameActivity.getBonusContext().getBonusAwarded());
//        assertEquals(10, ((NeonBonusContext) gameActivity.getBonusContext()).getFreeSpinsContext().getFreeSpinsAwarded());
//        assertTrue( bonusContext.getPreviousMeter() != 0);
//        //assertEquals(109.58,bonusContext.getMeter());
//        List<GameActivity> activities = new ArrayList<>();
//        activities.add(gameActivity);
//        while (response.getGamePlay().getStatus()!=GameStatus.COMPLETED){
//            response = neonGameEngine.play(getGameRequest(), response.getGamePlay());
//            response = neonGameEngine.processWinnings(getGameRequest(), response.getGamePlay(), response.getGameActivity());
//            activities.add(response.getGameActivity());
//        }
//        gameActivity = (SlotsSpinGameActivity) response.getGameActivity();
//        assertEquals(GameStatus.COMPLETED, gamePlayState.getGameStatus());
//        assertNull(gameActivity.getBonusAwarded());
//    }
//
//
//    @RepeatedTest(1)
//    void test_free_spins_15() throws GameEngineException {
//        ForceReelLayoutSelector.set(1);
//        ForceReelSpinner.set(List.of(18,4,5,15,0,11,15));
//
//        GamePlay gamePlay = neonGameEngine.startGame(GAME_CONFIGURATION);
//
//        StakeSettings settings = getStakeSettings();
//
//        SlotsGameEngineResponse response = neonGameEngine.validate(getGameRequest(), settings, gamePlay);
//
//        GamePlayState gamePlayState = response.getGamePlay().getGamePlayState();
//        gamePlayState.setData(new HashMap<>());
//
//        HashMap<String, Double> map = new HashMap<>();
//        map.put("neon_0_02", 102.6);
//        gamePlayState.getData().put(PLAYER_BAG,map);
//
//        response = neonGameEngine.play(getGameRequest(), response.getGamePlay());
//        response = neonGameEngine.processWinnings(getGameRequest(), response.getGamePlay(), response.getGameActivity());
//        SlotsSpinGameActivity gameActivity = (SlotsSpinGameActivity) response.getGameActivity();
//
//        NeonBonusContext bonusContext = (NeonBonusContext) gameActivity.getBonusContext();
//
//        assertEquals(GameStatus.INPROGRESS, gamePlayState.getGameStatus());
//        assertEquals(1D, bonusContext.getBonusMultiplier().doubleValue());
//        assertEquals(NeonGameBonus.FREE_SPINS, gameActivity.getBonusContext().getBonusAwarded());
//        assertEquals(15, ((NeonBonusContext) gameActivity.getBonusContext()).getFreeSpinsContext().getFreeSpinsAwarded());
//        assertTrue( bonusContext.getPreviousMeter() != 0);
//        //assertEquals(109.58,bonusContext.getMeter());
//        List<GameEngineResponse> activities = new ArrayList<>();
//        activities.add(response);
//        while (response.getGamePlay().getStatus()!=GameStatus.COMPLETED){
//            response = neonGameEngine.play(getGameRequest(), response.getGamePlay());
//            response = neonGameEngine.processWinnings(getGameRequest(), response.getGamePlay(), response.getGameActivity());
//            activities.add(response);
//        }
//        gameActivity = (SlotsSpinGameActivity) response.getGameActivity();
//        assertEquals(GameStatus.COMPLETED, gamePlayState.getGameStatus());
//        assertNull(gameActivity.getBonusAwarded());
//    }
//
//    @RepeatedTest(1)
//    void test_free_spins_20() throws GameEngineException {
//        ForceReelLayoutSelector.set(1);
//        ForceReelSpinner.set(List.of(18,11,5,15,0,11,15));
//
//        GamePlay gamePlay = neonGameEngine.startGame(GAME_CONFIGURATION);
//
//        StakeSettings settings = getStakeSettings();
//
//        SlotsGameEngineResponse response = neonGameEngine.validate(getGameRequest(), settings, gamePlay);
//
//        GamePlayState gamePlayState = response.getGamePlay().getGamePlayState();
//        gamePlayState.setData(new HashMap<>());
//
//        HashMap<String, Double> map = new HashMap<>();
//        map.put("neon_0_02", 102.6);
//        gamePlayState.getData().put(PLAYER_BAG,map);
//
//        response = neonGameEngine.play(getGameRequest(), response.getGamePlay());
//        response = neonGameEngine.processWinnings(getGameRequest(), response.getGamePlay(), response.getGameActivity());
//        SlotsSpinGameActivity gameActivity = (SlotsSpinGameActivity) response.getGameActivity();
//
//        NeonBonusContext bonusContext = (NeonBonusContext) gameActivity.getBonusContext();
//
//        assertEquals(GameStatus.INPROGRESS, gamePlayState.getGameStatus());
//        assertEquals(1D, bonusContext.getBonusMultiplier().doubleValue());
//        assertEquals(NeonGameBonus.FREE_SPINS, gameActivity.getBonusContext().getBonusAwarded());
//        assertEquals(20, ((NeonBonusContext) gameActivity.getBonusContext()).getFreeSpinsContext().getFreeSpinsAwarded());
//        assertTrue( bonusContext.getPreviousMeter() != 0);
//        //assertEquals(109.58,bonusContext.getMeter());
//        List<GameEngineResponse> activities = new ArrayList<>();
//        activities.add(response);
//        while (response.getGamePlay().getStatus()!=GameStatus.COMPLETED){
//            response = neonGameEngine.play(getGameRequest(), response.getGamePlay());
//            response = neonGameEngine.processWinnings(getGameRequest(), response.getGamePlay(), response.getGameActivity());
//            activities.add(response);
//        }
//        gameActivity = (SlotsSpinGameActivity) response.getGameActivity();
//        assertEquals(GameStatus.COMPLETED, gamePlayState.getGameStatus());
//        assertNull(gameActivity.getBonusAwarded());
//    }*/
//
//    private String getExternalStateKey(double stakePerLine, GamePlay slotsGamePlay) {
//        return ("meter_accumulated_" + stakePerLine).replace(".","_");
//    }
//
//    private StakeSettings getStakeSettings() {
//        StakeSettings settings = new StakeSettings();
//        settings.setDefaultStake(0.02);
//        settings.setMinMax(new double[]{0.2, 1000});
//        settings.setMinMaxLines(new Integer[]{10,10});
//
//        return settings;
//    }
//
//    NeonGameEngineRequest getGameRequest() {
//        NeonGameEngineRequest request = new NeonGameEngineRequest();
//        request.setNoOfLines(10);
//        request.setStakePerLine(BigDecimal.valueOf(1));
//
//        return request;
//    }
//
//    private String getReelSet(List<SlotReel<NeonSymbol>> reelSet) {
//        StringBuilder builder = new StringBuilder();
//
//        for (int row = 0; row < reelSet.get(0).getSymbols().size(); row++) {
//            for (int reel = 0; reel < reelSet.size(); reel++) {
//                if(row < reelSet.get(reel).getSymbols().size())
//                    builder.append(reelSet.get(reel).getSymbols().get(row));
//            }
//        }
//        return builder.toString();
//    }
//
//    private ReelLayout<NeonSymbol, NeonGameBonus> getSlotReels(int reelset) {
//        NeonGameConfiguration gc = neonGameEngine.getConfigurationMap().get(GAME_CONFIGURATION);
//        return gc.getReelLayoutConfiguration().getReelLayouts().get(reelset);
//    }
//
//    String reelset1 = """
//                            AA	FF	BB	FF	AA	FF	FF
//                            AA	CC	BB	CC	AA	CC	CC
//                            AA	GG	BB	GG	AA	GG	GG
//                            EE	GG	EE	GG	EE	GG	GG
//                            EE	CC	EE	CC	EE	CC	CC
//                            EE	CC	AA	CC	BB	CC	CC
//                            BB	CC	AA	CC	BB	CC	CC
//                            BB	FF	AA	EE	BB	FF	EE
//                            BB	FF	FF	EE	FF	FF	EE
//                            FF	FF	CC	EE	CC	FF	EE
//                            CC	DD	GG	DD	GG	DD	DD
//                            GG	AA	GG	AA	GG	AA	AA
//                            GG	AA	CC	AA	CC	AA	AA
//                            GG	AA	CC	AA	CC	AA	AA
//                            CC	AA	CC	AA	CC	AA	AA
//                            CC	EE	FF	FF	FF	EE	FF
//                            CC	EE	DD	FF	DD	EE	FF
//                            FF	EE	DD	FF	DD	EE	FF
//                            DD	BB	HH	BB	HH	BB	BB
//                            DD	BB	HH	BB	EE	BB	BB
//                            HH	BB	DD	BB	DD	BB	BB
//                            FF	HH	HH	HH	HH	HH	HH
//                            FF	BB	CC	BB	CC	BB	BB
//                            FF	BB	CC	BB	CC	BB	BB
//                            DD	HH	CC	HH	CC	HH	HH
//                            HH	AA	FF	AA	FF	AA	AA
//                            CC	EE	DD	EE	DD	EE	EE
//                            CC	EE	DD	EE	DD	EE	EE
//                            CC	BB	HH	BB	HH	BB	BB
//                            FF	BB	DD	BB	DD	BB	BB
//                            DD	BB	HH	BB	HH	BB	BB
//                            DD	HH		HH		HH	HH
//                            HH	BB		BB		BB	BB
//                            DD	HH		HH		HH	HH
//                            HH	DD		DD		DD	DD
//                            HH	DD		DD		DD	DD
//                            HH	DD		DD		DD	DD
//            """.replaceAll("[\\n\\t ]", "");
//
//
//    String reelset2 = """
//            AA	FF	BB	FF	AA	FF	FF
//            AA	CC	BB	CC	AA	CC	CC
//            AA	GG	BB	GG	AA	GG	GG
//            EE	GG	EE	GG	EE	GG	GG
//            EE	CC	EE	CC	EE	CC	CC
//            EE	CC	AA	CC	BB	CC	CC
//            BB	CC	AA	CC	BB	CC	CC
//            BB	FF	AA	EE	BB	FF	EE
//            BB	FF	FF	EE	FF	FF	EE
//            FF	FF	CC	EE	CC	FF	EE
//            CC	DD	GG	DD	GG	DD	DD
//            GG	WC	GG	AA	GG	WC	AA
//            GG	AA	CC	AA	CC	AA	AA
//            GG	AA	CC	AA	CC	AA	AA
//            CC	AA	CC	AA	CC	AA	AA
//            CC	AA	FF	WC	FF	AA	WC
//            CC	EE	DD	FF	DD	EE	FF
//            FF	EE	DD	FF	DD	EE	FF
//            WC	EE	HH	FF	HH	EE	FF
//            DD	BB	HH	BB	EE	BB	BB
//            DD	BB	DD	BB	DD	BB	BB
//            HH	BB	HH	BB	HH	BB	BB
//            FF	HH	CC	HH	CC	HH	HH
//            FF	BB	CC	BB	CC	BB	BB
//            FF	BB	CC	BB	CC	BB	BB
//            DD	HH	FF	HH	FF	HH	HH
//            HH	AA	DD	AA	DD	AA	AA
//            CC	EE	DD	EE	DD	EE	EE
//            CC	EE	HH	EE	HH	EE	EE
//            CC	BB	DD	BB	DD	BB	BB
//            FF	BB	HH	BB	HH	BB	BB
//            DD	BB		BB		BB	BB
//            DD	HH		HH		HH	HH
//            HH	BB		BB		BB	BB
//            DD	HH		HH		HH	HH
//            HH	DD		DD		DD	DD
//            HH	DD		DD		DD	DD
//            HH	DD		DD		DD	DD
//            """.replaceAll("[\\n\\t ]", "");
//
//
//    String reelset3 = """
//            AA	FF	BB	FF	AA	FF	FF
//            AA	CC	BB	CC	AA	CC	CC
//            AA	GG	BB	GG	AA	GG	GG
//            EE	GG	EE	GG	EE	GG	GG
//            EE	CC	EE	CC	EE	CC	CC
//            EE	CC	AA	CC	BB	CC	CC
//            BB	CC	AA	CC	BB	CC	CC
//            BB	FF	AA	EE	BB	FF	EE
//            BB	FF	FF	EE	FF	FF	EE
//            FF	FF	CC	EE	CC	FF	EE
//            CC	DD	GG	DD	GG	DD	DD
//            GG	AA	GG	AA	GG	AA	AA
//            GG	AA	CC	AA	CC	AA	AA
//            GG	AA	CC	AA	CC	AA	AA
//            CC	AA	CC	AA	CC	AA	AA
//            CC	EE	FF	FF	FF	EE	FF
//            CC	EE	WC	FF	DD	EE	FF
//            FF	EE	DD	FF	DD	EE	FF
//            WC	BB	DD	BB	WC	BB	BB
//            DD	BB	HH	BB	HH	BB	BB
//            DD	BB	HH	BB	EE	BB	BB
//            HH	HH	DD	HH	DD	HH	HH
//            FF	BB	HH	BB	HH	BB	BB
//            FF	BB	CC	BB	CC	BB	BB
//            FF	HH	CC	HH	CC	HH	HH
//            DD	AA	CC	AA	CC	AA	AA
//            HH	EE	FF	EE	FF	EE	EE
//            CC	EE	DD	EE	DD	EE	EE
//            CC	BB	DD	BB	DD	BB	BB
//            CC	BB	HH	BB	HH	BB	BB
//            FF	BB	DD	BB	DD	BB	BB
//            DD	HH	HH	HH	HH	HH	HH
//            DD	BB		BB		BB	BB
//            HH	HH		HH		HH	HH
//            DD	DD		DD		DD	DD
//            HH	DD		DD		DD	DD
//            HH	DD		DD		DD	DD
//            HH
//            """.replaceAll("[\\n\\t ]", "");
//
//
//    String reelset4 = """
//            AA	FF	BB	FF	AA	FF	FF
//            AA	CC	BB	CC	AA	CC	CC
//            AA	GG	BB	GG	AA	GG	GG
//            EE	GG	EE	GG	EE	GG	GG
//            EE	CC	EE	CC	EE	CC	CC
//            EE	CC	AA	CC	BB	CC	CC
//            BB	CC	AA	CC	BB	CC	CC
//            BB	FF	AA	EE	BB	FF	EE
//            BB	FF	FF	EE	FF	FF	EE
//            FF	FF	CC	EE	CC	FF	EE
//            CC	DD	GG	DD	GG	DD	DD
//            GG	AA	GG	AA	GG	AA	AA
//            GG	AA	CC	AA	CC	AA	AA
//            GG	AA	CC	AA	CC	AA	AA
//            CC	AA	CC	AA	CC	AA	AA
//            CC	EE	FF	FF	FF	EE	FF
//            CC	EE	WC	FF	DD	EE	FF
//            FF	EE	DD	FF	DD	EE	FF
//            DD	BB	DD	BB	HH	BB	BB
//            DD	BB	HH	BB	EE	BB	BB
//            HH	BB	HH	BB	DD	BB	BB
//            FF	HH	DD	HH	HH	HH	HH
//            FF	BB	HH	BB	CC	BB	BB
//            FF	BB	CC	BB	CC	BB	BB
//            DD	HH	CC	HH	CC	HH	HH
//            HH	AA	CC	AA	FF	AA	AA
//            CC	EE	FF	EE	DD	EE	EE
//            CC	EE	DD	EE	DD	EE	EE
//            CC	BB	DD	BB	HH	BB	BB
//            FF	BB	HH	BB	DD	BB	BB
//            DD	BB	DD	BB	HH	BB	BB
//            DD	HH	HH	HH		HH	HH
//            HH	BB		BB		BB	BB
//            DD	HH		HH		HH	HH
//            HH	DD		DD		DD	DD
//            HH	DD		DD		DD	DD
//            HH	DD		DD		DD	DD
//            """.replaceAll("[\\n\\t ]", "");
//
//
//    String reelset5 = """
//
//            AA	FF	BB	FF	AA	FF	FF
//            AA	CC	BB	CC	AA	CC	CC
//            AA	GG	BB	GG	AA	GG	GG
//            EE	GG	EE	GG	EE	GG	GG
//            EE	CC	EE	CC	EE	CC	CC
//            EE	CC	AA	CC	BB	CC	CC
//            BB	CC	AA	CC	BB	CC	CC
//            BB	FF	AA	EE	BB	FF	EE
//            BB	FF	FF	EE	FF	FF	EE
//            FF	FF	CC	EE	CC	FF	EE
//            CC	DD	GG	DD	GG	DD	DD
//            GG	AA	GG	AA	GG	AA	AA
//            GG	AA	CC	AA	CC	AA	AA
//            GG	AA	CC	AA	CC	AA	AA
//            CC	AA	CC	AA	CC	AA	AA
//            CC	EE	FF	FF	FF	EE	FF
//            CC	EE	DD	FF	DD	EE	FF
//            FF	EE	DD	FF	DD	EE	FF
//            WC	BB	HH	BB	HH	BB	BB
//            DD	BB	HH	BB	EE	BB	BB
//            DD	BB	DD	BB	DD	BB	BB
//            HH	HH	HH	HH	HH	HH	HH
//            FF	BB	CC	BB	CC	BB	BB
//            FF	BB	CC	BB	CC	BB	BB
//            FF	HH	CC	HH	CC	HH	HH
//            DD	AA	FF	AA	FF	AA	AA
//            HH	EE	DD	EE	DD	EE	EE
//            CC	EE	DD	EE	DD	EE	EE
//            CC	BB	HH	BB	HH	BB	BB
//            CC	BB	DD	BB	DD	BB	BB
//            FF	BB	HH	BB	HH	BB	BB
//            DD	HH		HH		HH	HH
//            DD	BB		BB		BB	BB
//            HH	HH		HH		HH	HH
//            DD	DD		DD		DD	DD
//            HH	DD		DD		DD	DD
//            HH	DD		DD		DD	DD
//            HH
//            """.replaceAll("[\\n\\t ]", "");
//
//    String freeSpinsReel = """
//
//            AA	FF	BB	FF	AA	FF	FF
//            AA	CC	BB	CC	AA	CC	CC
//            AA	GG	BB	GG	AA	GG	GG
//            EE	GG	EE	GG	EE	GG	GG
//            EE	CC	EE	CC	EE	CC	CC
//            EE	CC	AA	CC	BB	CC	CC
//            BB	CC	AA	CC	BB	CC	CC
//            BB	FF	AA	EE	BB	FF	EE
//            BB	FF	FF	EE	FF	FF	EE
//            FF	FF	CC	EE	CC	FF	EE
//            CC	DD	GG	DD	GG	DD	DD
//            GG	AA	GG	AA	GG	AA	AA
//            GG	AA	CC	AA	CC	AA	AA
//            GG	AA	CC	AA	CC	AA	AA
//            CC	AA	CC	AA	CC	AA	AA
//            CC	EE	FF	FF	FF	EE	FF
//            CC	EE	DD	FF	DD	EE	FF
//            FF	EE	DD	FF	DD	EE	FF
//            WC	BB	HH	BB	HH	BB	BB
//            DD	BB	HH	BB	EE	BB	BB
//            DD	BB	DD	BB	DD	BB	BB
//            HH	HH	HH	HH	HH	HH	HH
//            FF	BB	CC	BB	CC	BB	BB
//            FF	BB	CC	BB	CC	BB	BB
//            FF	HH	CC	HH	CC	HH	HH
//            DD	AA	FF	AA	FF	AA	AA
//            HH	EE	DD	EE	DD	EE	EE
//            CC	EE	DD	EE	DD	EE	EE
//            CC	BB	HH	BB	HH	BB	BB
//            CC	BB	DD	BB	DD	BB	BB
//            FF	BB	HH	BB	HH	BB	BB
//            DD	HH		HH		HH	HH
//            DD	BB		BB		BB	BB
//            HH	HH		HH		HH	HH
//            DD	DD		DD		DD	DD
//            HH	DD		DD		DD	DD
//            HH	DD		DD		DD	DD
//            HH
//            """.replaceAll("[\\n\\t ]", "");
//}