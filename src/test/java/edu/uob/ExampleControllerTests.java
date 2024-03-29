package edu.uob;

import edu.uob.OXOMoveException.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Duration;

import static org.junit.jupiter.api.Assertions.*;

class ExampleControllerTests {
    private OXOModel model;
    private OXOController controller;

    // Make a new "standard" (3x3) board before running each test case (i.e. this method runs before every `@Test` method)
    // In order to test boards of different sizes, winning thresholds or number of players, create a separate test file (without this method in it !)
    @BeforeEach
    void setup() {
        model = new OXOModel(3, 3, 3);
        model.addPlayer(new OXOPlayer('X'));
        model.addPlayer(new OXOPlayer('O'));
        controller = new OXOController(model);
    }

    // This next method is a utility function that can be used by any of the test methods to _safely_ send a command to the controller
    void sendCommandToController(String command) {
        // Try to send a command to the server - call will timeout if it takes too long (in case the server enters an infinite loop)
        // Note: this is ugly code and includes syntax that you haven't encountered yet
        String timeoutComment = "Controller took too long to respond (probably stuck in an infinite loop)";
        assertTimeoutPreemptively(Duration.ofMillis(1000), () -> controller.handleIncomingCommand(command), timeoutComment);
    }

    // Test simple move taking and cell claiming functionality
    @Test
    void testBasicMoveTaking() throws OXOMoveException {
        // Find out which player is going to make the first move
        OXOPlayer firstMovingPlayer = model.getPlayerByNumber(model.getCurrentPlayerNumber());
        // Make a move
        sendCommandToController("a1");
        // Check that A1 (cell [0,0] on the board) is now "owned" by the first player
        String failedTestComment = "Cell a1 wasn't claimed by the first player";
        assertEquals(firstMovingPlayer, controller.gameModel.getCellOwner(0, 0), failedTestComment);
    }

    // Test out basic win detection
    @Test
    void testBasicWin() throws OXOMoveException {
        // Find out which player is going to make the first move (they should be the eventual winner)
        OXOPlayer firstMovingPlayer = model.getPlayerByNumber(model.getCurrentPlayerNumber());
        // Make a bunch of moves for the two players
        sendCommandToController("a1"); // First player
        sendCommandToController("b1"); // Second player
        sendCommandToController("a2"); // First player
        sendCommandToController("b2"); // Second player
        sendCommandToController("a3"); // First player

        // a1, a2, a3 should be a win for the first player (since players alternate between moves)
        // Let's check to see whether the first moving player is indeed the winner
        String failedTestComment = "Winner was expected to be " + firstMovingPlayer.getPlayingLetter() + " but wasn't";
        assertEquals(firstMovingPlayer, model.getWinner(), failedTestComment);
    }

    @Test
    void testBasicReset() throws OXOMoveException {
        OXOPlayer firstMovingPlayer = model.getPlayerByNumber(model.getCurrentPlayerNumber());
        sendCommandToController("a1"); // First player
        sendCommandToController("b1"); // Second player
        sendCommandToController("a2"); // First player
        sendCommandToController("b2"); // Second player
        sendCommandToController("a3"); // First player

        // a1, a2, a3 should be a win for the first player (since players alternate between moves)
        // Let's check to see whether the first moving player is indeed the winner
        String failedTestComment = "Winner was expected to be " + firstMovingPlayer.getPlayingLetter() + " but wasn't";
        assertEquals(firstMovingPlayer, model.getWinner(), failedTestComment);

        controller.reset();

        String failedToReset = "Board failed to reset, cells are still claimed";
        assertEquals(null, model.getCellOwner(0,0), failedToReset);
        assertEquals(null, model.getCellOwner(0,1), failedToReset);
        assertEquals(null, model.getCellOwner(1,0), failedToReset);
        assertEquals(null, model.getCellOwner(1,1), failedToReset);
    }

    @Test
    void testWinThresholdChanging() throws OXOMoveException {
        /*Increases winthreshold before game and attempts to decrease it partway, if implemented
        correctly it should not work and there should be no winner*/
        model.addRow();
        model.addColumn();
        model.setWinThreshold(5);
        OXOPlayer firstMovingPlayer = model.getPlayerByNumber(model.getCurrentPlayerNumber());
        sendCommandToController("a1"); // First player
        controller.decreaseWinThreshold();
        controller.decreaseWinThreshold();
        sendCommandToController("b1"); // Second player
        sendCommandToController("a2"); // First player
        sendCommandToController("b2"); // Second player
        sendCommandToController("a3"); // First player

        String failedTestComment = "No winner was expected due to the winthreshold being too high, but a winner was found -" +
                "winthreshold should not be able to decrease once the game has started" ;
        assertEquals(null, model.getWinner(), failedTestComment);
    }

