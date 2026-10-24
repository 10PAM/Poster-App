/**
 * Programmers {
 *  Mario Aguilera Piceno
 * }
 * Course: CS 460 - Software Engineering for Mobile Devices
 * Original Implementation: Professor Shangyue Zhu
 * Honor Code: I pledge that this work (aside from Professor Zhu's base implementation, is my work and that I have not received nor given help to anyone else.
 * Date: 10/19/2026
 * Modified: 10/24/2026
 * File: PostersAdapter.java
 * About: Handles Poster viewing.
 */

// Package declaration
package com.example.poster1019;

// Required imports
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;
import com.makeramen.roundedimageview.RoundedImageView;
import java.util.ArrayList;
import java.util.List;

/**
 * Handles all Poster viewing functionality.
 */
public class PostersAdapter extends RecyclerView.Adapter<PostersAdapter.PostersViewHolder> {

    /**
     * The same adapter and card layout are used on two pages.
     * SELECT   = Main Page: tap a card to select it (orange border + checkmark).
     * FAVORITE = Favorite List: cards can't be selected; each has a remove button.
     */
    public enum Mode { SELECT, FAVORITE }

    /**
     * Called when the remove button on a Favorite List card is tapped.
     */
    public interface OnRemoveFavoriteListener {
        void onRemoveFavorite(Posters poster);
    }

    // Which page this adapter is for, and who handles "remove".
    private Mode mode = Mode.SELECT;
    private OnRemoveFavoriteListener removeFavoriteListener;

    /**
     * Handles Poster favorite updating.
     *
     * @param posters The Posters list.
     * @param removeFavoriteListener the RemoveFavoriteListener provided.
     */
    public PostersAdapter(List<Posters> posters, OnRemoveFavoriteListener removeFavoriteListener) {
        this.posters = posters;
        this.mode = Mode.FAVORITE;
        this.removeFavoriteListener = removeFavoriteListener;
    }

    /**
     * Updates the current Posters list.
     *
     * @param posters the updated Posters list.
     */
    public void setPosters(List<Posters> posters) {
        this.posters = posters;
        notifyDataSetChanged();
    }

    private List<Posters> posters;
    private posterListener posterListener;

    /**
     * Defines the posters.
     *
     * @param posters The posters list.
     * @param posterListener the PosterListener created.
     */
    public PostersAdapter(List<Posters>posters,posterListener posterListener){
        this.posters = posters;
        this.posterListener = posterListener;
    }

    /**
     * Constructor for creating the Poster's ViewHolder.
     *
     * @param parent   The ViewGroup into which the new View will be added after it is bound to
     *                 an adapter position.
     * @param viewType The view type of the new View.
     * @return
     */
    @NonNull
    @Override
    public PostersViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new PostersViewHolder(LayoutInflater.from(parent.getContext()).inflate(
                R.layout.item_container_poster,
                parent,
                false
        ));
    }

    /**
     * Binds Posters.
     *
     * @param holder   The ViewHolder which should be updated to represent the contents of the
     *                 item at the given position in the data set.
     * @param position The position of the item within the adapter's data set.
     */
    @Override
    public void onBindViewHolder(@NonNull PostersAdapter.PostersViewHolder holder, int position) {
        holder.bindPosters(posters.get(position));

    }

    /**
     * Retrieves the amount of Posters.
     *
     * @return Amount of posters.
     */
    @Override
    public int getItemCount() {
        return posters.size();
    }

    /**
     * Retrieve list of selected posters.
     *
     * @return Returns the list of all selected posters.
     */
    public List<Posters> getSelectedPosters(){
        List<Posters> selectedPosters = new ArrayList<>();
        for (Posters poster : posters){
            if (poster.isSelected){
                selectedPosters.add(poster);
            }
        }
        return selectedPosters;
    }

    /**
     * Sub-class; Handles Poster viewing.
     */
    class PostersViewHolder extends RecyclerView.ViewHolder{
        ConstraintLayout layoutPosters;
        View viewbackground;
        RoundedImageView imagePosters;
        TextView textName,textCreatedBy, textStory;
        RatingBar ratingBar;
        ImageView imageSelected;
        ImageView imageRemove;

        /**
         * Constructor for the PosterViewHolder.
         *
         * @param itemView the View state for the PosterViewHolder.
         */
        public PostersViewHolder(@NonNull View itemView) {
            super(itemView);
            layoutPosters = itemView.findViewById(R.id.layoutPosters);
            viewbackground = itemView.findViewById(R.id.viewBackground);
            imagePosters = itemView.findViewById(R.id.imagePosters);
            textName = itemView.findViewById(R.id.textName);
            textCreatedBy = itemView.findViewById(R.id.textCreateBy);
            textStory = itemView.findViewById(R.id.textStory);
            ratingBar = itemView.findViewById(R.id.ratingBar);
            imageSelected = itemView.findViewById(R.id.imageSelected);
            imageRemove = itemView.findViewById(R.id.imageRemove);
        }

        /**
         * Manages Poster binding.
         *
         * @param posters The Poster being altered.
         */
        void bindPosters(final Posters posters){
            imagePosters.setImageResource(posters.image);
            textName.setText(posters.name);
            textStory.setText(posters.story);
            textCreatedBy.setText(posters.createdBy);
            ratingBar.setRating(posters.rating);

            if(mode == Mode.FAVORITE){
                bindFavorite(posters);
                return;
            }
            imageRemove.setVisibility(View.GONE);

            // show whether the poster is selected
            if(posters.isSelected){
                viewbackground.setBackgroundResource(R.drawable.poster_selected_background);
                imageSelected.setVisibility(View.VISIBLE);

            }else{
                viewbackground.setBackgroundResource(R.drawable.poster_background);
                imageSelected.setVisibility(View.GONE);
            }

            layoutPosters.setOnClickListener(new View.OnClickListener() {
                /**
                 * Manages clicking for the Posters.
                 *
                 * @param view The view that was clicked.
                 */
                @Override
                public void onClick(View view) {
                    if(posters.isSelected){
                        viewbackground.setBackgroundResource(R.drawable.poster_background);
                        imageSelected.setVisibility(View.GONE);
                        posters.isSelected =false;
                        if(getSelectedPosters().isEmpty()){
                        posterListener.OnPosterAction(false);}
                    }else{
                        viewbackground.setBackgroundResource(R.drawable.poster_selected_background);
                        imageSelected.setVisibility(View.VISIBLE);
                        posters.isSelected =true;
                        posterListener.OnPosterAction(true);
                    }
                }
            });

        }

        /**
         * A Favorite List card: always the plain background and no checkmark (the
         * poster may still be selected on the Main Page, but that doesn't apply here), no
         * tap action, and a remove button that reports which poster to remove.
         *
         * @param posters The poster being altered.
         */
        private void bindFavorite(final Posters posters){
            viewbackground.setBackgroundResource(R.drawable.poster_background);
            imageSelected.setVisibility(View.GONE);
            layoutPosters.setOnClickListener(null);
            layoutPosters.setClickable(false);

            imageRemove.setVisibility(View.VISIBLE);
            imageRemove.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    removeFavoriteListener.onRemoveFavorite(posters);
                }
            });
        }
    }
}
