import Dependencies
import SharedModelsLibrary
import SharedModelsFetchableLibrary

public struct PersistenceService: Sendable {
	public var createBowler: @Sendable (Bowler) async throws -> Void
	public var updateBowler: @Sendable (Bowler) async throws -> Void
	public var deleteBowler: @Sendable (Bowler) async throws -> Void
	public var fetchBowlers: @Sendable (Bowler.FetchRequest) async throws -> [Bowler]
	public var observeBowlers: @Sendable (Bowler.FetchRequest) -> AsyncThrowingStream<[Bowler], Error>

	public var createLeague: @Sendable (League) async throws -> Void
	public var updateLeague: @Sendable (League) async throws -> Void
	public var deleteLeague: @Sendable (League) async throws -> Void
	public var fetchLeagues: @Sendable (League.FetchRequest) async throws -> [League]
	public var observeLeagues: @Sendable (League.FetchRequest) -> AsyncThrowingStream<[League], Error>

	public var createSeries: @Sendable (Series) async throws -> Void
	public var updateSeries: @Sendable (Series) async throws -> Void
	public var deleteSeries: @Sendable (Series) async throws -> Void
	public var fetchSeries: @Sendable(Series.FetchRequest) async throws -> [Series]
	public var observeSeries: @Sendable (Series.FetchRequest) -> AsyncThrowingStream<[Series], Error>

	public var createGame: @Sendable (Game) async throws -> Void
	public var updateGame: @Sendable (Game) async throws -> Void
	public var deleteGame: @Sendable (Game) async throws -> Void
	public var fetchGames: @Sendable (Game.FetchRequest) async throws -> [Game]
	public var observeGames: @Sendable (Game.FetchRequest) -> AsyncThrowingStream<[Game], Error>

	public var updateFrame: @Sendable (Frame) async throws -> Void
	public var fetchFrames: @Sendable (Frame.FetchRequest) async throws -> [Frame]
	public var observeFrames: @Sendable (Frame.FetchRequest) -> AsyncThrowingStream<[Frame], Error>

	public var createAlley: @Sendable (Alley) async throws -> Void
	public var updateAlley: @Sendable (Alley) async throws -> Void
	public var deleteAlley: @Sendable (Alley) async throws -> Void
	public var fetchAlleys: @Sendable (Alley.FetchRequest) async throws -> [Alley]
	public var observeAlleys: @Sendable (Alley.FetchRequest) -> AsyncThrowingStream<[Alley], Error>

	public var createLanes: @Sendable ([Lane]) async throws -> Void
	public var updateLanes: @Sendable ([Lane]) async throws -> Void
	public var deleteLanes: @Sendable ([Lane]) async throws -> Void
	public var fetchLanes: @Sendable (Lane.FetchRequest) async throws -> [Lane]

	public var createGear: @Sendable (Gear) async throws -> Void
	public var updateGear: @Sendable (Gear) async throws -> Void
	public var deleteGear: @Sendable (Gear) async throws -> Void
	public var fetchGear: @Sendable (Gear.FetchRequest) async throws -> [Gear]
	public var observeGear: @Sendable (Gear.FetchRequest) -> AsyncThrowingStream<[Gear], Error>

	public var fetchAverages: @Sendable (Average.FetchRequest) async throws -> [Average]
	public var observeAverages: @Sendable (Average.FetchRequest) -> AsyncThrowingStream<[Average], Error>

	public var createTeam: @Sendable (Team) async throws -> Void
	public var updateTeam: @Sendable (Team) async throws -> Void
	public var deleteTeam: @Sendable (Team) async throws -> Void
	public var fetchTeams: @Sendable (Team.FetchRequest) async throws -> [Team]
	public var observeTeams: @Sendable (Team.FetchRequest) -> AsyncThrowingStream<[Team], Error>

	public var updateTeamMembers: @Sendable (TeamMembership) async throws -> Void

