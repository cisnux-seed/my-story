package dev.cisnux.mystory.ui

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import dev.cisnux.mystory.R
import dev.cisnux.mystory.databinding.FragmentPostStoryBinding
import dev.cisnux.mystory.utils.ApplicationState
import dev.cisnux.mystory.utils.Failure
import dev.cisnux.mystory.viewmodels.HomeViewModel
import dev.cisnux.mystory.viewmodels.PostStoryViewModel
import kotlinx.coroutines.launch
import java.io.File

@AndroidEntryPoint
class PostStoryFragment : Fragment() {
    private var _binding: FragmentPostStoryBinding? = null
    private val binding get() = _binding!!
    private val viewModel: PostStoryViewModel by viewModels()
    private val homeViewModel: HomeViewModel by activityViewModels()
    private var getFile: File? = null
    private lateinit var currentPhotoPath: String
    private val launcherIntentCamera = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) {
        if (it.resultCode == AppCompatActivity.RESULT_OK) {
            val myFile = File(currentPhotoPath)
            myFile.let { file ->
                getFile = file
                binding.rotate.visibility = View.VISIBLE
                binding.storyPicture.setImageBitmap(BitmapFactory.decodeFile(file.path))
            }
        }
    }
    private val launcherIntentGallery = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == AppCompatActivity.RESULT_OK) {
            val selectedImg = result.data?.data as Uri
            selectedImg.let { uri ->
                lifecycleScope.launch {
                    val myFile = viewModel.convertUriPhotoToFilePhoto(uri)
                    getFile = myFile
                }
                binding.rotate.visibility = View.VISIBLE
                binding.storyPicture.setImageURI(uri)
            }
        }
    }
    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) {
            if (it)
                startTakePhoto()
            else
                Snackbar.make(
                    binding.root,
                    getString(R.string.camera_access_explanation),
                    Snackbar.LENGTH_LONG
                ).show()

        }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPostStoryBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupPostStoryView()
        binding.rotate.setOnClickListener {
            getFile?.let {
                lifecycleScope.launch {
                    viewModel.rotatePhoto(it)
                    binding.storyPicture.setImageBitmap(BitmapFactory.decodeFile(it.path))
                }
            }
        }
        viewModel.postStoryState.observe(viewLifecycleOwner, ::subscribeUploadProgress)
    }

    private fun setupPostStoryView() = with(binding) {
        materialToolbar.setNavigationOnClickListener {
            findNavController().navigateUp()
        }
        cameraButton.setOnClickListener {
            if (ContextCompat.checkSelfPermission(
                    requireContext(),
                    Manifest.permission.CAMERA
                ) == PackageManager.PERMISSION_GRANTED
            )
                startTakePhoto()
            else
                requestPermissionLauncher.launch(Manifest.permission.CAMERA)
        }
        galleryButton.setOnClickListener {
            startTakePhotoFromGallery()
        }
        uploadButton.setOnClickListener {
            getFile?.let {
                if (postEditText.text.toString().isNotEmpty()) {
                    viewModel.postStory(it, postEditText.text.toString())
                }
            }
        }
    }

    private fun subscribeUploadProgress(applicationState: ApplicationState<Nothing>) =
        with(binding) {
            when (applicationState) {
                is ApplicationState.Success -> {
                    progressBar.visibility = View.GONE
                    uploadButton.text = getString(R.string.upload)
                    homeViewModel.refreshStories()
                    findNavController().navigateUp()
                }

                is ApplicationState.Failed -> {
                    progressBar.visibility = View.GONE
                    uploadButton.text = getString(R.string.upload)
                    if (applicationState.failure is Failure.ConnectionFailure) {
                        applicationState.failure.message =
                            getString(R.string.no_internet_connection)
                    }
                    val errorMessage = applicationState.failure.message
                    errorMessage?.let {
                        Snackbar.make(binding.root, it, Snackbar.LENGTH_SHORT)
                            .show()
                    }
                }

                else -> {
                    progressBar.visibility = View.VISIBLE
                    uploadButton.text = null
                }
            }
        }

    private fun startTakePhoto() = lifecycleScope.launch {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        intent.resolveActivity(requireActivity().packageManager)

        viewModel.getPhotoFile().also {
            val photoURI = FileProvider.getUriForFile(
                requireActivity(),
                requireActivity().packageName,
                it
            )
            currentPhotoPath = it.absolutePath
            intent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
            launcherIntentCamera.launch(intent)
        }
    }

    private fun startTakePhotoFromGallery() {
        val intent = Intent()
        intent.action = Intent.ACTION_GET_CONTENT
        intent.type = "image/*"
        val chooser = Intent.createChooser(intent, "Choose a Picture")
        launcherIntentGallery.launch(chooser)
    }
}

