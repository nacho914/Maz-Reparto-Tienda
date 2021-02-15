package Retroapi.APIService;

import com.example.mazrepartotienda.PruebaFinal;

import org.json.JSONObject;

import Retroapi.Model.POSTSENDNOTIFICATIONS;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface APIService {

    @Headers({
            "Authorization: key=AAAAyUXOSJI:APA91bHRcWTrD3LB50qTECUJsKB5pCaUL5pOZBzsMcQHEwX_xyEujXHVkKcB9DpoHM39x_6IWVAUDM3jJ8peL_6W7DmtOJArJUWGmnOtW6RKz9Q7Vaqb2SXiUC5ygyex9OTqUD3sZ7Bc",
            "Content-Type: application/json"
    })
    @POST("/send")
    Call<POSTSENDNOTIFICATIONS> savePost(@Body PruebaFinal p);
}