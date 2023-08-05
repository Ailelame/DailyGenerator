@file:OptIn(ExperimentalFoundationApi::class, ExperimentalFoundationApi::class)

package com.stormbirdmedia.dailygenerator.screen.main

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.stormbirdmedia.dailygenerator.OnClickHandler
import com.stormbirdmedia.dailygenerator.R
import com.stormbirdmedia.dailygenerator.domain.models.User
import org.koin.androidx.compose.koinViewModel

@Composable
fun MainScreen(
    navController: NavController, viewModel: MainViewModel = koinViewModel()
) {
    val uiState = viewModel.state.collectAsStateWithLifecycle().value
    val uiAction = viewModel.uiAction.collectAsStateWithLifecycle().value

    val context = LocalContext.current
    LaunchedEffect(uiAction) {
        when (uiAction) {
            is MainViewModel.UIAction.UserAdded -> {
                Toast.makeText(context, "Utilisateur ajouté", Toast.LENGTH_SHORT).show()
            }

            is MainViewModel.UIAction.Error -> {
                Toast.makeText(context, "L'utilisateur existe déjà", Toast.LENGTH_SHORT).show()
            }

            else -> {}
        }
    }

    Column(Modifier.fillMaxSize()) {
        Header(uiState)
        Content(
            uiState = uiState,
            setSelectedForUser = { userName, isSelected ->
                viewModel.setUserSelected(
                    userName, isSelected
                )
            },
            randomize = { viewModel.setStep(MainViewModel.UiStep.RandomizedList()) },
            addUser = { viewModel.setStep(MainViewModel.UiStep.AddParticipant()) },
            onAddUser = {
                viewModel.addUser(it)
            },
            seeAllParticipants = { viewModel.setStep(MainViewModel.UiStep.AllParticipants()) },
            deleteUser = { viewModel.deleteUser(it) },
            modifier = Modifier.weight(1f),
        )

    }
}


@Composable
fun Header(uiState: MainViewModel.UiState, modifier: Modifier = Modifier) {

    val dpAnimator = animateDpAsState(
        if (uiState.step !is MainViewModel.UiStep.RandomizedList) 120.dp else 64.dp,
        tween(200)
    )

    Column(
        modifier = modifier
            .height(dpAnimator.value)
            .fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center

    ) {
        Text(text = "Daily\nGenerator", fontSize = 24.sp, textAlign = TextAlign.Center)
    }
}


@Composable
fun Content(
    modifier: Modifier = Modifier,
    uiState: MainViewModel.UiState,
    setSelectedForUser: (userName: String, isSelected: Boolean) -> Unit,
    onAddUser: (name: String) -> Unit,
    deleteUser: (user: User) -> Unit,
    randomize: OnClickHandler,
    addUser: OnClickHandler,
    seeAllParticipants: OnClickHandler
) {
    var stepIsAddUser = remember {
        mutableStateOf(false)
    }
    LaunchedEffect(uiState.step) {
        stepIsAddUser.value = uiState.step is MainViewModel.UiStep.AddParticipant
    }

    ConstraintLayout(modifier = modifier) {
        val (content, addParticipantButton, randomizeButton) = createRefs()

        Surface(
            Modifier
                .fillMaxWidth()
                .constrainAs(content) {
                    top.linkTo(parent.top, 8.dp)
                    bottom.linkTo(addParticipantButton.top, 8.dp)
                    height = Dimension.fillToConstraints

                }) {
            AnimatedVisibility(
                visible = !stepIsAddUser.value,
                enter = slideInHorizontally() + fadeIn(),
                exit = slideOutHorizontally() + fadeOut()
            ) {
                StepLayout(
                    uiState = uiState,
                    setSelectedForUser = setSelectedForUser,
                    deleteUser = {},
                    modifier = Modifier
                        .fillMaxSize(),
                )

            }

            AnimatedVisibility(
                visible = stepIsAddUser.value,
                enter = slideInHorizontally(
                    initialOffsetX = { it }
                ) + fadeIn(),
                exit = slideOutHorizontally(
                    targetOffsetX = { -it }
                ) + fadeOut()
            ) {
                AddUserLayout(
                    uiState = uiState,
                    modifier = Modifier
                        .fillMaxSize(),
                    onAddUser = onAddUser,
                    deleteUser = deleteUser,

                    )
            }

        }




        OutlinedButton(onClick = { if (uiState.step is MainViewModel.UiStep.AllParticipants) addUser() else seeAllParticipants() },
            modifier = Modifier
                .height(56.dp)
                .constrainAs(addParticipantButton) {
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                    width = Dimension.fillToConstraints
                    bottom.linkTo(randomizeButton.top, 8.dp)


                }) {
            if (uiState.step is MainViewModel.UiStep.AllParticipants) Text("Ajouter un participant")
            else Text(text = "Voir les participants")
        }

        Button(onClick = { randomize() },
            modifier = Modifier
                .height(56.dp)
                .constrainAs(randomizeButton) {
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                    bottom.linkTo(parent.bottom, 8.dp)
                    width = Dimension.fillToConstraints
                }) {
            Text("Générer le daily")
        }
    }
}

