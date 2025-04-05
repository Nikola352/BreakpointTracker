package com.github.nikola352.breakpointtracker.service

import com.github.nikola352.breakpointtracker.messaging.BreakpointChangeNotifier
import com.github.nikola352.breakpointtracker.model.Breakpoint
import com.intellij.openapi.Disposable
import com.intellij.openapi.components.Service
import com.intellij.openapi.project.Project
import com.jetbrains.rd.util.ConcurrentHashMap
import java.util.concurrent.ConcurrentSkipListSet

/**
 * Service for managing in-memory storage of active breakpoints.
 *
 * Dispatches updates to a message bus topic: [BreakpointChangeNotifier].
 */
@Service(Service.Level.PROJECT)
class BreakpointDataService(private val project: Project) : Disposable {
    // Thread-safe set to store breakpoints ordered by timestamp (newest first)
    private val _breakpoints = ConcurrentSkipListSet(
        compareByDescending<Breakpoint> { it.timestamp }
            .thenBy { it.id } // secondary sort by ID for consistency
    )

    // Additional map for fast lookup by ID
    private val _breakpointsById = ConcurrentHashMap<String, Breakpoint>()

    /** Total count of all currently tracked breakpoints */
    val breakpointCount get() = _breakpoints.size

    /** All currently tracked breakpoints, order from newest to oldest */
    val breakpoints: List<Breakpoint> get() = ArrayList(_breakpoints)

    /** Add a breakpoint to the tracking system */
    fun addBreakpoint(breakpoint: Breakpoint) {
        _breakpointsById[breakpoint.id]?.let { existing ->
            _breakpoints.remove(existing)
        }
        _breakpoints.add(breakpoint)
        _breakpointsById[breakpoint.id] = breakpoint
        notifyChange()
    }

    /** Update breakpoint information */
    fun updateBreakpoint(breakpoint: Breakpoint) {
        addBreakpoint(breakpoint)
    }

    /** Remove a breakpoint from the tracking system */
    fun removeBreakpoint(id: String) {
        _breakpointsById.remove(id)?.let { breakpoint ->
            _breakpoints.remove(breakpoint)
            notifyChange()
        }
    }

    private fun notifyChange() {
        val publisher = project.messageBus.syncPublisher(BreakpointChangeNotifier.TOPIC)
        publisher.breakpointsChanged(breakpointCount, breakpoints)
    }

    override fun dispose() {
        _breakpoints.clear()
        _breakpointsById.clear()
    }
}