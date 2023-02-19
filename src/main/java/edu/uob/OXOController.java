package edu.uob;

public class OXOController {
    OXOModel gameModel;

    public OXOController(OXOModel model) {
        gameModel = model;
    }

    public void handleIncomingCommand(String command) throws OXOMoveException {
        int currentplayer = gameModel.getCurrentPlayerNumber();
        OXOPlayer movingplayer = gameModel.getPlayerByNumber(currentplayer);
        int rowposition = Character.toLowerCase(command.charAt(0)) - 'a';
        int colposition = Character.getNumericValue(command.charAt(1)) - 1;
        if (gameModel.getWinner() == null) {
            gameModel.setCellOwner(rowposition, colposition, movingplayer);
        }
        //if player == the max number of players
        if (currentplayer == gameModel.getNumberOfPlayers() - 1) {
            currentplayer = 0;
            gameModel.setCurrentPlayerNumber(currentplayer);
        } else {
            gameModel.setCurrentPlayerNumber(currentplayer + 1);
        }
        if (checkForWin()) {
            gameModel.setWinner(movingplayer);
        }
        if (checkForDraw()) {
            gameModel.setGameDrawn();
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


    public boolean checkForDraw() {
        int rowCount = gameModel.getNumberOfRows();
        int colCount = gameModel.getNumberOfColumns();
        int emptyCells = 0;
        for (int i = 0; i < rowCount; i++) {
            for (int j = 0; j < colCount; j++) {
                if (gameModel.getCellOwner(i, j) == null) {
                    emptyCells++;
                }
            }
        }
        //Returns true if there is a draw and false otherwise
        return emptyCells == 0;
    }


    public void increaseWinThreshold() {
    }

    public void decreaseWinThreshold() {
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
