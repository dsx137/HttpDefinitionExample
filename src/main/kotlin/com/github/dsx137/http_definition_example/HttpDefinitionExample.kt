package com.github.dsx137.http_definition_example

import mu.KLogger
import mu.KotlinLogging

object HttpDefinitionExample {
    val Any.logger: KLogger
        get() = KotlinLogging.logger {}

    @JvmStatic
    fun main(args: Array<String>) {
        logger.info { "Hello, World!" }
        logger.info { "apiKey:" }
        val apiKey = readln()
        logger.info { "text:" }
        val text = readln()

        val mapping = mapOf(
            "apiKey" to apiKey,
            "text" to text,
        )

        val parsedDefinitions = Utils.loadDefinitions(mapping)

        logger.info { "There are ${parsedDefinitions.size} definitions of platforms:" }
        logger.info { parsedDefinitions.keys }

        logger.info { "Platform:" }
        val platform = readln()

        Utils.sendRequest(parsedDefinitions[platform] ?: throw Exception("platform not found"))
    }
}