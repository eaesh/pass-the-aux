package dev.eaesh.passtheaux;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.spotify.android.appremote.api.ConnectionParams;
import com.spotify.android.appremote.api.Connector;
import com.spotify.android.appremote.api.PlayerApi;
import com.spotify.android.appremote.api.SpotifyAppRemote;
import com.spotify.protocol.client.CallResult;
import com.spotify.protocol.client.ErrorCallback;
import com.spotify.protocol.client.Subscription;
import com.spotify.protocol.types.PlayerState;
import com.spotify.protocol.types.Track;

import java.util.HashMap;
import java.util.Map;

public class Home extends AppCompatActivity {

    private static final String CLIENT_ID = "8d7aebbeab044bc58fa49efa83948621";
    private static final String CLIENT_SECRET = "8d3129d3c0294501980da7e41bbadad2";
    private static final String AUTH_TOKEN = "Bearer BQB2jTaRHSA_Fh00drzmZSZLkbQn_xSULR6jBOaW3aSLcAe9PqfHwoPSwlkaslDfJJFsOBuJFDystf02yM8";
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

    @Override
    protected void onStop() {
        super.onStop();
        SpotifyAppRemote.CONNECTOR.disconnect(mSpotifyAppRemote);
    }

    public void getAuthToken() {

    }

    public void search(View view) {
        // View Elements
        final TextView resultsTextView = (TextView) findViewById(R.id.resultsTextView);
        String searchText = ((EditText) findViewById(R.id.searchTextBox)).getText().toString();
        String url = "https://api.spotify.com/v1/search?type=track&q=" + searchText;

        RequestQueue queue = Volley.newRequestQueue(this);
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d("Home", response);
                        resultsTextView.setText(response);
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("Home", "----- HTTP Request Error -----\t"
                                + error.toString());
                    }
        }) {
            // Headers
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<String, String>();
                headers.put("Content-Type", "application/x-www-form-urlencoded");
                headers.put("Authorization", AUTH_TOKEN);
                return headers;
            }
        };
        queue.add(stringRequest);
    }
}
