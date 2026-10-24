/**
 * Programmers {
 *  Mario Aguilera Piceno
 * }
 * Course: CS 460 - Software Engineering for Mobile Devices
 * Original Implementation: Professor Shangyue Zhu
 * Honor Code: I pledge that this work (aside from Professor Zhu's base implementation, is my work and that I have not received nor given help to anyone else.
 * Date: 10/19/2026
 * Modified: 10/24/2026
 * File: PostersFragment.java
 * About: Handles the Poster fragment for functionality.
 */

// Package declaration
package com.example.poster1019;

// Required imports
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

/**
 * Handles Poster fragment for functionality of Poster.
 */
public class PostersFragment extends Fragment implements posterListener {

    private Button buttonAddtoWatchList;

    private PosterRepository posterRepository;

    /**
     * Constructor. Calls parent constructor with fragment page.
     */
    public PostersFragment(){
        super(R.layout.fragment_posters);
    }

    /**
     * Handles the initialization of the View.
     *
     * @param view The View returned by the OnViewCreate method.
     * @param savedInstanceState If non-null, this fragment is being re-constructed
     * from a previous saved state as given here.
     */
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //find the list and the button
        RecyclerView posterRecyclerView = view.findViewById(R.id.posTersRecyclerView);
        buttonAddtoWatchList  = view.findViewById(R.id.buttonAddToWatchlist);

        //the posters come from the shared repository
        posterRepository = PosterRepository.getInstance(requireContext());
        List<Posters> posters = posterRepository.getAllPosters();

        //connect the list to its adapter
        final PostersAdapter postersAdapter = new PostersAdapter(posters, this);

        posterRecyclerView.setAdapter(postersAdapter);

        //selections live in the repository and survive rotation, so button show up again
        OnPosterAction(!postersAdapter.getSelectedPosters().isEmpty());

        //show the selected names
        buttonAddtoWatchList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                List<Posters> selectPosters = postersAdapter.getSelectedPosters();

                int added = posterRepository.addFavorites(selectPosters);

                posterRepository.clearSelection();
                postersAdapter.notifyDataSetChanged();
                OnPosterAction(false);

                String message;
                if(added>0){
                    message = getResources().getQuantityString(R.plurals.favorites_added,added,added);
                }else {
                    message = getString(R.string.favorites_already_added);
                }
                Toast.makeText(requireContext(),message,Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * Handles Poster clicking actions.
     *
     * @param isSelected Determines if Poster is selected or not.
     */
    @Override
    public void OnPosterAction(Boolean isSelected) {
        if(isSelected){
            buttonAddtoWatchList.setVisibility(View.VISIBLE);
        }else{
            buttonAddtoWatchList.setVisibility(View.GONE);
        }

    }
}
