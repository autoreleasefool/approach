import BowlersDataProviderInterface
import ComposableArchitecture
import SharedModelsLibrary

public struct BowlersList: ReducerProtocol {
	enum ListObservable {}

	public struct State: Equatable {
		public var bowlers: IdentifiedArrayOf<Bowler> = []

		public init() {}
	}

	public enum Action: Equatable, Sendable {
		case onAppear
		case onDisappear
		case bowlersResponse(TaskResult<[Bowler]>)
	}

	public init() {}

	@Dependency(\.bowlersDataProvider) var bowlersDataProvider

	public var body: some ReducerProtocol<State, Action> {
		Reduce { state, action in
			switch action {
			case .onAppear:
				return .run { send in
					for await bowlers in bowlersDataProvider.fetchAll() {
						await send(.bowlersResponse(.success(bowlers)))
					}
				}
				.cancellable(id: ListObservable.self)

			case .onDisappear:
				return .cancel(id: ListObservable.self)

			case let .bowlersResponse(.success(bowlers)):
				state.bowlers = .init(uniqueElements: bowlers)
				return .none

			case .bowlersResponse(.failure):
				// TODO: handle failed bowler response
				return .none
			}
		}
	}
}
