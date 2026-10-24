/**
 * Programmers {
 *  Mario Aguilera Piceno
 * }
 * Course: CS 460 - Software Engineering for Mobile Devices
 * Original Implementation: Professor Shangyue Zhu
 * Honor Code: I pledge that this work (aside from Professor Zhu's base implementation, is my work and that I have not received nor given help to anyone else.
 * Date: 10/19/2026
 * Modified: 10/24/2026
 * File: FavoritesStore.java
 * About: Contains the methods for managing favorited posters.
 */

package com.example.poster1019;

// Required imports
import android.content.Context;
import android.content.SharedPreferences;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * Saves the favorite poster IDs on the device, so they survive closing the app.
 */
public class FavoritesStore {
    private static final String PREFS_NAME = "favorites";
    private static final String KEY_IDS = "ids";
    private static final String SEPARATOR = ",";

    private final SharedPreferences preferences;

    //in-memory copy of the saved Ids
    private final List<String> ids;


    /**
     * Constructor for favorites container.
     *
     * @param context The AppContext from the PosterRepository.
     */
    public FavoritesStore(Context context) {
        preferences = context.getSharedPreferences(PREFS_NAME,Context.MODE_PRIVATE);
        ids = decode(preferences.getString(KEY_IDS,""));
    }

    /**
     * Get IDS, in the order they were added (read-only)
     *
     * @return Returns the list of favorited posters.
     */
    public List<String> getIds(){
        return Collections.unmodifiableList(ids);
    }

    /**
     * Adds favorited posters.
     *
     * @param newIds The ids of the newly favorited posters.
     * @return Retuns the added amount.
     */
    public int addAll(List<String> newIds){
        int added = 0;
        for (String id:newIds){
            if(!ids.contains(id)){
                ids.add(id);
                added++;
            }
        }
        if(added>0){
            save();
        }
        return added;
    }

    /**
     * Remove one ID, return true if it was there
     *
     * @param id the favorited item being removed/
     * @return Returns the removed item.
     */
    public boolean remove(String id){
        boolean removed = ids.remove(id);
        if(removed){
            save();
        }
        return removed;
    }

    /**
     * Drops saved ids that don't match any poster any more.
     *
     * @param validIds The ids being saved.
     */
    public void retainOnly(Collection<String> validIds){
        if(ids.retainAll(validIds)){
            save();
        }
    }

    /**
     * Saves the favorited posters.
     */
    private void save(){
        preferences.edit().putString(KEY_IDS,encode(ids)).apply();
    }

    /**
     * Encodes the favorite posters data.
     *
     * @param ids The ides being encoded.
     * @return Returns the string representation of the favorites
     * data.
     */
    static String encode(List<String> ids){
        StringBuilder builder = new StringBuilder();
        for(int i=0;i<ids.size();i++){
            if (i > 0) {
                builder.append(SEPARATOR);
            }
            builder.append(ids.get(i));
        }
        return builder.toString();
    }

    /**
     * Decodes the favorite posters data.
     *
     * @param saved the data of the saved favorite posters
     * @return Returns the parsed (decoded) data.
     */
    static List<String> decode(String saved){
        List<String> result = new ArrayList<>();
        if (saved == null || saved.isEmpty()){
            return result;
        }
        for(String id: saved.split(SEPARATOR)){
            if(!id.isEmpty() && !result.contains(id)){
                result.add(id);
            }
        }
        return result;
    }
}
