package dev.cisnux.mystory.ui.customviews

import android.content.Context
import android.util.AttributeSet
import androidx.core.widget.doOnTextChanged
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import dev.cisnux.mystory.R
import dev.cisnux.mystory.utils.FormType
import dev.cisnux.mystory.utils.isEmail
import dev.cisnux.mystory.utils.isPasswordSecure


class AuthEditText : TextInputEditText {
    var formType = FormType.EmailAddress

    var textInputLayout: TextInputLayout? = null

    constructor(context: Context) : super(context)

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(
        context, attrs, defStyleAttr
    )

    init {
        doOnTextChanged { text, _, _, _ ->
            doValidation(text.toString())
        }
    }

    private fun doValidation(text: String) {
        error = when (formType) {
            FormType.EmailAddress -> {
                if (!text.isEmail()) context.getString(R.string.invalid_email_message_error) else null
            }

            FormType.Password -> {
                if (!text.isPasswordSecure())
                    context.getString(R.string.invalid_password_error_message)
                else null
            }
        }
        textInputLayout?.endIconMode = if (error != null)
            TextInputLayout.END_ICON_NONE
        else
            TextInputLayout.END_ICON_PASSWORD_TOGGLE
    }
}