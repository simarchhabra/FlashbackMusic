package com.cse110.flashbackmusicplayer;


import android.util.Log;

import java.util.List;

/**
 * Created by vale_g on 3/13/18.
 */
public class DisplayName {

    // default name
    static String defaultName = "viber";

    /**
     * Gets the anon name for the user
     * @param id to get anon name of
     * @return the value (anonName) of the user id
     */
    public static String getAnonName(String id){
        return defaultName + id.substring(id.length() - 4);
    }

    public static boolean lastPlayedByCurrentUser(String lastUser){
        // get the NAME of current user
        String currUser = UserDataStorage.getProfile().get(1);
        return currUser.compareTo(lastUser) == 0;
    }

    public static String lastPlayedByOther(String lastUser){
        // get the google friends of the current user
        List<List<String>> userFriends = UserDataStorage.getContacts();

        for(List<String> currContact : userFriends){
            Log.d("Last played by other", currContact.get(1));
            if(currContact.get(1).compareTo(lastUser) == 0){
                return currContact.get(1);
            }
        }
        // TODO this should return the proxy name of the lastUser
        return getAnonName(lastUser);
    }
}
