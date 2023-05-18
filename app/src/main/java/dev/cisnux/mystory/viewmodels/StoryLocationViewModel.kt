package dev.cisnux.mystory.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.cisnux.mystory.domain.StoryMap
import dev.cisnux.mystory.domain.StoryRepository
import javax.inject.Inject

@HiltViewModel
class StoryLocationViewModel @Inject constructor(repository: StoryRepository) :
    ViewModel() {
    val storyMaps: LiveData<List<StoryMap>> = repository.getStoriesForMap().asLiveData()
}