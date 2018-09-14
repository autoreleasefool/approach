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
import ca.josephroque.bowlingcompanion.database.DatabaseHelper
import kotlinx.coroutines.experimental.CoroutineExceptionHandler
import kotlinx.coroutines.experimental.Job
import kotlinx.coroutines.experimental.JobCancellationException
import kotlinx.coroutines.experimental.yield
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.MalformedURLException
import java.net.SocketTimeoutException
import java.net.URL
import java.io.DataInputStream
import java.io.DataOutputStream
import java.io.FileInputStream
import java.io.FileNotFoundException
import java.io.FileOutputStream

/**
 * Copyright (C) 2018 Joseph Roque
 *
 * Manage a connection to the transfer server.
 */
class TransferServerConnection private constructor(context: Context) {

    companion object {
        @Suppress("unused")
        private const val TAG = "TranServerConnection"

        private const val CONNECTION_TIMEOUT = 1000 * 10
        private const val MULTIPART_BOUNDARY = "*****"
        private const val HYPHEN_SEPARATOR = "--"
        private const val LINE_END = "\r\n"
        private const val MAX_BUFFER_SIZE = 32 * 1024
        const val VALID_KEY_LENGTH = 5

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
            Error;

            val isTransferring: Boolean
                get() {
                    return when (this) {
                        Waiting, Connecting, Connected, Error -> false
                        Loading, Uploading, Downloading -> true
                    }
                }
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
            onStateChanged(field, serverError)
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

            onStateChanged(_state, serverError)
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

            onStateChanged(_state, serverError)
        }
        get() = cancelButtonWrapper?.get()

    // MARK: Endpoints

    private val statusEndpoint: String = listOf(BuildConfig.TRANSFER_SERVER_URL, "status").joinToString("/")

    private val uploadEndpoint: String = listOf(BuildConfig.TRANSFER_SERVER_URL, "upload").joinToString("/")

    private fun downloadEndpoint(key: String): String = listOf(BuildConfig.TRANSFER_SERVER_URL, "download?key=$key").joinToString("/")

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