	public var createOpponent: @Sendable (Opponent) async throws -> Void
	public var updateOpponent: @Sendable (Opponent) async throws -> Void
	public var deleteOpponent: @Sendable (Opponent) async throws -> Void
	public var fetchOpponents: @Sendable (Opponent.FetchRequest) async throws -> [Opponent]
	public var observeOpponents: @Sendable (Opponent.FetchRequest) -> AsyncThrowingStream<[Opponent], Error>

	public init(
		createBowler: @escaping @Sendable (Bowler) async throws -> Void,
		updateBowler: @escaping @Sendable (Bowler) async throws -> Void,
		deleteBowler: @escaping @Sendable (Bowler) async throws -> Void,
		fetchBowlers: @escaping @Sendable (Bowler.FetchRequest) async throws -> [Bowler],
		observeBowlers: @escaping @Sendable (Bowler.FetchRequest) -> AsyncThrowingStream<[Bowler], Error>,
		createLeague: @escaping @Sendable (League) async throws -> Void,
		updateLeague: @escaping @Sendable (League) async throws -> Void,
		deleteLeague: @escaping @Sendable (League) async throws -> Void,
		fetchLeagues: @escaping @Sendable (League.FetchRequest) async throws -> [League],
		observeLeagues: @escaping @Sendable (League.FetchRequest) -> AsyncThrowingStream<[League], Error>,
		createSeries: @escaping @Sendable (Series) async throws -> Void,
		updateSeries: @escaping @Sendable (Series) async throws -> Void,
		deleteSeries: @escaping @Sendable (Series) async throws -> Void,
		fetchSeries: @escaping @Sendable (Series.FetchRequest) async throws -> [Series],
		observeSeries: @escaping @Sendable (Series.FetchRequest) -> AsyncThrowingStream<[Series], Error>,
		createGame: @escaping @Sendable (Game) async throws -> Void,
		updateGame: @escaping @Sendable (Game) async throws -> Void,
		deleteGame: @escaping @Sendable (Game) async throws -> Void,
		fetchGames: @escaping @Sendable (Game.FetchRequest) async throws -> [Game],
		observeGames: @escaping @Sendable (Game.FetchRequest) -> AsyncThrowingStream<[Game], Error>,
		updateFrame: @escaping @Sendable (Frame) async throws -> Void,
		fetchFrames: @escaping @Sendable (Frame.FetchRequest) async throws -> [Frame],
		observeFrames: @escaping @Sendable (Frame.FetchRequest) -> AsyncThrowingStream<[Frame], Error>,
		createAlley: @escaping @Sendable (Alley) async throws -> Void,
		updateAlley: @escaping @Sendable (Alley) async throws -> Void,
		deleteAlley: @escaping @Sendable (Alley) async throws -> Void,
		fetchAlleys: @escaping @Sendable (Alley.FetchRequest) async throws -> [Alley],
		observeAlleys: @escaping @Sendable (Alley.FetchRequest) -> AsyncThrowingStream<[Alley], Error>,
		createLanes: @escaping @Sendable ([Lane]) async throws -> Void,
		updateLanes: @escaping @Sendable ([Lane]) async throws -> Void,
		deleteLanes: @escaping @Sendable ([Lane]) async throws -> Void,
		fetchLanes: @escaping @Sendable (Lane.FetchRequest) async throws -> [Lane],
		createGear: @escaping @Sendable (Gear) async throws -> Void,
		updateGear: @escaping @Sendable (Gear) async throws -> Void,
		deleteGear: @escaping @Sendable (Gear) async throws -> Void,
		fetchGear: @escaping @Sendable (Gear.FetchRequest) async throws -> [Gear],
		observeGear: @escaping @Sendable (Gear.FetchRequest) -> AsyncThrowingStream<[Gear], Error>,
		fetchAverages: @escaping @Sendable (Average.FetchRequest) async throws -> [Average],
		observeAverages: @escaping @Sendable (Average.FetchRequest) -> AsyncThrowingStream<[Average], Error>,
		createTeam: @escaping @Sendable (Team) async throws -> Void,
		updateTeam: @escaping @Sendable (Team) async throws -> Void,
		deleteTeam: @escaping @Sendable (Team) async throws -> Void,
		fetchTeams: @escaping @Sendable (Team.FetchRequest) async throws -> [Team],
		observeTeams: @escaping @Sendable (Team.FetchRequest) -> AsyncThrowingStream<[Team], Error>,
		updateTeamMembers: @escaping @Sendable (TeamMembership) async throws -> Void,
		createOpponent: @escaping @Sendable (Opponent) async throws -> Void,
		updateOpponent: @escaping @Sendable (Opponent) async throws -> Void,
		deleteOpponent: @escaping @Sendable (Opponent) async throws -> Void,
		fetchOpponents: @escaping @Sendable (Opponent.FetchRequest) async throws -> [Opponent],
		observeOpponents: @escaping @Sendable (Opponent.FetchRequest) -> AsyncThrowingStream<[Opponent], Error>
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
		self.createLanes = createLanes
		self.updateLanes = updateLanes
		self.deleteLanes = deleteLanes
		self.fetchLanes = fetchLanes
		self.createGear = createGear
		self.updateGear = updateGear
		self.deleteGear = deleteGear
		self.fetchGear = fetchGear
		self.observeGear = observeGear
		self.fetchAverages = fetchAverages
		self.observeAverages = observeAverages
		self.createTeam = createTeam
		self.updateTeam = updateTeam
		self.deleteTeam = deleteTeam
		self.fetchTeams = fetchTeams
		self.observeTeams = observeTeams
		self.updateTeamMembers = updateTeamMembers
		self.createOpponent = createOpponent
		self.updateOpponent = updateOpponent
		self.deleteOpponent = deleteOpponent
		self.fetchOpponents = fetchOpponents
		self.observeOpponents = observeOpponents
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
		createLanes: { _ in fatalError("\(Self.self).createLanes") },
		updateLanes: { _ in fatalError("\(Self.self).updateLanes") },
		deleteLanes: { _ in fatalError("\(Self.self).deleteLanes") },
		fetchLanes: { _ in fatalError("\(Self.self).fetchLanes") },
		createGear: { _ in fatalError("\(Self.self).createGear") },
		updateGear: { _ in fatalError("\(Self.self).updateGear") },
		deleteGear: { _ in fatalError("\(Self.self).deleteGear") },
		fetchGear: { _ in fatalError("\(Self.self).fetchGear") },
		observeGear: { _ in fatalError("\(Self.self).observeGear") },
		fetchAverages: { _ in fatalError("\(Self.self).fetchAverages") },
		observeAverages: { _ in fatalError("\(Self.self).observeAverages") },
		createTeam: { _ in fatalError("\(Self.self).createTeam") },
		updateTeam: { _ in fatalError("\(Self.self).updateTeam") },
		deleteTeam: { _ in fatalError("\(Self.self).deleteTeam") },
		fetchTeams: { _ in fatalError("\(Self.self).fetchTeams") },
		observeTeams: { _ in fatalError("\(Self.self).observeTeams") },
		updateTeamMembers: { _ in fatalError("\(Self.self).updateTeamMembers") },
		createOpponent: { _ in fatalError("\(Self.self).createOpponent") },
		updateOpponent: { _ in fatalError("\(Self.self).updateOpponent") },
		deleteOpponent: { _ in fatalError("\(Self.self).deleteOpponent") },
		fetchOpponents: { _ in fatalError("\(Self.self).fetchOpponents") },
		observeOpponents: { _ in fatalError("\(Self.self).observeOpponents") }
	)
}

extension DependencyValues {
	public var persistenceService: PersistenceService {
		get { self[PersistenceService.self] }
		set { self[PersistenceService.self] = newValue }
	}
}
