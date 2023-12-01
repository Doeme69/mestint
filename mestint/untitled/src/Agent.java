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
    List<Cell> pathSzakasz;
    int tries;

    @Override
    public Direction getDirection(long remainingTime) {

        updateVisitedCells();
        if (iteration == 0){
            updatePathSzakasz(coinpath.get(0));
            System.out.println(pathSzakasz);
        }

        iteration++;
        return new Direction(0, 0);

    }

    //ELindul, belerakom az elso elemet a listaba, kivonom a lista kovi elemebol a kezdopontot, az lesz a direction, majd a coinpath kovetkezo elemebol vonom ki a jelenlegi vegpontot
    //ha ez a kulonbseg megegyezik a jelenlegi directionnel akkor ez lesz az uj vegpont,
    //ha pedig ezek kulonbsege mas mint a direction akkor ez lesz a szakasz majd a vegpont lesz a kezdopontja a kovetkezo szakasznak
    public void updatePathSzakasz(Cell kezdopont) {
        Cell vegpont;
        Direction currentDir;
        pathSzakasz.add(kezdopont);

        pathSzakasz.set(0, kezdopont);

        for (int i = 0; i < coinpath.size(); i++) {
            vegpont = coinpath.get( i + 1 );

            currentDir = new Direction (vegpont.i - kezdopont.i, vegpont.j - kezdopont.j);

            if (currentDir.i == vegpont.i - pathSzakasz.get(1).i && currentDir.j == vegpont.j - pathSzakasz.get(1).j){
                pathSzakasz.set(1, vegpont);
                System.out.println(pathSzakasz);
            }

        }
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
/*
    public Direction getToCoin() {

        if (visitedCells.contains(coinpath.get(0))) {
            tries++;
        }
        if (tries > 3) {
            tries = 0;
            List<PathCell> newPath = RaceTrackGame.BFS(state.i, state.j, track);
            return new Direction(newPath.get(1).i - newPath.get(0).i, newPath.get(1).j - newPath.get(0).j);
        }

        if (line4connect(coinpath.get(1), coinpath.get(0)).contains(coinPlaces[pickedUpCoin])) {
            if (line4connect(visitedCells.get(visitedCells.size() - 2), visitedCells.get(visitedCells.size() - 1)).contains(coinPlaces[pickedUpCoin]) || coinPlaces[pickedUpCoin].i == state.i && coinPlaces[pickedUpCoin].j == state.j) {
                pickedUpCoin++;
            }
        }
        return new Direction(coinpath.get(1).i - coinpath.get(0).i, coinpath.get(1).j - coinpath.get(0).j);
    }

    public Direction getToFinish() {
        List<PathCell> finnishpath = RaceTrackGame.BFS(state.i, state.j, track);
        if (visitedCells.contains(finnishpath.get(0))) {
            tries++;
        }
        if (tries > 3) {
            tries = 0;
            pickedUpCoin++;
            List<PathCell> alternate = RaceTrackGame.BFS(state.i, state.j, track);
            pickedUpCoin--;
            return new Direction(alternate.get(1).i - alternate.get(0).i, alternate.get(1).j - alternate.get(0).j);
        }

        return new Direction(finnishpath.get(1).i - finnishpath.get(0).i, finnishpath.get(1).j - finnishpath.get(0).j);
    }






        return path;
    }
    public void move(RaceTrackPlayer player, Direction direction, int[][] track) {
        int i = player.state.i + player.state.vi + direction.i;
        int j = player.state.j + player.state.vj + direction.j;
        // check wall collision
        Cell cell = null;
        for (Cell c : line8connect(toCell(player), new Cell(i, j))) {
            if (isNotWall(c, track)) {
                cell = c;
            } else {
                direction = null;
                break;
            }
        }
        // wall collision has been occurred
        if (direction == null) {
            player.state.vi = cell.i - player.state.i;
            player.state.vj = cell.j - player.state.j;
            direction = new Direction(0, 0);
        }

    while (!isNeitherWall(line8connect(toCell(player), new Cell(i, j)), track)) {
      if (player.state.vi < 0) {
        player.state.vi += 1;
      } else if (0 < player.state.vi) {
        player.state.vi -= 1;
      } else if (player.state.vj < 0) {
        player.state.vj += 1;
      } else if (0 < player.state.vj) {
        player.state.vj -= 1;
      } else {
        direction = new Direction(0, 0);
      }
      i = player.state.i + player.state.vi + direction.i;
      j = player.state.j + player.state.vj + direction.j;
    }
        player.step(direction);
    }

 */