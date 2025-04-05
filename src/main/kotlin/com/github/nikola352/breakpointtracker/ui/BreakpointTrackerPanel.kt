package com.github.nikola352.breakpointtracker.ui

import com.github.nikola352.breakpointtracker.service.FrontendServerService
import com.intellij.ide.ui.LafManager
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
        @Suppress("UnstableApiUsage")
        val isDark = LafManager.getInstance().currentUIThemeLookAndFeel.isDark
        val themeParam = if (isDark) "?theme=dark" else "?theme=light"
        browser.loadURL("${frontendServerService.serverUrl}$themeParam")
    }

    /** Executes JavaScript to change the current theme */
    fun updateTheme(isDark: Boolean) {
        val script = """
            document.body.classList.toggle('theme-dark', $isDark);
            document.body.classList.toggle('theme-light', ${!isDark});
        """.trimIndent()
        browser.cefBrowser.executeJavaScript(script, browser.cefBrowser.url, 0)
    }
}
