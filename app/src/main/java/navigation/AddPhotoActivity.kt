package navigation

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.common.api.ApiException
import com.google.firebase.storage.FirebaseStorage
import com.juseung.instagram_clone.R
import com.juseung.instagram_clone.databinding.ActivityAddPhotoBinding
import java.text.SimpleDateFormat
import java.util.*

class AddPhotoActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAddPhotoBinding

    var photoUri : Uri? = null
    var photoPickerIntent = Intent(Intent.ACTION_PICK)



    private var storageActivityResultLauncher: ActivityResultLauncher<Intent> =
    registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
        if (result.resultCode == RESULT_OK) {
            //콜백됐을 때 REULTCODE가 OK면 실행
            photoUri = result.data?.data
            binding.addphotoImage.setImageURI(photoUri)
        } else {
            finish()
        }
    }

    var storage: FirebaseStorage? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddPhotoBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        //스토리지 초기화
        storage = FirebaseStorage.getInstance()

        useActivityResultLauncher()
        //버튼 이미지 업로드 이벤트
        binding.addphotoBtnUpload.setOnClickListener {
            contentUpload()
        }

    }
    fun useActivityResultLauncher() {
        photoPickerIntent.type = "image/*"
        storageActivityResultLauncher.launch(photoPickerIntent)
    }
    fun contentUpload(){
        var timestamp = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        var imageFileName = "IMAGE_" + timestamp + "_.png"

        var storageRef = storage?.reference?.child("images")?.child(imageFileName)

        storageRef?.putFile(photoUri!!)?.addOnSuccessListener {
            Toast.makeText(this,getString(R.string.upload_success), Toast.LENGTH_SHORT).show()
        }
        }


    }

