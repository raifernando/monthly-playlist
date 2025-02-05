package com.raifernando.lastfm;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import com.raifernando.util.Credentials;
import com.raifernando.util.DateRange;
import com.raifernando.util.PropertiesFile;
import com.raifernando.util.Request;

import java.lang.reflect.Type;
import java.security.InvalidParameterException;
import java.util.ArrayList;

import static java.lang.Integer.compare;

public class LastfmUser {
    private String user;

    public LastfmUser() {}

    public LastfmUser(String user) {
        this.user = user;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public void setUser(String [] args) {
        if (args.length == 3)
            user = args[0];
        else {
            PropertiesFile propertiesFile = new PropertiesFile();
            user = propertiesFile.get("LASTFM_USER");
        }

        if (user == null || user.isEmpty())
            throw new InvalidParameterException();
    }

    public ArrayList<LastfmTrack> getTopTracks() {
        String url = "http://ws.audioscrobbler.com/2.0/?method=user.gettoptracks&user=" + user
                + "&api_key=" + Credentials.lastfmApiKey +"&format=json";

        JsonObject jsonObject = Request.requestGetNoHeader(url, JsonObject.class);

        System.out.println(jsonObject.toString());

        JsonArray tracksJson = jsonObject.getAsJsonObject("toptracks").getAsJsonArray("track");
        Gson gson = new Gson();

        Response response = gson.fromJson(jsonObject, Response.class);
        System.out.println(response.toString());

        Type arrayType = new TypeToken<ArrayList<LastfmTrack>>(){}.getType();

        return gson.fromJson(tracksJson, arrayType);
    }

    public ArrayList<LastfmTrack> getRecentTracks(DateRange dateRange) {
        System.out.print("Requesting Lastfm data. Page: ");

        String url = "http://ws.audioscrobbler.com/2.0/?method=user.getrecenttracks&user="
                + user + "&from=" + dateRange.getStartDate() + "&to=" + dateRange.getEndDate() +"&limit=200"
                + "&api_key=" + Credentials.lastfmApiKey +"&format=json";

        Gson gson = new Gson();

        Response response = new Response();
        ArrayList<LastfmTrack> tracks =  new ArrayList<>();
        Type arrayType = new TypeToken<ArrayList<LastfmTrack>>(){}.getType();

        int currentPage = 1;
        do {
            System.out.printf("%d ", currentPage);

            JsonObject jsonObject = Request.requestGetNoHeader(url + "&page=" + currentPage, JsonObject.class)
                    .getAsJsonObject("recenttracks");

            if (currentPage == 1)
                response = gson.fromJson(jsonObject.getAsJsonObject("@attr"), Response.class);

            JsonArray trackJsonArray = jsonObject.getAsJsonArray("track");
            ArrayList<LastfmTrack> newTracks = gson.fromJson(trackJsonArray, arrayType);

            // The first track in the API is the currently playing track
            newTracks.removeFirst();

            tracks.addAll(newTracks);
        } while (currentPage++ < response.getTotalPages());

        System.out.println();

        return tracks;
    }

    /**
     * @param min: minimum quantity of tracks
     * @param max: maximum quantity of tracks
     * @param playcountPerTrack: add all tracks with this playcount, overriding the max count; set to 0 to respect the max parameter
     */
    public ArrayList<LastfmTrack> getUserTopTracks(ArrayList<LastfmTrack> tracks, int min, int max, int playcountPerTrack) {
        ArrayList<LastfmTrack> topTracks = new ArrayList<>();

        tracks.sort(( (a, b) -> { return -1 * a.compareNameTo(b); } ));

        topTracks.add(tracks.getFirst());
        for (var track : tracks) {
            if (track.getName().equals(topTracks.getLast().getName())) {
                topTracks.getLast().increasePlaycount(1);
            }
            else {
                track.increasePlaycount(1);
                topTracks.add(track);
            }
        }

        topTracks.sort(( (a, b) -> { return -1 * compare(a.getPlaycount(), b.getPlaycount()); } ));

        int arraySize = min;
        if (playcountPerTrack != 0) {
            for (; arraySize < topTracks.size(); arraySize++) {
                if (playcountPerTrack > topTracks.get(arraySize - 1).getPlaycount())
                    break;
            }
        }
        else
            arraySize = max;

        topTracks.subList(Integer.min(arraySize, topTracks.size()), topTracks.size()).clear();

        return topTracks;
    }
}
