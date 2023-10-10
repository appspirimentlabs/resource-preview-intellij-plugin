package io.github.appspirimentlabs.vectorpreview.screens.entity

import androidx.compose.ui.graphics.vector.ImageVector

interface ImageResource{
    val name: String
    val module: String
    val path: String
    val brand: String
    val theme: String
    val tint: String?
    fun getToolTipTest(projectpath: String?): String {
        return path.replace("${projectpath?:""}/", "").replace("/", " » ").trim().let{
            "Module:〈 $module 〉\nBrand:〈 $brand 〉\nTheme:〈 $theme 〉\nType:〈 ${path.split(".").last().let{ extn ->
                if(this is PngResource) extn.uppercase() else "Vector"
            }} 〉\n\n$it"
        }
    }
}

data class VectorResource(
    override val name: String,
    override val module: String,
    override val path: String,
    override val brand: String,
    override val theme: String,
    override val tint: String?,
    val vector: ImageVector
): ImageResource


data class PngResource(
    override val name: String,
    override val module: String,
    override val path: String,
    override val brand: String,
    override val theme: String,
    override val tint: String?,
): ImageResource
