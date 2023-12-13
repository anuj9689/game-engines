package com.algomics.torpedo.gaming.engine.divineegypt;


import com.algomics.torpedo.gaming.engine.divineegypt.service.NeonGameEngine;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

/**
 * The ROAGameEngineTest.
 */
@ExtendWith({SpringExtension.class})
@ContextConfiguration(classes = {TestConfig.class})
class NeonGameEngineTest {



    @Value("${rgs.games.player-bag.name:playerBag}")
    private String PLAYER_BAG;

    public static final String GAME_CONFIGURATION = "degypt96";

    @Autowired
    NeonGameEngine neonGameEngine;
   /* @RepeatedTest(1)
    void test_game_play() throws GameEngineException {
        GamePlay gamePlay = neonGameEngine.startGame(GAME_CONFIGURATION);
        SlotsGameEngineResponse response = neonGameEngine.validate(getGameRequest(), getStakeSettings(), gamePlay);
        response = neonGameEngine.play(getGameRequest(), response.getGamePlay());
        assertNotNull(response);
    }


    @Test
    void test_force_reelset_1() throws GameEngineException {
        int reel = 1;

        ForceReelLayoutSelector.set(reel-1);
        ForceReelSpinner.set(List.of(0,0,0,0,0,0,0));
        GamePlay gamePlay = neonGameEngine.startGame(GAME_CONFIGURATION);
        SlotsGameEngineResponse response = neonGameEngine.validate(getGameRequest(), getStakeSettings(), gamePlay);
        response = neonGameEngine.play(getGameRequest(), response.getGamePlay());
        SlotsSpinGameActivity gameActivity = (SlotsSpinGameActivity) response.getGameActivity();

        assertEquals(0, gameActivity.getSelectedReel());
        assertEquals("A,K,H1,H2,Q,H3,J", gameActivity.getPaySteps().get(0).getStepSymbolGrid().getSymbolGrid().get(0).stream().map(Object::toString).reduce((s, s2) -> s+","+s2).get());
        assertEquals(0, gameActivity.getLastPayStep().getClusters().size());

        ReelLayout<NeonSymbol, NeonGameBonus> reelSet = getSlotReels(reel-1);

        assertEquals(50, reelSet.getWeight());
        assertEquals(reelset1, getReelSet(reelSet.getReels()));
    }*/

    /*@Test
    void test_force_reelset_1_wins() throws GameEngineException {
        int reel = 1;

        ForceReelLayoutSelector.set(reel-1);
        ForceReelSpinner.set(List.of(15, 33, 12, 21, 11, 5, 14));
        GamePlay gamePlay = neonGameEngine.startGame(GAME_CONFIGURATION);
        SlotsGameEngineResponse response = neonGameEngine.validate(getGameRequest(), getStakeSettings(), gamePlay);
        response = neonGameEngine.play(getGameRequest(), response.getGamePlay());

        BigDecimal total = BigDecimal.ZERO;
        while (response.getGamePlay().getGamePlayState().getGameStatus() != GameStatus.COMPLETED){
            response = neonGameEngine.validate(getGameRequest(), getStakeSettings(), gamePlay);
            response = neonGameEngine.play(getGameRequest(), response.getGamePlay());
            total = total.add(response.getSpinPayout());
        }

        assertEquals(5, total.doubleValue());
    }*/
}