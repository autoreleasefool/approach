import Dependencies

public struct CrashGenerator: Sendable {
	private var generate: @Sendable () -> Void

	public init(_ generate: @escaping @Sendable () -> Void) {
		self.generate = generate
	}

	public func callAsFunction() {
		self.generate()
	}
}

extension CrashGenerator: TestDependencyKey {
	public static var testValue = Self { }
}

extension DependencyValues {
	public var crash: CrashGenerator {
		get { self[CrashGenerator.self] }
		set { self[CrashGenerator.self] = newValue }
	}
}
