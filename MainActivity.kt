package com.example.finalproject
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.content.Intent //추가
import android.widget.Button //추가

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val registerButton: Button = findViewById(R.id.button2) // 여기서부터 추가됨

        registerButton.setOnClickListener {  //버튼을 클릭시 이벤트
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }

        val loginButton: Button = findViewById(R.id.button)

        loginButton.setOnClickListener {  //버튼을 클릭시 이벤트
            val intent = Intent(this, MainScreenActivity::class.java)
            startActivity(intent)
        }
    }
}
