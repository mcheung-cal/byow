package byow.Core;



import byow.TileEngine.TETile;
import byow.TileEngine.Tileset;
import edu.princeton.cs.algs4.Stopwatch;

import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;
import java.util.Random;

public class MapGenerator {
    private static final int WIDTH = 80;
    private static final int HEIGHT = 40;
    private long seed;
    private List<Room> rooms;
    private HashMap<Position, TETile> tiles;
    private HashMap<Position, Position> floors;
    private List<Hallway> hallways;
    private int[][] graph;
    private TETile[][] world;
    private TETile[][] visible;
    private Player player;
    boolean changeNew;
    boolean changeOld;

    public MapGenerator(long s) {
        seed = s;
        rooms = new ArrayList<>();
        tiles = new HashMap<>();
        floors = new HashMap<>();
        hallways = new ArrayList<>();
        world = new TETile[WIDTH][HEIGHT];
        visible = new TETile[WIDTH][HEIGHT];

        for (int x = 0; x < WIDTH; x += 1) {
            for (int y = 0; y < HEIGHT; y += 1) {
                world[x][y] = Tileset.NOTHING;
                visible[x][y] = Tileset.NOTHING;
            }
        }

        buildRooms();
        buildHallways();
    }

    public TETile[][] getWorld() {
        return world;
    }

    public TETile[][] getVisible() {
        return visible;
    }

    public Player getPlayer() {
        return this.player;
    }

    public int getMapHeight() {
        return HEIGHT;
    }

    public int getMapWidth() {
        return WIDTH;
    }

    public void buildRooms() {
        Random random = new Random(seed);
        int count = 0;
        int bound = 3;
        int widthBound = WIDTH / bound;
        int heightBound = HEIGHT / bound;
        int fail = 0;
        double timeout = 0.5;
        Stopwatch sw = new Stopwatch();
        while (count < 15) {
            int x = RandomUtils.uniform(random, 0, WIDTH);
            int y = RandomUtils.uniform(random, 0, HEIGHT);
            int w = RandomUtils.uniform(random, 2, widthBound);
            int h = RandomUtils.uniform(random, 2, heightBound);
            Room r = new Room(new Position(x, y), w, h, count);
            if (fail > 40000 && widthBound > 11  &&  heightBound > 10) {
                fail = 0;
                bound++;
                widthBound -= 5;
                heightBound -= 5;
            } else if (fail > 40000 && widthBound > 11) {
                fail = 0;
                bound++;
                widthBound -= 5;
            }
            if (sw.elapsedTime() >= timeout) {
                count = 25;
            }

            if (x + w < WIDTH && y - h > 0 && y < HEIGHT && h >= 4
                    && w >= 4 && !r.overlap(rooms)) {
                rooms.add(r);
                r.addTiles();
                count++;

                if (count == 1) {
                    player = new Player(r.getCenter());
                }
            } else {
                fail++;
            }
        }

        for (Room room : rooms) {
            for (HashMap.Entry<Position, TETile> t : room.getTiles().entrySet()) {
                world[t.getKey().getX()][t.getKey().getY()] = t.getValue();
                if (t.getValue().equals(Tileset.FLOOR)) {
                    floors.put(t.getKey(), t.getKey());
                }
                tiles.put(t.getKey(), t.getValue());
            }
        }

    }

