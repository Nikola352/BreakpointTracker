package com.github.nikola352.breakpointtracker.service

import com.github.nikola352.breakpointtracker.frontend_server.BreakpointTrackerServer
import com.github.nikola352.breakpointtracker.shared.BreakpointDto
import com.intellij.openapi.Disposable
import com.intellij.openapi.components.Service

@Service(Service.Level.PROJECT)
class FrontendServerService : Disposable {
    private val server = BreakpointTrackerServer()

    val serverUrl get() = server.serverUrl

    fun updateBreakpoints(breakpointCount: Int, breakpoints: List<BreakpointDto>) {
        server.updateBreakpoints(breakpointCount, breakpoints)
    }

    override fun dispose() {
        server.shutdown()
    }
}
