package com.example.scandal;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class OrganizerListCheckedInActivity extends AppCompatActivity {
    FrameLayout backMain;
    ListView userList;
    FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.my_events_page); // Assume this is the correct layout
        TextView txtMyEvents = findViewById(R.id.txtMyEvents);
        txtMyEvents.setText("CheckedIn Attendees");

        backMain = findViewById(R.id.buttonBack_MyEventsPage);
        userList = findViewById(R.id.listView_MyEventsPage);
        db = FirebaseFirestore.getInstance();

        // Retrieve the event name from the intent
        String eventName = getIntent().getStringExtra("eventName");

        backMain.setOnClickListener(v -> finish());

        loadCheckedInUsers(eventName); // Modify to pass eventName
    }

    /**
     * Retrieves and displays users checked in for the specified event.
     */
    private void loadCheckedInUsers(String eventName) {
        List<String> userNames = new ArrayList<>();
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, userNames);
        userList.setAdapter(adapter);

        // Adjust the Firestore query to filter by event name
        db.collection("events")
                .whereEqualTo("name", eventName) // Use eventName to filter
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                        Map<String, Object> eventData = documentSnapshot.getData();
                        if (eventData.containsKey("checkedIn")) {
                            Map<String, Object> checkedInUsers = (Map<String, Object>) eventData.get("checkedIn");
                            for (Object userNameObj : checkedInUsers.values()) {
                                String userName = (String) userNameObj;
                                if (userName != null) {
                                    userNames.add(userName);
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
}
