package com.stormbirdmedia.dailygenerator.screen.main

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.stormbirdmedia.dailygenerator.domain.models.User
import com.stormbirdmedia.dailygenerator.domain.usecase.UserUseCase
import com.stormbirdmedia.dailygenerator.data.local.provider.JokeProvider
import com.stormbirdmedia.dailygenerator.utils.BitmapUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import timber.log.Timber

class MainViewModel(private val userUseCase: UserUseCase, jokeProvider: JokeProvider) :
    ViewModel() {


    private val _state = MutableStateFlow(UiState())
    val state = _state.asStateFlow()

    val _uiAction = MutableStateFlow<UIAction>(UIAction.Idle)
    val uiAction = _uiAction.asStateFlow()

    data class UiState(
        var step: UiStep = UiStep.AllParticipants(),
        var userList: List<UserPosition> = listOf(),
    )

    sealed class UiStep {
        class AllParticipants() : UiStep()
        class RandomizedList() : UiStep()
        class AddParticipant() : UiStep()
        class Jokes() : UiStep()
    }

    sealed class UIAction {
        object Idle : UIAction()
        class UserAdded(val name: String) : UIAction()
        class Error : UIAction()
    }


    init {
        viewModelScope.launch(Dispatchers.IO) {
            resetUserList()
        }
    }

    fun setUserSelected(userName: String, isSelected: Boolean) =
        viewModelScope.launch(Dispatchers.IO) {
            userUseCase.updateUserSelected(userName, isSelected)
        }


    fun setStep(step: UiStep) = viewModelScope.launch(Dispatchers.IO) {
        Timber.w("step called $step")
        _state.update {
            it.copy(step = step)
        }
        when (step) {
            is UiStep.AddParticipant -> {

            }

            is UiStep.AllParticipants -> {
                resetUserList()
            }

            is UiStep.Jokes -> {}
            is UiStep.RandomizedList -> {
                randomizeUserList()
            }
        }
    }


    fun addUser(name: String) = viewModelScope.launch(Dispatchers.IO) {
        val result =  userUseCase.insertUser(User(name.trim()))

        _uiAction.update {
            if (result.isSuccess) {
                UIAction.UserAdded(name)
            } else {
                UIAction.Error()
            }
        }
    }

    fun deleteUser(user: User) = viewModelScope.launch(Dispatchers.IO) {
            userUseCase.deleteUser(user)
    }

    fun deleteScreenShot(appContext : Context) {
        BitmapUtils.deleteScreenshot(appContext)
    }
    private suspend fun randomizeUserList() = withContext(Dispatchers.IO) {
        _state.update {
            it.copy(
                userList = it.userList.filter { it.user.isSelected }.shuffled()
                    .mapIndexed { index, userPosition -> userPosition.copy(position = index + 1) })

        }
    }


    private suspend fun resetUserList() = withContext(Dispatchers.IO) {
        userUseCase.getAllUsers()
            .collect { list ->
                _state.update {
                    _state.value.copy(
                        userList = list.sortForDisplay()
                            .mapIndexed { index, user -> UserPosition(user, position = index + 1) })
                }
            }

    }

    private fun List<User>.sortForDisplay(): List<User> {
        return this.sortedBy { it.name }.sortedBy { !it.isSelected }
    }




}
data class UserPosition(val user: User, val position: Int)
