package edu.uob;

public class OXOController {
    OXOModel gameModel;

    public OXOController(OXOModel model) {
        gameModel = model;
    }

    public void handleIncomingCommand(String command) throws OXOMoveException {
        int currentplayer = gameModel.getCurrentPlayerNumber();
        OXOPlayer player = gameModel.getPlayerByNumber(currentplayer);
        int rowposition = Character.toLowerCase(command.charAt(0)) - 'a';
        int colposition = Character.getNumericValue(command.charAt(1)) - 1;
        gameModel.setCellOwner(rowposition, colposition,player);
        //if player == the max number of players
        if (currentplayer == gameModel.getNumberOfPlayers() - 1)
        {
            currentplayer = 0;
            gameModel.setCurrentPlayerNumber(currentplayer);
        }
        else
        {
            gameModel.setCurrentPlayerNumber(currentplayer+1);
        }
    }
    public void addRow() {}
    public void removeRow() {}
    public void addColumn() {}
    public void removeColumn() {}
    public void increaseWinThreshold() {}
    public void decreaseWinThreshold() {}
    public void reset() {
        int rowcount;
        int colcount;
        for (rowcount = 0; rowcount < gameModel.getNumberOfRows();rowcount++)
        {
          for (colcount = 0; colcount < gameModel.getNumberOfColumns();colcount++){
              gameModel.setCellOwner(rowcount,colcount,null);
          }
        }
        gameModel.setCurrentPlayerNumber(0);
    }
}
