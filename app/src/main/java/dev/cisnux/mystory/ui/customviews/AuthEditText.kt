package dev.cisnux.mystory.ui.customviews

import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import android.util.AttributeSet
import com.google.android.material.textfield.TextInputEditText
import dev.cisnux.mystory.R
import dev.cisnux.mystory.utils.FormType
import dev.cisnux.mystory.utils.isEmail
import dev.cisnux.mystory.utils.isPasswordSecure


class AuthEditText : TextInputEditText {
    var formType = FormType.EmailAddress

    constructor(context: Context) : super(context)

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(
        context, attrs, defStyleAttr
    )

    init {
        addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                val text = s.toString()
                doValidation(text)
            }

            override fun afterTextChanged(s: Editable?) {
            }
        })
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
    }
}