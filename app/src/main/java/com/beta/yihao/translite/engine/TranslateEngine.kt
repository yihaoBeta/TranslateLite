package com.beta.yihao.translite.engine

import com.beta.yihao.translite.engine.translate.TranslateErrorException
import com.beta.yihao.translite.engine.translate.net.api.TranslateApi
import com.beta.yihao.translite.engine.translate.net.protocol.TranslateResp
import com.beta.yihao.translite.engine.translate.net.retrofit.RetrofitFactory
import com.beta.yihao.translite.utils.APPID
import com.beta.yihao.translite.utils.KEY
import com.beta.yihao.translite.utils.MD5
import com.beta.yihao.translite.utils.SystemUtils
import io.reactivex.Observable

/**
 * @Author yihao
 * @Date 2018/10/10-19:49
 * @Email yihaobeta@163.com
 */

object TranslateEngine {
    private fun translateFromApi(
        transText: String,
        from: String,
        to: String,
        appid: Long,
        salt: Long,
        sign: String
    ): Observable<TranslateResp> {
        return RetrofitFactory.INSTANCE.create(TranslateApi::class.java)
            .translateCore(transText, from, to, appid, salt, sign)
    }

    fun translateWithSourceAndTarget(transText: String, from: String, to: String): Observable<TranslateResp> {
        val salt = SystemUtils.getCurrentTime()
        val sign = MD5.md5(APPID.toString() + transText + salt + KEY)
            ?: throw TranslateErrorException("md5 error")
        return this.translateFromApi(transText, from, to, APPID, salt.toLong(), sign)
    }
}