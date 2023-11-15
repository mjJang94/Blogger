package com.mj.blogger.core

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import timber.log.Timber
import java.io.File
import java.io.FileOutputStream
import java.net.HttpURLConnection
import java.net.URL

object UploadHelper {

    suspend fun compressImage(context: Context, uri: Uri, desiredWidth: Int, desiredHeight: Int): Uri =
        runCatching {
            withContext(Dispatchers.IO) {
                // Uri로부터 Bitmap을 가져오기
                val inputStream = context.contentResolver.openInputStream(uri)
                val options = BitmapFactory.Options()
                options.inJustDecodeBounds = true
                BitmapFactory.decodeStream(inputStream, null, options)
                inputStream?.close()

                val srcWidth = options.outWidth
                val srcHeight = options.outHeight

                // 이미지를 원하는 크기로 조정
                var inSampleSize = 1
                if (srcHeight > desiredHeight || srcWidth > desiredWidth) {
                    val heightRatio = (srcHeight.toFloat() / desiredHeight.toFloat()).toInt()
                    val widthRatio = (srcWidth.toFloat() / desiredWidth.toFloat()).toInt()
                    inSampleSize = if (heightRatio < widthRatio) heightRatio else widthRatio
                }

                val scaledOptions = BitmapFactory.Options()
                scaledOptions.inSampleSize = inSampleSize

                val scaledBitmap = BitmapFactory.decodeStream(context.contentResolver.openInputStream(uri), null, scaledOptions)

                // Bitmap을 파일로 압축하여 저장
                val compressedFile = File(context.cacheDir, "compressed_image_${System.currentTimeMillis()}.jpg")
                val outputStream = FileOutputStream(compressedFile)

                // 압축 품질 조절
                scaledBitmap?.compress(Bitmap.CompressFormat.JPEG, 80, outputStream)

                scaledBitmap?.recycle()
                outputStream.close()

                return@withContext Uri.fromFile(compressedFile)
            }
        }.getOrElse { exception ->
            throw exception
        }

    suspend fun Context.downloadAndConvertToInternalUri(imageUrl: String): Uri =
        runCatching {
            withContext(Dispatchers.IO) {
                val url = URL(imageUrl)
                val connection: HttpURLConnection = url.openConnection() as HttpURLConnection
                connection.doInput = true
                connection.connect()

                val input = connection.inputStream
                val fileName = "downloaded_image_${System.currentTimeMillis()}.jpg" // 내부 저장소에 저장될 파일명

                val file = File(filesDir, fileName)
                val fileOutputStream = FileOutputStream(file)
                val buffer = ByteArray(1024)
                var bytesRead: Int
                while (input.read(buffer).also { bytesRead = it } != -1) {
                    fileOutputStream.write(buffer, 0, bytesRead)
                }
                return@withContext Uri.fromFile(file)
            }
        }.getOrElse { exception ->
            throw exception
        }
}