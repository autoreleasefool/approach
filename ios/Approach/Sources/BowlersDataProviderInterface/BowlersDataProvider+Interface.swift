import Dependencies
import SharedModelsLibrary
import SharedModelsFetchableLibrary

public struct BowlersDataProvider: Sendable {
	public var fetchBowlers: @Sendable (Bowler.FetchRequest) async throws -> [Bowler]
	public var observeBowlers: @Sendable (Bowler.FetchRequest) -> AsyncThrowingStream<[Bowler], Error>

	public init(
		fetchBowlers: @escaping @Sendable (Bowler.FetchRequest) async throws -> [Bowler],
		observeBowlers: @escaping @Sendable (Bowler.FetchRequest) -> AsyncThrowingStream<[Bowler], Error>
	) {
		self.fetchBowlers = fetchBowlers
		self.observeBowlers = observeBowlers
	}
}

extension BowlersDataProvider: TestDependencyKey {
	public static var testValue = Self(
		fetchBowlers: { _ in unimplemented("\(Self.self).fetchBowlers") },
		observeBowlers: { _ in unimplemented("\(Self.self).observeBowlers") }
	)
}

extension DependencyValues {
	public var bowlersDataProvider: BowlersDataProvider {
		get { self[BowlersDataProvider.self] }
		set { self[BowlersDataProvider.self] = newValue }
	}
}
