package dev.srikanth.nasaphotooftheday.pojos;
import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.io.Serializable

class APODResponse : Serializable, Parcelable {
    @SerializedName("copyright")
    @Expose
    var copyright: String? = null
    @SerializedName("date")
    @Expose
    var date: String? = null
    @SerializedName("explanation")
    @Expose
    var explanation: String? = null
    @SerializedName("hdurl")
    @Expose
    var hdurl: String? = null
    @SerializedName("media_type")
    @Expose
    var mediaType: String? = null
    @SerializedName("service_version")
    @Expose
    var serviceVersion: String? = null
    @SerializedName("title")
    @Expose
    var title: String? = null
    @SerializedName("url")
    @Expose
    var url: String? = null

    protected constructor(`in`: Parcel) {
        copyright =
            `in`.readValue(String::class.java.classLoader) as String?
        date = `in`.readValue(String::class.java.classLoader) as String?
        explanation =
            `in`.readValue(String::class.java.classLoader) as String?
        hdurl =
            `in`.readValue(String::class.java.classLoader) as String?
        mediaType =
            `in`.readValue(String::class.java.classLoader) as String?
        serviceVersion =
            `in`.readValue(String::class.java.classLoader) as String?
        title =
            `in`.readValue(String::class.java.classLoader) as String?
        url = `in`.readValue(String::class.java.classLoader) as String?
    }

    constructor() {}

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeValue(copyright)
        dest.writeValue(date)
        dest.writeValue(explanation)
        dest.writeValue(hdurl)
        dest.writeValue(mediaType)
        dest.writeValue(serviceVersion)
        dest.writeValue(title)
        dest.writeValue(url)
    }

    override fun describeContents(): Int {
        return 0
    }

    override fun toString(): String {
        return "APODResponse{" +
                "copyright='" + copyright + '\'' +
                ", date='" + date + '\'' +
                ", explanation='" + explanation + '\'' +
                ", hdurl='" + hdurl + '\'' +
                ", mediaType='" + mediaType + '\'' +
                ", serviceVersion='" + serviceVersion + '\'' +
                ", title='" + title + '\'' +
                ", url='" + url + '\'' +
                '}'
    }

    companion object {
        @JvmField val CREATOR: Parcelable.Creator<APODResponse?> =
            object : Parcelable.Creator<APODResponse?> {
                override fun createFromParcel(`in`: Parcel): APODResponse? {
                    return APODResponse(`in`)
                }

                override fun newArray(size: Int): Array<APODResponse?> {
                    return arrayOfNulls(size)
                }
            }
        private const val serialVersionUID = 4831731230038252403L
    }
}