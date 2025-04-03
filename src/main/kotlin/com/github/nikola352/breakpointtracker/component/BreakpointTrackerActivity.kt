package com.github.nikola352.breakpointtracker.component

import com.github.nikola352.breakpointtracker.model.Breakpoint
import com.github.nikola352.breakpointtracker.service.BreakpointDataService
import com.intellij.openapi.project.Project
import com.intellij.openapi.startup.ProjectActivity
import com.intellij.xdebugger.XDebuggerManager

/**
 * Startup activity that initializes breakpoint tracking when a project is opened.
 * Loads all existing breakpoints into the BreakpointDataService.
 */
class BreakpointTrackerActivity : ProjectActivity {
    override suspend fun execute(project: Project) {
        val breakpointDataService = project.getService(BreakpointDataService::class.java)
        val breakpointManager = XDebuggerManager.getInstance(project).breakpointManager
        val timestamp = System.currentTimeMillis()
        breakpointManager.allBreakpoints.forEach { breakpoint ->
            breakpointDataService.addBreakpoint(Breakpoint(breakpoint, timestamp))
        }
    }
}