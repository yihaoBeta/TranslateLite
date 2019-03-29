package com.beta.yihao.translite.engine.translate

/**
 * @Author yihao
 * @Description 错误状态码
 * @Date 2019/1/16-18:41
 * @Email yihaobeta@163.com
 */
enum class TransErrCode(value: Int) {
    SUCCESS(0) {
        override fun setMessage(string: String) {

        }

        override fun getMessage(): String = "成功"
    },
    TIMEOUT(52001) {
        override fun setMessage(string: String) {

        }

        override fun getMessage(): String = "请求超时"
    },
    SYSTEM_ERR(52002) {
        override fun setMessage(string: String) {

        }

        override fun getMessage(): String = "系统错误"
    },
    NO_PERMISSION(52003) {
        override fun setMessage(string: String) {

        }

        override fun getMessage(): String = "未授权用户"
    },
    PARAMETER_NULL(54000) {
        override fun setMessage(string: String) {

        }

        override fun getMessage(): String = "必填参数为空"
    },
    SIGNATURE_ERR(54001) {
        override fun setMessage(string: String) {

        }

        override fun getMessage(): String = "签名错误"
    },
    ACCESS_TOO_FREQUENTLY(54003) {
        override fun setMessage(string: String) {

        }

        override fun getMessage(): String = "访问频率受限"
    },
    LOW_MONEY(54004) {
        override fun setMessage(string: String) {

        }

        override fun getMessage(): String = "账户余额不足"
    },
    LONG_QUERY_TOO_MUCH(54005) {
        override fun setMessage(string: String) {

        }

        override fun getMessage(): String = "长query请求频繁"
    },
    IP_ERROR(58000) {
        override fun setMessage(string: String) {

        }

        override fun getMessage(): String = "客户端IP非法"
    },
    LANGUAGE_NOT_SUPPORT(58001) {
        override fun setMessage(string: String) {

        }

        override fun getMessage(): String = "译文语言方向不支持"
    },
    SERVICE_CLOSED(58002) {
        override fun setMessage(string: String) {

        }

        override fun getMessage(): String = "服务当前已关闭"
    },

    NETWORK_ERROR(1001) {
        override fun setMessage(string: String) {

        }

        override fun getMessage(): String = "网络访问受限,请检查网络连接"
    },
    OTHER_ERR(-1) {
        private var message: String = "未知错误"
        override fun setMessage(string: String) {
            message = string
        }

        override fun getMessage(): String = message
    };

    abstract fun getMessage(): String
    abstract fun setMessage(string: String)
}

@Throws
fun getEnumValue(value: Int): TransErrCode {
    TransErrCode.values().forEach {
        return if (it.ordinal == value) {
            it
        } else {
            TransErrCode.OTHER_ERR
        }
    }
    return TransErrCode.OTHER_ERR
}