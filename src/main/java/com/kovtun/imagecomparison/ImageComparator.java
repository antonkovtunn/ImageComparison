package com.kovtun.imagecomparison;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ImageComparator {
    private static int width;
    private static int height;

    private static int[][] firstImageArray;
    private static int[][] secondImageArray;

    private static BufferedImage result;
    private static List<List<Point>> areas;

    private static final int RATIO_VALUE = 1000; //ideal number dividing on which percentage difference will be calculated
    private static final int AREA_BUFFER = 150; // plus-minus distance to calculate pixel belongs to highlighted area or new area should be created

    public static BufferedImage compareImages(BufferedImage firstImage, BufferedImage secondImage) throws IOException {
        if (!isValid(firstImage, secondImage)) {
            throw new RuntimeException("Images are not valid");
        }

        firstImageArray = composePixelArray(firstImage);
        secondImageArray = composePixelArray(secondImage);
        height = firstImageArray.length;
        width = firstImageArray[0].length;

        result = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        findAreas();
        drawAreas();
        return result;
    }

    private static boolean isValid(BufferedImage firstImage, BufferedImage secondImage) {
        if (firstImage == null || secondImage == null
                || firstImage.getHeight() != secondImage.getHeight()
                || firstImage.getWidth() != secondImage.getWidth()) {
            return false;
        }
        return true;
    }

    private static int[][] composePixelArray(BufferedImage bufferedImage) {
        int width = bufferedImage.getWidth();
        int height = bufferedImage.getHeight();
        int[][] pixelsArray = new int[height][width];

        for (int row = 0; row < height; row++) {
            bufferedImage.getRGB(0, row, width, 1, pixelsArray[row], 0, width);
        }

        return pixelsArray;
    }

    private static void findAreas() {
        List<Point> points = new ArrayList<>();
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                result.setRGB(j, i, secondImageArray[i][j]);

                double firstPercent = Math.abs((double) firstImageArray[i][j] / RATIO_VALUE * 100);
                double secondPercent = Math.abs((double) secondImageArray[i][j] / RATIO_VALUE * 100);
                if (Math.abs(firstPercent - secondPercent) > 10) {
                    points.add(new Point(j, i));
                }
            }
        }

        areas = new ArrayList<>();

        points.stream().filter(point -> !isInExistingArea(point)).forEach(point -> {
            List<Point> newArea = new ArrayList<>();
            newArea.add(point);
            areas.add(newArea);
        });
    }

    private static boolean isInExistingArea(Point point) {
        for (int i = 0; i < areas.size(); i++) {
            for (int j = 0; j < areas.get(i).size(); j++) {
                int x = (int) areas.get(i).get(j).getX();
                int y = (int) areas.get(i).get(j).getY();

                if ((point.getX() > x - AREA_BUFFER) && (point.getX() < x + AREA_BUFFER)
                        && (point.getY() > y - AREA_BUFFER) && (point.getY() < y + AREA_BUFFER)) {
                    areas.get(i).add(point);
                    return true;
                }
            }
        }
        return false;
    }

    private static void drawAreas() {
        for (int i = 0; i < areas.size(); i++) {
            int left = width, right = 0, top = height, bottom = 0;
            for (int j = 0; j < areas.get(i).size(); j++) {
                Point point = areas.get(i).get(j);
                if (point.getX() < left) {
                    left = (int) point.getX();
                }
                if (point.getX() > right) {
                    right = (int) point.getX();
                }
                if (point.getY() > bottom) {
                    bottom = (int) point.getY() ;
                }
                if (point.getY() < top) {
                    top = (int) point.getY() ;
                }
            }

            Graphics2D g2d = result.createGraphics();
            g2d.setColor(Color.RED);
            g2d.drawRect(left, top, right - left, bottom - top);
            g2d.dispose();
        }
    }
}
