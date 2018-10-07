package dev.eaesh.passtheaux;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.spotify.android.appremote.api.ConnectionParams;
import com.spotify.android.appremote.api.Connector;
import com.spotify.android.appremote.api.PlayerApi;
import com.spotify.android.appremote.api.SpotifyAppRemote;
import com.spotify.protocol.client.CallResult;
import com.spotify.protocol.client.ErrorCallback;
import com.spotify.protocol.client.Subscription;
import com.spotify.protocol.types.PlayerState;
import com.spotify.protocol.types.Track;

public class Home extends AppCompatActivity {

    private static final String CLIENT_ID = "8d7aebbeab044bc58fa49efa83948621";
    private static final String REDIRECT_URI = "dev.eaesh.passtheaux://callback";
    private SpotifyAppRemote mSpotifyAppRemote;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
    }

    @Override
    protected void onStart() {
        super.onStart();

        // Set Connection Parameters
        ConnectionParams connectionParams =
                new ConnectionParams.Builder(CLIENT_ID)
                .setRedirectUri(REDIRECT_URI)
                .showAuthView(true)
                .build();

        // Connect to Spotify Android App
        SpotifyAppRemote.CONNECTOR.connect(this, connectionParams,
            new Connector.ConnectionListener() {

                @Override
                public void onConnected(SpotifyAppRemote spotifyAppRemote) {
                    mSpotifyAppRemote = spotifyAppRemote;
                    Log.d("Home", "----- Connected to Spotify -----");
                    //connected();
                }

                @Override
                public void onFailure(Throwable throwable) {
                    Log.e("Home", throwable.getMessage(), throwable);
                    // Handle Errors Here
                }
            });
    }

    private void connected() {
        // Play a Playist
        mSpotifyAppRemote.getPlayerApi().play("spotify:user:spotify:playlist:37i9dQZF1DX2sUQwD7tbmL");

        // Subscribe to PlayerState
        mSpotifyAppRemote.getPlayerApi()
            .subscribeToPlayerState().setEventCallback(
            new Subscription.EventCallback<PlayerState>() {
                @Override
                public void onEvent(PlayerState playerState) {
                    final Track track = playerState.track;
                    if (track != null) {
                        Log.d("Home", "----- " + track.name + " by " + track.artist.name);
                    }
                }
            });
        //*/
    }

    @Override
    protected void onStop() {
        super.onStop();
        SpotifyAppRemote.CONNECTOR.disconnect(mSpotifyAppRemote);
    }

    public void search(View view) {
        EditText searchTextBox = (EditText) findViewById(R.id.searchTextBox);
        TextView resultsTextView = (TextView) findViewById(R.id.resultsTextView);

        String searchText = searchTextBox.getText().toString();
        resultsTextView.setText(searchText);
    }

    public void testPlayerState(View view) {
        PlayerApi playerApi = mSpotifyAppRemote.getPlayerApi();
        playerApi.getPlayerState()
            .setResultCallback(new CallResult.ResultCallback<PlayerState>() {
                @Override
                public void onResult(PlayerState playerState) {
                    String message = "isPaused(): " + playerState.isPaused + "\n";
                    message += "toString(): " + playerState.toString();

                    TextView resultsTextView = (TextView) findViewById(R.id.resultsTextView);
                    resultsTextView.setText(message);
                }
            })
            .setErrorCallback(new ErrorCallback() {
                @Override
                public void onError(Throwable throwable) {
                    Log.e("Home", "----- SPOTIFY ERROR: " + throwable.getMessage() + " -----", throwable);
                }
            });
    }
}
