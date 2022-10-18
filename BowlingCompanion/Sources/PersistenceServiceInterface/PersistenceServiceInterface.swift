import Dependencies

public struct PersistenceService: Sendable {
	public var read: @Sendable (() -> Void) -> Void
	public var write: @Sendable (@escaping () -> Void, ((Error?) -> Void)?) -> Void

	public init(
		read: @escaping @Sendable (() -> Void) -> Void,
		write: @escaping @Sendable (@escaping () -> Void, ((Error?) -> Void)?) -> Void
	) {
		self.read = read
		self.write = write
	}
}

extension PersistenceService: TestDependencyKey {
	public static var testValue = Self(
		read: { _ in fatalError("\(Self.self).read") },
		write: { _, _ in fatalError("\(Self.self).write") }
	)
}

extension DependencyValues {
	public var persistenceService: PersistenceService {
		get { self[PersistenceService.self] }
		set { self[PersistenceService.self] = newValue }
	}
}
