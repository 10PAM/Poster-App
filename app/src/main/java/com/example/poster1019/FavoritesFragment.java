/**
 * Programmers {
 *  Mario Aguilera Piceno
 * }
 * Course: CS 460 - Software Engineering for Mobile Devices
 * Original Implementation: Professor Shangyue Zhu
 * Honor Code: I pledge that this work (aside from Professor Zhu's base implementation, is my work and that I have not received nor given help to anyone else.
 * Date: 10/19/2026
 * Modified: 10/24/2026
 * File: FavoritesFragment.java
 * About: Handles the Favorites fragment functionality.
 */

// Package declaration
package com.example.poster1019;

// Required imports
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

/**
 * Handles the favorites poge.
 */
public class FavoritesFragment extends Fragment implements PosterRepository.FavoritesListener, PostersAdapter.OnRemoveFavoriteListener {

    private PosterRepository posterRepository;
    private PostersAdapter favoritesAdapter;
    private TextView textFavoritesCount;
    private RecyclerView favoritesRecyclerView;
    private View layoutFavoritesEmpty;

    public FavoritesFragment(){
        super(R.layout.fragment_favorites);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        textFavoritesCount = view.findViewById(R.id.textFavoritesCount);
        favoritesRecyclerView = view.findViewById(R.id.favoritesRecyclerView);
        layoutFavoritesEmpty = view.findViewById(R.id.layoutFavoritesEmpty);

        posterRepository = PosterRepository.getInstance(requireContext());

        // "this" handles the remove button (onRemoveFavorite below).
        favoritesAdapter = new PostersAdapter(posterRepository.getFavorites(), this);
        favoritesRecyclerView.setAdapter(favoritesAdapter);

        // Listen for changes. MainActivity only hides this page, so it stays up to date
        // even while another page is on screen.
        posterRepository.addFavoritesListener(this);

        refresh();

    }

    @Override
    public void onDestroyView() {
        posterRepository.removeFavoritesListener(this);
        super.onDestroyView();
    }

    private void refresh() {
        List<Posters> favorites = posterRepository.getFavorites();
        favoritesAdapter.setPosters(favorites);

        int count = favorites.size();
        textFavoritesCount.setText(getResources().getQuantityString(R.plurals.favorites_count, count, count));

        boolean isEmpty = favorites.isEmpty();
        textFavoritesCount.setVisibility(isEmpty ? View.GONE : View.VISIBLE);
        favoritesRecyclerView.setVisibility(isEmpty ? View.GONE : View.VISIBLE);
        layoutFavoritesEmpty.setVisibility(isEmpty ? View.VISIBLE : View.GONE);
    }

    @Override
    public void onFavoritesChanged() {
        refresh();

    }

    @Override
    public void onRemoveFavorite(Posters poster) {
        posterRepository.removeFavorite(poster.id);
    }
}
