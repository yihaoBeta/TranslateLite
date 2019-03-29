package com.beta.yihao.translite.utils

/**
 * @Author yihao
 * @Description 常量类
 * @Date 2019/1/13-20:53
 * @Email yihaobeta@163.com
 */

const val DATABASE_NAME = "translate_lite_db"
const val ASSET_JSON_FILE_LANGUAGE = "language.json"


const val BASE_URL = "http://api.fanyi.baidu.com/api/trans/vip/"
const val APPID = 1234567L//此处为虚拟示意，正常使用的话请替换为自己申请的appid//20181007000*******
const val BAIDU_TTS_APPID = "14394342"//此处为虚拟示意，正常使用的话请替换为自己申请的
const val BAIDU_TTS_KEY = "LtcVOH88iiZx"//此处为虚拟示意，正常使用的话请替换为自己申请的
const val BAIDU_TTS_SECRET_KEY = "srOQtOK"//此处为虚拟示意，正常使用的话请替换为自己申请的
const val KEY = "aw3ByOgRI"//此处为虚拟示意，正常使用的话请替换为自己申请的


const val RECOGNIZE_STATE_INIT = 1L shl 0
const val RECOGNIZE_STATE_START = 1L shl 1
const val RECOGNIZE_STATE_PAUSE = 1L shl 2
val SUPPORT_TTS_LIST = arrayListOf("auto", "zh", "en", "cht", "yue", "wyw")