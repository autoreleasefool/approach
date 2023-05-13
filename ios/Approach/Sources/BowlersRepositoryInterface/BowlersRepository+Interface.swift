import Dependencies
import ModelsLibrary

extension Bowler {
	public enum Ordering: Hashable, CaseIterable {
		case byName
		case byRecentlyUsed
	}
}

public struct BowlersRepository: Sendable {
	public var list: @Sendable (
		Bowler.Status?,
		Bowler.Ordering
	) -> AsyncThrowingStream<[Bowler.Summary], Error>
	public var summaries: @Sendable ([Bowler.ID]) async throws -> [Bowler.Summary]
	public var edit: @Sendable (Bowler.ID) async throws -> Bowler.Edit?
	public var create: @Sendable (Bowler.Create) async throws -> Void
	public var update: @Sendable (Bowler.Edit) async throws -> Void
	public var delete: @Sendable (Bowler.ID) async throws -> Void

	public init(
		list: @escaping @Sendable (Bowler.Status?, Bowler.Ordering) -> AsyncThrowingStream<[Bowler.Summary], Error>,
		summaries: @escaping @Sendable ([Bowler.ID]) async throws -> [Bowler.Summary],
		edit: @escaping @Sendable (Bowler.ID) async throws -> Bowler.Edit?,
		create: @escaping @Sendable (Bowler.Create) async throws -> Void,
		update: @escaping @Sendable (Bowler.Edit) async throws -> Void,
		delete: @escaping @Sendable (Bowler.ID) async throws -> Void
	) {
		self.list = list
		self.summaries = summaries
		self.edit = edit
		self.create = create
		self.update = update
		self.delete = delete
	}

	public func summaries(forIds: [Bowler.ID]) async throws -> [Bowler.Summary] {
		try await self.summaries(forIds)
	}

	public func playable(ordered: Bowler.Ordering) -> AsyncThrowingStream<[Bowler.Summary], Error> {
		self.list(.playable, ordered)
	}

	public func opponents(ordered: Bowler.Ordering) -> AsyncThrowingStream<[Bowler.Summary], Error> {
		self.list(nil, ordered)
	}
}

extension BowlersRepository: TestDependencyKey {
	public static var testValue = Self(
		list: { _, _ in unimplemented("\(Self.self).list") },
		summaries: { _ in unimplemented("\(Self.self).summaries") },
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
