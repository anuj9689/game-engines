package com.algomics.torpedo.gaming.engine.divineegypt.service;

import com.algomics.torpedo.gaming.engine.api.bonus.BonusContext;
import com.algomics.torpedo.gaming.engine.api.exception.GameEngineException;
import com.algomics.torpedo.gaming.engine.api.model.*;
import com.algomics.torpedo.gaming.engine.api.module.GameEngineModule;
import com.algomics.torpedo.gaming.engine.api.service.GameEngineService;
import com.algomics.torpedo.gaming.engine.divineegypt.bonus.NeonBonusContext;
import com.algomics.torpedo.gaming.engine.divineegypt.bonus.NeonGameBonus;
import com.algomics.torpedo.gaming.engine.divineegypt.config.NeonGameConfiguration;
import com.algomics.torpedo.gaming.engine.divineegypt.jackson.NeonModule;
import com.algomics.torpedo.gaming.engine.divineegypt.model.WheelOption;
import com.algomics.torpedo.gaming.engine.divineegypt.model.dto.NeonGameEngineRequest;
import com.algomics.torpedo.gaming.engine.divineegypt.model.utils.FeatureUtils;
import com.algomics.torpedo.gaming.engine.divineegypt.symbol.NeonSymbol;
import com.algomics.torpedo.gaming.engine.slots.api.dto.SlotsGameEngineResponse;
import com.algomics.torpedo.gaming.engine.slots.api.model.SlotsGamePlayState;
import com.algomics.torpedo.gaming.engine.slots.bonus.FreeSpinsContext;
import com.algomics.torpedo.gaming.engine.slots.bonus.handler.CashOrFreeSpinsBonusOptionsHandler;
import com.algomics.torpedo.gaming.engine.slots.bonus.handler.FreeSpinBonusHandler;
import com.algomics.torpedo.gaming.engine.slots.bonus.handler.ScatterBonusHandler;
import com.algomics.torpedo.gaming.engine.slots.bonus.handler.ScatterPayHandler;
import com.algomics.torpedo.gaming.engine.slots.config.SlotsFreeGameConfiguration;
import com.algomics.torpedo.gaming.engine.slots.handlers.DefaultClusterPaysHandler;
import com.algomics.torpedo.gaming.engine.slots.handlers.DefaultPayStepWinningsHandler;
import com.algomics.torpedo.gaming.engine.slots.handlers.DefaultPayWayHandler;
import com.algomics.torpedo.gaming.engine.slots.model.PayWay;
import com.algomics.torpedo.gaming.engine.slots.model.SlotsBonusContext;
import com.algomics.torpedo.gaming.engine.slots.model.SlotsPay;
import com.algomics.torpedo.gaming.engine.slots.model.SlotsSpinGameActivity;
import com.algomics.torpedo.gaming.engine.slots.pays.PayStep;
import com.algomics.torpedo.gaming.engine.slots.reels.DefaultSlotReelLayoutSelector;
import com.algomics.torpedo.gaming.engine.slots.reels.DefaultSlotReelSpinner;
import com.algomics.torpedo.gaming.engine.slots.reels.ReelLayout;
import com.algomics.torpedo.gaming.engine.slots.reels.SymbolGrid;
import com.algomics.torpedo.gaming.engine.slots.utils.PickByWeightage;
import com.algomics.torpedo.rad.classloaders.jar.JarClassLoader;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.monitorjbl.json.JsonView;
import com.monitorjbl.json.JsonViewModule;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;

import javax.annotation.PostConstruct;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

import static com.algomics.torpedo.gaming.engine.api.model.GamePlay.scaledValue;
import static com.monitorjbl.json.Match.match;