    @Test
    void testWinPreventingInput() throws OXOMoveException {
        //c2 should not be claimed as it is entered after a player has won
        OXOPlayer firstMovingPlayer = model.getPlayerByNumber(model.getCurrentPlayerNumber());
        sendCommandToController("a1"); // First player
        sendCommandToController("b1"); // Second player
        sendCommandToController("a2"); // First player
        sendCommandToController("b2"); // Second player
        sendCommandToController("a3"); // First player
        sendCommandToController("c2"); // Second player - move should not activate

        String failedCellComment = "c2 should not be claimed by a player as a player had already won when it was claimed";
        assertEquals(null,model.getCellOwner(2,2), failedCellComment);  //Position of c2
    }

    @Test
    void testDrawWhileChangingBoard() throws OXOMoveException {
        model.setWinThreshold(5);//So the game does not end due to win
        OXOPlayer firstMovingPlayer = model.getPlayerByNumber(model.getCurrentPlayerNumber());
        sendCommandToController("a1"); // First player
        sendCommandToController("b1"); // Second player
        sendCommandToController("a2"); // First player
        sendCommandToController("b2"); // Second player
        sendCommandToController("a3"); // First player
        sendCommandToController("b3"); // Second player
        sendCommandToController("c1"); // First player
        sendCommandToController("c2"); // Second player
        sendCommandToController("c3"); // First player

        String failedToDraw = "Should have been a draw";
        assertEquals(model.isGameDrawn(),true,failedToDraw);

        model.addRow();
        model.addColumn();

        String removeDraw = "Draw status should be removed after increasing board size";
        assertEquals(model.isGameDrawn(),false,removeDraw);

        model.removeRow();
        model.removeColumn();

        String failedToRedraw = "Draw should have been reactivated after shrinking board";
        assertEquals(model.isGameDrawn(),true, failedToRedraw);
    }
    // Example of how to test for the throwing of exceptions
    @Test
    void testInvalidIdentifierException() throws OXOMoveException {
        // Check that the controller throws a suitable exception when it gets an invalid command
        String failedTestComment = "Controller failed to throw an InvalidIdentifierLengthException for command `abc123`";
        // The next lins is a bit ugly, but it is the easiest way to test exceptions (soz)
        assertThrows(InvalidIdentifierLengthException.class, () -> sendCommandToController("abc123"), failedTestComment);
        assertThrows(InvalidIdentifierLengthException.class, () -> controller.handleIncomingCommand("aa1"));
    }

    @Test
    void testCellAlreadyTakenException() throws OXOMoveException {
        sendCommandToController("a1");
        // Check that the controller throws a suitable exception when it gets a command for a cell which is claimed
        String failedTestComment = "Controller failed to throw an CellAlreadyTaken Exception for command a1";
        assertThrows(CellAlreadyTakenException.class, () -> sendCommandToController("a1"), failedTestComment);
    }

    @Test
    void testInvalidIdentifierCharacterException() throws OXOMoveException {
        // Check that the controller throws a suitable exception when it gets an invalid command
        String failedLetterTest = "Controller failed to throw an InvalidIdentifierCharacterException for z1";
        assertThrows(InvalidIdentifierCharacterException.class, () -> sendCommandToController("z1"), failedLetterTest);
        String failedSpecialCharacterTest = "Controller failed to throw an InvalidIdentifierCharacterException for a!";
        assertThrows(InvalidIdentifierCharacterException.class, () -> sendCommandToController("a!"), failedSpecialCharacterTest);
    }

    @Test
    void testOutsideCellRangeException() throws OXOMoveException {
        // Check that the controller throws a suitable exception when you try to enter a command unfit for a 3x3 board
        String failedOutsideRowTest = "Controller failed to throw an OutsideCellRangeException for i1 despite it being" +
                "a 3x3 board";
        assertThrows(OutsideCellRangeException.class, () -> sendCommandToController("i1"), failedOutsideRowTest);
        String failedOutsideColTest = "Controller failed to throw an OutsideCellRangeException for a9 despite it being" +
                "a 3x3 board";
        assertThrows(OutsideCellRangeException.class, () -> sendCommandToController("a9"), failedOutsideColTest);
    }
}

