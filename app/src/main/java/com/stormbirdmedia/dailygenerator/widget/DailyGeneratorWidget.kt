package com.stormbirdmedia.dailygenerator.widget

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import androidx.glance.Button
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.GlanceTheme
import androidx.glance.action.actionStartActivity
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.provideContent
import androidx.glance.background
import androidx.glance.layout.Alignment
import androidx.glance.layout.Column
import androidx.glance.layout.Row
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.padding
import androidx.glance.text.Text
import androidx.glance.text.TextStyle
import com.stormbirdmedia.dailygenerator.MainActivity

class DailyGeneratorWidget : GlanceAppWidget() {
    override suspend fun provideGlance(context: Context, id: GlanceId) {
        provideContent {
            MyContent()
        }
    }

    @Composable
    private fun MyContent() {
        Column(
            modifier = GlanceModifier.fillMaxSize().background(GlanceTheme.colors.background),
            verticalAlignment = Alignment.Top,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = "Vite un daily !", modifier = GlanceModifier.padding(12.dp), style = TextStyle(GlanceTheme.colors.onBackground) )
            Row(horizontalAlignment = Alignment.CenterHorizontally) {
                Button(
                    text = "Générer",
                    onClick = actionStartActivity<MainActivity>(),
                    modifier = GlanceModifier.fillMaxSize()
                )
            }
        }
    }
}