@Service
@Data
@Slf4j
@NoArgsConstructor
public class NeonGameEngine
        implements GameEngineService<NeonGameEngineRequest, SlotsGameEngineResponse> {

    private final Map<String, NeonGameConfiguration> configurationMap = new HashMap<>();
    private final Map<String, String> gameConfigurationJsonMap = new HashMap<>();
    private  List<String> stackReelSettings = new ArrayList<>();

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
    private DefaultPayWayHandler waysHandler;
    @Autowired
    private DefaultPayStepWinningsHandler payStepWinningsHandler;
    @Autowired
    private DefaultClusterPaysHandler<NeonSymbol> clusterPaysHandler;
    @Autowired
    private DefaultSlotReelLayoutSelector layoutSelector;
    @Autowired
    private PickByWeightage pickByWeightage;

/*
    @Value("${rgs.games.player-bag.name:playerBag}")
    private String PLAYER_BAG;
*/

    @PostConstruct
    void init() throws IOException {
        this.objectMapper = objectMapper.copy().registerModule(new JsonViewModule());

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
        return Collections.singletonList("degypt96");
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
        boolean buyFeature =  gameEngineRequest.isBuyFeature()
                            && gameEngineRequest.getId() > 0
                            && gameEngineRequest.getId() <= gc.getRtpInfo().getBuyFeature().size()
                            && slotsGamePlayState.getLastBonusAwarded() == NeonGameBonus.NONE;
        BigDecimal buyStake = null;
        if(buyFeature){
            buyStake = gc.getBuyFeatureBetMultiplier();
            buyStake = scaledValue(buyStake.multiply(gameEngineRequest.getStakePerLine()).multiply(BigDecimal.valueOf(gameEngineRequest.getNoOfLines())));

        }
        slotsGamePlayState.setTotalStake(buyStake!=null?buyStake:totalStake);

        slotsGamePlay.setGamePlayState(slotsGamePlayState);

        response.setTotalBet(slotsGamePlayState.getTotalStake());
        response.setGamePlay(slotsGamePlay);

        return response;
    }

    @Override
    public GamePlay startGame(String gameConfiguration) {
        GamePlay gamePlay = new GamePlay(GameType.SLOTS);
        gamePlay.setUid(UUID.randomUUID().toString());
        gamePlay.setGameConfiguration(gameConfiguration);
        SlotsGamePlayState slotsGamePlay = new SlotsGamePlayState(GameStatus.INPROGRESS);
        slotsGamePlay.getBonusAwarded().add(NeonGameBonus.NONE);
        gamePlay.setGamePlayState(slotsGamePlay);
        return gamePlay;
    }

    @Override
    public SlotsGameEngineResponse play(NeonGameEngineRequest slotsGameEngineRequest, GamePlay gamePlay) throws GameEngineException {
        SlotsGameEngineResponse         response                = new SlotsGameEngineResponse();
        SlotsGamePlayState              slotsGamePlayState      = (SlotsGamePlayState) gamePlay.getGamePlayState();

        if (slotsGamePlayState.getLastBonusAwarded()    == null
                || slotsGamePlayState.getGameStatus()   == GameStatus.COMPLETED)
            throw new GameEngineException("Invalid Game State");

        boolean                         buyFeature              = slotsGameEngineRequest.isBuyFeature()
                                                                    && slotsGamePlayState.getLastBonusAwarded() == NeonGameBonus.NONE;

        SlotsSpinGameActivity   slotSpinActivity;
        final NeonGameBonus           gameBonus   = (NeonGameBonus) slotsGamePlayState.getLastBonusAwarded();

        if (buyFeature)
            slotSpinActivity    = processBaseSpin(gamePlay, NeonGameBonus.BUY_FEATURE);
        else if (gameBonus.equals(NeonGameBonus.NONE) || gameBonus.equals(NeonGameBonus.FREE_SPINS) || gameBonus.equals(NeonGameBonus.BUY_FREE_SPINS))
            slotSpinActivity    = processBaseSpin(gamePlay, gameBonus);
        else if (gameBonus.equals(NeonGameBonus.WHEEL_BONUS)){
            slotSpinActivity    = processWheelSpin(gamePlay, gameBonus);
         }
        else throw new GameEngineException("Invalid Game process");

        response.setGameActivity(slotSpinActivity);
        response.setGamePlay(gamePlay);
        return response;
    }

    @Override
    public SlotsGameEngineResponse processWinnings(NeonGameEngineRequest request, GamePlay gamePlay, GameActivity gameActivity) {

        SlotsGameEngineResponse response = new SlotsGameEngineResponse();

        SlotsGamePlayState slotsGamePlayState = (SlotsGamePlayState) gamePlay.getGamePlayState();
        response.setTotalBet(slotsGamePlayState.getTotalStake());
        response.setGamePlay(gamePlay);

        SlotsSpinGameActivity spinGameActivity = (SlotsSpinGameActivity) gameActivity;
        NeonBonusContext     previousContext     = (NeonBonusContext) slotsGamePlayState.getBonusContext();
        BigDecimal spinWinnings;
            spinWinnings = spinGameActivity.getPaySteps()
                    .stream()
                    .map(payStep -> payStepWinningsHandler
                            .payStepWinnings(getTotalStake(slotsGamePlayState.getStakePerLine(), slotsGamePlayState.getNoOfLines()),
                                    slotsGamePlayState.getStakePerLine(),
                                    slotsGamePlayState.getNoOfLines(), payStep))
                    .reduce(BigDecimal::add).orElse(BigDecimal.ZERO);


         if(previousContext.getBonusAwarded()!=null && !previousContext.getBonusAwarded().equals(NeonGameBonus.FREE_SPINS) && previousContext.getBonusAwarded().equals(NeonGameBonus.NONE)  ) spinWinnings =spinWinnings.multiply(previousContext.getWildCount());

         if(previousContext.getWheelWin()!=null )  spinWinnings    = previousContext.getWheelWin().multiply(getTotalStake(slotsGamePlayState.getStakePerLine(), slotsGamePlayState.getNoOfLines()));


            spinGameActivity.setWinnings(spinWinnings);

        if(spinGameActivity.getTotalWinnings()!=null)
            spinGameActivity.setTotalWinnings(spinGameActivity.getTotalWinnings().add(spinWinnings));

        response.setSpinPayout(spinWinnings);
        slotsGamePlayState.setTotalWinnings(slotsGamePlayState.getTotalWinnings().add(spinWinnings));
        response.setTotalPayout(slotsGamePlayState.getTotalWinnings());
        response.setGameActivity(spinGameActivity);
        return response;
    }

    private SlotsSpinGameActivity processBaseSpin(GamePlay gamePlay, NeonGameBonus gameBonus) {
        SlotsGamePlayState      slotsGamePlayState          = (SlotsGamePlayState) gamePlay.getGamePlayState();
        NeonGameConfiguration   gc                          = configurationMap.get(gamePlay.getGameConfiguration());
        NeonBonusContext                previousContext     = (NeonBonusContext) slotsGamePlayState.getBonusContext();
        ReelLayout<NeonSymbol, NeonGameBonus> reelLayout    =   layoutSelector.selectReelLayout( gameBonus , gc.getReelLayoutConfiguration());

        List<Integer>                   topReelIndex        = getReelSpinner().reelSpin(gc.getReelLayoutConfiguration(),
                                                             reelLayout.getReels());

        int cols = (gameBonus.equals(NeonGameBonus.FREE_SPINS) || gameBonus.equals(NeonGameBonus.BUY_FREE_SPINS)) ? gc.getColumns() + 2 : gc.getColumns(); // added as free spins has 7 reels

        SlotsSpinGameActivity           slotSpinActivity    = new SlotsSpinGameActivity(
                                                                cols,
                                                                gc.getRows(),
                                                                topReelIndex,
                                                                slotsGamePlayState.getGameStatus(),
                                                                reelLayout.getIndex(),
                                                                BonusContext
                                                                        .copyFromPreviousOrCreateNew(previousContext,
                                                                                NeonBonusContext::new,
                                                                                NeonBonusContext::new));

        SymbolGrid                           symbolGrid         = slotSpinActivity.prepareSymbolGrid(gc.getReelLayoutConfiguration());
        NeonBonusContext                     bonusContext       = (NeonBonusContext) slotSpinActivity.getBonusContext();

        if( (previousContext!=null && previousContext.isBuyFreeGame()) || gameBonus.equals(NeonGameBonus.BUY_FEATURE) ) bonusContext.setBuyFreeGame(true);

        consumeBonus((NeonBonusContext) slotSpinActivity.getBonusContext());

        List<NeonSymbol>    wildSymbols     = new LinkedList<>();
        wildSymbols.add(NeonSymbol.WC);

        int wildCount = FeatureUtils.getFeatureSymbolCount(symbolGrid.getSymbolGrid(), NeonSymbol.WC);
        bonusContext.setWildCount(BigDecimal.valueOf(wildCount));

        if(wildCount > 1) {
            List<List<Integer>> reelSymbolPositions = symbolGrid.getSymbolPositionsOnGrid(NeonSymbol.WC);
            LinkedMultiValueMap<NeonSymbol, List<List<Integer>>> specialPositions = new LinkedMultiValueMap<>();
            specialPositions.set(NeonSymbol.WC, reelSymbolPositions);
            bonusContext.setSpecialSymbolPositions(specialPositions);
        }
        PayStep             payStep         = new PayStep(symbolGrid);

        slotSpinActivity.addPayStep(payStep);
        List<PayWay> payWays = waysHandler.findPayWays(
                slotSpinActivity.getLastPayStep().getStepSymbolGrid(),
                wildSymbols,
                Collections.singletonList(NeonSymbol.SC),
                gc.getSymbolStakeMultipliers(),
                BigDecimal.ONE );
        payStep.setPayWays(payWays);

        int bonusCount = FeatureUtils.getFeatureSymbolCount(symbolGrid.getSymbolGrid(), NeonSymbol.BG);
        if((gameBonus.equals(NeonGameBonus.NONE) || gameBonus.equals(NeonGameBonus.BUY_FEATURE) ) && bonusCount >= 3 ) {
            awardWheelBonus(slotSpinActivity, gc );
            ((SlotsGamePlayState) gamePlay.getGamePlayState()).getBonusAwarded().add(NeonGameBonus.WHEEL_BONUS);
        }

        if(gameBonus.equals(NeonGameBonus.FREE_SPINS) || gameBonus.equals(NeonGameBonus.BUY_FREE_SPINS)) awardFGBonus(slotSpinActivity, gc,gameBonus);

        slotsGamePlayState.setBonusContext(slotSpinActivity.getBonusContext());
        gamePlay.setGamePlayState(slotsGamePlayState);

        checkGameStatus(gamePlay);
        slotSpinActivity.setGameStatus(gamePlay.getGamePlayState().getGameStatus());
        gamePlay.setStatus(gamePlay.getGamePlayState().getGameStatus());
        //end

        return slotSpinActivity;
    }

    private SlotsSpinGameActivity processWheelSpin(GamePlay gamePlay, NeonGameBonus gameBonus) {
        SlotsGamePlayState      slotsGamePlayState          = (SlotsGamePlayState) gamePlay.getGamePlayState();
        NeonBonusContext                previousContext     = (NeonBonusContext) slotsGamePlayState.getBonusContext();
        WheelOption                     stopOption          = pickByWeightage.pick(previousContext.getWheelOptions());
        NeonGameConfiguration   gc                          = configurationMap.get(gamePlay.getGameConfiguration());
        SlotsSpinGameActivity           slotSpinActivity    = new SlotsSpinGameActivity();
        NeonBonusContext                bonusContext        = (NeonBonusContext) BonusContext
                                                                                    .copyFromPreviousOrCreateNew(null,
                                                                                            NeonBonusContext::new,
                                                                                            NeonBonusContext::new);

        slotSpinActivity.setBonusContext(bonusContext);
        slotSpinActivity.setPaySteps(new ArrayList<>());
        bonusContext.setWheelOptions( previousContext.getWheelOptions());
        bonusContext.setScatterCount(previousContext.getScatterCount());
        bonusContext.setWheelStop(stopOption);
        bonusContext.setWheelWin(BigDecimal.ZERO);


        if(stopOption.getType().equals("FREE_GAME")) {
            SlotsFreeGameConfiguration<NeonGameBonus> fgc = gc.getBonusGameConfiguration().getFreeGameConfiguration();
            NeonGameBonus bonusAward = fgc.getAwardGameBonus();
            if(previousContext.isBuyFreeGame()){
                 bonusAward = NeonGameBonus.BUY_FREE_SPINS;
            }
            FreeSpinsContext freeSpinsContext = new FreeSpinsContext();
            int freeSpins   = stopOption.getValue().intValue();
            freeSpinsContext.setFreeSpinsAwarded(freeSpins);
            freeSpinsContext.setTotalFreeSpinsAwarded(freeSpins);
            freeSpinsContext.setFreeSpinsRemaining(freeSpins);
            bonusContext.setFreeSpinsContext(freeSpinsContext);
            slotSpinActivity.setBonusAwarded(bonusAward);
            bonusContext.setBonusAwarded(bonusAward);
            gamePlay.getGamePlayState().getBonusContext().setBonusAwarded(bonusAward);
            slotsGamePlayState.getBonusAwarded().add(bonusAward);
        }
        else {
            BigDecimal    bonusWin     = stopOption.getValue();
            bonusContext.setWheelWin(bonusWin);
            bonusContext.setWildCount(BigDecimal.ONE);
        }

        slotsGamePlayState.setBonusContext(slotSpinActivity.getBonusContext());
        gamePlay.setGamePlayState(slotsGamePlayState);

         checkGameStatus(gamePlay);

        slotSpinActivity.setGameStatus(gamePlay.getGamePlayState().getGameStatus());
        gamePlay.setStatus(gamePlay.getGamePlayState().getGameStatus());
        //end

        return slotSpinActivity;
    }

    private void awardWheelBonus(SlotsSpinGameActivity spinGameActivity, NeonGameConfiguration gc) {

        NeonGameBonus       awardBonus      = NeonGameBonus.WHEEL_BONUS;
        NeonBonusContext    bonusContext    = (NeonBonusContext) spinGameActivity.getBonusContext();
        bonusContext.setBonusAwarded(awardBonus);
        bonusContext.setWheelOptions(gc.getBonusGameConfiguration().getJackpotWheelOptions());
        SymbolGrid          symbolGrid          = spinGameActivity.getLastPayStep().getStepSymbolGrid();
        List<List<Integer>> reelSymbolPositions = symbolGrid.getSymbolPositionsOnGrid(NeonSymbol.BG);
        LinkedMultiValueMap<NeonSymbol, List<List<Integer>>> specialPositions = new LinkedMultiValueMap<>();
        specialPositions.set(NeonSymbol.BG, reelSymbolPositions);
        bonusContext.setSpecialSymbolPositions(specialPositions);
        spinGameActivity.setBonusAwarded(awardBonus);
    }

    private void awardFGBonus(SlotsSpinGameActivity spinGameActivity, NeonGameConfiguration gc,NeonGameBonus gameBonus) {

        //scatter wins
        SlotsPay scatterPay = scatterPayHandler
                .awardBonus(spinGameActivity.getLastPayStep().getStepSymbolGrid(),
                        NeonSymbol.SC,
                        gc.getBonusGameConfiguration().getScatterStakeMultipliers());

        NeonBonusContext bonusContext = (NeonBonusContext) spinGameActivity.getBonusContext();
        //award free games
        if (scatterPay!=null && scatterPay.getSymbolCount() >= 3) {
            //to set positions data
            List<List<Integer>> reelSymbolPositions = spinGameActivity.getLastPayStep().getStepSymbolGrid().getSymbolPositionsOnGrid(NeonSymbol.BG);
            LinkedMultiValueMap<NeonSymbol, List<List<Integer>>> specialPositions = new LinkedMultiValueMap<>();
            specialPositions.set(NeonSymbol.SC, reelSymbolPositions);
            bonusContext.setSpecialSymbolPositions(specialPositions);
            spinGameActivity.getLastPayStep().setScatterPay(scatterPay);
            if(gc.getBonusGameConfiguration().getFreeGameConfiguration().getAwardGameBonus()!=null) {
                SlotsFreeGameConfiguration<NeonGameBonus> fgc = gc.getBonusGameConfiguration()
                        .getFreeGameConfiguration();
                NeonGameBonus bonusAwarded =
                        freeSpinBonusHandler.awardBonus((SlotsBonusContext) spinGameActivity.getBonusContext(),
                                scatterPay.getSymbolCount(), fgc);
                if(gameBonus.equals(NeonGameBonus.FREE_SPINS)){
                    bonusAwarded = NeonGameBonus.FREE_SPINS;
                }
                if(gameBonus.equals(NeonGameBonus.BUY_FREE_SPINS)){
                    bonusAwarded = NeonGameBonus.BUY_FREE_SPINS;
                }
                 bonusContext.setBonusMultiplier(fgc.getMultiplier());
                spinGameActivity.setBonusAwarded(bonusAwarded);
            }
        }
    }

    private void consumeBonus(NeonBonusContext bonusContext) throws GameEngineException {


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
                                                "expandReelConfiguration",
                                                "buyFeatureBetMultiplier"
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
}
