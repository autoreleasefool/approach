import ComposableArchitecture
import PersistenceServiceInterface
import SeriesEditorFeature
import SeriesSidebarFeature
import SharedModelsLibrary

public struct SeriesList: ReducerProtocol {
	public struct State: Equatable {
		public var league: League
		public var series: IdentifiedArrayOf<Series> = []
		public var selection: Identified<Series.ID, SeriesSidebar.State>?
		public var seriesEditor: SeriesEditor.State?
		public var createSeriesForm: CreateSeriesForm.State?
		public var newSeries: SeriesSidebar.State?
		public var alert: AlertState<AlertAction>?

		public init(league: League) {
			self.league = league
		}
	}

	public enum Action: Equatable {
		case subscribeToSeries
		case seriesResponse(TaskResult<[Series]>)
		case setNavigation(selection: Series.ID?)
		case setEditorSheet(isPresented: Bool)
		case seriesCreateResponse(TaskResult<Series>)
		case seriesDeleteResponse(TaskResult<Series>)
		case addSeriesButtonTapped
		case dismissNewSeries
		case swipeAction(Series, SwipeAction)
		case alert(AlertAction)

		case seriesSidebar(SeriesSidebar.Action)
		case seriesEditor(SeriesEditor.Action)
		case createSeries(CreateSeriesForm.Action)
	}

	public enum SwipeAction: Equatable {
		case edit
		case delete
	}

	public init() {}

	@Dependency(\.uuid) var uuid
	@Dependency(\.date) var date
	@Dependency(\.persistenceService) var persistenceService

	public var body: some ReducerProtocol<State, Action> {
		Reduce { state, action in
			switch action {
			case .subscribeToSeries:
				return .run { [leagueId = state.league.id] send in
					for try await series in persistenceService.fetchSeries(.init(league: leagueId, ordering: .byDate)) {
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
							try await persistenceService.createSeries(series)
							return series
						})
					}
				} else {
					state.createSeriesForm = .init()
					return .none
				}

			case let .seriesCreateResponse(.success(series)):
				state.newSeries = .init(series: series)
				return .none

			case .seriesCreateResponse(.failure):
				// TODO: show error creating series
				return .none

			case .dismissNewSeries:
				state.createSeriesForm = nil
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
				state.seriesEditor = .init(league: state.league, mode: .create)
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
				state.alert = SeriesList.alert(toDelete: series)
				return .none

			case .createSeries(.createButtonTapped):
				guard let numberOfGames = state.createSeriesForm?.numberOfGames else { return .none }
				state.createSeriesForm = nil
				return .task { [leagueId = state.league.id] in
					let series = Series(leagueId: leagueId, id: uuid(), date: date(), numberOfGames: numberOfGames)
					return await .seriesCreateResponse(TaskResult {
						try await persistenceService.createSeries(series)
						return series
					})
				}

			case .createSeries(.cancelButtonTapped):
				state.createSeriesForm = nil
				return .none

			case .alert(.dismissed):
				state.alert = nil
				return .none

			case let .alert(.deleteButtonTapped(series)):
				return .task {
					return await .seriesDeleteResponse(TaskResult {
						try await persistenceService.deleteSeries(series)
						return series
					})
				}

			case .seriesDeleteResponse(.failure):
				// TODO: show delete error
				return .none

			case .seriesSidebar, .seriesEditor, .createSeries, .seriesDeleteResponse(.success):
				return .none
			}
		}
		.ifLet(\.selection, action: /SeriesList.Action.seriesSidebar) {
			Scope(state: \Identified<Series.ID, SeriesSidebar.State>.value, action: /.self) {
				SeriesSidebar()
			}
		}
		.ifLet(\.newSeries, action: /SeriesList.Action.seriesSidebar) {
			SeriesSidebar()
		}
		.ifLet(\.seriesEditor, action: /SeriesList.Action.seriesEditor) {
			SeriesEditor()
		}
		.ifLet(\.createSeriesForm, action: /SeriesList.Action.createSeries) {
			CreateSeriesForm()
		}
	}
}
