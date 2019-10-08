package com.analytics.sdk.service.ad;

import android.graphics.Color;
import android.graphics.Point;
import android.graphics.Rect;
import android.text.TextUtils;
import android.util.Log;

import com.analytics.sdk.client.AdRequest;
import com.analytics.sdk.client.AdType;
import com.analytics.sdk.common.cache.CacheHelper;
import com.analytics.sdk.common.helper.AES;
import com.analytics.sdk.common.helper.DateHelper;
import com.analytics.sdk.common.http.Response;
import com.analytics.sdk.common.http.error.VolleyError;
import com.analytics.sdk.common.http.toolbox.HttpHelper;
import com.analytics.sdk.common.http.toolbox.JsonObjectPostRequest;
import com.analytics.sdk.common.log.Logger;
import com.analytics.sdk.config.AdConfig;
import com.analytics.sdk.helper.SdkHelper;
import com.analytics.sdk.service.common.RequestParameterBuilder;

import org.json.JSONObject;

import java.util.LinkedHashMap;

/**
 * 点击热力图
 */
public final class ClickMap {

    private static final String TAG = ClickMap.class.getSimpleName();

    final int[] colorArrayDefault = new int[] {
            Color.RED,Color.BLUE,Color.GREEN,Color.GRAY,Color.YELLOW,Color.WHITE,Color.GREEN,Color.RED,Color.MAGENTA,Color.LTGRAY
    };

    int[] colorArray;

    int rowCellSize = 0;
    int columnCellSize = 0;
    int[][] cellValueArray;

    Rect selectRect = new Rect();
    String clickMapData;

    public void parseCellArray(String clickMapData){
        this.clickMapData = clickMapData;
        String[] lines = clickMapData.split("#");

        String line = lines[0];

        String[] lineResult = line.split(",");
        int rowSize = Integer.valueOf(lineResult[0]);
        int columnSize = Integer.valueOf(lineResult[1]);

        rowCellSize = rowSize;
        columnCellSize = columnSize;

        cellValueArray = new int[rowCellSize][columnCellSize];

        Log.i(TAG,"IAdStrategyServiceImpl line = " + line);

        for(int i = 1; i < lines.length;i++){

            line = lines[i];

            lineResult = line.split(",");
            int columnNum = Integer.valueOf(lineResult[0]);
            int rowNum = Integer.valueOf(lineResult[1]);
            int value = Integer.valueOf(lineResult[2]);

            Log.i(TAG,"IAdStrategyServiceImpl rowNum = " + rowNum + " , columnNum = " + columnNum + " , value = " + value);

            cellValueArray[rowNum][columnNum] = value;
        }

        if(AdConfig.getDefault().isPrintLog()){

            colorArray = new int[rowCellSize];

            int r = rowSize / colorArrayDefault.length;
            int colorIndex = 0;
            for(int i = 0; i < rowSize; i++){

                if(colorIndex >= colorArrayDefault.length - 1){
                    colorIndex = 0;
                }

                Log.i(TAG,"IAdStrategyServiceImpl colorIndex = " + colorIndex + " , rowSize = " + rowSize);
                colorArray[i] = colorArrayDefault[colorIndex];

                ++colorIndex;
            }
        }
    }

    int getCellRandom(){
        int lastData = cellValueArray[rowCellSize - 1][columnCellSize - 1];
        return SdkHelper.getRandom(0,lastData);
    }

    int getRowIndex(int randomData){
        int index = 0;

        for(int i = 0;i < rowCellSize;i++){

            int min = cellValueArray[i][0];
            int max = cellValueArray[i][rowCellSize-1];

            if(min >= randomData){
                index = i;
                break;
            }

            if(max >= randomData){
                index = i;
                break;
            }

        }

        return index;

    }

    public int getRowCellSize() {
        return rowCellSize;
    }

    public void setRowCellSize(int rowCellSize) {
        this.rowCellSize = rowCellSize;
    }

    public int getColumnCellSize() {
        return columnCellSize;
    }

    public void setColumnCellSize(int columnCellSize) {
        this.columnCellSize = columnCellSize;
    }

    public int[][] getCellValueArray() {
        return cellValueArray;
    }

    public void setCellValueArray(int[][] cellValueArray) {
        this.cellValueArray = cellValueArray;
    }

