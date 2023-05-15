package rachman.forniandi.circlegathering.customViews

import android.content.Context
import android.graphics.Canvas
import android.graphics.drawable.Drawable
import android.text.Editable
import android.text.InputType
import android.text.TextWatcher
import android.text.method.PasswordTransformationMethod
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import androidx.appcompat.widget.AppCompatEditText
import androidx.core.content.ContextCompat
import androidx.core.widget.addTextChangedListener
import rachman.forniandi.circlegathering.R

class CustomPasswordEditText : AppCompatEditText, View.OnTouchListener {

    private var initiateIconDrawable: Drawable? = null
    private var showPasswordIconDrawable: Drawable? = null
    private var hidePasswordIconDrawable: Drawable? = null

    private var backgroundDrawable: Drawable ? = null
    private var backgroundErrorDrawable: Drawable ? = null
    private var backgroundCorrectDrawable: Drawable? = null
    private var minimumPasswordCharacter = 8
    private var hintPassword=""

    private var isPasswordShown = false


    constructor(context: Context) : super(context) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        init()
    }



    private fun init() {
        initiateIconDrawable= ContextCompat.getDrawable(context, R.drawable.ic_key)as Drawable
        showPasswordIconDrawable = ContextCompat.getDrawable(context, R.drawable.ic_visibility_on)
        hidePasswordIconDrawable = ContextCompat.getDrawable(context, R.drawable.ic_visibility_off)
        hintPassword = resources.getString(R.string.prompt_password)

        addTextChangedListener(onTextChanged = { password, _, _, _ ->
            if (!isValidPassword(password)) setError(resources.getString(R.string.input_password_error_message), null)
            backgroundCorrectDrawable = ContextCompat.getDrawable(context, R.drawable.bg_entry_form_correct)

        })
        backgroundDrawable= ContextCompat.getDrawable(context, R.drawable.bg_entry_form)
        backgroundErrorDrawable= ContextCompat.getDrawable(context, R.drawable.bg_entry_form_error)
        
        setOnTouchListener(this)
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        setAssetDrawAble(
            startOfTheText = initiateIconDrawable,
            endOfTheText = if (isPasswordShown) showPasswordIconDrawable else hidePasswordIconDrawable
        )
        background = if (error.isNullOrEmpty()) backgroundCorrectDrawable ?: backgroundDrawable else backgroundErrorDrawable
        hint = hintPassword
    }

    private fun setAssetDrawAble(startOfTheText: Drawable? = null,
                                 topOfTheText:Drawable? = null,
                                 endOfTheText:Drawable? = null,
                                 bottomOfTheText: Drawable? = null){
        setCompoundDrawablesWithIntrinsicBounds(startOfTheText, topOfTheText, endOfTheText, bottomOfTheText)
        compoundDrawablePadding = 16
    }

    fun isValidPassword(password: CharSequence?) =
        !password.isNullOrEmpty() && password.length >= minimumPasswordCharacter


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

    companion object{
        private const val INPUT_TYPE_INVISIBLE_PASSWORD = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
        private const val INPUT_TYPE_VISIBLE_PASSWORD = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
    }
}