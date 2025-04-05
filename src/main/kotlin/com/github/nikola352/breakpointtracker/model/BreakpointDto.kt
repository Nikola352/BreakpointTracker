package com.github.nikola352.breakpointtracker.model

import com.github.nikola352.breakpointtracker.shared.BreakpointDto
import com.github.nikola352.breakpointtracker.shared.BreakpointTypeDto

/**
 * Convert domain [Breakpoint] object to shared dto for transfer
 */
fun Breakpoint.toDto() = BreakpointDto(
    id = id,
    type = type.toDto(),
    descriptor = descriptor,
    filePath = filePath,
    lineNumber = lineNumber,
    enabled = enabled,
    timestamp = timestamp,
)

fun BreakpointType.toDto() = when (this) {
    BreakpointType.LINE -> BreakpointTypeDto.LINE
    BreakpointType.FUNCTION -> BreakpointTypeDto.FUNCTION
    BreakpointType.FIELD -> BreakpointTypeDto.FIELD
    BreakpointType.EXCEPTION -> BreakpointTypeDto.EXCEPTION
    BreakpointType.UNKNOWN -> BreakpointTypeDto.UNKNOWN
}