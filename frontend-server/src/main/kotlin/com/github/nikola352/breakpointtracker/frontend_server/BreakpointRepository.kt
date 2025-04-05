package com.github.nikola352.breakpointtracker.frontend_server

import com.github.nikola352.breakpointtracker.shared.BreakpointDto
import java.util.concurrent.locks.ReentrantReadWriteLock
import kotlin.concurrent.read
import kotlin.concurrent.write

/**
 * Thread safe, in-memory repository holding the breakpoint info.
 */
class BreakpointRepository {
    private val lock = ReentrantReadWriteLock()
    private var breakpointCount: Int = 0
    private var breakpoints: List<BreakpointDto> = emptyList()

    val breakpointData get() = lock.read { BreakpointData(breakpointCount, breakpoints) }

    fun update(breakpointCount: Int, breakpoints: List<BreakpointDto>) = lock.write {
        this.breakpointCount = breakpointCount
        this.breakpoints = breakpoints
    }
}
