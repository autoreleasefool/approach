import Combine
import Dependencies
import Foundation
import PreferenceServiceInterface
import UserDefaultsPackageServiceInterface

extension PreferenceService: DependencyKey {
	public static var liveValue: Self {
		return Self(
			bool: { key in
				@Dependency(\.userDefaults) var userDefaults
				return userDefaults.bool(forKey: key.rawValue)
			},
			setBool: { key, value in
				@Dependency(\.userDefaults) var userDefaults
				userDefaults.setBool(forKey: key.rawValue, to: value)
			},
			double: { key in
				@Dependency(\.userDefaults) var userDefaults
				return userDefaults.double(forKey: key.rawValue)
			},
			setDouble: { key, value in
				@Dependency(\.userDefaults) var userDefaults
				userDefaults.setDouble(forKey: key.rawValue, to: value)
			},
			float: { key in
				@Dependency(\.userDefaults) var userDefaults
				return userDefaults.float(forKey: key.rawValue)
			},
			setFloat: { key, value in
				@Dependency(\.userDefaults) var userDefaults
				userDefaults.setFloat(forKey: key.rawValue, to: value)
			},
			int: { key in
				@Dependency(\.userDefaults) var userDefaults
				return userDefaults.int(forKey: key.rawValue)
			},
			setInt: { key, value in
				@Dependency(\.userDefaults) var userDefaults
				userDefaults.setInt(forKey: key.rawValue, to: value)
			},
			string: { key in
				@Dependency(\.userDefaults) var userDefaults
				return userDefaults.string(forKey: key.rawValue)
			},
			setString: { key, value in
				@Dependency(\.userDefaults) var userDefaults
				userDefaults.setString(forKey: key.rawValue, to: value)
			},
			stringArray: { key in
				@Dependency(\.userDefaults) var userDefaults
				return userDefaults.stringArray(forKey: key.rawValue)
			},
			setStringArray: { key, value in
				@Dependency(\.userDefaults) var userDefaults
				userDefaults.setStringArray(forKey: key.rawValue, to: value)
			},
			contains: { key in
				@Dependency(\.userDefaults) var userDefaults
				return userDefaults.contains(key: key.rawValue)
			},
			remove: { key in
				@Dependency(\.userDefaults) var userDefaults
				userDefaults.remove(key: key.rawValue)
			},
			observe: { keys in
				@Dependency(\.userDefaults) var userDefaults
				return userDefaults
					.observe(keys: Set(keys.map(\.rawValue)))
					.compactMap {
						guard let key = PreferenceKey(rawValue: $0) else {
							return nil
						}

						return key
					}
					.eraseToStream()
			}
		)
	}
}
