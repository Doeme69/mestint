//doeme,yewwre12@gmail.com

import java.util.*;

import game.racetrack.Direction;
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
    int pickedUpCoin = 0;
    List<Coin> coinvalues = Arrays.asList(coins);

    Cell[] coinPlaces = new Cell[coinvalues.size()];
    LinkedList<PathCell> path = new LinkedList<PathCell>();

    List<Cell> visitedCells = new LinkedList<>();
    List<PathCell> coinpath = BFSBestCoin(state.i, state.j, track);
    List<Direction> directions;
    ArrayList<Cell> pathSzakasz = new ArrayList<Cell>();
    int tries;
    Direction currentDir;

    @Override
    public Direction getDirection(long remainingTime) {


        updateVisitedCells();


        if (iteration == 0){
            sortCoinsByValue();
            pathSzakasz.add(new Cell(coinpath.get(0).i, coinpath.get(0).j));
            updatePathSzakasz(pathSzakasz.get(0));
            getMaxVelocity();
        }
        iteration++;

        if (state.i == pathSzakasz.get(1).i && state.j == pathSzakasz.get(1).j) {
            updatePathSzakasz(new Cell(state.i, state.j));
            getMaxVelocity();
        }

        if (state.vi == getMaxVelocity() || state.vj == getMaxVelocity()){
            if ( state.vi != 1 && state.vj != 1){
                slowDown();
            }
        }

        return currentDir;
    }


    //ELindul, belerakom az elso elemet a listaba, kivonom a lista kovi elemebol a kezdopontot, az lesz a direction, majd a coinpath kovetkezo elemebol vonom ki a jelenlegi vegpontot
    //ha ez a kulonbseg megegyezik a jelenlegi directionnel akkor ez lesz az uj vegpont,
    //ha pedig ezek kulonbsege mas mint a direction akkor ez lesz a szakasz majd a vegpont lesz a kezdopontja a kovetkezo szakasznak
    public List<Cell> updatePathSzakasz(Cell kezdopont) {
        Cell vegpont;

        if (pathSzakasz.size() < 2) {
            pathSzakasz.add(kezdopont);
        } else {
            pathSzakasz.set(0, kezdopont);
        }
        vegpont = coinpath.get(coinpath.indexOf(kezdopont) + 1);
        currentDir = new Direction (vegpont.i - pathSzakasz.get(1).i, vegpont.j - pathSzakasz.get(1).j);

        for (int i = 1; i < coinpath.size(); i++) {
            vegpont = coinpath.get(coinpath.indexOf(kezdopont) + i);

            if (currentDir.i == vegpont.i - pathSzakasz.get(1).i && currentDir.j == vegpont.j - pathSzakasz.get(1).j){
                pathSzakasz.set(1, vegpont);
            }
            else break;
        }

        return pathSzakasz;
    }

    public Direction slowDown(){
        return new Direction(currentDir.i * -1, currentDir.j * -1);
    }

    public Cell getDistance() {
        return new Cell(Math.abs(pathSzakasz.get(1).i - pathSzakasz.get(0).i), Math.abs(pathSzakasz.get(1).j - pathSzakasz.get(0).j));
    }

    public int getMaxVelocity(){
        return Math.max((int) Math.sqrt(getDistance().i), (int) Math.sqrt(getDistance().j));
    }

    public List<PathCell> BFSBestCoin(int i, int j, int[][] track) {
        LinkedList<PathCell> open = new LinkedList<PathCell>();
        LinkedList<PathCell> close = new LinkedList<PathCell>();
        PathCell current = new PathCell(state.i, state.j, null);
        open.add(current);
        while(!open.isEmpty()) {
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