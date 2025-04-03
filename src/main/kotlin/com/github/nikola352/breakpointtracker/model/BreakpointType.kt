package com.github.nikola352.breakpointtracker.model

/**
 * Enum representing different types of breakpoints supported by the debugger.
 */
enum class BreakpointType {
    /** Line breakpoint */
    LINE,

    /** Function/method breakpoint */
    FUNCTION,

    /** Field watchpoint */
    FIELD,

    /** Exception breakpoint */
    EXCEPTION,

    /** Unrecognized breakpoint type */
    UNKNOWN,
}