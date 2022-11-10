import Dependencies
import FeatureFlagLibrary

public struct FeatureFlagService: Sendable {
	public var isEnabled: @Sendable (FeatureFlag) -> Bool
	public var allEnabled: @Sendable ([FeatureFlag]) -> Bool
	public var observe: @Sendable (FeatureFlag) -> AsyncStream<Bool>
	public var observeAll: @Sendable ([FeatureFlag]) -> AsyncStream<[Bool]>
	public var setEnabled: @Sendable (FeatureFlag, Bool?) -> Void

	public init(
		isEnabled: @escaping @Sendable (FeatureFlag) -> Bool,
		allEnabled: @escaping @Sendable ([FeatureFlag]) -> Bool,
		observe: @escaping @Sendable (FeatureFlag) -> AsyncStream<Bool>,
		observeAll: @escaping @Sendable ([FeatureFlag]) -> AsyncStream<[Bool]>,
		setEnabled: @escaping @Sendable (FeatureFlag, Bool?) -> Void
	) {
		self.isEnabled = isEnabled
		self.allEnabled = allEnabled
		self.observe = observe
		self.observeAll = observeAll
		self.setEnabled = setEnabled
	}
}

extension FeatureFlagService: TestDependencyKey {
	public static var testValue = Self(
		isEnabled: { _ in fatalError("\(Self.self).isEnabled") },
		allEnabled: { _ in fatalError("\(Self.self).allEnabled") },
		observe: { _ in fatalError("\(Self.self).observe") },
		observeAll: { _ in fatalError("\(Self.self).observeAll") },
		setEnabled: { _, _ in fatalError("\(Self.self).setEnabled") }
	)
}

extension DependencyValues {
	public var featureFlags: FeatureFlagService {
		get { self[FeatureFlagService.self] }
		set { self[FeatureFlagService.self] = newValue }
	}
}

