package com.algomics.torpedo.gaming.engine.moonstar.service;

import com.algomics.torpedo.gaming.engine.api.bonus.GameBonus;
import com.algomics.torpedo.gaming.engine.moonstar.model.BursterOption;
import com.algomics.torpedo.gaming.engine.moonstar.model.dto.NeonGameEngineRequest;
import com.algomics.torpedo.gaming.engine.slots.config.SlotsFreeGameConfiguration;
import com.algomics.torpedo.gaming.engine.slots.handlers.RandomBuilder;
import com.algomics.torpedo.gaming.engine.slots.utils.PickByWeightage;
import com.algomics.torpedo.rad.classloaders.jar.JarClassLoader;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.monitorjbl.json.JsonView;
import com.monitorjbl.json.JsonViewModule;
import com.algomics.torpedo.gaming.engine.api.bonus.BonusContext;
import com.algomics.torpedo.gaming.engine.api.exception.GameEngineException;
import com.algomics.torpedo.gaming.engine.api.model.GameActivity;
import com.algomics.torpedo.gaming.engine.api.model.GamePlay;
import com.algomics.torpedo.gaming.engine.api.model.GameType;
import com.algomics.torpedo.gaming.engine.api.model.StakeSettings;
import com.algomics.torpedo.gaming.engine.api.model.*;
import com.algomics.torpedo.gaming.engine.api.module.GameEngineModule;
import com.algomics.torpedo.gaming.engine.api.service.GameEngineService;
import com.algomics.torpedo.gaming.engine.moonstar.bonus.NeonBonusContext;
import com.algomics.torpedo.gaming.engine.moonstar.bonus.NeonGameBonus;
import com.algomics.torpedo.gaming.engine.moonstar.config.NeonGameConfiguration;
import com.algomics.torpedo.gaming.engine.moonstar.jackson.NeonModule;
import com.algomics.torpedo.gaming.engine.moonstar.symbol.NeonSymbol;
import com.algomics.torpedo.gaming.engine.slots.api.dto.SlotsGameEngineResponse;
import com.algomics.torpedo.gaming.engine.slots.api.model.SlotsGamePlayState;
import com.algomics.torpedo.gaming.engine.slots.bonus.FreeSpinsContext;
import com.algomics.torpedo.gaming.engine.slots.bonus.handler.*;
import com.algomics.torpedo.gaming.engine.slots.handlers.DefaultClusterPaysHandler;
import com.algomics.torpedo.gaming.engine.slots.handlers.DefaultPayStepWinningsHandler;
import com.algomics.torpedo.gaming.engine.slots.model.*;
import com.algomics.torpedo.gaming.engine.slots.pays.CascadePayStep;
import com.algomics.torpedo.gaming.engine.slots.reels.DefaultSlotReelLayoutSelector;
import com.algomics.torpedo.gaming.engine.slots.reels.DefaultSlotReelSpinner;
import com.algomics.torpedo.gaming.engine.slots.reels.ReelLayout;
import com.algomics.torpedo.gaming.engine.slots.reels.SymbolGrid;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import javax.annotation.PostConstruct;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.stream.Collectors;
import static com.algomics.torpedo.gaming.engine.api.model.GamePlay.*;
import static com.monitorjbl.json.Match.match;


