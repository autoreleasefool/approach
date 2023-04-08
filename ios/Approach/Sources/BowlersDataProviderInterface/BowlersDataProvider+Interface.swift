import Dependencies
import SharedModelsFetchableLibrary
import SharedModelsLibrary

public struct BowlersDataProvider: Sendable {
	public var observeBowler: @Sendable (Bowler.SingleFetchRequest) -> AsyncThrowingStream<Bowler?, Error>
	public var observeBowlers: @Sendable (Bowler.FetchRequest) -> AsyncThrowingStream<[Bowler], Error>

	public init(
		observeBowler: @escaping @Sendable (Bowler.SingleFetchRequest) -> AsyncThrowingStream<Bowler?, Error>,
		observeBowlers: @escaping @Sendable (Bowler.FetchRequest) -> AsyncThrowingStream<[Bowler], Error>
	) {
		self.observeBowler = observeBowler
		self.observeBowlers = observeBowlers
	}
}

extension BowlersDataProvider: TestDependencyKey {
	public static var testValue = Self(
		observeBowler: { _ in unimplemented("\(Self.self).observeBowler") },
		observeBowlers: { _ in unimplemented("\(Self.self).observeBowlers") }
	)
}

extension DependencyValues {
	public var bowlersDataProvider: BowlersDataProvider {
		get { self[BowlersDataProvider.self] }
		set { self[BowlersDataProvider.self] = newValue }
	}
}
