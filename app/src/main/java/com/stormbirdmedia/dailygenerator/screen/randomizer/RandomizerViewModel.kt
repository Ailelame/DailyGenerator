package com.stormbirdmedia.dailygenerator.screen.randomizer

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.stormbirdmedia.dailygenerator.domain.models.User
import com.stormbirdmedia.dailygenerator.domain.usecase.UserUseCase
import com.stormbirdmedia.dailygenerator.utils.BitmapUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class RandomizerViewModel(
    private val appContext : Context,
    private val userUseCase: UserUseCase) : ViewModel() {

    private val _state = MutableStateFlow(UiState())
    val state = _state.asStateFlow()

    data class UiState(
        var userPositionList: List<UserPosition> = listOf(),
        var konfettiState : KonfettiState = KonfettiState.Idle
    )

    sealed interface KonfettiState {
        object Idle : KonfettiState
        object Start : KonfettiState
        object Stop : KonfettiState
    }


    init {
        viewModelScope.launch(Dispatchers.IO) {
            userUseCase.getAllUsers()
                .onEach { list ->
                    _state.update {
                        _state.value.copy(userPositionList = list.toRandomizedPositions())
                    }
                }.launchIn(viewModelScope)
        }
    }

    fun setUserSelected(userName: String, isSelected: Boolean) =
        viewModelScope.launch(Dispatchers.IO) {
            userUseCase.updateUserSelected(userName, isSelected)
            if (_state.value.userPositionList.find { it.user.name == userName } != null) {
                _state.update {
                    _state.value.copy(userPositionList = _state.value.userPositionList.filter { it.user.name != userName }
                        .mapIndexed { index, positionUser -> positionUser.copy(position = index + 1) })
                }
            }
        }

    fun randomizeList() = viewModelScope.launch(Dispatchers.IO) {
        _state.update {
            it.copy(
                userPositionList = it.userPositionList.shuffled()
                    .mapIndexed { index, userPosition -> userPosition.copy(position = index + 1) })

        }
    }


    private fun List<User>.toRandomizedPositions(): List<UserPosition> =
        this.filter { user -> user.isSelected }.shuffled()
            .mapIndexed { index, user -> UserPosition(user, index + 1) }

    fun startKonfetti() {
        _state.update {
            it.copy(konfettiState = KonfettiState.Start)
        }
        deleteScreenShot()
    }

    fun stopKonfetti() {
     _state.update {
            it.copy(konfettiState = KonfettiState.Stop)
     }
    }

    fun deleteScreenShot() {
        BitmapUtils.deleteScreenshot(appContext)
    }


}

data class UserPosition(val user: User, val position: Int)