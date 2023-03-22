import ComposableArchitecture
import PreferenceServiceInterface
import Foundation

extension PreferenceService: DependencyKey {
	public static var liveValue: Self = {
		let userDefaults = UncheckedSendable(UserDefaults.standard)

		@Sendable func contains(_ key: String) -> Bool {
			userDefaults.value.object(forKey: key) != nil
		}

		return Self(
			getBool: { key in contains(key) ? userDefaults.value.bool(forKey: key) : nil },
			setBool: { key, value in userDefaults.value.set(value, forKey: key) },
			getDouble: { key in contains(key) ? userDefaults.value.double(forKey: key) : nil },
			setDouble: { key, value in userDefaults.value.set(value, forKey: key) },
			getFloat: { key in contains(key) ? userDefaults.value.float(forKey: key) : nil },
			setFloat: { key, value in userDefaults.value.set(value, forKey: key) },
			getInt: { key in contains(key) ? userDefaults.value.integer(forKey: key) : nil },
			setInt: { key, value in userDefaults.value.set(value, forKey: key) },
			getString: { key in contains(key) ? userDefaults.value.string(forKey: key) : nil },
			setString: { key, value in userDefaults.value.set(value, forKey: key) },
			getStringArray: { key in contains(key) ? userDefaults.value.stringArray(forKey: key) : nil },
			setStringArray: { key, value in userDefaults.value.set(value, forKey: key) },
			contains: contains(_:),
			removeKey: { key in userDefaults.value.removeObject(forKey: key) }
		)
	}()
}
