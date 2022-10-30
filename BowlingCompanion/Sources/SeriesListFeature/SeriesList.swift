import ComposableArchitecture
import GamesListFeature
import SeriesDataProviderInterface
import SeriesFormFeature
import SharedModelsLibrary

public struct SeriesList: ReducerProtocol {
	public struct State: Equatable {
		public var league: League
		public var series: IdentifiedArrayOf<Series> = []
		public var selection: Identified<Series.ID, GamesList.State>?
		public var newSeries: GamesList.State?
		public var seriesEditor: SeriesEditor.State?

		public init(league: League) {
			self.league = league
		}
	}

	public enum Action: Equatable {
		case subscribeToSeries
		case seriesResponse(TaskResult<[Series]>)
		case setNavigation(selection: Series.ID?)
		case seriesCreateResponse(TaskResult<Series>)
		case addSeriesButtonTapped
		case dismissNewSeries
		case swipeAction(Series, SwipeAction)
		case setEditorSheet(isPresented: Bool)
		case games(GamesList.Action)
		case seriesEditor(SeriesEditor.Action)
	}

	public enum SwipeAction: Equatable {
		case edit
		case delete
	}

	public init() {}

	@Dependency(\.uuid) var uuid
	@Dependency(\.date) var date
	@Dependency(\.seriesDataProvider) var seriesDataProvider

	public var body: some ReducerProtocol<State, Action> {
		Reduce { state, action in
			switch action {
			case .subscribeToSeries:
				return .run { [leagueId = state.league.id] send in
					for try await series in seriesDataProvider.fetchAll(.init(league: leagueId, ordering: .byDate)) {
						await send(.seriesResponse(.success(series)))
					}
				} catch: { error, send in
					await send(.seriesResponse(.failure(error)))
				}

			case let .seriesResponse(.success(series)):
				state.series = .init(uniqueElements: series)
				return .none

			case .seriesResponse(.failure):
				// TODO: show error when series fail to load
				return .none

			case .addSeriesButtonTapped:
				if let numberOfGames = state.league.numberOfGames {
					return .task { [leagueId = state.league.id] in
						let series = Series(leagueId: leagueId, id: uuid(), date: date(), numberOfGames: numberOfGames)
						return await .seriesCreateResponse(TaskResult {
							try await seriesDataProvider.create(series)
							return series
						})
					}
				} else {
					// TOD: add series
//					state.addSeries = .init()
					return .none
				}

			case let .seriesCreateResponse(.success(series)):
				state.newSeries = .init(series: series)
				return .none

			case .seriesCreateResponse(.failure):
				// TODO: show error creating series
				return .none

			case .dismissNewSeries:
				state.newSeries = nil
				return .none

			case let .setNavigation(selection: .some(id)):
				if let selection = state.series[id: id] {
					state.selection = Identified(.init(series: selection), id: selection.id)
				}
				return .none

			case .setNavigation(selection: .none):
				state.selection = nil
				return .none

			case .setEditorSheet(isPresented: true):
				// TODO: show series sheet
				return .none

			case .setEditorSheet(isPresented: false):
				state.seriesEditor = nil
				return .none

			case .seriesEditor(.form(.saveResult(.success))):
				state.seriesEditor = nil
				return .none

			case .seriesEditor(.form(.deleteResult(.success))):
				state.seriesEditor = nil
				return .none

			case let .swipeAction(series, .edit):
				state.seriesEditor = .init(league: state.league, mode: .edit(series))
				return .none

			case let .swipeAction(series, .delete):
				// TODO: show delete alert
				return .none

			case .games, .seriesEditor:
				return .none
			}
		}
		.ifLet(\.selection, action: /SeriesList.Action.games) {
			Scope(state: \Identified<Series.ID, GamesList.State>.value, action: /.self) {
				GamesList()
			}
		}
		.ifLet(\.newSeries, action: /SeriesList.Action.games) {
			GamesList()
		}
		.ifLet(\.seriesEditor, action: /SeriesList.Action.seriesEditor) {
			SeriesEditor()
		}
	}
}
