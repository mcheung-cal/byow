package byow.Core;

import byow.TileEngine.TERenderer;
import byow.TileEngine.TETile;
import byow.TileEngine.Tileset;
import edu.princeton.cs.introcs.StdDraw;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.FileWriter;

import java.awt.Color;
import java.awt.Font;
import java.util.Scanner;
/**
 * @source For java read/write files
 * https://www.w3schools.com/java/java_files_create.asp
 * https://www.w3schools.com/java/java_files_read.asp
 */
public class Engine {
    TERenderer ter = new TERenderer();
    /* Feel free to change the width and height. */
    public static final int WIDTH = 80;
    public static final int HEIGHT = 45;
    String absolutePath;
    long seed;
    boolean fromLoadFile;
    boolean gameOver;
    boolean limitedView;
    Position playerPos;
    char keyboardInput;
    String avatarName;

    /**
     * Method used for exploring a fresh world. This method should handle all inputs,
     * including inputs from the main menu.
     */
    public void interactWithKeyboard() {
        avatarName = "61b_student";
        gameOver = true;
        fromLoadFile = false;
        limitedView = false;
        Font oldFont = StdDraw.getFont();
        drawMenu();
        seed = 0;
        while (gameOver) {
            menuActions();
        }
        boolean update;
        String tile = "";
        StdDraw.setFont(oldFont);
        MapGenerator map = new MapGenerator(seed);
        Player p = map.getPlayer();
        if (fromLoadFile) {
            map.loadMap(p.getPosition(), Tileset.FLOOR);
            map.loadMap(playerPos, p.getAvatar());
            p.setPosition(playerPos);
        }
        playerPos = p.getPosition();
        map.initializeVisible();
        TETile[][] finalWorldFrame = map.getVisible();
        ter.renderFrame(finalWorldFrame);
        StdDraw.setPenColor(Color.WHITE);
        boolean possibleQuit = false;
        while (!gameOver) {
            tile = getTileString(map);
            StdDraw.setPenColor(Color.WHITE);
            StdDraw.text(10.0, HEIGHT - 1, tile);
            StdDraw.text(WIDTH / 2, HEIGHT - 1,  avatarName);
            StdDraw.show();
            if (limitedView) {
                ter.renderFrame(map.getVisible());
            } else {
                ter.renderFrame(map.getWorld());
            }
            if (StdDraw.hasNextKeyTyped()) {
                keyboardInput = Character.toUpperCase(StdDraw.nextKeyTyped());
                Position temp = p.updatePosition(keyboardInput);
                if (temp != null) {
                    TETile tileTemp = map.getWorld()[temp.getX()][temp.getY()];
                    update = map.updateMap(p.getPosition(), temp, p.getAvatar(), tileTemp);
                    if (update) {
                        map.updateVisible(p.getPosition(), temp);
                        p.setPosition(temp);
                        playerPos = p.getPosition();
                        if (limitedView) {
                            ter.renderFrame(map.getVisible());
                        } else {
                            ter.renderFrame(map.getWorld());
                        }
                    }
                }
                if (possibleQuit && keyboardInput == 'Q') {
                    saveQuit();
                    gameOver = true;
                    possibleQuit = false;
                    System.exit(0);
                }
                if (keyboardInput == ':') {
                    possibleQuit = true;
                }
            }
        }
    }


