package com.github.erikboros.requestsenderplugin.services

import com.github.erikboros.requestrenderplugin.YeeLightRequest
import com.github.erikboros.requestsenderplugin.MyBundle
import com.intellij.openapi.components.Service
import com.intellij.openapi.diagnostic.thisLogger
import com.intellij.openapi.project.Project
import java.net.HttpURLConnection
import java.net.URL


@Service(Service.Level.PROJECT)
class MyProjectService(project: Project) {
    //TODO save configs between starts
    //TODO handle defaults

    //TODO use request config object
    private var requestMethod = "http"
    private var requestUrl = MyBundle.message("defaultURL")

    init {
        thisLogger().info(MyBundle.message("projectService", project.name))
        thisLogger().warn("Don't forget to remove all non-needed sample code files with their corresponding registration entries in `plugin.xml`.")
    }

    fun setRequestMethod(method: String) {
        requestMethod = method
    }

    fun setUrl(url: String) {
        requestUrl = url
    }

    fun sendRequest(): String {
        //TODO use pattern for executing correct request
        if (requestMethod.equals("http")) {
            return sendHttpRequest().toString()
        } else if (requestMethod.equals("yeelight")) {
            return sendYeeLightRequest()
        } else {
            //TODO error handling
            return ""
        }
    }

    private fun sendYeeLightRequest(): String {
        return YeeLightRequest.sendSuccess(listOf(454661602));
    }

    private fun sendHttpRequest(): Int {
        //TODO error handling, no connection, http error, etc
        val jsonInputString = "{\"param\": \"val\"}"

        val con: HttpURLConnection = URL(requestUrl).openConnection() as HttpURLConnection
        con.requestMethod = "POST"
        con.setRequestProperty("Content-Type", "application/json")
        con.connectTimeout = 5000
        con.readTimeout = 5000
        con.doOutput = true

        con.outputStream.use { os ->
            val input = jsonInputString.toByteArray(charset("utf-8"))
            os.write(input, 0, input.size)
        }
//        var streamReader: Reader? = null
//        if (status > 299) {
//            streamReader = InputStreamReader(con.errorStream)
//        } else {
//            streamReader = InputStreamReader(con.inputStream)
//        }

        return con.responseCode
    }
}
