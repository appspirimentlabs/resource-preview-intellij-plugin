package io.github.appspirimentlabs.vectorpreview.vectors

import androidx.compose.foundation.gestures.rememberScrollableState
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.unit.dp
import com.intellij.ui.components.CheckBox

@Composable
fun FilterOptionsWindow(
    moduleList: List<Pair<String, Int>>,
    brandList: List<Pair<String, Int>>,
    themeList: List<Pair<String, Int>>
) {
    Column(Modifier.fillMaxWidth().padding(16.dp).wrapContentHeight()) {
        Row(horizontalArrangement = Arrangement.SpaceAround, modifier = Modifier.fillMaxWidth()) {
            Column(Modifier.fillMaxWidth(.3f).fillMaxHeight().shadow(1.dp).padding(16.dp)) {
                moduleList.forEach {
                    Text("${it.first}[${it.second}")
                }
            }
            Column(Modifier.padding(horizontal = 16.dp).fillMaxHeight().fillMaxWidth(.3f).shadow(1.dp).padding(16.dp)) {
                brandList.forEach {
                    Text("${it.first}[${it.second}")
                }
            }
            Column(Modifier.fillMaxWidth(.3f).fillMaxHeight().shadow(1.dp).padding(16.dp)) {
                themeList.forEach {
                    Text("${it.first}[${it.second}")
                }
            }
        }
    }
}