package com.simple.lightnote.net2;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

/**
 * Created by yangpeiyong on 16/1/12.
 */
public interface ApiService {

   // http://gank.avosapps.com/api/data/Android/2/2

//    @GET("Android/{pageSize}/{pageCount}")
//    Call<ReceiveData.ArticleListResponse> articles(@Path("pageSize") int pageSize, @Path("pageCount") int pageCount);


    @GET("福利/{pageSize}/{index}")
    Call<ReceiveData.FuliResponse> fulis(@Path("pageSize") int pageSize,@Path("index") int index);

}
