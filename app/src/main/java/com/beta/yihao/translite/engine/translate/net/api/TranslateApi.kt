package com.beta.yihao.translite.engine.translate.net.api

import com.beta.yihao.translite.engine.translate.net.protocol.TranslateResp
import io.reactivex.Observable
import retrofit2.http.POST
import retrofit2.http.Query

/**
 * @Author yihao
 * @Date 2018/10/10-19:15
 * @Email yihaobeta@163.com
 */

interface TranslateApi {

    @POST("translate")
    fun translateCore(
        @Query("q") transText: String,
        @Query("from") from: String,
        @Query("to") to: String,
        @Query("appid") appid: Long,
        @Query("salt") salt: Long,
        @Query("sign") sign: String
    )
            : Observable<TranslateResp>
}