package com.example.playlistmaker.media.ui.fragments

import android.app.AlertDialog
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.widget.addTextChangedListener
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.playlistmaker.databinding.FragmentCreatePlaylistBinding
import com.example.playlistmaker.main.MainActivity
import com.example.playlistmaker.media.ui.viewmodel.CreatePlaylistViewModel
import com.example.playlistmaker.search.ui.utils.ViewUtils
import org.koin.androidx.viewmodel.ext.android.viewModel
import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.content.ContextCompat

class CreatePlaylistFragment : Fragment() {
    private lateinit var binding: FragmentCreatePlaylistBinding
    private val viewModel: CreatePlaylistViewModel by viewModel()

    private val pickMedia = registerForActivityResult(
        ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        if (uri != null) {
            viewModel.onImageSelected(uri)
        }
    }

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            launchPhotoPicker()
        } else {
            Toast.makeText(requireContext(), "Разрешение не предоставлено:(", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentCreatePlaylistBinding.inflate(inflater, container, false)
        return binding.root    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        (activity as? MainActivity)?.showBottomNav(false)

        binding.backButton.setNavigationOnClickListener {

            checkBeforeExit()
        }

        binding.playlistImage.setOnClickListener {
            checkAndRequestPermission()
        }

        binding.playlistsName.addTextChangedListener {
            viewModel.updateName(it.toString())
        }

        binding.playlistDescription.addTextChangedListener {
            viewModel.updateDescription(it.toString())
        }

        viewModel.coverImagePath.observe(viewLifecycleOwner) { path ->
            if (path != null) {
                Glide.with(this)
                    .load(Uri.parse(path))
                    .transform(RoundedCorners(ViewUtils.dpToPx(8f, requireContext())))
                    .into(binding.playlistImage)
            }
        }

        viewModel.playlistCreated.observe(viewLifecycleOwner) {
            it?.let {
                showToast("Плейлист «${viewModel.name.value}» создан")
                requireActivity().onBackPressedDispatcher.onBackPressed()
            }
        }

        viewModel.isCreateButtonEnabled.observe(viewLifecycleOwner) { isEnabled ->
            binding.createPlaylist.isEnabled = isEnabled
        }

        binding.createPlaylist.setOnClickListener {
            viewModel.createPlaylist()
        }

        updateCreateButtonState()
    }

    private fun launchPhotoPicker() {
        pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
    }

    private fun checkAndRequestPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            launchPhotoPicker()
        } else {
            when {
                ContextCompat.checkSelfPermission(
                    requireContext(),
                    Manifest.permission.READ_EXTERNAL_STORAGE
                ) == PackageManager.PERMISSION_GRANTED -> {
                    launchPhotoPicker()
                }
                else -> {
                    requestPermissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
                }
            }
        }
    }

    private fun showToast(text: String) {
        Toast.makeText(requireContext(), text, Toast.LENGTH_SHORT).show()
    }

    private fun updateCreateButtonState() {
        val nameNotEmpty = viewModel.name.value?.isNotBlank() == true
        binding.createPlaylist.isEnabled = nameNotEmpty
    }

    private fun checkBeforeExit() {
        if (viewModel.hasUnsavedChanges()) {
            AlertDialog.Builder(requireContext())
                .setTitle("Завершить создание плейлиста?")
                .setMessage("Все несохраненные данные будут потеряны")
                .setNegativeButton("Отмена", null)
                .setPositiveButton("Завершить") { _, _ ->
                    requireActivity().onBackPressedDispatcher.onBackPressed()
                }
                .show()
        } else {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        (activity as? MainActivity)?.showBottomNav(true)
    }
}