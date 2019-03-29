package com.beta.yihao.translite.engine.tts

/**
 * @Author yihao
 * @Date 2018/10/12-13:43
 * @Email yihaobeta@163.com
 */

//普通女声（默认） 1 普通男声 2 特别男声 3 情感男声<度逍遥> 4 情感儿童声<度丫丫>
enum class VoiceMode(name: String) {
    VOICE_FEMALE("普通女声"),
    VOICE_MALE("普通男声"),
    VOICE_MALE_SP("特别男声"),
    VOICE_DUXY("情感男声<度逍遥>"),
    VOICE_DUYY("情感儿童声<度丫丫>"),
}