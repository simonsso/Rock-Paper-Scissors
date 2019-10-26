package net.thesimson.rockpaperscissors

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.view.View
import android.view.animation.AlphaAnimation
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import kotlinx.android.synthetic.main.activity_fullscreen.*

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
class FullscreenActivity : AppCompatActivity() {
    var watchDog = 0
    private val mHideHandler = Handler()
    private val mHidePart2Runnable = Runnable {
        // Delayed removal of status and navigation bar

        // Note that some of these constants are new as of API 16 (Jelly Bean)
        // and API 19 (KitKat). It is safe to use them, as they are inlined
        // at compile-time and do nothing on earlier devices.
        fullscreen_content.systemUiVisibility =
            View.SYSTEM_UI_FLAG_LOW_PROFILE or
                    View.SYSTEM_UI_FLAG_FULLSCREEN or
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE or
                    View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY or
                    View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION or
                    View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
    }
    private val mShowPart2Runnable = Runnable {
        // Delayed display of UI elements
        supportActionBar?.show()
     //   fullscreen_content_controls.visibility = View.VISIBLE
    }
    private var mVisible: Boolean = false
    private val mHideRunnable = Runnable { hide() }
    /**
     * Touch listener to use for in-layout UI controls to delay hiding the
     * system UI. This is to prevent the jarring behavior of controls going away
     * while interacting with activity UI.
     */
    private val mDelayHideTouchListener = View.OnTouchListener { _, _ ->
        if (AUTO_HIDE) {
            delayedHide(AUTO_HIDE_DELAY_MILLIS)
        }
        false
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_fullscreen)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val shakeAnim = AnimationUtils.loadAnimation(this, R.anim.milkshake);
        mVisible = true

        val fadeinAnim = AlphaAnimation(0.0f,1.0f)
        fadeinAnim.duration = 10000;
        fadeinAnim.repeatMode = Animation.REVERSE
        fadeinAnim.repeatCount = Animation.INFINITE

        // Set up the user interaction to manually show or hide the system UI.
        fullscreen_content.setOnClickListener { toggle() }

        // Upon interacting with UI controls, delay any scheduled hide()
        // operations to prevent the jarring behavior of controls going away
        // while interacting with the UI.
        rock.setOnTouchListener(mDelayHideTouchListener)
        paper.setOnTouchListener(mDelayHideTouchListener)
        scissors.setOnTouchListener(mDelayHideTouchListener)

        rock.setOnClickListener{
            rock.animate()
            fullscreen_content.setText("You Played Rock")
            playimage.setImageResource(resources.getIdentifier("rock", "drawable", packageName) )
            playgame(1)

        }
        paper.setOnClickListener{
            fullscreen_content.setText("You Played Paper")
            playimage.setImageResource(resources.getIdentifier("paper", "drawable", packageName) )
            playgame(2 )
        }
        scissors.setOnClickListener{
            fullscreen_content.setText("You played Scissors")
            playimage.setImageResource(resources.getIdentifier("scissors", "drawable", packageName) )
            playgame(3)
        }

