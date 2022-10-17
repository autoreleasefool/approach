import ComposableArchitecture
import Foundation
import GamesListFeature
import SeriesDataProviderInterface
import SharedModelsLibrary

public struct SeriesList: ReducerProtocol {
	enum ListObservable {}

	public struct State: Equatable {
		public var league: League
		public var series: IdentifiedArrayOf<Series> = []
		public var selection: Identified<Series.ID, GamesList.State>?
		public var newSeries: GamesList.State?

		public init(league: League) {
			self.league = league
		}
	}

	public enum Action: Equatable {
		case onAppear
		case onDisappear
		case seriesResponse(TaskResult<[Series]>)
		case setNavigation(selection: Series.ID?)
		case seriesCreateResponse(TaskResult<Series>)
		case addSeriesButtonTapped
		case dismissNewSeries
		case setFormSheet(isPresented: Bool)
		case games(GamesList.Action)
	}

	public init() {}

	@Dependency(\.uuid) var uuid
	@Dependency(\.seriesDataProvider) var seriesDataProvider

	public var body: some ReducerProtocol<State, Action> {
		Reduce { state, action in
			switch action {
			case .onAppear:
				return .run { [league = state.league] send in
					for await series in seriesDataProvider.fetchAll(league) {
						await send(.seriesResponse(.success(series)))
					}
				}
				.cancellable(id: ListObservable.self)

			case .onDisappear:
				// TODO: list observation doesn't cancel and leaks because store becomes nil before `onDisappear`
				return .cancel(id: ListObservable.self)

			case let .seriesResponse(.success(series)):
				state.series = .init(uniqueElements: series)
				return .none

			case .seriesResponse(.failure):
				// TODO: show error when series fail to load
				return .none

			case .addSeriesButtonTapped:
				return .task { [league = state.league] in
					let series = Series(id: uuid(), date: Date())
					return await .seriesCreateResponse(TaskResult {
						try await seriesDataProvider.create(league, series)
						return series
					})
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

			case .setFormSheet(isPresented: true):
				// TODO: show series sheet
				return .none

			case .setFormSheet(isPresented: false):
				// TODO: hide series sheet
				return .none

			case .games:
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
	}
}
