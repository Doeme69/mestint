//doeme,yewwre12@gmail.com

import java.util.*;

import game.racetrack.Direction;
import game.racetrack.RaceTrackGame;
import game.racetrack.RaceTrackPlayer;
import game.racetrack.utils.Cell;
import game.racetrack.utils.Coin;
import game.racetrack.utils.PathCell;
import game.racetrack.utils.PlayerState;

import static game.racetrack.RaceTrackGame.*;

public class Agent extends RaceTrackPlayer {

    public Agent(PlayerState state, Random random, int[][] track, Coin[] coins, int color) {
        super(state, random, track, coins, color);
    }

    int pickedUpCoin = 0;
    List<Coin> coinvalues = Arrays.asList(coins);

    Cell[] coinPlaces = new Cell[coinvalues.size()];
    LinkedList<PathCell> path = new LinkedList<PathCell>();

    List<Cell> visitedCells = new LinkedList<>();

    int tries;

    @Override
    public Direction getDirection(long remainingTime) {

        Comparator<Coin> valueComparator = Comparator.comparing(coin -> coin.value);
        coinvalues.sort(valueComparator.reversed());

        for (int i = 0; i < coinvalues.size(); i++) {
            coinPlaces[i] = coinvalues.get(i);
        }

        if (visitedCells.size() > 1) {
            visitedCells.add(new Cell(state.i, state.j));
            visitedCells.addAll(line4connect(visitedCells.get(visitedCells.size() - 2), visitedCells.get(visitedCells.size() - 1)));
            visitedCells.removeIf(record -> Collections.frequency(visitedCells, record) > 1);
        } else {
            visitedCells.add(new Cell(state.i, state.j));
        }

        if (pickedUpCoin < 2) {
            return getToCoin();
        } else {
            return getToFinish();
        }
    }

    public Direction getToCoin() {
        List<PathCell> coinpath = BFSCoin(state.i, state.j, track);

        if (visitedCells.contains(coinpath.get(0))) {
            tries++;
        }
        if (tries > 4) {
            tries = 0;
            List<PathCell> newPath = RaceTrackGame.BFS(state.i, state.j, track);
            return new Direction(newPath.get(1).i - newPath.get(0).i, newPath.get(1).j - newPath.get(0).j);
        }

        if (coinpath.size() < state.vi || coinpath.size() < state.vj) {
            return DIRECTIONS[0];
        }

        for (int i = 0; i < coins.length; i++) {
            if (line4connect(coinpath.get(1), coinpath.get(0)).contains(coinPlaces[i])) {
                if (line4connect(visitedCells.get(visitedCells.size() - 2), visitedCells.get(visitedCells.size() - 1)).contains(coinPlaces[i]) || coinPlaces[i].i == state.i && coinPlaces[i].j == state.j) {
                    pickedUpCoin++;
                }
            }
        }
        return new Direction(coinpath.get(1).i - coinpath.get(0).i, coinpath.get(1).j - coinpath.get(0).j);
    }

    public Direction getToFinish() {
        List<PathCell> finnishpath = RaceTrackGame.BFS(state.i, state.j, track);
        if (visitedCells.contains(finnishpath.get(0))) {
            tries++;
        }
        if (tries > 4) {
            tries = 0;
            return getToCoin();
        }

        return new Direction(finnishpath.get(1).i - finnishpath.get(0).i, finnishpath.get(1).j - finnishpath.get(0).j);
    }

    public List<PathCell> BFSCoin(int i, int j, int[][] track) {
        LinkedList<PathCell> open = new LinkedList<PathCell>();
        LinkedList<PathCell> close = new LinkedList<PathCell>();
        PathCell current = new PathCell(state.i, state.j, null);
        open.add(current);
        while (!open.isEmpty()) {
            current = open.pollFirst();
            if (new Cell(current.i, current.j).equals(coinPlaces[pickedUpCoin])) {
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
