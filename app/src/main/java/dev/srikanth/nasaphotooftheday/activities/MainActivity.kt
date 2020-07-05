package dev.srikanth.nasaphotooftheday.activities

import android.app.DatePickerDialog
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.text.method.ScrollingMovementMethod
import android.util.Log
import android.view.View
import android.widget.DatePicker
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.youtube.player.YouTubeInitializationResult
import com.google.android.youtube.player.YouTubeThumbnailLoader
import com.google.android.youtube.player.YouTubeThumbnailView
import com.squareup.picasso.Callback
import com.squareup.picasso.MemoryPolicy
import com.squareup.picasso.Picasso
import dev.srikanth.nasaphotooftheday.R
import dev.srikanth.nasaphotooftheday.listeners.APODCallback
import dev.srikanth.nasaphotooftheday.network.APODHttpRequest
import dev.srikanth.nasaphotooftheday.pojos.APODResponse
import dev.srikanth.nasaphotooftheday.utils.CommonUtils
import dev.srikanth.nasaphotooftheday.utils.Constants
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*


class MainActivity : AppCompatActivity(),
    com.squareup.picasso.Target,
    DatePickerDialog.OnDateSetListener, Callback, YouTubeThumbnailView.OnInitializedListener,
    YouTubeThumbnailLoader.OnThumbnailLoadedListener, APODCallback {

    private val calendar = Calendar.getInstance()
    private var imageHdUrl: String = ""
    private var imageUrl: String = ""
    private var fullScreen: Boolean = false
    private var mediaType: String? = ""
    private var loadingStatus:Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        apod_desc.movementMethod = ScrollingMovementMethod()
        if (CommonUtils().checkInternetConnection(this))
            sendGet()
        else
            CommonUtils().showAlertDialog(
                this,
                "No Internet Connection",
                "Retry",
                DialogInterface.OnClickListener { dialog, which -> retryClicked() })
    }

    private fun retryClicked() {
        if (CommonUtils().checkInternetConnection(this)) {
            Log.e("loadingstatus","$loadingStatus")
            when(loadingStatus){
                0 -> sendGet()
                1 -> Picasso.get()
                    .load(imageUrl)
                    .memoryPolicy(MemoryPolicy.NO_CACHE,MemoryPolicy.NO_STORE)
                    .into(apod_image, this)
                2 -> Picasso.get()
                    .load(imageHdUrl)
                    .placeholder(apod_image.drawable)
                    .memoryPolicy(MemoryPolicy.NO_STORE,MemoryPolicy.NO_CACHE)
                    .into(this)
            }
        } else
            CommonUtils().showAlertDialog(
                this,
                "No Internet Connection",
                "Retry",
                DialogInterface.OnClickListener { dialog, which -> retryClicked() })
    }

    private fun getThumbnailFromYoutubeVideo(youtubeThumbnail: String) {

        thumbnail.tag = youtubeThumbnail
        thumbnail.initialize(Constants().youtubeapikey, this@MainActivity)
    }

    fun sendGet() {

        loadingStatus = 1

        Picasso.get().cancelRequest(apod_image)
        Picasso.get().cancelRequest(this)
        apod_image.setImageBitmap(null)
        thumbnail.setImageBitmap(null)
        hideTopBottonLayouts()

        apod_desc.text = ""
        apod_desc.scrollTo(0, 0)
        apod_title.text = ""

        progress_circular.visibility = View.VISIBLE
        val format = Constants().dateFormat.format(calendar.time)

        APODHttpRequest().init(this,Constants().apodUrlWithDate+format,this)
    }

    private fun updateUI(response: APODResponse) {

        mediaType = response.mediaType

        apod_title.text = response.title
        apod_desc.text = response.explanation

        setPlayIcon(response.mediaType)

        var iurl: String = ""
        if ("video" == response.mediaType) {

            image.visibility = View.GONE
            video.visibility = View.VISIBLE
            imageHdUrl = CommonUtils().getYoutubeVideoID(response.url)
            getThumbnailFromYoutubeVideo(imageHdUrl)

        } else if ("image" == response.mediaType) {
            video.visibility = View.GONE
            image.visibility = View.VISIBLE

            iurl = if (response.url != null) {
                response.url!!
            } else response.hdurl!!

            imageHdUrl = if (response.hdurl == null) {
                response.url.toString()
            } else {
                response.hdurl!!
            }

            imageUrl = iurl

            Log.e("url", imageUrl)
            Log.e("hdurl", imageHdUrl)

            Picasso.get()
                .load(iurl)
                .memoryPolicy(MemoryPolicy.NO_CACHE,MemoryPolicy.NO_STORE)
                .into(apod_image, this)

        } else {
            CommonUtils().showAlertDialog(
                this,
                "Unspecified media type",
                "Ok",
                null
            )
        }

    }

    private fun setPlayIcon(mediaType: String?) {
        if ("video" == mediaType) {
            apod_action.setImageDrawable(getDrawable(R.drawable.ic_play_arrow_black_24dp))
        } else {
            apod_action.setImageDrawable(getDrawable(R.drawable.ic_zoom_black_24dp))
        }
    }

    override fun onPrepareLoad(placeHolderDrawable: Drawable?) {

    }

    override fun onBitmapFailed(e: Exception?, errorDrawable: Drawable?) {
//        progress_circular.visibility = View.INVISIBLE
        CommonUtils().showAlertDialog(
            this,
            "image loading error",
            "Ok",
            null
        )
    }

    override fun onBitmapLoaded(bitmap: Bitmap?, from: Picasso.LoadedFrom?) {
//        progress_circular.visibility = View.INVISIBLE
        loadingStatus = 3
        if (bitmap != null) {
            apod_image.setImageBitmap(bitmap)
//            apod_image.scaleType = ImageView.ScaleType.CENTER_CROP
        } else {
            CommonUtils().showAlertDialog(
                this,
                "image not available",
                "Ok",
                null
            )
        }
    }

    fun dateClicked(view: View) {
        val dpd = DatePickerDialog(
            this,
            this,
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        )
        dpd.datePicker.maxDate = Date().time
        dpd.show()
    }

    override fun onDateSet(view: DatePicker?, year: Int, month: Int, dayOfMonth: Int) {

        if (!(calendar.get(Calendar.YEAR) == year && calendar.get(Calendar.MONTH) == month && calendar.get(
                Calendar.DAY_OF_MONTH
            ) == dayOfMonth)
        ) {
            calendar.set(year, month, dayOfMonth)
            sendGet()
        }
    }

    override fun onSuccess() {
        progress_circular.visibility=View.INVISIBLE
        loadingStatus = 2
        Picasso.get()
            .load(imageHdUrl)
            .placeholder(apod_image.drawable)
            .memoryPolicy(MemoryPolicy.NO_STORE,MemoryPolicy.NO_CACHE)
            .into(this)
    }

    override fun onError(e: java.lang.Exception?) {
        progress_circular.visibility = View.INVISIBLE
        CommonUtils().showAlertDialog(
            this,
            "Failed to load image",
            "Ok",
            null
        )
    }

    override fun onInitializationSuccess(p0: YouTubeThumbnailView?, p1: YouTubeThumbnailLoader?) {
        p1?.setOnThumbnailLoadedListener(this)
        if (p0 != null) {
            Log.e("id", imageHdUrl)
            p1?.setVideo(imageHdUrl)
        } else {
            progress_circular.visibility = View.INVISIBLE
        }
    }

    override fun onInitializationFailure(
        p0: YouTubeThumbnailView?,
        p1: YouTubeInitializationResult?
    ) {
        progress_circular.visibility = View.INVISIBLE
        CommonUtils().showAlertDialog(
            this,
            "Thumbnail Initialization Failure ",
            "Ok",
            null
        )
    }

    override fun onThumbnailLoaded(p0: YouTubeThumbnailView?, p1: String?) {
        progress_circular.visibility = View.INVISIBLE
    }

    override fun onThumbnailError(
        p0: YouTubeThumbnailView?,
        p1: YouTubeThumbnailLoader.ErrorReason?
    ) {
        progress_circular.visibility = View.INVISIBLE
        CommonUtils().showAlertDialog(
            this,
            "Thumbnail Error ",
            "Ok",
            null
        )
    }

    fun actionClicked(view: View) {
        if ("image" == mediaType) {
            fullScreen = true
            hideTopBottonLayouts()
            Toast.makeText(this,"Press Back to exit full screen",Toast.LENGTH_SHORT).show()
        } else if ("video" == mediaType) {
            val inte: Intent = Intent(this, PlayActivity::class.java)
            inte.putExtra(Constants().videoId, imageHdUrl)
            startActivity(inte)
        }
    }

    override fun onBackPressed() {
        if (fullScreen) {
            fullScreen = false
            showTopBottomLayouts()
        } else
            super.onBackPressed()
    }

    private fun showTopBottomLayouts() {
        top_layout.visibility = View.VISIBLE
        bottom_layout.visibility = View.VISIBLE
    }

    private fun hideTopBottonLayouts() {
        top_layout.visibility = View.GONE
        bottom_layout.visibility = View.GONE
    }

    override fun onResume() {
        if (!CommonUtils().checkInternetConnection(this))
            CommonUtils().showAlertDialog(
                this,
                "No Internet Connection",
                "Retry",
                DialogInterface.OnClickListener { dialog, which -> retryClicked() })
        super.onResume()
    }

    override fun apodSuccessCallback(apodResponse: APODResponse) {
        Log.e("response", apodResponse.toString())
        showTopBottomLayouts()
        updateUI(apodResponse)
    }

    override fun apodFailureCallback(string: String) {
        progress_circular.visibility = View.INVISIBLE
        mediaType = ""
        showTopBottomLayouts()
        CommonUtils().showAlertDialog(
            this,
            string,
            "Ok",
            null
        )
    }

}
