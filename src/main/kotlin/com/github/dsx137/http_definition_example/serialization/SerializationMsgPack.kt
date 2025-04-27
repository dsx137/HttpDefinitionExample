package com.github.dsx137.http_definition_example.serialization

import kotlinx.serialization.json.*
import org.msgpack.core.MessageBufferPacker
import org.msgpack.core.MessagePack

object SerializationMsgPack {
    fun encodeToMsgPack(jsonElement: JsonElement): ByteArray {
        val packer = MessagePack.newDefaultBufferPacker()
        if (jsonElement is JsonObject) {
            packJsonObject(packer, jsonElement)
        }
        packer.close()
        return packer.toByteArray()
    }

    private fun packJsonObject(packer: MessageBufferPacker, jsonObject: JsonObject) {
        packer.packMapHeader(jsonObject.size)
        for ((key, value) in jsonObject) {
            packer.packString(key)
            packJsonValue(packer, value)
        }
    }

    private fun packJsonArray(packer: MessageBufferPacker, jsonArray: JsonArray) {
        packer.packArrayHeader(jsonArray.size)
        for (item in jsonArray) {
            packJsonValue(packer, item)
        }
    }

    private fun packJsonValue(packer: MessageBufferPacker, value: JsonElement) {
        when (value) {
            is JsonNull -> packer.packNil()
            is JsonPrimitive -> {
                when {
                    value.isString -> packer.packString(value.content)
                    value.booleanOrNull != null -> packer.packBoolean(value.boolean)
                    value.intOrNull != null -> packer.packInt(value.int)
                    value.longOrNull != null -> packer.packLong(value.long)
                    value.floatOrNull != null -> packer.packFloat(value.float)
                    value.doubleOrNull != null -> packer.packDouble(value.double)
                    else -> packer.packString(value.content) // fallback
                }
            }

            is JsonObject -> packJsonObject(packer, value)
            is JsonArray -> packJsonArray(packer, value)
        }
    }
}
