package com.echessa.todo;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.location.LocationManager;
import android.location.LocationListener;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MainActivity extends AppCompatActivity {

    private FirebaseAuth mFirebaseAuth;
    private FirebaseUser mFirebaseUser;
    private DatabaseReference mDatabase;
    private String mUserId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        class MyLocationListener implements LocationListener {

            public void onLocationChanged(android.location.Location loc) {
                Context context = getApplicationContext();
                String message = String.format(
                        "New FireLocation \n Longitude: %1$s \n Latitude: %2$s",
                       loc.getLongitude(), loc.getLatitude()
                );
                Toast.makeText(context, message, Toast.LENGTH_LONG).show();
            }

            public void onProviderDisabled(String arg0) {

            }

            public void onProviderEnabled(String provider) {

            }

            public void onStatusChanged(String provider, int status, Bundle extras) {

            }
        }

        //Create location listener
        final LocationManager mLocManager = (LocationManager) getSystemService(this.LOCATION_SERVICE);
        final LocationListener mLocListener = new MyLocationListener();
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            Context context = getApplicationContext();
            String message = String.format(
                    "No Permissions" );
            Toast.makeText(context, message, Toast.LENGTH_LONG).show();
        } else {
            Context context = getApplicationContext();
            String message = String.format(
                    "Request Updates" );
            Toast.makeText(context, message, Toast.LENGTH_LONG).show();
            mLocManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, mLocListener);
        }


        // Initialize Firebase Auth and Database Reference
        mFirebaseAuth = FirebaseAuth.getInstance();
        mFirebaseUser = mFirebaseAuth.getCurrentUser();
        mDatabase = FirebaseDatabase.getInstance().getReference();

        if (mFirebaseUser == null) {
            // Not logged in, launch the Log In activity
            loadLogInView();
        } else {
            mUserId = mFirebaseUser.getUid();

            // Set up ListView
            final ListView listView = (ListView) findViewById(R.id.listView);
            final ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, android.R.id.text1);
            listView.setAdapter(adapter);

            // Add items via the Button and EditText at the bottom of the view.
            final EditText text = (EditText) findViewById(R.id.todoText);
            final Button button = (Button) findViewById(R.id.addButton);
            button.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    Context context = getApplicationContext();
                    String message = String.format(
                            "Button Pushed" );
                    Toast.makeText(context, message, Toast.LENGTH_LONG).show();
                    if (ActivityCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                        // TODO: Consider calling
                        //    ActivityCompat#requestPermissions
                        // here to request the missing permissions, and then overriding
                        //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                        //                                          int[] grantResults)
                        // to handle the case where the user grants the permission. See the documentation
                        // for ActivityCompat#requestPermissions for more details.
                        message = String.format(
                                "No Permissions" );
                        Toast.makeText(context, message, Toast.LENGTH_LONG).show();
                    } else {
                        message = String.format(
                                "Location Found" );
                        Toast.makeText(context, message, Toast.LENGTH_LONG).show();
                        android.location.Location location = mLocManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                        FireLocation fire_location = new FireLocation(String.valueOf(location.getLatitude()), String.valueOf(location.getLongitude()));
                        mDatabase.child("data").child("locations").child(mUserId).setValue(fire_location);
                        //Item item = new Item(text.getText().toString());
                        //mDatabase.child("users").child(mUserId).child("items").push().setValue(item);
                        text.setText("");
                    }

                }
            });

            // Use Firebase to populate the list.
//            mDatabase.child("users").child(mUserId).child("items").addChildEventListener(new ChildEventListener() {
//                @Override
//                public void onChildAdded(DataSnapshot dataSnapshot, String s) {
//                    adapter.add((String) dataSnapshot.child("title").getValue());
//                }
//
//                @Override
//                public void onChildChanged(DataSnapshot dataSnapshot, String s) {
//
//                }
//
//                @Override
//                public void onChildRemoved(DataSnapshot dataSnapshot) {
//                    adapter.remove((String) dataSnapshot.child("title").getValue());
//                }
//
//                @Override
//                public void onChildMoved(DataSnapshot dataSnapshot, String s) {
//
//                }
//
//                @Override
//                public void onCancelled(DatabaseError databaseError) {
//
//                }
//            });

            //need better way of updating list when a user updates their location
            //callbacks are working, just hard to work with a list to show this
            mDatabase.child("data").child("locations").addChildEventListener(new ChildEventListener() {
                public String last_added;
                @Override
                public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                    last_added = dataSnapshot.child("lat").getValue() + ", " + dataSnapshot.child("lng").getValue();
                    adapter.add(last_added);
                }

                @Override
                public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                    //need to only change the one that matches the user id?! How to reget the database?
                    if(last_added.isEmpty() == false) {
                        adapter.remove(last_added);
                        last_added = dataSnapshot.child("lat").getValue() + ", " + dataSnapshot.child("lng").getValue();
                        adapter.add(last_added);
                    }
                }

                @Override
                public void onChildRemoved(DataSnapshot dataSnapshot) {
                    adapter.remove((String) dataSnapshot.child("lat").getValue() + ", " + dataSnapshot.child("lng").getValue());
                }

                @Override
                public void onChildMoved(DataSnapshot dataSnapshot, String s) {

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });

            // Delete items when clicked
//            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                    mDatabase.child("users").child(mUserId).child("items")
//                            .orderByChild("title")
//                            .equalTo((String) listView.getItemAtPosition(position))
//                            .addListenerForSingleValueEvent(new ValueEventListener() {
//                                @Override
//                                public void onDataChange(DataSnapshot dataSnapshot) {
////                                    if (dataSnapshot.hasChildren()) {
////                                        DataSnapshot firstChild = dataSnapshot.getChildren().iterator().next();
////                                        firstChild.getRef().removeValue();
////                                    }
//                                }
//
//                                @Override
//                                public void onCancelled(DatabaseError databaseError) {
//
//                                }
//                            });
//                }
//            });
            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                public void onItemClick(final AdapterView<?> parent, final View view, int position, long id) {
                    //careful this deletes the user's data even when one clicks the other user's location
                    mDatabase.child("data").child("locations").child(mUserId)
                            //need to find out how to delete with new lat, lng display
                            .addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    if (dataSnapshot.hasChildren()) {
                                        DatabaseReference ref = dataSnapshot.getRef();
                                        if( ref.getKey() == mUserId ) {
                                            ref.removeValue();
                                        }
                                    }
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                }
                            });
                }
            });
        }
    }

    private void loadLogInView() {
        Intent intent = new Intent(this, LogInActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_logout) {
            mFirebaseAuth.signOut();
            loadLogInView();
        }

        return super.onOptionsItemSelected(item);
    }
}
