@file:OptIn(ExperimentalFoundationApi::class, ExperimentalFoundationApi::class)

package com.stormbirdmedia.dailygenerator.screen.main

import android.app.Activity
import android.content.Intent
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
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
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
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
import com.stormbirdmedia.dailygenerator.utils.BitmapUtils
import dev.shreyaspatil.capturable.Capturable
import dev.shreyaspatil.capturable.controller.rememberCaptureController
import org.koin.androidx.compose.koinViewModel
import timber.log.Timber

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
        Header()
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
fun Header(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .height(90.dp)
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
        val (content, secondaryActionButton, mainActionButton) = createRefs()

        Surface(
            Modifier
                .fillMaxWidth()
                .constrainAs(content) {
                    top.linkTo(parent.top, 8.dp)
                    bottom.linkTo(secondaryActionButton.top, 8.dp)
                    height = Dimension.fillToConstraints

                }) {
            StepLayout(
                uiState = uiState,
                setSelectedForUser = setSelectedForUser,
                onAddUser = onAddUser,
                addUser = addUser,
                deleteUser = deleteUser,
                modifier = Modifier
                    .fillMaxSize(),
            )


        }

        OutlinedButton(onClick = { if (uiState.step is MainViewModel.UiStep.AllParticipants) addUser() else seeAllParticipants() },
            modifier = Modifier
                .height(56.dp)
                .constrainAs(secondaryActionButton) {
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                    width = Dimension.fillToConstraints
                    bottom.linkTo(mainActionButton.top, 8.dp)


                }) {
            if (uiState.step is MainViewModel.UiStep.AllParticipants) Text("Ajouter un participant")
            else Text(text = "Voir les participants")
        }

        Button(onClick = { randomize() },
            modifier = Modifier
                .height(56.dp)
                .constrainAs(mainActionButton) {
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                    bottom.linkTo(parent.bottom, 8.dp)
                    width = Dimension.fillToConstraints
                }) {
            Text("Générer le daily")
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun StepLayout(
    uiState: MainViewModel.UiState,
    setSelectedForUser: (userName: String, isSelected: Boolean) -> Unit,
    deleteUser: (user: User) -> Unit,
    addUser: OnClickHandler,
    modifier: Modifier = Modifier,
    onAddUser: (name: String) -> Unit,
) {
    var step = remember {
        mutableStateOf(uiState.step)
    }

    var gridColumnCount = remember {
        mutableIntStateOf(1)
    }
    LaunchedEffect(uiState) {
        gridColumnCount.intValue = if (uiState.userList.size > 10) 2 else 1
        if (uiState.step is MainViewModel.UiStep.RandomizedList && step.value is MainViewModel.UiStep.RandomizedList) {
            return@LaunchedEffect
        }
        step.value = uiState.step

    }

    val backgroundColor by animateColorAsState(
        targetValue = if (step.value !is MainViewModel.UiStep.RandomizedList) MaterialTheme.colorScheme.background else MaterialTheme.colorScheme.surfaceVariant,
        tween(500)
    )
    val animatedElevation by animateDpAsState(
        targetValue = if (step.value !is MainViewModel.UiStep.RandomizedList) 0.dp else 8.dp,
        tween(500)
    )
    val configuration = LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp.dp

    val captureController = rememberCaptureController()
    val context = LocalContext.current

    val launcher =
        rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                Timber.d("Share success")
                // todo delete screenshot on success
            } else {
                if (result.resultCode == Activity.RESULT_CANCELED) {
                    Timber.d("Share cancelled")
                    //  todo    viewModel.deleteScreenShot()
                } else {
                    Timber.d("Share failed")
                }
            }
        }
    ConstraintLayout(modifier = modifier) {
        val (content, fab) = createRefs()
        Capturable(
            controller = captureController,
            modifier = Modifier
                .constrainAs(content) {
                    centerHorizontallyTo(parent)
                },
            onCaptured = { bitmap, error ->
                bitmap?.let {
                    val fileUri =
                        BitmapUtils.saveBitmapAndPrepareUri(context, it.asAndroidBitmap())

                    val shareIntent = Intent(Intent.ACTION_SEND)
                    shareIntent.type = "image/*"
                    shareIntent.putExtra(Intent.EXTRA_STREAM, fileUri)

                    if (shareIntent.resolveActivity(context.packageManager) != null) {
                        launcher.launch(shareIntent)
                    } else {
                        Timber.d("No app found to handle the share action")
                    }
                }

                if (error != null) {
                    Timber.e(error)
                }
            }
        ) {
            Surface(
                shadowElevation = animatedElevation,
                color = backgroundColor,
                modifier = Modifier
                    .clip(RoundedCornerShape(16.dp))
                    .width(screenWidth * 0.8f)

            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {

                    AnimatedTitleForStep(step = step.value, onAddUser = onAddUser)

                    if (uiState.userList.isEmpty() && step.value is MainViewModel.UiStep.AllParticipants) {
                        FilledTonalButton(
                            onClick = {
                                addUser()
                            },
                            modifier = Modifier.padding(top = 24.dp),
                        ) {
                            Text(text = "Ajouter le premier participant")
                        }
                    }

                    LazyVerticalGrid(
                        columns = GridCells.Fixed(gridColumnCount.intValue),
                        contentPadding = PaddingValues(horizontal = 8.dp),
                        modifier = Modifier
                            .padding(top = 24.dp, bottom = 24.dp)
                    ) {
                        itemsIndexed(items = uiState.userList,
                            key = { index, item -> uiState.userList[index].user.name }) { index, item ->
                            UserCardLayout(
                                index,
                                uiState.userList[index].user,
                                step.value,
                                setSelectedForUser,
                                deleteUser,
                                Modifier.animateItemPlacement()
                            )
                        }
                    }
                }
            }
        }

        AnimatedVisibility(
            visible = uiState.step is MainViewModel.UiStep.RandomizedList,
            enter = slideInHorizontally(
                animationSpec = tween(500),
                initialOffsetX = {
                    it * 3
                }
            ) + fadeIn(),
            exit = slideOutHorizontally(
                animationSpec = tween(500),
                targetOffsetX = {
                    it
                }
            ) + fadeOut(),
            modifier = Modifier
                .padding(8.dp)
                .constrainAs(fab) {
                    bottom.linkTo(parent.bottom, 24.dp)
                    end.linkTo(parent.end, 16.dp)
                },
        ) {
            FloatingActionButton(
                onClick = { captureController.capture() }) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_share),
                    contentDescription = "share the list generated"
                )
            }

        }


    }
}

