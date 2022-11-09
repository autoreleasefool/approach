import BowlerEditorFeature
import ComposableArchitecture
import LeaguesListFeature
import PersistenceServiceInterface
import SharedModelsLibrary

public struct BowlersList: ReducerProtocol {
	public struct State: Equatable {
		public var bowlers: IdentifiedArrayOf<Bowler> = []
		public var selection: Identified<Bowler.ID, LeaguesList.State>?
		public var bowlerEditor: BowlerEditor.State?
		public var alert: AlertState<AlertAction>?

		public init() {}
	}

	public enum Action: Equatable {
		case subscribeToBowlers
		case swipeAction(Bowler, SwipeAction)
		case alert(AlertAction)
		case setNavigation(selection: Bowler.ID?)
		case setFormSheet(isPresented: Bool)
		case bowlersResponse(TaskResult<[Bowler]>)
		case deleteBowlerResponse(TaskResult<Bool>)
		case bowlerEditor(BowlerEditor.Action)
		case leagues(LeaguesList.Action)
	}

	public enum SwipeAction: Equatable {
		case delete
		case edit
	}

	public init() {}

	@Dependency(\.persistenceService) var persistenceService

	public var body: some ReducerProtocol<State, Action> {
		Reduce { state, action in
			switch action {
			case .subscribeToBowlers:
				return .run { send in
					for try await bowlers in persistenceService.fetchBowlers(.init(ordering: .byLastModified)) {
						await send(.bowlersResponse(.success(bowlers)))
					}
				} catch: { error, send in
					await send(.bowlersResponse(.failure(error)))
				}

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

			case let .swipeAction(bowler, .edit):
				state.bowlerEditor = .init(mode: .edit(bowler))
				return .none

			case let .swipeAction(bowler, .delete):
				state.alert = BowlersList.alert(toDelete: bowler)
				return .none

			case .alert(.dismissed):
				state.alert = nil
				return .none

			case let .alert(.deleteButtonTapped(bowler)):
				return .task {
					return await .deleteBowlerResponse(TaskResult {
						try await persistenceService.deleteBowler(bowler)
						return true
					})
				}

			case .deleteBowlerResponse(.success):
				return .none

			case .deleteBowlerResponse(.failure):
				// TODO: handle failed delete bowler response
				return .none

			case .setFormSheet(isPresented: true):
				state.bowlerEditor = .init(mode: .create)
				return .none

			case .setFormSheet(isPresented: false):
				state.bowlerEditor = nil
				return .none

			case .bowlerEditor(.form(.saveResult(.success))):
				state.bowlerEditor = nil
				return .none

			case .bowlerEditor(.form(.deleteResult(.success))):
				state.bowlerEditor = nil
				return .none

			case .bowlerEditor:
				return .none

			case .leagues:
				return .none
			}
		}
		.ifLet(\.bowlerEditor, action: /BowlersList.Action.bowlerEditor) {
			BowlerEditor()
		}
		.ifLet(\.selection, action: /BowlersList.Action.leagues) {
			Scope(state: \Identified<Bowler.ID, LeaguesList.State>.value, action: /.self) {
				LeaguesList()
			}
		}
	}
}
