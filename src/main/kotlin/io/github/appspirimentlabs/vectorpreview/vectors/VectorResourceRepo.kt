package io.github.appspirimentlabs.vectorpreview.vectors

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.loadXmlImageVector
import androidx.compose.ui.unit.Density
import com.android.utils.childrenIterator
import com.android.utils.forEach
import io.github.appspirimentlabs.vectorpreview.screens.entity.ImageResource
import io.github.appspirimentlabs.vectorpreview.screens.entity.PngResource
import io.github.appspirimentlabs.vectorpreview.screens.entity.VectorResource
import org.jetbrains.kotlin.idea.gradleTooling.get
import org.w3c.dom.Node
import org.xml.sax.InputSource
import java.io.File
import javax.xml.parsers.DocumentBuilderFactory

val attrMap = hashMapOf(
    "@android:color/transparent" to "#00000000",
    "@android:color/black" to "#ff000000",
    "@android:color/white" to "#ffffffff",
    "@android:color/holo_red_light" to "#ffff4444",
    "@android:color/holo_red_dark" to "#ffcc0000",
    "@android:color/holo_green_light" to "#ff99cc00",
    "@android:color/holo_green_dark" to "#ff669900",
    "@android:color/holo_blue_light" to "#ff33b5e5",
    "@android:color/holo_blue_dark" to "#ff0099cc",
    "@android:color/primary_text_light" to "#ff000000",
    "@android:color/primary_text_dark" to "#ffffffff",
    "@android:color/secondary_text_light" to "#8a000000",
    "@android:color/secondary_text_dark" to "#8affffff",
    "@android:color/background_light" to "#fff3f3f3",
    "@android:color/background_dark" to "#ff000000",
    "?android:attr/textColorPrimary" to "#000000",
    "?android:attr/textColorSecondary" to "#000000",
)
val fileSeperator = File.separator
val excludedVectors = emptyList<String>()
var replacerMap = hashMapOf<String, HashMap<String, String>>()
fun File.getBrand(): String {
    return takeIf { path.contains("${fileSeperator}src${fileSeperator}") }
        ?.path?.split("${fileSeperator}src${fileSeperator}")
        ?.get(1)?.split(fileSeperator)
        ?.first()?.trim() ?: "main"
}

fun File.getTheme(): String {
    return takeIf { path.contains("${fileSeperator}drawable") }
        ?.path?.split("${fileSeperator}drawable")
        ?.get(1)?.split(fileSeperator)
        ?.first()?.replace("-", "")
        ?.trim()?.ifBlank { "main" } ?: "main"
}

@Composable
fun getImageFiles(project: File): List<ImageResource> {
    replacerMap = getValuesList(project)
    val listOfVectors = arrayListOf<ImageResource>()
    val xmlfiles = project.walk().filter { it.endsWith("xml") }.toList()
    project.walk().forEach { file ->
        processProjectFile(file)?.let {
            listOfVectors.add(it)
        }
    }
    return listOfVectors.sortedBy { it.name }
}

