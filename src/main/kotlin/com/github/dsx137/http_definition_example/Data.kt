package com.github.dsx137.http_definition_example

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonElement

object Data {
    @Serializable
    data class Request(
        val url: String,
        val headers: Map<String, String>,
        val body: JsonElement,
    )

    @Serializable
    data class Response(
        val type: String,
        val audioField: String? = null,
    )

    @Serializable
    data class Definition(
        val request: Request,
        val response: Response,
    )
}