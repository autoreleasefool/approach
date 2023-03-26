import Dependencies
import ModelsLibrary

public struct BowlersRepository: Sendable {
	public var bowlers: @Sendable () -> AsyncThrowingStream<[Bowler.Summary], Error>
	public var opponents: @Sendable () -> AsyncThrowingStream<[Bowler.Summary], Error>
	public var edit: @Sendable (Bowler.ID) async throws -> Bowler.Editable?
	public var save: @Sendable (Bowler.Editable) async throws -> Void

	public init(
		bowlers: @escaping @Sendable () -> AsyncThrowingStream<[Bowler.Summary], Error>,
		opponents: @escaping @Sendable () -> AsyncThrowingStream<[Bowler.Summary], Error>,
		edit: @escaping @Sendable (Bowler.ID) async throws -> Bowler.Editable?,
		save: @escaping @Sendable (Bowler.Editable) async throws -> Void
	) {
		self.bowlers = bowlers
		self.opponents = opponents
		self.edit = edit
		self.save = save
	}
}

extension BowlersRepository: TestDependencyKey {
	public static var testValue = Self(
		bowlers: { unimplemented("\(Self.self).bowlers") },
		opponents: { unimplemented("\(Self.self).opponents") },
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
