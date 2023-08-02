@file:OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class)

package com.stormbirdmedia.dailygenerator.screen.randomizer

import android.app.Activity
import android.content.Intent
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Card
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.modifier.modifierLocalConsumer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieClipSpec
import com.airbnb.lottie.compose.LottieCompositionResult
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition
import com.stormbirdmedia.dailygenerator.R
import com.stormbirdmedia.dailygenerator.domain.models.User
import com.stormbirdmedia.dailygenerator.screen.main.UserCardLayout
import com.stormbirdmedia.dailygenerator.ui.composable.AutoSizeText
import com.stormbirdmedia.dailygenerator.ui.theme.md_theme_light_primary
import com.stormbirdmedia.dailygenerator.utils.BitmapUtils
import dev.shreyaspatil.capturable.Capturable
import dev.shreyaspatil.capturable.controller.rememberCaptureController
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import nl.dionsegijn.konfetti.compose.KonfettiView
import nl.dionsegijn.konfetti.compose.OnParticleSystemUpdateListener
import nl.dionsegijn.konfetti.core.Party
import nl.dionsegijn.konfetti.core.PartySystem
import nl.dionsegijn.konfetti.core.Position
import nl.dionsegijn.konfetti.core.emitter.Emitter
import org.koin.androidx.compose.koinViewModel
import timber.log.Timber
import java.util.concurrent.TimeUnit


@Composable
fun RandomizerScreen(
    navController: NavController,
    viewModel: RandomizerViewModel = koinViewModel()
) {
    val uiState = viewModel.state.collectAsStateWithLifecycle().value
    val captureController = rememberCaptureController()
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val launcher =
        rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                Timber.d("Share success")
                viewModel.startKonfetti()
            } else {
                if (result.resultCode == Activity.RESULT_CANCELED) {
                    Timber.d("Share cancelled")
                    viewModel.deleteScreenShot()
                } else {
                    Timber.d("Share failed")
                }
            }
        }
    ConstraintLayout(modifier = Modifier.fillMaxSize()) {
        val (mainContent, buttons, konfetti) = createRefs()
        Capturable(
            controller = captureController,
            modifier = Modifier
                .fillMaxSize()
                .constrainAs(mainContent) {
                    top.linkTo(parent.top)
                    bottom.linkTo(parent.bottom)
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
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
            ParticipantsPositionLayout(
                userPositionList = uiState.userPositionList,
                setUserSelected = { userName, isSelected ->
                    viewModel.setUserSelected(
                        userName,
                        isSelected
                    )
                },
                modifier = Modifier.fillMaxSize()
                )
        }

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

        if (uiState.konfettiState == RandomizerViewModel.KonfettiState.Start) {
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
                    override fun onParticleSystemEnded(
                        system: PartySystem,
                        activeSystems: Int
                    ) {
                        viewModel.stopKonfetti()
                    }
                }
            )
        }


        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .clip(CircleShape)
                .background(md_theme_light_primary)
                .constrainAs(buttons) {
                    bottom.linkTo(parent.bottom, 16.dp)
                    end.linkTo(parent.end, 16.dp)
                }) {
            val composition: LottieCompositionResult =
                rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.roll_dice))
            val isPlaying = remember { mutableStateOf(true) }
            val clipSpec = remember { mutableStateOf(LottieClipSpec.Progress(0.99f, 1f)) }
            val progress = animateLottieCompositionAsState(
                composition.value,
                isPlaying = isPlaying.value,
                clipSpec = clipSpec.value,
                restartOnPlay = true,
            )

            LaunchedEffect(progress.value) {
                scope.launch(Dispatchers.Main) {
                    if (progress.value >= 1f) {
                        clipSpec.value = LottieClipSpec.Progress(0f, 1f)
                        isPlaying.value = false
                    }
                }
            }

            LottieAnimation(
                composition = composition.value,
                progress = progress.value,
                modifier = Modifier
                    .size(56.dp)
                    .clip(CircleShape)
                    .clickable {
                        isPlaying.value = true
                        viewModel.randomizeList()
                    }
            )

            IconButton(
                onClick = {
                    captureController.capture()
                },
                modifier = Modifier.padding(16.dp)
            ) {
                Icon(
                    imageVector = ImageVector.vectorResource(id = R.drawable.ic_share),
                    contentDescription = "",
                    modifier = Modifier
                        .size(56.dp)
                        .padding(8.dp),
                    tint = Color.White
                )
            }
        }
    }


}

@Composable
fun ParticipantsPositionLayout(
    userPositionList: List<UserPosition>,
    setUserSelected: (userName: String, isSelected: Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    if (userPositionList.size <= 11) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White)
                .then(modifier)
        ) {
            items(userPositionList.size,
                key = { userPositionList[it].user.name }) {
                UserPositionCardLayout(
                    user = userPositionList[it],
                    setUserSelected,
                    modifier = Modifier.animateItemPlacement()
                )
            }
        }
    } else {
        LazyVerticalGrid(
            columns = GridCells.Fixed(2), modifier = Modifier
                .fillMaxSize()
        ) {
            items(userPositionList.size,
                key = { userPositionList[it].user.name }) {
                UserGridCardLayout(
                    currentUser = userPositionList[it],
                    setUserSelected,
                    modifier = Modifier.animateItemPlacement()
                )
            }
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
        UserCardLayout(user.position, currentUser = user.user, setSelectedForUser = setSelectedForUser)
    }

}


@Composable
fun UserGridCardLayout(
    currentUser: UserPosition,
    setSelectedForUser: (userName: String, isSelected: Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    val user = currentUser.user
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .clickable {
                setSelectedForUser(user.name, !user.isSelected)
            }
            .then(modifier),
    ) {
        ConstraintLayout(modifier = Modifier.fillMaxWidth()) {
            val (position, name, checkbox) = createRefs()

            Text(
                text = currentUser.position.toString().uppercase(),
                style = MaterialTheme.typography.headlineMedium,
                modifier = Modifier
                    .padding(8.dp)
                    .constrainAs(position) {
                        top.linkTo(parent.top)
                        start.linkTo(parent.start)
                        end.linkTo(parent.end)
                    }
            )

            AutoSizeText(
                text = user.name.uppercase(),
                textAlign = TextAlign.Center,

                modifier = Modifier
                    .constrainAs(name) {
                        top.linkTo(position.bottom)
                        start.linkTo(parent.start, 8.dp)
                        end.linkTo(parent.end, 8.dp)
                        bottom.linkTo(parent.bottom, 8.dp)
                        width = Dimension.fillToConstraints
                    }
            )
            Checkbox(
                checked = user.isSelected,
                onCheckedChange = { isSelected ->
                    setSelectedForUser(user.name, isSelected)
                },
                modifier = Modifier
                    .constrainAs(checkbox) {
                        top.linkTo(parent.top)
                        end.linkTo(parent.end)
                    }
            )

        }
    }
}

@Preview
@Composable
fun UserPositionCardLayoutPreview() {
    val user = UserPosition(
        User(
            name = "John Doe",
            isSelected = false
        ),
        1
    )
    UserPositionCardLayout(user = user, setSelectedForUser = { _, _ -> })
}


@Preview
@Composable
fun UserGridCardLayoutPreview() {
    val user = UserPosition(
        User(
            name = "John Doe",
            isSelected = false
        ),
        1
    )
    UserGridCardLayout(currentUser = user, setSelectedForUser = { _, _ -> })
}

