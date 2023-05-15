package rachman.forniandi.circlegathering.customViews

import android.content.Context
import android.graphics.drawable.Drawable
import android.text.Editable
import android.text.InputType
import android.text.TextWatcher
import android.util.AttributeSet
import android.util.Patterns
import androidx.appcompat.widget.AppCompatEditText
import androidx.core.content.ContextCompat
import rachman.forniandi.circlegathering.R

class CustomEmailEditText : AppCompatEditText {

    private lateinit var emailIcon: Drawable

    constructor(context: Context) : super(context) {
        startDraw()
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        startDraw()
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        startDraw()
    }

    private fun startDraw() {
        emailIcon = ContextCompat.getDrawable(context, R.drawable.ic_email) as Drawable
        inputType = InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS

        setHint(R.string.email)
        setAssetDrawAble(emailIcon)
        addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun afterTextChanged(p0: Editable?) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                // Email validation
                // Display error automatically if the email is not valid
                if (!s.isNullOrEmpty() && !Patterns.EMAIL_ADDRESS.matcher(s).matches())
                    error = context.getString(R.string.format_email_error_message)
            }
        })
    }

    private fun setAssetDrawAble(start: Drawable? = null,
                                 top:Drawable? = null,
                                 end:Drawable? = null,
                                 bottom: Drawable? = null) {
        setCompoundDrawablesWithIntrinsicBounds(start, top, end, bottom)
    }

    companion object{
        private const val INPUT_TYPE_EMAIL = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS
    }
}