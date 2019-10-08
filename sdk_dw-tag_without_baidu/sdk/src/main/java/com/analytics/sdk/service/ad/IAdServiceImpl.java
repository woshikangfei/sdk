package com.analytics.sdk.service.ad;

import android.content.Context;
import android.os.Environment;
import android.text.TextUtils;
import android.util.Pair;

import com.analytics.sdk.client.AdError;
import com.analytics.sdk.client.AdListeneable;
import com.analytics.sdk.client.AdRequest;
import com.analytics.sdk.client.AdType;
import com.analytics.sdk.common.cache.CacheHelper;
import com.analytics.sdk.common.helper.AES;
import com.analytics.sdk.common.helper.Listener;
import com.analytics.sdk.common.http.Response;
import com.analytics.sdk.common.http.error.VolleyError;
import com.analytics.sdk.common.http.toolbox.HttpHelper;
import com.analytics.sdk.common.http.toolbox.JsonObjectPostRequest;
import com.analytics.sdk.common.log.Logger;
import com.analytics.sdk.common.runtime.ThreadExecutor;
import com.analytics.sdk.config.AdConfig;
import com.analytics.sdk.config.CodeIdConfig;
import com.analytics.sdk.config.ServerInitConfig;
import com.analytics.sdk.exception.AdSdkException;
import com.analytics.sdk.exception.AdSdkRuntimeException;
import com.analytics.sdk.helper.PublicUtils;
import com.analytics.sdk.helper.SdkHelper;
import com.analytics.sdk.service.AbstractService;
import com.analytics.sdk.service.AdErrorFactory;
import com.analytics.sdk.service.ErrorCode;
import com.analytics.sdk.service.ErrorMessage;
import com.analytics.sdk.service.ad.entity.AdRequestParameters;
import com.analytics.sdk.service.ad.entity.AdResponse;
import com.analytics.sdk.service.ad.entity.ResponseData;
import com.analytics.sdk.service.common.RequestParameterBuilder;
import com.analytics.sdk.view.handler.AdHandler;
import com.analytics.sdk.view.handler.AdHandlerFactory;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * 提供所有广告相关服务
 */
public class IAdServiceImpl extends AbstractService implements IAdService {

    static final String TAG = IAdServiceImpl.class.getSimpleName();
    String adConfigCodeId = "init_code_id";
    /**
     * 支持缓存的广告类型
     */
    final List<AdType> supportCacheAdTypeList = new ArrayList<>();

    public IAdServiceImpl() {
        super(IAdServiceImpl.class);
    }

