package dev.cisnux.mystory.ui

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.res.Configuration
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputLayout.END_ICON_NONE
import com.google.android.material.textfield.TextInputLayout.END_ICON_PASSWORD_TOGGLE
import dagger.hilt.android.AndroidEntryPoint
import dev.cisnux.mystory.R
import dev.cisnux.mystory.databinding.FragmentLoginBinding
import dev.cisnux.mystory.utils.ApplicationState
import dev.cisnux.mystory.utils.Failure
import dev.cisnux.mystory.utils.FormType
import dev.cisnux.mystory.utils.createSpannableText
import dev.cisnux.mystory.viewmodels.AuthViewModel
import java.util.Locale

@AndroidEntryPoint
class LoginFragment : Fragment() {
    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!
    private val viewModel: AuthViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLoginBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        playAnimation()
        setUpFormAuthentication()
        viewModel.loginState.observe(viewLifecycleOwner, ::subscribeLoginProgress)
        onLogin()
    }

    private fun playAnimation() = with(binding) {
        val loginPict =
            ObjectAnimator.ofFloat(loginPict, View.ALPHA, 1F)
                .setDuration(ANIMATION_DURATION)
        val emailEditTextLayout =
            ObjectAnimator.ofFloat(emailEditTextLayout, View.ALPHA, 1F)
                .setDuration(ANIMATION_DURATION)
        val passwordEditTextLayout = ObjectAnimator.ofFloat(passwordEditTextLayout, View.ALPHA, 1F)
            .setDuration(ANIMATION_DURATION)
        val signup = ObjectAnimator.ofFloat(signUp, View.ALPHA, 1F)
            .setDuration(ANIMATION_DURATION)
        val loginButton = ObjectAnimator.ofFloat(loginButton, View.ALPHA, 1F).setDuration(
            ANIMATION_DURATION
        )

        AnimatorSet().apply {
            playSequentially(
                loginPict,
                emailEditTextLayout,
                passwordEditTextLayout,
                signup,
                loginButton
            )
        }.start()
    }

    private fun onLogin() = with(binding) {
        loginButton.setOnClickListener {
            if (emailEditText.text?.isEmpty() == true) {
                Snackbar.make(binding.root, getString(R.string.snackbar_empty_email), Snackbar.LENGTH_SHORT)
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
                val email = emailEditText.text.toString()
                val password = passwordEditText.text.toString()
                viewModel.login(
                    email, password
                )
            }
        }
    }

    private fun subscribeLoginProgress(applicationState: ApplicationState<String>?) =
        with(binding) {
            when (applicationState) {
                is ApplicationState.Success -> {
                    progressBar.visibility = View.GONE
                    loginButton.text = getString(R.string.sign_in)
                    Snackbar.make(binding.root, getString(R.string.success_login_message), Snackbar.LENGTH_SHORT)
                        .show()
                    val toHomeFragment = LoginFragmentDirections.actionLoginFragmentToHomeFragment()
                    findNavController().navigate(toHomeFragment)
                }

                is ApplicationState.Failed -> {
                    progressBar.visibility = View.GONE
                    loginButton.text = getString(R.string.sign_in)
                    if (applicationState.failure is Failure.ConnectionFailure){
                        applicationState.failure.message = getString(R.string.no_internet_connection)
                    }
                    val errorMessage = applicationState.failure.message
                    errorMessage?.let {
                        Snackbar.make(binding.root, it, Snackbar.LENGTH_SHORT)
                            .show()
                    }
                }

                else -> {
                    progressBar.visibility = View.VISIBLE
                    loginButton.text = null
                }
            }
        }

    private fun setUpFormAuthentication() = with(binding) {
        val loginTextPlaceholder = resources.getString(R.string.login_placeholder)
        val text = createSpannableText(
            loginTextPlaceholder,
            resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK,
            if (Locale.getDefault().displayLanguage == "Indonesia") 18 else 23,
            loginTextPlaceholder.length
        )
        signUp.text = text
        signUp.setOnClickListener {
            val toRegisterFragment = LoginFragmentDirections.actionLoginFragmentToRegisterFragment()
            findNavController().navigate(toRegisterFragment)
        }
        passwordEditText.formType = FormType.Password
        passwordEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(
                s: CharSequence?,
                start: Int, count: Int,
                after: Int
            ) {
            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                if (passwordEditText.error != null) {
                    passwordEditTextLayout.endIconMode = END_ICON_NONE
                } else {
                    passwordEditTextLayout.endIconMode = END_ICON_PASSWORD_TOGGLE
                }
            }

            override fun afterTextChanged(s: Editable?) {}
        })
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    companion object {
        const val ANIMATION_DURATION = 500L
    }
}

