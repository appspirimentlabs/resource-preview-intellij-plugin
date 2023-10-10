package io.github.appspirimentlabs.vectorpreview.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Search
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.produceState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.loadXmlImageVector
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.xml.sax.InputSource
import java.io.IOException
import java.net.URL


@Composable
fun RoundButton(imageVector: ImageVector, onClick: () -> Unit) {
    Icon(
        imageVector = imageVector,
        contentDescription = null,
        tint = Color.White,
        modifier = Modifier
            .clickable {
                onClick.invoke()
            }
            .padding(end = 8.dp)
            .size(32.dp)
            .background(Color(0xFF5E10B1), RoundedCornerShape(16.dp))
            .padding(8.dp)

    )
}

@Composable
fun LetterButton(letter: Char, onClick: () -> Unit) {
    Text(
        text = letter.uppercase(),
        fontWeight = FontWeight.Black,
        textAlign = TextAlign.Center,
        fontSize = 16.sp,
        color = Color.White,
        modifier = Modifier
            .clickable {
                onClick.invoke()
            }
            .padding(end = 8.dp)
            .size(32.dp)
            .background(Color(0xFF5E10B1), RoundedCornerShape(16.dp))
            .padding(8.dp)

    )
}

@Composable
fun IconButton(
    imageVector: ImageVector,
    tint: Color = MaterialTheme.colors.onBackground,
    size: Dp = 48.dp,
    onClick: () -> Unit
) {
    Icon(
        imageVector = imageVector,
        contentDescription = null,
        tint = tint,
        modifier = Modifier
            .padding(end = 8.dp)
            .size(size)
            .padding(8.dp)
            .clickable {
                onClick.invoke()
            }
    )
}

@Composable
fun ShowSearchBar(
    searchQry: MutableState<String>?,
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(horizontal = 8.dp),
    ) {

       Box(contentAlignment = Alignment.CenterEnd) {
           Row(verticalAlignment = Alignment.CenterVertically) {
               Box(contentAlignment = Alignment.CenterStart, modifier = Modifier.padding(start = 4.dp)) {
                   Icon(
                       imageVector = Icons.Default.Search,
                       contentDescription = null,
                       modifier = Modifier.padding(start = 8.dp).size(16.dp).padding(top = 1.dp),
                       tint = Color.Gray
                   )
                   BasicTextField(
                       value = searchQry?.value ?: "",
                       onValueChange = { qry: String ->
                           searchQry?.value = qry
                       },
                       textStyle = LocalTextStyle.current.merge(TextStyle(fontSize = 12.sp, color = LocalContentColor.current)),
                       cursorBrush = SolidColor(MaterialTheme.colors.onBackground),
                       singleLine = true,
                       modifier = Modifier.widthIn(208.dp, 208.dp).padding(start = 4.dp, end = 4.dp)
                           .padding(vertical = 6.dp)
                           .border(1.dp, Color.Gray, RoundedCornerShape(4.dp)).padding(4.dp).padding(horizontal = 24.dp),
                   )
               }
           }
           IconButton(onClick = { searchQry?.value = "" }) {
               Icon(
                   imageVector = Icons.Default.Clear,
                   contentDescription = null,
                   modifier = Modifier.padding(end=4.dp).size(16.dp)
               )
           }
       }
    }
}