    @Override
    public void init(final Context context) {
        super.init(context);
        adConfigCodeId = adConfigCodeId+"_"+AdConfig.getDefault().getSdkVersion();
        supportCacheAdTypeList.add(AdType.SPLASH);
        if(SdkHelper.isSupportInit(context)){
            ThreadExecutor.runOnCachedThreadPool(new Runnable() {
                @Override
                public void run() {
                    try {
                        initAdConfig(context);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }
            });

        } else {
            log(IAdService.class,"don't initAdConfig");
        }
    }

    @Override
    public ResponseData getDefaultResponseData(AdRequest adClientRequest) {
        return ResponseData.obtainDefault(adClientRequest);
    }

    @Override
    public void loadAdData(final AdRequest clientRequest, Listener listener) {

        if(clientRequest == null){
            throw new AdSdkRuntimeException("AdRequest is null");
        }

        log(IAdServiceImpl.class,"loadAdData enter , " + clientRequest.toString());

        final Listener clientListener = (listener == null ? Listener.EMPTY : listener);

        boolean isSupportCache = isSupportCache(clientRequest);
        //尝试走缓存
        // TODO: 2019/7/1 首次开屏的误点率
        final ResponseData cacheData = tryUseCache(clientRequest,clientListener);
        boolean isOkCache = !cacheData.isNoReponse();
        boolean validateResult = responseValidator(cacheData);
        boolean isUseCache = AdConfig.getDefault().getServerInitConfig().isUseCache();
        boolean isForceRequestServer = AdConfig.getDefault().getServerInitConfig().isForceRequestServer();

        Logger.i(TAG,"isSupportCache = "+isSupportCache+" , isOkCache = " + isOkCache + " , validateResult = " + validateResult + ", isUseCache = " + isUseCache + " , isForceRequestServer = " + isForceRequestServer);

        if(isOkCache && validateResult && isUseCache){ //缓存数据可用

            log(IAdServiceImpl.class,"* cache hit *");
            clientSuccessNotifier(clientRequest,listener,cacheData);

            requestAdData(clientRequest,clientListener,cacheData);

        } else { //是否有配置默认的3rd SDK

            if(isForceRequestServer){
                //请求服务器获取数据
                requestAdData(clientRequest, clientListener, null);
            } else {
                final ResponseData defaultResponseData = getDefaultResponseData(clientRequest);

                if(!defaultResponseData.isNoReponse()) { //存在默认配置,直接走默认配置去请求3rd sdk，后面需要继续请求服务器更新缓存
                    log(IAdServiceImpl.class,"* request with default ad config *");
                    clientSuccessNotifier(clientRequest,listener,defaultResponseData);
                }

                //请求服务器获取数据
                requestAdData(clientRequest, clientListener, defaultResponseData);
            }

        }

    }

    @Override
    public ResponseData loadAdDataFromCache(AdRequest adRequest) {
        return tryUseCache(adRequest,null);
    }

    @Override
    public void requestServerAdData(AdRequest adClientRequest, Listener<AdResponse, String> listener) {
        requestAdData(adClientRequest,listener,null);
    }

    @Override
    public CodeIdConfig getCodeIdConfig(String codeId) {

        CodeIdConfig adServerConfig = AdConfig.getDefault().getServerInitConfig().getCodeIdConfig(codeId);

        return (adServerConfig == null ? CodeIdConfig.EMPTY : adServerConfig);
    }

    /**
     * 请求服务器的广告数据或配置
     *
     * @param existResponseData 这个数据有可能是缓存的数据或者默认配置数据, 如果为空就表示不存在缓存和默认数据
     */
    private void requestAdData(final AdRequest clientRequest, final Listener clientListener, final ResponseData existResponseData) {
        JSONObject requestParams = AdRequestParameters.buildRequest2(clientRequest.getActivity(), clientRequest);
//        AdRequestParameters requestInfo = AdRequestParameters.buildRequest2(clientRequest.getActivity(), clientRequest);
//        JSONObject requestParams = requestInfo.toJson();
        String requestUrl = AdConfig.getDefault().getServerEnvConfig().getAdsUrl();
        Logger.printJson(SdkHelper.format(requestParams.toString()),"IAdServiceImpl#requestAdData requestUlr = "+requestUrl+" , params ↓");

        JsonObjectPostRequest jsonObjectPostRequest = new JsonObjectPostRequest(requestUrl, requestParams, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                log(IAdServiceImpl.class,"requestServerAdData.onResponse enter");

                Pair<ResponseData,Listener.ErrorMessage> pair = handleReponse(clientRequest,response,clientListener);

                ResponseData responseData = pair.first;
                Listener.ErrorMessage errorMessage = pair.second;

                //是否使用了缓存或者默认数据
                boolean isUseDefaultOrCacheData = (existResponseData == null || existResponseData.isNoReponse());

                if(!responseData.isNoReponse()){ //服务器正常返回,解析数据成功

                    if(isUseDefaultOrCacheData) { //没有使用默认代码位或者缓存数据不存在
                        log(IAdServiceImpl.class,"requestServerAdData invoke onSuccess callback");
                        clientSuccessNotifier(clientRequest,clientListener,responseData);
                    }

                    //更新缓存
                    updateCache(clientRequest,responseData,response);

                } else if(errorMessage != null && (isUseDefaultOrCacheData)){ //服务器没有正常返回,并且也没有使用默认代码或者缓存,则直接通知开发者
                    clientListener.onError(errorMessage);
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
                log(IAdServiceImpl.class,"loadAdData.onErrorResponse enter");

                if(existResponseData == null || existResponseData.isNoReponse()){//不存在默认代码位或者缓存的情况下失败了，直接通知开发者错误
                    clientListener.errorNotifier(error);
                }
            }
        });

        HttpHelper.send(jsonObjectPostRequest);
    }

    private ResponseData tryUseCache(AdRequest adRequest,Listener clientListener){
        log(IAdServiceImpl.class,"tryUseCache enter");

        if(isSupportCache(adRequest)){
            String cacheKey = SdkHelper.getCacheKeyWithRequestCodeId(adRequest);
            Logger.i(TAG,"tryUseCache key = " + cacheKey);
            String cacheData = getCacheData(cacheKey);
            return parseResponseData(cacheData,adRequest,clientListener);
        }

        return ResponseData.NO_RESPONSE;

    }

