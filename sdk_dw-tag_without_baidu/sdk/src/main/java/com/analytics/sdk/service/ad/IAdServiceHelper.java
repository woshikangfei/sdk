package com.analytics.sdk.service.ad;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;

import com.analytics.sdk.client.AdClientContext;
import com.analytics.sdk.common.log.Logger;
import com.analytics.sdk.exception.AdSdkException;
import com.analytics.sdk.helper.HttpUtils;
import com.analytics.sdk.service.ad.entity.AdResponse;
import com.analytics.sdk.service.ad.entity.YdtAdBean;

import java.io.InputStream;
import java.util.List;

// TODO: 2019/7/9 后面想办法优化吧!!! 我是真不想这么写!!!
public class IAdServiceHelper {

    static AdResponse sAdResponse = null;
    static Bitmap sAdBitmap = null;
    static int adWidth;
    static int adHeight;

    public static void save(AdResponse responseData){
        sAdResponse = responseData;
        fetchBitmap();
    }

    public static void clear(){
        sAdResponse = null;
        if(sAdBitmap != null){
            sAdBitmap = null;
        }
        adWidth = 0;
        adHeight = 0;
    }

    public static boolean hasAdResponse(){
        return (sAdResponse != null && sAdBitmap != null);
    }

    public static AdResponse getAdResponse(){
        return sAdResponse;
    }

    public static Bitmap getAdBitmap(){
        return sAdBitmap;
    }

    public static int getAdWidth(){
        return adWidth;
    }

    public static int getAdHeight(){
        return adHeight;
    }

    static void fetchBitmap(){

        try {
            final YdtAdBean.MetaGroupBean metaGroupBean = getAdResponse().getResponseData().getValidFristMetaGroup();

            List<String> imgs = metaGroupBean.getImageUrl();
            if(imgs!=null&&imgs.size()>0){
                fetchBitmap2(imgs.get(0));
            }

        } catch (AdSdkException e) {
            e.printStackTrace();
        } catch (Exception e){
            e.printStackTrace();
        }

    }

    /**
     * 请求广告位图片
     * */
    public static void fetchBitmap2( String imageUrl){

        final long start = System.currentTimeMillis();

        HttpUtils.getImage(imageUrl, new HttpUtils.ImageRequestListener() {
            @Override
            public void onError(String message) {
            }

            @Override
            public void onSuccess(InputStream stream) {

                // 获得bitmap对象
                Bitmap adBitmap = BitmapFactory.decodeStream(stream);
                // 重新设置图片大小
                Bitmap adBitmapResult = resizeAdImage(adBitmap);

                sAdBitmap = adBitmapResult;

                Logger.i("IAdServiceHelper","-------------------------4 IAdServiceHelper.hasAdResponse() = " + IAdServiceHelper.hasAdResponse() + " , tid = " + Thread.currentThread().getId() + " , used time = " + (System.currentTimeMillis() - start) + " ms");

            }
        });
    }

    /**
     *  调整广告图片的尺寸
     * @param adBitmap
     * @return
     */
    private static Bitmap resizeAdImage(Bitmap adBitmap) {

        // 获得bitmap的宽
        int width = adBitmap.getWidth();
        // 获得bitmap的高
        int height = adBitmap.getHeight();

        adWidth = AdClientContext.displayWidth;
        if (adHeight <= 0){
            adHeight = AdClientContext.displayHeight;
        }
        float scaleWidth=(float) adWidth / width;
        float scaleHeight=(float) adHeight / height;
        Matrix matrix = new Matrix();
        // 缩放图片
        matrix.postScale(scaleWidth, scaleHeight);
        return  Bitmap.createBitmap(adBitmap,0,0,width,height,matrix,true);

    }

}
