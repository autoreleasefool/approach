import Dependencies
import ModelsLibrary

public struct MatchPlaysRepository: Sendable {
	public var create: @Sendable (MatchPlay.Create) async throws -> Void
	public var update: @Sendable (MatchPlay.Edit) async throws -> Void
	public var delete: @Sendable (MatchPlay.ID) async throws -> Void

	public init(
		create: @escaping @Sendable (MatchPlay.Create) async throws -> Void,
		update: @escaping @Sendable (MatchPlay.Edit) async throws -> Void,
		delete: @escaping @Sendable (MatchPlay.ID) async throws -> Void
	) {
		self.create = create
		self.update = update
		self.delete = delete
	}
}

extension MatchPlaysRepository: TestDependencyKey {
	public static var testValue = Self(
		create: { _ in unimplemented("\(Self.self).create") },
		update: { _ in unimplemented("\(Self.self).update") },
		delete: { _ in unimplemented("\(Self.self).delete") }
	)
}
