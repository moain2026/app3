package com.p001yd.electricecollector.network;

import com.p001yd.electricecollector.entities.AccessToken;
import com.p001yd.electricecollector.entities.AccountBalanceResponse;
import com.p001yd.electricecollector.entities.AccountsResponse;
import com.p001yd.electricecollector.entities.AuthData;
import com.p001yd.electricecollector.entities.BalanceStateDetailsRespons;
import com.p001yd.electricecollector.entities.BondsHeaderResponse;
import com.p001yd.electricecollector.entities.BondsPaymentResponse;
import com.p001yd.electricecollector.entities.BondsResponse;
import com.p001yd.electricecollector.entities.CompanyInfoResult;
import com.p001yd.electricecollector.entities.CurrencyResponse;
import com.p001yd.electricecollector.entities.GetRepBalanceHeaderResult;
import com.p001yd.electricecollector.entities.ItemReading;
import com.p001yd.electricecollector.entities.PlacesResponse;
import com.p001yd.electricecollector.entities.PostResponse;
import com.p001yd.electricecollector.entities.ReadingResponse;
import com.p001yd.electricecollector.entities.RepBoxMovesDetailsResponse;
import com.p001yd.electricecollector.entities.RepBoxMovesResponse;
import com.p001yd.electricecollector.entities.RepExpensesResponse;
import com.p001yd.electricecollector.entities.RepReadingResponse;
import com.p001yd.electricecollector.entities.Reports;
import com.p001yd.electricecollector.entities.TGroipResponse;
import com.p001yd.electricecollector.entities.TblhResponse;
import com.p001yd.electricecollector.entities.UserPlacesResponse;
import com.p001yd.electricecollector.entities.UserResponse;
import com.p001yd.electricecollector.entities.Users;
import java.util.List;
import java.util.Map;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.QueryMap;

/* loaded from: classes12.dex */
public interface ApiService {
    @GET("GetAccountBalance")
    Call<Object> GetAccountBalance(@QueryMap Map<String, String> map);

    @GET("GetAccountBalanceInfo")
    Call<AccountBalanceResponse> GetAccountBalanceInfo(@QueryMap Map<String, String> map);

    @GET("GetBondPaymentRecordNext")
    Call<Object> GetBondPaymentRecordNext(@QueryMap Map<String, String> map);

    @GET("GetBondRecieptRcordNext")
    Call<Object> GetBondRecieptRecordNext(@QueryMap Map<String, String> map);

    @GET("GetCompanyData")
    Call<CompanyInfoResult> GetCompanyInfo();

    @GET("GetListAccounts")
    Call<AccountsResponse> GetListAccounts(@QueryMap Map<String, String> map);

    @GET("GetListBonds")
    Call<BondsResponse> GetListBonds(@QueryMap Map<String, String> map);

    @GET("GetListBondsPayment")
    Call<BondsPaymentResponse> GetListBondsPayment(@QueryMap Map<String, String> map);

    @GET("GetListCurrency")
    Call<CurrencyResponse> GetListCurrency();

    @GET("GetListGroup")
    Call<TGroipResponse> GetListGroup(@QueryMap Map<String, String> map);

    @GET("GetListPlaces")
    Call<PlacesResponse> GetListPlaces(@QueryMap Map<String, String> map);

    @GET("GetListReadingCounter")
    Call<ReadingResponse> GetListReadingCounter(@QueryMap Map<String, String> map);

    @GET("GetRepReadingHeader")
    Call<RepReadingResponse> GetListRepReading(@QueryMap Map<String, String> map);

    @GET("GetListGroup")
    Call<TblhResponse> GetListTblh(@QueryMap Map<String, String> map);

    @GET("GetListUserPlaces")
    Call<UserPlacesResponse> GetListUserPlaces();

    @GET("GetListUsers")
    Call<UserResponse> GetListUsers(@QueryMap Map<String, String> map);

    @GET("GetRepBalanceDetails")
    Call<BalanceStateDetailsRespons> GetRepBalanceDetails(@QueryMap Map<String, String> map);

    @GET("GetRepBalanceDetailsByDate")
    Call<BalanceStateDetailsRespons> GetRepBalanceDetailsByDate(@QueryMap Map<String, String> map);

    @GET("GetRepBalanceHeader")
    Call<GetRepBalanceHeaderResult> GetRepBalanceHeader();

    @GET("GetRepBondsHeader")
    Call<BondsHeaderResponse> GetRepBondsHeader(@QueryMap Map<String, String> map);

    @GET("GetRepBoxMove")
    Call<RepBoxMovesResponse> GetRepBoxMove(@QueryMap Map<String, String> map);

    @GET("GetRepBoxMoveDetails")
    Call<RepBoxMovesDetailsResponse> GetRepBoxMoveDetails(@QueryMap Map<String, String> map);

    @GET("GetRepExpenses")
    Call<RepExpensesResponse> GetRepExpenses(@QueryMap Map<String, String> map);

    @GET("report1")
    Call<List<Reports>> Report1();

    @Headers({"Content-Type:application/json"})
    @POST("SaveReading")
    Call<Object> SaveReading(@Body ItemReading itemReading);

    @FormUrlEncoded
    @POST("login")
    Call<Users> login(@FieldMap Map<String, String> map);

    @GET("GetRepBalanceHeader")
    Call<PostResponse> posts(@QueryMap Map<String, String> map);

    @FormUrlEncoded
    @POST("refresh")
    Call<AccessToken> refresh(@Field("refresh_token") String str);

    @FormUrlEncoded
    @POST("register")
    Call<AccessToken> register(@Field("name") String str, @Field("email") String str2, @Field("password") String str3);

    @Headers({"Content-Type:application/json"})
    @POST("UserAuth")
    Call<AccessToken> userAuth(@Body AuthData authData);
}
