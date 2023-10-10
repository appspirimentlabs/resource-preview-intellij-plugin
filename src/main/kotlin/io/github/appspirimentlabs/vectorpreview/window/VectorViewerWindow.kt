package io.github.appspirimentlabs.vectorpreview.window

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Surface
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.awt.ComposePanel
import androidx.compose.ui.unit.dp
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.DialogWrapper
import io.github.appspirimentlabs.vectorpreview.theme.WidgetTheme
import io.github.appspirimentlabs.vectorpreview.vectors.ShowVectorResource
import io.github.appspirimentlabs.vectorpreview.vectors.getImageFiles
import java.io.File
import javax.swing.JComponent

class VectorViewerWindow(val project:Project?): DialogWrapper(project) {
    init {
        title = "Vector Viewer"
        init()
    }

    override fun createCenterPanel(): JComponent {
       return ComposePanel().apply {
            setBounds(0, 0, 800, 600)
            setContent {
                WidgetTheme(darkTheme = true) {
                    Surface(modifier = Modifier.fillMaxSize()) {
                        (project?.basePath)?.let { File(it) }?.let { projectFile ->
                            val listOfRes = getImageFiles(projectFile)
                            ShowVectorResource(
                                project.basePath,
                                listOfRes,
                                mutableStateOf(48.dp),
                            )
                        }
                    }
                }
            }
        }
    }
}