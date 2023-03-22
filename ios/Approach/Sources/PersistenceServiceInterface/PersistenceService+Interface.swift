import Dependencies
import SharedModelsLibrary
import SharedModelsFetchableLibrary

// swiftlint:disable function_body_length

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
		createBowler: { _ in unimplemented("\(Self.self).createBowler") },
		updateBowler: { _ in unimplemented("\(Self.self).updateBowler") },
		deleteBowler: { _ in unimplemented("\(Self.self).deleteBowler") },
		fetchBowlers: { _ in unimplemented("\(Self.self).fetchBowlers") },
		observeBowlers: { _ in unimplemented("\(Self.self).observeBowlers") },
		createLeague: { _ in unimplemented("\(Self.self).createLeague") },
		updateLeague: { _ in unimplemented("\(Self.self).updateLeague") },
		deleteLeague: { _ in unimplemented("\(Self.self).deleteLeague") },
		fetchLeagues: { _ in unimplemented("\(Self.self).fetchLeagues") },
		observeLeagues: { _ in unimplemented("\(Self.self).observeLeagues") },
		createSeries: { _ in unimplemented("\(Self.self).createSeries") },
		updateSeries: { _ in unimplemented("\(Self.self).updateSeries") },
		deleteSeries: { _ in unimplemented("\(Self.self).deleteSeries") },
		fetchSeries: { _ in unimplemented("\(Self.self).fetchSeries") },
		observeSeries: { _ in unimplemented("\(Self.self).observeSeries") },
		createGame: { _ in unimplemented("\(Self.self).createGame") },
		updateGame: { _ in unimplemented("\(Self.self).updateGame") },
		deleteGame: { _ in unimplemented("\(Self.self).deleteGame") },
		fetchGames: { _ in unimplemented("\(Self.self).fetchGames") },
		observeGames: { _ in unimplemented("\(Self.self).observeGames") },
		updateFrame: { _ in unimplemented("\(Self.self).updateFrame") },
		fetchFrames: { _ in unimplemented("\(Self.self).fetchFrames") },
		observeFrames: { _ in unimplemented("\(Self.self).observeFrames") },
		createAlley: { _ in unimplemented("\(Self.self).createAlley") },
		updateAlley: { _ in unimplemented("\(Self.self).updateAlley") },
		deleteAlley: { _ in unimplemented("\(Self.self).deleteAlley") },
		fetchAlleys: { _ in unimplemented("\(Self.self).fetchAlleys") },
		observeAlleys: { _ in unimplemented("\(Self.self).observeAlleys") },
		createLanes: { _ in unimplemented("\(Self.self).createLanes") },
		updateLanes: { _ in unimplemented("\(Self.self).updateLanes") },
		deleteLanes: { _ in unimplemented("\(Self.self).deleteLanes") },
		fetchLanes: { _ in unimplemented("\(Self.self).fetchLanes") },
		createGear: { _ in unimplemented("\(Self.self).createGear") },
		updateGear: { _ in unimplemented("\(Self.self).updateGear") },
		deleteGear: { _ in unimplemented("\(Self.self).deleteGear") },
		fetchGear: { _ in unimplemented("\(Self.self).fetchGear") },
		observeGear: { _ in unimplemented("\(Self.self).observeGear") },
		fetchAverages: { _ in unimplemented("\(Self.self).fetchAverages") },
		observeAverages: { _ in unimplemented("\(Self.self).observeAverages") },
		createTeam: { _ in unimplemented("\(Self.self).createTeam") },
		updateTeam: { _ in unimplemented("\(Self.self).updateTeam") },
		deleteTeam: { _ in unimplemented("\(Self.self).deleteTeam") },
		fetchTeams: { _ in unimplemented("\(Self.self).fetchTeams") },
		observeTeams: { _ in unimplemented("\(Self.self).observeTeams") },
		updateTeamMembers: { _ in unimplemented("\(Self.self).updateTeamMembers") },
		createOpponent: { _ in unimplemented("\(Self.self).createOpponent") },
		updateOpponent: { _ in unimplemented("\(Self.self).updateOpponent") },
		deleteOpponent: { _ in unimplemented("\(Self.self).deleteOpponent") },
		fetchOpponents: { _ in unimplemented("\(Self.self).fetchOpponents") },
		observeOpponents: { _ in unimplemented("\(Self.self).observeOpponents") }
	)
}

extension DependencyValues {
	public var persistenceService: PersistenceService {
		get { self[PersistenceService.self] }
		set { self[PersistenceService.self] = newValue }
	}
}
