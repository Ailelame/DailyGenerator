package com.stormbirdmedia.dailygenerator.domain.models


import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class Joke(
    @Json(name = "answer")
    val answer: String,
    @Json(name = "id")
    val id: Int,
    @Json(name = "joke")
    val joke: String,
    @Json(name = "type")
    val type: String
)