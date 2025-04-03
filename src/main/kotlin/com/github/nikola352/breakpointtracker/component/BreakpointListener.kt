package com.github.nikola352.breakpointtracker.component

import com.github.nikola352.breakpointtracker.model.Breakpoint
import com.github.nikola352.breakpointtracker.service.BreakpointDataService
import com.intellij.openapi.project.Project
import com.intellij.xdebugger.breakpoints.XBreakpoint
import com.intellij.xdebugger.breakpoints.XBreakpointListener

/**
 * Service for managing in-memory storage of active breakpoints.
 *
 * This service is registered at the project level and automatically disposed when the project closes.
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