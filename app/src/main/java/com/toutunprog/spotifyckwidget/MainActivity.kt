package com.toutunprog.spotifyckwidget

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import com.spotify.android.appremote.api.ConnectionParams
import com.spotify.android.appremote.api.Connector
import com.spotify.android.appremote.api.SpotifyAppRemote
import com.spotify.protocol.types.PlayerState
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity() {

    private val CLIENT_ID = "48bfa25626844137bcbac9adc96143a4"
    private val REDIRECT_URI = "com.yourdomain.yourapp://callback"
    private var mSpotifyAppRemote: SpotifyAppRemote? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    override fun onStart() {
        super.onStart()
        val connectionParams = ConnectionParams.Builder(CLIENT_ID)
            .setRedirectUri(REDIRECT_URI)
            .showAuthView(true)
            .build()

        SpotifyAppRemote.connect(this, connectionParams,
            object : Connector.ConnectionListener {

                override fun onConnected(spotifyAppRemote: SpotifyAppRemote) {
                    mSpotifyAppRemote = spotifyAppRemote
                    Log.d("MainActivity", "Connected! Yay!")

                    // Now you can start interacting with App Remote
                    connected()

                }

                override fun onFailure(throwable: Throwable) {
                    Log.e("MyActivity", throwable.message, throwable)

                    // Something went wrong when attempting to connect! Handle errors here
                }
            })
    }

    private fun connected() {
        val playerState = mSpotifyAppRemote?.playerApi?.playerState
        playerState?.setResultCallback { playerState ->
            setViewFromPlayerState(playerState)
        }
        mSpotifyAppRemote?.playerApi?.subscribeToPlayerState()?.setEventCallback { playerState ->
            setViewFromPlayerState(playerState)
        }
    }

    override fun onStop() {
        super.onStop()
        SpotifyAppRemote.disconnect(mSpotifyAppRemote);
    }

    private fun setViewFromPlayerState(playerState: PlayerState) {
        val track = playerState.track
        trackTitle.text = track.name
        val trackArtistAlbumText = "${track.artist.name} - ${track.album.name}"
        trackArtistAlbum.text = trackArtistAlbumText
        mSpotifyAppRemote?.imagesApi?.getImage(track.imageUri)?.setResultCallback { bitmap ->
            trackImageView.setImageBitmap(bitmap)
        }

    }
}
