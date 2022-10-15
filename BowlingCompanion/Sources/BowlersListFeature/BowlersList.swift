import BowlersDataProviderInterface
import BowlerFormFeature
import ComposableArchitecture
import LeaguesListFeature
import SharedModelsLibrary

public struct BowlersList: ReducerProtocol {
	enum ListObservable {}

	public struct State: Equatable {
		public var bowlers: IdentifiedArrayOf<Bowler> = []
		public var selection: Identified<Bowler.ID, LeaguesList.State>?
		public var bowlerForm: BowlerForm.State?

		public init() {}
	}

	public enum Action: Equatable, Sendable {
		case onAppear
		case onDisappear
		case setNavigation(selection: Bowler.ID?)
		case setFormSheet(isPresented: Bool)
		case bowlersResponse(TaskResult<[Bowler]>)
		case bowlerForm(BowlerForm.Action)
		case leagues(LeaguesList.Action)
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
				// TODO: list observation doesn't cancel and leaks because store becomes nil before `onDisappear`
				return .cancel(id: ListObservable.self)

			case let .setNavigation(selection: .some(id)):
				if let selection = state.bowlers[id: id] {
					state.selection = Identified(.init(bowler: selection), id: selection.id)
				}
				return .none

			case .setNavigation(selection: .none):
				state.selection = nil
				return .none

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

			case .leagues:
				return .none
			}
		}
		.ifLet(\.bowlerForm, action: /BowlersList.Action.bowlerForm) {
			BowlerForm()
		}
		.ifLet(\.selection, action: /BowlersList.Action.leagues) {
			Scope(state: \Identified<Bowler.ID, LeaguesList.State>.value, action: /.self) {
				LeaguesList()
			}
		}
	}
}