@Service
@Data
@Slf4j
@NoArgsConstructor
public class NeonGameEngine
        implements GameEngineService<NeonGameEngineRequest, SlotsGameEngineResponse> {

    private final Map<String, NeonGameConfiguration> configurationMap = new HashMap<>();
    private final Map<String, String> gameConfigurationJsonMap = new HashMap<>();

    @Autowired
    private ResourceLoader resourceLoader;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private DefaultSlotReelSpinner reelSpinner;
    @Autowired
    private CashOrFreeSpinsBonusOptionsHandler<NeonSymbol, NeonGameBonus> cashOrFreeSpinsBonusOptionsHandler;
    @Autowired
    private FreeSpinBonusHandler<NeonSymbol, NeonGameBonus> freeSpinBonusHandler;
    @Autowired
    private ScatterBonusHandler<NeonSymbol> scatterBonusHandler;
    @Autowired
    private ScatterPayHandler scatterPayHandler;
    @Autowired
    private DefaultPayStepWinningsHandler payStepWinningsHandler;
    @Autowired
    private DefaultClusterPaysHandler<NeonSymbol> clusterPaysHandler;
    @Autowired
    private DefaultSlotReelLayoutSelector layoutSelector;
    @Autowired
    private PickByWeightage pickByWeightage;
    @Autowired
    RandomBuilder randomGenerator;

    @Value("${rgs.games.player-bag.name:playerBag}")
    private String PLAYER_BAG;

    @PostConstruct
    void init() throws IOException {
        this.objectMapper = objectMapper.copy().registerModule(new JsonViewModule())
                .registerModule(new NeonModule());
        NeonGameConfiguration gc;
        for (String gameConfiguration : supportedGameConfigurations()) {

            InputStreamReader is = null;
            if (this.getClass().getClassLoader() instanceof JarClassLoader) {
                byte[] bytes = ((JarClassLoader) this.getClass().getClassLoader()).getLocalResourceAsBytes(gameConfiguration + ".json");

                is = new InputStreamReader(new ByteArrayInputStream(bytes));

            } else {
                is = new InputStreamReader(resourceLoader.getResource("classpath:" + gameConfiguration + ".json").getInputStream());
            }
            try (BufferedReader reader = new BufferedReader(
                    is)) {
                String json = reader.lines()
                        .collect(Collectors.joining("\n"));
                gc = objectMapper
                        .readValue(json,
                                NeonGameConfiguration.class);
                configurationMap.put(gameConfiguration, gc);
            }
        }
    }

    @Override
    public List<String> supportedGameConfigurations() {
        return Collections.singletonList("neon");
    }

    @Override
    public SlotsGameEngineResponse validate(NeonGameEngineRequest gameEngineRequest,
                                            StakeSettings stakeSettings,
                                            GamePlay slotsGamePlay) throws GameEngineException {
        SlotsGameEngineResponse response = new SlotsGameEngineResponse();
        NeonGameConfiguration gc = this.configurationMap.get(slotsGamePlay.getGameConfiguration());
        int playingLines = gameEngineRequest.getNoOfLines();
        if (gameEngineRequest.getNoOfLines() == null ||
            gameEngineRequest.getNoOfLines() == 0 ||
            stakeSettings == null ||
            stakeSettings.getMinMaxLines() == null) {
            throw new GameEngineException("Invalid playLine request.");
        }

        int minPayLines = stakeSettings.getMinMaxLines()[0];
        int maxPayLines = stakeSettings.getMinMaxLines()[1];
        if (playingLines < minPayLines ||
            playingLines > maxPayLines) {
            throw new GameEngineException("Invalid playLine request.");
        }


        if (gameEngineRequest.getStakePerLine() == null
            || gameEngineRequest.getStakePerLine().compareTo(BigDecimal.ZERO) <= 0)
            throw new GameEngineException("Invalid Stake.");

        SlotsGamePlayState slotsGamePlayState = (SlotsGamePlayState) slotsGamePlay.getGamePlayState();
        slotsGamePlayState.setNoOfLines(gameEngineRequest.getNoOfLines());
        slotsGamePlayState.setStakePerLine(gameEngineRequest.getStakePerLine());

        BigDecimal totalStake = getTotalStake(gameEngineRequest.getStakePerLine(), gameEngineRequest.getNoOfLines());
        boolean buyFeature = gameEngineRequest.isBuyFeature()
                            && gameEngineRequest.getId() > 0
                            && gameEngineRequest.getId() <= gc.getRtpInfo().getBuyFeature().size()
                            && slotsGamePlayState.getLastBonusAwarded() == NeonGameBonus.NONE;
        BigDecimal buyStake = null;
        if(buyFeature){
            buyStake = new BigDecimal(gc.getBuyBurstersWithCredits()[gameEngineRequest.getId() - 1].split("-")[1]);
            buyStake = scaledValue(buyStake.multiply(gameEngineRequest.getStakePerLine()));
        }
        slotsGamePlayState.setTotalStake(buyStake!=null?buyStake:totalStake);

        String bursterMeterKey =  getExternalStateKey(slotsGamePlayState.getStakePerLine().doubleValue(), slotsGamePlay);

        if(slotsGamePlayState.getData()==null)
            slotsGamePlayState.setData(new HashMap<>());

        Map<String, Object> meter = (Map<String, Object>) slotsGamePlayState.getData().get(PLAYER_BAG);
        if(meter==null){
            HashMap<String, Double> map = new HashMap<>();
            map.put(bursterMeterKey, 0D);
            slotsGamePlayState.getData().put(PLAYER_BAG, map);
        }
        else meter.putIfAbsent(bursterMeterKey, 0D);

        slotsGamePlay.setGamePlayState(slotsGamePlayState);

        response.setTotalBet(slotsGamePlayState.getTotalStake());
        response.setGamePlay(slotsGamePlay);

        return response;
    }

    @Override
    public GamePlay startGame(String gameConfiguration) {
        GamePlay gamePlay = new GamePlay(GameType.SLOTS);
        gamePlay.setUid(UUID.randomUUID().toString());
        SlotsGamePlayState slotsGamePlay = new SlotsGamePlayState(GameStatus.INPROGRESS);
        gamePlay.setGameConfiguration(gameConfiguration);
        slotsGamePlay.getBonusAwarded().add(NeonGameBonus.NONE);
        gamePlay.setGamePlayState(slotsGamePlay);
        return gamePlay;
    }

    public  List<Integer> prepareTopReelIndex(Map<String, Integer> randomNumbersWithKey, ReelLayout<NeonSymbol, NeonGameBonus> reelLayout) {
        List<Integer> topReelIndex = new ArrayList<>();
        switch (reelLayout.getIndex()){
            case 0:
                topReelIndex.add(randomNumbersWithKey.get("reel0_0"));
                topReelIndex.add(randomNumbersWithKey.get("reel1_0"));
                topReelIndex.add(randomNumbersWithKey.get("reel2_0"));
                topReelIndex.add(randomNumbersWithKey.get("reel3_0"));
                topReelIndex.add(randomNumbersWithKey.get("reel4_0"));
                topReelIndex.add(randomNumbersWithKey.get("reel5_0"));
                topReelIndex.add(randomNumbersWithKey.get("reel6_0"));
                break;
            case 1:
                topReelIndex.add(randomNumbersWithKey.get("reel0_1"));
                topReelIndex.add(randomNumbersWithKey.get("reel1_1"));
                topReelIndex.add(randomNumbersWithKey.get("reel2_1"));
                topReelIndex.add(randomNumbersWithKey.get("reel3_1"));
                topReelIndex.add(randomNumbersWithKey.get("reel4_1"));
                topReelIndex.add(randomNumbersWithKey.get("reel5_1"));
                topReelIndex.add(randomNumbersWithKey.get("reel6_1"));
                break;
            case 2:
                topReelIndex.add(randomNumbersWithKey.get("reel0_2"));
                topReelIndex.add(randomNumbersWithKey.get("reel1_2"));
                topReelIndex.add(randomNumbersWithKey.get("reel2_2"));
                topReelIndex.add(randomNumbersWithKey.get("reel3_2"));
                topReelIndex.add(randomNumbersWithKey.get("reel4_2"));
                topReelIndex.add(randomNumbersWithKey.get("reel5_2"));
                topReelIndex.add(randomNumbersWithKey.get("reel6_2"));
                break;
            case 3:
                topReelIndex.add(randomNumbersWithKey.get("reel0_3"));
                topReelIndex.add(randomNumbersWithKey.get("reel1_3"));
                topReelIndex.add(randomNumbersWithKey.get("reel2_3"));
                topReelIndex.add(randomNumbersWithKey.get("reel3_3"));
                topReelIndex.add(randomNumbersWithKey.get("reel4_3"));
                topReelIndex.add(randomNumbersWithKey.get("reel5_3"));
                topReelIndex.add(randomNumbersWithKey.get("reel6_3"));
                break;
            case 4:
                topReelIndex.add(randomNumbersWithKey.get("reel0_4"));
                topReelIndex.add(randomNumbersWithKey.get("reel1_4"));
                topReelIndex.add(randomNumbersWithKey.get("reel2_4"));
                topReelIndex.add(randomNumbersWithKey.get("reel3_4"));
                topReelIndex.add(randomNumbersWithKey.get("reel4_4"));
                topReelIndex.add(randomNumbersWithKey.get("reel5_4"));
                topReelIndex.add(randomNumbersWithKey.get("reel6_4"));
                break;
        }
        return topReelIndex;
    }

    public  List<Integer> prepareTopReelIndex(Map<String, Integer> randomNumbersWithKey) {
        List<Integer> topReelIndex = new ArrayList<>();
        topReelIndex.add(randomNumbersWithKey.get("reel0"));
        topReelIndex.add(randomNumbersWithKey.get("reel1"));
        topReelIndex.add(randomNumbersWithKey.get("reel2"));
        topReelIndex.add(randomNumbersWithKey.get("reel3"));
        topReelIndex.add(randomNumbersWithKey.get("reel4"));
        topReelIndex.add(randomNumbersWithKey.get("reel5"));
        topReelIndex.add(randomNumbersWithKey.get("reel6"));
        return topReelIndex;
    }

    @Override
    public SlotsGameEngineResponse play(NeonGameEngineRequest slotsGameEngineRequest, GamePlay gamePlay)
            throws GameEngineException {
        SlotsGameEngineResponse response = new SlotsGameEngineResponse();

        SlotsGamePlayState slotsGamePlayState = (SlotsGamePlayState) gamePlay.getGamePlayState();
        if (slotsGamePlayState.getLastBonusAwarded() == null
                || slotsGamePlayState.getGameStatus() == GameStatus.COMPLETED)
            throw new GameEngineException("Invalid Game State");

        NeonGameConfiguration gc = configurationMap.get(gamePlay.getGameConfiguration());
        boolean buyFeature = slotsGameEngineRequest.isBuyFeature()
                            && slotsGameEngineRequest.getId() > 0
                            && slotsGameEngineRequest.getId() <= gc.getRtpInfo().getBuyFeature().size()
                             && slotsGamePlayState.getLastBonusAwarded() == NeonGameBonus.NONE;

        final NeonGameBonus gameBonus   = (NeonGameBonus) slotsGamePlayState.getLastBonusAwarded();

        String bursterMeterKey =  getExternalStateKey(slotsGamePlayState.getStakePerLine().doubleValue(), gamePlay);

        //random data generation
        Map<String, Integer> randomDataMap = randomGenerator.fetchRandomData(gc, gameBonus);

        ReelLayout<NeonSymbol, NeonGameBonus> reelLayout = layoutSelector.selectReelLayout((NeonGameBonus) slotsGamePlayState.getLastBonusAwarded(),
                gc.getReelLayoutConfiguration(), randomDataMap.get("reelLayout"));

        List<Integer> topReelIndex;
        if(gameBonus.equals(NeonGameBonus.NONE)){
            topReelIndex = this.prepareTopReelIndex(randomDataMap,reelLayout);
        }
        else{
            topReelIndex = this.prepareTopReelIndex(randomDataMap);
        }

        NeonBonusContext previousContext = (NeonBonusContext) slotsGamePlayState.getBonusContext();
        SlotsSpinGameActivity
                slotSpinActivity = new SlotsSpinGameActivity(
                gc.getColumns(),
                gc.getRows(),
                topReelIndex,
                slotsGamePlayState.getGameStatus(),
                reelLayout.getIndex(),
                BonusContext
                        .copyFromPreviousOrCreateNew(previousContext,
                                NeonBonusContext::new,
                                NeonBonusContext::new));
        SymbolGrid symbolGrid = slotSpinActivity.prepareSymbolGrid(gc.getReelLayoutConfiguration());



        Map<String, Object> bursterMap = (Map<String, Object>) slotsGamePlayState.getData()
                .get(PLAYER_BAG);

        GameBonus lastBonusAwarded = slotsGamePlayState.getLastBonusAwarded();
        NeonBonusContext bonusContext = (NeonBonusContext) slotSpinActivity.getBonusContext();


        //increase burster meter by 5 on every spin.
        BigDecimal meter = BigDecimal.ZERO;
        if (bursterMap.get(bursterMeterKey) != null && bursterMap.get(bursterMeterKey) instanceof Number) {
            meter = BigDecimal.valueOf(((Number) bursterMap.get(bursterMeterKey)).doubleValue());
        }
        bonusContext.setPreviousMeter(meter.doubleValue());
        if(!buyFeature) {
            if (lastBonusAwarded == NeonGameBonus.NONE) {
                BigDecimal increaseMeterOnEveryWager =
                        scaledValue(BigDecimal.valueOf(slotsGamePlayState.getNoOfLines()).
                                multiply(BigDecimal.valueOf(gc.getBonusGameConfiguration().getWildBursterMeter().getWager())));
                meter = scaledValue(meter.add(increaseMeterOnEveryWager));
            }
        }

       consumeBonus((NeonBonusContext) slotSpinActivity.getBonusContext());

        meter = computeClustersAndCascade(slotsGamePlayState, gc, slotSpinActivity,
                symbolGrid,
                lastBonusAwarded, bonusContext, meter, buyFeature);

        BursterOption bursterOption = null;
        if(buyFeature){
            bursterOption = gc.getBonusGameConfiguration()
                    .getWildBursterMeter().getBonusOptions().get(slotsGameEngineRequest.getId() - 1);
            slotSpinActivity.setBonusAwarded(NeonGameBonus.valueOf("BURSTER_" + slotsGameEngineRequest.getId() + "_PURCHASED"));
        }
        else if(meter.doubleValue() >= gc.getBonusGameConfiguration().getWildBursterMeter().getMeterThreshold()){
            slotSpinActivity.setBonusAwarded(NeonGameBonus.WILD_BURSTER);
            bursterOption = pickByWeightage
                    .pick(gc.getBonusGameConfiguration().getWildBursterMeter().getBonusOptions(),randomDataMap.get("bonusPicker"));
        }

        if(bursterOption!=null){
            symbolGrid = new SymbolGrid(slotSpinActivity.getLastPayStep().getStepSymbolGrid());

            int cols =  gc.getRows();
            int position,reel,col;
            for (int i = 0; i < bursterOption.getPositions().length; i++) {
                position = bursterOption.getPositions()[i];
                reel = position / cols;
                col = position % cols;
                if(symbolGrid.getSymbolGrid().get(reel).get(col)!=NeonSymbol.WC)
                    symbolGrid.getSymbolGrid().get(reel).set(col, NeonSymbol.WB);
            }

            bonusContext.setBursterPositions(bursterOption.getPositions());
            bonusContext.setBursterOption(bursterOption.getIndex());
            meter = computeClustersAndCascade(slotsGamePlayState, gc, slotSpinActivity,
                    symbolGrid,
                    lastBonusAwarded, bonusContext, meter, buyFeature);
        }

        if(bursterOption!=null && !buyFeature)
            bonusContext.setPreviousMeter(0);

        if(slotSpinActivity.getPaySteps().size() > 1){
            if(bonusContext.getFreeSpinsContext()!=null
               && bonusContext.getBonusMultiplier().doubleValue() < 10){
                //increase bonus multiplier in FREE_SPINS
                bonusContext.setBonusMultiplier(bonusContext.getBonusMultiplier().add(BigDecimal.ONE));
            }
        }

        bonusContext.setMeter(meter.doubleValue());
        int maxMeter = gc.getBonusGameConfiguration().getWildBursterMeter().getMeterThreshold();
        bonusContext.setMeterPercentage(meter.divide(BigDecimal.valueOf(maxMeter),2, RoundingMode.HALF_UP)
                .multiply(BigDecimal.valueOf(100)).setScale(0, RoundingMode.HALF_UP).intValue());


        Map<String, Object> playerBag = new HashMap<>();

        if(isWildBursterAwarded(slotSpinActivity))
            playerBag.put(bursterMeterKey, 0D);
        else
            playerBag.put(bursterMeterKey, meter);

        slotsGamePlayState.getData().put(PLAYER_BAG, playerBag);

        awardBonus(slotSpinActivity, gc);

        slotsGamePlayState.setBonusContext(slotSpinActivity.getBonusContext());
        gamePlay.setGamePlayState(slotsGamePlayState);

        // update game round status
        checkGameStatus(gamePlay);
        slotSpinActivity.setGameStatus(gamePlay.getGamePlayState().getGameStatus());
        gamePlay.setStatus(gamePlay.getGamePlayState().getGameStatus());
        //end

        response.setGameActivity(slotSpinActivity);
        response.setGamePlay(gamePlay);
        return response;
    }

    private boolean isWildBursterAwarded(SlotsSpinGameActivity slotSpinActivity) {
        return slotSpinActivity.getBonusAwarded()!=null && (slotSpinActivity.getBonusAwarded() == NeonGameBonus.WILD_BURSTER
        || slotSpinActivity.getBonusAwarded().getName().startsWith("BURSTER"));
    }

    private BigDecimal computeClustersAndCascade(SlotsGamePlayState slotsGamePlayState, NeonGameConfiguration gc,
                                                 SlotsSpinGameActivity slotSpinActivity,
                                                 SymbolGrid symbolGrid,
                                                 GameBonus lastBonusAwarded, NeonBonusContext bonusContext, BigDecimal meter, boolean skipMeter) {
        List<Cluster> clusters;
        boolean burster = isWildBursterAwarded(slotSpinActivity);

        do {
            clusters = clusterPaysHandler.findClusters(
                    symbolGrid,
                    Collections.emptyList(),
                    List.of(NeonSymbol.WC,NeonSymbol.WB),
                    Collections.emptyList(),//non replaceable symbols
                    gc.getSymbolStakeMultipliers(),
                    () -> {//bonus Multiplier
                        return bonusContext.getBonusMultiplier();
                        //return BigDecimal.ONE;
                    });


            CascadePayStep payStep = new CascadePayStep(symbolGrid, clusters, List.of(NeonSymbol.WC));
            slotSpinActivity.addPayStep(payStep);
            if(slotSpinActivity.getPaySteps()!=null
               && slotSpinActivity.getPaySteps().size()>1){
                if(burster) {
                    payStep.getData().putIfAbsent("burster", true);
                    burster = false;
                }else if(isWildBursterAwarded(slotSpinActivity)){
                    payStep.getData().putIfAbsent("burster_cascaded", true);
                }
                else
                    payStep.getData().putIfAbsent("cascaded", true);
            }

            if(!CollectionUtils.isEmpty(payStep.getReactingSymbolGrid())) {
                symbolGrid = slotSpinActivity.addCascadePayStep(SlotsPay.PayType.LINES, gc.getReelLayoutConfiguration());
            }

            //start increase meter on wins.
            BigDecimal clusterWins = clusters.stream()
                    .map(SlotsPay::winsMultiplier)
                    .reduce(BigDecimal::add).orElse(BigDecimal.ZERO);

            BigDecimal creditsWon = scaledValue(clusterWins.multiply(BigDecimal.valueOf(slotsGamePlayState.getNoOfLines())));

            if(!skipMeter && creditsWon.doubleValue() > 0){
                if(lastBonusAwarded == NeonGameBonus.NONE){
                    BigDecimal increaseMeterOnEveryWinINBaseGame = creditsWon.multiply(BigDecimal.valueOf(gc.getBonusGameConfiguration().getWildBursterMeter().getWinBG())).setScale(SlotsPay.SCALE, RoundingMode.HALF_UP).setScale(SlotsPay.SCALE, RoundingMode.HALF_UP);
                    meter = scaledValue(meter.add(increaseMeterOnEveryWinINBaseGame));
                }else if(bonusContext.getFreeSpinsContext()!=null){
                    BigDecimal increaseMeterOnEveryWinInFreeGame = creditsWon.multiply(BigDecimal.valueOf(gc.getBonusGameConfiguration().getWildBursterMeter().getWinFG())).setScale(SlotsPay.SCALE, RoundingMode.HALF_UP);
                    meter = scaledValue(meter.add(increaseMeterOnEveryWinInFreeGame));
                }
            }
            //end increase meter on wins.
        }
        while(!CollectionUtils.isEmpty(clusters));

        return meter;
    }

    @Override
    public SlotsGameEngineResponse processWinnings(NeonGameEngineRequest request,
                                                   GamePlay gamePlay, GameActivity gameActivity) {

        SlotsGameEngineResponse response = new SlotsGameEngineResponse();

        SlotsGamePlayState slotsGamePlayState = (SlotsGamePlayState) gamePlay.getGamePlayState();
        response.setTotalBet(slotsGamePlayState.getTotalStake());
        response.setGamePlay(gamePlay);

        SlotsSpinGameActivity spinGameActivity = (SlotsSpinGameActivity) gameActivity;
        BigDecimal spinWinnings = spinGameActivity.getPaySteps()
                .stream()
                .map(payStep -> payStepWinningsHandler
                        .payStepWinnings(getTotalStake(slotsGamePlayState.getStakePerLine(), slotsGamePlayState.getNoOfLines()),
                        slotsGamePlayState.getStakePerLine(),
                        1, payStep))
                .reduce(BigDecimal::add).orElse(BigDecimal.ZERO);

        spinGameActivity.setWinnings(spinWinnings);
        if(spinGameActivity.getTotalWinnings()!=null)
            spinGameActivity.setTotalWinnings(spinGameActivity.getTotalWinnings().add(spinWinnings));

        response.setSpinPayout(spinWinnings);
        slotsGamePlayState.setTotalWinnings(slotsGamePlayState.getTotalWinnings().add(spinWinnings));
        response.setTotalPayout(slotsGamePlayState.getTotalWinnings());
        response.setGameActivity(spinGameActivity);
        return response;
    }

    private void awardBonus(SlotsSpinGameActivity spinGameActivity, NeonGameConfiguration gc) {

        //scatter wins
        SlotsPay scatterPay = scatterPayHandler
                .awardBonus(spinGameActivity.getLastPayStep().getStepSymbolGrid(),
                        NeonSymbol.WC,
                        gc.getBonusGameConfiguration().getScatterStakeMultipliers());

        //award free games
        NeonBonusContext bonusContext = (NeonBonusContext) spinGameActivity.getBonusContext();
        //award free games
        if (scatterPay!=null && scatterPay.getSymbolCount() >= 3) {
            spinGameActivity.getLastPayStep().setScatterPay(scatterPay);

            if(gc.getBonusGameConfiguration().getFreeGameConfiguration().getAwardGameBonus()!=null) {
                SlotsFreeGameConfiguration<NeonGameBonus> fgc = gc.getBonusGameConfiguration()
                        .getFreeGameConfiguration();
                NeonGameBonus bonusAwarded =
                        freeSpinBonusHandler.awardBonus((SlotsBonusContext) spinGameActivity.getBonusContext(),
                                scatterPay.getSymbolCount(), fgc);
                bonusContext.setBonusMultiplier(fgc.getMultiplier());
                spinGameActivity.setBonusAwarded(bonusAwarded);
            }
        }
    }

    private void consumeBonus(NeonBonusContext bonusContext)
            throws GameEngineException {
        if (bonusContext.getFreeSpinsContext() != null &&
            bonusContext.getFreeSpinsContext().getFreeSpinsRemaining() > 0) {
            FreeSpinsContext freeSpinsContext = freeSpinBonusHandler
                    .consumeBonus(bonusContext.getFreeSpinsContext());
            bonusContext.setFreeSpinsContext(freeSpinsContext);
        }
    }

    private void checkGameStatus(GamePlay gamePlay) {
        BonusContext bonusContext = gamePlay.getGamePlayState().getBonusContext();
        if (bonusContext.updateGameBonusAwarded(gamePlay)) {
            gamePlay.getGamePlayState().setGameStatus(GameStatus.INPROGRESS);
        } else {
            gamePlay.getGamePlayState().setGameStatus(GameStatus.COMPLETED);
        }
    }

    @Override
    public String getGameConfigurationJson(String gameConfiguration) {
        if (this.gameConfigurationJsonMap.containsKey(gameConfiguration)) {
            return this.gameConfigurationJsonMap.get(gameConfiguration);
        }

        if (this.configurationMap.containsKey(gameConfiguration)) {

            NeonGameConfiguration gc = this.configurationMap.get(gameConfiguration);
            try {
                this.gameConfigurationJsonMap.put(gameConfiguration,
                        objectMapper.writeValueAsString(JsonView.with(gc)
                                .onClass(NeonGameConfiguration.class, match()
                                        .exclude("bonusGameConfiguration",
                                                "buyBurstersWithCredits",
                                                "randomConfiguration"
                                        ))));
            } catch (JsonProcessingException e) {
                e.printStackTrace();
                return "{}";
            }
            return this.gameConfigurationJsonMap.get(gameConfiguration);
        }
        return "{}";
    }

    @Override
    public GameEngineModule getGameEngineModule() {
        return new NeonModule();
    }

    private String getExternalStateKey(double stakePerLine, GamePlay slotsGamePlay) {
        return ("meter_accumulated_" + stakePerLine).replace(".","_");
    }

    private BigDecimal getTotalStake(BigDecimal stakePerLine, int noOfLines) {
        return scaledValue(stakePerLine
                .multiply(BigDecimal.valueOf(noOfLines)));
    }

    @Override
    public SlotsGameEngineResponse acknowledgeGame(NeonGameEngineRequest neonGameEngineRequest, GamePlay gamePlay, GameActivity gameActivity) throws GameEngineException {
        log.info("Acknowledge Request ++++++ {}" , neonGameEngineRequest.toString());
        SlotsGameEngineResponse response = new SlotsGameEngineResponse();
        response.setGamePlay(gamePlay);
        response.setGameActivity(gameActivity);
        return response;

    }

    @Override
    public JsonNode validate(JsonNode gameEngineRequest, JsonNode stakeSettings, JsonNode jsonNode) throws GameEngineException {
        return null;
    }

    @Override
    public JsonNode play(JsonNode gameEngineRequest, JsonNode jsonNode) throws GameEngineException {
        return null;
    }

    @Override
    public JsonNode processWinnings(JsonNode gameEngineRequest, JsonNode gamePlay, JsonNode jsonNode) throws GameEngineException {
        return null;
    }

    @Override
    public JsonNode acknowledgeGame(JsonNode jsonNode, JsonNode jsonNode1, JsonNode jsonNode2) throws GameEngineException {
        return null;
    }
}