        supportActionBar?.setHomeButtonEnabled(true)
        Thread{
            while (true){
                Thread.sleep(500)
                this@FullscreenActivity.runOnUiThread {
                    watchDog +=1
                    when  {
                        (watchDog % 20) == 0->{
                             paper.startAnimation(shakeAnim)
                             scissors.startAnimation(shakeAnim)
                             rock.startAnimation(shakeAnim)
                        }
                        watchDog == 17 ->   {
                            fullscreen_content.setText("Get ready player one!")
                            playimage.setImageResource(
                                resources.getIdentifier(
                                    "iconmonstr",
                                    "drawable",
                                    packageName
                                )
                            )
                            playimage.startAnimation(fadeinAnim)
                        }
                    }
                }
            }
        }.start()
    }

    override fun onPostCreate(savedInstanceState: Bundle?) {
        super.onPostCreate(savedInstanceState)

        // Trigger the initial hide() shortly after the activity has been
        // created, to briefly hint to the user that UI controls
        // are available.
        delayedHide(100)
    }

    private fun toggle() {
        if (mVisible) {
            hide()
        } else {
            show()
        }
    }

    private fun hide() {
        // Hide UI first
        supportActionBar?.hide()
//        fullscreen_content_controls.visibility = View.GONE
        mVisible = false

        // Schedule a runnable to remove the status and navigation bar after a delay
        mHideHandler.removeCallbacks(mShowPart2Runnable)
        mHideHandler.postDelayed(mHidePart2Runnable, UI_ANIMATION_DELAY.toLong())
    }

    private fun show() {
        // Show the system bar
        fullscreen_content.systemUiVisibility =
            View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or
                    View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
        mVisible = true

        // Schedule a runnable to display UI elements after a delay
        mHideHandler.removeCallbacks(mHidePart2Runnable)
        mHideHandler.postDelayed(mShowPart2Runnable, UI_ANIMATION_DELAY.toLong())
    }

    /**
     * Schedules a call to hide() in [delayMillis], canceling any
     * previously scheduled calls.
     */
    private fun delayedHide(delayMillis: Int) {
        mHideHandler.removeCallbacks(mHideRunnable)
        mHideHandler.postDelayed(mHideRunnable, delayMillis.toLong())
    }

    private fun playgame(youplay:Int) {
        rock.visibility = View.INVISIBLE
        paper.visibility = View.INVISIBLE
        scissors.visibility = View.INVISIBLE
        val fadeOutAnim = AlphaAnimation(1.0f,0.6f)
        fadeOutAnim.duration= 1500

        val fadeInAnim = AlphaAnimation(0.6f,1.0f)
        fadeInAnim.duration= 1000



        playimage.setImageResource(resources.getIdentifier("iconmonstr", "drawable", packageName) )
        watchDog = 0
        Thread {
            this@FullscreenActivity.runOnUiThread {
                playimage.clearAnimation()
            }
            Thread.sleep(1000)
            this@FullscreenActivity.runOnUiThread {
                fullscreen_content.setText("I Play...")
                playimage.setImageResource(resources.getIdentifier("iconmonstr", "drawable", packageName))
            }
            Thread.sleep(2000)
            val iplay = (1..3).random()
            this@FullscreenActivity.runOnUiThread {
                when (iplay) {
                    1 -> {
                        fullscreen_content.setText("I Play Rock")
                        playimage.setImageResource(resources.getIdentifier("rock", "drawable", packageName) )

                    }
                    2 -> {
                        fullscreen_content.setText("I Play Paper")
                        playimage.setImageResource(resources.getIdentifier("paper", "drawable", packageName) )

                    }
                    3 -> {
                        fullscreen_content.setText("I Play Scissors")
                        playimage.setImageResource(resources.getIdentifier("scissors", "drawable", packageName) )

                    }
                    else -> {
                        playimage.setImageResource(resources.getIdentifier("sample/backgrounds/scenic", "tools", packageName) )
                    }
                }
                fullscreen_content.startAnimation(fadeOutAnim)
            }
            Thread.sleep(1500)
            // Game logic
            val youwin = ((iplay+1) %3 == youplay % 3 )
            val iwin =   (iplay %3 == (youplay+1) % 3 )
            this@FullscreenActivity.runOnUiThread {
                fullscreen_content.startAnimation(fadeInAnim)
                when {
                    iwin -> {
                        fullscreen_content.setText("I Win")
                    }
                    youwin -> {
                        fullscreen_content.setText("You Win")
                    }
                    else -> {
                        fullscreen_content.setText("It's a Tie")
                    }
                }
                rock.visibility = View.VISIBLE
                paper.visibility = View.VISIBLE
                scissors.visibility = View.VISIBLE
            }
        }.start()
    }

    companion object {
        /**
         * Whether or not the system UI should be auto-hidden after
         * [AUTO_HIDE_DELAY_MILLIS] milliseconds.
         */
        private val AUTO_HIDE = true

        /**
         * If [AUTO_HIDE] is set, the number of milliseconds to wait after
         * user interaction before hiding the system UI.
         */
        private val AUTO_HIDE_DELAY_MILLIS = 3000

        /**
         * Some older devices needs a small delay between UI widget updates
         * and a change of the status and navigation bar.
         */
        private val UI_ANIMATION_DELAY = 300
    }
}
