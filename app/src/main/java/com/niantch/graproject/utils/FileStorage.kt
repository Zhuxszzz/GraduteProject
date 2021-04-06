package com.niantch.graproject.utils

import android.content.ContentResolver
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import java.io.*
import java.net.URL

/**
 * author: niantchzhu
 * date: 2021
 */
object FileStorage {
    private var cropIconDir: File? = null
    private var iconDir: File? = null

    fun FileStorage() {
        if (Environment.MEDIA_MOUNTED == Environment.getExternalStorageState()) {
            val external = Environment.getExternalStorageDirectory()
            val rootDir = "/" + "restaurant"
            cropIconDir = File(external, "$rootDir/crop")
            if (!cropIconDir!!.exists()) {
                cropIconDir!!.mkdirs()
            }
            iconDir = File(external, "$rootDir/camera")
            if (!iconDir!!.exists()) {
                iconDir!!.mkdirs()
            }
        }
    }

    fun createCropFile(): File? {
        var fileName = ""
        if (cropIconDir != null) {
            fileName = CommonUtils.generateUUID().toString() + ".png"
        }
        return File(cropIconDir, fileName)
    }

    fun createIconFile(): File? {
        var fileName = ""
        if (iconDir != null) {
            fileName = CommonUtils.generateUUID().toString() + ".png"
        }
        return File(iconDir, fileName)
    }

    fun getRealFilePath(context: Context, uri: Uri?): String? {
        if (null == uri) return null
        val scheme = uri.scheme
        var data: String? = null
        if (scheme == null) data = uri.path else if (ContentResolver.SCHEME_FILE == scheme) {
            data = uri.path
        } else if (ContentResolver.SCHEME_CONTENT == scheme) {
            val cursor = context.contentResolver.query(uri, arrayOf(MediaStore.Images.ImageColumns.DATA), null, null, null)
            if (null != cursor) {
                if (cursor.moveToFirst()) {
                    val index = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA)
                    if (index > -1) {
                        data = cursor.getString(index)
                    }
                }
                cursor.close()
            }
        }
        return data
    }

    /**
     * 检测sdcard是否可用
     *
     * @return true为可用; false为不可用
     */
    fun isSDAvailable(): Boolean {
        val status = Environment.getExternalStorageState()
        return if (status != Environment.MEDIA_MOUNTED) false else true
    }

    fun saveDataToFile(fileToWrite: File, data: String?, append: Boolean) {
        var fOut: FileOutputStream? = null
        var myOutWriter: OutputStreamWriter? = null
        try {
            if (!fileToWrite.exists()) {
                if (!fileToWrite.parentFile.exists()) {
                    fileToWrite.parentFile.mkdirs()
                }
                fileToWrite.createNewFile()
            }
            fOut = FileOutputStream(fileToWrite, append)
            myOutWriter = OutputStreamWriter(fOut)
            myOutWriter.append(data)
            myOutWriter.flush()
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            if (fOut != null) {
                try {
                    fOut.close()
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }
            if (myOutWriter != null) {
                try {
                    myOutWriter.close()
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }
        }
    }

    /**
     * 删除一个文件
     *
     * @param filePath
     * 要删除的文件路径名
     * @return true if this file was deleted, false otherwise
     */
    fun deleteFile(filePath: String?): Boolean {
        try {
            val file = File(filePath)
            if (file.exists()) {
                return file.delete()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return false
    }

    // 复制文件
    fun copyFile(sourceFile: File?, targetFile: File?) {
        var inBuff: BufferedInputStream? = null
        var outBuff: BufferedOutputStream? = null
        try {
            // 新建文件输入流并对它进行缓冲
            inBuff = BufferedInputStream(FileInputStream(sourceFile))

            // 新建文件输出流并对它进行缓冲
            outBuff = BufferedOutputStream(FileOutputStream(targetFile))

            // 缓冲数组
            val b = ByteArray(1024 * 5)
            var len: Int
            while (inBuff.read(b).also { len = it } != -1) {
                outBuff.write(b, 0, len)
            }
            // 刷新此缓冲的输出流
            outBuff.flush()
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {

            // 关闭流
            if (inBuff != null) try {
                inBuff.close()
            } catch (e: IOException) {
                e.printStackTrace()
            }
            if (outBuff != null) try {
                outBuff.close()
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }

    /**
     * 删除 dir目录下的所有文件，包括删除文件夹
     *
     * @param dir
     */
    fun deleteDirectory(dir: File) {
        if (dir.isDirectory) {
            val listFiles = dir.listFiles()
            for (i in listFiles.indices) {
                deleteDirectory(listFiles[i])
            }
        }
        dir.delete()
    }

    /**
     * @param imageUrl
     * @return Bitmap
     * 根据网络图片的url获取布局背景的对象,此方法要在子线程中执行
     */
    fun loadImageFromNetwork(imageUrl: String?): Drawable? {
        var drawable: Drawable? = null
        try {
            // 可以在这里通过文件名来判断，是否本地有此图片
            drawable = Drawable.createFromStream(
                    URL(imageUrl).openStream(), "image.jpg")
        } catch (e: IOException) {
            Log.d("loadImageFromNetwork", e.message)
        }
        if (drawable == null) {
            Log.d("loadImageFromNetwork", "null drawable")
        }
        return drawable
    }

    //srcPath是本地手机中图片的路径
    fun compressImageFromFile(srcPath: String?, num: Float): Bitmap? {
        val newOpts = BitmapFactory.Options()
        newOpts.inJustDecodeBounds = true //只读边,不读内容
        var bitmap = BitmapFactory.decodeFile(srcPath, newOpts)
        newOpts.inJustDecodeBounds = false
        val w = newOpts.outWidth
        val h = newOpts.outHeight
        var be = 1
        if (w > h && w > num) {
            be = (newOpts.outWidth / num).toInt()
        } else if (w < h && h > num) {
            be = (newOpts.outHeight / num).toInt()
        }
        if (be <= 0) be = 1
        newOpts.inSampleSize = be //设置采样率
        newOpts.inPurgeable = true // 同时设置才会有效
        newOpts.inInputShareable = true //。当系统内存不够时候图片自动被回收
        bitmap = BitmapFactory.decodeFile(srcPath, newOpts)
        return bitmap
    }

}