    public void buildHallways() {
        graph = new int[rooms.size()][rooms.size()];
        for (Room r : rooms) {
            List<Room> neighbors = r.createNeighbors(rooms);
            for (int i = 0; i < neighbors.size(); i++) {
                if (neighbors.get(i) != null) {
                    Room n = neighbors.get(i);
                    if (graph[r.getCount()][n.getCount()] != 1
                            && graph[n.getCount()][r.getCount()] != 1) {
                        graph[r.getCount()][n.getCount()] = 1;
                        graph[n.getCount()][r.getCount()] = 1;
                        Hallway h = r.makeHallway(n, i);
                        hallways.add(h);
                        if (h != null) {
                            h.makeTurning();
                            for (HashMap.Entry<Position, TETile> t : h.getTiles().entrySet()) {
                                if (t.getKey().getX() >= WIDTH || t.getKey().getY() >= HEIGHT) {
                                    continue;
                                } else if (world[t.getKey().getX()][t.getKey().getY()]
                                        == Tileset.FLOOR && h.getWalls().contains(t.getKey())) {
                                    continue;
                                } else {
                                    world[t.getKey().getX()][t.getKey().getY()] = t.getValue();
                                }
                            }
                        }
                    }
                }
            }
        }

        for (Hallway h : hallways) {
            for (Position p : h.getFloors()) {
                int x = p.getX();
                int y = p.getY();
                if (x >= WIDTH || y >= HEIGHT) {
                    continue;
                }
                if (x < 0 || x >= WIDTH || y < 0
                        || y >= HEIGHT || !world[x][y].equals(Tileset.FLOOR)) {
                    continue;
                }
                if (x - 1 >= 0 && x - 1 < WIDTH && y < HEIGHT && y >= 0
                        && world[x - 1][y].equals(Tileset.NOTHING)) {
                    world[x][y] = Tileset.WALL;
                } else if (x + 1 < WIDTH && x + 1 >= 0 && y < HEIGHT && y >= 0
                        && world[x + 1][y].equals(Tileset.NOTHING)) {
                    world[x][y] = Tileset.WALL;
                } else if (y - 1 >= 0 && y - 1 < HEIGHT && x >= 0 && x < WIDTH
                        && world[x][y - 1].equals(Tileset.NOTHING)) {
                    world[x][y] = Tileset.WALL;
                } else if (y + 1 < HEIGHT && y + 1 >= 0 && x >= 0 && x < WIDTH
                        && world[x][y + 1].equals(Tileset.NOTHING)) {
                    world[x][y] = Tileset.WALL;
                } else if (x == 0  || x == WIDTH - 1 || y == 0 || y == HEIGHT - 1) {
                    world[x][y] = Tileset.WALL;
                }
            }
        }

        world[player.getPosition().getX()][player.getPosition().getY()] = player.getAvatar();
    }

    public boolean updateMap(Position p1, Position p2, TETile t1, TETile t2) {
        int x1 = p1.getX();
        int y1 = p1.getY();
        int x2 = p2.getX();
        int y2 = p2.getY();
        if (x1 < 0 || x1 >= WIDTH || x2 < 0 || x2 >= WIDTH
                || y1 < 0 || y2 < 0 || y1 >= HEIGHT || y2 >=  HEIGHT) {
            return false;
        }
        if (t2 == Tileset.WALL || t2 == Tileset.NOTHING) {
            return false;
        }
        world[x1][y1] = t2;
        world[x2][y2] = t1;
        return true;
    }

    public void loadMap(Position p, TETile t) {
        int x = p.getX();
        int y = p.getY();
        world[x][y] = t;
    }

    public void changeTile(Position p, TETile t) {
        int x = p.getX();
        int y = p.getY();
        if (x >= 0 && x < WIDTH &&  y >= 0 && y < HEIGHT) {
            world[x][y] = t;
        }
    }

    public void updateVisible(Position pOld, Position pNew) {
        int x1 = pOld.getX();
        int y1 = pOld.getY();
        int x2 = pNew.getX();
        int y2 = pNew.getY();
        changeNew = false;
        changeOld = false;

        if (y2 > y1) {
            up(y1, x2, y2);

        } else if (y2 < y1) {
            down(y1, x2, y2);

        } else if (x2 < x1) {
            left(x1, x2, y2);
        } else if (x2 > x1) {
            right(x1, x2, y2);

        }
        visible[x1][y1] = world[x1][y1];
        visible[x2][y2] = player.getAvatar();
    }

    public void initializeVisible() {
        int x = player.getPosition().getX();
        int y = player.getPosition().getY();
        for (int i = x - player.getLineOfSight(); i <= x + player.getLineOfSight(); i++) {
            for (int j = y - player.getLineOfSight(); j <= y + player.getLineOfSight(); j++) {
                if (i >= 0 && i < WIDTH && j >= 0 && j < HEIGHT) {
                    visible[i][j] = world[i][j];
                }
            }
        }
    }

