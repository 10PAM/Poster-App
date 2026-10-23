package com.example.poster1019;

import android.content.Context;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * The one place that owns the app's movie data: all posters, the selection, and the favorites.
 */
public class PosterRepository {

    // Notified whenever favorites are added or removed (Favorite List, badge).
    public interface FavoritesListener{
        void onFavoritesChanged();
    }

    private static PosterRepository instance;

    private final List<Posters> allPosters;
    private final FavoritesStore favoritesStore;
    private final List<FavoritesListener> favoritesListeners = new ArrayList<>();

    // Creates the single instance on first use. It keeps the application context,
    // never an Activity: holding an Activity in a singleton would leak it.
    public static synchronized PosterRepository getInstance(Context context) {
        if (instance == null) {
            instance = new PosterRepository(context.getApplicationContext());
        }
        return instance;
    }

    private PosterRepository(Context appContext) {
        allPosters = Collections.unmodifiableList(createPosters());
        favoritesStore = new FavoritesStore(appContext);

        // Forget saved favorites whose poster no longer exists in the app.
        List<String> validIds = new ArrayList<>();
        for (Posters poster : allPosters) {
            validIds.add(poster.id);
        }
        favoritesStore.retainOnly(validIds);
    }

    public List<Posters> getAllPosters(){
        return allPosters;
    }

    public Posters findById(String id){
        for(Posters posters:allPosters){
            if(posters.id.equals(id)){
                return posters;
            }
        }
        return null;
    }

    // deselects every poster
    public void clearSelection(){
        for(Posters poster : allPosters){
            poster.isSelected = false;
        }
    }

    //get favorite posters, in the order they were added
    public List<Posters> getFavorites(){
        List<Posters> favorites = new ArrayList<>();
        for (String id : favoritesStore.getIds()) {
            Posters poster = findById(id);
            if (poster != null) {
                favorites.add(poster);
            }
        }
        return favorites;
    }

    public int getFavoriteCount(){
        return favoritesStore.getIds().size();
    }

    // Adds posters to favorites, skipping ones already there.
    // Returns how many were new, so the page can show the right message.
    public int addFavorites(List<Posters> posters) {
        List<String> ids = new ArrayList<>();
        for (Posters poster : posters) {
            ids.add(poster.id);
        }
        int added = favoritesStore.addAll(ids);
        if (added > 0) {
            notifyFavoritesChanged();
        }
        return added;
    }

    // Removes one favorite. Returns true if it was a favorite.
    public boolean removeFavorite(String id) {
        boolean removed = favoritesStore.remove(id);
        if (removed) {
            notifyFavoritesChanged();
        }
        return removed;
    }

    // The listener (observer) pattern: pages register to hear about changes.
    // Whoever adds a listener must remove it again (onDestroyView / onDestroy).
    public void addFavoritesListener(FavoritesListener listener) {
        if (!favoritesListeners.contains(listener)) {
            favoritesListeners.add(listener);
        }
    }

    public void removeFavoritesListener(FavoritesListener listener) {
        favoritesListeners.remove(listener);
    }

    private void notifyFavoritesChanged() {
        for(FavoritesListener listener : new ArrayList<>(favoritesListeners)){
            listener.onFavoritesChanged();
        }
    }

    private static List<Posters> createPosters() {
        List<Posters> posters = new ArrayList<>();

        Posters movie_1 = new Posters();
        movie_1.id = "lightswitch";
        movie_1.image = R.drawable.poster1;
        movie_1.name = "The Light Switch";
        movie_1.rating = 5f;
        movie_1.createdBy = "Mario Aguilera Piceno";
        movie_1.story = "A horror film about finding paths.";
        posters.add(movie_1);

        Posters movie_2 = new Posters();
        movie_2.id = "knock";
        movie_2.image = R.drawable.poster2;
        movie_2.name = "Don't knock";
        movie_2.rating = 1f;
        movie_2.createdBy = "Rick Sanchez";
        movie_2.story = "Don't Knock. Watch your next knock.";
        posters.add(movie_2);

        Posters movie_3 = new Posters();
        movie_3.id = "seeyou";
        movie_3.image = R.drawable.poster3;
        movie_3.name = "See You";
        movie_3.rating = 2f;
        movie_3.createdBy = "Mortimer Chauncey";
        movie_3.story = "An inspiring short film about finding yourself.";
        posters.add(movie_3);

        Posters movie_4 = new Posters();
        movie_4.id = "night";
        movie_4.image = R.drawable.poster4;
        movie_4.name = "The Night";
        movie_4.rating = 4f;
        movie_4.createdBy = "Squanchy";
        movie_4.story = "A thrilling passionate journey.";
        posters.add(movie_4);

        Posters movie_5 = new Posters();
        movie_5.id = "night";
        movie_5.image = R.drawable.poster5;
        movie_5.name = "The Night";
        movie_5.rating = 3f;
        movie_5.createdBy = "Mortaniel";
        movie_5.story = "A thrilling passionate journey.";
        posters.add(movie_5);

        return posters;

    }


}
