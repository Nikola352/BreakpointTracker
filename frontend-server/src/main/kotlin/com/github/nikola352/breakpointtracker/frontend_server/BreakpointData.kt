package com.github.nikola352.breakpointtracker.frontend_server

import com.github.nikola352.breakpointtracker.shared.BreakpointDto

data class BreakpointData(
    val breakpointCount: Int,
    val breakpoints: List<BreakpointDto>
)