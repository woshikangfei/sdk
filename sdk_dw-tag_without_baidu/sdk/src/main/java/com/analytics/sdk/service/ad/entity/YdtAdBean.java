package com.analytics.sdk.service.ad.entity;

import java.util.List;

/**
 * Created by yangminghui on 2018/11/2.
 */

public class YdtAdBean {

    private String view_id;
    private String adKey;
    private String adlogo;
    private String adtext;
    private String htmlSnippet;
    private String slotId;
    private int protocolType=0;
    private int fillType;
    private int xxlStyle;
    private int type;
    private String adsimg;
    private String bg;
    private List<MetaGroupBean> metaGroup;
    private List<TracksBean> tracks;
    /**
     * full : {"color":"#FFDAB9","fontSize":7,"gif":false,"imgUp":true,"lMargin":3,"rMargin":4,"textImgInner":6,"top":5,"url":"www.baidu.com"}
     * half : {"color":"#FFDAB9","fontSize":7,"gif":false,"imgUp":true,"lMargin":3,"rMargin":4,"textImgInner":6,"top":5,"url":"www.baidu.com"}
     */

    private FullBean full;
    private HalfBean half;


    public List<TracksBean> getTracks() {
        return tracks;
    }

    public void setTracks(List<TracksBean> tracks) {
        this.tracks = tracks;
    }

    public int getXxlStyle() {
        return xxlStyle;
    }

    public void setXxlStyle(int xxlStyle) {
        this.xxlStyle = xxlStyle;
    }

    public int getProtocolType() {
        return protocolType;
    }

    public void setProtocolType(int protocolType) {
        this.protocolType = protocolType;
    }

    public String getView_id() {
        return view_id;
    }

    public void setView_id(String view_id) {
        this.view_id = view_id;
    }

    public String getAdKey() {
        return adKey;
    }

    public void setAdKey(String adKey) {
        this.adKey = adKey;
    }

    public String getAdlogo() {
        return adlogo;
    }

    public void setAdlogo(String adlogo) {
        this.adlogo = adlogo;
    }

    public String getAdtext() {
        return adtext;
    }

    public void setAdtext(String adtext) {
        this.adtext = adtext;
    }

    public String getHtmlSnippet() {
        return htmlSnippet;
    }

    public void setHtmlSnippet(String htmlSnippet) {
        this.htmlSnippet = htmlSnippet;
    }

    public String getSlotId() {
        return slotId;
    }

    public void setSlotId(String slotId) {
        this.slotId = slotId;
    }

    public List<MetaGroupBean> getMetaGroup() {
        return metaGroup;
    }

