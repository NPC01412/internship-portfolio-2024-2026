package com.example.myapplication

import android.app.Activity
import android.content.ContentValues
import android.content.Intent
import android.graphics.ColorMatrix
import android.graphics.ColorMatrixColorFilter
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.provider.MediaStore
import android.view.MotionEvent
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.example.myapplication.databinding.ActivitySetupBinding
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class SetupActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySetupBinding
    private var picURI: Uri? = null
    private var finalPhotoUri: Uri? = null

    private val pickImageLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val imageUri = result.data?.data
            finalPhotoUri = imageUri
            showIdCard(imageUri)
        }
    }

    private val getPicture = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            finalPhotoUri = picURI
            showIdCard(picURI)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySetupBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val sdf = SimpleDateFormat("yyyy/MM/dd HH:mm", Locale.getDefault())
        val currentTime = sdf.format(Date())
        binding.tvIdNumber.text = "No. $currentTime"

        binding.btnPickPhoto.visibility = View.GONE
        binding.btnTakePhoto.visibility = View.GONE
        binding.btnNext.visibility = View.GONE
        binding.btnCheckCamera.visibility = View.GONE

        // 第一段文字
        val part1 = "四周一片死寂與黑暗...\n\n你感覺到冰冷的水泥地。你伸出手在黑暗中摸索，碰到了兩個冰冷的物體：\n一台老舊的閃光燈相機，還有一張似乎是政府配發的職員證件。"

        typeText(part1, false) {
            binding.btnCheckCamera.visibility = View.VISIBLE
        }

        // 點擊檢查相機後會顯示第二段文字
        binding.btnCheckCamera.setOnClickListener {
            binding.btnCheckCamera.visibility = View.GONE

            val part2 = "\n\n你檢查了相機狀態，發現只剩幾張空白底片，以及一點電量。相機並未擁有夜視功能，因此你決定在適當的時機把底片取出，並利用空拍的幾秒閃光觀察房間，另外再把重要物品先用底片紀錄，維持現場狀況。\n\n你決定先藉著微弱的直覺，確認證件上的長相..."

            typeText(part2, true) {
                binding.layoutCameraPrompt.visibility = View.VISIBLE
                binding.layoutCameraPrompt.alpha = 0f
                binding.layoutCameraPrompt.animate().alpha(1f).setDuration(1000).withEndAction {
                    binding.btnPickPhoto.visibility = View.VISIBLE
                    binding.btnTakePhoto.visibility = View.VISIBLE
                }.start()
            }
        }

        binding.btnPickPhoto.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            pickImageLauncher.launch(intent)
        }

        binding.btnTakePhoto.setOnClickListener {
            val values = ContentValues().apply { put(MediaStore.Images.Media.DISPLAY_NAME, "investigator_avatar.png") }
            picURI = contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)
            val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE).apply { putExtra(MediaStore.EXTRA_OUTPUT, picURI) }
            getPicture.launch(intent)
        }

        binding.btnNext.setOnClickListener {
            val intent = Intent(this, StoryActivity::class.java)
            intent.putExtra("PHOTO_URI", finalPhotoUri?.toString())
            startActivity(intent)
            finish()
        }
    }

    // 修bug!
    override fun dispatchTouchEvent(ev: MotionEvent?): Boolean {
        if (binding.layoutCameraPrompt.visibility == View.VISIBLE && ev != null) {
            when (ev.actionMasked) {
                MotionEvent.ACTION_DOWN -> {
                    binding.layoutCameraPrompt.animate().cancel()
                    binding.layoutCameraPrompt.alpha = 0f // 手指按下隱藏
                }
                MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                    binding.layoutCameraPrompt.animate().cancel()
                    binding.layoutCameraPrompt.alpha = 1f // 手指放開顯示
                }
            }
        }
        return super.dispatchTouchEvent(ev)
    }

    private fun showIdCard(uri: Uri?) {
        binding.tvSetupStory.visibility = View.GONE
        binding.btnPickPhoto.visibility = View.GONE
        binding.btnTakePhoto.visibility = View.GONE
        binding.layoutCameraPrompt.visibility = View.GONE

        binding.layoutIdCard.visibility = View.VISIBLE
        binding.ivIdPhoto.setImageURI(uri)

        val matrix = ColorMatrix()
        matrix.setSaturation(0f)
        binding.ivIdPhoto.colorFilter = ColorMatrixColorFilter(matrix)

        binding.tvDescription.text = "照片上的人看起來有些憔悴，這就是你嗎？\n不管怎樣，你必須先弄清楚自己在哪裡。"
        binding.btnNext.visibility = View.VISIBLE
    }

    private fun typeText(text: String, append: Boolean = false, speedMs: Long = 40L, onComplete: () -> Unit = {}) {
        if (!append) binding.tvSetupStory.text = ""
        var index = 0
        val handler = Handler(Looper.getMainLooper())
        val runnable = object : Runnable {
            override fun run() {
                if (index < text.length) {
                    binding.tvSetupStory.append(text[index].toString())
                    binding.tvSetupStory.parent.requestChildFocus(binding.tvSetupStory, binding.tvSetupStory)
                    index++
                    handler.postDelayed(this, speedMs)
                } else {
                    onComplete()
                }
            }
        }
        handler.post(runnable)
    }
}