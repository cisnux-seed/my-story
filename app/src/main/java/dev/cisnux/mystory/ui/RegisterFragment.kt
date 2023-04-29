package dev.cisnux.mystory.ui

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.res.Configuration
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputLayout
import dagger.hilt.android.AndroidEntryPoint
import dev.cisnux.mystory.R
import dev.cisnux.mystory.databinding.FragmentRegisterBinding
import dev.cisnux.mystory.utils.ApplicationState
import dev.cisnux.mystory.utils.Failure
import dev.cisnux.mystory.utils.FormType
import dev.cisnux.mystory.utils.createSpannableText
import dev.cisnux.mystory.viewmodels.AuthViewModel
import java.util.Locale

@AndroidEntryPoint
class RegisterFragment : Fragment() {
    private var _binding: FragmentRegisterBinding? = null
    private val binding get() = _binding!!
    private val viewModel: AuthViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRegisterBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        playAnimation()
        setUpFormAuthentication()
        viewModel.registerState.observe(viewLifecycleOwner, ::subscribeRegisterProgress)
        onRegister()
    }

    private fun playAnimation() = with(binding) {
        val registerPict =
            ObjectAnimator.ofFloat(registerPict, View.ALPHA, 1F)
                .setDuration(LoginFragment.ANIMATION_DURATION)
        val usernameEditTextLayout =
            ObjectAnimator.ofFloat(usernameEditTextLayout, View.ALPHA, 1F)
                .setDuration(LoginFragment.ANIMATION_DURATION)
        val emailEditTextLayout =
            ObjectAnimator.ofFloat(emailEditTextLayout, View.ALPHA, 1F)
                .setDuration(LoginFragment.ANIMATION_DURATION)
        val passwordEditTextLayout = ObjectAnimator.ofFloat(passwordEditTextLayout, View.ALPHA, 1F)
            .setDuration(LoginFragment.ANIMATION_DURATION)
        val signIn = ObjectAnimator.ofFloat(signIn, View.ALPHA, 1F)
            .setDuration(LoginFragment.ANIMATION_DURATION)
        val registerButton = ObjectAnimator.ofFloat(registerButton, View.ALPHA, 1F).setDuration(
            LoginFragment.ANIMATION_DURATION
        )

        AnimatorSet().apply {
            playSequentially(
                registerPict,
                usernameEditTextLayout,
                emailEditTextLayout,
                passwordEditTextLayout,
                signIn,
                registerButton
            )
        }.start()
    }

    private fun onRegister() = with(binding) {
        registerButton.setOnClickListener {
            if (usernameEditText.text?.isEmpty() == true) {
                Snackbar.make(
                    binding.root,
                    getString(R.string.snackbar_empty_username),
                    Snackbar.LENGTH_SHORT
                )
                    .show()
                return@setOnClickListener
            }
            if (emailEditText.text?.isEmpty() == true) {
                Snackbar.make(
                    binding.root,
                    getString(R.string.snackbar_empty_email),
                    Snackbar.LENGTH_SHORT
                )
                    .show()
                return@setOnClickListener
            }
            if (passwordEditText.text?.isEmpty() == true) {
                Snackbar.make(
                    binding.root,
                    getString(R.string.snackbar_empty_password),
                    Snackbar.LENGTH_SHORT
                )
                    .show()
                return@setOnClickListener
            }
            if (emailEditText.error == null && passwordEditText.error == null) {
                val name = usernameEditText.text.toString()
                val email = emailEditText.text.toString()
                val password = passwordEditText.text.toString()
                viewModel.register(
                    name, email, password
                )
            }
        }
    }

    private fun subscribeRegisterProgress(applicationState: ApplicationState<String>?) =
        with(binding) {
            when (applicationState) {
                is ApplicationState.Success -> {
                    progressBar.visibility = View.GONE
                    registerButton.text = getString(R.string.sign_up)
                    val message = applicationState.data
                    message?.let {
                        Snackbar.make(binding.root, it, Snackbar.LENGTH_SHORT)
                            .show()
                    }
                    findNavController().navigateUp()
                }

                is ApplicationState.Failed -> {
                    progressBar.visibility = View.GONE
                    registerButton.text = getString(R.string.sign_up)
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
                    registerButton.text = null
                }
            }
        }

    private fun setUpFormAuthentication() = with(binding) {
        val registerTextPlaceholder = resources.getString(R.string.register_placeholder)
        val text = createSpannableText(
            registerTextPlaceholder,
            resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK,
            if (Locale.getDefault().displayLanguage == "Indonesia") 18 else 17,
            registerTextPlaceholder.length
        )
        signIn.text = text
        signIn.setOnClickListener {
            val toLoginFragment = RegisterFragmentDirections.actionRegisterFragmentToLoginFragment()
            findNavController().navigate(toLoginFragment)
        }
        passwordEditText.formType = FormType.Password
        passwordEditText.doOnTextChanged { _, _, _, _ ->
            passwordEditTextLayout.endIconMode = if (passwordEditText.error != null)
                TextInputLayout.END_ICON_NONE
            else
                TextInputLayout.END_ICON_PASSWORD_TOGGLE
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}
