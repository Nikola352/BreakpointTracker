package com.github.nikola352.breakpointtracker.ui

import com.intellij.openapi.project.Project
import com.intellij.openapi.wm.ToolWindow
import com.intellij.openapi.wm.ToolWindowFactory
import com.intellij.ui.content.ContentFactory

/**
 * Factory class that creates the Breakpoint Tracker tool window
 */
class BreakpointTrackerToolWindowFactory: ToolWindowFactory {
    override fun createToolWindowContent(project: Project, toolWindow: ToolWindow) {
        val breakpointWindow = BreakpointTrackerWindow(toolWindow)
        val content = ContentFactory.getInstance().createContent(breakpointWindow.content, null, false)
        toolWindow.contentManager.addContent(content)
    }
}