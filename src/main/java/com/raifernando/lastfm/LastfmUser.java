package com.raifernando.lastfm;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import javax.sound.midi.SysexMessage;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;

import static java.lang.Integer.compare;

public class LastfmUser {
    private String user;

    public LastfmUser(String user) {
        this.user = user;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public ArrayList<LastfmTrack> getTopTracks() throws IOException, InterruptedException {
        String url = "http://ws.audioscrobbler.com/2.0/?method=user.gettoptracks&user=" + user;

        JsonObject jsonObject = Request.requestGet(url);

        System.out.println(jsonObject.toString());

        JsonArray tracksJson = jsonObject.getAsJsonObject("toptracks").getAsJsonArray("track");
        Gson gson = new Gson();

        Response response = gson.fromJson(jsonObject, Response.class);
        System.out.println(response.toString());

        Type arrayType = new TypeToken<ArrayList<LastfmTrack>>(){}.getType();

        return gson.fromJson(tracksJson, arrayType);
    }

    public ArrayList<LastfmTrack> getRecentTracks(String startDate, String endDate) throws IOException, InterruptedException {
        // TODO: save response in json file

        String url = "http://ws.audioscrobbler.com/2.0/?method=user.getrecenttracks&user="
                + user + "&from=" + startDate + "&to=" + endDate +"&limit=200";

        Gson gson = new Gson();

        Response response = new Response();
        ArrayList<LastfmTrack> tracks =  new ArrayList<>();
        Type arrayType = new TypeToken<ArrayList<LastfmTrack>>(){}.getType();

        int currentPage = 1;

        do {
            JsonObject jsonObject = Request.requestGet(url + "&page=" + currentPage)
                    .getAsJsonObject("recenttracks");

            if (currentPage == 1)
                response = gson.fromJson(jsonObject.getAsJsonObject("@attr"), Response.class);

            JsonArray trackJsonArray = jsonObject.getAsJsonArray("track");
            ArrayList<LastfmTrack> newTracks = gson.fromJson(trackJsonArray, arrayType);

            // The first track in the API is the currently playing track
            newTracks.removeFirst();

            tracks.addAll(newTracks);
        } while (currentPage++ < response.getTotalPages());

        return tracks;
    }

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

        if (max > 0  && topTracks.size() > max) {
            topTracks.subList(max, topTracks.size()).clear();
        }

        for (var track: topTracks) {
            System.out.println(track.getPlaycount() + " " + track.getName());
        }

        return topTracks;
    }
}
