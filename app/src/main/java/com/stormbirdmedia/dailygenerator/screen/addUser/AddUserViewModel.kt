package com.stormbirdmedia.dailygenerator.screen.addUser

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.stormbirdmedia.dailygenerator.domain.models.User
import com.stormbirdmedia.dailygenerator.domain.usecase.UserUseCase
import com.stormbirdmedia.dailygenerator.screen.main.MainViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class AddUserViewModel(private val userUseCase: UserUseCase) : ViewModel() {

    val uiAction = MutableStateFlow<UIAction>(UIAction.Idle)

    private val _state = MutableStateFlow(UIState())
    val state = _state.asStateFlow()

    data class UIState(
        val userList: List<User> = listOf()
    )


    sealed class UIAction {
        object Idle : UIAction()
        class Success(val name: String) : UIAction()
        class Error : UIAction()
    }


    init {
        viewModelScope.launch(Dispatchers.IO) {
            userUseCase.getAllUsers().collect { list ->
                _state.update {
                    _state.value.copy(userList = list.reversed())
                }
            }
        }
    }

    fun addUser(name: String) = viewModelScope.launch(Dispatchers.IO) {
       val result =  userUseCase.insertUser(User(name))
        if(result.isSuccess) {
            uiAction.update{
                UIAction.Success(name)
            }
        } else {
            uiAction.value = UIAction.Error()
        }

    }

    fun deleteUser(user: User) = viewModelScope.launch(Dispatchers.IO) {
        userUseCase.deleteUser(user)
    }
}