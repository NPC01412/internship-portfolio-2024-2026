package com.example.myapplication

import android.animation.ArgbEvaluator
import android.animation.ValueAnimator
import android.content.Intent
import android.graphics.Color
import android.graphics.ColorMatrix
import android.graphics.ColorMatrixColorFilter
import android.media.MediaPlayer
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.myapplication.databinding.ActivityStoryBinding

class StoryActivity : AppCompatActivity() {

    private lateinit var binding: ActivityStoryBinding

    //音樂 player變數
    private var bgmPlayer: MediaPlayer? = null
    private var heartbeatPlayer: MediaPlayer? = null

    var flashCount = 25
    var filmCount = 8
    var hasNote = false
    var hasPhoto = false

    var hasCheckedPaint = false
    var hasGolfBall = false
    var hasEyePhoto = false

    var hasKey = false
    var mirrorBroken = false
    var cameraDamaged = false
    var currentFacing = 0

    var hasTriggeredRatTrap = false
    var hasPhotographedChains = false
    var hasPhotographedStatue = false

    var randomBooksRead = 0

    var bookcasePuzzleStep = 0
    var currentNotebookPage = 1
    val maxNotebookPage = 5

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityStoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val photoUriString = intent.getStringExtra("PHOTO_URI")
        if (photoUriString != null) {
            binding.ivEndingPhoto.setImageURI(Uri.parse(photoUriString))
            val matrix = ColorMatrix()
            matrix.setSaturation(0f)
            binding.ivEndingPhoto.colorFilter = ColorMatrixColorFilter(matrix)
        }

        //背景音樂BGM
        bgmPlayer = MediaPlayer.create(this, R.raw.bgm_creepy)
        bgmPlayer?.isLooping = true // 設定為無限循環
        bgmPlayer?.start()

