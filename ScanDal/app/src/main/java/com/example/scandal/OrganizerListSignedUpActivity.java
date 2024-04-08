package com.example.scandal;

import android.os.Bundle;
import android.util.Pair;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Activity for displaying the list of user who signed up for an event
 */
public class OrganizerListSignedUpActivity extends AppCompatActivity implements CustomArrayAdapter.OnItemClickListener {
    /**
     * FrameLayout for navigating back to the main page.
     */
    FrameLayout backMain;
    /**
     * ListView for displaying signed up users.
     */
    ListView userList;
    /**
     * Firebase Firestore instance for database operations.
     */
    FirebaseFirestore db;
    /**
     * Called when the activity is starting.
     *
     * @param savedInstanceState If the activity is being re-initialized after being previously shut down, this Bundle contains the data it most recently supplied. Otherwise, it is null.
     */
    CustomArrayAdapter adapter;
    List<Pair<String, String>> userNames;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.events_attendees_page); // Ensure this is the correct layout
        TextView txtMyEvents = findViewById(R.id.list_view_header);
        txtMyEvents.setText("SignedUp Attendees");

        backMain = findViewById(R.id.buttonBack_EventsAttendeesPage);
        userList = findViewById(R.id.listView_EventsAttendeesPage);
        db = FirebaseFirestore.getInstance();

        backMain.setOnClickListener(v -> finish());

        // Retrieve the event name from the intent
        String eventName = getIntent().getStringExtra("eventName");

        //Initialize ArrayAdapter
        userNames = new ArrayList<>();
        adapter = new CustomArrayAdapter(this, R.layout.list_item_layout, userNames);
        adapter.setOnItemClickListener(OrganizerListSignedUpActivity.this);
        userList.setAdapter(adapter);
        loadUsers(eventName); // Pass the eventName to the method
    }
    /**
     * Retrieves and displays users signed up for the specified event.
     */
    private void loadUsers(String eventName) {
        //Make Header
        userNames.add(new Pair<>("Name", ""));
        //Load rest of data
        db.collection("events")
                .whereEqualTo("name", eventName) // Filter by the event name
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                        Map<String, Object> eventData = documentSnapshot.getData();
                        if (eventData.containsKey("signedUp")) {
                            Map<String, Object> signedUpUsers = (Map<String, Object>) eventData.get("signedUp");
                            for (Object userNameObj : signedUpUsers.values()) {
                                String userName = (String) userNameObj;
                                if (userName != null) {
                                    userNames.add(new Pair<>("userName", ""));
                                    adapter.notifyDataSetChanged();
                                }
                            }
                        }
                    }
                })
                .addOnFailureListener(e -> {
                    // Handle errors
                });
    }

    @Override
    public void onItemClick(int position) {
        Pair<String, String> eventObject = adapter.getItem(position);
        String attendeeNames = eventObject.first;
        Toast.makeText(OrganizerListSignedUpActivity.this, attendeeNames+" is selected", Toast.LENGTH_SHORT).show();
    }
}