@Composable
fun processProjectFile(file: File): ImageResource? {
    return if (file.path.contains("${fileSeperator}drawable") &&
        !file.path.contains("${fileSeperator}build${fileSeperator}") &&
        (file.name.endsWith(".xml") || file.name.endsWith(".png") || file.name.endsWith(".jpg") || file.name.endsWith(".jpeg")) &&
        file.path !in excludedVectors
    ) {
        val module = file.path.split("${fileSeperator}src${fileSeperator}")[0].split(fileSeperator).last().trim()
        val brand = file.getBrand()
        val theme = file.getTheme()
        remember(file) {
            var fileContent = file.readText()
            var tint: String? = null
            if (file.path.endsWith(".xml") && fileContent.contains("<vector ")) {
                if (!fileContent.contains("width=")) {
                    null
                }
                else {
                    try {
                        tint =  fileContent.takeIf { it.contains("android:tint") }?.split("android:tint")?.get(1)?.split("\"")?.get(1)?.trim()

//                    while(fileContent.contains("@color")){
//                        val colorname = fileContent.split("@color/")[1]
//                        xmlfiles.filter { it.name == "$colorname.xml" }.firstOrNull()?.let{
//                            val colorFile = it.readText()
//                            if(colorFile.contains("<selector") && colorFile.contains("android:color")){
//
//                            }
//                        }
//                    }

                        VectorResource(
                            name = file.name,
                            path = file.path,
                            module = module,
                            brand = brand,
                            theme = theme,
                            tint = tint,
                            vector = null
                        )
                    } catch (e: Exception) {
                        println(file.path)
                        e.printStackTrace()
                       null
                    }

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

fun processVectorFile(vectorFile: VectorResource, density: Density): ImageVector?{
    return try {
        var fileContent = File(vectorFile.path).readText()
        replacerMap[vectorFile.run{"$brand-$theme"}]?.entries?.forEach { entry ->
            fileContent = fileContent.replace(entry.key, entry.value)
        }
        replacerMap["${vectorFile.brand}-main"]?.entries?.forEach { entry ->
            fileContent = fileContent.replace(entry.key, entry.value)
        }
        replacerMap["main-${vectorFile.theme}"]?.entries?.forEach { entry ->
            fileContent = fileContent.replace(entry.key, entry.value)
        }
        replacerMap["main-main"]?.entries?.forEach { entry ->
            fileContent = fileContent.replace(entry.key, entry.value)
        }
        attrMap.entries.forEach { entry ->
            fileContent = fileContent.replace(entry.key, entry.value)
        }
        loadXmlImageVector(InputSource(fileContent.byteInputStream()), density)
    } catch (e: Exception) {
        null
    }
}
fun getValuesList(project: File): HashMap<String, HashMap<String, String>> {
    val replaceMap = HashMap<String, HashMap<String, String>>()
    project.walk().filter {
        !it.path.contains("${fileSeperator}.")
                && !it.path.contains("${fileSeperator}build${fileSeperator}")
                && (it.path.contains("${fileSeperator}values") || it.path.contains("${fileSeperator}color"))
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
                        replaceMap[brandtheme] = (replaceMap[brandtheme] ?: hashMapOf()).apply { put(name, value) }
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
                        replaceMap[brandtheme] = (replaceMap[brandtheme] ?: hashMapOf()).apply { put(name, value) }
                    }
                }
            }
        }

        xmldoc.getElementsByTagName("selector").also{
            println("${file.path} --> ${it.length}")
        }.forEach { node ->

            try {
                val colorNodes = arrayListOf<Node>()
                node.childrenIterator().forEach {item->
                    val text = item.attributes?.getNamedItem("android:color")?.textContent?:""
                    if(text.isNotBlank()) colorNodes.add(item)
                }
                val color = colorNodes.first{
                    it.attributes?.getNamedItem("android:state_enabled") == null || it.attributes?.getNamedItem("android:state_enabled")?.textContent == "true"
                }.attributes?.getNamedItem("android:color")?.textContent
                (replaceMap[brandtheme] ?: hashMapOf()).apply {put(file.name.replace(".xml", "").let{"@color/$it"}, color?:"#ffffff") }
            } catch (ignored: Exception) { }
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
                            replaceMap[brandtheme] = (replaceMap[brandtheme] ?: hashMapOf()).apply { put(name, value) }
                        }
                    }
                }
            }
        }
    }

    replaceMap.entries.map { brandmap ->
        brandmap.value.entries.map {
            while (it.value.contains("/") && (it.value.contains("@") || it.value.contains("?"))){
                it.setValue(brandmap.value[it.value] ?: replaceMap.entries.flatMap { fullmap ->
                    fullmap.value.entries
                }.associate { entry ->
                    entry.key to entry.value
                }.getOrDefault(it.value, "#00FF00"))
            }
        }
    }
    return replaceMap
}
