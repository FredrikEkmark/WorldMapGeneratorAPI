package com.fredrik.worldMapGenerator.tile;

import com.fredrik.worldMapGenerator.PerlinNoise.OpenSimplex;

import java.util.Random;

public class TileInturpetation {

    private static final int WIDTH = 3000;
    private static final int WIDTH_PART = WIDTH/10;
    private static final int HEIGHT = 1500;
    private static final int HEIGHT_PART = HEIGHT/10;
    private static final double FREQUENCY1 = 1 / 36.0;
    private static final double FREQUENCY2 = 1 / 120.0;
    private static final double FREQUENCY3 = 1 / 340.0;
    private static final double FREQUENCY4 = 1 / 24.0;
    private static final double FREQUENCY5 = 1 / 6.0;
    private static final int SEED =  new Random().nextInt(1, 10000);
    private static final int WATER_PATH = spinSeed(SEED);

    public static Tile tileValue(int x, int y) {

        Tile tile = new Tile();

        double terrainNoiseValue = threeFrequencyNoise(x, y, FREQUENCY1, FREQUENCY2, FREQUENCY3);
        terrainNoiseValue = waterPath(x, y, terrainNoiseValue);
        double difNoiseValue = twoFrequencyNoise(x, y, FREQUENCY4, FREQUENCY5);
        double precipitationNoiseValue = singleFrequencyNoise(x, y, FREQUENCY2) + (difNoiseValue * 0.05);

        int terrainValue = terrain(terrainNoiseValue, difNoiseValue , x, y);

        int temperatureValue = temperature(y, (terrainNoiseValue + difNoiseValue - 0.8)/3);
        int precipitationValue = precipitation(precipitationNoiseValue, terrainValue, difNoiseValue, terrainNoiseValue);

        tile.tileValue = terrainValue + temperatureValue + precipitationValue;
        return tile;
    };

    private static Double threeFrequencyNoise(int x, int y, double frequency1, double frequency2, double frequency3) {
        double value1 = OpenSimplex.noise3_ImproveXY(SEED, x * frequency1, y * frequency1, 0.0);
        double value2 = OpenSimplex.noise3_ImproveXY(SEED, x * frequency2, y * frequency2, 0.0);
        double value3 = OpenSimplex.noise3_ImproveXY(SEED, x * frequency3, y * frequency3, 0.0);

        return ((value1 + value2 + value3 + value3) / 4);
    }

    private static double twoFrequencyNoise(int x, int y, double frequency1, double frequency2) {
        double value = OpenSimplex.noise3_ImproveXY(SEED/2, x * frequency1, y * frequency1, 0.0);
        value += OpenSimplex.noise3_ImproveXY(SEED/2, x * frequency2, y * frequency2, 0.0);

        return value;
    }

    private static double singleFrequencyNoise(int x, int y, double frequency1) {
        return OpenSimplex.noise3_ImproveXY(SEED/2, x * frequency1, y * frequency1, 0.0);
    }

    private static int terrain(Double valuesCombined, double difValue, int x, int y) {

        if (y < 100) {
            Double gradiant100 = y * 0.01;
            Double gradiant0 = 1 - gradiant100;
            valuesCombined = ((valuesCombined * gradiant100) + (0.01 * gradiant0)/2);
        } else if (y > HEIGHT - 100) {
            int gradiant = HEIGHT - y;
            Double gradiant100 = gradiant * 0.01;
            Double gradiant0 = 1 - gradiant100;
            valuesCombined = ((valuesCombined * gradiant100) + (0.01 * gradiant0)/2);
        }

        int terrainResult;

        if (x <= WIDTH_PART) {
            float test = ((1.f/WIDTH_PART)* x);
            double test2 = 1 - test;
            Double value = ((valuesCombined * test) + (test2 * 1/2));
            terrainResult =  terrainMapping(value, difValue);
        } else if (x >= WIDTH - WIDTH_PART) {
            float test = ((1.f/WIDTH_PART) * (WIDTH - x));
            double test2 = 1 - test;
            Double value = ((valuesCombined * test) + (test2 * 1/2)) ;
            terrainResult = terrainMapping(value, difValue);
        } else {
            terrainResult = terrainMapping(valuesCombined, difValue);
        }

        return terrainResult;
    }

    private  static double waterPath(int x, int y, double value) {

        if (WATER_PATH <= y + 100 && WATER_PATH >= y - 100) {
            int gradiant = y - WATER_PATH;
            if (gradiant < 0) {
                gradiant = gradiant * -1;
            }
            Double gradiant100 = gradiant * 0.01;
            Double gradiant0 = 1 - gradiant100;
            if (value * gradiant100 < 0.5 * gradiant0) {
                value = ((value * gradiant100) + (0.3 * gradiant0)/2);
            }
        }
        return value;
    }

    private static int terrainMapping(Double terrain, double difValue) {

        terrain = terrain * -1 + (difValue*0.05);

        if (terrain < -0.1) {
            return 100;
        } else if (terrain < 0) {
            if (difValue > 1.1) {
                return 300;}
            return 200;
        } else if (terrain < 0.15) {
            if (difValue > -1.4) {
                return 300;}
            return 300;
        } else if (terrain < 0.4) {
            return 400;
        } else {
            return 500;
        }
    }

