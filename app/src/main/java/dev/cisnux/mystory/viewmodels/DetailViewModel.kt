package dev.cisnux.mystory.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.cisnux.mystory.domain.DetailStory
import dev.cisnux.mystory.domain.StoryRepository
import dev.cisnux.mystory.utils.ApplicationState
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DetailViewModel @Inject constructor(private val repository: StoryRepository) : ViewModel() {
    private val _detailStory = MutableLiveData<ApplicationState<DetailStory>>()
    val detailStory: LiveData<ApplicationState<DetailStory>> get() = _detailStory

    fun getDetailStory(id: String) = viewModelScope.launch {
        repository.getDetailStory(id).collect {
            _detailStory.value = it
        }
    }
}