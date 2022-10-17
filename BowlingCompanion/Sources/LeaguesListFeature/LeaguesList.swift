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

		public init(bowler: Bowler) {
			self.bowler = bowler
		}
	}

	public enum Action: Equatable {
		case subscribeToLeagues
		case leaguesResponse(TaskResult<[League]>)
		case setNavigation(selection: League.ID?)
		case setFormSheet(isPresented: Bool)
		case leagueForm(LeagueForm.Action)
		case series(SeriesList.Action)
	}

	public init() {}

	@Dependency(\.leaguesDataProvider) var leaguesDataProvider

	public var body: some ReducerProtocol<State, Action> {
		Reduce { state, action in
			switch action {
			case .subscribeToLeagues:
				return .run { [bowler = state.bowler] send in
					for await leagues in leaguesDataProvider.fetchAll(bowler) {
						await send(.leaguesResponse(.success(leagues)))
					}
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

			case .leagueForm(.saveLeagueResult(.success)):
				state.leagueForm = nil
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
