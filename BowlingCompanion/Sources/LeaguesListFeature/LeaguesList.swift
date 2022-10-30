import ComposableArchitecture
import LeagueFormFeature
import LeaguesDataProviderInterface
import SeriesListFeature
import SharedModelsLibrary

public struct LeaguesList: ReducerProtocol {
	public struct State: Equatable {
		public var bowler: Bowler
		public var leagues: IdentifiedArrayOf<League> = []
		public var selection: Identified<League.ID, SeriesList.State>?
		public var leagueForm: LeagueForm.State?
		public var alert: AlertState<AlertAction>?

		public init(bowler: Bowler) {
			self.bowler = bowler
		}
	}

	public enum Action: Equatable {
		case subscribeToLeagues
		case leaguesResponse(TaskResult<[League]>)
		case setNavigation(selection: League.ID?)
		case setFormSheet(isPresented: Bool)
		case alert(AlertAction)
		case swipeAction(League, SwipeAction)
		case deleteLeagueResponse(TaskResult<Bool>)
		case leagueForm(LeagueForm.Action)
		case series(SeriesList.Action)
	}

	public enum SwipeAction: Equatable {
		case edit
		case delete
	}

	public init() {}

	@Dependency(\.leaguesDataProvider) var leaguesDataProvider

	public var body: some ReducerProtocol<State, Action> {
		Reduce { state, action in
			switch action {
			case .subscribeToLeagues:
				return .run { [bowlerId = state.bowler.id] send in
					for try await leagues in leaguesDataProvider.fetchAll(.init(bowler: bowlerId, ordering: .byLastModified)) {
						await send(.leaguesResponse(.success(leagues)))
					}
				} catch: { error, send in
					await send(.leaguesResponse(.failure(error)))
				}

			case let .leaguesResponse(.success(leagues)):
				state.leagues = .init(uniqueElements: leagues)
				return .none

			case .leaguesResponse(.failure):
				// TODO: show error when leagues fail to load
				return .none

			case let .setNavigation(selection: .some(id)):
				if let selection = state.leagues[id: id] {
					state.selection = Identified(.init(league: selection), id: selection.id)
				}
				return .none

			case .setNavigation(selection: .none):
				state.selection = nil
				return .none

			case .setFormSheet(isPresented: true):
				state.leagueForm = .init(bowler: state.bowler, mode: .create)
				return .none

			case .setFormSheet(isPresented: false):
				state.leagueForm = nil
				return .none

			case .leagueForm(.form(.saveResult(.success))):
				state.leagueForm = nil
				return .none

			case .leagueForm(.form(.deleteResult(.success))):
				state.leagueForm = nil
				return .none

			case let .swipeAction(league, .edit):
				state.leagueForm = .init(bowler: state.bowler, mode: .edit(league))
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
						try await leaguesDataProvider.delete(league)
						return true
					})
				}

			case .deleteLeagueResponse(.success):
				return .none

			case .deleteLeagueResponse(.failure):
				// TODO: handle failed delete league response
				return .none

			case .leagueForm:
				return .none

			case .series:
				return .none
			}
		}
		.ifLet(\.leagueForm, action: /LeaguesList.Action.leagueForm) {
			LeagueForm()
		}
		.ifLet(\.selection, action: /LeaguesList.Action.series) {
			Scope(state: \Identified<League.ID, SeriesList.State>.value, action: /.self) {
				SeriesList()
			}
		}
	}
}
