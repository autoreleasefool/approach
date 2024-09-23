import Dependencies
import DependenciesMacros

@DependencyClient
public struct PreferenceService: Sendable {
	public var bool: @Sendable (_ forKey: PreferenceKey) -> Bool?
	public var setBool: @Sendable (_ forKey: PreferenceKey, _ to: Bool) -> Void
	public var double: @Sendable (_ forKey: PreferenceKey) -> Double?
	public var setDouble: @Sendable (_ forKey: PreferenceKey, _ to: Double) -> Void
	public var float: @Sendable (_ forKey: PreferenceKey) -> Float?
	public var setFloat: @Sendable (_ forKey: PreferenceKey, _ to: Float) -> Void
	public var int: @Sendable (_ forKey: PreferenceKey) -> Int?
	public var setInt: @Sendable (_ forKey: PreferenceKey, _ to: Int) -> Void
	public var string: @Sendable (_ forKey: PreferenceKey) -> String?
	public var setString: @Sendable (_ forKey: PreferenceKey, _ to: String) -> Void
	public var stringArray: @Sendable (_ forKey: PreferenceKey) -> [String]?
	public var setStringArray: @Sendable (_ forKey: PreferenceKey, _ to: [String]) -> Void
	public var contains: @Sendable (_ key: PreferenceKey) -> Bool = { _ in
		unimplemented("\(Self.self).contains", placeholder: false)
	}
	public var remove: @Sendable (_ key: PreferenceKey) -> Void
	public var observe: @Sendable (_ keys: Set<PreferenceKey>) -> AsyncStream<PreferenceKey> = { _ in
		unimplemented("\(Self.self).observe", placeholder: .never)
	}
}

extension PreferenceService: TestDependencyKey {
	public static var testValue: Self { Self() }
}

extension DependencyValues {
	public var preferences: PreferenceService {
		get { self[PreferenceService.self] }
		set { self[PreferenceService.self] = newValue }
	}
}
