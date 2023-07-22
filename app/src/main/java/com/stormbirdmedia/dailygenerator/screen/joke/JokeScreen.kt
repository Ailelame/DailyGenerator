package com.stormbirdmedia.dailygenerator.screen.joke

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import com.airbnb.lottie.compose.*
import com.stormbirdmedia.dailygenerator.R
import com.stormbirdmedia.dailygenerator.domain.models.Joke
import com.stormbirdmedia.dailygenerator.ui.theme.md_theme_light_surfaceVariant
import org.koin.androidx.compose.koinViewModel


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun JokeScreen(
    navController: NavHostController,
    viewModel: JokeViewModel = koinViewModel()
) {

    val uiState = viewModel.state.collectAsStateWithLifecycle().value

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    val noPrefixList = listOf("global", "dark", "limit")
                    Text("La Blague ${if (noPrefixList.none { it == uiState.joke.type }) "de" else ""} ${uiState.joke.type}")
                },
                navigationIcon = {
                    IconButton(onClick = {
                        navController.navigateUp()
                    }) {
                        Icon(Icons.Rounded.ArrowBack, "")
                    }
                }
            )
        }
    ) { innerPadding ->
        JokeLayout(
            onRandomJokeClicked = { viewModel.onRandomJokeClicked() },
            joke = uiState.joke,
            modifier = Modifier.padding(innerPadding)
        )
    }

}

@Composable
fun JokeLayout(
    onRandomJokeClicked: () -> Unit,
    joke: Joke,
    modifier: Modifier = Modifier
) {

    ConstraintLayout(
        modifier = Modifier
            .then(modifier)
            .fillMaxSize()
    ) {
        val (jokeText, jokeResponse, category, randomJokeButton, lottie) = createRefs()
        val responseVisible = remember {
            mutableStateOf(false)
        }
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .heightIn(200.dp, 300.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(md_theme_light_surfaceVariant)
                .constrainAs(jokeText) {
                    top.linkTo(parent.top, margin = 16.dp)
                    start.linkTo(parent.start, margin = 16.dp)
                    end.linkTo(parent.end, margin = 16.dp)
                    width = Dimension.fillToConstraints
                }) {
            Text(
                text = joke.joke,
                style = MaterialTheme.typography.headlineMedium,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .padding(8.dp)
                    .animateContentSize()
            )
        }

        Text(
            text = "Catégorie : ${joke.type}",
            fontSize = 12.sp,
            modifier = Modifier
                .background(Color.Transparent)
                .constrainAs(category) {
                    bottom.linkTo(jokeText.bottom, margin = 4.dp)
                    start.linkTo(jokeText.start, margin = 8.dp)
                }

        )

        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .heightIn(150.dp, 200.dp)
                .clip(RoundedCornerShape(16.dp))
                .border(
                    BorderStroke(1.dp, md_theme_light_surfaceVariant),
                    RoundedCornerShape(16.dp)
                )
                .constrainAs(jokeResponse) {
                    top.linkTo(jokeText.bottom, margin = 16.dp)
                    start.linkTo(parent.start, margin = 16.dp)
                    end.linkTo(parent.end, margin = 16.dp)
                    width = Dimension.fillToConstraints
                }) {
            Text(
                text = joke.answer,
                style = MaterialTheme.typography.headlineSmall,
                textAlign = TextAlign.Center,
                color = if (responseVisible.value) Color.Black else Color.Transparent,
                modifier = Modifier
                    .padding(8.dp)
                    .animateContentSize()
            )
        }



        Button(
            onClick = {
                if (responseVisible.value) {
                    responseVisible.value = false
                    onRandomJokeClicked()
                } else responseVisible.value = true
            },
            modifier = Modifier
                .animateContentSize()
                .constrainAs(randomJokeButton) {
                    top.linkTo(jokeResponse.bottom, margin = 16.dp)
                    start.linkTo(parent.start, margin = 16.dp)
                    end.linkTo(parent.end, margin = 16.dp)
                }
        ) {
            if (responseVisible.value)
                Text("Une autre", style = MaterialTheme.typography.labelMedium)
            else
                Text("Voir la réponse", style = MaterialTheme.typography.labelMedium)
        }
        val composition: LottieCompositionResult =
            rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.cat))
        val progress = animateLottieCompositionAsState(
            composition.value,
            iterations = LottieConstants.IterateForever
        )
        LottieAnimation(
            modifier = Modifier.constrainAs(lottie) {
                top.linkTo(randomJokeButton.bottom, margin = 16.dp)
                start.linkTo(parent.start, margin = 16.dp)
                end.linkTo(parent.end, margin = 16.dp)
                bottom.linkTo(parent.bottom, margin = 16.dp)
            },
            composition = composition.value,
            progress = progress.value
        )


    }
}


@Preview
@Composable
fun JokeLayoutPreview() {
    JokeLayout(
        onRandomJokeClicked = {},
        joke = Joke(
            joke = "Joke", answer = "answer", id = 0, type = "type",

            )
    )
}
