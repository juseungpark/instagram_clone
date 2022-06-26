package navigation

import android.app.Activity
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
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.UploadTask
import com.juseung.instagram_clone.R
import com.juseung.instagram_clone.databinding.ActivityAddPhotoBinding
import navigation.model.ContentDTO
import java.text.SimpleDateFormat
import java.util.*

class AddPhotoActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAddPhotoBinding

    var photoUri: Uri? = null
    var photoPickerIntent = Intent(Intent.ACTION_PICK)
    var auth: FirebaseAuth? = null
    var firestore: FirebaseFirestore? = null
    var storage: FirebaseStorage? = null


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


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddPhotoBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        //스토리지 초기화
        storage = FirebaseStorage.getInstance()
        auth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()





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

    fun contentUpload() {
        var timestamp = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        var imageFileName = "IMAGE_" + timestamp + "_.png"

        var storageRef = storage?.reference?.child("images")?.child(imageFileName)

        storageRef?.putFile(photoUri!!)?.continueWithTask { task: Task<UploadTask.TaskSnapshot> ->
            return@continueWithTask storageRef.downloadUrl


        }?.addOnCompleteListener { uri ->
            var contentDTO = ContentDTO()

            //이미지 경로
            contentDTO.imageUrl = uri.toString()

            //UID or USER
            contentDTO.uid = auth?.currentUser?.uid

            //유저 아이디
            contentDTO.userId = auth?.currentUser?.email


            contentDTO.explain = binding.addphotoEditExplain.text.toString()

            contentDTO.timestamp = System.currentTimeMillis()

            firestore?.collection("images")?.document()?.set(contentDTO)
            setResult(Activity.RESULT_OK)
            finish()
        }


        //콜백

    }


}

