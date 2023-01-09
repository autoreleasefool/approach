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
		public var isLeagueFiltersPresented = false
		public var leagueFilters: LeaguesFilter.State = .init()

		public init(bowler: Bowler) {
			self.bowler = bowler
		}
	}

	public enum Action: Equatable {
		case observeLeagues
		case errorButtonTapped
		case leaguesResponse(TaskResult<[League]>)
		case setNavigation(selection: League.ID?)
		case setEditorFormSheet(isPresented: Bool)
		case setFilterSheet(isPresented: Bool)
		case alert(AlertAction)
		case swipeAction(League, SwipeAction)
		case deleteLeagueResponse(TaskResult<Bool>)
		case leagueEditor(LeagueEditor.Action)
		case series(SeriesList.Action)
		case leaguesFilter(LeaguesFilter.Action)
	}

	public enum SwipeAction: Equatable {
		case edit
		case delete
	}

	struct ObservationCancellable {}

	public init() {}

	@Dependency(\.continuousClock) var clock
	@Dependency(\.persistenceService) var persistenceService
	@Dependency(\.leaguesDataProvider) var leaguesDataProvider
	@Dependency(\.recentlyUsedService) var recentlyUsedService
	@Dependency(\.featureFlags) var featureFlags

	public var body: some ReducerProtocol<State, Action> {
		Scope(state: \.leagueFilters, action: /LeaguesList.Action.leaguesFilter) {
			LeaguesFilter()
		}

		Reduce { state, action in
			switch action {
			case .observeLeagues:
				state.error = nil
				return .run { [bowler = state.bowler.id, filters = state.leagueFilters.filters] send in
					for try await leagues in leaguesDataProvider.observeLeagues(.init(filter: [.bowler(bowler)] + filters, ordering: .byRecentlyUsed)) {
						await send(.leaguesResponse(.success(leagues)))
					}
				} catch: { error, send in
					await send(.leaguesResponse(.failure(error)))
				}
				.cancellable(id: ObservationCancellable.self, cancelInFlight: true)

			case .errorButtonTapped:
				// TODO: handle error button tapped
				return .none

			case let .leaguesResponse(.success(leagues)):
				state.leagues = .init(uniqueElements: leagues)
				return .none

			case .leaguesResponse(.failure):
				state.error = .loadError
				return .none

			case .setFilterSheet(isPresented: true):
				state.isLeagueFiltersPresented = true
				return .none

			case .setFilterSheet(isPresented: false), .leaguesFilter(.applyButtonTapped):
				state.isLeagueFiltersPresented = false
				return .task { .observeLeagues }

			case .leaguesFilter(.binding):
				return .task { .observeLeagues }

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
				state.leagueEditor = .init(
					bowler: state.bowler,
					mode: .create,
					hasAlleysEnabled: featureFlags.isEnabled(.alleys)
				)
				return .none

			case .setEditorFormSheet(isPresented: false),
					.leagueEditor(.form(.didFinishSaving)),
					.leagueEditor(.form(.didFinishDeleting)),
					.leagueEditor(.form(.alert(.discardButtonTapped))):
				state.leagueEditor = nil
				return .none

			case let .swipeAction(league, .edit):
				state.leagueEditor = .init(
					bowler: state.bowler,
					mode: .edit(league),
					hasAlleysEnabled: featureFlags.isEnabled(.alleys)
				)
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

			case .deleteLeagueResponse(.failure):
				state.error = .deleteError
				return .none

			case .leagueEditor, .series, .leaguesFilter, .deleteLeagueResponse(.success):
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
