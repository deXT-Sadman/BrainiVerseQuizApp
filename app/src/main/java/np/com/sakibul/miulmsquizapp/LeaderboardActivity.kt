package np.com.sakibul.miulmsquizapp

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.FirebaseDatabase

class LeaderboardActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_leaderboard)

        val recyclerView: RecyclerView = findViewById(R.id.leaderboardRecyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)

        fetchLeaderboard { leaderboard ->
            val adapter = LeaderboardAdapter(leaderboard)
            recyclerView.adapter = adapter
        }
    }

    private fun fetchLeaderboard(callback: (List<Pair<String, Long>>) -> Unit) {
        val database = FirebaseDatabase.getInstance().reference

        database.child("users").orderByChild("score").limitToLast(20).get()
            .addOnSuccessListener { snapshot ->
                val leaderboard = mutableListOf<Pair<String, Long>>()
                for (user in snapshot.children) {
                    val name = user.child("name").value as? String ?: "Unknown"
                    val score = user.child("score").value as? Long ?: 0
                    leaderboard.add(Pair(name, score))
                }

                leaderboard.sortByDescending { it.second } // Sort descending
                callback(leaderboard)
            }
            .addOnFailureListener { exception ->
                Toast.makeText(this, "Failed to fetch leaderboard: ${exception.message}", Toast.LENGTH_SHORT).show()
            }
    }
}
