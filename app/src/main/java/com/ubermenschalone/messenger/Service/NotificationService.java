package com.ubermenschalone.messenger.Service;

import com.ubermenschalone.messenger.Notification.MyResponse;
import com.ubermenschalone.messenger.Notification.Sender;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface NotificationService {
    @Headers(
            {
                    "Content-Type:application/json",
                    "Authorization:key=AAAAutb8LOI:APA91bEbHKAs9QTKz_-WHfprPklafKN3AQskL5FA_pFCvYoBwrKJqY6-XC2bS9PpfCsXQfBMFxQGDt2KLs8YFigGfAkCpBqb-VOsTeFZhB6TKjgicMTSlo3uZONBrjGnFyi6wDXaurGi"
            }
    )

    @POST("fcm/send")
    Call<MyResponse> sendNotification(@Body Sender body);
}