    /**
     * Method used for autograding and testing your code. The input string will be a series
     * of characters (for example, "n123sswwdasdassadwas", "n123sss:q", "lwww". The engine should
     * behave exactly as if the user typed these characters into the engine using
     * interactWithKeyboard.
     *
     * Recall that strings ending in ":q" should cause the game to quite save. For example,
     * if we do interactWithInputString("n123sss:q"), we expect the game to run the first
     * 7 commands (n123sss) and then quit and save. If we then do
     * interactWithInputString("l"), we should be back in the exact same state.
     *
     * In other words, both of these calls:
     *   - interactWithInputString("n123sss:q")
     *   - interactWithInputString("lww")
     *
     * should yield the exact same world state as:
     *   - interactWithInputString("n123sssww")
     *
     * @param input the input string to feed to your program
     * @return the 2D TETile[][] representing the state of the world
     */
    public TETile[][] interactWithInputString(String input) {
        char[] inputs = input.toCharArray();
        boolean newGame = false;
        boolean start = false;
        boolean possibleQuit = false;
        int count = 0;
        String temp = "";
        MapGenerator map = null;
        Player p = null;
        for (char c : inputs) {
            if (count == 0) {
                if (c == 'N' || c == 'n') {
                    newGame = true;
                } else if (c == 'L' || c == 'l') {
                    loadFile();
                    map = new MapGenerator(seed);
                    p = map.getPlayer();
                    map.changeTile(p.getPosition(), Tileset.FLOOR);
                    p.setPosition(playerPos);
                    map.loadMap(playerPos, Tileset.FLOOR);
                    map.loadMap(playerPos, p.getAvatar());
                    start = true;
                }
            } else if (start) {
                if (c == ':') {
                    possibleQuit = true;
                } else if (possibleQuit && (c == 'Q' || c == 'q')) {
                    saveQuit();
                    return map.getWorld();
                } else {
                    Position newPosTemp = p.updatePosition(Character.toUpperCase(c));
                    if (newPosTemp != null) {
                        TETile tileTemp = map.getWorld()[newPosTemp.getX()][newPosTemp.getY()];
                        boolean update = map.updateMap(p.getPosition(), newPosTemp,
                                p.getAvatar(), tileTemp);
                        if (update) {
                            p.setPosition(newPosTemp);
                            playerPos = p.getPosition();
                        }
                    }
                }
            } else if ((c == 'S' || c == 's') && temp.length() > 0 && newGame) {
                seed = Long.parseLong(temp);
                System.out.println(seed);
                map = new MapGenerator(seed);
                p = map.getPlayer();
                start = true;
            } else if (newGame) {
                temp += Character.toString(c);
            }
            count++;
        }
        return map.getWorld();
    }

    private String getTileString(MapGenerator map) {
        int mouseX = (int) StdDraw.mouseX();
        int mouseY = (int) StdDraw.mouseY();
        //HUD
        String tile = "Current tile: ";
        if (mouseX >= 0 && mouseX < map.getMapWidth() && mouseY >= 0
                && mouseY < map.getMapHeight()) {
            if (map.getWorld()[mouseX][mouseY] == Tileset.FLOOR) {
                tile += "floor";
            } else if (map.getWorld()[mouseX][mouseY] == Tileset.WALL) {
                tile += "wall";
            } else if (map.getWorld()[mouseX][mouseY] == Tileset.AVATAR) {
                tile += "player";
            } else {
                tile += "nothing";
            }
        }
        return tile;
    }
    private void drawMenu() {
        ter.initialize(WIDTH, HEIGHT);
        StdDraw.setPenColor(Color.WHITE);
        StdDraw.setFont(new Font("Arial", Font.PLAIN, 60));
        StdDraw.text(WIDTH / 2, HEIGHT - 5, "CS61B: THE GAME");
        StdDraw.setFont(new Font("Arial", Font.PLAIN, 30));
        StdDraw.text(WIDTH / 2, 20, "New Game (N)");
        StdDraw.text(WIDTH / 2, 17, "New Game with Limited View (X)");
        StdDraw.text(WIDTH / 2, 14, "Load Game (L)");
        StdDraw.text(WIDTH / 2, 11, "Name Avatar (A)");
        StdDraw.text(WIDTH / 2, 8, "Quit (Q)");

        StdDraw.show();
    }

    private void menuActions() {
        if (StdDraw.hasNextKeyTyped()) {
            keyboardInput = Character.toUpperCase(StdDraw.nextKeyTyped());
            if (keyboardInput == 'N') {
                gameOver = false;
                seed = getSeed();
            } else if (keyboardInput == 'X') {
                gameOver = false;
                seed = getSeed();
                limitedView = true;
            } else if (keyboardInput == 'L') {
                loadFile();
                if (fromLoadFile) {
                    gameOver = false;
                } else {
                    seed = getSeed();
                    gameOver = false;
                }

            } else if (keyboardInput == 'Q') {
                StdDraw.clear();
                System.exit(0);
            } else if (keyboardInput == 'A') {
                avatarName = avatarName();
                //returns to menu in a strange way?
                drawMenu();
                menuActions();

            }
        }
    }

