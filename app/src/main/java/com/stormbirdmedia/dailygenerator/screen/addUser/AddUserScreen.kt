package com.stormbirdmedia.dailygenerator.screen

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import com.stormbirdmedia.dailygenerator.domain.models.User
import com.stormbirdmedia.dailygenerator.screen.addUser.AddUserViewModel
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddUserScreen(
    navController: NavHostController,
    viewModel: AddUserViewModel = koinViewModel()
) {
    val state = viewModel.state.collectAsStateWithLifecycle().value
    val uiAction = viewModel.uiAction.collectAsStateWithLifecycle().value
    val context = LocalContext.current
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Ajouter un utilisateur") },
                navigationIcon = {
                    IconButton(onClick = {
                        navController.navigateUp()
                    }) {
                        Icon(Icons.Rounded.ArrowBack, "")
                    }
                }
            )
        },

        ) { innerPadding ->
        AddUserLayout(
            onAddUser = { viewModel.addUser(it) },
            modifier = Modifier.padding(innerPadding),
            userList = state.userList,
            onDeleteUser = {
                viewModel.deleteUser(it)
            }

        )
    }

    LaunchedEffect(uiAction) {
        when (uiAction) {
            is AddUserViewModel.UIAction.Success -> {
                Toast.makeText(context, "Utilisateur ajouté", Toast.LENGTH_SHORT).show()
            }
            is AddUserViewModel.UIAction.Error -> {
                Toast.makeText(context, "L'utilisateur existe déjà", Toast.LENGTH_SHORT).show()
            }
            else -> {}
        }
    }


}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddUserLayout(
    userList: List<User>,
    onAddUser: (name: String) -> Unit,
    onDeleteUser: (user: User) -> Unit,
    modifier: Modifier = Modifier
) {
    ConstraintLayout(
        modifier = modifier
            .fillMaxSize()
            .padding(8.dp)
            .then(modifier)
    ) {
        val (editText, button, list) = createRefs()
        var text by rememberSaveable(stateSaver = TextFieldValue.Saver) {
            mutableStateOf(TextFieldValue(""))
        }

        TextField(
            value = text,
            onValueChange = { text = it },
            label = { Text("Ajouter son nom") },
            singleLine = true,
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
            keyboardActions = KeyboardActions(onDone = {
                if (text.text.isNotBlank()) onAddUser(text.text)
                text = TextFieldValue("")
            }),
            modifier = Modifier.constrainAs(editText) {
                top.linkTo(parent.top)
                start.linkTo(parent.start)
                end.linkTo(parent.end)
                width = Dimension.fillToConstraints
            }
        )

        Button(
            onClick = {
                if (text.text.isNotBlank()) onAddUser(text.text)
                text = TextFieldValue("")
            },
            modifier = Modifier.constrainAs(button) {
                top.linkTo(editText.bottom, 16.dp)
                start.linkTo(parent.start)
                end.linkTo(parent.end)
            }) {
            Text("Ajouter")
        }

        LazyColumn(
            contentPadding = PaddingValues(0.dp),
            modifier = Modifier.constrainAs(list) {
                top.linkTo(button.bottom, 16.dp)
                start.linkTo(parent.start)
                end.linkTo(parent.end)
                bottom.linkTo(parent.bottom, 16.dp)
                width = Dimension.fillToConstraints
                height = Dimension.fillToConstraints
            }
        ) {
            items(userList.size) {
                UserPreviewLayout(
                    user = userList[it],
                    onDeleteUser = {
                        onDeleteUser(it)
                    }
                )

            }


        }

    }


}

@Composable
fun UserPreviewLayout(
    user: User,
    onDeleteUser: (user: User) -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = Modifier
            .padding(8.dp)
            .then(modifier),
    ) {
        ConstraintLayout(
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
                .padding(16.dp)
        ) {
            val (name, deleteButton) = createRefs()

            Text(
                text = user.name.uppercase(),
                style = MaterialTheme.typography.labelLarge,
                modifier = Modifier.constrainAs(name) {
                    top.linkTo(parent.top)
                    start.linkTo(parent.start, 8.dp)
                    end.linkTo(parent.end, 8.dp)
                    bottom.linkTo(parent.bottom)
                    width = Dimension.fillToConstraints
                }
            )
            IconButton(onClick = {
                onDeleteUser(user)
            },
                modifier = Modifier.constrainAs(deleteButton) {
                    start.linkTo(name.end)
                    end.linkTo(parent.end)
                    top.linkTo(parent.top)
                    bottom.linkTo(parent.bottom, 8.dp)
                }) {
                Icon(Icons.Rounded.Delete, "deleteItem")

            }
        }
    }
}
