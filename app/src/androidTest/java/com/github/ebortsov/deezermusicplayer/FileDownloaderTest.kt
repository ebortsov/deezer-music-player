package com.github.ebortsov.deezermusicplayer

import android.os.Environment
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.ebortsov.deezermusicplayer.download.general.FileDownloader
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.runBlocking
import org.junit.After

import org.junit.Test
import org.junit.runner.RunWith

import org.junit.Assert.*
import org.junit.Before


@RunWith(AndroidJUnit4::class)
class FileDownloaderTest {
    private val destinationFolder =
        Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
            .resolve("file-downloader-test")
    private val fileUrl = "https://cdn-images.dzcdn.net/images/artist/38ad60ed94bef5e6fc05bd70f3299e4e/1000x1000-000000-80-0-0.jpg"
    private val expectedFileSize = 248441L // Hardcoded file size

    @Before
    fun setup() {
        destinationFolder.mkdirs()
        FileDownloader.initialize()
    }

    @Test
    fun file_is_correctly_downloaded() {
        val fileDownloader = FileDownloader.getInstance()
        val destination = destinationFolder.resolve("test-file")
        val result = runBlocking {
            fileDownloader.download(destination, fileUrl)
        }
        assertTrue(result)
        assertTrue(destination.exists())
        assertEquals(expectedFileSize, destination.length())
    }

    @Test
    fun concurrent_downloads_produce_valid_file() = runBlocking {
        val downloader = FileDownloader.getInstance()
        val destination = destinationFolder.resolve("concurrent-download-test")

        val results = List(5) { // Test with more concurrent downloads
            async { downloader.download(destination, fileUrl) }
        }.awaitAll()

        assertTrue(results.all { it }) // All downloads succeeded
        assertTrue(destination.exists())
        assertEquals(expectedFileSize, destination.length())
    }

    @Test
    fun download_fails_with_invalid_url() = runBlocking {
        val downloader = FileDownloader.getInstance()
        val destination = destinationFolder.resolve("invalid-test")

        assertFalse(downloader.download(destination, "https://invalid.url/nonexistent"))
        assertFalse(destination.exists())
    }

    @After
    fun cleanup() {
        destinationFolder.deleteRecursively()
    }
}