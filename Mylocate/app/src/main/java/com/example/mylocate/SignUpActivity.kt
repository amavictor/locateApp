package com.example.mylocate

import android.app.ProgressDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.activity_sign_up.*

class SignUpActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)

        signin_link_btn.setOnClickListener {
            startActivity(Intent(this, SignInActivity::class.java))
        }

        signup_btn.setOnClickListener {
            createAccount()
        }
    }

    private fun createAccount() {

        val fullName = fullName_signup.text.toString()
        val userName = username_signup.text.toString()
        val email = email_signup.text.toString()
        val password = password_signup.text.toString()

        when{
            TextUtils.isEmpty(fullName)-> Toast.makeText(this, "Full name is required ", Toast.LENGTH_LONG).show()
            TextUtils.isEmpty(userName)-> Toast.makeText(this, "username is required ", Toast.LENGTH_LONG).show()
            TextUtils.isEmpty(email)-> Toast.makeText(this, "Email is required ", Toast.LENGTH_LONG).show()
            TextUtils.isEmpty(password)-> Toast.makeText(this, "password is required ", Toast.LENGTH_LONG).show()

            else ->{
                val progressDialog = ProgressDialog(this@SignUpActivity)
                progressDialog.setTitle("SignUp")
                progressDialog.setMessage("Please wait, This may take a while")
                progressDialog.setCanceledOnTouchOutside(false)
                progressDialog.show()

                val mAuth =FirebaseAuth.getInstance()

                mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener { task ->
                    if(task.isSuccessful){
                        saveUserInfo(fullName,userName,email,progressDialog)
                    }
                    else{
                        val message = task.exception!!.toString()
                        Toast.makeText(this, "Error: $message", Toast.LENGTH_LONG).show()
                        progressDialog.dismiss()
                        mAuth.signOut()
                    }
                }
            }
        }
    }

    private fun saveUserInfo(fullName: String, userName: String, email: String,progressDialog:ProgressDialog) {
        val currentUserID = FirebaseAuth.getInstance().currentUser!!.uid
        val usersRef = FirebaseDatabase.getInstance().reference.child("Users")

        val userMap =HashMap<String, Any>()
        userMap["uid"] = currentUserID
        userMap["fullname"] = fullName.toLowerCase()
        userMap["username"] = userName.toLowerCase()
        userMap["email"] = email
        userMap["bio"] = "Hey I love this real Estate App, its cool!"
        userMap["image"] = "https://firebasestorage.googleapis.com/v0/b/my-locate-765b8.appspot.com/o/Default%20Images%2Fprofile.png?alt=media&token=a22b7887-9df8-47a9-9b77-0dae73ac6959"

        usersRef.child(currentUserID).setValue(userMap)
            .addOnCompleteListener { task->
                if(task.isSuccessful){
                    progressDialog.dismiss()
                    Toast.makeText(this,"Account has been created successfully",Toast.LENGTH_LONG).show()

                    val intent =Intent(this, MainActivity::class.java)
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)//this stops the user fro going back
                    //to this sign up page when they hit the back button
                    startActivity(intent)
                    finish()
                }
                else{
                    val message = task.exception!!.toString()
                    Toast.makeText(this, "Error: $message", Toast.LENGTH_LONG).show()
                    progressDialog.dismiss()
                    FirebaseAuth.getInstance().signOut()
                }
            }
    }

    }


