package ca.josephroque.bowlingcompanion.transfer

import android.content.Context
import android.view.View
import android.widget.Button
import ca.josephroque.bowlingcompanion.R
import ca.josephroque.bowlingcompanion.common.Android
import ca.josephroque.bowlingcompanion.transfer.view.ProgressView
import kotlinx.coroutines.experimental.CommonPool
import kotlinx.coroutines.experimental.Deferred
import kotlinx.coroutines.experimental.async
import kotlinx.coroutines.experimental.launch
import java.lang.ref.WeakReference
import android.net.ConnectivityManager
import android.util.Log
import ca.josephroque.bowlingcompanion.BuildConfig
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.MalformedURLException
import java.net.URL


/**
 * Copyright (C) 2018 Joseph Roque
 */
class TransferServerConnection private constructor(context: Context) {

    companion object {
        @Suppress("unused")
        private const val TAG = "TranServerConnection"

        private const val CONNECTION_TIMEOUT = 1000 * 10

        fun openConnection(context: Context): TransferServerConnection {
            return TransferServerConnection(context)
        }

        enum class State {
            Waiting,
            Connecting,
            Connected,
            Loading,
            Uploading,
            Downloading,
            Error
        }

        enum class ServerError(val message: Int) {
            NoInternet(R.string.error_no_internet),
            InvalidKey(R.string.error_invalid_key),
            ServerUnavailable(R.string.error_server_unavailable),
            Timeout(R.string.error_timeout),
            Cancelled(R.string.error_cancelled),
            IOException(R.string.error_unknown),
            OutOfMemory(R.string.error_out_of_memory),
            FileNotFound(R.string.error_file_not_found),
            MalformedURL(R.string.error_unknown),
            Unknown(R.string.error_unknown)
        }
    }

    private var context: WeakReference<Context>? = WeakReference(context)

    private var _state: State = State.Waiting
        set(value) {
            field = value
            onStateChanged(field)
        }
    val state: State
        get() = _state

    private var serverError: ServerError? = null
        set(value) {
            field = value
            onStateChanged(state, field)
        }

    private var progressViewWrapper: WeakReference<ProgressView>? = null
    var progressView: ProgressView?
        set(value) {
            progressViewWrapper = if (value == null) {
                null
            } else {
                WeakReference(value)
            }
        }
        get() = progressViewWrapper?.get()

    private var cancelButtonWrapper: WeakReference<Button>? = null
    var cancelButton: Button?
        set(value) {
            cancelButtonWrapper = if (value == null) {
                null
            } else {
                WeakReference(value)
            }
        }
        get() = cancelButtonWrapper?.get()

    // MARK: Endpoints

    private val statusEndpoint: String = listOf(BuildConfig.TRANSFER_SERVER_URL, "status").joinToString("/")

    private val uploadEndpoint: String = listOf(BuildConfig.TRANSFER_SERVER_URL, "upload").joinToString("/")

    private fun downloadEnpoint(key: String): String = listOf(BuildConfig.TRANSFER_SERVER_URL, "download?key=$key").joinToString("/")

    private fun validKeyEndpoint(key: String): String = listOf(BuildConfig.TRANSFER_SERVER_URL, "valid?key=$key").joinToString("/")

    // MARK: Private functions

    private fun isConnectionAvailable(): Boolean {
        val cm = (context?.get()?.getSystemService(Context.CONNECTIVITY_SERVICE) as? ConnectivityManager) ?: return false
        val activeNetwork = cm.activeNetworkInfo
        return activeNetwork != null && activeNetwork.isConnectedOrConnecting
    }