    private boolean isSupportCache(AdRequest adRequest){
        if(adRequest != null){
            return supportCacheAdTypeList.contains(adRequest.getAdType());
        }
        return false;
    }

    private String getCacheData(String cacheKey) {
        String cacheResponse = CacheHelper.getHelper().getAsString(cacheKey);
        return cacheResponse;
    }

    private ResponseData parseResponseData(String cacheResponse, AdRequest adRequest,Listener clientListener){
        if(TextUtils.isEmpty(cacheResponse)) {
            log(IAdServiceImpl.class,"cache(data provider) empty");
            return ResponseData.NO_RESPONSE;
        }

        Pair<ResponseData,Listener.ErrorMessage> pair = handleReponse(adRequest,cacheResponse,clientListener);
        ResponseData responseData =  pair.first;
        return responseData;
    }

    /**
     * 更新缓存
     */
    private void updateCache(final AdRequest clientRequest,final ResponseData responseData,String response){
        if(responseData.isUseCache() && isSupportCache(clientRequest)) {
            updateCache(SdkHelper.getCacheKeyWithRequestCodeId(clientRequest),response);
        }
    }

    private void removeCache(String codeId){
        CacheHelper.getHelper().remove(codeId);
    }

    private void updateCache(String cacheKey,String response){
        CacheHelper.getHelper().remove(cacheKey);
        CacheHelper.getHelper().put(cacheKey,response);
        Logger.i(TAG,"*updateCache(helper_impl:"+cacheKey+") success*");
    }

    private Pair<ResponseData,Listener.ErrorMessage> handleReponse(final AdRequest adClientRequest, String response, final Listener clientListener) {
        try {
            log(IAdServiceImpl.class,"handleReponse enter");
            // 取出回应字串
            if (TextUtils.isEmpty(response)) {
                Logger.i(TAG,"response is empty");
                return new Pair(ResponseData.NO_RESPONSE,Listener.ErrorMessage.obtain(ErrorCode.ApiServer.ERROR_SERVER_PARAMS,ErrorMessage.Ad.ERROR_NO_AD));
            }
            response = AES.decode(response);

            Logger.printJson(response,"*** aes response decode result");

            if (PublicUtils.isEmpty(response)||response.equals("{}")) {
                log(IAdServiceImpl.class,"decode result is empty");
                return new Pair(ResponseData.NO_RESPONSE,Listener.ErrorMessage.obtain(ErrorCode.ApiServer.ERROR_AES_DECODE_DATA,ErrorMessage.Ad.ERROR_NO_AD));
            }
            // 解析返回数据
            ResponseData responseData = ResponseData.build(response);
            Logger.i(TAG,String.format("parse decore reponse %s ",(responseData != null ? "ok" : "error")));
            if(responseData!=null){

                String errorCode = responseData.getErrorCode();

                if("0".equals(errorCode)){

                    Logger.i(TAG,"isSdkSource = " + responseData.isSdkSource() + " , isUseCache = " + responseData.isUseCache() + " , client request adType = " + adClientRequest.getAdType() + ", cacheValidTime = " + responseData.getCacheValidTime());

                    return new Pair(responseData,null);

                } else if("2000".equals(errorCode)){

                    AdError adError = AdErrorFactory.factory().create(ErrorCode.SPAM);
                    return new Pair(ResponseData.NO_RESPONSE,Listener.ErrorMessage.obtain(adError.getErrorCode(),adError.getErrorMessage()));

                } else {

                    return new Pair(ResponseData.NO_RESPONSE,Listener.ErrorMessage.obtain(ErrorCode.ApiServer.ERROR_SERVER_RESPONSE,ErrorMessage.Ad.ERROR_NO_AD));
                }

            }else{
                return new Pair(ResponseData.NO_RESPONSE,Listener.ErrorMessage.obtain(ErrorCode.ApiServer.ERROR_SERVER_RESPONSE,ErrorMessage.Ad.ERROR_NO_AD));
            }

        } catch (JSONException e){
            Logger.i(TAG,"handleReponse JSONException msg = " + e.getMessage());
            e.printStackTrace();
            return new Pair(ResponseData.NO_RESPONSE,Listener.ErrorMessage.obtain(ErrorCode.ApiServer.ERROR_PARSE_JSON,ErrorMessage.Ad.ERROR_NO_AD));
        } catch (AES.AESDecodeException e) {
            Logger.i(TAG,"handleReponse AESDecodeException msg = " + e.getMessage());
            e.printStackTrace();
            return new Pair(ResponseData.NO_RESPONSE,Listener.ErrorMessage.obtain(ErrorCode.ApiServer.ERROR_AES_DECODE_DATA,ErrorMessage.Ad.ERROR_NO_AD));
        } catch (Exception e) {
            e.printStackTrace();
            Logger.i(TAG,"handleReponse Exception msg = " + e.getMessage());
            return new Pair(ResponseData.NO_RESPONSE,Listener.ErrorMessage.obtain(ErrorCode.ApiServer.ERROR_UNKNOW,ErrorMessage.Ad.ERROR_NO_AD));
        }

    }

