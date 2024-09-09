package com.example.playlistmaker

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class MainActivity : AppCompatActivity() {
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        //1 способ через анонимный класс
        val search = findViewById<Button>(R.id.search)

        val searchClickListener: View.OnClickListener = object : View.OnClickListener {
            override fun onClick(v: View?) {
                Toast.makeText(this@MainActivity, "Поиск", Toast.LENGTH_SHORT).show()
            }
        }
        search.setOnClickListener(searchClickListener)

        //2 способ через лямбду
        val library = findViewById<Button>(R.id.library)
        library.setOnClickListener {
            Toast.makeText(this@MainActivity, "Медиатека", Toast.LENGTH_SHORT).show()
        }

        val settings = findViewById<Button>(R.id.settings)
        settings.setOnClickListener{
            Toast.makeText(this@MainActivity, "Настройки", Toast.LENGTH_SHORT).show()
        }

    }
}