    private fun publishProgress(progress: Int) {
        if (!state.isTransferring) { return }
        launch(Android) {
            progressView?.setProgress(progress)
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

    fun prepareConnection(parentJob: Job? = null): Deferred<Boolean> {
        val exceptionHandler = CoroutineExceptionHandler { _, throwable ->
            _state = State.Error
            serverError = if (throwable is JobCancellationException) {
                ServerError.Cancelled
            } else {
                ServerError.Unknown
            }
        }

        return async(CommonPool + exceptionHandler, parent = parentJob) {
            _state = State.Connecting
            val error: ServerError? = try {
                internalPrepareConnection(parentJob, exceptionHandler).await()
            } catch (ex: JobCancellationException) {
                ServerError.Cancelled
            }

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

    private fun internalPrepareConnection(parentJob: Job?, exceptionHandler: CoroutineExceptionHandler): Deferred<ServerError?> {
        return async(CommonPool + exceptionHandler, parent = parentJob) {
            if (!isConnectionAvailable()) {
                return@async ServerError.NoInternet
            }

            try {
                yield()
                val url = URL(statusEndpoint)
                val connection = url.openConnection() as HttpURLConnection
                val response = getConnectionBody(connection)

                Log.d(TAG, "Transfer server status response: $response")

                // The server is only ready to accept uploads if it responds with "OK"
                if (response == "OK") {
                    yield()
                    _state = State.Connected
                    return@async null
                } else {
                    return@async ServerError.ServerUnavailable
                }
            } catch (ex: JobCancellationException) {
                Log.e(TAG, "prepareConnection - Connection was cancelled before it could be opened.", ex)
                return@async ServerError.Cancelled
            } catch (ex: MalformedURLException) {
                Log.e(TAG, "prepareConnection - Error parsing URL. This shouldn't happen.", ex)
                return@async ServerError.MalformedURL
            } catch (ex: SocketTimeoutException) {
                Log.e(TAG, "prepareConnection - Server timed out during connection.", ex)
                return@async ServerError.Timeout
            } catch (ex: IOException) {
                Log.e(TAG, "prepareConnection - Error opening or closing connection getting status.", ex)
                return@async ServerError.IOException
            } catch (ex: Exception) {
                return@async ServerError.ServerUnavailable
            }
        }
    }

    fun isKeyValid(key: String, parentJob: Job?): Deferred<Boolean> {
        val exceptionHandler = CoroutineExceptionHandler { _, throwable ->
            _state = State.Error
            serverError = if (throwable is JobCancellationException) {
                ServerError.Cancelled
            } else {
                ServerError.Unknown
            }
        }

        return async(CommonPool + exceptionHandler, parent = parentJob) {
            val error = internalIsKeyValid(key, parentJob, exceptionHandler).await()
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

    private fun internalIsKeyValid(key: String, parentJob: Job?, exceptionHandler: CoroutineExceptionHandler): Deferred<ServerError?> {
        return async(CommonPool + exceptionHandler, parent = parentJob) {
            assert(_state == State.Connected) { "Ensure the server is connected before you contact it." }
            _state = State.Loading
            if (!isConnectionAvailable()) {
                return@async ServerError.NoInternet
            }

            try {
                yield()
                val url = URL(validKeyEndpoint(key))
                val connection = url.openConnection() as HttpURLConnection
                val response = getConnectionBody(connection)

                Log.d(TAG, "Transfer server status response: $response")

                // The key is only valid if the server responds with "VALID"
                yield()
                if (response == "VALID") {
                    return@async null
                } else {
                    return@async ServerError.InvalidKey
                }
            } catch (ex: JobCancellationException) {
                Log.e(TAG, "isKeyValid - User cancelled connection.")
                return@async ServerError.Cancelled
            } catch (ex: MalformedURLException) {
                Log.e(TAG, "isKeyValid - Error parsing URL. This shouldn't happen.", ex)
                return@async ServerError.MalformedURL
            } catch (ex: SocketTimeoutException) {
                Log.e(TAG, "isKeyValid - Server timed out during connection.", ex)
                return@async ServerError.Timeout
            } catch (ex: IOException) {
                Log.e(TAG, "isKeyValid - Error opening or closing connection validating key.", ex)
                return@async ServerError.IOException
            } catch (ex: Exception) {
                return@async ServerError.Unknown
            }
        }
    }

    fun uploadUserData(parentJob: Job? = null): Deferred<String?> {
        val exceptionHandler = CoroutineExceptionHandler { _, throwable ->
            _state = State.Error
            serverError = if (throwable is JobCancellationException) {
                ServerError.Cancelled
            } else {
                ServerError.Unknown
            }
        }

        return async(CommonPool + exceptionHandler, parent = parentJob) {
            var error: ServerError?
            var key: String?
            try {
                val errorAndKey = internalUploadUserData(parentJob, exceptionHandler).await()
                error = errorAndKey.first
                key = errorAndKey.second
            } catch (ex: JobCancellationException) {
                error = ServerError.Cancelled
                key = null
            }

            if (error == null) {
                _state = State.Connected
                return@async key
            } else {
                _state = State.Error
                serverError = error
                return@async null
            }
        }
    }

    // Most of this method retrieved from this StackOverflow question.
    // http://stackoverflow.com/a/7645328/4896787
    private fun internalUploadUserData(parentJob: Job? = null, exceptionHandler: CoroutineExceptionHandler): Deferred<Pair<ServerError?, String?>> {
        return async(CommonPool + exceptionHandler, parent = parentJob) {
            assert(_state == State.Connected) { "Ensure the server is connected before you contact it." }
            _state = State.Loading
            if (!isConnectionAvailable()) {
                return@async Pair(ServerError.NoInternet, null)
            }

            val context = this@TransferServerConnection.context?.get() ?: return@async Pair(ServerError.Unknown, null)
            val dbFile = context.getDatabasePath(DatabaseHelper.DATABASE_NAME)

            var fileInputStream: FileInputStream? = null
            var outputStream: DataOutputStream? = null
            var reader: BufferedReader? = null

            _state = State.Uploading
            try {
                yield()
                fileInputStream = FileInputStream(dbFile)
                val url = URL(uploadEndpoint)

                // Preparing connection for upload
                val connection = url.openConnection() as HttpURLConnection
                connection.doInput = true
                connection.doOutput = true
                connection.useCaches = false
                connection.connectTimeout = CONNECTION_TIMEOUT
                connection.readTimeout = CONNECTION_TIMEOUT
                connection.requestMethod = "POST"
                connection.setRequestProperty("Connection", "Keep-Alive")
                connection.setRequestProperty("Content-Type", "multipart/form-data;boundary=$MULTIPART_BOUNDARY")
                connection.setRequestProperty("Authorization", BuildConfig.TRANSFER_API_KEY)

                yield()
                outputStream = DataOutputStream(connection.outputStream)
                outputStream.writeBytes("$HYPHEN_SEPARATOR$MULTIPART_BOUNDARY$LINE_END")
                outputStream.writeBytes("Content-Disposition: form-data; name=\"uploadedfile\";filename=\"${dbFile.name}\"$LINE_END")
                outputStream.writeBytes(LINE_END)

                val totalBytes = fileInputStream.available()
                var lastProgressPercentage = 0
                var bytesAvailable = totalBytes
                var bufferSize = Math.min(bytesAvailable, MAX_BUFFER_SIZE)
                val buffer = ByteArray(bufferSize)

                yield()
                var bytesRead = fileInputStream.read(buffer, 0, bufferSize)
                try {
                    while (bytesRead > 0) {
                        yield()
                        try {
                            outputStream.write(buffer, 0, bufferSize)
                        } catch (ex: OutOfMemoryError) {
                            Log.e(TAG, "Out of memory sending file.", ex)
                            return@async Pair(ServerError.OutOfMemory, null)
                        }

                        // Update the progress bar
                        val currentProgressPercentage = ((totalBytes - bytesAvailable) / totalBytes.toFloat() * 100).toInt()
                        if (currentProgressPercentage > lastProgressPercentage) {
                            lastProgressPercentage = currentProgressPercentage
                            publishProgress(currentProgressPercentage)
                        }

                        bytesAvailable = fileInputStream.available()
                        bufferSize = Math.min(bytesAvailable, MAX_BUFFER_SIZE)
                        bytesRead = fileInputStream.read(buffer, 0, bufferSize)
                    }
                } catch (ex: JobCancellationException) {
                    Log.e(TAG, "upload - User cancelled upload.")
                    return@async Pair(ServerError.Cancelled, null)
                } catch (ex: Exception) {
                    Log.e(TAG, "upload - Error sending file.", ex)
                    return@async Pair(ServerError.Unknown, null)
                }

                yield()
                outputStream.writeBytes(LINE_END)
                outputStream.writeBytes("$HYPHEN_SEPARATOR$MULTIPART_BOUNDARY$HYPHEN_SEPARATOR$LINE_END")
                publishProgress(100)

                val responseBuilder = StringBuilder()
                if (connection.responseCode == HttpURLConnection.HTTP_OK) {
                    try {
                        yield()
                        reader = BufferedReader(InputStreamReader(DataInputStream(connection.inputStream)))
                        var line = reader.readLine()
                        while (line != null) {
                            yield()
                            responseBuilder.append(line)
                            line = reader.readLine()
                        }
                    } catch (ex: JobCancellationException) {
                        Log.e(TAG, "upload - User cancelled upload.")
                        return@async Pair(ServerError.Cancelled, null)
                    } catch (ex: IOException) {
                        Log.e(TAG, "upload - Error reading server response.", ex)
                        return@async Pair(ServerError.IOException, null)
                    } finally {
                        try {
                            reader?.close()
                        } catch (ex: IOException) {
                            Log.e(TAG, "upload - Error closing stream.", ex)
                        }
                    }
                }

                return@async if (!isActive) {
                    Pair(ServerError.Cancelled, null)
                } else {
                    Pair(null, responseBuilder.toString())
                }
            } catch (ex: JobCancellationException) {
                Log.e(TAG, "upload - User cancelled upload.")
                return@async Pair(ServerError.Cancelled, null)
            } catch (ex: FileNotFoundException) {
                Log.e(TAG, "upload - Unable to find database file.", ex)
                return@async Pair(ServerError.FileNotFound, null)
            } catch (ex: MalformedURLException) {
                Log.e(TAG, "upload - Malformed url. I have no idea how this happened.", ex)
                return@async Pair(ServerError.MalformedURL, null)
            } catch (ex: SocketTimeoutException) {
                Log.e(TAG, "upload - Timed out reading response.", ex)
                return@async Pair(ServerError.Timeout, null)
            } catch (ex: IOException) {
                Log.e(TAG, "upload - Couldn't open or maintain connection.", ex)
                return@async Pair(ServerError.IOException, null)
            } catch (ex: Exception) {
                Log.e(TAG, "upload - Unknown exception.", ex)
                return@async Pair(ServerError.Unknown, null)
            } finally {
                try {
                    fileInputStream?.close()
                    outputStream?.let {
                        it.flush()
                        it.close()
                    }
                } catch (ex: IOException) {
                    Log.e(TAG, "upload - Error closing streams.", ex)
                }
            }
        }
    }

    fun downloadUserData(key: String, parentJob: Job?): Deferred<Boolean> {
        val exceptionHandler = CoroutineExceptionHandler { _, throwable ->
            _state = State.Error
            serverError = if (throwable is JobCancellationException) {
                ServerError.Cancelled
            } else {
                ServerError.Unknown
            }
        }

        return async(CommonPool + exceptionHandler, parent = parentJob) {
            val error = internalDownloadUserData(key, parentJob, exceptionHandler).await()
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

    fun internalDownloadUserData(key: String, parentJob: Job?, exceptionHandler: CoroutineExceptionHandler): Deferred<ServerError?> {
        return async(CommonPool + exceptionHandler, parent = parentJob) {
            assert(_state == State.Connected) { "Ensure the server is connected before you contact it." }
            _state = State.Loading
            if (!isConnectionAvailable()) {
                return@async ServerError.NoInternet
            } else if (!isKeyValid(key, parentJob).await()) {
                return@async ServerError.InvalidKey
            }

            val context = this@TransferServerConnection.context?.get() ?: return@async ServerError.Unknown
            val userData = UserData(context)

            _state = State.Downloading
            try {
                yield()
                val url = URL(downloadEndpoint(key))

                // Preparing connection for upload
                val connection = url.openConnection() as HttpURLConnection
                connection.readTimeout = CONNECTION_TIMEOUT
                connection.connectTimeout = CONNECTION_TIMEOUT

                val contentLength = connection.contentLength
                val inputStream = url.openStream()
                val outputStream = FileOutputStream(userData.downloadFile)

                val data = ByteArray(MAX_BUFFER_SIZE)
                var totalDataRead = 0
                var lastProgressPercentage = 0

                try {
                    yield()
                    var dataRead = inputStream!!.read(data)
                    while (dataRead != -1) {
                        yield()
                        totalDataRead += dataRead

                        // Update the progress bar
                        val currentProgressPercentage = (totalDataRead / contentLength.toFloat() * 100).toInt()
                        if (currentProgressPercentage > lastProgressPercentage) {
                            lastProgressPercentage = currentProgressPercentage
                            publishProgress(currentProgressPercentage)
                        }

                        outputStream.write(data, 0, dataRead)
                        dataRead = inputStream.read(data)
                    }
                } catch (ex: JobCancellationException) {
                    Log.e(TAG, "download - User cancelled download.")
                    return@async ServerError.Cancelled
                } catch (ex: Exception) {
                    Log.e(TAG, "download - Error receiving file.", ex)
                    return@async ServerError.Unknown
                } finally {
                    try {
                        outputStream.close()
                        inputStream?.close()
                    } catch (ex: IOException) {
                        Log.e(TAG, "download - Error closing streams.", ex)
                        return@async ServerError.IOException
                    }
                }
                yield()
                publishProgress(100)
            } catch (ex: JobCancellationException) {
                Log.e(TAG, "download - User cancelled download.")
                return@async ServerError.Cancelled
            } catch (ex: MalformedURLException) {
                Log.e(TAG, "Malformed url. I have no idea how this happened.", ex)
                return@async ServerError.MalformedURL
            } catch (ex: SocketTimeoutException) {
                Log.e(TAG, "Timed out reading response.", ex)
                return@async ServerError.Timeout
            } catch (ex: IOException) {
                Log.e(TAG, "Couldn't open or maintain connection.", ex)
                return@async ServerError.IOException
            } catch (ex: Exception) {
                Log.e(TAG, "Unknown exception.", ex)
                return@async ServerError.Unknown
            }

            return@async null
        }
    }
}
