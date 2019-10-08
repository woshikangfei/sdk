package com.analytics.sdk.common.image;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;

import com.analytics.sdk.common.log.Logger;

import java.io.IOException;
import java.io.InputStream;

/**
 * 将流进行解码为图片
 * @author devy
 */
public final class ImageDecoder {

    private static final int MARK_POSITION = 5 * 1024 * 1024;
    static final String TAG = ImageDecoder.class.getSimpleName();

    public static Bitmap decode(InputStream inputStream, int clientRequestWidth, int clientRequestHeight) throws ImageDecodeException {
        BitmapFactory.Options options = new BitmapFactory.Options();

        if(!inputStream.markSupported()) {
            inputStream = new MarkEnforcingInputStream(new RecyclableBufferedInputStream(inputStream));
        }

        try {
            //缩放处理
            calculateScaling(inputStream, clientRequestWidth, clientRequestHeight, options);

            //配置处理
            calculateConfig(inputStream,options);

            //尝试使用bitmap pool(像素空间复用)
            calculateInbitmap(inputStream,options);

            Bitmap bitmap = BitmapFactory.decodeStream(inputStream, null, options);

//            if(bitmap!=null){
//                ImagePool.putBitmap(bitmap);
//            }

            return bitmap;
        } catch (Throwable t){ //oom ?
            t.printStackTrace();
            throw new ImageDecodeException(t);
        } finally {
            try {
                inputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private static void calculateInbitmap(InputStream inputStream, BitmapFactory.Options options) {

        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.HONEYCOMB) {
            options.inMutable = true;
            Bitmap inBitmap = ImagePool.getBitmap(options.outWidth, options.outHeight, options.inPreferredConfig);
            if (inBitmap != null && ImageHelper.canUseForInBitmap(inBitmap, options)) {
                options.inBitmap = inBitmap;
                Logger.i(TAG,"calculateInbitmap true");
            } else {
                Logger.i(TAG,"calculateInbitmap false");
            }
        }

    }

    //CenterCrop
    public static void calculateScaling(InputStream inputStream, int clientRequestWidth, int clientRequestHeight, BitmapFactory.Options options) throws IOException {
        //不加载图片的像素到内存中,只获取高度,为了计算缩放比例
        int[] bitmapDimensions = getBitmapDimensions(inputStream,options);
        int sourceWidth = bitmapDimensions[0];
        int sourceHeight = bitmapDimensions[1];

        int inSampleSize = ImageHelper.calculateInSampleSize(options, clientRequestWidth, clientRequestHeight);

        Logger.i(TAG,"calculateScaling sourceWidth = " + sourceWidth + " , sourceHeight = " + sourceHeight + " , inSampleSize = " + inSampleSize);

        float widthPercentage = clientRequestWidth / (float) sourceWidth;
        float heightPercentage = clientRequestHeight / (float) sourceHeight;
        //CenterCrop
        float scaleFactor = Math.max(widthPercentage, heightPercentage);

        Logger.i(TAG,"calculateScaling scaleFactor = " + scaleFactor);

        int outWidth = round(scaleFactor * sourceWidth);
        int outHeight = round(scaleFactor * sourceHeight);

        int widthScaleFactor = sourceWidth / outWidth;
        int heightScaleFactor = sourceHeight / outHeight;

        int scaleFactorFinal = Math.min(widthScaleFactor, heightScaleFactor);

        Logger.i(TAG,"calculateScaling scaleFactorFinal = " + scaleFactorFinal);

        int powerOfTwoSampleSize = Math.max(1, Integer.highestOneBit(scaleFactorFinal));

        Logger.i(TAG,"calculateScaling powerOfTwoSampleSize = " + powerOfTwoSampleSize);

        options.inSampleSize = powerOfTwoSampleSize;
    }

    private static int[] getBitmapDimensions(InputStream is, BitmapFactory.Options options) throws IOException {
        options.inJustDecodeBounds = true;
        try {
            is.mark(MARK_POSITION);
            BitmapFactory.decodeStream(is, null, options);
        } finally {
            is.reset();
        }
        options.inJustDecodeBounds = false;
        return new int[] { options.outWidth, options.outHeight };
    }

    public static void calculateConfig(InputStream inputStream, BitmapFactory.Options options) throws IOException {
        if(Build.VERSION.SDK_INT == Build.VERSION_CODES.JELLY_BEAN) {
            options.inPreferredConfig = Bitmap.Config.ARGB_8888;
            return ;
        }

        boolean hasAlpha = false;
        try {

            inputStream.mark(MARK_POSITION);
            ImageType imageType = ImageHeaderParser.getDefault().getType(inputStream);
            hasAlpha = imageType.hasAlpha();
            Logger.i(TAG,"calculateConfig enter, imageType = " + imageType + " , hasAlpha = " + hasAlpha);
        } catch (IOException e) {
            //ignore
            Logger.i(TAG,"calculateConfig enter, getType IOException");
            throw e;
        } finally {
            inputStream.reset();
        }

        options.inPreferredConfig =
                hasAlpha ? Bitmap.Config.ARGB_8888 : Bitmap.Config.RGB_565;
        if (options.inPreferredConfig == Bitmap.Config.RGB_565) {
            options.inDither = true; //抖动解码
            Logger.i(TAG,"calculateConfig enter, inDither true");
        }

    }

    private static int round(double value) {
        return (int) (value + 0.5d);
    }

}
