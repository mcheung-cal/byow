package byow.Core;

import byow.TileEngine.TETile;
import byow.TileEngine.Tileset;

import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;
import java.util.HashSet;


public class Room {
    private Position upperLeft;
    private Position upperRight;
    private Position lowerLeft;
    private Position lowerRight;
    private Position center;
    private int width;
    private int height;
    private int count;
    private TETile floor = Tileset.FLOOR;
    private TETile wall = Tileset.WALL;
    private HashSet<Position> walls;
    private HashSet<Position> floors;
    private HashMap<Position, TETile> tiles;
    private List<Room> overlapLeft;
    private List<Room> overlapRight;
    private List<Room> overlapTop;
    private List<Room> overlapBottom;
    private List<Room> neighbors; //left right top bottom



    public Room(Position uL, int w, int h, int c) {
        upperLeft = uL;
        upperRight = new Position(upperLeft.getX() + w, upperLeft.getY());
        lowerLeft = new Position(upperLeft.getX(), upperLeft.getY() - h);
        lowerRight = new Position(upperLeft.getX() + w, upperLeft.getY() - h);
        center = new Position((uL.getX() + uL.getX() + w) / 2, (uL.getY() + (uL.getY() - h)) / 2);
        width = w;
        height = h;
        count = c;
        walls = new HashSet<>();
        floors = new HashSet<>();
        tiles = new HashMap<>();
        overlapLeft = new ArrayList<>();
        overlapRight = new ArrayList<>();
        overlapBottom = new ArrayList<>();
        overlapTop = new ArrayList<>();
    }

    public boolean overlap(List<Room> rooms) {
        boolean overlapX;
        boolean overlapY;
        for (Room r : rooms) {
            if (this.upperRight.getX() + 3 < r.upperLeft.getX()) {
                overlapX = false;
            } else if (this.upperLeft.getX() > r.upperRight.getX() + 3) {
                overlapX = false;
            } else {
                overlapX = true;
            }
            if (this.lowerLeft.getY() > r.upperLeft.getY() + 3) {
                overlapY = false;
            } else if (this.upperLeft.getY() + 3 < r.lowerLeft.getY()) {
                overlapY = false;
            } else {
                overlapY = true;
            }
            if (overlapX && overlapY) {
                return true;
            }
        }
        return false;
    }

    public void addTiles() {
        for (int x = upperLeft.getX(); x <=  upperRight.getX(); x++) {
            for (int y = upperRight.getY(); y >= lowerRight.getY(); y--) {
                Position p = new Position(x, y);
                if (x == upperLeft.getX() || x  == upperRight.getX()
                        || y == upperRight.getY() || y == lowerRight.getY()) {
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
        return this.tiles;
    }

    public HashSet<Position> getWalls() {
        return walls;
    }

    public HashSet<Position> getFloors() {
        return floors;
    }

    public List<Room> createNeighbors(List<Room> rooms) {
        neighbors = new ArrayList<>();
        boolean overlapX;
        boolean overlapY;
        for (Room r : rooms) {
            if (!r.equals(this)) {
                if (this.upperRight.getX() + 3 < r.upperLeft.getX()) {
                    overlapX = false;
                } else if (this.upperLeft.getX() > r.upperRight.getX() + 3) {
                    overlapX = false;
                } else {
                    overlapX = true;
                }
                if (this.lowerLeft.getY() > r.upperLeft.getY() + 3) {
                    overlapY = false;
                } else if (this.upperLeft.getY() + 3 < r.lowerLeft.getY()) {
                    overlapY = false;
                } else {
                    overlapY = true;
                }
                if (overlapX && overlapY) {
                    continue;
                } else if (overlapY) {
                    if (this.upperRight.getX() < r.lowerLeft.getX()) {
                        overlapRight.add(r);
                    } else if (this.upperLeft.getX() > r.upperRight.getX()) {
                        overlapLeft.add(r);
                    }
                } else if (overlapX) {
                    if (this.lowerLeft.getY() > r.upperLeft.getY()) {
                        overlapBottom.add(r);
                    } else if (this.upperLeft.getY() < r.lowerLeft.getY()) {
                        overlapTop.add(r);
                    }
                }
            }
        }
        neighbors.add(getNearest(overlapLeft));
        neighbors.add(getNearest(overlapRight));
        neighbors.add(getNearest(overlapTop));
        neighbors.add(getNearest(overlapBottom));
        return neighbors;
    }

    private Room getNearest(List<Room> rooms) {
        if (rooms.size() == 0) {
            return null;
        } else if  (rooms.size() == 1) {
            return rooms.get(0);
        }
        double nearest = Double.POSITIVE_INFINITY;
        Room closest = null;
        double temp;
        double x2;
        double y2;
        for (Room r : rooms) {
            x2 = Math.pow(r.center.getX() - this.center.getX(), 2);
            y2 = Math.pow(r.center.getX() - this.center.getX(), 2);
            temp = Math.pow(x2 + y2, 0.5);
            if (temp < nearest) {
                nearest = temp;
                closest = r;
            }
        }
        return closest;
    }

    public Hallway makeHallway(Room r, int c) {
        //left
        if (c == 0) {
            if (r.center.getY() > this.upperLeft.getY()) {
                Position p = new Position(r.center.getX(), this.center.getY());
                return new Hallway(p, this.upperLeft.getX() - r.center.getX(),
                        r.lowerLeft.getY() - this.center.getY(), "ll"); //change o later on
            } else {
                Position p = new Position(this.center.getX(), r.center.getY());
                return new Hallway(p, this.center.getX() - r.upperRight.getX(),
                        this.lowerRight.getY() -  r.center.getY(), "lr"); //change o later on
            }

        } else if (c == 1) { //right
//            System.out.println("Error");
            if (this.center.getY() > r.upperLeft.getY()) {
                Position p = new Position(r.center.getX(), this.center.getY());
                return new Hallway(p, r.center.getX() - this.upperRight.getX(),
                        this.center.getY() - r.upperLeft.getY(), "ur"); //change o later o
            } else {
                Position p = new Position(this.center.getX(), r.center.getY());
                return new Hallway(p, r.upperRight.getX() - this.center.getX(),
                        r.center.getY() - this.upperRight.getY(), "ul"); //change o later on
            }
        } else if (c == 2) { //top
            if (this.center.getX() > r.upperRight.getX()) {
                Position p = new Position(r.center.getX(), this.center.getY());
                return new Hallway(p, this.upperLeft.getX() - r.center.getX(),
                        r.lowerLeft.getY() - this.center.getY(), "ll"); //change o later on
            } else {
                Position p = new Position(this.center.getX(), r.center.getY());
                return new Hallway(p, r.upperLeft.getX() - this.center.getX(),
                        r.center.getY() - this.upperLeft.getY(), "ul"); //change o later on
            }
        } else if (c == 3) { //bottom
            if (this.center.getX() > r.upperRight.getX()) {
                Position p = new Position(this.center.getX(), r.center.getY());
                return new Hallway(p, this.center.getX() - r.lowerRight.getX(),
                        this.lowerLeft.getY() - r.center.getY(), "lr"); //change o later on
            } else {
                Position p = new Position(r.center.getX(), this.center.getY());
                return new Hallway(p, r.center.getX() - this.upperRight.getX(),
                        this.center.getY() - r.upperLeft.getY(), "ur"); //change o later o
            }
        }

        return null;
    }

    public int getCount() {
        return count;
    }

    public Position getUpperLeft() {
        return this.upperLeft;
    }

    public Position getCenter() {
        return this.center;
    }
}
