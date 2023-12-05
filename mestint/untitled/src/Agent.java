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

    int iteration = 0;
    int pickedUpCoin = 1;
    List<Coin> coinvalues = Arrays.asList(coins);

    Cell[] coinPlaces = new Cell[coinvalues.size()];
    LinkedList<PathCell> path = new LinkedList<PathCell>();

    List<Cell> visitedCells = new LinkedList<>();
    List<PathCell> pathPath = BFSBestCoin(state.i, state.j, track);
    ArrayList<Cell> pathSzakasz = new ArrayList<Cell>();
    Direction currentDir;

    boolean shouldSlowDown = false;

    @Override
    public Direction getDirection(long remainingTime) {

        updateVisitedCells();

        if (iteration == 0) {
            sortCoinsByValue();
            pathSzakasz.add(new Cell(pathPath.get(0).i, pathPath.get(0).j));
            updatePathSzakasz(pathSzakasz.get(0));
            getMaxVelocity();
        }

        iteration++;

        //ha vi-re van a vegtol akkor ugy lassitson hogy a vi 0 lesz
        if ((state.i + state.vi == pathSzakasz.get(1).i && state.j + state.vj == pathSzakasz.get(1).j)) {
            shouldSlowDown = false;
            return new Direction(state.vi + state.vi * -2, state.vj + state.vj * -2);
        }

        //ha elerem a szakasz veget akkor update-elek
        if (state.i == pathSzakasz.get(1).i && state.j == pathSzakasz.get(1).j) {
            updatePathSzakasz(new Cell(state.i, state.j));
            getMaxVelocity();
            return new Direction(state.vi * -1, state.vj * -1);
        }

        //ha elerem azt a sebesseget amivel meg tudok meg allni idoben akkor lassulnia kell
        if ((Math.abs(state.vi) == getMaxVelocity().get(0) && state.vi != 0) || (Math.abs(state.vj) == getMaxVelocity().get(1) && state.vj != 0)) {
            shouldSlowDown = true;
        }

        //ha lassulnia kell akkor amig a velocityje nem 1 addig lassuljon
        if (shouldSlowDown) {
            if ((state.vi == 1 || state.vi == -1) || (state.vj == 1 || state.vj == -1)) {
                return new Direction(0, 0);
            } else
                return slowDown();
        }
        if (pathSzakasz.get(1).i == pathPath.get(pathPath.size() -1).i && pathSzakasz.get(1).j == pathPath.get(pathPath.size() -1).j){
            if (pickedUpCoin < 1) {
                pickedUpCoin++;
                pathPath = BFSBestCoin(state.i, state.j, track);
            } else pathPath = RaceTrackGame.BFS(state.i, state.j, track);
        }

        return currentDir;

    }

    //ELindul, belerakom az elso elemet a listaba, kivonom a lista kovi elemebol a kezdopontot, az lesz a direction, majd a coinpath kovetkezo elemebol vonom ki a jelenlegi vegpontot
    //ha ez a kulonbseg megegyezik a jelenlegi directionnel akkor ez lesz az uj vegpont,
    //ha pedig ezek kulonbsege mas mint a direction akkor ez lesz a szakasz majd a vegpont lesz a kezdopontja a kovetkezo szakasznak
    public void updatePathSzakasz(Cell kezdopont) {
        Cell vegpont;

        if (pathSzakasz.size() < 2) {
            pathSzakasz.add(kezdopont);
        } else {
            pathSzakasz.set(0, kezdopont);
        }
        vegpont = pathPath.get(pathPath.indexOf(kezdopont) + 1);
        currentDir = new Direction(vegpont.i - pathSzakasz.get(1).i, vegpont.j - pathSzakasz.get(1).j);

        for (int i = 1; i < pathPath.size(); i++) {
            vegpont = pathPath.get(pathPath.indexOf(kezdopont) + i);

            if (currentDir.i == vegpont.i - pathSzakasz.get(1).i && currentDir.j == vegpont.j - pathSzakasz.get(1).j) {
                pathSzakasz.set(1, vegpont);
            } else
                break;
        }
    }

    //ugy lassit hogy az irany ellentetet adja vissza
    public Direction slowDown() {
        return new Direction(currentDir.i * -1, currentDir.j * -1);
    }

    //kb csak a manhattan tavolsaga a szakasz elejenek es vegenek
    public Cell getDistance() {
        return new Cell(Math.abs(pathSzakasz.get(1).i - pathSzakasz.get(0).i), Math.abs(pathSzakasz.get(1).j - pathSzakasz.get(0).j));
    }

    public List<Integer> getMaxVelocity() {
        List<Integer> MaxVelocity = new ArrayList<>();

        int maxVelocityX = (int)Math.sqrt(getDistance().i);
        int maxVelocityY = (int)Math.sqrt(getDistance().j);
        MaxVelocity.add(maxVelocityX);
        MaxVelocity.add(maxVelocityY);

        return MaxVelocity;
    }

    public List<PathCell> BFSBestCoin(int i, int j, int[][] track) {
        LinkedList<PathCell> open = new LinkedList<PathCell>();
        LinkedList<PathCell> close = new LinkedList<PathCell>();
        PathCell current = new PathCell(state.i, state.j, null);
        open.add(current);
        while(!open.isEmpty()) {
            current = open.pollFirst();
            if (new Cell(current.i, current.j).equals(coinPlaces[0])) {
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
        while(current != null) {
            path.addFirst(current);
            current = current.parent;
        }
        return path;
    }

    private void sortCoinsByValue() {
        Comparator<Coin> valueComparator = Comparator.comparing(coin -> coin.value);
        coinvalues.sort(valueComparator.reversed());

        for (int i = 0; i < coinvalues.size(); i++) {
            coinPlaces[i] = coinvalues.get(i);
        }
    }

    public void updateVisitedCells() {
        if (visitedCells.size() > 1) {
            visitedCells.add(new Cell(state.i, state.j));
            visitedCells.addAll(line4connect(visitedCells.get(visitedCells.size() - 2), visitedCells.get(visitedCells.size() - 1)));
            visitedCells.removeIf(record -> Collections.frequency(visitedCells, record) > 1);
        } else {
            visitedCells.add(new Cell(state.i, state.j));
        }
    }
}
