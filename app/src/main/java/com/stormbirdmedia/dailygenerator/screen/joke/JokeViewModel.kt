package com.stormbirdmedia.dailygenerator.screen.joke

import androidx.lifecycle.ViewModel
import com.stormbirdmedia.dailygenerator.domain.models.Joke
import com.stormbirdmedia.dailygenerator.data.local.provider.JokeProvider
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class JokeViewModel(private val jokeProvider: JokeProvider) : ViewModel() {

    private val _state = MutableStateFlow(JokeUiState(jokeProvider.getRandomJoke()))
    val state = _state.asStateFlow()

    data class JokeUiState(
        val joke: Joke
    )

    fun onRandomJokeClicked() {
        _state.update {
            JokeUiState(jokeProvider.getRandomJoke())
        }
    }

}