    private fun onStateChanged(state: State, error: ServerError? = null) {
        launch(Android) {
            val context = this@TransferServerConnection.context?.get() ?: return@launch
            when (state) {
                State.Waiting, State.Connected -> {
                    cancelButton?.visibility = View.GONE
                    progressView?.visibility = View.GONE
                }
                State.Connecting, State.Uploading, State.Downloading, State.Loading -> {
                    val statusMessage = when (state) {
                        State.Connecting -> R.string.connecting_to_server
                        State.Uploading -> R.string.uploading
                        State.Downloading -> R.string.downloading
                        State.Loading -> R.string.loading
                        State.Error, State.Waiting, State.Connected -> throw IllegalStateException("Invalid state.")
                    }
                    cancelButton?.visibility = View.VISIBLE
                    progressView?.let {
                        it.setProgress(0)
                        it.setStatus(context.resources.getString(statusMessage))
                        it.visibility = View.VISIBLE
                    }
                }
                State.Error -> {
                    cancelButton?.visibility = View.GONE
                    progressView?.setProgress(0)
                    val errorText = error?.message
                    if (errorText != null) {
                        progressView?.setStatus(context.resources.getString(errorText))
                        progressView?.visibility = View.VISIBLE
                    } else {
                        progressView?.visibility = View.GONE
                    }
                }
            }
        }
    }

    private fun getConnectionBody(connection: HttpURLConnection): String? {
        connection.requestMethod = "GET"
        connection.connectTimeout = CONNECTION_TIMEOUT
        connection.readTimeout = CONNECTION_TIMEOUT

        val responseCode = connection.responseCode
        if (connection.responseCode == HttpURLConnection.HTTP_OK) {
            val responseMsg = StringBuilder()
            val reader = BufferedReader(InputStreamReader(connection.inputStream))
            var line = reader.readLine()
            while (line != null) {
                responseMsg.append(line)
                line = reader.readLine()
            }
            reader.close()

            return responseMsg.toString().trim().toUpperCase()
        } else {
            Log.e(TAG, "Invalid response getting server status: $responseCode")
        }

        return null
    }

    // MARK: Connection functions

    fun prepareConnection(): Deferred<Boolean> {
        return async(CommonPool) {
            _state = State.Connecting
            val error = internalPrepareConnection().await()
            if (error == null) {
                _state = State.Connected
                return@async true
            } else {
                _state = State.Error
                serverError = error
                return@async false
            }
        }
    }

    private fun internalPrepareConnection(): Deferred<ServerError?> {
        return async(CommonPool) {
            if (!isConnectionAvailable()) {
                return@async ServerError.NoInternet
            }

            try {
                val url = URL(statusEndpoint)
                val connection = url.openConnection() as HttpURLConnection
                val response = getConnectionBody(connection)

                Log.d(TAG, "Transfer server status response: $response")

                // The server is only ready to accept uploads if it responds with "OK"
                if (response == "OK") {
                    _state = State.Connected
                    return@async null
                } else {
                    return@async ServerError.Unknown
                }
            } catch (ex: MalformedURLException) {
                Log.e(TAG, "Error parsing URL. This shouldn't happen.", ex)
                return@async ServerError.MalformedURL
            } catch (ex: IOException) {
                Log.e(TAG, "Error opening or closing connection getting status.", ex)
                return@async ServerError.IOException
            } catch (ex: Exception) {
                return@async ServerError.Unknown
            }
        }
    }

    fun isKeyValid(key: String): Deferred<Boolean> {
        return async(CommonPool) {
            _state = State.Loading
            val error = internalIsKeyValid(key).await()
            if (error == null) {
                _state = State.Connected
                return@async true
            } else {
                _state = State.Error
                serverError = error
                return@async false
            }
        }
    }

    private fun internalIsKeyValid(key: String): Deferred<ServerError?> {
        assert(_state == State.Connected) { "Ensure the server is connected before you contact it." }
        return async(CommonPool) {
            if (!isConnectionAvailable()) {
                return@async ServerError.NoInternet
            }

            try {
                val url = URL(validKeyEndpoint(key))
                val connection = url.openConnection() as HttpURLConnection
                val response = getConnectionBody(connection)

                Log.d(TAG, "Transfer server status response: $response")

                // The key is only valid if the server responds with "VALID"
                if (response == "VALID") {
                    return@async null
                } else {
                    return@async ServerError.Unknown
                }
            } catch (ex: MalformedURLException) {
                Log.e(TAG, "Error parsing URL. This shouldn't happen.", ex)
                return@async ServerError.MalformedURL
            } catch (ex: IOException) {
                Log.e(TAG, "Error opening or closing connection validating key.", ex)
                return@async ServerError.IOException
            } catch (ex: Exception) {
                return@async ServerError.Unknown
            }
        }
    }
}