@Composable
fun AnimatedTitleForStep(
    step: MainViewModel.UiStep,
    onAddUser: (name: String) -> Unit,
    modifier: Modifier = Modifier
) {
    var text by rememberSaveable(stateSaver = TextFieldValue.Saver) {
        mutableStateOf(TextFieldValue(""))
    }
    val keyboardController = LocalSoftwareKeyboardController.current

    Row(
        modifier = Modifier
            .heightIn(min = 64.dp)
            .then(modifier),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        AnimatedContent(targetState = step,
            content = { step ->
                when (step) {
                    is MainViewModel.UiStep.RandomizedList -> {
                        Text(
                            "Ordre du daily",
                            fontSize = 18.sp,
                            textAlign = TextAlign.Center,
                        )
                    }

                    is MainViewModel.UiStep.AddParticipant -> {
                        OutlinedTextField(
                            value = text,
                            onValueChange = { text = it },
                            label = { Text("Ajouter un participant") },
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                            keyboardActions = KeyboardActions(onDone = {
                                if (text.text.isNotBlank()) onAddUser(text.text)
                                text = TextFieldValue("")
                                keyboardController?.hide()
                            }),
                        )
                    }

                    else -> {
                        Text(
                            "Les participants",
                            fontSize = 18.sp,
                            textAlign = TextAlign.Center,
                        )
                    }
                }
            })
    }
}

@Composable
fun UserCardLayout(
    position: Int,
    currentUser: User,
    step: MainViewModel.UiStep,
    setSelectedForUser: (userName: String, isSelected: Boolean) -> Unit,
    deleteUser: (user: User) -> Unit,
    modifier: Modifier = Modifier
) {

    Row(
        modifier = modifier.padding(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = (position + 1).toString(),
            fontSize = 16.sp,
            modifier = Modifier.widthIn(min = 16.dp)
        )

        Text(
            text = currentUser.name.lowercase().capitalize(),
            fontSize = 14.sp,
            modifier = Modifier
                .padding(start = 8.dp, end = 8.dp)
                .weight(1f),
            maxLines = 2,
            overflow = TextOverflow.Ellipsis
        )


        AnimatedContent(targetState = step, Modifier.width(24.dp)) { step ->
            when (step) {
                is MainViewModel.UiStep.AddParticipant -> {
                    IconButton(
                        onClick = { deleteUser(currentUser) },
                        modifier = Modifier
                            .padding(end = 8.dp)
                            .size(18.dp)
                            .align(Alignment.Bottom)
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_delete),
                            contentDescription = "delete user ${currentUser.name}"
                        )
                    }
                }

                is MainViewModel.UiStep.AllParticipants -> {
                    IconButton(
                        onClick = { setSelectedForUser(currentUser.name, !currentUser.isSelected) },
                        modifier = Modifier
                            .padding(end = 8.dp)
                            .size(18.dp)
                            .align(Alignment.Bottom)
                    ) {
                        Icon(
                            painter = if (currentUser.isSelected) painterResource(id = R.drawable.ic_visible) else painterResource(
                                id = R.drawable.ic_invisible
                            ), contentDescription = "visibility for ${currentUser.name}",
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }

                else -> {
                    Spacer(modifier = Modifier.width(8.dp))
                }
            }
        }
    }
}


@Preview
@Composable
fun MainScreenPreview() {
    MainScreen(navController = NavController(LocalContext.current))
}
