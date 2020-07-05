package dev.srikanth.nasaphotooftheday.listeners

import dev.srikanth.nasaphotooftheday.pojos.APODResponse

interface APODCallback {
    fun apodSuccessCallback(apodResponse: APODResponse)
    fun apodFailureCallback(string: String)
}