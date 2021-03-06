package com.parrishsystems.stock.repo.rest_api;

import com.parrishsystems.stock.model.IntradayRoot;
import com.parrishsystems.stock.model.LookupRoot;
import com.parrishsystems.stock.model.QuoteRoot;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface Apis {
    @GET("stock")
    Call<QuoteRoot> getQuote(
            @Query("symbol") String symbol,
            @Query("api_token") String apiKey);

    @GET("stock_search")
    Call<LookupRoot> lookup(
            @Query("search_term") String searchterm,
            @Query("search_by") String searchBy,
            @Query("stock_exchange") String exchanges,
            @Query("sort_by") String sortBy,
            @Query("page") int page,  // 1 based
            @Query("api_token") String apiKey);

    @GET("intraday")
    Call<IntradayRoot> intraday(
            @Query("symbol") String symbol,
            @Query("range") int range,
            @Query("interval") int interval,
            @Query("api_token") String apiKey);


    //https://intraday.worldtradingdata.com/api/v1/intraday?symbol=AAPL&range=1&interval=60&api_token=1Bc1bZ6lGjMfjUbX89gbSar18kkllnBgpjFc8zmQObR4bD2VzjCbfMDbwxpp

    // API example...
    //stock_search?search_term=DE&search_by=symbol&stock_exchange=NASDAQ,NYSE&sort_by=symbol&api_token=1Bc1bZ6lGjMfjUbX89gbSar18kkllnBgpjFc8zmQObR4bD2VzjCbfMDbwxpp

}
