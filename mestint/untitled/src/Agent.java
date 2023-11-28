//doeme,yewwre12@gmail.com

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

    //Kivonom a jelenlegi poziciobol a kovetkezot es a kulonbseget meglepi
    @Override
    public Direction getDirection(long remainingTime) {

        boolean gotCoin = false;

        RaceTrackPlayer player = null;

        List<PathCell> coinpath = BFSCoin(state.i, state.j, track);
        List<PathCell> finnishpath = RaceTrackGame.BFS(state.i, state.j, track);


        int finishxmenes = finnishpath.get(1).i - finnishpath.get(0).i;
        int finishymenes = finnishpath.get(1).j - finnishpath.get(0).j;


        int coinxmenes = coinpath.get(1).i - coinpath.get(0).i;
        int coinymenes = coinpath.get(1).j - coinpath.get(0).j;

        if (track[state.i][state.j] == COIN){
            System.out.println("KAKAKAKAKAKAKA");
        }


        return new Direction(coinxmenes, coinymenes);
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

    /*public static List<PathCell> AStar(int i, int j, int[][] track) {
        LinkedList<PathCell> path = new LinkedList<PathCell>();
        PriorityQueue<PathCell> open = new PriorityQueue<PathCell>();
        LinkedList<PathCell> close = new LinkedList<PathCell>();
        PathCell current = new PathCell(i, j, null);
        boolean foundCoin = false;
        open.add(current);
        while (!open.isEmpty()) {
            current = open.poll();
            if (mask(track[current.i][current.j], COIN)) {
                foundCoin = true;
            }
            if (mask(track[current.i][current.j], FINISH)) {
                break;
            }
            close.add(current);
            for (int idx = 0; idx < DIRECTIONS.length; idx++) {
                i = current.i + DIRECTIONS[idx].i;
                j = current.j + DIRECTIONS[idx].j;
                PathCell neighbor = new PathCell(i, j, current);
                if (isNotWall(i, j, track) && !close.contains(neighbor)) {
                    int g = current.g + 1;
                    int h = Math.abs(i - FINISH_I) + Math.abs(j - FINISH_J);
                    neighbor.g = g;
                    neighbor.h = h;
                    neighbor.f = g + h;
                    if (foundCoin && (mask(track[i][j], COIN) || mask(track[i][j], FINISH))) {
                        open.add(neighbor);
                    } else if (!foundCoin && !open.contains(neighbor)) {
                        open.add(neighbor);
                    }
                }
            }
        }
        while (current != null) {
            path.addFirst(current);
            current = current.parent;
        }
        return path;
    }
*/
}
