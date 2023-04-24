package dev.cisnux.mystory.ui.widgets

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.Intent
import android.os.Build
import android.widget.RemoteViews
import androidx.core.net.toUri
import dev.cisnux.mystory.R
import dev.cisnux.mystory.ui.adapters.StackWidgetService

class LatestStoryWidget : AppWidgetProvider() {
    override fun onReceive(context: Context, intent: Intent) {
        super.onReceive(context, intent)
    }
    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        for (appWidgetId in appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId)
        }
    }

    override fun onEnabled(context: Context) {
    }

    override fun onDisabled(context: Context) {
    }

    companion object {
        private const val TOAST_ACTION = "dev.cisnux.MyStory.TOAST_ACTION"
        const val EXTRA_ITEM = "dev.cisnux.MyStory.EXTRA_ITEM"

        private fun updateAppWidget(
            context: Context,
            appWidgetManager: AppWidgetManager,
            appWidgetId: Int
        ) {
            val intent = Intent(context, StackWidgetService::class.java).apply {
                putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId)
                data = toUri(Intent.URI_INTENT_SCHEME).toUri()
            }

            val views = RemoteViews(context.packageName, R.layout.latest_story_widget).apply {
                setRemoteAdapter(R.id.stackView, intent)
                setEmptyView(R.id.stackView, R.id.emptyView)
            }

            val toastIntent = Intent(context, LatestStoryWidget::class.java).apply {
                action = TOAST_ACTION
                putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId)
            }

            val toastPendingIntent = PendingIntent.getBroadcast(
                context,
                0,
                toastIntent,
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                    PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_MUTABLE
                } else {
                    0
                }
            )
            views.setPendingIntentTemplate(R.id.stackView, toastPendingIntent)
            appWidgetManager.updateAppWidget(appWidgetId, views)
        }
    }
}