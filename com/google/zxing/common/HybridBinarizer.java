package com.google.zxing.common;

import com.google.zxing.Binarizer;
import com.google.zxing.LuminanceSource;
import com.google.zxing.NotFoundException;
import java.lang.reflect.Array;

public final class HybridBinarizer extends GlobalHistogramBinarizer {
    private static final int MINIMUM_DIMENSION = 40;
    private BitMatrix matrix = null;

    public HybridBinarizer(LuminanceSource source) {
        super(source);
    }

    public BitMatrix getBlackMatrix() throws NotFoundException {
        binarizeEntireImage();
        return this.matrix;
    }

    public Binarizer createBinarizer(LuminanceSource source) {
        return new HybridBinarizer(source);
    }

    private void binarizeEntireImage() throws NotFoundException {
        if (this.matrix == null) {
            LuminanceSource source = getLuminanceSource();
            if (source.getWidth() < 40 || source.getHeight() < 40) {
                this.matrix = super.getBlackMatrix();
                return;
            }
            byte[] luminances = source.getMatrix();
            int width = source.getWidth();
            int height = source.getHeight();
            int subWidth = width >> 3;
            if ((width & 7) != 0) {
                subWidth++;
            }
            int subHeight = height >> 3;
            if ((height & 7) != 0) {
                subHeight++;
            }
            int[][] blackPoints = calculateBlackPoints(luminances, subWidth, subHeight, width, height);
            this.matrix = new BitMatrix(width, height);
            calculateThresholdForBlock(luminances, subWidth, subHeight, width, height, blackPoints, this.matrix);
        }
    }

    private static void calculateThresholdForBlock(byte[] luminances, int subWidth, int subHeight, int width, int height, int[][] blackPoints, BitMatrix matrix) {
        int y = 0;
        while (y < subHeight) {
            int yoffset = y << 3;
            if (yoffset + 8 >= height) {
                yoffset = height - 8;
            }
            int x = 0;
            while (x < subWidth) {
                int xoffset = x << 3;
                if (xoffset + 8 >= width) {
                    xoffset = width - 8;
                }
                int left = x > 1 ? x : 2;
                if (left >= subWidth - 2) {
                    left = subWidth - 3;
                }
                int top = y > 1 ? y : 2;
                if (top >= subHeight - 2) {
                    top = subHeight - 3;
                }
                int sum = 0;
                for (int z = -2; z <= 2; z++) {
                    int[] blackRow = blackPoints[top + z];
                    sum = ((((sum + blackRow[left - 2]) + blackRow[left - 1]) + blackRow[left]) + blackRow[left + 1]) + blackRow[left + 2];
                }
                threshold8x8Block(luminances, xoffset, yoffset, sum / 25, width, matrix);
                x++;
            }
            y++;
        }
    }

    private static void threshold8x8Block(byte[] luminances, int xoffset, int yoffset, int threshold, int stride, BitMatrix matrix) {
        for (int y = 0; y < 8; y++) {
            int offset = ((yoffset + y) * stride) + xoffset;
            for (int x = 0; x < 8; x++) {
                if ((luminances[offset + x] & 255) < threshold) {
                    matrix.set(xoffset + x, yoffset + y);
                }
            }
        }
    }

    private static int[][] calculateBlackPoints(byte[] luminances, int subWidth, int subHeight, int width, int height) {
        int[][] blackPoints = (int[][]) Array.newInstance(Integer.TYPE, new int[]{subHeight, subWidth});
        for (int y = 0; y < subHeight; y++) {
            int yoffset = y << 3;
            if (yoffset + 8 >= height) {
                yoffset = height - 8;
            }
            for (int x = 0; x < subWidth; x++) {
                int average;
                int xoffset = x << 3;
                if (xoffset + 8 >= width) {
                    xoffset = width - 8;
                }
                int sum = 0;
                int min = 255;
                int max = 0;
                for (int yy = 0; yy < 8; yy++) {
                    int offset = ((yoffset + yy) * width) + xoffset;
                    for (int xx = 0; xx < 8; xx++) {
                        int pixel = luminances[offset + xx] & 255;
                        sum += pixel;
                        if (pixel < min) {
                            min = pixel;
                        }
                        if (pixel > max) {
                            max = pixel;
                        }
                    }
                }
                if (max - min > 24) {
                    average = sum >> 6;
                } else {
                    average = max == 0 ? 1 : min >> 1;
                }
                blackPoints[y][x] = average;
            }
        }
        return blackPoints;
    }
}
