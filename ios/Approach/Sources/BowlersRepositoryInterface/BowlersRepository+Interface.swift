import Dependencies
import ModelsLibrary

extension Bowler {
	public enum Ordering: Hashable, CaseIterable {
		case byName
		case byRecentlyUsed
	}
}

public struct BowlersRepository: Sendable {
	public var list: @Sendable (Bowler.Ordering) -> AsyncThrowingStream<[Bowler.List], Error>
	public var summaries: @Sendable (Bowler.Kind?, Bowler.Ordering) -> AsyncThrowingStream<[Bowler.Summary], Error>
	public var fetchSummaries: @Sendable ([Bowler.ID]) async throws -> [Bowler.Summary]
	public var opponentRecord: @Sendable (Bowler.ID) async throws -> Bowler.OpponentDetails
	public var edit: @Sendable (Bowler.ID) async throws -> Bowler.Edit
	public var create: @Sendable (Bowler.Create) async throws -> Void
	public var update: @Sendable (Bowler.Edit) async throws -> Void
	public var delete: @Sendable (Bowler.ID) async throws -> Void

	public init(
		list: @escaping @Sendable (Bowler.Ordering) -> AsyncThrowingStream<[Bowler.List], Error>,
		summaries: @escaping @Sendable (Bowler.Kind?, Bowler.Ordering) -> AsyncThrowingStream<[Bowler.Summary], Error>,
		fetchSummaries: @escaping @Sendable ([Bowler.ID]) async throws -> [Bowler.Summary],
		opponentRecord: @escaping @Sendable (Bowler.ID) async throws -> Bowler.OpponentDetails,
		edit: @escaping @Sendable (Bowler.ID) async throws -> Bowler.Edit,
		create: @escaping @Sendable (Bowler.Create) async throws -> Void,
		update: @escaping @Sendable (Bowler.Edit) async throws -> Void,
		delete: @escaping @Sendable (Bowler.ID) async throws -> Void
	) {
		self.list = list
		self.summaries = summaries
		self.fetchSummaries = fetchSummaries
		self.opponentRecord = opponentRecord
		self.edit = edit
		self.create = create
		self.update = update
		self.delete = delete
	}

	public func pickable() -> AsyncThrowingStream<[Bowler.Summary], Error> {
		self.summaries(nil, .byName)
	}

	public func opponents(ordered: Bowler.Ordering) -> AsyncThrowingStream<[Bowler.Summary], Error> {
		self.summaries(nil, ordered)
	}

	public func summaries(forIds: [Bowler.ID]) async throws -> [Bowler.Summary] {
		try await self.fetchSummaries(forIds)
	}

	public func list(ordered: Bowler.Ordering) -> AsyncThrowingStream<[Bowler.List], Error> {
		self.list(ordered)
	}

	// FIXME: allow filtering by bowler vs opponent
	public func record(againstOpponent: Bowler.ID) async throws -> Bowler.OpponentDetails {
		try await self.opponentRecord(againstOpponent)
	}
}

extension BowlersRepository: TestDependencyKey {
	public static var testValue = Self(
		list: { _ in unimplemented("\(Self.self).list") },
		summaries: { _, _ in unimplemented("\(Self.self).summaries") },
		fetchSummaries: { _ in unimplemented("\(Self.self).fetchSummaries") },
		opponentRecord: { _ in unimplemented("\(Self.self).opponentRecord") },
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
