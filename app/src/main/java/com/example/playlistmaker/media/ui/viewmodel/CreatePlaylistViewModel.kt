package com.example.playlistmaker.media.ui.viewmodel

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.media.domain.ImageSaver
import com.example.playlistmaker.media.domain.PlaylistInteractor
import com.example.playlistmaker.models.Playlist
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class CreatePlaylistViewModel(
    private val imageSaver: ImageSaver,
    private val savePlaylistInteractor: PlaylistInteractor,
    private val playlistId: Int?

) : ViewModel() {

    private val _coverImagePath = MutableLiveData<String?>()
    val coverImagePath: LiveData<String?> = _coverImagePath

    private val _name = MutableLiveData<String>()
    val name: LiveData<String> = _name

    private val _description = MutableLiveData<String>()
    val description: LiveData<String> = _description

    private val _isCreateButtonEnabled = MutableLiveData(false)
    val isCreateButtonEnabled: LiveData<Boolean> = _isCreateButtonEnabled

    private val _existingName = MutableLiveData<String?>()
    val existingName: LiveData<String?> = _existingName

    private val _existingDescription = MutableLiveData<String?>()
    val existingDescription: LiveData<String?> = _existingDescription

    private val _playlistSaved = MutableLiveData<Boolean>()
    val playlistSaved: LiveData<Boolean> = _playlistSaved

    val isEditMode: Boolean = playlistId != -1

    private var existingPlaylist: Playlist? = null
    private val _existingCoverImagePath = MutableLiveData<String?>()

    val existingCoverImagePath: LiveData<String?> = _existingCoverImagePath

    init {
        if (isEditMode) {
            viewModelScope.launch {
                savePlaylistInteractor.getPlaylistById(playlistId!!).collect { pl ->
                    existingPlaylist = pl
                    _name.value = pl.name
                    _description.value = pl.description.orEmpty()
                    _coverImagePath.value = pl.imagePath
                    _isCreateButtonEnabled.value = pl.name.isNotBlank()
                }
                _existingName.postValue(_name.value)
                _existingDescription.postValue(_description.value)
                _existingCoverImagePath.postValue(_coverImagePath.value)

            }
        }
    }

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
        if (playlistId == -1) {
            return !name.value.isNullOrBlank() ||
                    !description.value.isNullOrBlank() ||
                    !coverImagePath.value.isNullOrEmpty()
        } else {
            return name.value != existingName.value ||
                    description.value != existingDescription.value ||
                    coverImagePath.value != existingCoverImagePath.value
        }
    }

    fun savePlaylist() {
        viewModelScope.launch(Dispatchers.IO) {
            val name = _name.value.orEmpty()
            val desc = _description.value.orEmpty()
            val img = if (_coverImagePath.value.isNullOrBlank()) null else _coverImagePath.value
            if (isEditMode) {
                val old = existingPlaylist
                val trackIds = old?.trackIds ?: emptyList()
                val trackCount = old?.trackCount ?: trackIds.size

                savePlaylistInteractor.updatePlaylist(
                    Playlist(
                        id = playlistId!!,
                        name = name,
                        description = desc,
                        imagePath = img,
                        trackIds = trackIds,
                        trackCount = trackCount
                    )
                )
                _playlistSaved.postValue(true)
            } else {
                savePlaylistInteractor.createPlaylist(name, desc.ifBlank { null }, img)

                _playlistSaved.postValue(true)
            }
        }
    }
}