package edu.uob;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class OXOModel {

    public ArrayList<ArrayList<OXOPlayer>> cells;

    private OXOPlayer[] players;
    private int currentPlayerNumber;
    private OXOPlayer winner;
    private boolean gameDrawn;
    private int winThreshold;

    public OXOModel(int numberOfRows, int numberOfColumns, int winThresh) {
        winThreshold = winThresh;

        cells = new ArrayList<ArrayList<OXOPlayer>>(numberOfRows);
        for (int i = 0; i < numberOfRows; i++) {
            cells.add(new ArrayList<OXOPlayer>(numberOfColumns));
            for (int j = 0; j < numberOfColumns; j++) {
                cells.get(i).add(null);
            }
        }

        players = new OXOPlayer[2];
    }

    public int getNumberOfPlayers() {
        return players.length;
    }

    public void addPlayer(OXOPlayer player) {
        for (int i = 0; i < players.length; i++) {
            if (players[i] == null) {
                players[i] = player;
                return;
            }
        }
    }

    public OXOPlayer getPlayerByNumber(int number) {
        return players[number];
    }

    public OXOPlayer getWinner() {
        return winner;
    }

    public void setWinner(OXOPlayer player) {
        winner = player;
    }

    public int getCurrentPlayerNumber() {
        return currentPlayerNumber;
    }

    public void setCurrentPlayerNumber(int playerNumber) {
        currentPlayerNumber = playerNumber;
    }

    public int getNumberOfRows() {
        return cells.size();
    }

    public int getNumberOfColumns() {
        return cells.get(0).size();
    }

    public void addRow() {
        int numberOfColumns = cells.get(0).size();
        cells.add(new ArrayList<OXOPlayer>(numberOfColumns));
        for (int j = 0; j < numberOfColumns; j++) {
            cells.get(cells.size() - 1).add(null);
        }
    }

    public void addColumn() {
        for (int i = 0; i < cells.size(); i++) {
            cells.get(i).add(null);
        }
    }

    public void removeRow() {
        if (cells.size() > 1) {
            int finalRow = cells.size() - 1;
            cells.remove(finalRow);
        }
    }

    public void removeColumn() {
        if (cells.get(0).size() > 1) {
            for (int i = 0; i < cells.size(); i++) {
                int finalCol = cells.get(i).size() - 1;
                cells.get(i).remove(finalCol);
            }
        }
    }


    public void removeRow(int rowIndex) {
        cells.remove(rowIndex);
    }

    public void removeColumn(int columnIndex) {
        for (int i = 0; i < cells.size(); i++) {
            cells.get(i).remove(columnIndex);
        }
    }


    public OXOPlayer getCellOwner(int rowNumber, int colNumber) {
        return cells.get(rowNumber).get(colNumber);
    }

    public void setCellOwner(int rowNumber, int colNumber, OXOPlayer player) {
        cells.get(rowNumber).set(colNumber, player);
    }

    public void setWinThreshold(int winThresh) {
        winThreshold = winThresh;
    }

    public int getWinThreshold() {
        return winThreshold;
    }

    public void setGameDrawn() {
        gameDrawn = true;
    }

    public void resetGameDraw() {
        gameDrawn = false;
    }

    public boolean isGameDrawn() {
        return gameDrawn;
    }

}
