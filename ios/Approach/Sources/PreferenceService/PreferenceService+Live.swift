import Combine
import Dependencies
import Foundation
import PreferenceServiceInterface

extension NSNotification.Name {
	enum UserDefaults {
		static let didChange = NSNotification.Name("UserDefaults.didChange")
	}
}

extension PreferenceService: DependencyKey {
	public static var liveValue: Self = {
		let userDefaults = UncheckedSendable(UserDefaults.standard)

		@Sendable func contains(_ key: String) -> Bool {
			userDefaults.value.object(forKey: key) != nil
		}

		return Self(
			getBool: { key in contains(key) ? userDefaults.value.bool(forKey: key) : nil },
			setBool: { key, value in
				userDefaults.value.set(value, forKey: key)
				if let preference = PreferenceKey(rawValue: key) {
					NotificationCenter.default.post(name: .UserDefaults.didChange, object: preference)
				}
			},
			getDouble: { key in contains(key) ? userDefaults.value.double(forKey: key) : nil },
			setDouble: { key, value in
				userDefaults.value.set(value, forKey: key)
				if let preference = PreferenceKey(rawValue: key) {
					NotificationCenter.default.post(name: .UserDefaults.didChange, object: preference)
				}
			},
			getFloat: { key in contains(key) ? userDefaults.value.float(forKey: key) : nil },
			setFloat: { key, value in
				userDefaults.value.set(value, forKey: key)
				if let preference = PreferenceKey(rawValue: key) {
					NotificationCenter.default.post(name: .UserDefaults.didChange, object: preference)
				}
			},
			getInt: { key in contains(key) ? userDefaults.value.integer(forKey: key) : nil },
			setInt: { key, value in
				userDefaults.value.set(value, forKey: key)
				if let preference = PreferenceKey(rawValue: key) {
					NotificationCenter.default.post(name: .UserDefaults.didChange, object: preference)
				}
			},
			getString: { key in contains(key) ? userDefaults.value.string(forKey: key) : nil },
			setString: { key, value in
				userDefaults.value.set(value, forKey: key)
				if let preference = PreferenceKey(rawValue: key) {
					NotificationCenter.default.post(name: .UserDefaults.didChange, object: preference)
				}
			},
			getStringArray: { key in contains(key) ? userDefaults.value.stringArray(forKey: key) : nil },
			setStringArray: { key, value in
				userDefaults.value.set(value, forKey: key)
				if let preference = PreferenceKey(rawValue: key) {
					NotificationCenter.default.post(name: .UserDefaults.didChange, object: preference)
				}
			},
			contains: contains(_:),
			remove: { key in
				userDefaults.value.removeObject(forKey: key)
				if let preference = PreferenceKey(rawValue: key) {
					NotificationCenter.default.post(name: .UserDefaults.didChange, object: preference)
				}
			},
			observe: { keys in
				.init { continuation in
					let cancellable = NotificationCenter.default
						.publisher(for: .UserDefaults.didChange)
						.compactMap {
							guard let preference = $0.object as? PreferenceKey, keys.contains(preference) else { return nil }
							return preference
						}
						.sink { preference in
							continuation.yield(preference)
						}

					continuation.onTermination = { _ in cancellable.cancel() }
				}
			}
		)
	}()
}
