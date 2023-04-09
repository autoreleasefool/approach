import Dependencies
import ModelsLibrary

public struct BowlersRepository: Sendable {
	public var playable: @Sendable (Bowler.Ordering) -> AsyncThrowingStream<[Bowler.Summary], Error>
	public var opponents: @Sendable (Bowler.Ordering) -> AsyncThrowingStream<[Bowler.Summary], Error>
	public var edit: @Sendable (Bowler.ID) async throws -> Bowler.Editable?
	public var save: @Sendable (Bowler.Editable) async throws -> Void

	public init(
		playable: @escaping @Sendable (Bowler.Ordering) -> AsyncThrowingStream<[Bowler.Summary], Error>,
		opponents: @escaping @Sendable (Bowler.Ordering) -> AsyncThrowingStream<[Bowler.Summary], Error>,
		edit: @escaping @Sendable (Bowler.ID) async throws -> Bowler.Editable?,
		save: @escaping @Sendable (Bowler.Editable) async throws -> Void
	) {
		self.playable = playable
		self.opponents = opponents
		self.edit = edit
		self.save = save
	}
}

extension BowlersRepository: TestDependencyKey {
	public static var testValue = Self(
		playable: { _ in unimplemented("\(Self.self).playable") },
		opponents: { _ in unimplemented("\(Self.self).opponents") },
		edit: { _ in unimplemented("\(Self.self).edit") },
		save: { _ in unimplemented("\(Self.self).save") }
	)
}

extension DependencyValues {
	public var bowlers: BowlersRepository {
		get { self[BowlersRepository.self] }
		set { self[BowlersRepository.self] = newValue }
	}
}
