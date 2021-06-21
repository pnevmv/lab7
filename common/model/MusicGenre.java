package common.model;

import java.io.Serializable;

/**
 * Enumeration with music genre constants.
 */
public enum MusicGenre implements Serializable {
    PROGRESSIVE_ROCK,
    SOUL,
    POST_ROCK,
    PUNK_ROCK;

    /**
     * @return List of names of music genres.
     */
    public static String nameList() {
        String nameList = "";
        for (MusicGenre category : values()) {
            nameList += category.name() + ", ";
        }
        return nameList.substring(0, nameList.length()-2);
    }

}