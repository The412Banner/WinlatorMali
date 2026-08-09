package com.winlator.star.ui.screens

import android.content.Context
import android.net.Uri
import com.winlator.star.contents.ContentProfile
import com.winlator.star.contents.ContentsManager
import com.winlator.star.contents.Downloader
import java.io.File

// Extracted from Bannerlator's ContentDownloadSheet.kt (only the store download path needs it, not the
// whole sheet UI). Stable per-component temp name so an interrupted download resumes via HTTP Range.
internal fun downloadToCache(context: Context, profile: ContentProfile, onProgress: (Float) -> Unit): Uri? {
    val safe = ContentsManager.getEntryName(profile).replace(Regex("[^A-Za-z0-9._-]"), "_")
    val f = File(context.cacheDir, "content_dl_$safe.part")
    return if (Downloader.downloadFile(profile.remoteUrl, f, /* resume = */ true) { frac -> onProgress(frac) })
        Uri.fromFile(f) else null
}
