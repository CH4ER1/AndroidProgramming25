package com.example.finalproject

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore

class MainScreenActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var postAdapter: ArchiveAdapter
    private val db = FirebaseFirestore.getInstance()
    private var postList = mutableListOf<ArchiveData>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main_screen)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.mainScreenLayout)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // 뷰 초기화
        recyclerView = findViewById(R.id.recyclerViewPosts)
        recyclerView.layoutManager = LinearLayoutManager(this)
        postAdapter = ArchiveAdapter(postList)
        recyclerView.adapter = postAdapter

        // 하단 버튼
        findViewById<ImageView>(R.id.btnWritePost).setOnClickListener {
            startActivity(Intent(this, ArchiveActivity::class.java))
        }

        findViewById<ImageView>(R.id.btnSetting).setOnClickListener {
            startActivity(Intent(this, SettingActivity::class.java))
        }

        // 필터 버튼
        findViewById<Button>(R.id.btnAll).setOnClickListener {
            fetchPosts(null)
        }
        findViewById<Button>(R.id.btnBaseball).setOnClickListener {
            fetchPosts("야구")
        }
        findViewById<Button>(R.id.btnLCK).setOnClickListener {
            fetchPosts("LCK")
        }
        findViewById<Button>(R.id.btnConcert).setOnClickListener {
            fetchPosts("공연")
        }

        // 최초 로딩 시 전체 데이터 불러오기
        fetchPosts(null)
    }

    private fun fetchPosts(category: String?) {
        val collectionRef = db.collection("archives")
        val query = if (category != null) {
            collectionRef.whereEqualTo("category", category)
        } else {
            collectionRef
        }

        query.get().addOnSuccessListener { result ->
            postList.clear()
            for (document in result) {
                val post = document.toObject(ArchiveData::class.java)
                postList.add(post)
            }
            postAdapter.notifyDataSetChanged()
        }.addOnFailureListener {
            // 실패 처리 (필요 시 Toast 등으로 추가)
        }
    }
}
