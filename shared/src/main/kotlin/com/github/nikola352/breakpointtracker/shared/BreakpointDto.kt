package com.github.nikola352.breakpointtracker.shared

/**
 * Data class representing a breakpoint in the tracking system.
 * Used for transferring breakpoint data between modules.
 *
 * See [com.github.nikola352.breakpointtracker.model.Breakpoint] for property descriptions.
 */
data class BreakpointDto(
    val id: String,
    val type: BreakpointTypeDto,
    val descriptor: String?,
    val filePath: String?,
    val lineNumber: Int?,
    val enabled: Boolean,
    val timestamp: Long,
)
