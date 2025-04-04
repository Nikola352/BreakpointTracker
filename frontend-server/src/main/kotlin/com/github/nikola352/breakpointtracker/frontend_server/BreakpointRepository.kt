package com.github.nikola352.breakpointtracker.frontend_server

import com.github.nikola352.breakpointtracker.shared.BreakpointDto

/**
 * In-memory repository holding the breakpoint info.
 */
class BreakpointRepository {
    private var breakpointCount: Int = 0
    private var breakpoints: List<BreakpointDto> = emptyList()

    val breakpointData get() = BreakpointData(breakpointCount, breakpoints)

    fun update(breakpointCount: Int, breakpoints: List<BreakpointDto>) {
        this.breakpointCount = breakpointCount
        this.breakpoints = breakpoints
    }
}