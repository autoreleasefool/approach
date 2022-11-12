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
	public var removeKey: @Sendable (String) -> Void

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
		removeKey: @escaping @Sendable (String) -> Void
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
		self.removeKey = removeKey
	}
}

extension PreferenceService: TestDependencyKey {
	public static var testValue = Self(
		getBool: { _ in fatalError("\(Self.self).getBool") },
		setBool: { _, _ in fatalError("\(Self.self).setBool") },
		getDouble: { _ in fatalError("\(Self.self).getDouble") },
		setDouble: { _, _ in fatalError("\(Self.self).setDouble") },
		getFloat: { _ in fatalError("\(Self.self).getFloat") },
		setFloat: { _, _ in fatalError("\(Self.self).setFloat") },
		getInt: { _ in fatalError("\(Self.self).getInt") },
		setInt: { _, _ in fatalError("\(Self.self).setInt") },
		getString: { _ in fatalError("\(Self.self).getString") },
		setString: { _, _ in fatalError("\(Self.self).setString") },
		getStringArray: { _ in fatalError("\(Self.self).getStringArray") },
		setStringArray: { _, _ in fatalError("\(Self.self).setStringArray") },
		contains: { _ in fatalError("\(Self.self).contains") },
		removeKey: { _ in fatalError("\(Self.self).removeKey") }
	)
}

extension DependencyValues {
	public var preferenceService: PreferenceService {
		get { self[PreferenceService.self] }
		set { self[PreferenceService.self] = newValue }
	}
}
