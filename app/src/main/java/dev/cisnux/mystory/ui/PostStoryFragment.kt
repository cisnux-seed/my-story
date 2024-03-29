package dev.cisnux.mystory.ui

import android.Manifest
import android.app.Activity.RESULT_CANCELED
import android.app.Activity.RESULT_OK
import android.content.Intent
import android.content.IntentSender
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.location.Location
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.LocationSettingsRequest
import com.google.android.gms.location.Priority
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import dev.cisnux.mystory.R
import dev.cisnux.mystory.databinding.FragmentPostStoryBinding
import dev.cisnux.mystory.utils.ApplicationState
import dev.cisnux.mystory.utils.Failure
import dev.cisnux.mystory.viewmodels.PostStoryViewModel
import kotlinx.coroutines.launch
import java.io.File
import java.util.concurrent.TimeUnit

@AndroidEntryPoint
class PostStoryFragment : Fragment() {
    private var _binding: FragmentPostStoryBinding? = null
    private val binding get() = _binding!!
    private val viewModel: PostStoryViewModel by viewModels()
    private var getFile: File? = null
    private lateinit var currentPhotoPath: String
    private lateinit var savedStateHandle: SavedStateHandle
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private var myLocation: Location? = null
    private val resolutionLauncher =
        registerForActivityResult(ActivityResultContracts.StartIntentSenderForResult()) { result ->
            when (result.resultCode) {
                RESULT_OK -> {
                    Log.i(TAG, "onActivityResult: All location settings are satisfied.")
                }

                RESULT_CANCELED -> {
                    Toast.makeText(
                        requireActivity(),
                        getString(R.string.turn_on_gps),
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    private val launcherIntentCamera = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) {
        if (it.resultCode == AppCompatActivity.RESULT_OK) {
            val myFile = File(currentPhotoPath)
            myFile.let { file ->
                getFile = file
                binding.rotateBtn.visibility = View.VISIBLE
                binding.storyPicture.setImageBitmap(BitmapFactory.decodeFile(file.path))
            }
        }
    }
    private val locationPermissionRequest = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        when {
            permissions[Manifest.permission.ACCESS_FINE_LOCATION] ?: false -> {
                shareMyLocation()
            }

            permissions[Manifest.permission.ACCESS_COARSE_LOCATION] ?: false -> {
                shareMyLocation()
            }

            else -> {
                binding.shareLocation.isChecked = false
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
                binding.rotateBtn.visibility = View.VISIBLE
                binding.storyPicture.setImageURI(uri)
            }
        }
    }
    private val cameraPermissionRequest =
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())
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
        savedStateHandle = findNavController().previousBackStackEntry!!.savedStateHandle
        savedStateHandle[ON_POST] = false
        setupPostStoryView()
        binding.rotateBtn.setOnClickListener {
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
        shareLocation.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked)
                shareMyLocation()
        }
        cameraButton.setOnClickListener {
            if (ContextCompat.checkSelfPermission(
                    requireContext(),
                    Manifest.permission.CAMERA
                ) == PackageManager.PERMISSION_GRANTED
            )
                startTakePhoto()
            else
                cameraPermissionRequest.launch(Manifest.permission.CAMERA)
        }
        galleryButton.setOnClickListener {
            startTakePhotoFromGallery()
        }
        uploadButton.setOnClickListener {
            getFile?.let {
                if (postEditText.text.toString().isNotEmpty()) {
                    viewModel.postStory(
                        it,
                        postEditText.text.toString(),
                        myLocation?.latitude,
                        myLocation?.longitude
                    )
                }
            }
        }
    }

    private fun shareMyLocation() {
        when {
            arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ).all { permission ->
                ContextCompat.checkSelfPermission(
                    requireActivity(), permission
                ) == PackageManager.PERMISSION_GRANTED
            } -> {
                val locationRequest = LocationRequest.Builder(
                    TimeUnit.SECONDS.toMillis(1)
                ).apply {
                    setPriority(Priority.PRIORITY_HIGH_ACCURACY)
                    setMaxUpdateDelayMillis(TimeUnit.SECONDS.toMillis(1))
                }.build()
                val locationSettingsBuilder = LocationSettingsRequest.Builder()
                    .addLocationRequest(locationRequest)
                val client = LocationServices.getSettingsClient(requireActivity())
                client.checkLocationSettings(locationSettingsBuilder.build())
                    .addOnSuccessListener {
                        fusedLocationClient.lastLocation.addOnSuccessListener { location ->
                            binding.shareLocation.isChecked = true
                            location?.let {
                                myLocation = location
                            }
                        }
                    }
                    .addOnFailureListener { exception ->
                        binding.shareLocation.isChecked = false
                        if (exception is ResolvableApiException) {
                            try {
                                resolutionLauncher.launch(
                                    IntentSenderRequest.Builder(exception.resolution).build()
                                )
                            } catch (sendEx: IntentSender.SendIntentException) {
                                Log.e(TAG, sendEx.stackTraceToString())
                            }
                        }
                    }

            }

            else -> {
                binding.shareLocation.isChecked = false
                locationPermissionRequest.launch(
                    arrayOf(
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                    )
                )
            }
        }
    }

    private fun subscribeUploadProgress(applicationState: ApplicationState<Nothing>) =
        with(binding) {
            when (applicationState) {
                is ApplicationState.Success -> {
                    progressBar.visibility = View.GONE
                    uploadButton.text = getString(R.string.upload)
                    savedStateHandle[ON_POST] = true
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

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    companion object {
        const val ON_POST = "on_post"
        private val TAG = PostStoryFragment::class.simpleName
    }
}

