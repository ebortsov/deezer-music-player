package com.github.ebortsov.deezermusicplayer

import android.os.Environment
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.ebortsov.deezermusicplayer.download.FileDownloader
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
    private val fileDownloader = FileDownloader()

    @Before
    fun setup() {
        destinationFolder.mkdirs()
    }

    @Test
    fun renameTo_replaces_file() {
        val originalFile = destinationFolder.resolve("original-file")
        val anotherFile = destinationFolder.resolve("another-file")

        val originalText = "Hello World"
        val anotherText = "Another text"
        originalFile.writeText(originalText)
        anotherFile.writeText(anotherText)

        anotherFile.renameTo(originalFile)

        assertEquals(anotherText, originalFile.readText())
        assertFalse(anotherFile.exists())
    }

    @Test
    fun file_is_correctly_downloaded() {
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
        val destination = destinationFolder.resolve("concurrent-download-test")

        List(5) { // Test with more concurrent downloads
            async { fileDownloader.download(destination, fileUrl) }
        }.awaitAll()

        assertTrue(destination.exists())
        assertEquals(expectedFileSize, destination.length())
    }

    @Test
    fun download_fails_with_invalid_url() = runBlocking {
        val destination = destinationFolder.resolve("invalid-test")

        assertFalse(fileDownloader.download(destination, "https://invalid.url/nonexistent"))
        assertFalse(destination.exists())
    }

    @After
    fun cleanup() {
        destinationFolder.deleteRecursively()
    }
}