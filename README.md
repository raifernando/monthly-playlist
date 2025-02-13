# monthly-playlist

This application creates a Spotify playlist based on your most-played tracks from last.fm for a specified month.
It retrieves your scrobbles from last.fm, sorts them by playcount, and creates a new playlist on Spotify.


## Requirements

Since requests to both Spotify and Last.fm API are required,
it is necessary to have the credentials for both APIs.

- For Spotify*: log in with an account at [Spotify Developer](https://developer.spotify.com/) and create a new app at the Dashboard. Set the Redirect URI to `http://localhost:8080/callback`.
- For Last.fm: create an API account at [last.fm API](https://www.last.fm/api/account/create).

*Note: to use the same API credentials with another account, it is necessary to add it to the user's allowlist. See [this](https://developer.spotify.com/documentation/web-api/concepts/quota-modes#adding-a-user-to-your-apps-allowlist) for more information.

For building and running the project,
 you need [Java JDK 23](https://docs.oracle.com/en/java/javase/23/install/overview-jdk-installation.html) and [Maven](https://maven.apache.org/install.html).


## Build

To run this application locally, first download the project or clone this repository using:
```commandline
git clone https://github.com/raifernando/monthly-playlist.git
```

Build the project with
```commandline
make all
```
This will create an executable `.jar` file and generate a new `config.properties` file.
In the file, fill the fields with the API credentials obtained previously.


## Run

After building the project, run this application with
```commandline
sh run.sh [lastfmuser] [month] [year]
```

where 
- **lastfmuser**: the last.fm user to retrieve data from. This argument is optional. If not provided, the user specified in the properties file will be used.

- **month**: the month for which the playlist will be created. It is accepted the month number or by its English name or abbreviation.

- **year**: the year corresponding the requested month.

The first time running, you'll be prompted to authorize the application created at Spotify Developer to modify/create playlists in your account.
Once authorized, the playlist with the selected tracks will be created.

If you wish to create a playlist in another account, add it to the [user's allowlist](https://developer.spotify.com/documentation/web-api/concepts/quota-modes#adding-a-user-to-your-apps-allowlist) and run `make signout` to be prompted again to authorize the other account.