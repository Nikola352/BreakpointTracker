package com.github.nikola352.breakpointtracker.model

import com.intellij.xdebugger.breakpoints.XBreakpoint

/**
 * Data class representing a breakpoint in the tracking system.
 *
 * @property id Unique identifier for the breakpoint
 * @property type The type of breakpoint (line, function, etc.)
 * @property filePath Path to the file containing the breakpoint
 * @property className Name of the class containing the breakpoint
 * @property lineNumber Line number where breakpoint is set (1-based)
 * @property enabled Whether the breakpoint is currently active
 * @property timestamp When the breakpoint was last modified
 */
data class Breakpoint(
    val id: String,
    val type: BreakpointType,
    val filePath: String?,
    val className: String?,
    val lineNumber: Int?,
    val enabled: Boolean,
    val timestamp: Long,
) {
    /**
     * Constructs a Breakpoint from an IntelliJ XBreakpoint.
     *
     * @param breakpoint The IntelliJ platform breakpoint
     * @param timestamp When the breakpoint was created/modified
     */
    constructor(breakpoint: XBreakpoint<*>, timestamp: Long) : this(
        id = "${breakpoint.type.id}-${System.identityHashCode(breakpoint)}",
        type = breakpoint.type.id.determineBreakpointType(),
        filePath = breakpoint.sourcePosition?.file?.path,
        className = breakpoint.sourcePosition?.file?.javaClass?.simpleName,
        lineNumber = breakpoint.sourcePosition?.line?.plus(1),
        enabled = breakpoint.isEnabled,
        timestamp = timestamp,
    )
}

/**
 * Extension function to determine breakpoint type from string identifier.
 */
private fun String.determineBreakpointType(): BreakpointType = when {
    contains("line") -> BreakpointType.LINE
    contains("method") || contains("function") -> BreakpointType.FUNCTION
    contains("field") -> BreakpointType.FIELD
    contains("exception") -> BreakpointType.EXCEPTION
    else -> BreakpointType.UNKNOWN
}