@Composable
fun StepLayout(
    uiState: MainViewModel.UiState,
    setSelectedForUser: (userName: String, isSelected: Boolean) -> Unit,
    deleteUser: (user: User) -> Unit,
    modifier: Modifier = Modifier,
) {
    var isAllParticipantsScreen =
        remember { mutableStateOf(uiState.step is MainViewModel.UiStep.AllParticipants) }
    var gridColumnCount = remember {
        mutableIntStateOf(1)
    }
    LaunchedEffect(uiState) {
        isAllParticipantsScreen.value = uiState.step is MainViewModel.UiStep.AllParticipants

        gridColumnCount.intValue = if (uiState.userList.size > 8) 2 else 1
    }

    val dpAnimator by animateDpAsState(
        if (isAllParticipantsScreen.value) 16.dp else 24.dp,
        tween(200)
    )

    val backgroundColor by animateColorAsState(
        targetValue = if (isAllParticipantsScreen.value) MaterialTheme.colorScheme.background else MaterialTheme.colorScheme.surfaceVariant,
        tween(500)
    )
    val configuration = LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp.dp

    Column(modifier = modifier, horizontalAlignment = Alignment.CenterHorizontally) {
        Column(
            modifier = Modifier
                .clip(RoundedCornerShape(16.dp))
                .width(screenWidth * 0.8f)
                .background(backgroundColor),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            AnimatedVisibility(
                visible = isAllParticipantsScreen.value,
                enter = fadeIn(),
                exit = slideOutVertically() + fadeOut()
            ) {
                Text(
                    "Qui participe au daily?",
                    fontSize = 18.sp,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(top = dpAnimator)
                )
            }
            AnimatedVisibility(
                visible = !isAllParticipantsScreen.value,
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                Text(
                    "Ordre du daily",
                    fontSize = 18.sp,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(top = dpAnimator)
                )
            }

            LazyVerticalGrid(
                columns = GridCells.Fixed(gridColumnCount.intValue),
                modifier = Modifier
                    .padding(top = 24.dp, bottom = dpAnimator)
            ) {
                itemsIndexed(items = uiState.userList,
                    key = { index, item -> uiState.userList[index].user.name }) { index, item ->
                    UserCardLayout(
                        index,
                        uiState.userList[index].user,
                        uiState.step is MainViewModel.UiStep.AllParticipants,
                        false,
                        setSelectedForUser,
                        deleteUser,
                        Modifier.animateItemPlacement()
                    )
                }
            }
        }
    }


}


@Composable
fun UserCardLayout(
    position: Int,
    currentUser: User,
    showSelectionButton: Boolean,
    showDeleteButton: Boolean,
    setSelectedForUser: (userName: String, isSelected: Boolean) -> Unit,
    deleteUser: (user: User) -> Unit,
    modifier: Modifier = Modifier
) {
    val selectionButtonAlphaAnimation by animateFloatAsState(targetValue = if (showSelectionButton) 1f else 0f)

    Row(
        modifier = modifier.padding(vertical = 8.dp, horizontal = 16.dp),
    ) {
        Text(
            text = (position + 1).toString(),
            fontSize = 16.sp,
            modifier = Modifier.widthIn(min = 16.dp)
        )

        Text(
            text = currentUser.name.lowercase().capitalize(),
            fontSize = 14.sp,
            modifier = Modifier.padding(start = 8.dp)
        )

        Spacer(modifier = Modifier.weight(1f))
        IconButton(
            onClick = { setSelectedForUser(currentUser.name, !currentUser.isSelected) },
            modifier = Modifier
                .alpha(selectionButtonAlphaAnimation)
                .size(18.dp)
                .align(Alignment.Bottom)
        ) {
            Icon(
                painter = if (currentUser.isSelected) painterResource(id = R.drawable.ic_visible) else painterResource(
                    id = R.drawable.ic_invisible
                ), contentDescription = "visibility for ${currentUser.name}"
            )
        }

        if (showDeleteButton) {
            IconButton(
                onClick = { deleteUser(currentUser) },
                modifier = Modifier
                    .size(18.dp)
                    .align(Alignment.Bottom)

            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_delete),
                    contentDescription = "delete user ${currentUser.name}"
                )
            }
        }

    }
}

@Composable
fun AddUserLayout(
    modifier: Modifier = Modifier,
    onAddUser: (name: String) -> Unit,
    deleteUser: (user: User) -> Unit,
    uiState: MainViewModel.UiState,
) {
    var text by rememberSaveable(stateSaver = TextFieldValue.Saver) {
        mutableStateOf(TextFieldValue(""))
    }

    var gridColumnCount = remember {
        mutableIntStateOf(1)
    }
    LaunchedEffect(uiState) {
        gridColumnCount.intValue = if (uiState.userList.size > 8) 2 else 1
    }

    Column(modifier = modifier, horizontalAlignment = Alignment.CenterHorizontally) {
        OutlinedTextField(
            value = text,
            onValueChange = { text = it },
            label = { Text("Ajouter son nom") },
            singleLine = true,
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
            keyboardActions = KeyboardActions(onDone = {
                if (text.text.isNotBlank()) onAddUser(text.text)
                text = TextFieldValue("")
            }),

            )

        LazyVerticalGrid(
            columns = GridCells.Fixed(gridColumnCount.intValue),
            modifier = Modifier
                .padding(top = 24.dp)
                .weight(1f)
        ) {
            itemsIndexed(items = uiState.userList,
                key = { index, item -> uiState.userList[index].user.name }) { index, item ->
                UserCardLayout(
                    index,
                    uiState.userList[index].user,
                    uiState.step is MainViewModel.UiStep.AllParticipants,
                    true,
                    { _, _ -> },
                    deleteUser,
                    Modifier.animateItemPlacement()
                )
            }
        }

    }
}


@Preview
@Composable
fun MainScreenPreview() {
    MainScreen(navController = NavController(LocalContext.current))
}
