package com.yun.orderPad.net


import com.yun.orderPad.model.result.*
import com.yun.orderPad.net.model.BaseModel
import okhttp3.RequestBody
import retrofit2.http.*
import java.math.BigDecimal


interface RequestApi {

    @GET("/api-jlh-bff/canteen/device/getDeviceConfig")
    suspend fun getDeviceConfig(): BaseModel<Config?>

    @GET("/api-jlh-bff/canteen/device/getCurrentMeal")
    suspend fun getCurrentMeal(): BaseModel<Meal?>

    @GET("/api-jlh-bff/canteen/device/getOrderModeList")
    suspend fun getOrderModeList(): BaseModel<List<OrderMode>?>

    @GET("/api-jlh-bff/canteen/device/queryMealTableInfo")
    suspend fun queryMealTableInfo(): BaseModel<List<TableInfo>?>

    @GET("/api-jlh-bff/canteen/device/unbind")
    suspend fun unbind(): BaseModel<Boolean?>

    @GET("/api-jlh-bff/canteen/device/listSchool")
    suspend fun listSchool(): BaseModel<List<SchoolInfo>?>

    @GET("/api-jlh-bff/canteen/logout")
    suspend fun logout(): BaseModel<Boolean?>


    @POST("/api-jlh-bff/canteen/device/save")
    suspend fun save(@Body body: RequestBody): BaseModel<Boolean?>

    @POST("/api-jlh-bff/canteen/mealOrder/confirmPickUp")
    suspend fun confirmPickUp(@Body body: RequestBody): BaseModel<Boolean?>

    @POST("/api-jlh-bff/canteen/student/getStudentByFaceUid")
    suspend fun getStudentByFaceUid(@Body body: RequestBody): BaseModel<Student?>

    @POST("/api-jlh-bff/canteen/device/listKitchen")
    suspend fun listKitchen(@Body body: RequestBody): BaseModel<List<KitchenInfo>?>

    @POST("/api-jlh-bff/canteen/device/listWindow")
    suspend fun listWindow(@Body body: RequestBody): BaseModel<List<WindowInfo>?>

    @POST("/api-jlh-bff/canteen/mealOrder/getStudentPackageMeal")
    suspend fun getStudentPackageMeal(@Body body: RequestBody): BaseModel<List<OrderInfo>?>

    @POST("/api-jlh-bff/canteen/mealOrder/getLatestReceivedOrderList")
    suspend fun getLatestReceivedOrderList(@Body body: RequestBody): BaseModel<List<ReceivedOrder>?>

    @POST("/api-jlh-bff/canteen/mealOrder/getMealMenuList")
    suspend fun getMealMenuList(@Body body: RequestBody): BaseModel<List<MealMenu>?>

    @POST("/api-jlh-bff/canteen/mealOrder/submitMealOrder")
    suspend fun submitMealOrder(@Body body: RequestBody): BaseModel<Boolean?>


    @POST("/api-jlh-bff/canteen/student/getStudentAccount")
    suspend fun getStudentAccount(@Body body: RequestBody): BaseModel<BigDecimal?>

    @POST("/api-jlh-bff/canteen/login")
    suspend fun login(@Body body: RequestBody): BaseModel<LoginModel?>
}