package edu.uob;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTimeoutPreemptively;

import edu.uob.OXOMoveException.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Duration;

class FurtherTesting {
    private OXOModel model;
    private OXOController controller;

    // Make a new "standard" (5x5) board with a win threshold of 4
    @BeforeEach
    void setup() {
        model = new OXOModel(5, 5, 4);
        model.addPlayer(new OXOPlayer('X'));
        model.addPlayer(new OXOPlayer('O'));
        model.addPlayer(new OXOPlayer('Z'));
        model.addPlayer(new OXOPlayer('J'));
        controller = new OXOController(model);
    }

    @Test
    void testAdditionalPlayers() throws OXOMoveException {
        OXOPlayer firstMovingPlayer = model.getPlayerByNumber(model.getCurrentPlayerNumber());
        sendCommandToController("a1"); //First Player Move
        OXOPlayer secondMovingPlayer = model.getPlayerByNumber(model.getCurrentPlayerNumber());
        sendCommandToController("a2"); //Second Player Move
        OXOPlayer thirdMovingPlayer = model.getPlayerByNumber(model.getCurrentPlayerNumber());
        sendCommandToController("a3"); //Third Player Move
        OXOPlayer fourthMovingPlayer = model.getPlayerByNumber(model.getCurrentPlayerNumber());
        sendCommandToController("a4"); //Fourth Player
        sendCommandToController("b1"); //First Player Move
        sendCommandToController("b2"); //Second Player Move
        sendCommandToController("b3"); //Third Player Move
        sendCommandToController("b4"); //Fourth Player
        sendCommandToController("c5"); //First Player Move
        sendCommandToController("b5"); //Second Player Move
        sendCommandToController("c3"); //Third Player Move
        sendCommandToController("c4"); //Fourth Player
        sendCommandToController("d1"); //First Player Move
        sendCommandToController("d2"); //Second Player Move
        sendCommandToController("d3"); //Third Player Move
        sendCommandToController("d4"); //Third Player Move

        // Check that the third player has won
        String failedTestComment = "Winner was expected to be " + thirdMovingPlayer.getPlayingLetter() + " but wasn't";
        assertEquals(thirdMovingPlayer, model.getWinner(), failedTestComment);
    }

    @Test
    void testWinThresholdIncrease() throws OXOMoveException {
        //Similar to previous test, but decreases winthres to 3 before the game
        controller.decreaseWinThreshold();
        OXOPlayer firstMovingPlayer = model.getPlayerByNumber(model.getCurrentPlayerNumber());
        sendCommandToController("a1"); //First Player Move
        OXOPlayer secondMovingPlayer = model.getPlayerByNumber(model.getCurrentPlayerNumber());
        sendCommandToController("a2"); //Second Player Move
        OXOPlayer thirdMovingPlayer = model.getPlayerByNumber(model.getCurrentPlayerNumber());
        sendCommandToController("a3"); //Third Player Move
        OXOPlayer fourthMovingPlayer = model.getPlayerByNumber(model.getCurrentPlayerNumber());
        sendCommandToController("a4"); //Fourth Player
        sendCommandToController("b1"); //First Player Move
        sendCommandToController("b2"); //Second Player Move
        sendCommandToController("b3"); //Third Player Move
        sendCommandToController("b4"); //Fourth Player
        sendCommandToController("c5"); //First Player Move
        sendCommandToController("b5"); //Second Player Move
        sendCommandToController("c3"); //Third Player Move
        sendCommandToController("c4"); //Fourth Player

        // Check that the third player has won
        String failedTestComment = "Winner was expected to be " + thirdMovingPlayer.getPlayingLetter() + " but wasn't -" +
                " winthreshold may not be decreasing before the game properly";
        assertEquals(thirdMovingPlayer, model.getWinner(), failedTestComment);
    }

    // This next method is a utility function that can be used by any of the test methods to _safely_ send a command to the controller
    void sendCommandToController(String command) {
        // Try to send a command to the server - call will timeout if it takes too long (in case the server enters an infinite loop)
        // Note: this is ugly code and includes syntax that you haven't encountered yet
        String timeoutComment = "Controller took too long to respond (probably stuck in an infinite loop)";
        assertTimeoutPreemptively(Duration.ofMillis(1000), () -> controller.handleIncomingCommand(command), timeoutComment);
    }
}
