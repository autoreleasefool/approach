import Dependencies
import ModelsLibrary

extension Bowler {
	public enum Ordering: Hashable, CaseIterable {
		case byName
		case byRecentlyUsed
	}
}

public struct BowlersRepository: Sendable {
	public var playable: @Sendable (Bowler.Ordering) -> AsyncThrowingStream<[Bowler.Summary], Error>
	public var opponents: @Sendable (Bowler.Ordering) -> AsyncThrowingStream<[Bowler.Summary], Error>
	public var edit: @Sendable (Bowler.ID) async throws -> Bowler.Edit?
	public var create: @Sendable (Bowler.Create) async throws -> Void
	public var update: @Sendable (Bowler.Edit) async throws -> Void
	public var delete: @Sendable (Bowler.ID) async throws -> Void

	public init(
		playable: @escaping @Sendable (Bowler.Ordering) -> AsyncThrowingStream<[Bowler.Summary], Error>,
		opponents: @escaping @Sendable (Bowler.Ordering) -> AsyncThrowingStream<[Bowler.Summary], Error>,
		edit: @escaping @Sendable (Bowler.ID) async throws -> Bowler.Edit?,
		create: @escaping @Sendable (Bowler.Create) async throws -> Void,
		update: @escaping @Sendable (Bowler.Edit) async throws -> Void,
		delete: @escaping @Sendable (Bowler.ID) async throws -> Void
	) {
		self.playable = playable
		self.opponents = opponents
		self.edit = edit
		self.create = create
		self.update = update
		self.delete = delete
	}

	public func playable(ordered: Bowler.Ordering) -> AsyncThrowingStream<[Bowler.Summary], Error> {
		self.playable(ordered)
	}

	public func opponents(ordered: Bowler.Ordering) -> AsyncThrowingStream<[Bowler.Summary], Error> {
		self.opponents(ordered)
	}
}

extension BowlersRepository: TestDependencyKey {
	public static var testValue = Self(
		playable: { _ in unimplemented("\(Self.self).playable") },
		opponents: { _ in unimplemented("\(Self.self).opponents") },
		edit: { _ in unimplemented("\(Self.self).edit") },
		create: { _ in unimplemented("\(Self.self).create") },
		update: { _ in unimplemented("\(Self.self).update") },
		delete: { _ in unimplemented("\(Self.self).delete") }
	)
}

extension DependencyValues {
	public var bowlers: BowlersRepository {
		get { self[BowlersRepository.self] }
		set { self[BowlersRepository.self] = newValue }
	}
}
