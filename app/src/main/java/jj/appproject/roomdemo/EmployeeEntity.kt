package jj.appproject.roomdemo

import android.provider.ContactsContract
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "employee-table") // 테이블 이름 지정
data class EmployeeEntity(
    @PrimaryKey(autoGenerate = true) // 키를 자동으로 생성
    val id: Int = 0,
    val name: String = "",
    @ColumnInfo(name = "email-id")  // 해당 열에 내부적으로 다른 이름을 부여 가능.
    val email: String = ""

)
