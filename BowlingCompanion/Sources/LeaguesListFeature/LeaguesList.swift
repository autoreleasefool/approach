import ComposableArchitecture
import LeaguesDataProviderInterface
import LeagueEditorFeature
import PersistenceServiceInterface
import RecentlyUsedServiceInterface
import SeriesListFeature
import SharedModelsLibrary
import ViewsLibrary

public struct LeaguesList: ReducerProtocol {
	public struct State: Equatable {
		public var bowler: Bowler
		public var leagues: IdentifiedArrayOf<League>?
		public var error: ListErrorContent?
		public var selection: Identified<League.ID, SeriesList.State>?
		public var leagueEditor: LeagueEditor.State?
		public var alert: AlertState<AlertAction>?

		public init(bowler: Bowler) {
			self.bowler = bowler
		}
	}

	public enum Action: Equatable {
		case subscribeToLeagues
		case errorButtonTapped
		case leaguesResponse(TaskResult<[League]>)
		case setNavigation(selection: League.ID?)
		case setEditorFormSheet(isPresented: Bool)
		case alert(AlertAction)
		case swipeAction(League, SwipeAction)
		case deleteLeagueResponse(TaskResult<Bool>)
		case leagueEditor(LeagueEditor.Action)
		case series(SeriesList.Action)
	}

	public enum SwipeAction: Equatable {
		case edit
		case delete
	}

	public init() {}

	@Dependency(\.continuousClock) var clock
	@Dependency(\.persistenceService) var persistenceService
	@Dependency(\.leaguesDataProvider) var leaguesDataProvider
	@Dependency(\.recentlyUsedService) var recentlyUsedService

	public var body: some ReducerProtocol<State, Action> {
		Reduce { state, action in
			switch action {
			case .subscribeToLeagues:
				state.error = nil
				return .run { [bowlerId = state.bowler.id] send in
					for try await leagues in leaguesDataProvider.fetchLeagues(.init(bowler: bowlerId, ordering: .byRecentlyUsed)) {
						await send(.leaguesResponse(.success(leagues)))
					}
				} catch: { error, send in
					await send(.leaguesResponse(.failure(error)))
				}

			case .errorButtonTapped:
				// TODO: handle error button tapped
				return .none

			case let .leaguesResponse(.success(leagues)):
				state.leagues = .init(uniqueElements: leagues)
				return .none

			case .leaguesResponse(.failure):
				state.error = .loadError
				return .none

			case let .setNavigation(selection: .some(id)):
				if let selection = state.leagues?[id: id] {
					state.selection = Identified(.init(league: selection), id: selection.id)
					return .fireAndForget {
						try await clock.sleep(for: .seconds(1))
						recentlyUsedService.didRecentlyUseResource(.leagues, selection.id)
					}
				}
				return .none

			case .setNavigation(selection: .none):
				state.selection = nil
				return .none

			case .setEditorFormSheet(isPresented: true):
				state.leagueEditor = .init(bowler: state.bowler, mode: .create)
				return .none

			case .setEditorFormSheet(isPresented: false):
				state.leagueEditor = nil
				return .none

			case .leagueEditor(.form(.saveResult(.success))):
				state.leagueEditor = nil
				return .none

			case .leagueEditor(.form(.deleteResult(.success))):
				state.leagueEditor = nil
				return .none

			case let .swipeAction(league, .edit):
				state.leagueEditor = .init(bowler: state.bowler, mode: .edit(league))
				return .none

			case let .swipeAction(league, .delete):
				state.alert = self.alert(toDelete: league)
				return .none

			case .alert(.dismissed):
				state.alert = nil
				return .none

			case let .alert(.deleteButtonTapped(league)):
				return .task {
					return await .deleteLeagueResponse(TaskResult {
						try await persistenceService.deleteLeague(league)
						return true
					})
				}

			case .deleteLeagueResponse(.success):
				return .none

			case .deleteLeagueResponse(.failure):
				state.error = .deleteError
				return .none

			case .leagueEditor:
				return .none

			case .series:
				return .none
			}
		}
		.ifLet(\.leagueEditor, action: /LeaguesList.Action.leagueEditor) {
			LeagueEditor()
		}
		.ifLet(\.selection, action: /LeaguesList.Action.series) {
			Scope(state: \Identified<League.ID, SeriesList.State>.value, action: /.self) {
				SeriesList()
			}
		}
	}
}

extension ListErrorContent {
	static let loadError = Self(
		title: "Something went wrong!",
		message: "We couldn't load your data",
		action: "Try again"
	)

	static let deleteError = Self(
		title: "Something went wrong!",
		action: "Reload"
	)
}
