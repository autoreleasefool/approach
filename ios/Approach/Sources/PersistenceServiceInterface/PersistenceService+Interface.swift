import Dependencies
import SharedModelsFetchableLibrary
import SharedModelsLibrary

public struct PersistenceService: Sendable {
	public var saveBowler: @Sendable (Bowler) async throws -> Void
	public var deleteBowler: @Sendable (Bowler) async throws -> Void
	public var observeBowler: @Sendable (Bowler.SingleFetchRequest) -> AsyncThrowingStream<Bowler?, Error>
	public var observeBowlers: @Sendable (Bowler.FetchRequest) -> AsyncThrowingStream<[Bowler], Error>

	public var saveLeague: @Sendable (League) async throws -> Void
	public var deleteLeague: @Sendable (League) async throws -> Void
	public var observeLeagues: @Sendable (League.FetchRequest) -> AsyncThrowingStream<[League], Error>

	public var saveSeries: @Sendable (Series) async throws -> Void
	public var deleteSeries: @Sendable (Series) async throws -> Void
	public var observeSeries: @Sendable (Series.FetchRequest) -> AsyncThrowingStream<[Series], Error>

	public var saveGame: @Sendable (Game) async throws -> Void
	public var deleteGame: @Sendable (Game) async throws -> Void
	public var observeGames: @Sendable (Game.FetchRequest) -> AsyncThrowingStream<[Game], Error>

	public var saveFrame: @Sendable (Frame) async throws -> Void
	public var fetchFrames: @Sendable (Frame.FetchRequest) async throws -> [Frame]

	public var saveAlley: @Sendable (Alley) async throws -> Void
	public var deleteAlley: @Sendable (Alley) async throws -> Void
	public var observeAlley: @Sendable (Alley.SingleFetchRequest) -> AsyncThrowingStream<Alley?, Error>
	public var observeAlleys: @Sendable (Alley.FetchRequest) -> AsyncThrowingStream<[Alley], Error>

	public var saveLanes: @Sendable ([Lane]) async throws -> Void
	public var deleteLanes: @Sendable ([Lane]) async throws -> Void
	public var fetchLanes: @Sendable (Lane.FetchRequest) async throws -> [Lane]
	public var observeLanes: @Sendable (Lane.FetchRequest) -> AsyncThrowingStream<[Lane], Error>

	public var saveGear: @Sendable (Gear) async throws -> Void
	public var deleteGear: @Sendable (Gear) async throws -> Void
	public var observeGear: @Sendable (Gear.FetchRequest) -> AsyncThrowingStream<[Gear], Error>

	public var saveTeam: @Sendable (Team) async throws -> Void
	public var deleteTeam: @Sendable (Team) async throws -> Void
	public var observeTeams: @Sendable (Team.FetchRequest) -> AsyncThrowingStream<[Team], Error>

	public var updateTeamMembers: @Sendable (TeamMembership) async throws -> Void

	public var saveOpponent: @Sendable (Opponent) async throws -> Void
	public var deleteOpponent: @Sendable (Opponent) async throws -> Void
	public var observeOpponents: @Sendable (Opponent.FetchRequest) -> AsyncThrowingStream<[Opponent], Error>

