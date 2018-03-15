
import java.util.HashMap;

/**
 * Created by vale_g on 3/13/18.
 */
public class DisplayName {

    // Hashmap<"key", "value"> where key is user id, and value is user's anonName
    static HashMap<String, String> hmap = new HashMap<String, String>();

    // keeps track of number of anonNames
    static int anonNameCount = 1;

    // default name
    static String defaultName = "viber";

    /**
     * Gets the anon name for the user if given if they already have one.
     * Else, it returns an error message stating the user does not have one.
     *
     * @param id to get anon name of
     * @return the value (anonName) of the user id
     */
    public static String getAnonName(String id){
        if(nameExists(id)) {
            return hmap.get(id);
        }
        return id + " does not have an anon name.";
    }


    /**
     * If the user has not been assigned an anonName,
     * Their anonName is the default name with the current count appended
     * After the name is added, the count is incremented by 1
     *
     * @param id the string id of the user
     */
    public static void addAnonName(String id){
        if(!nameExists(id)){
            hmap.put(id, defaultName + anonNameCount);
            anonNameCount++;
        }
    }

    /**
     * Checks if the user id has already been assigned an anonName
     *
     * @param id user id to check
     * @return true if key (user id) is already assigned an anonName
     */
    public static boolean nameExists(String id){
        return hmap.containsKey(id);
    }
}
