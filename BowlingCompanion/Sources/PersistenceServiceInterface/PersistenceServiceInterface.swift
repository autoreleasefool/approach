import RealmSwift

public struct PersistenceService: Sendable {
	public var read: @Sendable ((Realm) -> Void) -> Void
	public var write: @Sendable (@escaping (Realm) -> Void, ((Error?) -> Void)?) -> Void

	public init(
		read: @escaping @Sendable ((Realm) -> Void) -> Void,
		write: @escaping @Sendable (@escaping (Realm) -> Void, ((Error?) -> Void)?) -> Void
	) {
		self.read = read
		self.write = write
	}
}

#if DEBUG
extension PersistenceService {
	public static func mock() -> Self {
		.init(
			read: { _ in fatalError("\(Self.self).read") },
			write: { _, _ in fatalError("\(Self.self).write") }
		)
	}
}
#endif
