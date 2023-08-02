@file:OptIn(ExperimentalFoundationApi::class, ExperimentalFoundationApi::class)

package com.stormbirdmedia.dailygenerator.screen.main

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.airbnb.lottie.compose.*
import com.stormbirdmedia.dailygenerator.MainDestination
import com.stormbirdmedia.dailygenerator.OnClickHandler
import com.stormbirdmedia.dailygenerator.domain.models.User
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    navController: NavController,
    viewmodel: MainViewModel = koinViewModel()
) {
    val uiState = viewmodel.state.collectAsStateWithLifecycle().value

    Column(Modifier.fillMaxSize()) {

        Header()
        Content(
            userList = uiState.userList,
            setSelectedForUser = { userName, isSelected ->
                viewmodel.setUserSelected(
                    userName,
                    isSelected
                )
            },
            randomize = { navController.navigate(MainDestination.Randomizer.route) },
            addUser = { navController.navigate(MainDestination.AddUser.route) },
            modifier = Modifier.weight(1f)
        )

    }
}


@Composable
fun Header(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .height(150.dp)
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
    userList: List<User>,
    setSelectedForUser: (userName: String, isSelected: Boolean) -> Unit,
    randomize: OnClickHandler,
    addUser: OnClickHandler
) {
    ConstraintLayout(modifier = modifier) {
        val (title, lazyColumn, addParticipantButton, randomizeButton) = createRefs()

        Text("Qui participe au daily?", modifier = Modifier.constrainAs(title) {
            centerHorizontallyTo(parent)
            top.linkTo(parent.top, 8.dp)
        })

        LazyColumn(modifier = Modifier
            .fillMaxWidth()
            .constrainAs(lazyColumn) {
                top.linkTo(title.bottom, 8.dp)
                start.linkTo(parent.start)
                end.linkTo(parent.end)
                bottom.linkTo(addParticipantButton.top, 8.dp)
                height = Dimension.fillToConstraints
            }) {
            itemsIndexed(
                items = userList,
                key = { index, item -> userList[index].name }) { index, item ->
                UserCardLayout(
                    index,
                    userList[index],
                    setSelectedForUser,
                    Modifier.animateItemPlacement()
                )
            }
        }

        OutlinedButton(onClick = { addUser() }, modifier = Modifier
            .height(56.dp)
            .constrainAs(addParticipantButton) {
                start.linkTo(parent.start)
                end.linkTo(parent.end)
                width = Dimension.fillToConstraints
                bottom.linkTo(randomizeButton.top, 8.dp)


            }) {
            Text("Ajouter un participant")
        }

        Button(onClick = { randomize() }, modifier = Modifier
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
fun UserCardLayout(
    position: Int,
    currentUser: User,
    setSelectedForUser: (userName: String, isSelected: Boolean) -> Unit,
    modifier: Modifier = Modifier
) {


    Row {
        Text(
            text = position.toString(),
            style = MaterialTheme.typography.bodyLarge,

        )

        Text(
            text = currentUser.name.lowercase().capitalize(),
            style = MaterialTheme.typography.labelLarge,
            modifier = Modifier.padding(start = 8.dp)
        )

    }

//    Card(
//        modifier = Modifier
//            .fillMaxWidth()
//            .padding(8.dp)
//            .then(modifier),
//    ) {
//        val checkedState = remember { mutableStateOf(currentUser.isSelected) }
//
//        ConstraintLayout(
//            modifier = Modifier.fillMaxWidth()
//        ) {
//            val (icon, name, checkbox) = createRefs()
////            Icon(
////                imageVector = Icons.Default.Person,
////                contentDescription = "${currentUser.name}",
////                modifier = Modifier
////                    .padding(start = 8.dp)
////                    .constrainAs(icon) {
////                        top.linkTo(parent.top)
////                        bottom.linkTo(parent.bottom)
////                        start.linkTo(parent.start, 8.dp)
////                    }
////            )
//
//            Text(
//                text = currentUser.name.lowercase().capitalize(),
//                style = MaterialTheme.typography.labelLarge,
//                modifier = Modifier.constrainAs(name) {
//                    top.linkTo(parent.top)
//                    bottom.linkTo(parent.bottom)
//                    start.linkTo(parent.start, 16.dp)
//                    end.linkTo(checkbox.start, 8.dp)
//                    width = Dimension.fillToConstraints
//                })
//            Checkbox(checked = currentUser.isSelected, onCheckedChange = {
//                setSelectedForUser(currentUser.name, it)
//                checkedState.value = it
//            }, modifier = Modifier.constrainAs(checkbox) {
//                top.linkTo(parent.top)
//                bottom.linkTo(parent.bottom)
//                end.linkTo(parent.end, 8.dp)
//            })
//        }
//    }
}


@Preview
@Composable
fun MainScreenPreview() {
    MainScreen(navController = NavController(LocalContext.current))
}
