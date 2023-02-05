package com.stormbirdmedia.dailygenerator.screen.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.stormbirdmedia.dailygenerator.domain.models.User
import com.stormbirdmedia.dailygenerator.domain.usecase.UserUseCase
import com.stormbirdmedia.dailygenerator.infrastructure.local.provider.JokeProvider
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class MainViewModel(private val userUseCase: UserUseCase, jokeProvider: JokeProvider) : ViewModel() {


    private val _state = MutableStateFlow(UiState())
    val state = _state.asStateFlow()

    data class UiState(
        var userList: List<User> = listOf(),
    )

    init {
        viewModelScope.launch(Dispatchers.IO) {
            userUseCase.getAllUsers()
                .onEach { list ->
                    _state.update {
                        _state.value.copy(userList = list.sortForDisplay())
                    }
                }.launchIn(viewModelScope)
        }
    }

    fun setUserSelected(userName: String, isSelected: Boolean) =
        viewModelScope.launch(Dispatchers.IO) {
            userUseCase.updateUserSelected(userName, isSelected)
        }



    private fun List<User>.sortForDisplay(): List<User> {
        return this.sortedBy { it.name }.sortedBy { !it.isSelected }
    }



}

