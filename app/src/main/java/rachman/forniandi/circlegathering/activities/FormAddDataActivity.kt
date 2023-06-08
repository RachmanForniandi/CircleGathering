package rachman.forniandi.circlegathering.activities

import android.app.Dialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import rachman.forniandi.circlegathering.R
import rachman.forniandi.circlegathering.databinding.ActivityFormAddDataBinding

class FormAddDataActivity : AppCompatActivity() {

    private lateinit var binding:ActivityFormAddDataBinding
    private lateinit var mCustomListDialog: Dialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_form_add_data)



    }

    companion object {
        private const val CAMERA = 1
        private const val GALLERY = 2

        private const val IMG_DIRECTORY ="IMG_DIRECTORY "
    }
}