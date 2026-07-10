package com.daniil.csb.local

import androidx.datastore.core.Serializer
import com.daniil.csb.persistence.CSBStoredData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.Json
import java.io.InputStream
import java.io.OutputStream

object SettingsSerializer : Serializer<CSBStoredData> {
    override val defaultValue: CSBStoredData = CSBStoredData()

    override suspend fun readFrom(input: InputStream): CSBStoredData {
        return try {
            Json.decodeFromString(
                deserializer = CSBStoredData.serializer(),
                string = input.readBytes().decodeToString()
            )
        } catch (serialization: SerializationException) {
            defaultValue
        }
    }

    override suspend fun writeTo(t: CSBStoredData, output: OutputStream) {
        withContext(Dispatchers.IO) {
            output.write(
                Json.encodeToString(
                    serializer = CSBStoredData.serializer(),
                    value = t
                ).encodeToByteArray()
            )
        }
    }
}
