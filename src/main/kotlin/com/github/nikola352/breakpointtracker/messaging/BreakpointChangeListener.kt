package com.github.nikola352.breakpointtracker.messaging

import com.github.nikola352.breakpointtracker.model.Breakpoint
import com.github.nikola352.breakpointtracker.ui.BreakpointTrackerPanel
import com.intellij.openapi.project.Project
import com.intellij.openapi.wm.ToolWindowManager

/**
 * Listener for changes of currently tracked breakpoints state.
 *
 * Acts as a consumer of change events and delegates UI updates to the responsible module.
 */
class BreakpointChangeListener(private val project: Project) : BreakpointChangeNotifier {
    override fun breakpointsChanged(totalCount: Int, breakpoints: List<Breakpoint>) {
        val toolWindow = ToolWindowManager.getInstance(project).getToolWindow("Breakpoint Tracker")
        toolWindow?.contentManager?.contents?.forEach { content ->
            (content.component as? BreakpointTrackerPanel)?.updateContent(totalCount, breakpoints)
        }
    }
}