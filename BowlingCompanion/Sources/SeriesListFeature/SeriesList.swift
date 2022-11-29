import ComposableArchitecture
import FeatureFlagServiceInterface
import PersistenceServiceInterface
import SeriesEditorFeature
import SeriesSidebarFeature
import SharedModelsLibrary
import StringsLibrary
import ViewsLibrary

public struct SeriesList: ReducerProtocol {
	public struct State: Equatable {
		public var league: League
		public var series: IdentifiedArrayOf<Series>?
		public var error: ListErrorContent?
		public var selection: Identified<Series.ID, SeriesSidebar.State>?
		public var seriesEditor: SeriesEditor.State?
		public var newSeries: SeriesSidebar.State?
		public var alert: AlertState<AlertAction>?

		public init(league: League) {
			self.league = league
		}
	}

	public enum Action: Equatable {
		case refreshList
		case seriesResponse(TaskResult<[Series]>)
		case setNavigation(selection: Series.ID?)
		case setEditorFormSheet(isPresented: Bool)
		case seriesCreateResponse(TaskResult<Series>)
		case seriesDeleteResponse(TaskResult<Series>)
		case errorButtonTapped
		case dismissNewSeries
		case swipeAction(Series, SwipeAction)
		case alert(AlertAction)

		case seriesSidebar(SeriesSidebar.Action)
		case seriesEditor(SeriesEditor.Action)
	}

	public enum SwipeAction: Equatable {
		case edit
		case delete
	}

	public init() {}

	@Dependency(\.uuid) var uuid
	@Dependency(\.date) var date
	@Dependency(\.persistenceService) var persistenceService
	@Dependency(\.featureFlags) var featureFlags: FeatureFlagService

	public var body: some ReducerProtocol<State, Action> {
		Reduce { state, action in
			switch action {
			case .refreshList:
				state.error = nil
				return .task { [league = state.league.id] in
					await .seriesResponse(TaskResult {
						try await persistenceService.fetchSeries(.init(league: league, ordering: .byDate))
					})
				}

			case .errorButtonTapped:
				return .task { .refreshList }

			case let .seriesResponse(.success(series)):
				state.series = .init(uniqueElements: series)
				return .none

			case .seriesResponse(.failure):
				state.error = .loadError
				return .none

			case let .seriesCreateResponse(.success(series)):
				state.newSeries = .init(series: series)
				return .none

			case .seriesCreateResponse(.failure):
				state.error = .createError
				return .none

			case .dismissNewSeries:
				state.newSeries = nil
				return .none

			case let .setNavigation(selection: .some(id)):
				if let selection = state.series?[id: id] {
					state.selection = Identified(.init(series: selection), id: selection.id)
				}
				return .none

			case .setNavigation(selection: .none):
				state.selection = nil
				return .none

			case .setEditorFormSheet(isPresented: true):
				state.seriesEditor = .init(
					league: state.league,
					mode: .create,
					date: date(),
					hasAlleysEnabled: featureFlags.isEnabled(.alleyTracking)
				)
				return .none

			case .setEditorFormSheet(isPresented: false),
					.seriesEditor(.form(.saveResult(.success))),
					.seriesEditor(.form(.deleteResult(.success))),
					.seriesEditor(.form(.alert(.discardButtonTapped))):
				state.seriesEditor = nil
				return .none

			case let .swipeAction(series, .edit):
				state.seriesEditor = .init(
					league: state.league,
					mode: .edit(series),
					date: date(),
					hasAlleysEnabled: featureFlags.isEnabled(.alleyTracking)
				)
				return .none

			case let .swipeAction(series, .delete):
				state.alert = SeriesList.alert(toDelete: series)
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
				state.error = .deleteError
				return .none

			case .seriesSidebar, .seriesEditor, .seriesDeleteResponse(.success):
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
	}
}

extension ListErrorContent {
	static let createError = Self(
		title: Strings.Series.Errors.Create.title,
		message: Strings.Series.Errors.Create.message,
		action: Strings.Series.Errors.Create.tryAgain
	)
}
