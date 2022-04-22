package andlima.hafizhfy.challengelima.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class RequestLogin(
    @SerializedName("email")
    val email : String,
    @SerializedName("password")
    val password : String
) : Parcelable
