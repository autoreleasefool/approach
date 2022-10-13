import SharedModelsLibrary

public struct BowlersDataProvider: Sendable {
	public var save: @Sendable (Bowler) async throws -> Void
	public var delete: @Sendable (Bowler) async throws -> Void
	public var fetchAll: @Sendable () -> AsyncStream<[Bowler]>

	public init(
		save: @escaping @Sendable (Bowler) async throws -> Void,
		delete: @escaping @Sendable (Bowler) async throws -> Void,
		fetchAll: @escaping @Sendable () -> AsyncStream<[Bowler]>
	) {
		self.save = save
		self.delete = delete
		self.fetchAll = fetchAll
	}
}

#if DEBUG
extension BowlersDataProvider {
	public static func mock() -> Self {
		.init(
			save: { _ in fatalError("\(Self.self).save") },
			delete: { _ in fatalError("\(Self.self).delete") },
			fetchAll: { fatalError("\(Self.self).fetchAll") }
		)
	}
}
#endif
