package com.beta.yihao.translite.utils

import android.content.Context
import android.content.res.AssetManager
import android.os.Environment
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream

/**
 * 文件操作工具类
 */
object FileUtil {

    // 创建一个临时目录，用于复制临时文件，如assets目录下的离线资源文件
    fun createTmpDir(context: Context, dirName: String): String {
        var tmpDir = Environment.getExternalStorageDirectory().toString() + "/" + dirName
        if (!makeDir(tmpDir)) {
            tmpDir = context.getExternalFilesDir(dirName)!!.absolutePath
            if (!makeDir(dirName)) {
                throw RuntimeException("create model resources dir failed :$tmpDir")
            }
        }
        return tmpDir
    }

    fun fileCanRead(filename: String): Boolean {
        val f = File(filename)
        return f.canRead()
    }

    private fun makeDir(dirPath: String): Boolean {
        val file = File(dirPath)
        return if (!file.exists()) {
            file.mkdirs()
        } else {
            true
        }
    }

    @Throws(IOException::class)
    fun copyFromAssets(assets: AssetManager, source: String, dest: String, isCover: Boolean) {
        val file = File(dest)
        if (isCover || !isCover && !file.exists()) {
            val ins: InputStream = assets.open(source)
            val fos = FileOutputStream(dest)

            val buffer = ByteArray(1024)

            ins.use { inputStream ->
                fos.use { outputStream ->
                    while (inputStream.read(buffer) != -1)
                        outputStream.write(buffer)
                }
            }
        }
    }

    fun readFromAssets(context: Context, fileName: String): String {

        var inputStream = context.assets.open(fileName, AssetManager.ACCESS_STREAMING)

        inputStream.buffered().reader().use {
            return it.readText()
        }
    }
}
