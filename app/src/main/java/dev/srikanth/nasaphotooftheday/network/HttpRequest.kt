package dev.srikanth.nasaphotooftheday.network

import com.android.volley.NetworkResponse
import com.android.volley.ParseError
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.HttpHeaderParser
import java.io.UnsupportedEncodingException
import java.nio.charset.Charset


class HttpRequest(
    url: String,
    private val headers: MutableMap<String, String>?,
    private val listener: Response.Listener<String>,
    errorListener: Response.ErrorListener,
    method: Int
) : Request<String>(method, url, errorListener) {

    override fun getHeaders(): MutableMap<String, String> = headers ?: super.getHeaders()

    override fun deliverResponse(response: String) = listener.onResponse(response)

    override fun parseNetworkResponse(response: NetworkResponse?): Response<String> {
        return try {
            val json = String(
                response?.data ?: ByteArray(0),
                Charset.forName(HttpHeaderParser.parseCharset(response?.headers)))
            Response.success(
                json,
                HttpHeaderParser.parseCacheHeaders(response))
        } catch (e: UnsupportedEncodingException) {
            Response.error(ParseError(e))
        }
    }
}