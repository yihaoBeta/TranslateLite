package com.beta.yihao.translite.utils

/**
 * @Author yihao
 * @Date 2018/12/10-20:25
 * @Email yihaobeta@163.com
 */

object StateController {
    fun hasState(states: Long, value: Long) = (states and value) != 0L

    fun addState(states: Long, value: Long): Long {
        if (hasState(states, value)) return states
        return states or value
    }

    fun delState(states: Long, value: Long): Long {
        if (!hasState(states, value)) return states
        return states xor value
    }

}