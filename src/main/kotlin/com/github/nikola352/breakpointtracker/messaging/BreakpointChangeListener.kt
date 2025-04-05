package com.github.nikola352.breakpointtracker.messaging

import com.github.nikola352.breakpointtracker.model.Breakpoint
import com.github.nikola352.breakpointtracker.model.toDto
import com.github.nikola352.breakpointtracker.service.FrontendServerService
import com.intellij.openapi.project.Project

/**
 * Listener for changes of currently tracked breakpoints state.
 *
 * Acts as a consumer of change events and delegates UI updates to the responsible module.
 */
class BreakpointChangeListener(private val project: Project) : BreakpointChangeNotifier {
    override fun breakpointsChanged(totalCount: Int, breakpoints: List<Breakpoint>) {
        val serverService = project.getService(FrontendServerService::class.java)
        serverService.updateBreakpoints(totalCount, breakpoints.map { it.toDto() })
    }
}