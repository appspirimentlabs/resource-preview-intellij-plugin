package io.github.appspirimentlabs.vectorpreview.vectors

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.loadXmlImageVector
import io.github.appspirimentlabs.vectorpreview.screens.entity.ImageResource
import io.github.appspirimentlabs.vectorpreview.screens.entity.PngResource
import io.github.appspirimentlabs.vectorpreview.screens.entity.VectorResource
import org.xml.sax.InputSource
import java.io.File
import javax.xml.parsers.DocumentBuilderFactory

val attrMap = hashMapOf<String, String>().apply {
    put("?android:attr/textColorSecondary", "#000000")
    put("@android:color/white", "#ffffff")
    put("@color/body_color_selector_on_surface", "#646068")
    put("@color/selector_icon", "#5E10B1")
}
val fileSeperator = File.separator
val excludedVectors = emptyList<String>()

fun File.getBrand(): String {
    return takeIf { path.contains("${fileSeperator}src${fileSeperator}") }
        ?.path?.split("${fileSeperator}src${fileSeperator}")
        ?.get(1)?.split("$fileSeperator")
        ?.first()?.trim() ?: "main"
}

fun File.getTheme(): String {
    return takeIf { path.contains("${fileSeperator}drawable") }
        ?.path?.split("${fileSeperator}drawable")
        ?.get(1)?.split("${fileSeperator}")
        ?.first()?.replace("-", "")
        ?.trim()?.ifBlank { "main" } ?: "main"
}

@Composable
fun getImageFiles(project: File): List<ImageResource> {
    val replacerMap = getValuesList(project)
    val listOfVectors = arrayListOf<ImageResource>()
    project.walk().forEach { file ->
        processProjectFile(file, replacerMap)?.let {
            listOfVectors.add(it)
        }
    }
    return listOfVectors.sortedBy { it.name }
}

@Composable
fun processProjectFile(file: File, replacerMap: HashMap<String, HashMap<String, String>>): ImageResource? {
    return if (file.path.contains("${fileSeperator}drawable") &&
        !file.path.contains("${fileSeperator}build${fileSeperator}") &&
        (file.name.endsWith(".xml") || file.name.endsWith(".png") || file.name.endsWith(".jpg") || file.name.endsWith(".jpeg")) &&
        file.path !in excludedVectors
    ) {
        val module = file.path.split("${fileSeperator}src${fileSeperator}")[0].split(fileSeperator).last().trim()
        val brand = file.getBrand()
        val theme = file.getTheme()
        val density = LocalDensity.current
        remember(file) {
            var fileContent = file.readText()
            var tint: String? = null
            if (file.path.endsWith(".xml") && fileContent.contains("<vector ")) {
                if (!fileContent.contains("width=")) {
                    null
                }
                else {
                    tint =  fileContent.takeIf { it.contains("android:tint") }?.split("android:tint")?.get(1)?.split("\"")?.get(1)?.trim()
                    replacerMap[brand]?.entries?.forEach { entry ->
                        fileContent = fileContent.replace(entry.key, entry.value)
                    }
                    attrMap.entries.forEach { entry ->
                        fileContent = fileContent.replace(entry.key, entry.value)
                    }
                    VectorResource(
                        name = file.name,
                        path = file.path,
                        module = module,
                        brand = brand,
                        theme = theme,
                        tint = tint,
                        vector = loadXmlImageVector(InputSource(fileContent.byteInputStream()), density)
                    )

                }
            } else if(file.path.endsWith(".png") || file.path.endsWith(".jpg") || file.path.endsWith(".jpeg")) {
                PngResource(
                    name = file.name,
                    path = file.path,
                    module = module,
                    brand = brand,
                    theme = theme,
                    tint = tint
                )
            } else {
                null
            }
        }
    } else null
}
fun getValuesList(project: File): HashMap<String, HashMap<String, String>> {
    val replaceMap = HashMap<String, HashMap<String, String>>()
    project.walk().filter {
        !it.path.contains("${fileSeperator}.")
                && !it.path.contains("${fileSeperator}build${fileSeperator}")
                && it.path.contains("${fileSeperator}values/")
                && it.path.contains(
            ".xml"
        )
    }.forEach { file ->
        val brandtheme = "${file.getBrand()}-${file.getTheme()}"
        val factory = DocumentBuilderFactory.newInstance()
        val xmldoc = factory.newDocumentBuilder().parse(file)
        xmldoc.getElementsByTagName("color").let { node ->
            for (i in 0..node.length) {
                node.item(i).let { item ->
                    val name = item?.attributes?.getNamedItem("name")?.textContent?.let {
                        "@color${fileSeperator}$it"
                    }
                    val value = item?.textContent
                    if (name != null && value != null) {
                        replaceMap[brandtheme] = replaceMap[brandtheme] ?: hashMapOf<String, String>().apply { put(name, value) }
                    }
                }
            }
        }
        xmldoc.getElementsByTagName("dimen").let { node ->
            for (i in 0..node.length) {
                node.item(i).let { item ->
                    val name = item?.attributes?.getNamedItem("name")?.textContent?.let {
                        "@dimen/$it"
                    }
                    val value = item?.textContent
                    if (name != null && value != null) {
                        replaceMap[brandtheme] = replaceMap[brandtheme] ?: hashMapOf<String, String>().apply { put(name, value) }
                    }
                }
            }
        }

        xmldoc.getElementsByTagName("item").let { node ->
            for (i in 0..node.length) {
                node.item(i).let { item ->
                    if (item?.textContent?.contains("@color/")==true) {
                        val name = item.attributes?.getNamedItem("name")?.textContent?.let {
                            "?attr/$it"
                        }
                        val valuename = item.textContent?.replace("@color/", "")
                        val value = replaceMap[brandtheme]?.get("@color/$valuename")
                        if (name != null && value != null) {
                            replaceMap[brandtheme] = replaceMap[brandtheme] ?: hashMapOf<String, String>().apply { put(name, value) }
                        }
                    }
                }
            }
        }
    }
    return replaceMap
}
