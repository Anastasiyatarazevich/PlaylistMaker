package com.example.playlistmaker.media.ui.fragments

import android.Manifest
import android.app.AlertDialog
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.FragmentCreatePlaylistBinding
import com.example.playlistmaker.main.MainActivity
import com.example.playlistmaker.media.ui.viewmodel.CreatePlaylistViewModel
import com.example.playlistmaker.search.ui.utils.ViewUtils
import org.koin.androidx.viewmodel.ext.android.getViewModel
import org.koin.core.parameter.parametersOf

class CreatePlaylistFragment : Fragment() {
    private lateinit var binding: FragmentCreatePlaylistBinding
    private lateinit var viewModel: CreatePlaylistViewModel
    private val playlistId: Int by lazy { arguments?.getInt(PLAYLIST_ID) ?: -1 }
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
            Toast.makeText(requireContext(), R.string.no_permission, Toast.LENGTH_SHORT)
                .show()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentCreatePlaylistBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = getViewModel { parametersOf(playlistId) }

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

        viewModel.isCreateButtonEnabled.observe(viewLifecycleOwner) { isEnabled ->
            binding.createPlaylist.isEnabled = isEnabled
        }

        viewModel.existingName.observe(viewLifecycleOwner) { name ->
            binding.playlistsName.setText(name)

        }
        viewModel.existingDescription.observe(viewLifecycleOwner) { desc ->
            binding.playlistDescription.setText(desc)
        }


        viewModel.playlistSaved.observe(viewLifecycleOwner) {
            val action = if (playlistId == -1) "создан" else "сохранён"
            Toast.makeText(
                requireContext(),
                "Плейлист «${viewModel.name.value}» $action", Toast.LENGTH_SHORT
            )
                .show()
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }

        binding.playlistsName.addTextChangedListener {
            viewModel.updateName(it.toString())
        }
        binding.playlistDescription.addTextChangedListener {
            viewModel.updateDescription(it.toString())
        }

        binding.createPlaylist.setOnClickListener {
            viewModel.savePlaylist()
        }

        binding.playlistsName.setText(viewModel.existingName.value)
        binding.playlistsName.setText(viewModel.description.value)


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

    private fun updateCreateButtonState() {
        val nameNotEmpty = viewModel.name.value?.isNotBlank() == true
        binding.createPlaylist.isEnabled = nameNotEmpty
    }

    private fun checkBeforeExit() {
        if (viewModel.hasUnsavedChanges()) {
            AlertDialog.Builder(requireContext())
                .setTitle("Завершить ${if (playlistId == -1) "создание" else "редактирование"}?")
                .setMessage(R.string.data_loss)
                .setNegativeButton(R.string.cancel, null)
                .setPositiveButton(R.string.complete) { _, _ ->
                    requireActivity().onBackPressedDispatcher.onBackPressed()
                }
                .show()
        } else {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }
    }

    override fun onResume() {
        super.onResume()
        (activity as? MainActivity)?.showBottomNav(false)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        (activity as? MainActivity)?.showBottomNav(true)
    }

    companion object {
        private const val PLAYLIST_ID = "playlistId"
    }
}