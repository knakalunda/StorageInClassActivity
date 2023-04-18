package com.example.networkapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import com.android.volley.RequestQueue
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.squareup.picasso.Picasso
import org.json.JSONObject
import java.io.BufferedReader
import java.io.File
import java.io.FileOutputStream
import java.io.FileReader
import java.io.IOException


class MainActivity : AppCompatActivity() {

    private lateinit var requestQueue: RequestQueue
    lateinit var titleTextView: TextView
    lateinit var descriptionTextView: TextView
    lateinit var numberEditText: EditText
    lateinit var showButton: Button
    lateinit var comicImageView: ImageView

    private lateinit var file: File
    private val filename = "json_text"


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        requestQueue = Volley.newRequestQueue(this)

        titleTextView = findViewById<TextView>(R.id.comicTitleTextView)
        descriptionTextView = findViewById<TextView>(R.id.comicDescriptionTextView)
        numberEditText = findViewById<EditText>(R.id.comicNumberEditText)
        showButton = findViewById<Button>(R.id.showComicButton)
        comicImageView = findViewById<ImageView>(R.id.comicImageView)

        file = File(filesDir, filename)

        if(file.exists()){
            try{
                val br = BufferedReader(FileReader(file))
                val text = StringBuilder()
                var line:String?
                while(br.readLine().also {line=it} != null){
                    text.append(line)
                   text.append("/n")
                }
                br.close()
                showComic(JSONObject(text.toString()))
            } catch(e : IOException){
                e.printStackTrace()
            }
        }

        showButton.setOnClickListener {
            downloadComic(numberEditText.text.toString())
        }


    }

    private fun downloadComic (comicId: String) {
        val url = "https://xkcd.com/$comicId/info.0.json"
        requestQueue.add (
            JsonObjectRequest(url, {
                try{
                    val outputStream = FileOutputStream(file)
                    outputStream.write(it.toString().toByteArray())
                    outputStream.close()
                } catch (e:Exception) {
                    e.printStackTrace()
                }
                showComic(it)
                                   }, {
            })
        )

    }

    private fun showComic (comicObject: JSONObject) {
        titleTextView.text = comicObject.getString("title")
        descriptionTextView.text = comicObject.getString("alt")
        Picasso.get().load(comicObject.getString("img")).into(comicImageView)
    }

}