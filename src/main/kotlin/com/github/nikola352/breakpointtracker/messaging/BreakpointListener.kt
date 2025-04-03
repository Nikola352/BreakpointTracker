package com.github.nikola352.breakpointtracker.messaging

import com.github.nikola352.breakpointtracker.model.Breakpoint
import com.github.nikola352.breakpointtracker.service.BreakpointDataService
import com.intellij.openapi.project.Project
import com.intellij.xdebugger.breakpoints.XBreakpoint
import com.intellij.xdebugger.breakpoints.XBreakpointListener

/**
 * Listener for breakpoint events in the IDE.
 *
 * Calls [BreakpointDataService] to dynamically update tracked breakpoints.
 */
class BreakpointListener(private val project: Project) : XBreakpointListener<XBreakpoint<*>> {

    override fun breakpointAdded(breakpoint: XBreakpoint<*>) {
        val breakpointDataService = project.getService(BreakpointDataService::class.java)
        breakpointDataService.addBreakpoint(Breakpoint(breakpoint, System.currentTimeMillis()))
    }

    override fun breakpointRemoved(breakpoint: XBreakpoint<*>) {
        val breakpointDataService = project.getService(BreakpointDataService::class.java)
        breakpointDataService.removeBreakpoint(Breakpoint(breakpoint, 0).id)
    }

    override fun breakpointChanged(breakpoint: XBreakpoint<*>) {
        val breakpointDataService = project.getService(BreakpointDataService::class.java)
        breakpointDataService.updateBreakpoint(Breakpoint(breakpoint, System.currentTimeMillis()))
    }
}