package edu.uob;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Duration;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTimeoutPreemptively;

public class MaxBoardSizeTesting {
    private OXOModel model;
    private OXOController controller;

    // Make a new "standard" (9x9) board with a win threshold of 9
    @BeforeEach
    void setup() {
        model = new OXOModel(9, 9, 9);
        model.addPlayer(new OXOPlayer('X'));
        model.addPlayer(new OXOPlayer('O'));
        controller = new OXOController(model);
    }

    @Test
    void testMaxBoardSize() throws OXOMoveException {
        OXOPlayer firstMovingPlayer = model.getPlayerByNumber(model.getCurrentPlayerNumber());
        model.addRow(); //Should not work as it goes over 9x9
        model.addColumn(); //Should not work as it goes over 9x9
        sendCommandToController("b3"); //First Player Move
        OXOPlayer secondMovingPlayer = model.getPlayerByNumber(model.getCurrentPlayerNumber());
        sendCommandToController("a2"); //Second Player Move
        sendCommandToController("b1"); //First Player Move
        sendCommandToController("b2"); //Second Player Move
        sendCommandToController("c1"); //First Player Move
        sendCommandToController("c2"); //Second Player Move
        controller.decreaseWinThreshold(); //Should not work as midgame
        sendCommandToController("d1"); //First Player Move
        sendCommandToController("d2"); //Second Player Move
        sendCommandToController("e1"); //First Player Move
        sendCommandToController("e2"); //Second Player Move
        sendCommandToController("f1"); //First Player Move
        sendCommandToController("f2"); //Second Player Move
        sendCommandToController("g1"); //First Player Move
        sendCommandToController("g2"); //Second Player Move
        sendCommandToController("h1"); //First Player Move
        sendCommandToController("h2"); //Second Player Move

        //Check that winthreshold cannot be decreased midgame
        String failedWinDecreaseCheck = "The win threshold was decreased midgame - this should not be possible";
        assertEquals(null, model.getWinner(), failedWinDecreaseCheck);

        sendCommandToController("h3"); //First Player Move
        sendCommandToController("i2"); //Winning Move for P2

        // Check that the second player has won
        String failedTestComment = "Winner was expected to be " + secondMovingPlayer.getPlayingLetter() + " but wasn't";
        assertEquals(secondMovingPlayer, model.getWinner(), failedTestComment);

        //Check that the board size is still 9x9
        String failedToCapBoardSize = "Board has become bigger than 9x9";
        assertEquals(model.getNumberOfRows(), 9, failedToCapBoardSize);
        assertEquals(model.getNumberOfColumns(), 9, failedToCapBoardSize);
    }


    // This next method is a utility function that can be used by any of the test methods to _safely_ send a command to the controller
    void sendCommandToController(String command) {
        // Try to send a command to the server - call will timeout if it takes too long (in case the server enters an infinite loop)
        // Note: this is ugly code and includes syntax that you haven't encountered yet
        String timeoutComment = "Controller took too long to respond (probably stuck in an infinite loop)";
        assertTimeoutPreemptively(Duration.ofMillis(1000), () -> controller.handleIncomingCommand(command), timeoutComment);
    }
}

