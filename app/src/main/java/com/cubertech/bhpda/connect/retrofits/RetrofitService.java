package com.cubertech.bhpda.connect.retrofits;

import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;
import retrofit2.http.Path;
import rx.Observable;

public interface RetrofitService {

    @FormUrlEncoded
    @POST("api/user/{f}")
    Observable<String> Mytest(@Path("f") String f, @Field("") String strJson);

    @FormUrlEncoded
    @POST("api/JQData/{f}")
    Observable<String> JQ(@Path("f") String f, @Field("") String strJson);
}
