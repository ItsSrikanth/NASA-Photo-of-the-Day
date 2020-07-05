package dev.srikanth.nasaphotooftheday.network

import android.content.Context
import android.util.Log
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.VolleyError
import com.android.volley.toolbox.Volley
import com.google.gson.Gson
import dev.srikanth.nasaphotooftheday.listeners.APODCallback
import dev.srikanth.nasaphotooftheday.pojos.APODResponse

class APODHttpRequest : Response.Listener<String>, Response.ErrorListener {

    private var apodCallback: APODCallback? = null

    fun init(context: Context, url:String ,apodCallback: APODCallback){

        this.apodCallback = apodCallback

        val newRequestQueue = Volley.newRequestQueue(context)

        val gsonRequest = HttpRequest(
            url,
            null,
            this,
            this,
            Request.Method.GET
        )

        newRequestQueue.add(gsonRequest)
    }

    override fun onResponse(response: String?) {
        Log.e("response",""+response)
        val fromJson = Gson().fromJson(response, APODResponse::class.java)
        apodCallback!!.apodSuccessCallback(fromJson)
    }

    override fun onErrorResponse(error: VolleyError?) {
        if (error?.networkResponse != null) {
            Log.e("response", String(error.networkResponse.data))
            apodCallback!!.apodFailureCallback(String(error.networkResponse.data))
        }else{
            apodCallback!!.apodFailureCallback("Null error response")
        }
    }
}