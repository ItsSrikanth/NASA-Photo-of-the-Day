package dev.srikanth.nasaphotooftheday.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.google.android.youtube.player.YouTubeBaseActivity
import com.google.android.youtube.player.YouTubeInitializationResult
import com.google.android.youtube.player.YouTubePlayer
import dev.srikanth.nasaphotooftheday.R
import dev.srikanth.nasaphotooftheday.utils.Constants
import kotlinx.android.synthetic.main.activity_play.*

class PlayActivity : YouTubeBaseActivity(), YouTubePlayer.OnInitializedListener {

    var videoId :String? = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_play)

        videoId = intent.getStringExtra(Constants().videoId)

        player.initialize(Constants().youtubeapikey, this@PlayActivity)
    }

    override fun onInitializationSuccess(
        p0: YouTubePlayer.Provider?,
        p1: YouTubePlayer?,
        wasRestored: Boolean
    ) {
        if (!wasRestored) {
            p1?.loadVideo(videoId)
        }
        p1?.play()
    }

    override fun onInitializationFailure(
        p0: YouTubePlayer.Provider?,
        p1: YouTubeInitializationResult?
    ) {
        Toast.makeText(this, "Initialization Failure", Toast.LENGTH_SHORT).show()
    }

}
