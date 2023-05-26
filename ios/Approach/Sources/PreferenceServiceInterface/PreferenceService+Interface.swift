import Dependencies

public struct PreferenceService: Sendable {
	public var getBool: @Sendable (String) -> Bool?
	public var setBool: @Sendable (String, Bool) -> Void
	public var getDouble: @Sendable (String) -> Double?
	public var setDouble: @Sendable (String, Double) -> Void
	public var getFloat: @Sendable (String) -> Float?
	public var setFloat: @Sendable (String, Float) -> Void
	public var getInt: @Sendable (String) -> Int?
	public var setInt: @Sendable (String, Int) -> Void
	public var getString: @Sendable (String) -> String?
	public var setString: @Sendable (String, String) -> Void
	public var getStringArray: @Sendable (String) -> [String]?
	public var setStringArray: @Sendable (String, [String]) -> Void
	public var contains: @Sendable (String) -> Bool
	public var remove: @Sendable (String) -> Void

	public init(
		getBool: @escaping @Sendable (String) -> Bool?,
		setBool: @escaping @Sendable (String, Bool) -> Void,
		getDouble: @escaping @Sendable (String) -> Double?,
		setDouble: @escaping @Sendable (String, Double) -> Void,
		getFloat: @escaping @Sendable (String) -> Float?,
		setFloat: @escaping @Sendable (String, Float) -> Void,
		getInt: @escaping @Sendable (String) -> Int?,
		setInt: @escaping @Sendable (String, Int) -> Void,
		getString: @escaping @Sendable (String) -> String?,
		setString: @escaping @Sendable (String, String) -> Void,
		getStringArray: @escaping @Sendable (String) -> [String]?,
		setStringArray: @escaping @Sendable (String, [String]) -> Void,
		contains: @escaping @Sendable (String) -> Bool,
		remove: @escaping @Sendable (String) -> Void
	) {
		self.getBool = getBool
		self.setBool = setBool
		self.getDouble = getDouble
		self.setDouble = setDouble
		self.getFloat = getFloat
		self.setFloat = setFloat
		self.getInt = getInt
		self.setInt = setInt
		self.getString = getString
		self.setString = setString
		self.getStringArray = getStringArray
		self.setStringArray = setStringArray
		self.contains = contains
		self.remove = remove
	}

	public func bool(forKey: PreferenceKey) -> Bool? { self.getBool(forKey.rawValue) }
	public func setKey(_ key: PreferenceKey, toBool: Bool) { self.setBool(key.rawValue, toBool) }
	public func double(forKey: PreferenceKey) -> Double? { self.getDouble(forKey.rawValue) }
	public func setKey(_ key: PreferenceKey, toDouble: Double) { self.setDouble(key.rawValue, toDouble) }
	public func float(forKey: PreferenceKey) -> Float? { self.getFloat(forKey.rawValue) }
	public func setKey(_ key: PreferenceKey, toFloat: Float) { self.setFloat(key.rawValue, toFloat) }
	public func int(forKey: PreferenceKey) -> Int? { self.getInt(forKey.rawValue) }
	public func setKey(_ key: PreferenceKey, toInt: Int) { self.setInt(key.rawValue, toInt) }
	public func string(forKey: PreferenceKey) -> String? { self.getString(forKey.rawValue) }
	public func setKey(_ key: PreferenceKey, toString: String) { self.setString(key.rawValue, toString) }
	public func stringArray(forKey: PreferenceKey) -> [String]? { self.getStringArray(forKey.rawValue) }
	public func setKey(_ key: PreferenceKey, toStringArray: [String]) { self.setStringArray(key.rawValue, toStringArray) }
}

extension PreferenceService: TestDependencyKey {
	public static var testValue = { Self(
		getBool: { _ in unimplemented("\(Self.self).getBool") },
		setBool: { _, _ in unimplemented("\(Self.self).setBool") },
		getDouble: { _ in unimplemented("\(Self.self).getDouble") },
		setDouble: { _, _ in unimplemented("\(Self.self).setDouble") },
		getFloat: { _ in unimplemented("\(Self.self).getFloat") },
		setFloat: { _, _ in unimplemented("\(Self.self).setFloat") },
		getInt: { _ in unimplemented("\(Self.self).getInt") },
		setInt: { _, _ in unimplemented("\(Self.self).setInt") },
		getString: { _ in unimplemented("\(Self.self).getString") },
		setString: { _, _ in unimplemented("\(Self.self).setString") },
		getStringArray: { _ in unimplemented("\(Self.self).getStringArray") },
		setStringArray: { _, _ in unimplemented("\(Self.self).setStringArray") },
		contains: { _ in unimplemented("\(Self.self).contains") },
		remove: { _ in unimplemented("\(Self.self).remove") }
	)}()
}

extension DependencyValues {
	public var preferences: PreferenceService {
		get { self[PreferenceService.self] }
		set { self[PreferenceService.self] = newValue }
	}
}
