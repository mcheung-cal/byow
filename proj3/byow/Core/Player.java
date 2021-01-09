package byow.Core;

import byow.TileEngine.TETile;
import byow.TileEngine.Tileset;

public class Player {
    private Position pos;
    private TETile avatar;
    private int lineOfSight;


    public Player(Position p) {
        pos = p;
        avatar = Tileset.AVATAR;
        lineOfSight = 3;
    }

    public Position getPosition() {
        return this.pos;
    }

    public void setPosition(Position p) {
        this.pos = p;
    }

    public TETile getAvatar() {
        return this.avatar;
    }

    public Position updatePosition(char c) {
        if (c == 'W') {
            return new Position(pos.getX(), pos.getY() + 1);
        } else if (c == 'S') {
            return new Position(pos.getX(), pos.getY() - 1);
        } else if (c == 'A') {
            return new Position(pos.getX() - 1, pos.getY());
        } else if (c == 'D') {
            return new Position(pos.getX() + 1, pos.getY());
        }
        return null;
    }

    public int getLineOfSight() {
        return lineOfSight;
    }
}
