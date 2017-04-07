package com.itant.zhuling.api;


 
/* Retrofit 2.0 */

import com.itant.zhuling.ui.tab.music.MusicBean;

import java.util.List;
import java.util.Map;

import io.reactivex.Observable;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Path;

public interface APIService {

    @GET("nc/article/{type}/{id}/{startPage}-20.html")
    Call<List<MusicBean>> loadRepository(
                                  @Path("type") String type, @Path("id") String id,
                                  @Path("startPage") int startPage);



    @GET("nc/article/{type}/{id}/{startPage}-20.html")
    Observable<Map<String, List<MusicBean>>> getRepository(
            @Header("Cache-Control") String cacheControl,
            @Path("type") String type, @Path("id") String id,
            @Path("startPage") int startPage);

}