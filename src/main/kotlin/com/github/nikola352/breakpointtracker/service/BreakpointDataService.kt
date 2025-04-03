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
class BreakpointDataService : Disposable {
    // Thread safe map to store active breakpoints (by id)
    private val _breakpoints = ConcurrentHashMap<String, Breakpoint>()

    // List of listeners to notify when data changes
    private val listeners = ArrayList<(List<Breakpoint>) -> Unit>()

    /** Total count of all currently tracked breakpoints */
    val breakpointCount get() = _breakpoints.size

    /** All currently tracked breakpoints */
    val breakpoints: List<Breakpoint> get() = ArrayList(_breakpoints.values)

    /** Add a breakpoint to the tracking system */
    fun addBreakpoint(breakpoint: Breakpoint) {
        _breakpoints[breakpoint.id] = breakpoint
        notifyListeners()
    }

    /** Update breakpoint information */
    fun updateBreakpoint(breakpoint: Breakpoint) {
        _breakpoints[breakpoint.id] = breakpoint
        notifyListeners()
    }

    /** Remove a breakpoint from the tracking system */
    fun removeBreakpoint(id: String) {
        _breakpoints.remove(id)
        notifyListeners()
    }

    fun addListener(listener: (List<Breakpoint>) -> Unit) {
        listeners.add(listener)
        listener(breakpoints)
    }

    fun removeListener(listener: (List<Breakpoint>) -> Unit) {
        listeners.remove(listener)
    }

    private fun notifyListeners() {
        val breakpoints = this.breakpoints
        listeners.forEach { it(breakpoints) }
    }

    override fun dispose() {
        _breakpoints.clear()
    }
}