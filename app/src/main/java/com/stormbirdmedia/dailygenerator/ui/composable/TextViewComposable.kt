package com.stormbirdmedia.dailygenerator.ui.composable

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow

@Composable
fun AutoSizeText(text: String, modifier: Modifier = Modifier, textAlign: TextAlign = TextAlign.Start) {
    val textStyleBody1 = MaterialTheme.typography.labelLarge

    var textStyle = remember { mutableStateOf(textStyleBody1) }
    var readyToDraw = remember { mutableStateOf(false) }

    Text(
        text = text,
        style = textStyle.value,
        maxLines = 1,
        textAlign = textAlign ,
        overflow = TextOverflow.Clip,
        modifier = modifier.drawWithContent {
            if (readyToDraw.value) drawContent()
        },
        onTextLayout = { textLayoutResult: TextLayoutResult ->
            if (textLayoutResult.didOverflowHeight) {
                textStyle.value = textStyle.value.copy(fontSize = textStyle.value.fontSize * 0.9)
            } else {
                readyToDraw.value = true
            }
        }
    )
}
