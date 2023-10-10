package io.github.appspirimentlabs.vectorpreview.window

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Surface
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.awt.ComposePanel
import androidx.compose.ui.unit.dp
import com.intellij.openapi.project.DumbAware
import com.intellij.openapi.project.Project
import com.intellij.openapi.wm.ToolWindow
import com.intellij.openapi.wm.ToolWindowFactory
import com.intellij.ui.content.ContentFactory
import io.github.appspirimentlabs.vectorpreview.theme.WidgetTheme
import io.github.appspirimentlabs.vectorpreview.vectors.ShowVectorResource
import io.github.appspirimentlabs.vectorpreview.vectors.getImageFiles
import java.io.File

class DrawableToolWindowFactory() : ToolWindowFactory, DumbAware {

    override fun createToolWindowContent(project: Project, toolWindow: ToolWindow) {
        ComposePanel().apply {
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

        }.let {
            val content = ContentFactory.SERVICE.getInstance().createContent(it, "", false)
            toolWindow.contentManager.addContent(content)
        }
    }
}