package andlima.hafizhfy.challengelima.model


import com.google.gson.annotations.SerializedName

data class LoginThing(
    @SerializedName("code")
    val code: Int,
    @SerializedName("message")
    val message: String,
    @SerializedName("responseuser")
    val responseuser: Responseuser
)