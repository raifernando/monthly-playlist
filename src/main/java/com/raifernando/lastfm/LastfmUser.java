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
 * This class contains methods used to retrieve information about the user.
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
     * @param user lastfm username
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
     * If there are three arguments, get the username from the first element from the list of arguments.
     * @param args argument list
     * @return the username
     */
    private static String getUserFromArguments(String [] args) {
        if (args != null && args.length == 3)
            return args[0];

        return getUserFromProperties();
    }

    /**
     * Get the username stored in the properties file.
     * @return the username
     */
    private static String getUserFromProperties() {
        PropertiesFile propertiesFile = new PropertiesFile();
        return propertiesFile.get("LASTFM_USER");
    }

    /**
     * Get user's recent tracks based of a date range.
     * @param dateRange data range for the request
     * @return an array list with the tracks. can be null.
     */
    public ArrayList<LastfmTrack> getRecentTracks(DateRange dateRange) {
        System.out.printf("""
                Requesting Lastfm data of %s.
                Receiving scrobbles from %s.
                """, user, dateRange.getFullRange());

        String url = "http://ws.audioscrobbler.com/2.0/?method=user.getrecenttracks&user="
                + user + "&from=" + dateRange.getStartDate() + "&to=" + dateRange.getEndDate() +"&limit=200"
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
     * @return true if is playing, false if not
     */
    private boolean isTrackNowPlaying(JsonElement track) {
        return track.getAsJsonObject().get("@attr") != null;
    }

    /**
     * Select all tracks with playcount equals or higher than {@code playcountPerTrack}.
     * If the track quantity is less than {@code min}, all subsequent tracks of the array are added.
     * @param tracks array of tracks
     * @param min: minimum quantity of tracks
     * @param playcountPerTrack value which all tracks with this playcount are added.
     * @return an arraylist of {@link LastfmTrack} with the tracks selected
     * @throws NullPointerException if the array of tracks is null.
     * @throws IllegalArgumentException if min/playcount is 0. See {@link #getUserTracks(ArrayList)}.
     */
    public ArrayList<LastfmTrack> getUserTopTracks(ArrayList<LastfmTrack> tracks, int min, int playcountPerTrack) {
        if (tracks == null || tracks.isEmpty())
            throw new NullPointerException("array of tracks is null.");

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
     * Sort the array based on the track playcount.
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
 * Class containing the response information.
 * Every response from the user's request page contains a field "@attr",
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
