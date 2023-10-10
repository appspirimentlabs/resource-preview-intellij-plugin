package io.github.appspirimentlabs.vectorpreview.vectors

import androidx.compose.foundation.*
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Face
import androidx.compose.material.icons.outlined.Clear
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.loadImageBitmap
import androidx.compose.ui.res.loadXmlImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.intellij.openapi.fileEditor.FileEditorManager
import com.intellij.openapi.project.ProjectManager
import com.intellij.openapi.vfs.VfsUtil
import io.github.appspirimentlabs.vectorpreview.screens.ShowSearchBar
import io.github.appspirimentlabs.vectorpreview.screens.entity.ImageResource
import io.github.appspirimentlabs.vectorpreview.screens.entity.PngResource
import io.github.appspirimentlabs.vectorpreview.screens.entity.VectorResource
import io.github.appspirimentlabs.vectorpreview.theme.toolbarBackground
import io.github.appspirimentlabs.vectorpreview.theme.tooltipBackground
import io.github.appspirimentlabs.vectorpreview.theme.tooltipText
import io.github.appspirimentlabs.vectorpreview.util.toColor
import org.xml.sax.InputSource
import java.io.File

@Composable
fun ShowZoomControls(iconSize: MutableState<Dp>) {
    val availableIconSizes = listOf(32.dp, 48.dp, 56.dp, 64.dp, 96.dp, 128.dp, 256.dp)
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(horizontal = 8.dp)
    ) {
        Icon(
            painter = painterResource("/images/ic_zoom_in.svg"),
            contentDescription = "",
            tint = MaterialTheme.colors.onBackground,
            modifier = Modifier.padding(start = 4.dp, end = 8.dp, top = 4.dp, bottom = 4.dp).size(20.dp).clickable {
                iconSize.value = availableIconSizes.run {
                    indexOf(iconSize.value).let {
                        get(
                            if (it < (size - 1)) {
                                it + 1
                            } else it
                        )
                    }
                }
            }.alpha(0.7f)
        )
        Icon(
            painter = painterResource("/images/ic_zoom_out.svg"),
            contentDescription = "",
            tint = MaterialTheme.colors.onBackground,
            modifier = Modifier.padding(start = 8.dp, end = 4.dp, top = 4.dp, bottom = 4.dp).size(20.dp).clickable {
                iconSize.value = availableIconSizes.run {
                    indexOf(iconSize.value).let {
                        get(
                            if (it > 0) {
                                it - 1
                            } else it
                        )
                    }
                }
            }.alpha(0.7f)
        )
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ShowVectorResource(
    projectpath: String?,
    listOfRes: List<ImageResource>,
    iconSize: MutableState<Dp>,
) {

    val moduleList = listOfRes.groupBy { it.module }.map {
        Pair(it.key, it.value.size)
    }
    val brandList = listOfRes.groupBy { it.brand }.map {
        Pair(it.key, it.value.size)
    }
    val themeList = listOfRes.groupBy { it.theme }.map {
        Pair(it.key, it.value.size)
    }

    val searchQry = remember { mutableStateOf<String>("") }
    val moduleFilter = remember { mutableStateOf<String?>(null) }
    val brandFilter = remember { mutableStateOf<String?>(null) }
    val themeFilter = remember { mutableStateOf<String?>(null) }
    val pngSupport = remember { mutableStateOf<Boolean>(false) }

    val initialColor = Pair(
        MaterialTheme.colors.background,
        MaterialTheme.colors.onBackground
    )
    val backgroundColor = remember { mutableStateOf(initialColor) }

    val filteredList = listOfRes.filter {
        (if (pngSupport.value) true else it is VectorResource)
                && (moduleFilter.value?.let { module -> it.module == module } ?: true)
                && (brandFilter.value?.let { brand -> it.brand == brand } ?: true)
                && (themeFilter.value?.let { theme -> it.theme == theme } ?: true)
                && (searchQry.value.takeIf { it.isNotBlank() }?.let { qry -> it.name.contains(qry) } ?: true)

    }

    Column(Modifier.fillMaxHeight()) {
        VectorToolBar(
            searchQry = searchQry,
            iconSize = iconSize,
            backgroundColor = backgroundColor
        )
        if (filteredList.isNotEmpty()) {
            ImagePreviewList(backgroundColor, projectpath, iconSize, filteredList)
        } else {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(Icons.Filled.Face, "", Modifier.padding(bottom = 16.dp).size(56.dp))
                    Text("No resources found!", color = MaterialTheme.colors.onBackground, fontSize = 14.sp)
                }
            }
        }

        Row(
            Modifier.height(40.dp).background(toolbarBackground).fillMaxWidth().padding(end = 8.dp),
            verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(modifier = Modifier.fillMaxHeight(), verticalAlignment = Alignment.CenterVertically) {
                FilterView("Module", moduleFilter, moduleList)
                ToolbarDivider()
                FilterView("Brand", brandFilter, brandList)
                ToolbarDivider()
                FilterView("Theme", themeFilter, themeList)
                ToolbarDivider()
                Checkbox(
                    checked = pngSupport.value,
                    onCheckedChange = { pngSupport.value = it },
                    modifier = Modifier.padding(start=8.dp).scale(0.7f),
                    colors = CheckboxDefaults.colors(
                        checkedColor = MaterialTheme.colors.onBackground,
                        uncheckedColor = MaterialTheme.colors.onBackground,
                    )
                )
                Text("Show PNG/JPG", fontSize = 11.sp, modifier = Modifier.offset(x=(-8).dp))
            }

            Text(
                text = "${filteredList.size}/${listOfRes.size}",
                modifier = Modifier,
                textAlign = TextAlign.Center,
                fontWeight = FontWeight.Normal,
                color = MaterialTheme.colors.onBackground,
                fontSize = 12.sp,
            )
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ColumnScope.ImagePreviewList(
    backgroundColor: MutableState<Pair<Color, Color>>,
    projectpath: String?,
    iconSize: MutableState<Dp>,
    filteredList: List<ImageResource>
) {
    backgroundColor.value.run {
        LazyVerticalGrid(
            columns = GridCells.Adaptive(184.dp),
            contentPadding = PaddingValues(1.dp),
            modifier = Modifier.fillMaxWidth().weight(1f).background(second.copy(alpha = 0.1f))
        ) {
            items(items = filteredList) {
                TooltipArea(
                    tooltip = {
                        Text(
                            text = it.getToolTipTest(projectpath),
                            modifier = Modifier.wrapContentWidth().background(tooltipBackground).padding(8.dp),
                            color = tooltipText,
                            textAlign = TextAlign.Start,
                            fontSize = 12.sp
                        )
                    },
                    delayMillis = 600, // in milliseconds
                    modifier = Modifier.fillMaxWidth(0.5f).padding(0.5.dp).offset(x = (-.25).dp)
                ) {
                    ImagePreview(resource = it, iconSize = iconSize, first, second)
                }
            }
        }
    }
}

@Composable
fun VectorToolBar(
    searchQry: MutableState<String>,
    iconSize: MutableState<Dp>,
    backgroundColor: MutableState<Pair<Color, Color>>,
) {
    Row(
        Modifier.fillMaxWidth().height(36.dp).background(toolbarBackground).padding(horizontal = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            ShowZoomControls(iconSize)
            ToolbarDivider()
            ShowSearchBar(searchQry = searchQry)
        }

        BackgroundColorSelector(backgroundColor)
    }


    Divider(Modifier.height(0.5.dp).fillMaxWidth())
}

@Composable
fun ToolbarDivider() {
    Divider(
        Modifier.padding(vertical = 8.dp).padding(horizontal = 0.dp).fillMaxHeight().width(0.5.dp)
            .shadow(1.dp, spotColor = MaterialTheme.colors.onBackground)
    )
}

@Composable
fun BackgroundColorSelector(backgroundColor: MutableState<Pair<Color, Color>>) {
    val defaultColorPair = Pair(
        MaterialTheme.colors.background,
        MaterialTheme.colors.onBackground
    )
    val lightColor = Color(242, 242, 242)
    val darkColor = Color(60, 63, 65)
    Row(verticalAlignment = Alignment.CenterVertically) {

        Text(
            text = "",
            modifier = Modifier.padding(horizontal = 4.dp).size(20.dp)
                .background(darkColor, CircleShape)
                .border(1.dp, lightColor, CircleShape)
                .clickable {
                    backgroundColor.value = Pair(darkColor, lightColor)
                })
        Text(
            text = "",
            modifier = Modifier.padding(horizontal = 4.dp).size(20.dp)
                .background(lightColor, CircleShape)
                .border(1.dp, darkColor, CircleShape)
                .clickable {
                    backgroundColor.value = Pair(lightColor, darkColor)
                })
        Icon(
            imageVector = Icons.Outlined.Clear,
            contentDescription = null,
            modifier = Modifier.padding(horizontal = 4.dp).size(18.dp)
                .border(1.dp, defaultColorPair.second, CircleShape)
                .clickable {
                    backgroundColor.value = defaultColorPair
                })
    }
}


@Composable
fun FilterView(title: String, filterState: MutableState<String?>, listOfModules: List<Pair<String, Int>>) {
    val filterExpanded = remember { mutableStateOf(false) }
    val onClick: (String?) -> Unit = {
        filterState.value = it
        filterExpanded.value = false
    }
    Row(
        Modifier.fillMaxHeight().wrapContentWidth().clickable { filterExpanded.value = true },
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "$title :",
            modifier = Modifier.padding(top = 0.dp)
                .padding(start = 16.dp, end = 4.dp),
            textAlign = TextAlign.Center,
            fontWeight = FontWeight.Normal,
            color = Color.Gray,
            fontSize = 11.sp,
        )
        Text(
            text = filterState.value ?: "All", color = MaterialTheme.colors.onBackground,
            modifier = Modifier.padding(top = 0.dp)
                .padding(start = 2.dp, end = 2.dp),
            textAlign = TextAlign.Center,
            fontWeight = FontWeight.Normal,
            fontSize = 12.sp,
        )
        DropdownMenu(
            expanded = filterExpanded.value,
            onDismissRequest = { filterExpanded.value = false }
        ) {
            DropdownMenuItem(
                content = {
                    Text(
                        text = "All ${title}s",
                        color = MaterialTheme.colors.onBackground,
                        fontSize = 13.sp
                    )
                },
                onClick = { onClick.invoke(null) }
            )
            listOfModules.forEach {
                DropdownMenuItem(
                    content = {
                        Text(
                            text = "▶ ${it.first}  [${it.second}]",
                            color = MaterialTheme.colors.onBackground,
                            fontSize = 13.sp
                        )
                    },
                    onClick = { onClick.invoke(it.first) },
                    modifier = Modifier.wrapContentWidth()
                )
            }
        }
        Icon(Icons.Default.ArrowDropDown, "", Modifier.padding(end = 8.dp))
    }
}

@Composable
fun ImagePreview(resource: ImageResource, iconSize: MutableState<Dp>, bgcolor: Color, textColor: Color) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight()
            .background(bgcolor)
            .pointerInput(Unit) {
                detectTapGestures(
                    onDoubleTap = {
                        ProjectManager.getInstance().openProjects.firstOrNull()?.let { project ->
                            VfsUtil.findFileByIoFile(File(resource.path), true)?.let {
                                FileEditorManager.getInstance(project).openFile(it, true)
                            }
                        }
                    }
                )
            }
    ) {
        resource.run {
            Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                theme.replace("main", "").let { theme ->
                    Text(
                        text = theme,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Normal,
                        textAlign = TextAlign.Center,
                        color = bgcolor,
                        modifier = Modifier
                            .background(
                                if (theme.isBlank()) Color.Transparent else textColor,
                                RoundedCornerShape(2.dp)
                            )
                            .padding(horizontal = 4.dp).padding(top = 2.dp, bottom = 4.dp)
                    )
                }
                brand.replace("main", "").uppercase().let { brand ->
                    Text(
                        text = brand,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Normal,
                        textAlign = TextAlign.Center,
                        color = bgcolor,
                        modifier = Modifier
                            .background(
                                if (brand.isBlank()) Color.Transparent else textColor,
                                RoundedCornerShape(2.dp)
                            )
                            .padding(horizontal = 4.dp).padding(top = 2.dp, bottom = 4.dp)
                    )
                }
            }

            when (this) {
                is VectorResource -> {
                    Image(
                        imageVector = this.vector,
                        contentScale = ContentScale.Fit,
                        contentDescription = null,
                        colorFilter = tint?.toColor()?.let { ColorFilter.tint(it) },
                        modifier = Modifier.padding(horizontal = 8.dp).padding(top = 0.dp).size(iconSize.value)
                    )
                }

                is PngResource -> {
                    val imageBitmap: ImageBitmap = File(path).let {
                        remember(it) {
                            loadImageBitmap(it.inputStream())
                        }
                    }
                    Image(
                        painter = BitmapPainter(imageBitmap),
                        contentScale = ContentScale.Fit,
                        contentDescription = null,
                        colorFilter = tint?.toColor()?.let { ColorFilter.tint(it) },
                        modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp).padding(top = 0.dp)
                            .size(iconSize.value)
                    )
                }
            }

            SelectionContainer() {
                Column(
                    modifier = Modifier.padding(horizontal = 8.dp).padding(top = 8.dp, bottom = 16.dp),
                    verticalArrangement = Arrangement.Center
                ) {

                    Text(
                        text = resource.name.split(".").dropLast(1).joinToString("."),
                        fontSize = 12.sp,
                        color = textColor,
                        fontWeight = FontWeight.Normal,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth().padding(top = 8.dp)
                    )

//                    Text(
//                        text = listOf(vector.module.replace("main", ""), vector.theme)
//                            .filter { item -> item.isNotBlank() }.joinToString(" | "),
//                        fontSize = 11.sp,
//                        fontWeight = FontWeight.Normal,
//                        textAlign = TextAlign.Center,
//                        color = MaterialTheme.colors.error,
//                        modifier = Modifier.fillMaxWidth().padding(top = 4.dp, bottom = 0.dp)
//                    )
                }
            }
        }
    }
}