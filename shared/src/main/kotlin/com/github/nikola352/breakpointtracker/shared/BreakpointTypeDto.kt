package com.github.nikola352.breakpointtracker.shared

/**
 * Enum representing different types of breakpoints supported by the debugger.
 * Used in [BreakpointDto] for transferring breakpoint data between modules.
 */
enum class BreakpointTypeDto {
    LINE,
    FUNCTION,
    FIELD,
    EXCEPTION,
    UNKNOWN,
}
