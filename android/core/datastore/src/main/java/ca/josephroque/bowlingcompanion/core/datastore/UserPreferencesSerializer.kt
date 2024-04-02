package ca.josephroque.bowlingcompanion.core.datastore

import androidx.datastore.core.CorruptionException
import androidx.datastore.core.Serializer
import ca.josephroque.bowlingcompanion.core.error.ErrorReporting
import java.io.InputStream
import java.io.OutputStream
import javax.inject.Inject

class UserPreferencesSerializer @Inject constructor(
	private val errorReporting: ErrorReporting,
) : Serializer<UserPreferences> {
	override val defaultValue: UserPreferences = UserPreferences.getDefaultInstance()

	override suspend fun readFrom(input: InputStream): UserPreferences = try {
		UserPreferences.parseFrom(input)
	} catch (exception: Exception) {
		errorReporting.captureException(exception)
		throw CorruptionException("Cannot read proto.", exception)
	}

	override suspend fun writeTo(t: UserPreferences, output: OutputStream) {
		t.writeTo(output)
	}
}
