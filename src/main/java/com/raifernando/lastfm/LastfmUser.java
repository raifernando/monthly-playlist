package com.raifernando.lastfm;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import com.raifernando.util.Credentials;
import com.raifernando.util.DateRange;
import com.raifernando.util.PropertiesFile;
import com.raifernando.util.Request;

import java.lang.reflect.Type;
import java.util.ArrayList;

import static java.lang.Integer.compare;

/**
 * The {@link LastfmUser} class is responsible to retrieve data of the Last.fm user's account.
 * For this project, the available operation is to get the recent tracks scrobbled within a certain period of time.
 */
public class LastfmUser {
    // Lastfm username used for API calls
    private final String user;

    /**
     * Create an instance with the default username stored in the properties file.
     * @throws IllegalArgumentException if the user is invalid
     */
    public LastfmUser() {
        this(getUserFromProperties());
    }

    /**
     * Create an instance using the provided String as username.
     * @param user the last.fm username
     * @throws IllegalArgumentException if the user is invalid
     */
    public LastfmUser(String user) {
        if (user == null || user.isEmpty())
            throw new IllegalArgumentException("Invalid lastfm user");

        this.user = user;
    }

    /**
     * Create an instance with the user from the first argument in the list (if available),
     * otherwise retrieve it from the properties file.
     * @param args argument list
     * @throws IllegalArgumentException if the user is invalid
     */
    public LastfmUser(String [] args) {
        this(getUserFromArguments(args));
    }

    /**
     * If there are three arguments, retrieves the username from the first element in the list of arguments.
     * @param args argument list
     * @return the username as a {@link String}.
     */
    private static String getUserFromArguments(String [] args) {
        if (args != null && args.length == 3)
            return args[0];

        return getUserFromProperties();
    }

    /**
     * Retrieve the username stored in the properties file.
     * @return the username as a {@link String}.
     */
    private static String getUserFromProperties() {
        PropertiesFile propertiesFile = new PropertiesFile();
        return propertiesFile.get("LASTFM_USER");
    }

    /**
     * Retrieves the user's recent tracks within a specified range.
     * @param range data range for the request
     * @return an array list with the tracks. Can be empty.
     */
    public ArrayList<LastfmTrack> getRecentTracks(DateRange range) {
        System.out.printf("""
                Requesting Lastfm data of %s.
                Receiving scrobbles from %s.
                """, user, range.getFullRange());

        String url = "http://ws.audioscrobbler.com/2.0/?method=user.getrecenttracks&user="
                + user + "&from=" + range.getStartDate() + "&to=" + range.getEndDate() +"&limit=200"
                + "&api_key=" + Credentials.lastfmApiKey +"&format=json";

        Gson gson = new Gson();
        JsonArray recentTracks = new JsonArray();

        int currentPage = 1, totalPages = 1;
        do {
            // The expected response from the API is in this format: {"recenttracks": {"track":[...], "@attr":{...}}}
            JsonObject scrobbles = Request.getNoHeader(url + "&page=" + currentPage, JsonObject.class);

            if (scrobbles == null) {
                System.err.printf("Failed to retrieve data for page %d.%n", currentPage);
                continue; // Try the next page
            }

            scrobbles = scrobbles.getAsJsonObject("recenttracks");

            // Get the response information of the field "@attr" on the first iteration
            if (currentPage == 1) {
                ResponseInformation response = gson.fromJson(scrobbles.getAsJsonObject("@attr"), ResponseInformation.class);
                totalPages = response.getTotalPages();
                System.out.printf("Total pages: %d. Total scrobbles: %d\nPage: ", totalPages, response.getTotal());
            }

            System.out.printf("%d ", currentPage);

            JsonArray pageTracks = scrobbles.getAsJsonArray("track");
            if (pageTracks == null || pageTracks.isEmpty())
                continue; // Try the next page

            // The first track in the response is possibly the currently playing track
            if (isTrackNowPlaying(pageTracks.get(0)))
                pageTracks.remove(0);

            recentTracks.addAll(pageTracks);
        } while (currentPage++ < totalPages);

        System.out.println();

        Type arrayType = new TypeToken<ArrayList<LastfmTrack>>(){}.getType();
        return gson.fromJson(recentTracks, arrayType);
    }

