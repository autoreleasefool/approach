import BowlersDataProviderInterface
import BowlerFormFeature
import ComposableArchitecture
import SharedModelsLibrary

public struct BowlersList: ReducerProtocol {
	enum ListObservable {}

	public struct State: Equatable {
		public var bowlers: IdentifiedArrayOf<Bowler> = []
		public var bowlerForm: BowlerForm.State?

		public init() {}
	}

	public enum Action: Equatable, Sendable {
		case onAppear
		case onDisappear
		case setFormSheet(isPresented: Bool)
		case bowlersResponse(TaskResult<[Bowler]>)
		case bowlerForm(BowlerForm.Action)
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

			case .setFormSheet(isPresented: true):
				state.bowlerForm = .init(mode: .create)
				return .none

			case .setFormSheet(isPresented: false):
				state.bowlerForm = nil
				return .none

			case .bowlerForm(.saveBowlerResult(.success)):
				state.bowlerForm = nil
				return .none

			case .bowlerForm:
				return .none
			}
		}
		.ifLet(\.bowlerForm, action: /BowlersList.Action.bowlerForm) {
			BowlerForm()
		}
	}
}
