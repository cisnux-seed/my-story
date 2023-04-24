package dev.cisnux.mystory.viewmodels

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.cisnux.mystory.domain.StoryRepository
import dev.cisnux.mystory.utils.ApplicationState
import kotlinx.coroutines.launch
import java.io.File
import javax.inject.Inject

@HiltViewModel
class PostStoryViewModel @Inject constructor(private val repository: StoryRepository) :
    ViewModel() {
    private val _postStoryState = MutableLiveData<ApplicationState<Nothing>>()
    val postStoryState: LiveData<ApplicationState<Nothing>> get() = _postStoryState
    suspend fun getPhotoFile() = repository.getPhotoFile()

    suspend fun rotatePhoto(file: File) =
        repository.rotatePhoto(file)


    fun postStory(file: File, description: String) = viewModelScope.launch {
        repository.postStory(file, description).collect {
            _postStoryState.value = it
        }
    }

    suspend fun convertUriPhotoToFilePhoto(photoUri: Uri) = repository.convertUriToFile(photoUri)
}