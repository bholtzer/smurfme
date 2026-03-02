package com.bih.applicationsmurfforyou.presentation.smurfgallery

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.core.content.FileProvider
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.logEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.io.File
import javax.inject.Inject
import javax.inject.Named

@HiltViewModel
class SmurfGalleryViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    @Named("smurfGallery") private val smurfGalleryDir: File,
    private val analytics: FirebaseAnalytics
) : ViewModel() {

    private val _images = MutableStateFlow<List<String>>(emptyList())
    val images: StateFlow<List<String>> = _images.asStateFlow()

    private val _isGridLayout = MutableStateFlow(true)
    val isGridLayout: StateFlow<Boolean> = _isGridLayout.asStateFlow()

    private val _selectedImages = MutableStateFlow<Set<String>>(emptySet())
    val selectedImages: StateFlow<Set<String>> = _selectedImages.asStateFlow()

    private val _isSelectionMode = MutableStateFlow(false)
    val isSelectionMode: StateFlow<Boolean> = _isSelectionMode.asStateFlow()

    init {
        loadImages()
        logScreenView()
    }

    private fun logScreenView() {
        analytics.logEvent(FirebaseAnalytics.Event.SCREEN_VIEW) {
            param(FirebaseAnalytics.Param.SCREEN_NAME, "Smurf Gallery")
            param(FirebaseAnalytics.Param.SCREEN_CLASS, "SmurfGalleryViewModel")
        }
    }

    private fun loadImages() {
        viewModelScope.launch {
            val imageFiles = smurfGalleryDir.listFiles()
                ?.filter { it.isFile }
                ?.map { it.absolutePath }
                ?.sortedByDescending { File(it).lastModified() }
                ?: emptyList()
            _images.value = imageFiles
        }
    }

    fun toggleLayout() {
        _isGridLayout.update { !it }
        analytics.logEvent("gallery_toggle_layout") {
            param("layout_mode", if (_isGridLayout.value) "grid" else "list")
        }
    }

    fun toggleSelectionMode() {
        _isSelectionMode.update { !it }
        if (!_isSelectionMode.value) {
            _selectedImages.value = emptySet()
        }
        analytics.logEvent("gallery_toggle_selection") {
            param("enabled", _isSelectionMode.value.toString())
        }
    }

    fun toggleImageSelection(path: String) {
        _selectedImages.update { current ->
            if (current.contains(path)) current - path else current + path
        }
    }

    fun shareSelectedImages() {
        val selected = _selectedImages.value
        if (selected.isEmpty()) return

        analytics.logEvent("gallery_share_multiple") {
            param("count", selected.size.toLong())
        }

        val uris = ArrayList<Uri>()
        selected.forEach { path ->
            val file = File(path)
            uris.add(FileProvider.getUriForFile(context, "${context.packageName}.provider", file))
        }

        val intent = if (uris.size == 1) {
            Intent(Intent.ACTION_SEND).apply {
                type = "image/jpeg"
                putExtra(Intent.EXTRA_STREAM, uris[0])
            }
        } else {
            Intent(Intent.ACTION_SEND_MULTIPLE).apply {
                type = "image/jpeg"
                putParcelableArrayListExtra(Intent.EXTRA_STREAM, uris)
            }
        }
        
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        val chooser = Intent.createChooser(intent, "Share Smurf Images")
        chooser.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        context.startActivity(chooser)
        
        // Exit selection mode after sharing
        _isSelectionMode.value = false
        _selectedImages.value = emptySet()
    }

    fun shareImage(imagePath: String) {
        analytics.logEvent("gallery_share_single", null)
        val file = File(imagePath)
        val uri = FileProvider.getUriForFile(context, "${context.packageName}.provider", file)
        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "image/jpeg"
            putExtra(Intent.EXTRA_STREAM, uri)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
        val chooser = Intent.createChooser(intent, "Share Smurf Image")
        chooser.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        context.startActivity(chooser)
    }
    
    fun deleteImage(imagePath: String) {
        val file = File(imagePath)
        if (file.exists() && file.delete()) {
            analytics.logEvent("gallery_delete_image", null)
            loadImages()
        }
    }
}