    private static int temperature(int latitude, double difValue) {

        int value;
        int latitudeLine = -1;

        if (latitude < HEIGHT_PART/2) {
            latitudeLine = HEIGHT_PART/2;
            value = 10;
        } else if (latitude > (HEIGHT_PART/2)*19) {
            latitudeLine = (HEIGHT_PART/2)*19;
            value = 10;
        } else if (latitude < HEIGHT_PART*2 ) {
            latitudeLine = (HEIGHT_PART*2);
            value = 20;
        } else if (latitude > HEIGHT_PART*8) {
            latitudeLine = (HEIGHT_PART*8);
            value = 20;
        } else if (latitude < HEIGHT_PART*3.5) {
            latitudeLine = (int) (HEIGHT_PART*3.5);
            value = 30;
        } else if (latitude > HEIGHT_PART*6.5) {
            latitudeLine = (int) (HEIGHT_PART*6.5);
            value = 30; //
        } else if (latitude < HEIGHT_PART*4.5) {
            latitudeLine = (int) (HEIGHT_PART*4.5);
            value = 40;
        } else if (latitude > HEIGHT_PART*5.5) {
            latitudeLine = (int) (HEIGHT_PART*5.5);
            value = 40;
        } else {
            value = 50;
        }

        double difPos;

        if (latitudeLine == -1) {
            difPos = 100.0;
        } else if (latitude < HEIGHT/2) {
            difPos = (latitudeLine - latitude);
        } else {
            difPos = (latitude - latitudeLine);
        }

        difPos = (difPos) * 0.1;

        if (difValue + difPos < 0 )
            value += 10;

        return value;
    }

    public static int precipitation(double perNoise, int terrain, double difValue, double terrainNoiseValue) {
        if (terrain == 500) {
            return 1;
        } else if (terrain < 201) {
            return 3;
        }

        terrainNoiseValue = (terrainNoiseValue * -1) + (difValue * 0.03);
        perNoise = perNoise + (difValue * 0.2);

        if (terrainNoiseValue > 0.25) {
            if (perNoise > -0.2) {
                return 1;
            } else if (perNoise > -0.6) {
                return 2;
            } else {
                return 3;
            }
        } else {
            if (perNoise > 0.7) {
                return 1;
            } else if (perNoise > 0) {
                return 2;
            } else {
                return 3;
            }
        }
    }

    private static int spinSeed(int latitude) {

        while (latitude > HEIGHT - HEIGHT_PART) {
            latitude -= (HEIGHT -HEIGHT_PART*2);
        }

        if (latitude < HEIGHT_PART) {
            latitude += (HEIGHT - HEIGHT_PART*2);
        }

        return latitude;
    }

    public static int xSpin(int x) {

        if (x >= WIDTH) {
            x -= WIDTH;
        }
        return x;
    }

    public static int TEMP_PAINTER(int value) {
        switch (value) {
            case 111, 112, 113, 211, 212, 213, 311, 312, 313 -> {return 0xc2d7f2;}  // Glacier
            case 123,  133, 143, 153 -> {return 0x1433a6;}  // Deep Water
            case 223, 233, 243, 253 -> {return 0x3c5cfa;}  // Coastal Water
            case 322, 323 -> {return 0x10944b;} // Lowland Tundra
            case 422, 423 -> {return 0x53916f;} // Highlands Tundra
            case 321 -> {return 0x768c91;} // Cold Desert
            case 421 -> {return 0x3c474a;} // Cold Desert Hills
            case 331 -> {return 0x18d628;}  // Temperate Lowlands Plains
            case 332 -> {return 0x10941b;}  // Temperate Forest
            case 431 -> {return 0x6ed477;}  // Highland Hills
            case 432 -> {return 0x498f4f;}  // Temperate Highland Forest
            case 333 -> {return 0x084d0e;}  // Temperate Rainforest
            case 433 -> {return 0x2a452c;}  // Temperate Highland Rainforest
            case 341 -> {return 0xe84827;} // Hot Desert
            case 441 -> {return 0x9c2e17;} // Hot Desert Hills
            case 342 -> {return 0x459410;} // Hot Steppe
            case 442 -> {return 0x69914e;} // Hot Steppe hills
            case 351, 352, 343 -> {return 0x739410;} // Tropical Savanna
            case 451, 452, 443 -> {return 0x7e8f4c;} // Tropical Savanna Hills
            case 353 -> {return 0x3f5209;} // Tropical Rainforest
            case 453 -> {return 0x464f2b;} // Tropical Rainforest Hills
            case 411, 412, 413 -> {return 0x9dc6fc;} // Glacial Heights
            case 511, 521, 531 -> {return 0xebf0f7;} // Frozen Mountains
            case 541, 551 -> {return 0x260f02;}  // Mountains
        }
        System.out.println("Error not valid value: " + value);
        return 0x010101;
    }


}
