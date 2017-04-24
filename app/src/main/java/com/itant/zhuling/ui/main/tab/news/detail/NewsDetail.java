package com.itant.zhuling.ui.main.tab.news.detail;

import java.util.List;

/**
 * Created by iTant on 2017/4/24.
 */

public class NewsDetail {
    private String source;      // 来源
    private String ptime;       // 时间
    private String shareLink;   // 分享链接
    private String body;        // 内容

    private List<ImgBean> img;  // 内容里的图片

    public List<ImgBean> getImg() {
        return img;
    }

    public void setImg(List<ImgBean> img) {
        this.img = img;
    }

    public static class ImgBean {
        /**
         * ref : <!--IMG#0-->
         * pixel : 595*335
         * alt :
         * src : http://cms-bucket.nosdn.127.net/8e2c717332ad4852a1ac769332f498ac20170424105147.jpeg
         */

        private String ref;
        private String pixel;
        private String alt;
        private String src;

        public String getRef() {
            return ref;
        }

        public void setRef(String ref) {
            this.ref = ref;
        }

        public String getPixel() {
            return pixel;
        }

        public void setPixel(String pixel) {
            this.pixel = pixel;
        }

        public String getAlt() {
            return alt;
        }

        public void setAlt(String alt) {
            this.alt = alt;
        }

        public String getSrc() {
            return src;
        }

        public void setSrc(String src) {
            this.src = src;
        }
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getPtime() {
        return ptime;
    }

    public void setPtime(String ptime) {
        this.ptime = ptime;
    }

    public String getShareLink() {
        return shareLink;
    }

    public void setShareLink(String shareLink) {
        this.shareLink = shareLink;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }
}
