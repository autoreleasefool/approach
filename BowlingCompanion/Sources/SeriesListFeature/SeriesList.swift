import ComposableArchitecture
import SeriesDataProviderInterface
import SharedModelsLibrary

public struct SeriesList: ReducerProtocol {
	enum ListObservable {}

	public struct State: Equatable {
		public var league: League
		public var series: IdentifiedArrayOf<Series> = []

		public init(league: League) {
			self.league = league
		}
	}

	public enum Action: Equatable {
		case onAppear
		case onDisappear
		case seriesResponse(TaskResult<[Series]>)
		case setFormSheet(isPresented: Bool)
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

			case .setFormSheet(isPresented: true):
				// TODO: show series sheet
				return .none

			case .setFormSheet(isPresented: false):
				// TODO: hide series sheet
				return .none
			}
		}
	}
}
