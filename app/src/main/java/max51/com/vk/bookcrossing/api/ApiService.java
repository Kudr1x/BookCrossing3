package max51.com.vk.bookcrossing.api;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface ApiService {
    @GET("volumes")
    Call<BookResponse> getMyJSON(@Query("q") String query, @Query("maxResults") int maxResults);
}
