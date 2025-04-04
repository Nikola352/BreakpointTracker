package com.github.nikola352.breakpointtracker.ui

import com.github.nikola352.breakpointtracker.service.FrontendServerService
import com.intellij.openapi.wm.ToolWindow
import com.intellij.ui.jcef.JBCefBrowser
import java.awt.BorderLayout
import javax.swing.JPanel

/**
 * JPanel containing embedded JCEF browser showing breakpoint tracker information.
 */
class BreakpointTrackerPanel(toolWindow: ToolWindow) : JPanel(BorderLayout()) {
    private val frontendServerService = toolWindow.project.getService(FrontendServerService::class.java)
    private val browser: JBCefBrowser = JBCefBrowser()

    init {
        // Add browser content to this panel
        add(browser.component, BorderLayout.CENTER)

        // Load the html page
        browser.loadURL(frontendServerService.serverUrl)
    }
}
