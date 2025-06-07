package com.example.bites.data.dao

import androidx.room.*
import com.example.bites.data.entity.CouponEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface CouponDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCoupon(coupon: CouponEntity)

    @Query("SELECT * FROM Coupons WHERE couponID = :id")
    fun getCouponById(id: Int): Flow<CouponEntity?>

    @Query("SELECT * FROM Coupons WHERE Code = :code")
    fun getCouponByCode(code: String): Flow<CouponEntity?>

    @Query("SELECT * FROM Coupons WHERE ExpirationDate > :currentDateString") // Assuming date strings are comparable
    fun getActiveCoupons(currentDateString: String): Flow<List<CouponEntity>>
}