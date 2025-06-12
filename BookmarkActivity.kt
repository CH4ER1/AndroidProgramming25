package com.example.finalproject

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat.startActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore


class BookmarkActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var postAdapter: ArchiveAdapter
    private val db = FirebaseFirestore.getInstance()
    private var postList = mutableListOf<BookmarkData>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_bookmark)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // 하단 버튼 바
        findViewById<ImageView>(R.id.btnWritePost).setOnClickListener {
            startActivity(Intent(this, ArchiveActivity::class.java))
        }

        findViewById<ImageView>(R.id.btnSetting).setOnClickListener {
            startActivity(Intent(this, SettingActivity::class.java))
        }

        findViewById<ImageView>(R.id.btnContent).setOnClickListener {
            startActivity(Intent(this, MainScreenActivity::class.java))
        }

    }
}