    public void up(int y1, int x2, int y2) {
        if (y2 + player.getLineOfSight() < HEIGHT) {
            changeNew = true;
        }
        if (y1 - player.getLineOfSight() >= 0) {
            changeOld = true;
        }
        for (int i = 0; i <= player.getLineOfSight(); i++) {
            if (x2 + i < WIDTH) {
                if (changeOld) {
                    visible[x2 + i][y1 - player.getLineOfSight()] = Tileset.NOTHING;
                }
                if (changeNew) {
                    visible[x2 + i][y2 + player.getLineOfSight()]
                            = world[x2 + i][y2 + player.getLineOfSight()];
                }
            }
            if (x2 - i >= 0) {
                if (changeOld) {
                    visible[x2 - i][y1 - player.getLineOfSight()] = Tileset.NOTHING;
                }
                if (changeNew) {
                    visible[x2 - i][y2 + player.getLineOfSight()]
                            = world[x2 - i][y2 + player.getLineOfSight()];
                }
            }
        }
    }

    public void down(int y1, int x2, int y2) {
        if (y2 - player.getLineOfSight() >= 0) {
            changeNew = true;
        }
        if (y1 + player.getLineOfSight() < HEIGHT) {
            changeOld = true;
        }
        for (int i = 0; i <= player.getLineOfSight(); i++) {
            if (x2 + i < WIDTH) {
                if (changeOld) {
                    visible[x2 + i][y1 + player.getLineOfSight()] = Tileset.NOTHING;
                }
                if (changeNew) {
                    visible[x2 + i][y2 - player.getLineOfSight()]
                            = world[x2 + i][y2 - player.getLineOfSight()];
                }
            }
            if (x2 - i >= 0) {
                if (changeOld) {
                    visible[x2 - i][y1 + player.getLineOfSight()] = Tileset.NOTHING;
                }
                if (changeNew) {
                    visible[x2 - i][y2 - player.getLineOfSight()]
                            = world[x2 - i][y2 - player.getLineOfSight()];
                }
            }
        }
    }

    public void left(int x1, int x2, int y2) {
        if (x2 - player.getLineOfSight() >= 0) {
            changeNew = true;
        }
        if (x1 + player.getLineOfSight() < WIDTH) {
            changeOld = true;
        }
        for (int i = 0; i <= player.getLineOfSight(); i++) {
            if (y2 + i < HEIGHT) {
                if (changeOld) {
                    visible[x1 + player.getLineOfSight()][y2 + i] = Tileset.NOTHING;
                }
                if (changeNew) {
                    visible[x2 - player.getLineOfSight()][y2 + i]
                            = world[x2 - player.getLineOfSight()][y2 + i];
                }
            }
            if (y2 - i >= 0) {
                if (changeOld) {
                    visible[x1 + player.getLineOfSight()][y2 - i] = Tileset.NOTHING;
                }
                if (changeNew) {
                    visible[x2 - player.getLineOfSight()][y2 - i]
                            = world[x2 - player.getLineOfSight()][y2 - i];
                }
            }
        }
    }

    public void right(int x1, int x2, int y2) {
        if (x2 + player.getLineOfSight() < WIDTH) {
            changeNew = true;
        }
        if (x1 - player.getLineOfSight() >= 0) {
            changeOld = true;
        }
        for (int i = 0; i <= player.getLineOfSight(); i++) {
            if (y2 + i < HEIGHT) {
                if (changeOld) {
                    visible[x1 - player.getLineOfSight()][y2 + i] = Tileset.NOTHING;
                }
                if (changeNew) {
                    visible[x2 + player.getLineOfSight()][y2 + i]
                            = world[x2 + player.getLineOfSight()][y2 + i];
                }
            }
            if (y2 - i >= 0) {
                if (changeOld) {
                    visible[x1 - player.getLineOfSight()][y2 - i] = Tileset.NOTHING;
                }
                if (changeNew) {
                    visible[x2 + player.getLineOfSight()][y2 - i]
                            = world[x2 + player.getLineOfSight()][y2 - i];
                }
            }
        }
    }
}

