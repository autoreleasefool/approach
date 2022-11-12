import Dependencies
import SharedModelsLibrary

public struct PersistenceService: Sendable {
	public var createBowler: @Sendable (Bowler) async throws -> Void
	public var updateBowler: @Sendable (Bowler) async throws -> Void
	public var deleteBowler: @Sendable (Bowler) async throws -> Void
	public var fetchBowlers: @Sendable (Bowler.Query) -> AsyncThrowingStream<[Bowler], Error>

	public var createLeague: @Sendable (League) async throws -> Void
	public var updateLeague: @Sendable (League) async throws -> Void
	public var deleteLeague: @Sendable (League) async throws -> Void
	public var fetchLeagues: @Sendable (League.FetchRequest) -> AsyncThrowingStream<[League], Error>

	public var createSeries: @Sendable (Series) async throws -> Void
	public var updateSeries: @Sendable (Series) async throws -> Void
	public var deleteSeries: @Sendable (Series) async throws -> Void
	public var fetchSeries: @Sendable (Series.FetchRequest) -> AsyncThrowingStream<[Series], Error>

	public var createGame: @Sendable (Game) async throws -> Void
	public var updateGame: @Sendable (Game) async throws -> Void
	public var deleteGame: @Sendable (Game) async throws -> Void
	public var fetchGames: @Sendable (Game.FetchRequest) -> AsyncThrowingStream<[Game], Error>

	public var updateFrame: @Sendable (Frame) async throws -> Void
	public var fetchFrames: @Sendable (Frame.FetchRequest) -> AsyncThrowingStream<[Frame], Error>

	public var createAlley: @Sendable (Alley) async throws -> Void
	public var updateAlley: @Sendable (Alley) async throws -> Void
	public var deleteAlley: @Sendable (Alley) async throws -> Void
	public var fetchAlleys: @Sendable (Alley.Query) -> AsyncThrowingStream<[Alley], Error>

	public init(
		createBowler: @escaping @Sendable (Bowler) async throws -> Void,
		updateBowler: @escaping @Sendable (Bowler) async throws -> Void,
		deleteBowler: @escaping @Sendable (Bowler) async throws -> Void,
		fetchBowlers: @escaping @Sendable (Bowler.Query) -> AsyncThrowingStream<[Bowler], Error>,
		createLeague: @escaping @Sendable (League) async throws -> Void,
		updateLeague: @escaping @Sendable (League) async throws -> Void,
		deleteLeague: @escaping @Sendable (League) async throws -> Void,
		fetchLeagues: @escaping @Sendable (League.FetchRequest) -> AsyncThrowingStream<[League], Error>,
		createSeries: @escaping @Sendable (Series) async throws -> Void,
		updateSeries: @escaping @Sendable (Series) async throws -> Void,
		deleteSeries: @escaping @Sendable (Series) async throws -> Void,
		fetchSeries: @escaping @Sendable (Series.FetchRequest) -> AsyncThrowingStream<[Series], Error>,
		createGame: @escaping @Sendable (Game) async throws -> Void,
		updateGame: @escaping @Sendable (Game) async throws -> Void,
		deleteGame: @escaping @Sendable (Game) async throws -> Void,
		fetchGames: @escaping @Sendable (Game.FetchRequest) -> AsyncThrowingStream<[Game], Error>,
		updateFrame: @escaping @Sendable (Frame) async throws -> Void,
		fetchFrames: @escaping @Sendable (Frame.FetchRequest) -> AsyncThrowingStream<[Frame], Error>,
		createAlley: @escaping @Sendable (Alley) async throws -> Void,
		updateAlley: @escaping @Sendable (Alley) async throws -> Void,
		deleteAlley: @escaping @Sendable (Alley) async throws -> Void,
		fetchAlleys: @escaping @Sendable (Alley.Query) -> AsyncThrowingStream<[Alley], Error>
	) {
		self.createBowler = createBowler
		self.updateBowler = updateBowler
		self.deleteBowler = deleteBowler
		self.fetchBowlers = fetchBowlers
		self.createLeague = createLeague
		self.updateLeague = updateLeague
		self.deleteLeague = deleteLeague
		self.fetchLeagues = fetchLeagues
		self.createSeries = createSeries
		self.updateSeries = updateSeries
		self.deleteSeries = deleteSeries
		self.fetchSeries = fetchSeries
		self.createGame = createGame
		self.updateGame = updateGame
		self.deleteGame = deleteGame
		self.fetchGames = fetchGames
		self.updateFrame = updateFrame
		self.fetchFrames = fetchFrames
		self.createAlley = createAlley
		self.updateAlley = updateAlley
		self.deleteAlley = deleteAlley
		self.fetchAlleys = fetchAlleys
	}
}

extension PersistenceService: TestDependencyKey {
	public static var testValue = Self(
		createBowler: { _ in fatalError("\(Self.self).createBowler") },
		updateBowler: { _ in fatalError("\(Self.self).updateBowler") },
		deleteBowler: { _ in fatalError("\(Self.self).deleteBowler") },
		fetchBowlers: { _ in fatalError("\(Self.self).fetchBowlers") },
		createLeague: { _ in fatalError("\(Self.self).createLeague") },
		updateLeague: { _ in fatalError("\(Self.self).updateLeague") },
		deleteLeague: { _ in fatalError("\(Self.self).deleteLeague") },
		fetchLeagues: { _ in fatalError("\(Self.self).fetchLeagues") },
		createSeries: { _ in fatalError("\(Self.self).createSeries") },
		updateSeries: { _ in fatalError("\(Self.self).updateSeries") },
		deleteSeries: { _ in fatalError("\(Self.self).deleteSeries") },
		fetchSeries: { _ in fatalError("\(Self.self).fetchSeries") },
		createGame: { _ in fatalError("\(Self.self).createGame") },
		updateGame: { _ in fatalError("\(Self.self).updateGame") },
		deleteGame: { _ in fatalError("\(Self.self).deleteGame") },
		fetchGames: { _ in fatalError("\(Self.self).fetchGames") },
		updateFrame: { _ in fatalError("\(Self.self).updateFrame") },
		fetchFrames: { _ in fatalError("\(Self.self).fetchFrames") },
		createAlley: { _ in fatalError("\(Self.self).createAlley") },
		updateAlley: { _ in fatalError("\(Self.self).updateAlley") },
		deleteAlley: { _ in fatalError("\(Self.self).deleteAlley") },
		fetchAlleys: { _ in fatalError("\(Self.self).fetchAlleys") }
	)
}

extension DependencyValues {
	public var persistenceService: PersistenceService {
		get { self[PersistenceService.self] }
		set { self[PersistenceService.self] = newValue }
	}
}
