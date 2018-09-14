package com.autocontrol.coc.cocautomanagement.imagesearch;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.widget.Toast;

import java.util.ArrayList;

/**
 * Created by pengyan on 14/09/2018.
 */

public class SearchImage {

    private int targetWidth;
    private int targetHeight;
    private Context mContext;

    private int selfSearchAreaX = -1;
    private int selfSearchAreaY = -1;
    private int selfSearchAreaWidth = -1;
    private int selfSearchAreaHeight = -1;

    public SearchImage() {
    }

    public ArrayList<ResultXYBean> searchImage(String resouce, String target) {
        ArrayList<ResultXYBean> result = new ArrayList<>();
        Bitmap bigBt = BitmapFactory.decodeFile(resouce);
        Bitmap smallBt = BitmapFactory.decodeFile(target);
        int width = bigBt.getWidth();
        int height = bigBt.getHeight();
        ArrayList<PositionBean> characteristicPoint = getCharacteristicPoint(smallBt);
        if (characteristicPoint != null) {
            if (selfSearchAreaX != -1 && selfSearchAreaY != -1) {//自定义搜索区域
                int SearchEndX = selfSearchAreaX + selfSearchAreaWidth;
                int SearchEndY = selfSearchAreaY + selfSearchAreaHeight;
                for (int i = selfSearchAreaX; i < SearchEndX; i++) {
                    for (int j = selfSearchAreaY; j < SearchEndY; j++) {
                        int pixel = bigBt.getPixel(i, j);
//                    int r = (pixel & 0xff0000) >> 16;
//                    int g = (pixel & 0xff00) >> 8;
//                    int b = (pixel & 0xff);
                        //依次对比5个点。
                        searchTraget(result, bigBt, characteristicPoint, i, j, pixel);
                    }
                }
            } else {//全范围搜索
                for (int i = 0; i < width; i++) {
                    for (int j = 0; j < height; j++) {
                        int pixel = bigBt.getPixel(i, j);
//                    int r = (pixel & 0xff0000) >> 16;
//                    int g = (pixel & 0xff00) >> 8;
//                    int b = (pixel & 0xff);
                        //依次对比5个点。
                        searchTraget(result, bigBt, characteristicPoint, i, j, pixel);
                    }
                }
            }

        }

        if (result.size() > 0) {
            return result;
        } else {
            return null;
        }
    }

    private void searchTraget(ArrayList<ResultXYBean> result, Bitmap bigBt, ArrayList<PositionBean> characteristicPoint, int i, int j, int pixel) {
        PositionBean p1 = characteristicPoint.get(0);
        if (pixel == p1.pxrgb) {
            int other = 0;
            PositionBean p2 = characteristicPoint.get(1);
            int pixel2 = bigBt.getPixel(i + (p2.x - p1.x), j);
            if (pixel2 == p2.pxrgb) {
                other++;
                PositionBean p3 = characteristicPoint.get(2);
                int pixel3 = bigBt.getPixel(i + (p3.x - p1.x), j + (p3.y - p1.y));
                if (pixel3 == p3.pxrgb) {
                    other++;
                    PositionBean p4 = characteristicPoint.get(3);
                    int pixel4 = bigBt.getPixel(i, j + (p4.y - p1.y));
                    if (pixel4 == p4.pxrgb) {
                        other++;
                        PositionBean p5 = characteristicPoint.get(4);
                        int pixel5 = bigBt.getPixel(i + (p5.x - p1.x), j + (p5.y - p1.y));
                        if (pixel5 == p5.pxrgb) {
                            other++;
                        }
                    }
                }
            }
            if (other == 4) {
                ResultXYBean resultXYBean = new ResultXYBean();
                resultXYBean.setWidth(targetWidth);
                resultXYBean.setHeight(targetHeight);
                resultXYBean.setX(i - p1.x);
                resultXYBean.setY(j - p1.y);
                result.add(resultXYBean);
            }
        }
    }


    private ArrayList<PositionBean> getCharacteristicPoint(Bitmap bitmap) {
        ArrayList<PositionBean> searchXYList = new ArrayList<>();
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        targetWidth = width;
        targetHeight = height;

        if (width >= 10 && height >= 10) {
            int px1 = (int) (width * 0.25);
            int py1 = (int) (height * 0.25);
            int px2 = (int) (width * 0.75);
            int py2 = (int) (height * 0.25);
            int px3 = (int) (width * 0.5);
            int py3 = (int) (height * 0.5);
            int px4 = (int) (width * 0.25);
            int py4 = (int) (height * 0.75);
            int px5 = (int) (width * 0.75);
            int py5 = (int) (height * 0.75);
            searchXYList.add(new PositionBean(px1, py1, bitmap.getPixel(px1, py1)));
            searchXYList.add(new PositionBean(px2, py2, bitmap.getPixel(px2, py2)));
            searchXYList.add(new PositionBean(px3, py3, bitmap.getPixel(px3, py3)));
            searchXYList.add(new PositionBean(px4, py4, bitmap.getPixel(px4, py4)));
            searchXYList.add(new PositionBean(px5, py5, bitmap.getPixel(px5, py5)));
        } else {
            Toast.makeText(mContext, "不支持10px以内的搜索", Toast.LENGTH_SHORT);
            return null;
        }

        return searchXYList;
    }


}
