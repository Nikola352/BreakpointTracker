package com.github.nikola352.breakpointtracker.messaging

import com.github.nikola352.breakpointtracker.model.Breakpoint
import com.intellij.util.messages.Topic

interface BreakpointChangeNotifier {
    fun breakpointsChanged(totalCount: Int, breakpoints: List<Breakpoint>)

    companion object {
        @Topic.ProjectLevel
        val TOPIC = Topic.create(
            "Breakpoint Changes",
            BreakpointChangeNotifier::class.java,
            Topic.BroadcastDirection.TO_CHILDREN
        )
    }
}