package com.maseko.root.lugchatv1.Api;

import com.maseko.root.lugchatv1.Notification.MyResponse;
import com.maseko.root.lugchatv1.Notification.Sender;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface APIService {
    @Headers(
            {
                    "Content-Type:application/json",
                    "Authorization:key=AAAA8p__n_A:APA91bG03mOkpybmTqNtJvOXgi2S-M4cK5WDWtQrLfiWBHf2TKqwR1_w3BZ6WJ4fFajh8WLMGkwR8QAlnZE4oFiQVHmfQS6bN6yRgqnwHpluc90oIIWPuD43OifMVu6vFXsuC28vkR5u"
            }
    )

    @POST("fcm/send")
    Call<MyResponse> sendNotification(@Body Sender body);
}