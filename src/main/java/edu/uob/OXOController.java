package edu.uob;

import edu.uob.OXOMoveException.*;

public class OXOController {
    OXOModel gameModel;

    public OXOController(OXOModel model) {
        gameModel = model;
    }

    public void handleIncomingCommand(String command) throws OXOMoveException {
        int currentPlayer = gameModel.getCurrentPlayerNumber();
        OXOPlayer movingPlayer = gameModel.getPlayerByNumber(currentPlayer);
        char row = Character.toLowerCase(command.charAt(0));
        char col = command.charAt(1);
        int rowPosition = row - 'a';
        int colPosition = Character.getNumericValue(col) - 1;
        if (command.length() != 2) {
            throw new InvalidIdentifierLengthException(command.length());
        }
        if (row < 'a' || row > 'i') {
            throw new InvalidIdentifierCharacterException(RowOrColumn.ROW, row);
        }
        if (col < '1' || col > '9') {
            throw new InvalidIdentifierCharacterException(RowOrColumn.COLUMN, col);
        }
        validateInput(rowPosition, colPosition);
        if (gameModel.getWinner() == null) {
            gameModel.setCellOwner(rowPosition, colPosition, movingPlayer);
        }
        //if player == the max number of players
        if (currentPlayer == gameModel.getNumberOfPlayers() - 1) {
            currentPlayer = 0;
            gameModel.setCurrentPlayerNumber(currentPlayer);
        } else {
            gameModel.setCurrentPlayerNumber(currentPlayer + 1);
        }
        if (checkForWin()) {
            gameModel.setWinner(movingPlayer);
        }
        if (gameModel.checkForDraw()) {
            gameModel.setGameDrawn();
        }
    }

    public void validateInput(int rowPosition, int colPosition) throws OXOMoveException {
        int rowCount = gameModel.getNumberOfRows();
        int colCount = gameModel.getNumberOfColumns();
        if (rowPosition < 0 || rowPosition >= rowCount || colPosition < 0 || colPosition >= colCount) {
            throw new OutsideCellRangeException(rowPosition >= rowCount ? RowOrColumn.ROW : RowOrColumn.COLUMN,
                    rowPosition >= rowCount ? rowPosition - rowCount : colPosition - colCount);
        }
        if (gameModel.getCellOwner(rowPosition, colPosition) != null) {
            throw new CellAlreadyTakenException(rowPosition, colPosition);
        }
    }


    public void addRow() {
        gameModel.addRow();
    }

    public void removeRow() {
        gameModel.removeRow();
    }

    public void addColumn() {
        gameModel.addColumn();
    }

    public void removeColumn() {
        gameModel.removeColumn();
    }

    public boolean checkForWin() {
        OXOPlayer lastPlayer = gameModel.getPlayerByNumber((gameModel.getCurrentPlayerNumber() - 1 + gameModel.getNumberOfPlayers()) % gameModel.getNumberOfPlayers());
        int winThreshold = gameModel.getWinThreshold();
        int rowCount = gameModel.getNumberOfRows();
        int colCount = gameModel.getNumberOfColumns();

        // Check rows
        for (int i = 0; i < rowCount; i++) {
            int count = 0;
            for (int j = 0; j < colCount; j++) {
                if (gameModel.getCellOwner(i, j) == lastPlayer) {
                    count++;
                    if (count == winThreshold) {
                        return true;
                    }
                } else {
                    count = 0;
                }
            }
        }

        // Check columns
        for (int j = 0; j < colCount; j++) {
            int count = 0;
            for (int i = 0; i < rowCount; i++) {
                if (gameModel.getCellOwner(i, j) == lastPlayer) {
                    count++;
                    if (count == winThreshold) {
                        return true;
                    }
                } else {
                    count = 0;
                }
            }
        }

        // Check diagonal from top left to bottom right
        for (int i = 0; i <= rowCount - winThreshold; i++) {
            for (int j = 0; j <= colCount - winThreshold; j++) {
                int count = 0;
                for (int k = 0; k < winThreshold; k++) {
                    if (gameModel.getCellOwner(i + k, j + k) == lastPlayer) {
                        count++;
                        if (count == winThreshold) {
                            return true;
                        }
                    } else {
                        break;
                    }
                }
            }
        }

        // Check diagonal from bottom left to top right
        for (int i = winThreshold - 1; i < rowCount; i++) {
            for (int j = 0; j <= colCount - winThreshold; j++) {
                int count = 0;
                for (int k = 0; k < winThreshold; k++) {
                    if (gameModel.getCellOwner(i - k, j + k) == lastPlayer) {
                        count++;
                        if (count == winThreshold) {
                            return true;
                        }
                    } else {
                        break;
                    }
                }
            }
        }

        return false;
    }

    public void increaseWinThreshold() {
        gameModel.setWinThreshold(gameModel.getWinThreshold() + 1);
    }

    public void decreaseWinThreshold() {
        if (gameModel.getWinThreshold() > 3 && !gameModel.checkForDraw()) //Check for draw to make sure the game has started
        {
            gameModel.setWinThreshold((gameModel.getWinThreshold() - 1));
        }
    }

    public void reset() {
        int rowcount;
        int colcount;
        for (rowcount = 0; rowcount < gameModel.getNumberOfRows(); rowcount++) {
            for (colcount = 0; colcount < gameModel.getNumberOfColumns(); colcount++) {
                gameModel.setCellOwner(rowcount, colcount, null);
            }
        }
        gameModel.setCurrentPlayerNumber(0);
        gameModel.setWinner(null);
        gameModel.resetGameDraw();
    }
}
