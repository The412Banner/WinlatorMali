package com.winlator.star.util

import android.content.Context
import android.content.Intent
import android.net.Uri

/**
 * Store port: file/folder picking via the Android system picker (Storage Access Framework) instead of
 * Bannerlator's in-app File Manager (not part of WinlatorMali). Returns content:// Uris that
 * contentResolver.openInputStream(uri) reads directly, so import call sites are unchanged.
 * (WinlatorMali dev: if a caller needs a real filesystem path from a directory pick, resolve the
 * ACTION_OPEN_DOCUMENT_TREE Uri via DocumentFile — the common file-import path below already works.)
 */
object InAppFilePicker {
    val WCP = arrayOf("wcp", "tzst", "xz", "zst", "zip")
    val ICP = arrayOf("icp", "icpx")
    val IMAGES = arrayOf("png", "jpg", "jpeg", "webp", "bmp", "gif")
    val DLL = arrayOf("dll")
    val DRIVER = arrayOf("zip", "adpkg")
    val WRAPPER = arrayOf("tzst")
    val SAVE = arrayOf("zip", "tzst", "zst", "xz", "tar")
    val SF2 = arrayOf("sf2")
    val SHORTCUT = arrayOf("exe", "desktop", "lnk")
    val JSON = arrayOf("json")

    fun buildIntent(
        context: Context,
        extensions: Array<String> = emptyArray(),
        title: String? = null,
        initialDir: String? = null,
    ): Intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
        addCategory(Intent.CATEGORY_OPENABLE)
        type = "*/*"
        title?.let { putExtra(Intent.EXTRA_TITLE, it) }
    }

    fun buildDirIntent(
        context: Context,
        title: String? = null,
        initialDir: String? = null,
    ): Intent = Intent(Intent.ACTION_OPEN_DOCUMENT_TREE)

    /** The picked content:// Uri as a string, or null. */
    fun pickedPath(data: Intent?): String? = data?.data?.toString()

    fun asUri(path: String): Uri = Uri.parse(path)

    /** OK-result data -> content:// Uri, or null. */
    fun pickedUri(data: Intent?): Uri? = data?.data
}
