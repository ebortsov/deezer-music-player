package com.github.ebortsov.deezermusicplayer.download.general

import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.ResponseBody
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import java.util.concurrent.ConcurrentHashMap

private const val TAG = "FileDownloader"

/**
 * Class for asynchronous download of files
 */
class FileDownloader private constructor() {

    private val okHttpClient = OkHttpClient.Builder().build()

    /**
     * Download response body from [url] to [destination]
     * Returns true if download is successful, otherwise false
     */
    suspend fun download(destination: File, url: String): Boolean {
        Log.i(TAG, "download begin of $url to $destination")

        val request = Request.Builder()
            .url(url)
            .build()

        val call = okHttpClient.newCall(request)

        // `Call.execute` is blocking so perform the call on Dispatchers.IO
        return withContext(Dispatchers.IO) {
            val response = try {
                call.execute()
            } catch (ex: Exception) {
                Log.e(TAG, "download: $ex")
                return@withContext false
            }

            if (!response.isSuccessful) {
                Log.e(TAG, "download: response if not successful: ${response.message}")
                return@withContext false
            }

            fileMutexMap.getOrPut(destination) { Mutex() }.withLock {
                saveResponseBodyToFile(destination, response.body)
            }.also {
                fileMutexMap.remove(destination)
            }
        }
    }

    /**
     * Get the [ResponseBody] and save it to [destination]
     * The method is blocking
     */
    private fun saveResponseBodyToFile(destination: File, body: ResponseBody?): Boolean {
        if (body == null)
            return false

        var inputStream: InputStream? = null

        try {
            val destinationStream = FileOutputStream(destination, false)

            inputStream = body.byteStream()

            destinationStream.use {
                var readBytes: Int
                val buffer = ByteArray(size = 64 * 1024) // 64 kb buffer
                while (inputStream.read(buffer).also { readBytes = it } != -1) {
                    destinationStream.write(buffer, 0, readBytes)
                }
            }

            Log.i(TAG, "File is downloaded to $destination")
            return true
        } catch (ex: Exception) {
            Log.e(TAG, "saveResponseBodyToFile: $ex")
            return false
        } finally {
            inputStream?.close()
        }
    }

    companion object {
        private val fileMutexMap = ConcurrentHashMap<File, Mutex>()

        private var instance: FileDownloader? = null
        fun initialize() {
            instance = FileDownloader()
        }

        fun getInstance(): FileDownloader {
            return checkNotNull(instance) { "FileDownloader must be initialized" }
        }
    }
}