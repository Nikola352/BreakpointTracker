package com.github.nikola352.breakpointtracker.ui

import com.github.nikola352.breakpointtracker.model.Breakpoint
import com.github.nikola352.breakpointtracker.service.BreakpointDataService
import com.intellij.openapi.Disposable
import com.intellij.openapi.wm.ToolWindow
import com.intellij.ui.jcef.JBCefBrowser

class BreakpointTrackerWindow(toolWindow: ToolWindow) : Disposable {
    private val breakpointDataService = toolWindow.project.getService(BreakpointDataService::class.java)
    private val browser: JBCefBrowser = JBCefBrowser()
    private val updateListener: (List<Breakpoint>) -> Unit = { updateBrowserContent() }

    init {
        breakpointDataService.addListener(updateListener)
    }

    val content get() = browser.component

    private fun updateBrowserContent() {
        val html = generateHtml(breakpointDataService.breakpointCount, breakpointDataService.breakpoints)
        browser.loadHTML(html)
    }

    private fun generateHtml(totalCount: Int, breakpoints: Collection<Breakpoint>): String {
        val html = StringBuilder("<html><body>")
        html.append("<h2>Active Breakpoints: ").append(totalCount).append("</h2>")
        html.append("<table border='1'><tr><th>File</th><th>Line</th></tr>")

        breakpoints.forEach { bp ->
            html.append("<tr>")
                .append("<td>").append(bp.filePath).append("</td>")
                .append("<td>").append(bp.lineNumber).append("</td>")
                .append("</tr>")
        }

        html.append("</table></body></html>")
        return html.toString()
    }

    override fun dispose() {
        breakpointDataService.removeListener(updateListener)
    }
}