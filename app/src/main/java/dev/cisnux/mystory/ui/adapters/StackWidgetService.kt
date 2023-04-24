package dev.cisnux.mystory.ui.adapters

import android.content.Intent
import android.widget.RemoteViewsService
import dagger.hilt.android.AndroidEntryPoint
import dev.cisnux.mystory.domain.StoryRepository
import javax.inject.Inject

@AndroidEntryPoint
class StackWidgetService : RemoteViewsService() {
    @Inject
    lateinit var repository: StoryRepository

    override fun onGetViewFactory(intent: Intent?): RemoteViewsFactory =
        StackRemoteViewsFactory(applicationContext, repository)
}