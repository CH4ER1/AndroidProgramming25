package com.example.finalproject

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Base64
import android.widget.*
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.firestore.FirebaseFirestore
import java.io.ByteArrayOutputStream
import java.io.InputStream
import kotlin.jvm.java

class ArchiveActivity : AppCompatActivity() {

    private lateinit var starViews: List<ImageView>
    private lateinit var imageSelect: ImageView
    private lateinit var editTitle: EditText
    private lateinit var editContent: EditText
    private lateinit var spinnerCategory: Spinner
    private lateinit var btnSubmit: Button

    private val IMAGE_PICK_CODE = 1000
    private var selectedImageUri: Uri? = null
    private var currentRating = 0

    // Firestore만 사용
    private val db = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_archive)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.archiveLayout)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        initViews()
        setupStarRating()
        setupImagePicker()
        setupSpinner()
        setupSubmitButton()

        // 하단 버튼
        findViewById<ImageView>(R.id.btnAll).setOnClickListener {
            startActivity(Intent(this, MainScreenActivity::class.java))
        }

        findViewById<ImageView>(R.id.btnSetting).setOnClickListener {
            startActivity(Intent(this, SettingActivity::class.java))
        }

        findViewById<ImageView>(R.id.btnBookmark).setOnClickListener {
            startActivity(Intent(this, BookmarkActivity::class.java))
        }


    }

    private fun initViews() {
        starViews = listOf(
            findViewById(R.id.star1),
            findViewById(R.id.star2),
            findViewById(R.id.star3),
            findViewById(R.id.star4),
            findViewById(R.id.star5)
        )

        imageSelect = findViewById(R.id.imageSelect)
        editTitle = findViewById(R.id.editTitle)
        editContent = findViewById(R.id.editContent)
        spinnerCategory = findViewById(R.id.spinnerCategory)
        btnSubmit = findViewById(R.id.btnSubmit)
    }

    private fun setupStarRating() {
        for ((index, star) in starViews.withIndex()) {
            star.setOnClickListener {
                currentRating = index + 1
                for (i in starViews.indices) {
                    starViews[i].setImageResource(
                        if (i <= index) R.drawable.star1 else R.drawable.emptystar
                    )
                }
            }
        }
    }

    private fun setupImagePicker() {
        imageSelect.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK)
            intent.type = "image/*"
            startActivityForResult(intent, IMAGE_PICK_CODE)
        }
    }

    private fun setupSpinner() {
        val categories = listOf("야구", "LCK", "공연")
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, categories)
        spinnerCategory.adapter = adapter
    }

    private fun setupSubmitButton() {
        btnSubmit.setOnClickListener {
            saveArchiveData()
        }
    }

    private fun saveArchiveData() {
        val title = editTitle.text.toString().trim()
        val content = editContent.text.toString().trim()
        val category = spinnerCategory.selectedItem.toString()

        // 입력 검증
        if (title.isEmpty()) {
            Toast.makeText(this, "제목을 입력해주세요", Toast.LENGTH_SHORT).show()
            return
        }

        if (content.isEmpty()) {
            Toast.makeText(this, "내용을 입력해주세요", Toast.LENGTH_SHORT).show()
            return
        }

        // 로딩 표시
        btnSubmit.isEnabled = false
        btnSubmit.text = "저장 중..."

        // 이미지를 Base64로 변환
        val imageBase64 = if (selectedImageUri != null) {
            convertImageToBase64(selectedImageUri!!)
        } else {
            ""
        }

        saveDataToFirestore(title, content, category, imageBase64)
    }

    private fun convertImageToBase64(imageUri: Uri): String {
        return try {
            val inputStream: InputStream? = contentResolver.openInputStream(imageUri)
            val bitmap = BitmapFactory.decodeStream(inputStream)

            // 이미지 크기 줄이기 (Firestore 문서 크기 제한 때문)
            val resizedBitmap = resizeBitmap(bitmap, 800, 600)

            val byteArrayOutputStream = ByteArrayOutputStream()
            resizedBitmap.compress(Bitmap.CompressFormat.JPEG, 70, byteArrayOutputStream)
            val byteArray = byteArrayOutputStream.toByteArray()
            Base64.encodeToString(byteArray, Base64.DEFAULT)
        } catch (e: Exception) {
            Toast.makeText(this, "이미지 변환 실패: ${e.message}", Toast.LENGTH_SHORT).show()
            ""
        }
    }

    private fun resizeBitmap(bitmap: Bitmap, maxWidth: Int, maxHeight: Int): Bitmap {
        val width = bitmap.width
        val height = bitmap.height

        val bitmapRatio = width.toFloat() / height.toFloat()
        val targetRatio = maxWidth.toFloat() / maxHeight.toFloat()

        val targetWidth: Int
        val targetHeight: Int

        if (bitmapRatio > targetRatio) {
            targetWidth = maxWidth
            targetHeight = (maxWidth / bitmapRatio).toInt()
        } else {
            targetHeight = maxHeight
            targetWidth = (maxHeight * bitmapRatio).toInt()
        }

        return Bitmap.createScaledBitmap(bitmap, targetWidth, targetHeight, true)
    }

    private fun saveDataToFirestore(title: String, content: String, category: String, imageBase64: String) {
        val archiveData = ArchiveData(
            title = title,
            rating = currentRating,
            imageUrl = imageBase64, // Base64 문자열로 저장
            category = category,
            content = content,
            timestamp = System.currentTimeMillis()
        )

        db.collection("archives")
            .add(archiveData)
            .addOnSuccessListener { documentReference ->
                // 문서 ID 업데이트
                db.collection("archives")
                    .document(documentReference.id)
                    .update("id", documentReference.id)
                    .addOnSuccessListener {
                        Toast.makeText(this, "기록이 저장되었습니다!", Toast.LENGTH_SHORT).show()
                        resetForm()
                    }
            }
            .addOnFailureListener { exception ->
                showError("저장 실패: ${exception.message}")
            }
    }

    private fun resetForm() {
        editTitle.text.clear()
        editContent.text.clear()
        currentRating = 0
        selectedImageUri = null

        // 별점 초기화
        starViews.forEach { it.setImageResource(R.drawable.emptystar) }

        // 이미지 초기화
        imageSelect.setImageResource(R.drawable.gallery)

        // 스피너 초기화
        spinnerCategory.setSelection(0)

        // 버튼 상태 복원
        btnSubmit.isEnabled = true
        btnSubmit.text = "작성 완료"
    }

    private fun showError(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
        btnSubmit.isEnabled = true
        btnSubmit.text = "작성 완료"
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == IMAGE_PICK_CODE && resultCode == Activity.RESULT_OK) {
            selectedImageUri = data?.data
            imageSelect.setImageURI(selectedImageUri)
            Toast.makeText(this, "이미지가 선택되었습니다", Toast.LENGTH_SHORT).show()
        }
    }

}
