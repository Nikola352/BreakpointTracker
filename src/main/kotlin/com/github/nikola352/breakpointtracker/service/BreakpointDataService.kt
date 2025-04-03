package com.github.nikola352.breakpointtracker.service

import com.github.nikola352.breakpointtracker.model.Breakpoint
import com.intellij.openapi.Disposable
import com.intellij.openapi.components.Service
import com.jetbrains.rd.util.ConcurrentHashMap

/**
 * Service for managing in-memory storage of active breakpoints.
 *
 * Implements a Listener pattern to allow other modules to listen for data changes.
 */
@Service(Service.Level.PROJECT)
class BreakpointDataService: Disposable {
    // Thread safe map to store active breakpoints (by id)
    private val _breakpoints = ConcurrentHashMap<String, Breakpoint>()

    /** Total count of all currently tracked breakpoints */
    val breakpointCount get() = _breakpoints.size

    /** All currently tracked breakpoints */
    val breakpoints: List<Breakpoint> get() = ArrayList(_breakpoints.values)

    /** Add a breakpoint to the tracking system */
    fun addBreakpoint(breakpoint: Breakpoint) {
        _breakpoints[breakpoint.id] = breakpoint
    }

    /** Update breakpoint information */
    fun updateBreakpoint(breakpoint: Breakpoint) {
        _breakpoints[breakpoint.id] = breakpoint
    }

    /** Remove a breakpoint from the tracking system */
    fun removeBreakpoint(id: String) {
        _breakpoints.remove(id)
    }

    override fun dispose() {
        _breakpoints.clear()
    }
}