package com.fredrik.worldMapGenerator.tile;

import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.io.*;
import java.text.DecimalFormat;

public class MapGeneration
{
    private static final int WIDTH = 3000;
    private static final int HEIGHT = 1500;

    public static String mapGeneration() {

        StringBuilder api = new StringBuilder();

        Tile[][] map = new Tile[HEIGHT][WIDTH];

        api.append("[");
        for (int y = 0; y < HEIGHT; y++)
        {
            if (y == 0) {
                api.append("[");
            } else {
            api.append(",[");
            }
            int oddOrEven = 0;
            if (y % 2 == 1) {
                oddOrEven = 1;
            }
            for (int x = 0; x < (WIDTH - oddOrEven); x++) {
                if (!(x == 0)) {
                    api.append(",");
                }
                Tile tile;

                    tile = TileInturpetation.tileValue(x, y);

                    String apiEntry = "{\"x\":" + x + ",\"y\":" + y + ",\"t\":" + tile.tileValue + "}";

                    api.append(apiEntry);
            }
            api.append("]");
        }
        api.append("]");
        return api.toString();
    }
}
