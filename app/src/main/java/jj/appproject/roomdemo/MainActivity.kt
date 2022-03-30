package jj.appproject.roomdemo

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import jj.appproject.roomdemo.databinding.ActivityMainBinding
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {
    private var binding:ActivityMainBinding? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val employeeDao = (application as EmployeeApp).db.employeeDao() // 데이터베이스를 얻을 수 있따.
        setContentView(binding?.root)

        binding?.btnAdd?.setOnClickListener {
            addRecord(employeeDao)
        }
    }

    fun addRecord(employeeDao: EmployeeDao){
        val name = binding?.etName?.text.toString()
        val email = binding?.etEmailId?.text.toString()
        if(name.isNotEmpty() && email.isNotEmpty()){
            lifecycleScope.launch {
                employeeDao.insert(EmployeeEntity(name=name,email = email)) // insert는 coroutine 함수
                Toast.makeText(applicationContext, "기록이 저장되었습니다", Toast.LENGTH_LONG).show()
                binding?.etName?.text?.clear()
                binding?.etEmailId?.text?.clear()
                }
        }
        else{
            Toast.makeText(
                applicationContext,
                "이름, 이메일의 칸을 모두 채워주세요",
                Toast.LENGTH_LONG
            ).show()
        }
    }

}