    public void setMetaGroup(List<MetaGroupBean> metaGroup) {
        this.metaGroup = metaGroup;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getAdsimg() {
        return adsimg;
    }

    public void setAdsimg(String adsimg) {
        this.adsimg = adsimg;
    }

    public String getBg() {
        return bg;
    }

    public void setBg(String bg) {
        this.bg = bg;
    }

    public FullBean getFull() {
        return full;
    }

    public void setFull(FullBean full) {
        this.full = full;
    }

    public HalfBean getHalf() {
        return half;
    }

    public void setHalf(HalfBean half) {
        this.half = half;
    }

    public int getFillType() {
        return fillType;
    }

    public void setFillType(int fillType) {
        this.fillType = fillType;
    }


    public static class TracksBean {
        /**
         * type : 0
         * urls : ["http://track.bes.baidu.com/track.php?c=OAOKAgc1OTY2MDc3sAnD5dPfBco JEGEzNDI4NmFhZDdkMWU4NGSACgiqCghiY2M1NDQ5ZMIMFQj89e4KEOK7PBi-4bcBIP_kgwsoB-oSANAb AA&event=3&progress=${PROGRESS}"]
         */
        private int type;
        private List<String> urls;

        public int getType() {
            return type;
        }

        public void setType(int type) {
            this.type = type;
        }

        public List<String> getUrls() {
            return urls;
        }

        public void setUrls(List<String> urls) {
            this.urls = urls;
        }
    }

    public static class MetaGroupBean {

        //广点通字段
        private int adStyle;
        private String click_key;
        private String impression_key;
        private String conversion_key;

        private String end_card_html;
        private String action_txt;

        private int comments;
        private String imageSize;

        private String adMark;
        private String adTitle;
        private int appSize;
        private String brandName;
        private String clickUrl;
        private int creativeType;
        private int currentIndex;
        private int interactionType;
        private int materialHeight;
        private int materialWidth;
        private String packageName;
        private int totalNum;
        private int videoDuration;
        private String videoUrl;
        private String deepLink;
        private String strLinkUrl;
        private String downloadLink;
        private List<String> arrSkipTrackUrl;
        private List<String> descs;
        private List<String> iconUrls;
        private List<String> imageUrl;
        private List<AdBean> assets;
        private List<String> winNoticeUrls;
        private List<String> winCNoticeUrls;
        private List<String> arrDownloadTrackUrl;
        private List<String> arrDownloadedTrakUrl;
        private List<String> arrIntallTrackUrl;
        private List<String> arrIntalledTrackUrl;
        private int rating;

        public static class AdBean{
            private int materialType;
            private String url;

            public int getMaterialType() {
                return materialType;
            }

            public void setMaterialType(int materialType) {
                this.materialType = materialType;
            }

            public String getUrl() {
                return url;
            }

            public void setUrl(String url) {
                this.url = url;
            }
        }

        public int getAdStyle() {
            return adStyle;
        }

        public void setAdStyle(int adStyle) {
            this.adStyle = adStyle;
        }

        public String getClick_key() {
            if(click_key==null){
                return "";
            }

            return click_key;
        }

        public void setClick_key(String click_key) {
            this.click_key = click_key;
        }

        public String getImpression_key() {
            if(impression_key==null){
                return "";
            }
            return impression_key;
        }

        public void setImpression_key(String impression_key) {
            this.impression_key = impression_key;
        }

        public String getConversion_key() {
            if(conversion_key==null){
                return "";
            }
            return conversion_key;
        }

        public void setConversion_key(String conversion_key) {
            this.conversion_key = conversion_key;
        }

        public List<AdBean> getAssets() {
            return assets;
        }

        public void setAssets(List<AdBean> assets) {
            this.assets = assets;
        }

        public String getAdMark() {
            if(adMark==null){
                return "";
            }
            return adMark;
        }

        public void setAdMark(String adMark) {
            this.adMark = adMark;
        }

        public String getDeepLink() {
            if(deepLink==null){
                return "";
            }
            return deepLink;
        }

        public void setDeepLink(String deepLink) {
            this.deepLink = deepLink;
        }

        public String getStrLinkUrl() {
            return strLinkUrl;
        }

        public void setStrLinkUrl(String strLinkUrl) {
            this.strLinkUrl = strLinkUrl;
        }

        public List<String> getArrSkipTrackUrl() {
            return arrSkipTrackUrl;
        }

        public void setArrSkipTrackUrl(List<String> arrSkipTrackUrl) {
            this.arrSkipTrackUrl = arrSkipTrackUrl;
        }

        public String getDownloadLink() {
            return downloadLink;
        }

        public void setDownloadLink(String downloadLink) {
            this.downloadLink = downloadLink;
        }

        public String getAdTitle() {
            return adTitle;
        }

        public void setAdTitle(String adTitle) {
            this.adTitle = adTitle;
        }

        public int getAppSize() {
            return appSize;
        }

        public void setAppSize(int appSize) {
            this.appSize = appSize;
        }

        public String getBrandName() {
            if(brandName==null){
                return "";
            }
            return brandName;
        }

        public void setBrandName(String brandName) {
            this.brandName = brandName;
        }

        public String getClickUrl() {
            if(clickUrl==null){
                return "";
            }
            return clickUrl;
        }

        public void setClickUrl(String clickUrl) {
            this.clickUrl = clickUrl;
        }

        public int getCreativeType() {
            return creativeType;
        }

        public void setCreativeType(int creativeType) {
            this.creativeType = creativeType;
        }

        public int getCurrentIndex() {
            return currentIndex;
        }

        public void setCurrentIndex(int currentIndex) {
            this.currentIndex = currentIndex;
        }

        public int getInteractionType() {
            return interactionType;
        }

        public void setInteractionType(int interactionType) {
            this.interactionType = interactionType;
        }

        public int getMaterialHeight() {
            return materialHeight;
        }

        public void setMaterialHeight(int materialHeight) {
            this.materialHeight = materialHeight;
        }

        public int getMaterialWidth() {
            return materialWidth;
        }

        public void setMaterialWidth(int materialWidth) {
            this.materialWidth = materialWidth;
        }

        public String getPackageName() {
            return packageName;
        }

        public void setPackageName(String packageName) {
            this.packageName = packageName;
        }

        public int getTotalNum() {
            return totalNum;
        }

        public void setTotalNum(int totalNum) {
            this.totalNum = totalNum;
        }

        public int getVideoDuration() {
            return videoDuration;
        }

        public void setVideoDuration(int videoDuration) {
            this.videoDuration = videoDuration;
        }

        public String getVideoUrl() {
            return videoUrl;
        }

        public void setVideoUrl(String videoUrl) {
            this.videoUrl = videoUrl;
        }

        public List<String> getDescs() {
            return descs;
        }

        public void setDescs(List<String> descs) {
            this.descs = descs;
        }

        public List<String> getIconUrls() {
            return iconUrls;
        }

        public void setIconUrls(List<String> iconUrls) {
            this.iconUrls = iconUrls;
        }

        public List<String> getImageUrl() {
            return imageUrl;
        }

        public void setImageUrl(List<String> imageUrl) {
            this.imageUrl = imageUrl;
        }

        public List<String> getWinNoticeUrls() {
            return winNoticeUrls;
        }

        public void setWinNoticeUrls(List<String> winNoticeUrls) {
            this.winNoticeUrls = winNoticeUrls;
        }

        public List<String> getWinCNoticeUrls() {
            return winCNoticeUrls;
        }

        public void setWinCNoticeUrls(List<String> winCNoticeUrls) {
            this.winCNoticeUrls = winCNoticeUrls;
        }

        public List<String> getArrDownloadTrackUrl() {
            return arrDownloadTrackUrl;
        }

        public void setArrDownloadTrackUrl(List<String> arrDownloadTrackUrl) {
            this.arrDownloadTrackUrl = arrDownloadTrackUrl;
        }

        public List<String> getArrDownloadedTrakUrl() {
            return arrDownloadedTrakUrl;
        }

        public void setArrDownloadedTrakUrl(List<String> arrDownloadedTrakUrl) {
            this.arrDownloadedTrakUrl = arrDownloadedTrakUrl;
        }

        public List<String> getArrIntallTrackUrl() {
            return arrIntallTrackUrl;
        }

        public void setArrIntallTrackUrl(List<String> arrIntallTrackUrl) {
            this.arrIntallTrackUrl = arrIntallTrackUrl;
        }

        public List<String> getArrIntalledTrackUrl() {
            return arrIntalledTrackUrl;
        }

        public void setArrIntalledTrackUrl(List<String> arrIntalledTrackUrl) {
            this.arrIntalledTrackUrl = arrIntalledTrackUrl;
        }

        public int getComments() {
            return comments;
        }

        public void setComments(int comments) {
            this.comments = comments;
        }

        public String getImageSize() {
            return imageSize;
        }

        public void setImageSize(String imageSize) {
            this.imageSize = imageSize;
        }


        public int getRating() {
            return rating;
        }

        public void setRating(int rating) {
            this.rating = rating;
        }

        public String getEnd_card_html() {
            return end_card_html;
        }

        public void setEnd_card_html(String end_card_html) {
            this.end_card_html = end_card_html;
        }

        public String getAction_txt() {
            return action_txt;
        }

        public void setAction_txt(String action_txt) {
            this.action_txt = action_txt;
        }
    }

    public static class ThemeConfig {
        /**
         * color : #FFDAB9
         * fontSize : 7
         * gif : false
         * imgUp : true
         * lMargin : 3
         * rMargin : 4
         * textImgInner : 6
         * top : 5
         * url : www.baidu.com
         */

        private String color;
        private int fontSize;
        private boolean gif;
        private boolean imgUp;
        private int lMargin;
        private int rMargin;
        private int textImgInner;
        private int top;
        private String url;

        public String getColor() {
            return color;
        }

        public void setColor(String color) {
            this.color = color;
        }

        public int getFontSize() {
            return fontSize;
        }

        public void setFontSize(int fontSize) {
            this.fontSize = fontSize;
        }

        public boolean isGif() {
            return gif;
        }

        public void setGif(boolean gif) {
            this.gif = gif;
        }

        public boolean isImgUp() {
            return imgUp;
        }

        public void setImgUp(boolean imgUp) {
            this.imgUp = imgUp;
        }

        public int getLMargin() {
            return lMargin;
        }

        public void setLMargin(int lMargin) {
            this.lMargin = lMargin;
        }

        public int getRMargin() {
            return rMargin;
        }

        public void setRMargin(int rMargin) {
            this.rMargin = rMargin;
        }

        public int getTextImgInner() {
            return textImgInner;
        }

        public void setTextImgInner(int textImgInner) {
            this.textImgInner = textImgInner;
        }

        public int getTop() {
            return top;
        }

        public void setTop(int top) {
            this.top = top;
        }

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }
    }

    public static class FullBean extends ThemeConfig{

    }

    public static class HalfBean extends ThemeConfig{

    }
}
