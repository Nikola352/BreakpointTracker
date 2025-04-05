package com.github.nikola352.breakpointtracker.ui

import com.github.nikola352.breakpointtracker.service.FrontendServerService
import com.intellij.ide.ui.LafManager
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.fileEditor.FileEditorManager
import com.intellij.openapi.fileEditor.OpenFileDescriptor
import com.intellij.openapi.vfs.LocalFileSystem
import com.intellij.openapi.wm.ToolWindow
import com.intellij.ui.jcef.JBCefBrowser
import com.intellij.ui.jcef.JBCefBrowserBase
import com.intellij.ui.jcef.JBCefJSQuery
import org.cef.browser.CefBrowser
import org.cef.browser.CefFrame
import org.cef.handler.CefLoadHandlerAdapter
import java.awt.BorderLayout
import javax.swing.JPanel

/**
 * JPanel containing embedded JCEF browser showing breakpoint tracker information.
 */
class BreakpointTrackerPanel(toolWindow: ToolWindow) : JPanel(BorderLayout()) {
    private val project = toolWindow.project
    private val frontendServerService = project.getService(FrontendServerService::class.java)
    private val browser: JBCefBrowser = JBCefBrowser()
    private val goToBreakpointQuery: JBCefJSQuery = JBCefJSQuery.create(browser as JBCefBrowserBase)

    init {
        // Add browser content to this panel
        add(browser.component, BorderLayout.CENTER)

        // Load the html page
        browser.loadURL("${frontendServerService.serverUrl}${getThemeParam()}")

        initializeNavigateQuery()
    }

    /** Get the url param based on light/dark theme mode */
    @Suppress("UnstableApiUsage")
    private fun getThemeParam(): String {
        val isDark = LafManager.getInstance().currentUIThemeLookAndFeel.isDark
        return if (isDark) "?theme=dark" else "?theme=light"
    }

    /** Executes JavaScript to change the current theme */
    fun updateTheme(isDark: Boolean) {
        val script = """
            document.body.classList.toggle('theme-dark', $isDark);
            document.body.classList.toggle('theme-light', ${!isDark});
        """.trimIndent()
        browser.cefBrowser.executeJavaScript(script, browser.cefBrowser.url, 0)
    }

    /** Initialize the JS query for breakpoint navigation */
    private fun initializeNavigateQuery() {
        goToBreakpointQuery.addHandler { link ->
            handleGoToBreakpoint(link)
            null
        }

        // Create a JS function that calls the handler
        browser.jbCefClient.addLoadHandler(object : CefLoadHandlerAdapter() {
            override fun onLoadEnd(browser: CefBrowser?, frame: CefFrame?, httpStatusCode: Int) {
                val script = """
                    window.goToBreakpoint = function(link) {
                        ${goToBreakpointQuery.inject("link")}
                    }
                """.trimIndent()
                browser?.executeJavaScript(script, browser.url, 0)
            }
        }, browser.cefBrowser)
    }

    private fun handleGoToBreakpoint(link: String): JBCefJSQuery.Response {
        try {
            val (filePath, lineNumber) = link.split("|")
            navigateToBreakpoint(filePath, lineNumber.toInt())
            return JBCefJSQuery.Response(null)
        } catch (e: Exception) {
            return JBCefJSQuery.Response(null, 1, "Failed to navigate to breakpoint: ${e.message}")
        }
    }

    private fun navigateToBreakpoint(filePath: String, lineNumber: Int) {
        ApplicationManager.getApplication().invokeLater {
            val virtualFile = LocalFileSystem.getInstance().findFileByPath(filePath)
            if (virtualFile != null) {
                FileEditorManager.getInstance(project).openTextEditor(
                    OpenFileDescriptor(project, virtualFile, lineNumber - 1, 0),
                    true // request focus
                )
            }
        }
    }
}
