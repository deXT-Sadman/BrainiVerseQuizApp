package np.com.sakibul.miulmsquizapp

import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class ProfileActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        val userNameTextView: TextView = findViewById(R.id.userNameTextView)
        val userScoreTextView: TextView = findViewById(R.id.userScoreTextView)

        val userId = FirebaseAuth.getInstance().currentUser?.uid
        val database = FirebaseDatabase.getInstance().reference

        if (userId != null) {
            database.child("users").child(userId).get()
                .addOnSuccessListener { snapshot ->
                    val name = snapshot.child("name").value as? String ?: "Unknown"
                    val score = snapshot.child("score").value as? Long ?: 0

                    userNameTextView.text = "Hi, $name !"
                    userScoreTextView.text = "Your Score: $score"
                }
                .addOnFailureListener { exception ->
                    Toast.makeText(this, "Failed to fetch data: ${exception.message}", Toast.LENGTH_SHORT).show()
                }
        } else {
            Toast.makeText(this, "User is not logged in!", Toast.LENGTH_SHORT).show()
        }
    }
}
