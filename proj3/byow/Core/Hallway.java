package byow.Core;

import byow.TileEngine.TETile;
import byow.TileEngine.Tileset;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

public class Hallway {
    private Position corner;
    private String orientation;
    private int width;
    private int height;
    private TETile floor;
    private TETile wall;
    private HashMap<Position, TETile> tiles;
    private HashSet<Position> walls;
    private HashSet<Position> floors;
    private List<Room> neighbors;

    public Hallway(Position c, int w, int h, String o) {
        corner = c;
        orientation = o;
        width = w;
        height = h;
        floor = Tileset.FLOOR;
        wall = Tileset.WALL;
        tiles = new HashMap<>();
        walls = new HashSet<>();
        floors = new HashSet<>();
    }

    public void makeTurning() {
        if (orientation.equals("ul")) {
            upperLeft();

        } else if (orientation.equals("ur")) {
            upperRight();

        } else if (orientation.equals("ll")) {
            lowerLeft();

        } else if (orientation.equals("lr")) {
            lowerRight();

        }
    }

    public void upperLeft() {
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < 3; y++) {
                Position p = new Position(corner.getX() + x, corner.getY() - y);
                if (x == width - 1 && y == 1) {
                    Position p2 = new Position(corner.getX() + x + 1, corner.getY() - y);
                    tiles.put(p, floor);
                    tiles.put(p2, floor);
                    floors.add(p);
                    floors.add(p2);

                } else if (x == 1 && y == 2) {
                    Position p2 = new Position(corner.getX() + x, corner.getY() - y - 1);
                    tiles.put(p, floor);
                    tiles.put(p2, floor);
                    floors.add(p);
                    floors.add(p2);
                } else if (x == 0 || x == width - 1 || y == 0 || y == 2) {
                    tiles.put(p, wall);
                    walls.add(p);
                } else {
                    tiles.put(p, floor);
                    floors.add(p);
                }
            }
        }
        for (int x = 0; x < 3; x++) {
            for (int y = 0; y < height; y++) {
                Position p = new Position(corner.getX() + x, corner.getY() - y);
                if (tiles.containsKey(p)) {
                    continue;
                }
                if (y == height - 1 && x == 1) {
                    Position p2 = new Position(corner.getX() + x, corner.getY() - y - 1);
                    tiles.put(p, floor);
                    tiles.put(p2, floor);
                    floors.add(p);
                    floors.add(p2);
                } else if (x == 0 || x == 2 || y == 0 || y == height - 1) {
                    tiles.put(p, wall);
                    walls.add(p);
                } else {
                    tiles.put(p, floor);
                    floors.add(p);
                }
            }
        }

    }

    public void upperRight() {
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < 3; y++) {
                Position p = new Position(corner.getX() - x, corner.getY() - y);
                if (x == 1 && y == 2) {
                    Position p2 = new Position(corner.getX() - x, corner.getY() - y - 1);
                    tiles.put(p, floor);
                    tiles.put(p2, floor);
                    floors.add(p);
                    floors.add(p2);
                } else if (x == width - 1 && y == 1) {
                    Position p2 = new Position(corner.getX() - x - 1, corner.getY() - y);
                    tiles.put(p, floor);
                    tiles.put(p2, floor);
                    floors.add(p);
                    floors.add(p2);
                } else if (x == 0 || x == width - 1 || y == 0 || y == 2) {
                    tiles.put(p, wall);
                    walls.add(p);
                } else {
                    tiles.put(p, floor);
                    floors.add(p);
                }
            }
        }
        for (int x = 0; x < 3; x++) {
            for (int y = 0; y < height; y++) {
                Position p = new Position(corner.getX() - x, corner.getY() - y);
                if (tiles.containsKey(p)) {
                    continue;
                }
                if (x == 1 && y == height - 1) {
                    Position p2 = new Position(corner.getX() - x, corner.getY() - y - 1);
                    tiles.put(p, floor);
                    tiles.put(p2, floor);
                    floors.add(p);
                    floors.add(p2);
                } else if (x == 0 || x == 2 || y == 0 || y == height - 1) {
                    tiles.put(p, wall);
                    walls.add(p);
                } else {
                    tiles.put(p, floor);
                    floors.add(p);
                }
            }
        }

    }

    public void lowerLeft() {
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < 3; y++) {
                Position p = new Position(corner.getX() + x, corner.getY() + y);
                if (y == 1 && x == width - 1) {
                    Position p2 = new Position(corner.getX() + x + 1, corner.getY() + y);
                    tiles.put(p, floor);
                    tiles.put(p2, floor);
                    floors.add(p);
                    floors.add(p2);
                } else if (x == 1 && y == height - 1) {
                    Position p2 = new Position(corner.getX() + x, corner.getY() + y + 1);
                    tiles.put(p, floor);
                    tiles.put(p2, floor);
                    floors.add(p);
                    floors.add(p2);
                } else if (x == 0 || x == width - 1 || y == 0 || y == 2) {
                    tiles.put(p, wall);
                    walls.add(p);
                } else {
                    tiles.put(p, floor);
                    floors.add(p);
                }
            }
        }
        for (int x = 0; x < 3; x++) {
            for (int y = 0; y < height; y++) {
                Position p = new Position(corner.getX() + x, corner.getY() + y);
                if (tiles.containsKey(p)) {
                    continue;
                }
                if (x == 1 &&  y == height - 1) {
                    Position p2 = new Position(corner.getX() + x, corner.getY() + y + 1);
                    tiles.put(p, floor);
                    tiles.put(p2, floor);
                    floors.add(p);
                    floors.add(p2);
                } else if (x == 0 || x == 2 || y == 0 || y == height - 1) {
                    tiles.put(p, wall);
                    walls.add(p);
                } else {
                    tiles.put(p, floor);
                    floors.add(p);
                }
            }
        }

    }

    public void lowerRight() {
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < 3; y++) {
                Position p = new Position(corner.getX() - x, corner.getY() + y);
                if (x == width - 1 && y == 1) {
                    Position p2 = new Position(corner.getX() - x - 1, corner.getY() + y);
                    tiles.put(p, floor);
                    tiles.put(p2, floor);
                    floors.add(p);
                    floors.add(p2);
                } else if (x == 1 && y == 2) {
                    Position p2 = new Position(corner.getX() + x, corner.getY() + y + 1);
                    tiles.put(p, floor);
                    tiles.put(p2, floor);
                    floors.add(p);
                    floors.add(p2);
                } else if (x == 0 || x == width - 1 || y == 0 || y == 2) {
                    tiles.put(p, wall);
                    walls.add(p);
                } else {
                    tiles.put(p, floor);
                    floors.add(p);
                }
            }
        }
        for (int x = 0; x < 3; x++) {
            for (int y = 0; y < height; y++) {
                Position p = new Position(corner.getX() - x, corner.getY() + y);
                if (tiles.containsKey(p)) {
                    continue;
                }
                if (x == 1  && y == height - 1) {
                    Position p2 = new Position(corner.getX() - x, corner.getY() + y + 1);
                    tiles.put(p, floor);
                    tiles.put(p2, floor);
                    floors.add(p);
                    floors.add(p2);
                } else if (x == 0 || x == 2 || y == 0 || y == height - 1) {
                    tiles.put(p, wall);
                    walls.add(p);
                } else {
                    tiles.put(p, floor);
                    floors.add(p);
                }
            }
        }

    }

    public HashMap<Position, TETile> getTiles() {
        return tiles;
    }

    public HashSet<Position> getWalls() {
        return walls;
    }

    public HashSet<Position> getFloors() {
        return floors;
    }
}
