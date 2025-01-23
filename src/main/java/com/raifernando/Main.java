package com.raifernando;

import com.raifernando.lastfm.User;
import java.io.IOException;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;

public class Main {
    public static void main(String[] args) throws IOException, InterruptedException {
        com.raifernando.lastfm.Credentials.loadKeys();
        User user = new User("rrrarrsr");

        ZoneId zoneId = ZoneId.systemDefault();

        LocalDate startDate = LocalDate.of(2021, 12, 1);
        LocalDate endDate = LocalDate.of(2026, 1, 1);

        long startUnix = startDate.atStartOfDay(zoneId).toEpochSecond();
        long endUnix = endDate.atStartOfDay(zoneId).toEpochSecond();

        ArrayList<com.raifernando.lastfm.Track> tracks = user.getRecentTracks(String.valueOf(startUnix), String.valueOf(endUnix));

        user.getUserTopTracks(tracks, 1,1);
    }
}