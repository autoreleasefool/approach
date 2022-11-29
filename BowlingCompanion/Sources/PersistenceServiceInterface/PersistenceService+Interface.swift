import Dependencies
import SharedModelsLibrary

public struct PersistenceService: Sendable {
	public var createBowler: @Sendable (Bowler) async throws -> Void
	public var updateBowler: @Sendable (Bowler) async throws -> Void
	public var deleteBowler: @Sendable (Bowler) async throws -> Void
	public var fetchBowlers: @Sendable (Bowler.Query) async throws -> [Bowler]
	public var observeBowlers: @Sendable (Bowler.Query) -> AsyncThrowingStream<[Bowler], Error>

	public var createLeague: @Sendable (League) async throws -> Void
	public var updateLeague: @Sendable (League) async throws -> Void
	public var deleteLeague: @Sendable (League) async throws -> Void
	public var fetchLeagues: @Sendable (League.Query) async throws -> [League]
	public var observeLeagues: @Sendable (League.Query) -> AsyncThrowingStream<[League], Error>

	public var createSeries: @Sendable (Series) async throws -> Void
	public var updateSeries: @Sendable (Series) async throws -> Void
	public var deleteSeries: @Sendable (Series) async throws -> Void
	public var fetchSeries: @Sendable(Series.Query) async throws -> [Series]
	public var observeSeries: @Sendable (Series.Query) -> AsyncThrowingStream<[Series], Error>

	public var createGame: @Sendable (Game) async throws -> Void
	public var updateGame: @Sendable (Game) async throws -> Void
	public var deleteGame: @Sendable (Game) async throws -> Void
	public var fetchGames: @Sendable (Game.Query) async throws -> [Game]
	public var observeGames: @Sendable (Game.Query) -> AsyncThrowingStream<[Game], Error>

	public var updateFrame: @Sendable (Frame) async throws -> Void
	public var fetchFrames: @Sendable (Frame.Query) async throws -> [Frame]
	public var observeFrames: @Sendable (Frame.Query) -> AsyncThrowingStream<[Frame], Error>

	public var createAlley: @Sendable (Alley) async throws -> Void
	public var updateAlley: @Sendable (Alley) async throws -> Void
	public var deleteAlley: @Sendable (Alley) async throws -> Void
	public var fetchAlleys: @Sendable (Alley.Query) async throws -> [Alley]
	public var observeAlleys: @Sendable (Alley.Query) -> AsyncThrowingStream<[Alley], Error>

	public var createGear: @Sendable (Gear) async throws -> Void
	public var updateGear: @Sendable (Gear) async throws -> Void
	public var deleteGear: @Sendable (Gear) async throws -> Void
	public var fetchGear: @Sendable (Gear.Query) async throws -> [Gear]
	public var observeGear: @Sendable (Gear.Query) -> AsyncThrowingStream<[Gear], Error>

