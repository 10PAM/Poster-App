/**
 * Programmers {
 *  Mario Aguilera Piceno
 * }
 * Course: CS 460 - Software Engineering for Mobile Devices
 * Original Implementation: Professor Shangyue Zhu
 * Honor Code: I pledge that this work (aside from Professor Zhu's base implementation, is my work and that I have not received nor given help to anyone else.
 * Date: 10/19/2026
 * Modified: 10/24/2026
 * File: ContactFragment.java
 * About: Handles the contact fragment functionality.
 */

// Package declaration
package com.example.poster1019;

// Required imports
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

/**
 * Contains contact fragment methods.
 */
public class ContactFragment extends Fragment {

    /**
     * Constructor. Sends the fragment to the parent constructor.
     */
    public ContactFragment(){
        super(R.layout.fragment_contact);
    }

    private TextView description;

    /**
     * The fragment being
     *
     * @param view The View returned by onCreateView.
     * @param savedInstanceState If non-null, this fragment is being re-constructed
     * from a previous saved state as given here.
     */
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        view.findViewById(R.id.layoutEmail).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openEmailApp(getString(R.string.contact_email));
            }
        });

        description = view.findViewById(R.id.description_aboutme);
        view.findViewById(R.id.button_aboutme).setOnClickListener(view1 -> changeDescription(description));
    }

    /**
     * Changes description of view.
     *
     * @param view the View getting text updated.
     */
    private void changeDescription(TextView view) {
        String textToSet = "Hey there, my name is Mario, and as of today (10/23/2026), I am a third year at Central Washington University. I am looking forward to learning more about software development and engineering practices!";
        view.setText(textToSet);
    }

    /**
     * Opens the email app when email contact-us button is clicked.
     *
     * @param emailAddress the email address being referred.
     */
    private void openEmailApp(String emailAddress){
        Intent intent = new Intent(Intent.ACTION_SENDTO, Uri.parse("mailto:"+emailAddress));
        try {
            startActivity(intent);
        } catch (ActivityNotFoundException e) {
            Toast.makeText(requireContext(),R.string.no_email_app,Toast.LENGTH_SHORT).show();
        }
    }
}
