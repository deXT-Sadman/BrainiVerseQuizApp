package np.com.sakibul.miulmsquizapp

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class SignupActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signup)

        auth = FirebaseAuth.getInstance()

        val nameEditText: EditText = findViewById(R.id.nameEditText)
        val emailEditText: EditText = findViewById(R.id.emailEditText)
        val passwordEditText: EditText = findViewById(R.id.passwordEditText)
        val signupButton: Button = findViewById(R.id.signupButton)

        signupButton.setOnClickListener {
            val name = nameEditText.text.toString().trim()
            val email = emailEditText.text.toString().trim()
            val password = passwordEditText.text.toString().trim()

            if (name.isNotEmpty() && email.isNotEmpty() && password.isNotEmpty()) {
                auth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            // User created successfully
                            val userId = auth.currentUser?.uid

                            if (userId != null) {
                                // Save user data to Firebase Realtime Database
                                saveUserToDatabase(userId, name, email)
                            } else {
                                Toast.makeText(this, "Failed to retrieve user ID.", Toast.LENGTH_SHORT).show()
                            }

                            // Redirect to login page
                            startActivity(Intent(this, LoginActivity::class.java))
                            finish()
                        } else {
                            // Signup failed
                            Toast.makeText(this, "Signup failed: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                        }
                    }
            } else {
                Toast.makeText(this, "Please fill out all fields.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun saveUserToDatabase(userId: String, name: String, email: String) {
        val database = FirebaseDatabase.getInstance().reference
        val userData = mapOf(
            "name" to name,
            "email" to email,
            "score" to 0 // Initialize score to 0 for new users
        )

        database.child("users").child(userId).setValue(userData)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Toast.makeText(this, "User data saved successfully!", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this, "Failed to save user data: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                }
            }
    }
}