	public init(
		createBowler: @escaping @Sendable (Bowler) async throws -> Void,
		updateBowler: @escaping @Sendable (Bowler) async throws -> Void,
		deleteBowler: @escaping @Sendable (Bowler) async throws -> Void,
		fetchBowlers: @escaping @Sendable (Bowler.Query) async throws -> [Bowler],
		observeBowlers: @escaping @Sendable (Bowler.Query) -> AsyncThrowingStream<[Bowler], Error>,
		createLeague: @escaping @Sendable (League) async throws -> Void,
		updateLeague: @escaping @Sendable (League) async throws -> Void,
		deleteLeague: @escaping @Sendable (League) async throws -> Void,
		fetchLeagues: @escaping @Sendable (League.Query) async throws -> [League],
		observeLeagues: @escaping @Sendable (League.Query) -> AsyncThrowingStream<[League], Error>,
		createSeries: @escaping @Sendable (Series) async throws -> Void,
		updateSeries: @escaping @Sendable (Series) async throws -> Void,
		deleteSeries: @escaping @Sendable (Series) async throws -> Void,
		fetchSeries: @escaping @Sendable (Series.Query) async throws -> [Series],
		observeSeries: @escaping @Sendable (Series.Query) -> AsyncThrowingStream<[Series], Error>,
		createGame: @escaping @Sendable (Game) async throws -> Void,
		updateGame: @escaping @Sendable (Game) async throws -> Void,
		deleteGame: @escaping @Sendable (Game) async throws -> Void,
		fetchGames: @escaping @Sendable (Game.Query) async throws -> [Game],
		observeGames: @escaping @Sendable (Game.Query) -> AsyncThrowingStream<[Game], Error>,
		updateFrame: @escaping @Sendable (Frame) async throws -> Void,
		fetchFrames: @escaping @Sendable (Frame.Query) async throws -> [Frame],
		observeFrames: @escaping @Sendable (Frame.Query) -> AsyncThrowingStream<[Frame], Error>,
		createAlley: @escaping @Sendable (Alley) async throws -> Void,
		updateAlley: @escaping @Sendable (Alley) async throws -> Void,
		deleteAlley: @escaping @Sendable (Alley) async throws -> Void,
		fetchAlleys: @escaping @Sendable (Alley.Query) async throws -> [Alley],
		observeAlleys: @escaping @Sendable (Alley.Query) -> AsyncThrowingStream<[Alley], Error>,
		createGear: @escaping @Sendable (Gear) async throws -> Void,
		updateGear: @escaping @Sendable (Gear) async throws -> Void,
		deleteGear: @escaping @Sendable (Gear) async throws -> Void,
		fetchGear: @escaping @Sendable (Gear.Query) async throws -> [Gear],
		observeGear: @escaping @Sendable (Gear.Query) -> AsyncThrowingStream<[Gear], Error>
	) {
		self.createBowler = createBowler
		self.updateBowler = updateBowler
		self.deleteBowler = deleteBowler
		self.fetchBowlers = fetchBowlers
		self.observeBowlers = observeBowlers
		self.createLeague = createLeague
		self.updateLeague = updateLeague
		self.deleteLeague = deleteLeague
		self.fetchLeagues = fetchLeagues
		self.observeLeagues = observeLeagues
		self.createSeries = createSeries
		self.updateSeries = updateSeries
		self.deleteSeries = deleteSeries
		self.fetchSeries = fetchSeries
		self.observeSeries = observeSeries
		self.createGame = createGame
		self.updateGame = updateGame
		self.deleteGame = deleteGame
		self.fetchGames = fetchGames
		self.observeGames = observeGames
		self.updateFrame = updateFrame
		self.fetchFrames = fetchFrames
		self.observeFrames = observeFrames
		self.createAlley = createAlley
		self.updateAlley = updateAlley
		self.deleteAlley = deleteAlley
		self.fetchAlleys = fetchAlleys
		self.observeAlleys = observeAlleys
		self.createGear = createGear
		self.updateGear = updateGear
		self.deleteGear = deleteGear
		self.fetchGear = fetchGear
		self.observeGear = observeGear
	}
}

extension PersistenceService: TestDependencyKey {
	public static var testValue = Self(
		createBowler: { _ in fatalError("\(Self.self).createBowler") },
		updateBowler: { _ in fatalError("\(Self.self).updateBowler") },
		deleteBowler: { _ in fatalError("\(Self.self).deleteBowler") },
		fetchBowlers: { _ in fatalError("\(Self.self).fetchBowlers") },
		observeBowlers: { _ in fatalError("\(Self.self).observeBowlers") },
		createLeague: { _ in fatalError("\(Self.self).createLeague") },
		updateLeague: { _ in fatalError("\(Self.self).updateLeague") },
		deleteLeague: { _ in fatalError("\(Self.self).deleteLeague") },
		fetchLeagues: { _ in fatalError("\(Self.self).fetchLeagues") },
		observeLeagues: { _ in fatalError("\(Self.self).observeLeagues") },
		createSeries: { _ in fatalError("\(Self.self).createSeries") },
		updateSeries: { _ in fatalError("\(Self.self).updateSeries") },
		deleteSeries: { _ in fatalError("\(Self.self).deleteSeries") },
		fetchSeries: { _ in fatalError("\(Self.self).fetchSeries") },
		observeSeries: { _ in fatalError("\(Self.self).observeSeries") },
		createGame: { _ in fatalError("\(Self.self).createGame") },
		updateGame: { _ in fatalError("\(Self.self).updateGame") },
		deleteGame: { _ in fatalError("\(Self.self).deleteGame") },
		fetchGames: { _ in fatalError("\(Self.self).fetchGames") },
		observeGames: { _ in fatalError("\(Self.self).observeGames") },
		updateFrame: { _ in fatalError("\(Self.self).updateFrame") },
		fetchFrames: { _ in fatalError("\(Self.self).fetchFrames") },
		observeFrames: { _ in fatalError("\(Self.self).observeFrames") },
		createAlley: { _ in fatalError("\(Self.self).createAlley") },
		updateAlley: { _ in fatalError("\(Self.self).updateAlley") },
		deleteAlley: { _ in fatalError("\(Self.self).deleteAlley") },
		fetchAlleys: { _ in fatalError("\(Self.self).fetchAlleys") },
		observeAlleys: { _ in fatalError("\(Self.self).observeAlleys") },
		createGear: { _ in fatalError("\(Self.self).createGear") },
		updateGear: { _ in fatalError("\(Self.self).updateGear") },
		deleteGear: { _ in fatalError("\(Self.self).deleteGear") },
		fetchGear: { _ in fatalError("\(Self.self).fetchGear") },
		observeGear: { _ in fatalError("\(Self.self).observeGear") }
	)
}

extension DependencyValues {
	public var persistenceService: PersistenceService {
		get { self[PersistenceService.self] }
		set { self[PersistenceService.self] = newValue }
	}
}
