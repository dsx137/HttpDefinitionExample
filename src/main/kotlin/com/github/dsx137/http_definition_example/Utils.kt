package com.github.dsx137.http_definition_example

import com.github.dsx137.http_definition_example.HttpDefinitionExample.logger
import com.github.dsx137.http_definition_example.serialization.SerializationMsgPack
import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.util.*
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import java.awt.Desktop
import java.io.File

object Utils {
    val client = HttpClient(CIO)

    fun loadDefinitions(mapping: Map<String, String>): Map<String, Data.Definition> {
        val patternDir = File("definitions")
        if (!patternDir.exists()) {
            patternDir.mkdirs()
            logger.info("patterns directory not found, creating...")
            return emptyMap()
        }

        return patternDir
            .listFiles { file -> file.isFile && file.extension == "json" }
            ?.mapNotNull { file ->
                try {
                    val text = file.readText()
                    file.nameWithoutExtension to Json.decodeFromString<Data.Definition>(inject(text, mapping))
                } catch (e: Exception) {
                    logger.error("Failed to load ${file.nameWithoutExtension}: ${e.message}")
                    null
                }
            }?.toMap() ?: emptyMap()
    }

    fun inject(pattern: String, mapping: Map<String, String>): String {
        return Regex("""(?<!\\)\$\{(\w+)}""").replace(pattern) { mapping[it.groupValues[1]] ?: it.value }
    }

    fun serialize(contentType: ContentType, body: JsonElement): ByteArray {
        return when {
            contentType.match(ContentType.parse("application/msgpack")) -> {
                SerializationMsgPack.encodeToMsgPack(body)
            }

            contentType.match(ContentType.parse("application/json")) -> {
                Json.encodeToString(body).toByteArray(Charsets.UTF_8)
            }

            else -> throw Exception("Unsupported content type: $contentType")
        }
    }

    fun sendRequest(parsedDefinition: Data.Definition) {
        runBlocking {
            val response = client.post(parsedDefinition.request.url) {
                headers { parsedDefinition.request.headers.forEach { (key, value) -> append(key, value) } }
                setBody(
                    serialize(
                        ContentType.parse(
                            parsedDefinition.request.headers["Content-Type"] ?: "application/json"
                        ), parsedDefinition.request.body
                    )
                )
            }

            if (response.status.isSuccess()) logger.info("Request successful")
            else logger.error("Request failed: ${response.status}")

            Desktop.getDesktop().open(File("response.mp3").apply {
                writeBytes(
                    when (parsedDefinition.response.type) {
                        "binary" -> response.bodyAsChannel().toByteArray()
                        "json" -> {
                            val json = Json.parseToJsonElement(response.bodyAsText())
                            val audioUrl = parsedDefinition.response.audioField?.let {
                                json.jsonObject[it]?.jsonPrimitive?.content
                            } ?: throw Exception("audioField not found in response")

                            val audioResponse = client.get(audioUrl)
                            audioResponse.bodyAsChannel().toByteArray()
                        }

                        else -> throw Exception("Unsupported response type: ${parsedDefinition.response.type}")
                    })
            })
        }
    }
}