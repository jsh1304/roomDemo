package jj.appproject.roomdemo

import android.app.Application

class EmployeeApp: Application() {
    val db by lazy{ // lazy = 필용할 때만 변수를 전달
        EmployeeDatabase.getInstance(this)
    }
}