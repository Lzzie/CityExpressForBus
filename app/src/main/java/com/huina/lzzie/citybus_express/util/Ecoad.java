package com.huina.lzzie.citybus_express.util;

import android.graphics.Bitmap;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.common.BitMatrix;

/**
 * Created by Lzzie on 2017/3/17.
 */

public class Ecoad {
    private int width;
    private int height;

    public Ecoad(int width, int height) {
        this.width = width;
        this.height = height;

    }

    //绘制条形码
    public Bitmap bitmap1(String ss) throws Exception{
        //条形码CODE_128
        BarcodeFormat fomt=BarcodeFormat.CODE_128;
        BitMatrix matrix=new MultiFormatWriter().encode(ss, fomt, width, height);
        int width=matrix.getWidth();
        int height=matrix.getHeight();
        int[] pixel=new int[width*height];
        for(int i=0;i<height;i++){
            for(int j=0;j<width;j++){
                if(matrix.get(j,i))
                    pixel[i*width+j]=0xff000000;
            }
        }
        Bitmap bmapp=Bitmap.createBitmap(width,height,Bitmap.Config.ARGB_8888);
        bmapp.setPixels(pixel, 0, width, 0, 0, width, height);
        return bmapp;
    }


}
