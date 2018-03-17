package com.cse110.flashbackmusicplayer;


import android.util.Log;

import java.util.List;


public class DisplayName {

    // default name
    static String defaultName1 = "viber_";
    static String defaultName2 = "fox_";
    static String defaultName3 = "fresh_";
    static String defaultName4 = "walrus_";

    /**
     * Gets the anon name for the user
     * @param id to get anon name of
     * @return the value (anonName) of the user id
     */
    public static String getAnonName(String id){
        String base;
        switch (id.length() % 4) {
            case 0:
                base = defaultName1;
                break;
            case 1:
                base = defaultName2;
                break;
            case 2:
                base = defaultName3;
                break;
            case 3:
                base = defaultName4;
                break;
            default:
                base = "unknown_";
        }

        if (id.length() >= 4) {
            return base + id.substring(id.length() - 4);
        }
        else {
            return base + id;
        }
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