        updateStatusText()
        scene1_Awake()
    }


    private fun playSoundEffect(soundResId: Int) {
        val mediaPlayer = MediaPlayer.create(this, soundResId)
        mediaPlayer.setOnCompletionListener {
            it.release()
        }
        mediaPlayer.start()
    }

    // 聽音樂!
    override fun onDestroy() {
        super.onDestroy()
        bgmPlayer?.release()
        heartbeatPlayer?.release()
    }

    private fun updateStatusText() {
        binding.tvStatus.text = "⚡ 閃光: $flashCount   |   🎞️ 底片: $filmCount"
    }

    private fun setMaskBrightness(isReadingNote: Boolean) {
        if (isReadingNote) {
            binding.vDarkMask.setBackgroundColor(Color.parseColor("#99000000"))
        } else {
            binding.vDarkMask.setBackgroundColor(Color.parseColor("#66000000"))
        }
    }

    private fun triggerFlashEffect() {
        // 相機喀嚓聲
        playSoundEffect(R.raw.camera_click)

        binding.vFlashOverlay.visibility = View.VISIBLE
        binding.vFlashOverlay.alpha = 1f
        binding.vFlashOverlay.animate()
            .alpha(0f)
            .setDuration(400)
            .withEndAction {
                binding.vFlashOverlay.visibility = View.GONE
            }
            .start()
    }

    private fun triggerFilmEffect(onComplete: () -> Unit = {}) {
        binding.vFilmBorder.visibility = View.VISIBLE
        binding.vFilmBorder.alpha = 0f
        binding.vFilmBorder.animate()
            .alpha(1f)
            .setDuration(200)
            .withEndAction {
                binding.vFilmBorder.animate()
                    .alpha(0f)
                    .setStartDelay(400)
                    .setDuration(400)
                    .withEndAction {
                        binding.vFilmBorder.visibility = View.GONE
                        onComplete()
                    }
                    .start()
            }.start()
    }

    private fun typeText(text: String, colorHex: String = "#E0E0E0", speedMs: Long = 30L, onComplete: () -> Unit = {}) {
        binding.tvStory.setTextColor(Color.parseColor(colorHex))
        binding.tvStory.text = ""
        var index = 0
        val handler = Handler(Looper.getMainLooper())
        hideAllButtons()
        val runnable = object : Runnable {
            override fun run() {
                if (index < text.length) {
                    binding.tvStory.append(text[index].toString())
                    binding.tvStory.parent.requestChildFocus(binding.tvStory, binding.tvStory)
                    index++
                    handler.postDelayed(this, speedMs)
                } else {
                    onComplete()
                }
            }
        }
        handler.post(runnable)
    }

    private fun hideAllButtons() {
        binding.btnOption1.visibility = View.GONE
        binding.btnOption2.visibility = View.GONE
        binding.btnOption3.visibility = View.GONE
        binding.btnOption4.visibility = View.GONE
    }

    private fun useFlash(cost: Int = 1): Boolean {
        if (flashCount >= cost) {
            flashCount -= cost
            updateStatusText()
            triggerFlashEffect()
            return true
        } else {
            if (!hasKey) scene_BadEnding()
            else Toast.makeText(this, "相機電量不足！", Toast.LENGTH_SHORT).show()
            return false
        }
    }

    private fun returnToMainMenu() {
        // 1.隱藏所有按鈕避免有人在動畫期間亂點
        hideAllButtons()

        // 2.最上層的圖層的全黑遮罩並將透明度設為0
        binding.vFadeOutBlack.visibility = View.VISIBLE
        binding.vFadeOutBlack.alpha = 0f

        // 3.變黑動畫
        binding.vFadeOutBlack.animate()
            .alpha(1f)
            .setDuration(2500)
            .withEndAction {
                // 動畫結束後才真正切換回首頁
                val intent = Intent(this, MainActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
                startActivity(intent)

                // 修bug!!加入Android內建的平滑切換特效
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
                finish()
            }
            .start()
    }

    // 場景

    private fun scene1_Awake() {
        binding.ivScene.setImageResource(android.R.color.black)
        setMaskBrightness(false)
        typeText("你從昏迷中醒來，四周伸手不見五指。空氣中瀰漫著濃烈的鐵鏽與血腥味。\n\n你發現自己在一間完全黑暗的房間裡，但令人毛骨悚然的是，你明明沒有開燈，卻能清楚看見手上的調查員證件與相機...") {
            binding.btnOption1.visibility = View.VISIBLE
            binding.btnOption1.text = "[按下相機閃光燈確認前方]"
            binding.btnOption1.setOnClickListener {
                if (useFlash()) {
                    currentFacing = 0
                    renderCurrentScene()
                }
            }
        }
    }

    private fun renderCurrentScene() {
        setMaskBrightness(false)
        binding.btnOption1.visibility = View.VISIBLE
        binding.btnOption2.visibility = View.VISIBLE
        binding.btnOption3.visibility = View.VISIBLE
        binding.btnOption4.visibility = View.GONE

        binding.btnOption1.text = "[往左轉向並閃光] (消耗 1 電量)"
        binding.btnOption2.text = "[往右轉向並閃光] (消耗 1 電量)"
        binding.btnOption3.text = "[在黑暗中調查此方向的房間]"

        binding.btnOption1.setOnClickListener {
            if (useFlash()) { currentFacing = if (currentFacing == 0) 3 else currentFacing - 1; renderCurrentScene() }
        }
        binding.btnOption2.setOnClickListener {
            if (useFlash()) { currentFacing = if (currentFacing == 3) 0 else currentFacing + 1; renderCurrentScene() }
        }
        binding.btnOption3.setOnClickListener { investigateCurrentFacing() }

        when (currentFacing) {
            0 -> {
                binding.ivScene.setImageResource(R.drawable.north)
                binding.tvStory.text = "【北面牆壁】\n一扇被沉重鐵鍊與大鎖死死封住的鐵門。牆角散落著幾桶紅色油漆，地上遺留著一把鐵扳手。這裡是地下室。"
            }
            1 -> {
                binding.ivScene.setImageResource(if (mirrorBroken) R.drawable.broken_mirror else R.drawable.east)
                binding.tvStory.text = if (mirrorBroken) "【東面牆壁】\n地上滿是全身鏡的碎片，鏡框裡只剩下斑駁的牆壁。" else "【東面牆壁】\n正中央佇立著那一面被沾污白布覆蓋著的巨大全身鏡。一種不祥的預感死死揪住你的心臟..."
            }
            2 -> {
                binding.ivScene.setImageResource(R.drawable.south)
                binding.tvStory.text = "【南面牆壁】\n一個巨大的老舊木製書架，上面塞滿了發霉的神祕學書籍與文獻。"
            }
            3 -> {
                binding.ivScene.setImageResource(R.drawable.west)
                binding.tvStory.text = "【西面牆壁】\n一個大書櫃與一張凌亂的書桌。桌上放著一本沾滿灰塵的【舊筆記本】。"
            }
        }
    }

    private fun investigateCurrentFacing() {
        setMaskBrightness(false)
        binding.ivScene.setImageResource(android.R.color.black)
        binding.tvStory.text = "四周陷入黑暗...\n\n你憑藉記憶與剛剛短暫的燈光，決定："

        binding.btnOption1.visibility = View.VISIBLE
        binding.btnOption2.visibility = View.GONE
        binding.btnOption3.visibility = View.GONE
        binding.btnOption4.visibility = View.VISIBLE
        binding.btnOption4.text = "[停止調查，準備相機轉向]"

        binding.btnOption4.setOnClickListener {
            binding.tvStory.text = "你在黑暗中停下動作。接下來要往哪裡看？"
            binding.btnOption1.visibility = View.VISIBLE
            binding.btnOption2.visibility = View.VISIBLE
            binding.btnOption3.visibility = View.GONE
            binding.btnOption4.visibility = View.GONE
            binding.btnOption1.text = "[往左轉向並閃光] (消耗 1 電量)"
            binding.btnOption2.text = "[往右轉向並閃光] (消耗 1 電量)"
            binding.btnOption1.setOnClickListener { if (useFlash()) { currentFacing = if (currentFacing == 0) 3 else currentFacing - 1; renderCurrentScene() } }
            binding.btnOption2.setOnClickListener { if (useFlash()) { currentFacing = if (currentFacing == 3) 0 else currentFacing + 1; renderCurrentScene() } }
        }

        when (currentFacing) {
            0 -> {
                if (hasKey) {
                    binding.btnOption1.text = "[對準大鎖開門]"
                    binding.btnOption1.setOnClickListener { scene_NormalEnding() }
                } else {
                    binding.btnOption1.text = "摸黑踩上油漆桶，檢查天花板燈泡"
                    binding.btnOption1.setOnClickListener {
                        if (!cameraDamaged) {
                            cameraDamaged = true
                            flashCount = maxOf(0, flashCount - 3)
                            updateStatusText()
                            binding.ivCameraCrack.visibility = View.VISIBLE
                            playSoundEffect(R.raw.glass_smash)
                            binding.tvStory.text = "油漆桶一陣搖晃，你重重摔在地上！相機重擊地面，發出碎裂聲！\n螢幕出現了永久的裂痕，閃光燈模組受損，瞬間流失了 3 點電量！"
                            binding.btnOption1.visibility = View.GONE
                        } else {
                            binding.tvStory.text = "你不敢再爬上去了，相機已經承受不起另一次撞擊。"
                        }
                    }
                }

                // 摸黑選擇收口袋或是把球球放回桶內
                if (!hasCheckedPaint) {
                    binding.btnOption2.visibility = View.VISIBLE
                    binding.btnOption2.text = "摸黑伸手檢查地上的油漆桶"
                    binding.btnOption2.setOnClickListener {
                        binding.btnOption1.visibility = View.GONE
                        binding.btnOption3.visibility = View.GONE
                        binding.btnOption4.visibility = View.GONE

                        binding.tvStory.text = "你感到手上沾滿了濃稠的液體以及握著一顆不明球體。"

                        //開放拍照或放口袋
                        binding.btnOption1.visibility = View.VISIBLE
                        binding.btnOption2.visibility = View.VISIBLE

                        binding.btnOption1.text = "[將球體收入口袋]"
                        binding.btnOption1.setOnClickListener {
                            hasCheckedPaint = true
                            hasGolfBall = true // 帶在身上就不能觸發真結局
                            investigateCurrentFacing()
                        }

                        binding.btnOption2.text = "[覺得噁心，將球體放回桶內]"
                        binding.btnOption2.setOnClickListener {
                            hasCheckedPaint = true
                            hasGolfBall = false // 放回桶子，球體(眼球)還在桶內！
                            investigateCurrentFacing()
                        }
                    }
                } else {
                    // 我規定要滿足「摸過油漆」+「有筆記與照片」+「球體放回桶內」才能拍攝真結局
                    if (hasNote && hasPhoto && !hasEyePhoto && !hasGolfBall) {
                        binding.btnOption2.visibility = View.VISIBLE
                        binding.btnOption2.text = "開啟閃光燈並拍攝油漆桶內部與放回的球體 (消耗 1 電/1 底)"
                        binding.btnOption2.setOnClickListener {
                            if (filmCount > 0 && useFlash()) {
                                filmCount -= 1
                                updateStatusText()
                                binding.ivScene.setImageResource(R.drawable.eyeball)
                                triggerFilmEffect {
                                    hasCheckedPaint = true
                                    hasEyePhoto = true
                                    scene_TrueEnding()
                                }
                            } else if (filmCount <= 0) {
                                Toast.makeText(this, "底片不足！", Toast.LENGTH_SHORT).show()
                            }
                        }
                    }
                }

                if (!hasPhotographedChains) {
                    binding.btnOption3.visibility = View.VISIBLE
                    binding.btnOption3.text = "拍攝鎖住大門的沉重鐵鍊 (消耗 1 電/1 底)"
                    binding.btnOption3.setOnClickListener {
                        if (filmCount > 0 && useFlash()) {
                            filmCount -= 1
                            hasPhotographedChains = true
                            updateStatusText()
                            triggerFilmEffect {
                                binding.tvStory.text = "照片洗出來了，你放大檢視，發現鐵鍊上似乎刻著微小的詭異經文，但太過模糊無法辨識。除此之外對逃生毫無幫助，你白白浪費了一張底片。"
                                binding.btnOption3.visibility = View.GONE
                            }
                        } else if (filmCount <= 0) {
                            Toast.makeText(this, "底片不足！", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }
            1 -> {
                if (!mirrorBroken) {
                    binding.btnOption1.text = "[拉開覆蓋全身鏡的布並使用閃光燈]"
                    binding.btnOption1.setOnClickListener {
                        if (useFlash()) {
                            binding.ivScene.setImageResource(R.drawable.east)
                            if (hasNote && hasPhoto) {
                                binding.tvStory.text = "鏡子裡映照出你現在的模樣，看起來就是個普通人...\n等等，鏡子裡似乎有一塊微小的陰影？"
                                binding.btnOption1.text = "[離開調查其他面房間]"
                                binding.btnOption2.visibility = View.VISIBLE
                                binding.btnOption2.text = "[再盯久一點]"

                                binding.btnOption1.setOnClickListener { binding.btnOption4.performClick() }
                                binding.btnOption2.setOnClickListener {
                                    binding.tvStory.text = "那塊陰影似乎在蠕動，一股強烈的詭異感襲來！你要打破它嗎？"
                                    binding.btnOption1.text = "[打破鏡子]"
                                    binding.btnOption2.text = "[不打破鏡子]"

                                    binding.btnOption1.setOnClickListener {
                                        mirrorBroken = true
                                        hasKey = true
                                        binding.ivScene.setImageResource(R.drawable.broken_mirror)
                                        playSoundEffect(R.raw.glass_smash)
                                        binding.tvStory.text = "你憤怒地一拳打破了鏡子！鏡子碎裂一地。\n你在碎鏡子後方的夾層裡找到了一把【鐵門鑰匙】！"
                                        binding.btnOption1.visibility = View.GONE
                                        binding.btnOption2.visibility = View.GONE
                                    }
                                    binding.btnOption2.setOnClickListener { binding.btnOption4.performClick() }
                                }
                            } else {
                                binding.tvStory.text = "鏡子裡映照出你現在的模樣，看起來就是個普通人。除此之外什麼也沒有。"
                                binding.btnOption1.visibility = View.GONE
                            }
                        }
                    }

                    binding.btnOption2.visibility = View.VISIBLE
                    binding.btnOption2.text = "拍攝牆壁上的詭異符文 (消耗 1 電/1 底)"
                    binding.btnOption2.setOnClickListener {
                        if (filmCount > 0 && useFlash()) {
                            filmCount -= 1
                            updateStatusText()
                            triggerFilmEffect {
                                binding.tvStory.text = "你拍下了符文。照片顯示這些符文似乎與『眼之崇拜』有關，但對逃脫沒有直接幫助。你浪費了一張底片。"
                                binding.btnOption2.visibility = View.GONE
                            }
                        } else if (filmCount <= 0) {
                            Toast.makeText(this, "底片不足！", Toast.LENGTH_SHORT).show()
                        }
                    }
                } else {
                    binding.btnOption1.text = "摸索地上的碎玻璃"
                    binding.btnOption1.setOnClickListener { binding.tvStory.text = "地上滿是碎玻璃，你已經拿到鑰匙了。" }
                }
            }
            2 -> {
                if (randomBooksRead < 5) {
                    binding.btnOption1.visibility = View.VISIBLE
                    binding.btnOption1.text = if (randomBooksRead >= 3) {
                        "【無視警告】繼續隨機閱讀禁忌典籍 (消耗 1 電量)"
                    } else {
                        "隨機抽一本書閱讀 (消耗 1 電量)"
                    }

                    binding.btnOption1.setOnClickListener {
                        if (useFlash()) {
                            randomBooksRead++

                            //只要讀滿5本直接觸發發瘋結局
                            if (randomBooksRead >= 5) {
                                scene_InsaneEnding()
                            } else {
                                val bookText = when (randomBooksRead) {
                                    1 -> "閃光燈短暫照亮了書頁。「那永恆長眠的並非亡者，在奇妙的萬古之中，即便死亡亦會消逝...」這本書記載著喚醒深海沉睡之神的瘋狂囈語。\n\n你莫名的被書本內文吸引，希望看更多。"
                                    2 -> "閃光燈短暫照亮了書頁。「群星歸位之時，舊日支配者將重返世間...」這頁畫滿了扭曲的星象圖與用鮮血描繪的祭祀法陣。\n\n你莫名的被書本內文吸引，希望看更多。"
                                    3 -> "閃光燈短暫照亮了書頁。書中詳細描繪了食屍鬼的畸形解剖結構，以及如何在墓地中與之進行毛骨悚然的交流儀式...\n你的腦海深處突然傳來一陣尖銳的刺痛與耳鳴。\n\n你莫名的被書本內文吸引，希望看更多。\n\n【警告：過度探究未知，恐將導致理智受損。你確定還要繼續閱讀這些書嗎？】"
                                    4 -> "閃光燈短暫照亮了書頁。「在終北大陸的冰原之下，隱藏著超越人類理解的古老智慧...」文字旁配有令人作嘔的無定形生物插圖。\n\n你莫名的被書本內文吸引，希望看更多。"
                                    else -> ""
                                }

                                binding.tvStory.text = bookText

                                if (randomBooksRead >= 3) {
                                    binding.btnOption1.text = "【無視警告】繼續隨機閱讀禁忌典籍 (消耗 1 電量)"
                                }
                            }
                        }
                    }
                } else {
                    binding.btnOption1.visibility = View.GONE
                }

                if (!hasPhoto) {
                    binding.btnOption2.visibility = View.VISIBLE
                    binding.btnOption2.text = "檢視書架上特別完整的三本書"
                    binding.btnOption2.setOnClickListener {
                        showBookcasePuzzle()
                    }
                } else {
                    binding.btnOption2.visibility = View.GONE
                }
            }
            3 -> {
                if (!hasNote) {
                    binding.btnOption1.text = "[調查桌上的舊筆記本]"
                    binding.btnOption1.setOnClickListener {
                        currentNotebookPage = 1
                        renderNotebookPage()
                    }
                } else {
                    binding.btnOption1.text = "摸索舊書桌"
                    binding.btnOption1.setOnClickListener { binding.tvStory.text = "桌上剩下的只是一些雜物。" }
                }

                if (bookcasePuzzleStep == 0 && !hasPhoto && !hasPhotographedStatue) {
                    binding.btnOption2.visibility = View.VISIBLE
                    binding.btnOption2.text = "拍攝桌上那尊詭異的章魚神像 (消耗 1 電/1 底)"
                    binding.btnOption2.setOnClickListener {
                        if (filmCount > 0 && useFlash()) {
                            filmCount -= 1
                            hasPhotographedStatue = true
                            updateStatusText()
                            triggerFilmEffect {
                                binding.tvStory.text = "你拍下了神像。洗出的照片中，無論你從什麼角度看，神像的眼睛似乎都直勾勾地盯著你。雖然令人毛骨悚然，但對逃脫毫無幫助。"
                                binding.btnOption2.visibility = View.GONE
                            }
                        } else if (filmCount <= 0) {
                            Toast.makeText(this, "底片不足！", Toast.LENGTH_SHORT).show()
                        }
                    }
                }

                if (!hasTriggeredRatTrap) {
                    binding.btnOption3.visibility = View.VISIBLE
                    binding.btnOption3.text = "調查書桌底下傳來的詭異抓撓聲"
                    binding.btnOption3.setOnClickListener {
                        if (useFlash()) {
                            hasTriggeredRatTrap = true
                            flashCount = flashCount / 2
                            updateStatusText()

                            if (!cameraDamaged) {
                                cameraDamaged = true
                                binding.ivCameraCrack.visibility = View.VISIBLE
                            }
                            playSoundEffect(R.raw.glass_smash)

                            binding.tvStory.text = "你用閃光燈照亮桌底，一隻巨大且肉塊外翻的變異老鼠突然尖叫著撲向你的臉！\n\n你驚慌失措地往後倒，相機重重地砸在石磚地上。電池蓋被摔飛，好幾顆電池滾進了深邃的黑暗中... 相機的電量瞬間流失了一大半！"
                            binding.btnOption3.visibility = View.GONE
                        }
                    }
                }
            }
        }
    }

    private fun showBookcasePuzzle() {
        binding.tvStory.text = "這三本書的書脊特別乾淨，似乎經常被前屋主翻閱。你要先抽出哪一本？"

        //修Bug
        binding.btnOption1.visibility = View.VISIBLE
        binding.btnOption2.visibility = View.VISIBLE
        binding.btnOption3.visibility = View.VISIBLE
        binding.btnOption4.visibility = View.VISIBLE

        binding.btnOption1.text = "抽出《血肉的獻祭》"
        binding.btnOption2.text = "抽出《凝視虛無》"
        binding.btnOption3.text = "抽出《深淵的呼喚》"
        binding.btnOption4.text = "[退回]"

        binding.btnOption1.setOnClickListener { checkBookPuzzle(2, "《血肉的獻祭》") }
        binding.btnOption2.setOnClickListener { checkBookPuzzle(3, "《凝視虛無》") }
        binding.btnOption3.setOnClickListener { checkBookPuzzle(1, "《深淵的呼喚》") }
        binding.btnOption4.setOnClickListener { investigateCurrentFacing() }
    }

    private fun checkBookPuzzle(bookId: Int, bookName: String) {
        if (bookcasePuzzleStep == bookId - 1) {
            bookcasePuzzleStep++
            if (bookcasePuzzleStep == 3) {
                binding.ivScene.setImageResource(R.drawable.exhost)
                binding.ivScene.alpha = 1f

                binding.tvStory.text = "你翻開了《凝視虛無》，裡面的內容確實與筆記上的狂亂狀態吻合...\n突然，書頁的夾層裡掉出了一張照片！\n這是【前屋主的照片】！這絕對是關鍵證據！"

                hideAllButtons()
                binding.btnOption4.visibility = View.VISIBLE
                binding.btnOption4.text = "[使用底片翻拍照片並收好] (消耗 1 電/1 底)"

                binding.btnOption4.setOnClickListener {
                    if (filmCount > 0 && useFlash()) {
                        filmCount -= 1
                        hasPhoto = true
                        updateStatusText()
                        triggerFilmEffect {

                            investigateCurrentFacing()
                        }
                    } else if (filmCount <= 0) {
                        Toast.makeText(this, "底片不足！", Toast.LENGTH_SHORT).show()
                    }
                }
            } else {
                binding.tvStory.text = "你翻開了$bookName，裡面的內容似乎與筆記上的精神狀態吻合... 接下來要抽哪一本？"
            }
        } else {
            bookcasePuzzleStep = 0
            binding.tvStory.text = "你抽出了$bookName，但裡面的內容雜亂無章，跟《眼之教義》一樣毫無意義... 你覺得抽書的順序可能錯了。"
        }
    }
    private fun renderNotebookPage() {
        setMaskBrightness(true)
        binding.ivScene.setImageResource(R.drawable.notebook_bg)

        when (currentNotebookPage) {
            1 -> binding.tvStory.text = "【筆記本：封面】\n沾滿灰塵與暗紅污漬的皮革筆記本。"
            2 -> binding.tvStory.text = "【筆記本：第 1 頁】\n「買下這棟木屋作為度假小屋，真的是意外之喜。特別是那天雷雨交加時，我發現了這個隱蔽的地下室。地下室有個大書櫃，還有一面被白布蓋著的普通全身鏡。我很好奇前屋主到底是什麼職業？我從書櫃抽出了第一本書《深淵的呼喚》，內容深深吸引了我... 我把書桌搬到這面平凡的鏡子正對面，這樣我一邊研讀，一抬頭就能看見它。」"
            3 -> binding.tvStory.text = "【筆記本：第 2 頁】\n「房間沒有燈泡，我用那桶『紅色油漆』裡的溫熱材料自製了蠟燭當桌燈。當我坐在桌前閱讀《血肉的獻祭》時，開始理解這個空間的真理... 火焰的微光映照在鏡子上，剛好照亮我看向鏡子的雙眼... 我特別喜歡自己的雙眼在鏡內發亮的樣子。但最近，倒影裡總覺得有什麼在蠕動...」"
            4 -> binding.tvStory.text = "【筆記本：第 3 頁】\n「第三夜，我終於翻開了那本《凝視虛無》。我懂了... 那不是反光，那是無數顆正在注視著我的完美眼球... 太美了... 我一天比一天更迷戀鏡子裡的視線，我必須一直看著它們...」"
            5 -> binding.tvStory.text = "【筆記本：第 4 頁 (最後一頁)】\n「祂們在看著我... 祂們需要我... 眼睛... 更多的眼睛...」\n\n(筆記的最後一頁，竟然貼著一張這本『筆記本自己』的照片！這不合邏輯的畫面讓你感到一陣暈眩。)"
        }

        binding.btnOption1.visibility = View.VISIBLE
        binding.btnOption2.visibility = View.VISIBLE
        binding.btnOption3.visibility = View.VISIBLE
        binding.btnOption4.visibility = View.VISIBLE

        binding.btnOption1.text = if (currentNotebookPage > 1) "[往前翻一頁] (消耗 1 電量)" else ""
        binding.btnOption1.visibility = if (currentNotebookPage > 1) View.VISIBLE else View.GONE
        binding.btnOption1.setOnClickListener { if (useFlash()) { currentNotebookPage--; renderNotebookPage() } }

        binding.btnOption2.text = if (currentNotebookPage < maxNotebookPage) "[往後翻一頁] (消耗 1 電量)" else ""
        binding.btnOption2.visibility = if (currentNotebookPage < maxNotebookPage) View.VISIBLE else View.GONE
        binding.btnOption2.setOnClickListener { if (useFlash()) { currentNotebookPage++; renderNotebookPage() } }

        binding.btnOption3.text = "[拍攝目前這頁] (消耗 1 電/1 底)"
        binding.btnOption3.setOnClickListener {
            if (filmCount > 0 && useFlash()) {
                filmCount -= 1
                updateStatusText()
                triggerFilmEffect {
                    if (currentNotebookPage == maxNotebookPage) {
                        hasNote = true
                        Toast.makeText(this, "成功拍下最後一頁筆記！", Toast.LENGTH_SHORT).show()
                        binding.btnOption4.performClick()
                    } else {
                        Toast.makeText(this, "你拍下了這頁，但似乎沒什麼關鍵用處...", Toast.LENGTH_SHORT).show()
                    }
                }
            } else if (filmCount <= 0) {
                Toast.makeText(this, "底片不足！", Toast.LENGTH_SHORT).show()
            }
        }

        binding.btnOption4.text = "[放下筆記本，停止調查]"
        binding.btnOption4.setOnClickListener { investigateCurrentFacing() }
    }


    // 結局和音效
    private fun scene_BadEnding() {
        bgmPlayer?.stop()
        setMaskBrightness(false)
        binding.ivScene.setImageResource(android.R.color.black)
        typeText("閃光燈再也無法亮起。\n\n你在極度的黑暗中迷失了方向。恐懼如同潮水般湧來，你感到後腦勺遭遇了重擊...\n\n你再次像剛進入這間屋子一樣陷入昏迷，但這次你有預感，你或許再也不會像第一次一樣幸運地醒來了。", "#AAAAAA", 50L) {
            binding.btnOption1.visibility = View.VISIBLE
            binding.btnOption1.text = "[陷入黑暗]"
            binding.btnOption1.setOnClickListener { returnToMainMenu() }
        }
    }

    private fun scene_InsaneEnding() {
        bgmPlayer?.stop()
        // 心跳聲
        heartbeatPlayer = MediaPlayer.create(this, R.raw.heartbeat)
        heartbeatPlayer?.isLooping = true
        heartbeatPlayer?.start()

        setMaskBrightness(false)
        binding.ivScene.setImageResource(android.R.color.black)

        binding.vScreenBlood.visibility = View.VISIBLE
        binding.vScreenBlood.animate().alpha(1f).setDuration(4000).start()

        typeText("你決定靜下心來，翻閱書櫃上一本又一本的禁忌典籍...\n\n隨著時間流逝，書上那些原本毫無邏輯的扭曲符號，在你眼中竟然開始拼湊出某種『真理』。\n你的頭痛欲裂，四周的空間開始扭曲，牆壁上滲出黏稠的血肉，無數不可名狀的觸手與眼球在黑暗中沸騰！\n\n你終於理解了這個世界的『真相』，也理解了前屋主的狂熱...\n但人類脆弱的大腦根本無法承受這份知識。你的理智瞬間歸零，嘴角揚起詭異的微笑，加入了黑暗中的狂歡...", "#CC66FF", 50L) {
            binding.btnOption1.visibility = View.VISIBLE
            binding.btnOption1.text = "[擁抱真理]"
            binding.btnOption1.setOnClickListener { returnToMainMenu() }
        }
    }

    private fun scene_NormalEnding() {
        bgmPlayer?.stop()
        setMaskBrightness(false)
        binding.ivScene.setImageResource(android.R.color.black)

        val extraText = if (hasGolfBall) {
            "\n\n事後你摸了摸口袋，發現你在地下室蠟桶裡拿出的那顆『球』，竟然只是一顆沾滿紅色油漆的【高爾夫球】。\n或許前屋主度假時有打高爾夫的習慣才遺留在這的... 這一切，難道只是你緊繃神經下的幻覺嗎？"
        } else ""

        typeText("你用鑰匙順利打開了大鎖！\n推開沉重的鐵門，你頭也不回地逃出這棟詭異的屋子。你順利帶著相片與筆記，一路開車到有訊號的地方，聯絡到了同仁的支援。\n當你終於喘口氣時，你在回憶中突然發現一件毛骨悚然的事... 你對著大門拍攝時，碎掉的鏡子反射出的你的衣物，似乎有一瞬間和前屋主照片裡的衣著一模一樣...$extraText\n\n【結局：逃出生天？】", "#FFFFFF", 40L) {
            binding.btnOption1.visibility = View.VISIBLE
            binding.btnOption1.text = "[返回主畫面]"
            binding.btnOption1.setOnClickListener { returnToMainMenu() }
        }
    }

    private fun scene_TrueEnding() {
        currentFacing = 1
        setMaskBrightness(false)

        bgmPlayer?.stop()
        // 真結局心跳聲
        heartbeatPlayer = MediaPlayer.create(this, R.raw.heartbeat)
        heartbeatPlayer?.isLooping = true
        heartbeatPlayer?.start()

        typeText("你想起筆記上的內容，用手觸碰油漆桶... 裡面裝的根本不是油漆，而是【溫熱的蠟】！\n相機沖洗出的畫面，是一顆正在直勾勾盯著你的血淋淋眼球！\n\n你開始瘋狂懷疑自己是否有眼球！你迫切地需要確認！你跌跌撞撞地衝向那面鏡子，扯下白布...\n你本來慶幸自己長得很正常，但下一秒，鏡子裡你的雙眼處出現了兩個深不見底的黑洞！你發現自己的上半身與下半身像是被粗劣縫合拼接的！\n\n你顫抖著拿出政府職員身分證，照片上的人... 眼睛處也變成了兩個黑洞！", "#FF6666", 60L) {

            binding.layoutIdCardOverlay.visibility = View.VISIBLE
            binding.layoutIdCardOverlay.alpha = 0f

            binding.layoutIdCardOverlay.animate().alpha(1f).setDuration(1500).withEndAction {

                binding.ivEndingPhoto.animate()
                    .scaleX(2.5f).scaleY(0.2f).rotation(720f).alpha(0.1f)
                    .setDuration(3000)
                    .withEndAction {
                        binding.ivEndingPhoto.animate()
                            .scaleX(0.1f).scaleY(3.5f).rotation(-360f).alpha(0f)
                            .setDuration(3000)
                            .start()
                    }.start()

                binding.vScreenBlood.animate().alpha(1f).setDuration(6000).withEndAction {

                    binding.layoutIdCardOverlay.animate().alpha(0f).setDuration(1500).withEndAction {
                        binding.layoutIdCardOverlay.visibility = View.GONE

                        typeText("鏡子漸漸映照不出任何東西，只剩下一片純粹的死寂與黑暗...", "#FF6666", 60L) {
                            binding.btnOption1.visibility = View.VISIBLE
                            binding.btnOption2.visibility = View.VISIBLE
                            binding.btnOption1.text = "[打破鏡子]"
                            binding.btnOption2.text = "[打破鏡子]"

                            val loopAction = View.OnClickListener {
                                playSoundEffect(R.raw.glass_smash) // 碎鏡聲
                                binding.ivScene.setImageResource(android.R.color.black)
                                typeText("鏡子破碎的瞬間，一切歸於虛無...\n\n這一次，你又會是誰呢？", "#AAAAAA", 80L) {
                                    binding.btnOption1.visibility = View.VISIBLE
                                    binding.btnOption1.text = "[陷入無盡輪迴]"
                                    binding.btnOption2.visibility = View.GONE
                                    binding.btnOption1.setOnClickListener { returnToMainMenu() }
                                }
                            }
                            binding.btnOption1.setOnClickListener(loopAction)
                            binding.btnOption2.setOnClickListener(loopAction)
                        }
                    }.start()
                }.start()
            }.start()
        }
    }
}