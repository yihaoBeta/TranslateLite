package com.beta.yihao.translite.utils

import com.beta.yihao.translite.AppContext


/**
 * @Author yihao
 * @Description 设置相关工具类，主要用来使用sp存储语言信息--后改为数据库存储，当前该类已废弃
 * @Date 2018/12/18-15:44
 * @Email yihaobeta@163.com
 */
object SettingsUtil {

    private const val SOURCE_LANGUAGE_ID = "source_language_id"
    private const val TARGET_LANGUAGE_ID = "target_language_id"
    private const val DEFAULT_LANGUAGE_ID = "auto"

    object LanguageSetting {
        var sourceLangId: String
            get() {
                return this.sourceLanguageId
            }
            set(value) {
                this.sourceLanguageId = value
            }

        var targetLang: String
            get() {
                return targetLanguageId
            }
            set(value) {
                this.targetLanguageId = value
            }


        private var sourceLanguageId: String by PreferenceProxy(AppContext, SOURCE_LANGUAGE_ID, DEFAULT_LANGUAGE_ID)
        private var targetLanguageId: String by PreferenceProxy(AppContext, TARGET_LANGUAGE_ID, DEFAULT_LANGUAGE_ID)
    }
}