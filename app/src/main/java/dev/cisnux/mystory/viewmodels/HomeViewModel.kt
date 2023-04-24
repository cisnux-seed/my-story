package dev.cisnux.mystory.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.cisnux.mystory.domain.Story
import dev.cisnux.mystory.domain.StoryRepository
import dev.cisnux.mystory.utils.ApplicationState
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val repository: StoryRepository,
) : ViewModel() {
    private val _storyStates = MutableLiveData<ApplicationState<List<Story>>>()
    val storyStates: LiveData<ApplicationState<List<Story>>> get() = _storyStates

    fun refreshStories() = viewModelScope.launch {
        repository.getStories().collect {
            _storyStates.value = it
        }
    }
}