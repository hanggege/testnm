package com.mei.orc.util.json

import com.google.gson.JsonObject
import com.google.gson.JsonPrimitive
import com.google.gson.reflect.TypeToken
import com.mei.orc.single.gson
import org.json.JSONException
import org.json.JSONObject
import java.lang.reflect.Type
import java.util.*

fun Any?.toJson(): String? = gson.toJson(this)

inline fun <reified T : Any> String.json2Obj(): T? {
    return json2Obj(T::class.java)
}

fun <T> String.json2Obj(cls: Class<T>): T? {
    return try {
        return gson.fromJson(this, cls)
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
}

fun <T> String.json2Obj(type: Type): T? {
    return try {
        return gson.fromJson<T>(this, type)
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
}

fun String.getStringValue(key: String, defaultValue: String = ""): String {
    return try {
        JSONObject(this).getString(key)
    } catch (e: java.lang.Exception) {
        defaultValue
    }
}

@JvmOverloads
fun JSONObject.getStringValue(key: String, defaultValue: String = ""): String {
    return try {
        return getString(key)
    } catch (e: JSONException) {
        e.printStackTrace()
        defaultValue
    }
}


@JvmOverloads
fun JSONObject.getIntValue(key: String, defaultValue: Int = -1): Int {
    return try {
        return getInt(key)
    } catch (e: JSONException) {
        e.printStackTrace()
        defaultValue
    }
}

fun <T> String.jsonToList(classOfT: Class<T>): ArrayList<T> {
    val listOfT = ArrayList<T>()
    try {

        val type: Type
        if (classOfT.isPrimitive) {
            type = object : TypeToken<ArrayList<JsonPrimitive>>() {}.type
            val jsonObjs = gson.fromJson<ArrayList<JsonPrimitive>>(this, type).orEmpty()
            for (jsonObj in jsonObjs) {
                listOfT.add(gson.fromJson(jsonObj, classOfT))
            }
        } else {
            type = object : TypeToken<ArrayList<JsonObject>>() {}.type
            val jsonObjs = gson.fromJson<ArrayList<JsonObject>>(this, type).orEmpty()
            for (jsonObj in jsonObjs) {
                listOfT.add(gson.fromJson(jsonObj, classOfT))
            }
        }
    } catch (e: Exception) {
        e.printStackTrace()
    }
    return listOfT
}
