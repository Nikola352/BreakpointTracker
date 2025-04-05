package com.github.nikola352.breakpointtracker.messaging

import com.github.nikola352.breakpointtracker.ui.BreakpointTrackerPanel
import com.intellij.ide.ui.LafManager
import com.intellij.ide.ui.LafManagerListener
import com.intellij.openapi.project.Project
import com.intellij.openapi.project.ProjectManager
import com.intellij.openapi.wm.ToolWindowManager

/** Listens for theme changes and calls [com.github.nikola352.breakpointtracker.ui.BreakpointTrackerPanel] theme update. */
class ThemeChangeListener : LafManagerListener {
    override fun lookAndFeelChanged(lafManager: LafManager) {
        @Suppress("UnstableApiUsage")
        val isDark = lafManager.currentUIThemeLookAndFeel.isDark

        val projectManager = ProjectManager.getInstance()
        projectManager.openProjects.forEach { project ->
            updateToolWindowTheme(project, isDark)
        }
    }

    private fun updateToolWindowTheme(project: Project, isDark: Boolean) {
        val toolWindow = ToolWindowManager.getInstance(project).getToolWindow("Breakpoint Tracker")
        toolWindow?.contentManager?.contents?.forEach { content ->
            (content.component as? BreakpointTrackerPanel)?.updateTheme(isDark)
        }
    }
}