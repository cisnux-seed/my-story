package dev.cisnux.mystory.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.paging.PagingData
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.cisnux.mystory.domain.Story
import dev.cisnux.mystory.domain.StoryRepository
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val repository: StoryRepository,
) : ViewModel() {
    val stories: LiveData<PagingData<Story>> get() = repository.getStories().asLiveData()
}