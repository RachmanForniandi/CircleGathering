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
import rachman.forniandi.circlegathering.R

class CustomPasswordEditText : AppCompatEditText{

    private lateinit var passwordIcon: Drawable

    constructor(context: Context) : super(context) {
        startDrawPasswordEditText()
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        startDrawPasswordEditText()
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        startDrawPasswordEditText()
    }



    private fun startDrawPasswordEditText() {
        passwordIcon= ContextCompat.getDrawable(context, R.drawable.ic_key)as Drawable


        addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            override fun afterTextChanged(p0: Editable?) {}

            override fun onTextChanged(char: CharSequence?, start: Int, before: Int, count: Int) {
                // Password validation
                // Display error automatically if the password doesn't meet certain criteria
                if (!char.isNullOrEmpty() && char.length < 6)
                    error = context.getString(R.string.input_password_error_message)
            }
        })
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        transformationMethod = PasswordTransformationMethod.getInstance()
        inputType = InputType.TYPE_TEXT_VARIATION_PASSWORD

        setAssetDrawAble(passwordIcon)
        setHint(R.string.password)
    }
    /*private fun enableShowPassword(){
        //setAssetDrawAble(endOfTheText = showPasswordIcon)
        inputType = InputType.TYPE_TEXT_VARIATION_NORMAL
    }

    private fun enableHidePassword(){
        //setAssetDrawAble(endOfTheText = hidePasswordIcon)
        inputType = InputType.TYPE_TEXT_VARIATION_PASSWORD
    }*/
    private fun setAssetDrawAble(startOfTheText: Drawable? = null, topOfTheText:Drawable? = null, endOfTheText:Drawable? = null, bottomOfTheText: Drawable? = null){
        setCompoundDrawablesWithIntrinsicBounds(startOfTheText, topOfTheText, endOfTheText, bottomOfTheText)
    }

    /*override fun onTouch(view: View?, event: MotionEvent?): Boolean {
        if (compoundDrawables[2] != null) {
            var isEyeButtonClicked = false

            if (isEyeButtonClicked){
                enableHidePassword()
            }else{
                enableShowPassword()
            }
        }
        return false
    }*/
}