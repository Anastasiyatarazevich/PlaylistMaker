package com.example.playlistmaker.media.ui.viewmodel

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.media.domain.ImageSaver
import com.example.playlistmaker.media.domain.PlaylistInteractor
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class CreatePlaylistViewModel(
    private val imageSaver: ImageSaver,
    private val savePlaylistInteractor: PlaylistInteractor

) : ViewModel() {

    private val _coverImagePath = MutableLiveData<String?>()
    val coverImagePath: LiveData<String?> = _coverImagePath

    private val _name = MutableLiveData<String>()
    val name: LiveData<String> = _name

    private val _description = MutableLiveData<String>()
    val description: LiveData<String> = _description

    private val _playlistCreated = MutableLiveData<Boolean>()
    val playlistCreated: LiveData<Boolean> = _playlistCreated

    private val _isCreateButtonEnabled = MutableLiveData(false)
    val isCreateButtonEnabled: LiveData<Boolean> = _isCreateButtonEnabled

    fun onImageSelected(uri: Uri) {
        viewModelScope.launch {
            val path = withContext(Dispatchers.IO) {
                imageSaver.saveImage(uri)
            }
            _coverImagePath.value = path
        }
    }

    fun updateName(newName: String) {
        _name.value = newName
        _isCreateButtonEnabled.value = !newName.isNullOrBlank()
    }

    fun updateDescription(newDescription: String) {
        _description.value = newDescription
    }

    fun hasUnsavedChanges(): Boolean {
        return !name.value.isNullOrBlank() ||
                !description.value.isNullOrBlank() ||
                !coverImagePath.value.isNullOrEmpty()
    }

    fun createPlaylist() {
        viewModelScope.launch(Dispatchers.IO) {
            val imagePathToSave = if (_coverImagePath.value.isNullOrBlank()) null else _coverImagePath.value

            savePlaylistInteractor.createPlaylist(
                name = name.value.orEmpty(),
                description = description.value.orEmpty(),
                imagePath = imagePathToSave
            )
            _playlistCreated.postValue(true)
        }
    }
}
