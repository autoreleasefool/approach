import Dependencies
import FeatureFlagsLibrary

public struct FeatureFlagsService: Sendable {
	public var isEnabled: @Sendable (FeatureFlag) -> Bool
	public var allEnabled: @Sendable ([FeatureFlag]) -> Bool
	public var observe: @Sendable (FeatureFlag) -> AsyncStream<Bool>
	public var observeAll: @Sendable ([FeatureFlag]) -> AsyncStream<[Bool]>
	public var setEnabled: @Sendable (FeatureFlag, Bool?) -> Void
	public var resetOverrides: @Sendable () -> Void

	public init(
		isEnabled: @escaping @Sendable (FeatureFlag) -> Bool,
		allEnabled: @escaping @Sendable ([FeatureFlag]) -> Bool,
		observe: @escaping @Sendable (FeatureFlag) -> AsyncStream<Bool>,
		observeAll: @escaping @Sendable ([FeatureFlag]) -> AsyncStream<[Bool]>,
		setEnabled: @escaping @Sendable (FeatureFlag, Bool?) -> Void,
		resetOverrides: @escaping @Sendable () -> Void
	) {
		self.isEnabled = isEnabled
		self.allEnabled = allEnabled
		self.observe = observe
		self.observeAll = observeAll
		self.setEnabled = setEnabled
		self.resetOverrides = resetOverrides
	}
}

extension FeatureFlagsService: TestDependencyKey {
	public static var testValue = Self(
		isEnabled: { _ in fatalError("\(Self.self).isEnabled") },
		allEnabled: { _ in fatalError("\(Self.self).allEnabled") },
		observe: { _ in fatalError("\(Self.self).observe") },
		observeAll: { _ in fatalError("\(Self.self).observeAll") },
		setEnabled: { _, _ in fatalError("\(Self.self).setEnabled") },
		resetOverrides: { fatalError("\(Self.self).resetOverrides") }
	)
}

extension DependencyValues {
	public var featureFlags: FeatureFlagsService {
		get { self[FeatureFlagsService.self] }
		set { self[FeatureFlagsService.self] = newValue }
	}
}
