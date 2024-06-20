package rachman.forniandi.circlegathering.customViews

import android.content.Context
import android.graphics.Canvas
import android.graphics.drawable.Drawable
import android.text.InputType
import android.util.AttributeSet
import android.util.Patterns
import android.view.MotionEvent
import android.view.View
import androidx.appcompat.widget.AppCompatEditText
import androidx.core.content.ContextCompat
import androidx.core.content.res.getIntOrThrow
import androidx.core.widget.addTextChangedListener
import rachman.forniandi.circlegathering.R

class CustomInputEditText : AppCompatEditText, View.OnTouchListener {

    private var initiateIconDrawable: Drawable? = null
    private var showPasswordIconDrawable: Drawable? = null
    private var hidePasswordIconDrawable: Drawable? = null

    private var backgroundDrawable: Drawable ? = null
    private var backgroundErrorDrawable: Drawable ? = null
    private var backgroundCorrectDrawable: Drawable? = null

    private var minimumPasswordCharacter = 8
    private var hintInput=""
    private var textViewUsage = TextViewUsage.PASSWORD

    private var isPasswordShown = false


    constructor(context: Context) : super(context) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        getAttribute(attrs)
        init()
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        getAttribute(attrs)
        init()
    }



    private fun init() {
        when(textViewUsage){
            TextViewUsage.EMAIL->{
                inputType = INPUT_TYPE_EMAIL_FORMAT
                initiateIconDrawable= ContextCompat.getDrawable(context, R.drawable.ic_email)as Drawable
                
                hintInput = resources.getString(R.string.prompt_email)
                addTextChangedListener(onTextChanged = { email, _, _, _ ->
                    if (!isInputEmailValid(email)) setError(resources.getString(R.string.email_error_message), null)
                    else clearErrorMessage()
                    backgroundCorrectDrawable = ContextCompat.getDrawable(context, R.drawable.bg_entry_form_correct)
                })
            }
            TextViewUsage.PASSWORD->{
                inputType = INPUT_TYPE_INVISIBLE_PASSWORD
                
                initiateIconDrawable= ContextCompat.getDrawable(context, R.drawable.ic_key)as Drawable
                showPasswordIconDrawable = ContextCompat.getDrawable(context, R.drawable.ic_visibility_on)
                hidePasswordIconDrawable = ContextCompat.getDrawable(context, R.drawable.ic_visibility_off)
                hintInput = resources.getString(R.string.prompt_password)
                addTextChangedListener(onTextChanged = { password, _, _, _ ->
                    if (!isInputPasswordValid(password)) setError(resources.getString(R.string.input_password_error_message), null)
                    else clearErrorMessage()
                    backgroundCorrectDrawable = ContextCompat.getDrawable(context, R.drawable.bg_entry_form_correct)

                })

                setOnTouchListener(this)
            }
            TextViewUsage.USERNAME->{

                inputType = INPUT_TYPE_TEXT_NORMAL

                initiateIconDrawable= ContextCompat.getDrawable(context, R.drawable.ic_person)as Drawable
                hintInput = resources.getString(R.string.prompt_name)

                addTextChangedListener(onTextChanged = { name, _, _, _ ->
                    if (!isInputUsernameValid(name)) setError(resources.getString(R.string.input_name_error_message), null)
                    clearErrorMessage()
                    backgroundCorrectDrawable = ContextCompat.getDrawable(context, R.drawable.bg_entry_form_correct)

                })
            }
        }
        backgroundDrawable= ContextCompat.getDrawable(context, R.drawable.bg_entry_form)
        backgroundErrorDrawable= ContextCompat.getDrawable(context, R.drawable.bg_entry_form_error)

    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        setAssetDrawAble(
            startOfTheText = initiateIconDrawable,
            endOfTheText = if (isPasswordShown) showPasswordIconDrawable else hidePasswordIconDrawable
        )
        background = if (error.isNullOrEmpty()) backgroundCorrectDrawable ?: backgroundDrawable else backgroundErrorDrawable
        hint = hintInput
    }

    private fun getAttribute(attrs: AttributeSet?) {
        val style = context.obtainStyledAttributes(attrs, R.styleable.CustomInputEditText)
        minimumPasswordCharacter = style.getInt(R.styleable.CustomInputEditText_min_password_length, 8)
        textViewUsage = when (style.getIntOrThrow(R.styleable.CustomInputEditText_custom_type)) {
            TextViewUsage.EMAIL.value -> TextViewUsage.EMAIL
            TextViewUsage.PASSWORD.value -> TextViewUsage.PASSWORD
            TextViewUsage.USERNAME.value -> TextViewUsage.USERNAME
            else -> throw IllegalArgumentException("Invalid custom_type value")
        }
        style.recycle()
    }
    private fun setAssetDrawAble(startOfTheText: Drawable? = null,
                                 topOfTheText:Drawable? = null,
                                 endOfTheText:Drawable? = null,
                                 bottomOfTheText: Drawable? = null){
        setCompoundDrawablesWithIntrinsicBounds(startOfTheText, topOfTheText, endOfTheText, bottomOfTheText)
        compoundDrawablePadding = 16
    }

    private fun isInputPasswordValid(password: CharSequence?) =
        !password.isNullOrEmpty() && password.length >= minimumPasswordCharacter

    private fun isInputEmailValid(email: CharSequence?) =
        !email.isNullOrEmpty() && Patterns.EMAIL_ADDRESS.matcher(email).matches() && email.isNotBlank()

    private fun isInputUsernameValid(name: CharSequence?) = !name.isNullOrEmpty()

    private fun clearErrorMessage() {
        error = null
    }
    override fun onTouch(view: View?, event: MotionEvent): Boolean {
        if (compoundDrawables[2] != null) {
            val showHideButtonStart: Float
            val showHideButtonEnd: Float
            var isEyeButtonClicked = false

            if (layoutDirection == View.LAYOUT_DIRECTION_RTL){
                //enableHidePassword()
                showHideButtonEnd = ((hidePasswordIconDrawable?.intrinsicWidth ?: 0) + paddingStart).toFloat()
                when {
                    event.x < showHideButtonEnd -> isEyeButtonClicked = true
                }
            }else{
                //enableShowPassword()
                showHideButtonStart = (width - paddingEnd - (hidePasswordIconDrawable?.intrinsicWidth ?: 0)).toFloat()
                when {
                    event.x > showHideButtonStart -> isEyeButtonClicked = true
                }
            }

            return if (isEyeButtonClicked) {
                when (event.action) {
                    MotionEvent.ACTION_UP -> {
                        isPasswordShown = !isPasswordShown
                        updatePasswordVisibility()
                        true
                    }
                    else -> false
                }
            } else false
        }
        return false
    }

    private fun updatePasswordVisibility() {
        inputType = if (isPasswordShown) INPUT_TYPE_VISIBLE_PASSWORD else INPUT_TYPE_INVISIBLE_PASSWORD
        setSelection(text?.length ?: 0)
    }
    enum class TextViewUsage(val value: Int) {
        EMAIL(0),
        PASSWORD(1),
        USERNAME(2)
    }

    companion object{
        private const val INPUT_TYPE_EMAIL_FORMAT = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS
        private const val INPUT_TYPE_INVISIBLE_PASSWORD = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
        private const val INPUT_TYPE_VISIBLE_PASSWORD = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
        private const val INPUT_TYPE_TEXT_NORMAL = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_NORMAL

    }
}