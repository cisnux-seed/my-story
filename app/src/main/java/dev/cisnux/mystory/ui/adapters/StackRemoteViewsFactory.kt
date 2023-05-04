package dev.cisnux.mystory.ui.adapters

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.util.Log
import android.widget.RemoteViews
import android.widget.RemoteViewsService
import androidx.core.os.bundleOf
import coil.imageLoader
import coil.request.CachePolicy
import coil.request.ImageRequest
import coil.request.SuccessResult
import dev.cisnux.mystory.R
import dev.cisnux.mystory.domain.StoryRepository
import dev.cisnux.mystory.ui.widgets.LatestStoryWidget
import kotlinx.coroutines.runBlocking

internal class StackRemoteViewsFactory(
    private val context: Context,
    private val repository: StoryRepository
) : RemoteViewsService.RemoteViewsFactory {
    private val stories = ArrayList<Bitmap>()

    override fun onCreate() {

    }

    override fun onDataSetChanged(): Unit =
        runBlocking {
            val latestStories = repository.getStoriesForWidget()
            Log.d(StackRemoteViewsFactory::class.simpleName, latestStories.toString())
            if (stories.size > 0)
                stories.clear()
            stories.addAll(latestStories.map { story ->
                val request = ImageRequest.Builder(context)
                    .crossfade(true)
                    .networkCachePolicy(CachePolicy.ENABLED)
                    .diskCachePolicy(CachePolicy.ENABLED)
                    .memoryCachePolicy(CachePolicy.ENABLED)
                    .data(story.photoUrl)
                    .allowHardware(false)
                    .build()
                val result =
                    (context.imageLoader.execute(request) as SuccessResult).drawable
                (result as BitmapDrawable).bitmap
            })
        }

    override fun onDestroy() {
    }

    override fun getCount(): Int = stories.size

    override fun getViewAt(position: Int): RemoteViews {
        val rv = RemoteViews(context.packageName, R.layout.widget_item)
        rv.setImageViewBitmap(R.id.widgetStoryPicture, stories[position])

        val extras = bundleOf(
            LatestStoryWidget.EXTRA_ITEM to position
        )
        val fillInIntent = Intent().apply {
            putExtras(extras)
        }

        rv.setOnClickFillInIntent(R.id.widgetStoryPicture, fillInIntent)
        return rv
    }

    override fun getLoadingView(): RemoteViews? = null

    override fun getViewTypeCount(): Int = 1

    override fun getItemId(position: Int): Long = 0

    override fun hasStableIds(): Boolean = false
}


