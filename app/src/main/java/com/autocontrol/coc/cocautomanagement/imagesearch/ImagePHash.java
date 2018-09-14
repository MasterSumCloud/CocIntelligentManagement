package com.autocontrol.coc.cocautomanagement.imagesearch;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Rect;
/* 
 * 汉明距离越大表明图片差异越大，如果不相同的数据位不超过5，就说明两张图片很相似；如果大于10，就说明这是两张不同的图片。
 */
public class ImagePHash {
    private int size = 32;
    private int smallerSize = 8;

    public ImagePHash() {
        initCoefficients();
    }

    public ImagePHash(int size, int smallerSize) {
        this.size = size;
        this.smallerSize = smallerSize;

        initCoefficients();
    }

    public int distance(String s1, String s2) {
        int counter = 0;
        for (int k = 0; k < s1.length(); k++) {
            if (s1.charAt(k) != s2.charAt(k)) {
                counter++;
            }
        }
        return counter;
    }

    /**
     * 返回图片二进制流的字符串
     * @throws Exception
     */
    public String getHash(String path) throws Exception {
        Bitmap img = BitmapFactory.decodeFile(path);

        /*
         * 简化图片尺寸
         */
        img = resize(img, size, size);

        /*
         *  减少图片颜色
         */
        img = ImageTo565.convertGreyImg(img);

        double[][] vals = new double[size][size];

        for (int x = 0; x < img.getWidth(); x++) {
            for (int y = 0; y < img.getHeight(); y++) {
                vals[x][y] = getBlue(img, x, y);
            }
        }

        /*
         * 计算DTC 采用32*32尺寸
         */
        long start = System.currentTimeMillis();
        double[][] dctVals = applyDCT(vals);
        System.out.println("DCT: " + (System.currentTimeMillis() - start));

    
        /*
         * 计算平均值DTC
         */
        double total = 0;

        for (int x = 0; x < smallerSize; x++) {
            for (int y = 0; y < smallerSize; y++) {
                total += dctVals[x][y];
            }
        }
        total -= dctVals[0][0];

        double avg = total / (double) ((smallerSize * smallerSize) - 1);

        /*
         * 计算hash值
         */
        String hash = "";

        for (int x = 0; x < smallerSize; x++) {
            for (int y = 0; y < smallerSize; y++) {
                if (x != 0 && y != 0) {
                    hash += (dctVals[x][y] > avg ? "1" : "0");
                }
            }
        }

        return hash;
    }

    private Bitmap resize(Bitmap bmp, int width, int height) {
        // 压缩Bitmap到对应尺寸
        Bitmap result = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(result);
        Rect rect = new Rect(0, 0, width, height);
        canvas.drawBitmap(bmp, null, rect, null);

        return result;
    }

    private static int getBlue(Bitmap img, int x, int y) {
        return (img.getPixel(x, y)) & 0xff;
    }


    private double[] c;

    private void initCoefficients() {
        c = new double[size];

        for (int i = 1; i < size; i++) {
            c[i] = 1;
        }
        c[0] = 1 / Math.sqrt(2.0);
    }

    private double[][] applyDCT(double[][] f) {
        int N = size;

        double[][] F = new double[N][N];
        for (int u = 0; u < N; u++) {
            for (int v = 0; v < N; v++) {
                double sum = 0.0;
                for (int i = 0; i < N; i++) {
                    for (int j = 0; j < N; j++) {
                        sum += Math.cos(((2 * i + 1) / (2.0 * N)) * u * Math.PI)
                                * Math.cos(((2 * j + 1) / (2.0 * N)) * v * Math.PI) * (f[i][j]);
                    }
                }
                sum *= ((c[u] * c[v]) / 4.0);
                F[u][v] = sum;
            }
        }
        return F;
    }
}