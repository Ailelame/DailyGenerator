@file:OptIn(ExperimentalFoundationApi::class)

package com.stormbirdmedia.dailygenerator.screen.main

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.outlined.AddCircle
import androidx.compose.material.icons.outlined.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.airbnb.lottie.compose.*
import com.stormbirdmedia.dailygenerator.MainDestination
import com.stormbirdmedia.dailygenerator.OnClickHandler
import com.stormbirdmedia.dailygenerator.R
import com.stormbirdmedia.dailygenerator.domain.models.User
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    navController: NavController,
    viewmodel: MainViewModel = koinViewModel()
) {
    val uiState = viewmodel.state.collectAsStateWithLifecycle().value



    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Daily Generator") },
                actions = {
                    IconButton(onClick = {
                        navController.navigate(MainDestination.Joke.route)
                    }) {
                        val composition: LottieCompositionResult =
                            rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.cat))
                        val progress = animateLottieCompositionAsState(
                            composition.value,
                            iterations = LottieConstants.IterateForever
                        )
                        LottieAnimation(
                            composition = composition.value,
                            progress = progress.value
                        )

                    }
                    IconButton(onClick = {
                        navController.navigate(MainDestination.AddUser.route)
                    }) {
                        Icon(Icons.Outlined.AddCircle, "")
                    }

                }
            )
        }
    ) { innerPadding ->

        ParticipantsLayout(
            userList = uiState.userList,
            setSelectedForUser = { userName, isSelected ->
                viewmodel.setUserSelected(
                    userName,
                    isSelected
                )
            },
            randomize = { navController.navigate(MainDestination.Randomizer.route) },
            modifier = Modifier.padding(innerPadding)
        )


    }
}

@Composable
fun ParticipantsLayout(
    userList: List<User>,
    setSelectedForUser: (userName: String, isSelected: Boolean) -> Unit,
    randomize: OnClickHandler,
    modifier: Modifier = Modifier
) {

    ConstraintLayout(
        modifier = Modifier
            .fillMaxSize()
            .then(modifier)
    ) {
        val (title, list, validateButton) = createRefs()

        Text(
            text = "${userList.filter { it.isSelected }.size}/${userList.size} participants",
            style = MaterialTheme.typography.labelLarge,
            modifier = Modifier.constrainAs(title) {
                top.linkTo(parent.top)
                start.linkTo(parent.start)
            })


        LazyColumn(
            modifier = Modifier.constrainAs(list) {
                top.linkTo(title.bottom, 8.dp)
                start.linkTo(parent.start)
                end.linkTo(parent.end)
                bottom.linkTo(validateButton.top)
                height = Dimension.fillToConstraints
                width = Dimension.fillToConstraints
            }) {
            items(userList.size,
                key = { userList[it].name }) {
                UserCardLayout(
                    userList[it],
                    setSelectedForUser,
                    Modifier.animateItemPlacement()
                )
            }

        }

        Button(onClick = { randomize() }, modifier = Modifier.constrainAs(validateButton) {
            top.linkTo(list.bottom, 8.dp)
            bottom.linkTo(parent.bottom)
            start.linkTo(parent.start)
            end.linkTo(parent.end)
        }) {
            Text(text = "Randomize")
        }
    }

}


@Composable
fun UserCardLayout(
    currentUser: User,
    setSelectedForUser: (userName: String, isSelected: Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .then(modifier),
    ) {
        val checkedState = remember { mutableStateOf(currentUser.isSelected) }

        ConstraintLayout(
            modifier = Modifier.fillMaxWidth()
        ) {
            val (icon, name, checkbox) = createRefs()
            Icon(
                imageVector = Icons.Default.Person,
                contentDescription = "${currentUser.name}",
                modifier = Modifier
                    .padding(start = 8.dp)
                    .constrainAs(icon) {
                        top.linkTo(parent.top)
                        bottom.linkTo(parent.bottom)
                        start.linkTo(parent.start, 8.dp)
                    }
            )

            Text(
                text = currentUser.name.uppercase(),
                style = MaterialTheme.typography.labelLarge,
                modifier = Modifier.constrainAs(name) {
                    top.linkTo(parent.top)
                    bottom.linkTo(parent.bottom)
                    start.linkTo(icon.end, 8.dp)
                    end.linkTo(checkbox.start, 8.dp)
                    width = Dimension.fillToConstraints
                })
            Checkbox(checked = currentUser.isSelected, onCheckedChange = {
                setSelectedForUser(currentUser.name, it)
                checkedState.value = it
            }, modifier = Modifier.constrainAs(checkbox) {
                top.linkTo(parent.top)
                bottom.linkTo(parent.bottom)
                end.linkTo(parent.end, 8.dp)
            })
        }
    }
}


@Preview
@Composable
fun MainScreenPreview() {
    MainScreen(navController = NavController(LocalContext.current))
}
