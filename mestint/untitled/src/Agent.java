//doeme,yewwre12@gmail.com

import java.nio.file.Path;
import java.util.LinkedList;
import java.util.PriorityQueue;
import java.util.Random;
import java.util.List;

import game.racetrack.Direction;
import game.racetrack.RaceTrackGame;
import game.racetrack.RaceTrackPlayer;
import game.racetrack.utils.Cell;
import game.racetrack.utils.Coin;
import game.racetrack.utils.PathCell;
import game.racetrack.utils.PlayerState;

import static game.racetrack.RaceTrackGame.*;

public class Agent extends RaceTrackPlayer{

    public Agent(PlayerState state, Random random, int[][] track, Coin[] coins, int color) {
        super(state, random, track, coins, color);
    }

    Cell start = new Cell(state.i, state.j);
    boolean gotCoins = false;

    @Override
    public Direction getDirection(long remainingTime) {

        if (!gotCoins) {
            return getToCoin();
        } else return getToFinish();

    }

    public Direction getToCoin(){
        List<PathCell> coinpath = BFSCoin(state.i, state.j, track);
        int coinxmenes;
        int coinymenes;

        coinxmenes = coinpath.get(1).i - coinpath.get(0).i;
        coinymenes = coinpath.get(1).j - coinpath.get(0).j;

        Cell[] coinplaces = coins;
        for (int i = 0; i < coins.length; i++) {
            if (line8connect(coinpath.get(0), coinpath.get(1)).contains(coins[i])) {
                List<PathCell> finnishpath = RaceTrackGame.BFS(state.i, state.j, track);
                coinxmenes = finnishpath.get(1).i - finnishpath.get(0).i;
                coinymenes = finnishpath.get(1).j - finnishpath.get(0).j;
                gotCoins = true;
            }
        }
        return new Direction(coinxmenes, coinymenes);
    }

    public Direction getToFinish(){
        List<PathCell> finnishpath = RaceTrackGame.BFS(state.i, state.j, track);

        int finishxmenes = finnishpath.get(1).i - finnishpath.get(0).i;
        int finishymenes = finnishpath.get(1).j - finnishpath.get(0).j;

        return new Direction(finishxmenes, finishymenes);
    }


    public List<PathCell> BFSCoin(int i, int j, int[][] track) {
        LinkedList<PathCell> path = new LinkedList<PathCell>();
        LinkedList<PathCell> open = new LinkedList<PathCell>();
        LinkedList<PathCell> close = new LinkedList<PathCell>();
        PathCell current = new PathCell(state.i, state.j, null);
        open.add(current);
        while (!open.isEmpty()) {
            current = open.pollFirst();
            if (mask(track[current.i][current.j], COIN)) {
                break;
            }
            close.add(current);
            for (int idx = 0; idx < DIRECTIONS.length; idx++) {
                i = current.i + DIRECTIONS[idx].i;
                j = current.j + DIRECTIONS[idx].j;
                PathCell neighbor = new PathCell(i, j, current);
                if (isNotWall(i, j, track) && !close.contains(neighbor) && !open.contains(neighbor)) {
                    open.add(neighbor);
                }
            }
        }
        while (current != null) {
            path.addFirst(current);
            current = current.parent;
        }
        return path;
    }
}
