package com.github.erikboros.requestsenderplugin.toolWindow

import com.github.erikboros.requestsenderplugin.MyBundle
import com.github.erikboros.requestsenderplugin.services.MyProjectService
import com.intellij.openapi.components.service
import com.intellij.openapi.diagnostic.thisLogger
import com.intellij.openapi.project.Project
import com.intellij.openapi.wm.ToolWindow
import com.intellij.openapi.wm.ToolWindowFactory
import com.intellij.ui.JBSplitter
import com.intellij.ui.components.JBLabel
import com.intellij.ui.components.JBPanel
import com.intellij.ui.components.JBTextField
import com.intellij.ui.content.ContentFactory
import javax.swing.BoxLayout
import javax.swing.JButton
import javax.swing.JComboBox
import javax.swing.JPanel
import javax.swing.event.DocumentEvent
import javax.swing.event.DocumentListener


class MyToolWindowFactory : ToolWindowFactory {

    init {
        thisLogger().warn("Don't forget to remove all non-needed sample code files with their corresponding registration entries in `plugin.xml`.")
    }

    override fun createToolWindowContent(project: Project, toolWindow: ToolWindow) {
        val myToolWindow = MyToolWindow(toolWindow)
        val content = ContentFactory.getInstance().createContent(myToolWindow.getContent(), null, false)
        toolWindow.contentManager.addContent(content)
    }

    override fun shouldBeAvailable(project: Project) = true

    class MyToolWindow(toolWindow: ToolWindow) {

        private val service = toolWindow.project.service<MyProjectService>()

        fun getContent() = JBPanel<JBPanel<*>>().apply {
            //TODO structure UI
            //TODO use request config object

            this.layout = BoxLayout(this, BoxLayout.Y_AXIS)

            val debugLabel = JBLabel("debugLabel")

            val methodSelectPanel = JPanel()
            val httpConfigPanel = JPanel()
            val testPanel = JPanel()
            //TODO yeelight config panel
            // discover ids
            // set ids

            val cb = JComboBox<String>()
            cb.addItem("HTTP JSON")
            cb.addItem("YeeLight")

            cb.addActionListener() {
                debugLabel.text = "Selected: " + (cb.selectedItem?.toString() ?: "none")
                if (cb.selectedItem?.toString().equals("HTTP JSON")) {
                    service.setRequestMethod("http")
                    add(httpConfigPanel, 1)
                    this.revalidate()
                    this.repaint();
                } else if (cb.selectedItem?.toString().equals("YeeLight")) {
                    service.setRequestMethod("yeelight")
                    remove(1)
                    this.revalidate();
                    this.repaint();
                }
            };
            methodSelectPanel.add(JBLabel("Method: "))
            methodSelectPanel.add(cb)

            add(methodSelectPanel, 0)
            add(JBSplitter(false))

            val descriptionLabel = JBLabel(MyBundle.message("descLabel"))
            val urlTextField = JBTextField(MyBundle.message("defaultURL"))

            val dl = object : DocumentListener {

                override fun insertUpdate(e: DocumentEvent) {
                    handleTextChange()
                }

                override fun removeUpdate(e: DocumentEvent) {
                    handleTextChange()
                }
                override fun changedUpdate(e: DocumentEvent) {
                    handleTextChange()
                }
                fun handleTextChange() {
                    service.setUrl(urlTextField.text)

                    debugLabel.text = urlTextField.text
                    thisLogger().info("URL textfield changed, value saved")
                }
            }
            urlTextField.document.addDocumentListener(dl)
            httpConfigPanel.add(descriptionLabel)
            httpConfigPanel.add(urlTextField)
            add(httpConfigPanel, 1)


            testPanel.add(JButton(MyBundle.message("test")).apply {
                addActionListener {
                    //TODO test request
                    val status = service.sendRequest()
                    debugLabel.text = status
                }
            })
            testPanel.add(debugLabel)
            add(testPanel, 2)

        }
    }
}
