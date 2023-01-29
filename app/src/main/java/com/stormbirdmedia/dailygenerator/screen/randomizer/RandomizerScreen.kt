@file:OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class)

package com.stormbirdmedia.dailygenerator.screen.randomizer

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.stormbirdmedia.dailygenerator.OnClickHandler
import com.stormbirdmedia.dailygenerator.R
import com.stormbirdmedia.dailygenerator.screen.main.UserCardLayout
import nl.dionsegijn.konfetti.compose.KonfettiView
import nl.dionsegijn.konfetti.compose.OnParticleSystemUpdateListener
import nl.dionsegijn.konfetti.core.Party
import nl.dionsegijn.konfetti.core.PartySystem
import nl.dionsegijn.konfetti.core.Position
import nl.dionsegijn.konfetti.core.emitter.Emitter
import org.koin.androidx.compose.koinViewModel
import java.util.concurrent.TimeUnit

@Composable
fun RandomizerScreen(
    navController: NavController,
    viewModel: RandomizerViewModel = koinViewModel()
) {
    val uiState = viewModel.state.collectAsStateWithLifecycle().value

    Scaffold(
        topBar = {
            TopAppBar(
                navigationIcon = {
                    if (uiState.userPositionList.isNotEmpty()) {
                        IconButton(onClick = { navController.popBackStack() }) {
                            Icon(Icons.Filled.ArrowBack, "")
                        }
                    }
                },
                title = { Text("Tadaaa") },

                )
        }
    ) { innerPadding ->

        ParticipantsPositionLayout(
            userPositionList = uiState.userPositionList,
            setSelectedForUser = { userName, isSelected ->
                viewModel.setUserSelected(
                    userName,
                    isSelected
                )
            },
            randomize = { viewModel.randomizeList() },
            modifier = Modifier.padding(innerPadding),
        )
    }

}

@Composable
fun ParticipantsPositionLayout(
    userPositionList: List<UserPosition>,
    setSelectedForUser: (userName: String, isSelected: Boolean) -> Unit,
    randomize: OnClickHandler,
    modifier: Modifier = Modifier
) {
    val konfettiAnimation = remember { mutableStateOf(false) }

    ConstraintLayout(
        modifier = Modifier
            .fillMaxSize()
            .then(modifier)
    ) {
        val ( list, validateButton, konfetti, konfettiButton) = createRefs()


        val party = remember {
            Party(
                speed = 0f,
                maxSpeed = 30f,
                damping = 0.9f,
                spread = 360,
                colors = listOf(0xfce18a, 0xff726d, 0xf4306d, 0xb48def),
                position = Position.Relative(0.5, 0.3),
                emitter = Emitter(duration = 100, TimeUnit.MILLISECONDS).max(100)
            )
        }





        LazyColumn(
            modifier = Modifier.constrainAs(list) {
                top.linkTo(parent.top, 16.dp)
                start.linkTo(parent.start)
                end.linkTo(parent.end)
                bottom.linkTo(validateButton.top)
                height = Dimension.fillToConstraints
                width = Dimension.fillToConstraints
            }) {
            items(userPositionList.size,
                key = { userPositionList[it].user.name }) {
                UserPositionCardLayout(
                    user = userPositionList[it],
                    setSelectedForUser,
                    modifier = Modifier.animateItemPlacement()
                )
            }
        }

        Button(onClick = {
            randomize()
        }, modifier = Modifier.constrainAs(validateButton) {
            top.linkTo(list.bottom)
            bottom.linkTo(parent.bottom)
            start.linkTo(parent.start)
            end.linkTo(parent.end)
        }) {
            Text(text = "Randomize")
        }

        if (konfettiAnimation.value) {
            KonfettiView(
                modifier = Modifier
                    .fillMaxSize()
                    .constrainAs(konfetti) {
                        top.linkTo(parent.top)
                        start.linkTo(parent.start)
                        end.linkTo(parent.end)
                    },
                parties = listOf(party),
                updateListener = object : OnParticleSystemUpdateListener {
                    override fun onParticleSystemEnded(system: PartySystem, activeSystems: Int) {
                        konfettiAnimation.value = false
                    }
                }
            )
        }

        IconButton(onClick = { konfettiAnimation.value = true }, modifier = Modifier.constrainAs(konfettiButton){
            bottom.linkTo(parent.bottom)
            end.linkTo(parent.end)
        }) {
            Icon(imageVector = ImageVector.vectorResource(id = R.drawable.ic_confetti), contentDescription = "", modifier = Modifier.size(48.dp))
        }

    }

}

@Composable
fun UserPositionCardLayout(
    user: UserPosition,
    setSelectedForUser: (userName: String, isSelected: Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        Modifier
            .fillMaxWidth()
            .then(modifier), verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = user.position.toString().uppercase(),
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(8.dp)
        )
        UserCardLayout(currentUser = user.user, setSelectedForUser = setSelectedForUser)
    }

}