package andlima.hafizhfy.challengelima.network

import andlima.hafizhfy.challengelima.model.GetAllFilmResponseItem
import andlima.hafizhfy.challengelima.model.LoginThing
import andlima.hafizhfy.challengelima.model.RequestLogin
import andlima.hafizhfy.challengelima.model.Responseuser
import andlima.hafizhfy.challengelima.model.login.GetUserItem
import andlima.hafizhfy.challengelima.model.login.PutUser
import andlima.hafizhfy.challengelima.model.login.RequestUser
import retrofit2.Call
import retrofit2.http.*

interface ApiService {
//    @Headers("Content-Type: application/json")
    @Headers("Accept: application/json")
    @POST("login.php")
    fun postLogin(@Body request: RequestLogin) : Call<List<LoginThing>>
//    fun postLogin(email: String, password: String) : Call<Int>

    @GET("apifilm.php")
    fun getAllFilm() : Call<List<GetAllFilmResponseItem>>

    // Login service
    @GET("users")
    fun getUser(@Query("email") email : String) : Call<List<GetUserItem>>

    // Register service
    @POST("users")
    fun postUser(@Body request: RequestUser) : Call<GetUserItem>

    // Update profile
    @PUT("users/{id}")
    fun updateUser(
        @Path("id") id: Int,
        @Body request: PutUser
    ) : Call<GetUserItem>
}