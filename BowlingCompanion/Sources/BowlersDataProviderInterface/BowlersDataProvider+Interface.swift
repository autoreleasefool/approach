import Dependencies
import SharedModelsLibrary

public struct BowlersDataProvider: Sendable {
	public var fetchBowlers: @Sendable (Bowler.FetchRequest) -> AsyncThrowingStream<[Bowler], Error>

	public init(
		fetchBowlers: @escaping @Sendable (Bowler.FetchRequest) -> AsyncThrowingStream<[Bowler], Error>
	) {
		self.fetchBowlers = fetchBowlers
	}
}

extension BowlersDataProvider: TestDependencyKey {
	public static var testValue = Self(
		fetchBowlers: { _ in fatalError("\(Self.self).fetchBowlers") }
	)
}

extension DependencyValues {
	public var bowlersDataProvider: BowlersDataProvider {
		get { self[BowlersDataProvider.self] }
		set { self[BowlersDataProvider.self] = newValue }
	}
}