	public init(
		saveBowler: @escaping @Sendable (Bowler) async throws -> Void,
		deleteBowler: @escaping @Sendable (Bowler) async throws -> Void,
		observeBowler: @escaping @Sendable (Bowler.SingleFetchRequest) -> AsyncThrowingStream<Bowler?, Error>,
		observeBowlers: @escaping @Sendable (Bowler.FetchRequest) -> AsyncThrowingStream<[Bowler], Error>,
		saveLeague: @escaping @Sendable (League) async throws -> Void,
		deleteLeague: @escaping @Sendable (League) async throws -> Void,
		observeLeagues: @escaping @Sendable (League.FetchRequest) -> AsyncThrowingStream<[League], Error>,
		saveSeries: @escaping @Sendable (Series) async throws -> Void,
		deleteSeries: @escaping @Sendable (Series) async throws -> Void,
		observeSeries: @escaping @Sendable (Series.FetchRequest) -> AsyncThrowingStream<[Series], Error>,
		saveGame: @escaping @Sendable (Game) async throws -> Void,
		deleteGame: @escaping @Sendable (Game) async throws -> Void,
		observeGames: @escaping @Sendable (Game.FetchRequest) -> AsyncThrowingStream<[Game], Error>,
		saveFrame: @escaping @Sendable (Frame) async throws -> Void,
		fetchFrames: @escaping @Sendable (Frame.FetchRequest) async throws -> [Frame],
		saveAlley: @escaping @Sendable (Alley) async throws -> Void,
		deleteAlley: @escaping @Sendable (Alley) async throws -> Void,
		observeAlley: @escaping @Sendable (Alley.SingleFetchRequest) -> AsyncThrowingStream<Alley?, Error>,
		observeAlleys: @escaping @Sendable (Alley.FetchRequest) -> AsyncThrowingStream<[Alley], Error>,
		saveLanes: @escaping @Sendable ([Lane]) async throws -> Void,
		deleteLanes: @escaping @Sendable ([Lane]) async throws -> Void,
		fetchLanes: @escaping @Sendable (Lane.FetchRequest) async throws -> [Lane],
		observeLanes: @escaping @Sendable (Lane.FetchRequest) -> AsyncThrowingStream<[Lane], Error>,
		saveGear: @escaping @Sendable (Gear) async throws -> Void,
		deleteGear: @escaping @Sendable (Gear) async throws -> Void,
		observeGear: @escaping @Sendable (Gear.FetchRequest) -> AsyncThrowingStream<[Gear], Error>,
		saveTeam: @escaping @Sendable (Team) async throws -> Void,
		deleteTeam: @escaping @Sendable (Team) async throws -> Void,
		observeTeams: @escaping @Sendable (Team.FetchRequest) -> AsyncThrowingStream<[Team], Error>,
		updateTeamMembers: @escaping @Sendable (TeamMembership) async throws -> Void,
		saveOpponent: @escaping @Sendable (Opponent) async throws -> Void,
		deleteOpponent: @escaping @Sendable (Opponent) async throws -> Void,
		observeOpponents: @escaping @Sendable (Opponent.FetchRequest) -> AsyncThrowingStream<[Opponent], Error>
	) {
		self.saveBowler = saveBowler
		self.deleteBowler = deleteBowler
		self.observeBowler = observeBowler
		self.observeBowlers = observeBowlers
		self.saveLeague = saveLeague
		self.deleteLeague = deleteLeague
		self.observeLeagues = observeLeagues
		self.saveSeries = saveSeries
		self.deleteSeries = deleteSeries
		self.observeSeries = observeSeries
		self.saveGame = saveGame
		self.deleteGame = deleteGame
		self.observeGames = observeGames
		self.saveFrame = saveFrame
		self.fetchFrames = fetchFrames
		self.saveAlley = saveAlley
		self.deleteAlley = deleteAlley
		self.observeAlley = observeAlley
		self.observeAlleys = observeAlleys
		self.saveLanes = saveLanes
		self.deleteLanes = deleteLanes
		self.fetchLanes = fetchLanes
		self.observeLanes = observeLanes
		self.saveGear = saveGear
		self.deleteGear = deleteGear
		self.observeGear = observeGear
		self.saveTeam = saveTeam
		self.deleteTeam = deleteTeam
		self.observeTeams = observeTeams
		self.updateTeamMembers = updateTeamMembers
		self.saveOpponent = saveOpponent
		self.deleteOpponent = deleteOpponent
		self.observeOpponents = observeOpponents
	}
}

extension PersistenceService: TestDependencyKey {
	public static var testValue = Self(
		saveBowler: { _ in unimplemented("\(Self.self).saveBowler") },
		deleteBowler: { _ in unimplemented("\(Self.self).deleteBowler") },
		observeBowler: { _ in unimplemented("\(Self.self).observeBowler") },
		observeBowlers: { _ in unimplemented("\(Self.self).observeBowlers") },
		saveLeague: { _ in unimplemented("\(Self.self).saveLeague") },
		deleteLeague: { _ in unimplemented("\(Self.self).deleteLeague") },
		observeLeagues: { _ in unimplemented("\(Self.self).observeLeagues") },
		saveSeries: { _ in unimplemented("\(Self.self).saveSeries") },
		deleteSeries: { _ in unimplemented("\(Self.self).deleteSeries") },
		observeSeries: { _ in unimplemented("\(Self.self).observeSeries") },
		saveGame: { _ in unimplemented("\(Self.self).saveGame") },
		deleteGame: { _ in unimplemented("\(Self.self).deleteGame") },
		observeGames: { _ in unimplemented("\(Self.self).observeGames") },
		saveFrame: { _ in unimplemented("\(Self.self).saveFrame") },
		fetchFrames: { _ in unimplemented("\(Self.self).fetchFrames") },
		saveAlley: { _ in unimplemented("\(Self.self).saveAlley") },
		deleteAlley: { _ in unimplemented("\(Self.self).deleteAlley") },
		observeAlley: { _ in unimplemented("\(Self.self).observeAlley") },
		observeAlleys: { _ in unimplemented("\(Self.self).observeAlleys") },
		saveLanes: { _ in unimplemented("\(Self.self).saveLanes") },
		deleteLanes: { _ in unimplemented("\(Self.self).deleteLanes") },
		fetchLanes: { _ in unimplemented("\(Self.self).fetchLanes") },
		observeLanes: { _ in unimplemented("\(Self.self).observeLanes") },
		saveGear: { _ in unimplemented("\(Self.self).saveGear") },
		deleteGear: { _ in unimplemented("\(Self.self).deleteGear") },
		observeGear: { _ in unimplemented("\(Self.self).observeGear") },
		saveTeam: { _ in unimplemented("\(Self.self).saveTeam") },
		deleteTeam: { _ in unimplemented("\(Self.self).deleteTeam") },
		observeTeams: { _ in unimplemented("\(Self.self).observeTeams") },
		updateTeamMembers: { _ in unimplemented("\(Self.self).updateTeamMembers") },
		saveOpponent: { _ in unimplemented("\(Self.self).saveOpponent") },
		deleteOpponent: { _ in unimplemented("\(Self.self).deleteOpponent") },
		observeOpponents: { _ in unimplemented("\(Self.self).observeOpponents") }
	)
}

extension DependencyValues {
	public var persistenceService: PersistenceService {
		get { self[PersistenceService.self] }
		set { self[PersistenceService.self] = newValue }
	}
}
