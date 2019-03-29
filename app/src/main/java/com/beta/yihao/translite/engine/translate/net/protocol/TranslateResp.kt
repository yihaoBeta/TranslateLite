package com.beta.yihao.translite.engine.translate.net.protocol

/**
 * @Author yihao
 * @Date 2018/10/10-19:35
 * @Email yihaobeta@163.com
 */

data class TranslateResp(
    val error_code: Int = 0,
    val error_msg: String,
    val from: String,
    val to: String,
    val trans_result: ArrayList<TranslateResult>
)

data class TranslateResult(val src: String, val dst: String)
