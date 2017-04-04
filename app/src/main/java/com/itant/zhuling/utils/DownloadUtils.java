package com.itant.zhuling.utils;

public class DownloadUtils {
  
   /* private OkHttpClient httpUtils;
  
    public DownloadUtils() {  
        httpUtils = new OkHttpClient();
    }  
  
    *//**
     * 定义下载方法，使用rx的编程思想 
     *
     * @param url
     * @return
     *//*
    public Observable<byte[]> downloadImage(final String url, final RequestBody requestBody) {
        //创建被观察者  
        return Observable.create(new Observable.OnSubscribe<byte[]>() {  
            @Override  
            public void call(final Subscriber<? super byte[]> subscriber) {
                //判断观察者和被观察者是否存在订阅关系  
                if (!subscriber.isUnsubscribed()) {  
                    Request request = new Request.Builder().url(url).post(requestBody).build();

                    if (httpUtils != null) {
                        httpUtils.newCall(request).enqueue(new Callback() {
                            @Override
                            public void onFailure(Call call, IOException e) {
                                subscriber.onError(e);
                            }

                            @Override
                            public void onResponse(Call call, Response response) throws IOException {
                                if (response.isSuccessful()) {
                                    //拿到结果的一瞬间触发事件，并传递数据给观察者
                                    //把请求结果转化成字节数组
                                    byte[] bytes = response.body().bytes();
                                    subscriber.onNext(bytes);
                                }
                                //数据发送已经完成
                                subscriber.onCompleted();
                            }
                        });
                    }
                }  
            }  
        });
    }

    public DownloadUtils addTrustAllSSL(String url) {
        if (url.startsWith("https")) {
            OkHttpClient.Builder builder = new OkHttpClient.Builder().hostnameVerifier(new HostnameVerifier() {
                @Override
                public boolean verify(String s, SSLSession sslSession) {
                    return true;
                }
            });

            TrustManager[] trustAllCerts = new TrustManager[] { new X509TrustManager() {
                @Override
                public void checkClientTrusted(
                        java.security.cert.X509Certificate[] x509Certificates,
                        String s) throws java.security.cert.CertificateException {
                }

                @Override
                public void checkServerTrusted(
                        java.security.cert.X509Certificate[] x509Certificates,
                        String s) throws java.security.cert.CertificateException {
                }

                @Override
                public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                    return new java.security.cert.X509Certificate[] {};
                }
            } };


            try {
                SSLContext sc = SSLContext.getInstance("TLS");
                sc.init(null, trustAllCerts, new java.security.SecureRandom());
                builder.sslSocketFactory(sc.getSocketFactory());
            } catch (Exception e) {
                e.printStackTrace();
            }


            httpUtils = builder.protocols(Collections.singletonList(Protocol.HTTP_1_1)).build();
        }

        return  this;
    }*/
}  