    private String avatarName() {
        //cam we implement deleting?
        StdDraw.clear(Color.BLACK);
        StdDraw.setFont(new Font("Arial", Font.PLAIN, 60));
        StdDraw.text(WIDTH / 2, (HEIGHT / 2) + 3,
                "Enter Avatar Name:");
        StdDraw.show();
        boolean doneTyping = false;
        String name = "";
        char keyInput;

        while (!doneTyping) {
            if (StdDraw.hasNextKeyTyped()) {
                keyInput = StdDraw.nextKeyTyped();
                if (keyInput == '1') {
                    doneTyping =  true;
                } else {
                    name += Character.toString(keyInput);
                    StdDraw.clear(Color.BLACK);
                    StdDraw.setFont(new Font("Arial", Font.PLAIN, 60));
                    StdDraw.text(WIDTH / 2, (HEIGHT / 2) + 3,
                            "Enter Avatar Name:");
                    StdDraw.text(WIDTH / 2, (HEIGHT / 2) - 2, name);
                    StdDraw.text(WIDTH / 2, (HEIGHT / 2) - 6,
                            "Press 1 to go back to Main Menu");
                    StdDraw.show();
                }
            }
        }
        return name;

    }

    private long getSeed() {
        StdDraw.clear(Color.BLACK);
        StdDraw.setFont(new Font("Arial", Font.PLAIN, 60));
        StdDraw.text(WIDTH / 2, HEIGHT / 2,
                "Enter random seed (must be positive) :");
        StdDraw.show();
        boolean doneTyping = false;
        String s = "";
        long temp = 0;
        char keyInput;

        while (!doneTyping) {
            if (StdDraw.hasNextKeyTyped()) {
                keyInput = Character.toUpperCase(StdDraw.nextKeyTyped());
                if (keyInput == 'S') {
                    temp = Long.parseLong(s);
                    doneTyping =  true;
                } else if (Character.isDigit(keyInput)) {
                    s += Character.toString(keyInput);
                    StdDraw.clear(Color.BLACK);
                    StdDraw.setFont(new Font("Arial", Font.PLAIN, 60));
                    StdDraw.text(WIDTH / 2, HEIGHT / 2,
                            "Enter random seed (must be positive) :");
                    StdDraw.text(WIDTH / 2, (HEIGHT / 2) - 5, s);
                    StdDraw.text(WIDTH / 2, (HEIGHT / 2) - 9,
                            "Press S to start");
                    StdDraw.show();
                }
            }
        }
        return temp;
    }

    private void saveQuit() {
        String directory = "byow/Core/";
        String fileName = "loadgame.txt";
        absolutePath = directory + File.separator + fileName;
        try {
            File savedFile = new File(absolutePath);
            if (savedFile.createNewFile()) {
                System.out.println("File created: " + savedFile.getName());
            } else {
                System.out.println("File already exists.");
            }
        } catch (IOException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }

        try {
            FileWriter writer = new FileWriter(absolutePath);
            writer.write(seed + "\n");
            writer.write(playerPos.getX() + "\n");
            writer.write(playerPos.getY() + "\n");
            writer.write(avatarName + "\n");
            writer.write(limitedView + "\n");
            writer.close();
            System.out.println("Successfully wrote to the file.");
        } catch (IOException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }

    }

    private void loadFile() {
        int playerX = 0;
        int playerY = 0;
        String directory = "byow/Core/";
        String fileName = "loadgame.txt";
        absolutePath = directory + File.separator + fileName;
        try {
            File loadFile = new File(absolutePath);
            if (loadFile.exists()) {
                fromLoadFile = true;
                Scanner scanner = new Scanner(loadFile);
                int lineNum = 1;
                while (scanner.hasNextLine()) {
                    String currLine = scanner.nextLine();
                    if (lineNum == 1) {
                        seed = Long.parseLong(currLine);
                    }
                    if (lineNum == 2) {
                        playerX = Integer.parseInt(currLine);
                    }
                    if (lineNum == 3) {
                        playerY = Integer.parseInt(currLine);
                    }
                    if (lineNum == 4) {
                        avatarName = currLine;
                    }
                    if (lineNum == 5) {
                        limitedView = Boolean.parseBoolean(currLine);
                    }
                    lineNum += 1;
                    System.out.println(currLine);
                }
                scanner.close();
            } else {
                System.out.println("No loaded game. Will start new game.");
            }

        } catch (FileNotFoundException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }
        playerPos = new Position(playerX, playerY);
    }

}
