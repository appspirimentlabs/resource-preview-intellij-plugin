package io.github.appspirimentlabs.vectorpreview

import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.project.DumbAwareAction
import io.github.appspirimentlabs.vectorpreview.window.VectorViewerWindow


/**
 * @author Konstantin Bulenkov
 */
class ComposeDemoAction : DumbAwareAction() {
    override fun actionPerformed(e: AnActionEvent) {
        VectorViewerWindow(e.project).show()
    }
}