    /**
     * Check whether the track response has a field "@attr", which means that the track is currently playing in Spotify
     * @param track track to check the status
     * @return {@code true} if is playing, {@code false} if not
     */
    private boolean isTrackNowPlaying(JsonElement track) {
        return track.getAsJsonObject().has("@attr");
    }

    /**
     * Selects all tracks with playcount greater than or equal to {@code playcountPerTrack}.
     * If the number of selected tracks is lower than {@code min},
     * subsequent tracks of the array are included until {@code min} is reached. <br>
     * The returned array is sorted in descending order of playcount.
     * @param tracks array of tracks
     * @param min: minimum quantity of tracks
     * @param playcountPerTrack value which all tracks with playcount greater or equal are added.
     * @return an arraylist of {@link LastfmTrack} with the tracks selected
     * @throws NullPointerException if the array of tracks is null.
     * @throws IllegalArgumentException if min/playcount is 0.
     * @see #getUserTracks(ArrayList)
     */
    public ArrayList<LastfmTrack> getUserTopTracks(ArrayList<LastfmTrack> tracks, int min, int playcountPerTrack) {
        if (tracks == null || tracks.isEmpty())
            throw new NullPointerException("array of tracks is null/empty.");

        if (min == 0 || playcountPerTrack == 0)
            throw new IllegalArgumentException("min/playcount cannot be 0.");

        // Sort list alphabetically
        tracks.sort(LastfmTrack::compareNameTo);

        ArrayList<LastfmTrack> topTracks = new ArrayList<>();
        int tracksWithEnoughPlaycount = 0;

        // Unite tracks with the same name
        topTracks.add(tracks.getFirst());
        for (LastfmTrack track : tracks) {
            if (track.getName().equals(topTracks.getLast().getName())) {
                topTracks.getLast().increasePlaycount(1);
            }
            else {
                if (topTracks.getLast().getPlaycount() >= playcountPerTrack)
                    tracksWithEnoughPlaycount++;

                track.increasePlaycount(1);
                topTracks.add(track);
            }
        }

        topTracks.sort((a,b) -> -1 * compare(a.getPlaycount(), b.getPlaycount()));

        // Value in the range min <= x <= topTracks.size
        int totalTracks = Math.min(topTracks.size(), Math.max(min, tracksWithEnoughPlaycount));

        // Remove unselected tracks
        topTracks.subList(totalTracks, topTracks.size()).clear();
        return topTracks;
    }

    /**
     * Sorts the array in descending order based on the track's playcount.
     * @param tracks array of tracks
     * @return a sorted arraylist of {@link LastfmTrack} with the all tracks selected
     * @throws NullPointerException if the array of tracks is null.
     */
    public ArrayList<LastfmTrack> getUserTracks(ArrayList<LastfmTrack> tracks) {
        if (tracks == null || tracks.isEmpty())
            throw new NullPointerException("array of tracks is null.");

        // Sort list alphabetically
        tracks.sort(LastfmTrack::compareNameTo);

        ArrayList<LastfmTrack> topTracks = new ArrayList<>();

        // Unite tracks with the same name
        topTracks.add(tracks.getFirst());
        for (LastfmTrack track : tracks) {
            if (track.getName().equals(topTracks.getLast().getName())) {
                topTracks.getLast().increasePlaycount(1);
            }
            else {
                track.increasePlaycount(1);
                topTracks.add(track);
            }
        }

        topTracks.sort((a,b) -> -1 * compare(a.getPlaycount(), b.getPlaycount()));
        return topTracks;
    }
}

/**
 * The {@link ResponseInformation} class contains the response information of the API request.
 * Every response from the user's request page contains a field {@code "@attr"},
 * which stores information about the request: the username, the number
 * of pages from that request, the actual page and the total numbers of items.
 */
class ResponseInformation {
    private String user;
    private int totalPages;
    private int page;
    private int total;

    /**
     * Get the total number of pages from this response.
     * @return an integer with the number of pages
     */
    public int getTotalPages() {
        return totalPages;
    }

    /**
     * @return an integer with the number of scrobbles from the period
     */
    public int getTotal() {
        return total;
    }
}
