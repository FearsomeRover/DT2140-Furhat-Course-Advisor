package furhatos.app.courseadvisor.llm

import io.ktor.client.*
import io.ktor.client.engine.apache.*
import io.ktor.client.plugins.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException

val httpClient = HttpClient(Apache) {
    install(HttpTimeout) {
        requestTimeoutMillis = 10000L
    }
    followRedirects = true
}


class OpenAIChatCompletionModel(
    private val model: String = "gpt-4.1-mini",
    private val serviceKey: String,
    private val systemPrompt: String) {

    private val apiUrl = "https://api.openai.com/v1/chat/completions"
    suspend fun request(prompt: String): String {
        val jsonRequest = JSONObject().apply {
            put("model", model)
        }
        val messages = JSONArray()
            .put(
                JSONObject()
                    .put("role", "system")
                    .put("content", systemPrompt)
            )
            .put(
                JSONObject()
                    .put("role", "user")
                    .put("content", prompt)


            )
        jsonRequest.put("messages", messages)
        jsonRequest.put("temperature", 0.7)
        jsonRequest.put("max_tokens", 300)

        val postResultBody = try {
            val postResult = httpClient.post(apiUrl) {
                header("Content-Type", "application/json")
                header("Authorization", "Bearer $serviceKey")
                setBody(jsonRequest.toString())
                contentType(ContentType.Application.Json)
            }
            postResult.bodyAsText()
        } catch (e: IOException) {
            throw LLMException("Error in LLM request: " + (e.message?:e.cause?.message))
        }
        try {
            val responseJSONObject = JSONObject(postResultBody)

            val error: JSONObject? = responseJSONObject.getOrNull("error")
            if (error != null) {
                val errorCode: Int? = error.getOrNull("status")
                if (errorCode == 400) {
                    throw LLMUserContentFilterException()
                } else {
                    throw LLMException(error.getOrNull("message") ?: error.toString())
                }
            } else {
                val choices = responseJSONObject.getJSONArray("choices")[0] as JSONObject
                val finishReason: String? = choices.getOrNull("finish_reason")
                if (finishReason == "content_filter") {
                    throw LLMAssistantContentFilterException()
                }
                val message: JSONObject? = choices.getOrNull("message")
                val content: String = message?.getOrNull<String>("content")?.trim() ?: throw LLMException("No content in response")
                return content
            }
        } catch (e: JSONException) {
            throw LLMException("Error parsing response: ${e.message}")
        }
    }

}

open class LLMException(message: String): Exception(message)

class LLMAssistantContentFilterException(): LLMException("Content filter in system response")

class LLMUserContentFilterException(): LLMException("Content filter in user request")
