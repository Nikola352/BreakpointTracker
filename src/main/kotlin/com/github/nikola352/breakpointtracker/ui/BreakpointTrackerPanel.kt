package com.github.nikola352.breakpointtracker.ui

import com.github.nikola352.breakpointtracker.model.Breakpoint
import com.github.nikola352.breakpointtracker.service.BreakpointDataService
import com.intellij.openapi.wm.ToolWindow
import com.intellij.ui.jcef.JBCefBrowser
import java.awt.BorderLayout
import javax.swing.JPanel

class BreakpointTrackerPanel(toolWindow: ToolWindow) : JPanel(BorderLayout()) {
    private val breakpointDataService = toolWindow.project.getService(BreakpointDataService::class.java)
    private val browser: JBCefBrowser = JBCefBrowser()

    init {
        // Add browser content to this panel
        add(browser.component, BorderLayout.CENTER)

        // Initial content load
        updateContent(breakpointDataService.breakpointCount, breakpointDataService.breakpoints)
    }

    fun updateContent(totalCount: Int, breakpoints: List<Breakpoint>) {
        val html = generateHtml(totalCount, breakpoints)
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
}