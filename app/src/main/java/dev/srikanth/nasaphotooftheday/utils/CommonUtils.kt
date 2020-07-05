package dev.srikanth.nasaphotooftheday.utils

import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.net.ConnectivityManager
import android.net.NetworkInfo

class CommonUtils {

    fun getYoutubeVideoID(url: String?): String {
        val split = url!!.split("/")

        var imageurl = ""
//        val url1: String = "http://img.youtube.com/vi/"
//        val url2 = "/0.jpg"

        if (split.size == 5) {
            val split1 = split[4].split("?")
            if (split1.isNotEmpty()) {
                imageurl = split1[0]
            }
        }
        return imageurl
    }

    fun checkInternetConnection(context : Context):Boolean {
        val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetwork: NetworkInfo? = cm.activeNetworkInfo
        return activeNetwork?.isConnectedOrConnecting == true
    }

    fun showAlertDialog(
        context: Context,
        message:String,
        buttonText:String, onClickListener: DialogInterface.OnClickListener?
    ){
        val alertDialog : AlertDialog = AlertDialog.Builder(context)
            .setTitle("Alert")
            .setMessage(message)
            .setPositiveButton(buttonText,onClickListener)
            .create()
        alertDialog.setCanceledOnTouchOutside(false)
        alertDialog.show()

    }


}