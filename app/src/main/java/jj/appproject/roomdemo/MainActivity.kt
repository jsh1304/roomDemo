package jj.appproject.roomdemo

import android.app.AlertDialog
import android.app.Dialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import jj.appproject.roomdemo.databinding.ActivityMainBinding
import jj.appproject.roomdemo.databinding.DialogUpdateBinding
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {
    private var binding:ActivityMainBinding? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        val employeeDao = (application as EmployeeApp).db.employeeDao() // 데이터베이스를 얻을 수 있따.
        binding?.btnAdd?.setOnClickListener {
            addRecord(employeeDao)
        }

        lifecycleScope.launch {
            employeeDao.fetchAllEmployee().collect {
                Log.d("exactemployee", "$it")
                val list = ArrayList(it)
                setupListOfDataIntoRecyclerView(list, employeeDao)
            }
        }
    }

    private fun setupListOfDataIntoRecyclerView(employeesList:ArrayList<EmployeeEntity>,
                                                employeeDao: EmployeeDao){
        if(employeesList.isNotEmpty()){

            val itemAdapter = ItemAdapter(employeesList,
                {
                    updateId ->
                    updateRecordDialog(updateId, employeeDao)
                })
                {
                    deleteId ->
                    lifecycleScope.launch {
                        employeeDao.fetchEmployeeById(deleteId).collect {
                            if(it != null){
                                deleteRecordAlertDialog(deleteId, employeeDao, it)
                            }
                        }
                    }
                }

            binding?.rvItemsList?.layoutManager = LinearLayoutManager(this)
            binding?.rvItemsList?.adapter = itemAdapter
            binding?.rvItemsList?.visibility = View.VISIBLE
            binding?.tvNoRecordsAvailable?.visibility = View.GONE

            }
        else{
            binding?.rvItemsList?.visibility = View.GONE
            binding?.tvNoRecordsAvailable?.visibility = View.VISIBLE
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
                "이름, 이메일을 모두 입력하시오",
                Toast.LENGTH_LONG
            ).show()
        }
    }

    fun updateRecordDialog(id:Int,employeeDao: EmployeeDao){
        val updateDialog = Dialog(this, R.style.Theme_Dialog)
        updateDialog.setCancelable(false)

        val binding = DialogUpdateBinding.inflate(layoutInflater)
        updateDialog.setContentView(binding.root)

        lifecycleScope.launch {
            employeeDao.fetchEmployeeById(id).collect {
                if(it != null){
                    binding.etUpdateName.setText(it.name)
                    binding.etUpdateEmailId.setText(it.email)
                }
            }
        }
        binding.tvUpdate.setOnClickListener {
            val name = binding.etUpdateName.text.toString()
            val email = binding.etUpdateEmailId.text.toString()

            if(name.isNotEmpty()&&email.isNotEmpty()){
                lifecycleScope.launch {
                    employeeDao.update(EmployeeEntity(id, name, email))
                    Toast.makeText(
                        applicationContext,
                        "기록이 업데이트되었습니다.",
                        Toast.LENGTH_LONG).show()
                }
            }
        }
        binding.tvCancel.setOnClickListener {
            updateDialog.dismiss()
        }

        updateDialog.show()
    }

    fun deleteRecordAlertDialog(id:Int, employeeDao: EmployeeDao, employee: EmployeeEntity){
        val builder = AlertDialog.Builder(this)

        builder.setTitle("기록 삭제")

        builder.setMessage("정말로 ${employee.name}을 삭제하시겠습니까?")

        builder.setIcon(android.R.drawable.ic_dialog_alert)

        builder.setPositiveButton("네"){ dialogInterface, _ ->
            lifecycleScope.launch {
                employeeDao.delete(EmployeeEntity(id))
                Toast.makeText(
                    applicationContext,
                    "성공적으로 기록이 삭제되었습니다.",
                    Toast.LENGTH_LONG
                ).show()

                dialogInterface.dismiss()
            }

        }

        builder.setNegativeButton("아니오"){ dialogInterface, which ->
            dialogInterface.dismiss()
        }

        val alertDialog: AlertDialog = builder.create()

        alertDialog.setCancelable(false)
        alertDialog.show()
    }

}