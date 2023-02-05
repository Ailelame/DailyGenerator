package com.stormbirdmedia.dailygenerator.infrastructure.local.provider

import android.content.Context
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import com.stormbirdmedia.dailygenerator.R
import com.stormbirdmedia.dailygenerator.domain.models.Joke
import timber.log.Timber
import java.io.*


class JokeProvider(val appContext : Context) {

    private val jokes = mutableListOf<Joke>()

    init {
        try {
            val joskesJson: InputStream = appContext.resources.openRawResource(R.raw.jokes)
            val writer: Writer = StringWriter()
            val buffer = CharArray(1024)
            try {
                val reader: Reader = BufferedReader(InputStreamReader(joskesJson, "UTF-8"))
                var n: Int
                while (reader.read(buffer).also { n = it } != -1) {
                    writer.write(buffer, 0, n)
                }

            } catch (e: IOException) {
                e.printStackTrace()
            }

            finally {
                joskesJson.close()
            }
            val moshi = Moshi.Builder().build()
            val type = Types.newParameterizedType(
                List::class.java,
                Joke::class.java
            )
            val adapter: JsonAdapter<List<Joke>> = moshi.adapter(type)

            jokes.addAll(adapter.fromJson(writer.toString())!!)

        } catch (e: IOException) {
            e.printStackTrace()
        } finally {
            Timber.d("Jokes loaded : ${jokes.map { it.id }}")
        }
    }

    fun getRandomJoke() : Joke {
        return jokes.random()
    }


}