package edu.cnm.deepdive.quotes.service;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import edu.cnm.deepdive.quotes.BuildConfig;
import edu.cnm.deepdive.quotes.model.Quote;
import edu.cnm.deepdive.quotes.model.Source;
import edu.cnm.deepdive.quotes.model.User;
import io.reactivex.Completable;
import io.reactivex.Single;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import okhttp3.logging.HttpLoggingInterceptor.Level;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface QuotesService {

  @GET("quotes/random")
  Single<Quote> getRandomQuote(@Header("Authorization") String oauthHeader);

  @GET("quotes/qod")
  Single<Quote> getQuoteOfDay(
      @Header("Authorization") String oauthHeader, @Query("date") String date);

  @GET("quotes")
  Single<List<Quote>> getAllQuotes(@Header("Authorization") String oauthHeader);

  @GET("quotes/{id}")
  Single<Quote> getQuote(@Header("Authorization") String oauthHeader, @Path("id") UUID id);

  @GET("sources")
  Single<List<Source>> getAllSources(
      @Header("Authorization") String oauthHeader,
      @Query("includeNull") boolean includeNull, @Query("includeEmpty") boolean includeEmpty);

  @POST("quotes")
  Single<Quote> postQuote(@Header("Authorization") String oauthHeader, @Body Quote quote);

  @PUT("quotes/{id}")
  Single<Quote> putQuote(
      @Header("Authorization") String oauthHeader, @Body Quote quote, @Path("id") Long id);

  @DELETE("quotes/{id}")
  Completable deleteQuote(
      @Header("Authorization") String oauthHeader, @Path("id") Long id);

  @GET("users/me")
  Single<User> getMe(@Header("Authorization") String oauthHeader);

  static QuotesService getInstance() {
    return InstanceHolder.INSTANCE;
  }

  class InstanceHolder {

    private static final String TIMESTAMP_FORMAT = "yyyy-MM-dd'T'HH:mm:ss.SSSZ";
    private static final QuotesService INSTANCE;

    static {
      Gson gson  = new GsonBuilder()
          .setDateFormat(TIMESTAMP_FORMAT)
          .excludeFieldsWithoutExposeAnnotation()
          .create();
      HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
      interceptor.setLevel(BuildConfig.DEBUG ? Level.BODY : Level.NONE);
      OkHttpClient client = new OkHttpClient.Builder()
          .readTimeout(60, TimeUnit.SECONDS)
          .addInterceptor(interceptor)
          .build();
      Retrofit retrofit = new Retrofit.Builder()
          .addConverterFactory(GsonConverterFactory.create(gson))
          .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
          .client(client)
          .baseUrl(BuildConfig.BASE_URL)
          .build();
      INSTANCE = retrofit.create(QuotesService.class);
    }

  }

}
