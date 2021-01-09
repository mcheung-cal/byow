package byow.lab12;
import org.junit.Test;
import static org.junit.Assert.*;

import byow.TileEngine.TERenderer;
import byow.TileEngine.TETile;
import byow.TileEngine.Tileset;

import java.util.Random;

/**
 * Draws a world consisting of hexagonal regions.
 */
public class HexWorld {

    private class Hexagon {
        private int minLength;
        private int maxLength;
        private String tile;

        public Hexagon(int minL, int maxL, String t) {
            minLength = minL;
            maxLength = maxL;
            tile = t;
        }
    }

    public void addHexagon(int s) {
        Hexagon h = new Hexagon(minLength(s), maxLength(s), "a");
    }

    private int minLength(int s) {
        return s;
    }

    private int maxLength(int s) {
        return s + (2 * (s-1));
    }
}
