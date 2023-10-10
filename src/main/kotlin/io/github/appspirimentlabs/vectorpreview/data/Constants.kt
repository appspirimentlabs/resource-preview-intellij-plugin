package io.github.appspirimentlabs.vectorpreview.data

import java.io.File
import java.util.prefs.Preferences

object Constants {
    val prefs = Preferences.userNodeForPackage(this::class.java)
    const val PROJECT_PATH_KEY = "AssetViewerProjectRoot"
    const val REPLACER_MAP_KEY = "assetViewerReplacerMap"
    const val COLOR_MAP_KEY = "assetViewerColorMap"
    const val THEME_MAP_KEY = "assetViewerThemedColorMap"
    const val DIMEN_MAP_KEY = "assetViewerDimenMap"
    var PROJECT_PATH = prefs.get(PROJECT_PATH_KEY, null)?.let { File(it) }

}