    private void fillAdServerConfigMapWithCache(String codeId) throws JSONException {

        String decodeResult = getCacheData(codeId);
        if(!TextUtils.isEmpty(decodeResult)) {
            Logger.printJson(SdkHelper.format(decodeResult),"fillAdServerConfigMapWithCache hit it ↓");
            parseServerInitConfig(decodeResult,false);
        } else {
            Logger.i(TAG,"fillAdServerConfigMapWithCache not hit");
        }
    }

    private void parseServerInitConfig(String response, boolean isUpdateCache) throws JSONException {

        ServerInitConfig serverInitConfig = ServerInitConfig.parse(response);

        AdConfig.getDefault().setServerInitConfig(serverInitConfig);

        if(serverInitConfig.isClearPSCache()){
            serverInitConfig.clearServerAdConfig();
        } else if(serverInitConfig.isClearSpamCache()) {
            serverInitConfig.clearSpam();
        }

        if(isUpdateCache){
            updateCache(adConfigCodeId,response);
        }

    }

    public void initAdConfig(final Context context) throws JSONException {
        log(IAdService.class,"initAdConfig enter");

        try {
            fillAdServerConfigMapWithCache(adConfigCodeId);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        final String requestUrl = AdConfig.getDefault().getServerEnvConfig().getInitUrl();
        JSONObject requestParams = RequestParameterBuilder.buildJsonObjectParameters();

        Logger.printJson(SdkHelper.format(requestParams.toString()),"IAdServiceImpl#initAdConfig requestUlr = "+requestUrl+" , params ↓");

        JsonObjectPostRequest jsonObjectPostRequest = new JsonObjectPostRequest(requestUrl, requestParams, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                try {
                    if(TextUtils.isEmpty(response)){
                        Logger.i(TAG,"*init.onResponse empty*");
                        return;
                    }
                    String decodeResult = AES.decode(response);

                    Logger.printJson(SdkHelper.format(decodeResult),"updateAdConfig from server("+requestUrl+") ↓");

                    parseServerInitConfig(decodeResult,true);

                } catch (Exception e) {
                    e.printStackTrace();
                    log(IAdServiceImpl.class,"init.onResponse handle exception " + e.getMessage() + " ,requestUrl = " + requestUrl + ", response = " + response);
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
                log(IAdServiceImpl.class,"init.onErrorResponse enter, error = " + error.getMessage());
            }
        });

        HttpHelper.send(jsonObjectPostRequest);

    }

    @Override
    public AdHandler handleApiAd(final AdResponse adResponse, final AdListeneable adListeneable) throws AdSdkException {
        adResponse.clear3rdSdkConfig(); //表示要走API的流程

        AdHandler adHandler = AdHandlerFactory.factory().createAdHandler(adResponse);
        adHandler.handleAd(adResponse,adListeneable);

        return adHandler;
    }

    private void clientSuccessNotifier(AdRequest adClientRequest, Listener clientListener, ResponseData responseData) {
        clientListener.onSuccess(Listener.SuccessMessage.obtain(AdResponse.obtain(adClientRequest,responseData,clientListener)));
    }

    @Override
    public AdHandler getAdHandler(AdResponse adResponse) {
        return AdHandlerFactory.factory().createAdHandler(adResponse);
    }

    /**
     * 主要负责验证返回的数据是否正确
     */
    private boolean responseValidator(ResponseData responseData) {

        //如果是sdk处理，但找不到对应的sdk配置
        if(responseData.isSdkSource() || responseData.isApiSource()) {
            return true;
        }

        return false;
    }

}
