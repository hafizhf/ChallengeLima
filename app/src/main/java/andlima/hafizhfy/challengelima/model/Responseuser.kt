package andlima.hafizhfy.challengelima.model


import com.google.gson.annotations.SerializedName

data class Responseuser(
    @SerializedName("address")
    val address: Any,
    @SerializedName("complete_name")
    val completeName: Any,
    @SerializedName("dateofbirth")
    val dateofbirth: Any,
    @SerializedName("email")
    val email: String,
    @SerializedName("password")
    val password: String,
    @SerializedName("username")
    val username: String
)