    int getColumnIndex(int rowIndex, int randomData){

        int index = 0;

        if(cellValueArray[rowIndex][0] > randomData){
            return 0;
        }

        for(int i = 0;i < columnCellSize;i++){
            int value1 = cellValueArray[rowIndex][i];
            if(i+1 >= columnCellSize - 1){
                index = columnCellSize - 1;
                break;
            }

            int value2 = cellValueArray[rowIndex][i + 1];

            if(randomData > value1 && randomData <= value2){
                index = i + 1;
                break;
            }

        }

        return index;
    }

    public boolean isRealy(){
        return (cellValueArray != null && rowCellSize > 0 && columnCellSize > 0);
    }

    public Point getCellRandomPoint(int viewWidth,int viewHeight){

        if(cellValueArray == null){
            return null;
        }

        try {

            int cellRandom = getCellRandom();

            int rowIndex = getRowIndex(cellRandom);
            int columnIndex = getColumnIndex(rowIndex,cellRandom);

            int cellWidth = viewWidth / rowCellSize;
            int cellHeight = viewHeight / columnCellSize;

            Logger.i(TAG,"rowIndex = " + rowIndex + " , columnIndex = " + columnIndex + " , cellRandom = " + cellRandom);

            int top = ((rowIndex) * cellHeight);
            int bottom = ((rowIndex + 1) * cellHeight);

            int left = columnIndex * cellWidth;
            int right = ((columnIndex + 1) * cellWidth);

            int padding = 0;

            int x = SdkHelper.getRandom(left + padding,right - padding);
            int y = SdkHelper.getRandom(top + padding,bottom - padding);

            Point finalPoint = new Point(x,y);

            return finalPoint;

        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    public void requestClickMap(AdRequest adRequest){

        String codeIdCacheKey = SdkHelper.getCacheKeyWithRequestCodeId(adRequest);

        Logger.i(TAG,"requestClickMap enter , codeId = " + codeIdCacheKey);

        try {
            final String cacheKey = codeIdCacheKey+"_"+"click_map";
            final String cacheDateKey = codeIdCacheKey+"_"+DateHelper.currentDate();

            String clickMap = CacheHelper.getHelper().getAsString(cacheKey);
            String hasDateTime = CacheHelper.getHelper().getAsString(cacheDateKey);

            Logger.i(TAG,"hasDateTime = " + hasDateTime);

            if(!TextUtils.isEmpty(clickMap) && !TextUtils.isEmpty(hasDateTime)) {
                Logger.i(TAG,"requestClickMap hit cache");
                parseCellArray(clickMap);
                return;
            }

            String defaultClickMap = ClickMap.getDefaultClickMap(adRequest);
            if(!TextUtils.isEmpty(defaultClickMap)) {
                parseCellArray(defaultClickMap);
            } else {
                Logger.i(TAG,"default click map is empty");
            }

            final String requestUrl = AdConfig.getDefault().getServerEnvConfig().getClickMapUrl();
            JSONObject requestParams = RequestParameterBuilder.buildJsonObjectParameters();

            requestParams.put("channelid",adRequest.getCodeId());

            Logger.printJson(SdkHelper.format(requestParams.toString()),"ClickMap#requestClickMap requestUlr = "+requestUrl+" , params ↓");

            JsonObjectPostRequest jsonObjectPostRequest = new JsonObjectPostRequest(requestUrl, requestParams, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {

                    try {
                        if(TextUtils.isEmpty(response)){
                            Logger.i(TAG,"*requestClickMap.onResponse empty*");
                            return;
                        }
                        String decodeResult = AES.decode(response);

                        Logger.i(TAG,decodeResult);

                        JSONObject adJsonData = new JSONObject(decodeResult);

                        if(adJsonData.has("code")){
                            int code = adJsonData.getInt("code");

                            if(code == 1){

                                if(adJsonData.has("data")){
                                    JSONObject data = adJsonData.getJSONObject("data");

                                    String clickMapString = data.getString("sdk_click_map");

                                    Logger.i(TAG,"clickMapString = " + clickMapString);

                                    parseCellArray(clickMapString);

                                    CacheHelper.getHelper().put(cacheKey,clickMapString);
                                    CacheHelper.getHelper().put(cacheDateKey,"save_time",24 * 60 * 60);

                                }

                            } else if(code == -1000){
                                CacheHelper.getHelper().remove(cacheKey);
                                CacheHelper.getHelper().remove(cacheDateKey);
                            }

                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                        Logger.i(TAG,"requestClickMap.onResponse handle exception " + e.getMessage());
                    }

                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    error.printStackTrace();
                    Logger.i(TAG,"requestClickMap.onErrorResponse enter, error = " + error.getMessage());
                }
            });

            HttpHelper.send(jsonObjectPostRequest);
        } catch (Exception e){
            e.printStackTrace();
            parseCellArray(ClickMap.getDefaultClickMap(adRequest));
        }
    }

    public int[] getColorArray() {
        return colorArray;
    }

    static final class DefualtClickMap {
        static final LinkedHashMap<AdType,String> clickCellMapping = new LinkedHashMap<>();

        static final String DEFAULT_SPLASH_TEXT = "10,10#" +
                "0,0,1112#" +
                "1,0,2475#" +
                "2,0,3765#" +
                "3,0,4820#" +
                "4,0,5738#" +
                "5,0,6763#" +
                "6,0,7934#" +
                "7,0,9790#" +
                "8,0,9790#" +
                "9,0,9790#" +
                "0,1,10299#" +
                "1,1,11070#" +
                "2,1,11810#" +
                "3,1,12630#" +
                "4,1,13362#" +
                "5,1,14131#" +
                "6,1,14863#" +
                "7,1,15533#" +
                "8,1,16473#" +
                "9,1,17015#" +
                "0,2,17455#" +
                "1,2,17780#" +
                "2,2,18132#" +
                "3,2,18577#" +
                "4,2,19005#" +
                "5,2,19416#" +
                "6,2,19768#" +
                "7,2,20092#" +
                "8,2,20561#" +
                "9,2,20922#" +
                "0,3,21304#" +
                "1,3,21603#" +
                "2,3,21954#" +
                "3,3,22349#" +
                "4,3,22845#" +
                "5,3,23309#" +
                "6,3,23647#" +
                "7,3,23996#" +
                "8,3,24255#" +
                "9,3,24558#" +
                "0,4,25005#" +
                "1,4,25306#" +
                "2,4,25657#" +
                "3,4,26046#" +
                "4,4,26657#" +
                "5,4,27252#" +
                "6,4,27632#" +
                "7,4,27939#" +
                "8,4,28319#" +
                "9,4,28669#" +
                "0,5,29213#" +
                "1,5,29588#" +
                "2,5,29992#" +
                "3,5,30429#" +
                "4,5,30998#" +
                "5,5,31673#" +
                "6,5,32153#" +
                "7,5,32582#" +
                "8,5,33058#" +
                "9,5,33558#" +
                "0,6,34015#" +
                "1,6,34292#" +
                "2,6,35144#" +
                "3,6,36141#" +
                "4,6,37026#" +
                "5,6,38506#" +
                "6,6,39199#" +
                "7,6,39916#" +
                "8,6,40290#" +
                "9,6,40793#" +
                "0,7,41226#" +
                "1,7,41515#" +
                "2,7,41886#" +
                "3,7,42364#" +
                "4,7,43788#" +
                "5,7,46290#" +
                "6,7,47102#" +
                "7,7,47606#" +
                "8,7,47927#" +
                "9,7,48377#" +
                "0,8,48770#" +
                "1,8,49069#" +
                "2,8,49421#" +
                "3,8,49953#" +
                "4,8,51395#" +
                "5,8,54020#" +
                "6,8,55015#" +
                "7,8,55544#" +
                "8,8,55941#" +
                "9,8,56377#" +
                "0,9,56786#" +
                "1,9,57095#" +
                "2,9,57366#" +
                "3,9,57759#" +
                "4,9,58424#" +
                "5,9,59634#" +
                "6,9,60244#" +
                "7,9,60723#" +
                "8,9,61080#" +
                "9,9,61609#";

        static {
            clickCellMapping.put(AdType.SPLASH,DEFAULT_SPLASH_TEXT);
            //clickCellMapping.put(AdType.INFORMATION_FLOW,DEFAULT_SPLASH_TEXT);
            //clickCellMapping.put(AdType.BANNER,DEFAULT_SPLASH_TEXT);
        }

    }

    public static String getDefaultClickMap(AdRequest adRequest){
        AdType adType = adRequest.getAdType();
        return getDefaultClickMap(adType);
    }

    public static String getDefaultClickMap(AdType adType){
        String clickMap = DefualtClickMap.clickCellMapping.get(adType);
        if(TextUtils.isEmpty(clickMap)){
            return "";
        }
        return clickMap;
    }

    @Override
    public String toString() {
        return "ClickMap{" +
                "rowCellSize=" + rowCellSize +
                ", columnCellSize=" + columnCellSize +
                ", cellValueArray=" + clickMapData +
                '}';
    }
}
