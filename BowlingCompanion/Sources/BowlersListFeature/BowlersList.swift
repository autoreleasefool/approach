import BowlersDataProviderInterface
import BowlerFormFeature
import ComposableArchitecture
import LeaguesListFeature
import SharedModelsLibrary

public struct BowlersList: ReducerProtocol {
	public struct State: Equatable {
		public var bowlers: IdentifiedArrayOf<Bowler> = []
		public var selection: Identified<Bowler.ID, LeaguesList.State>?
		public var bowlerForm: BowlerForm.State?
		public var alert: AlertState<AlertAction>?

		public init() {}
	}

	public enum Action: Equatable {
		case subscribeToBowlers
		case delete(Bowler)
		case edit(Bowler)
		case alert(AlertAction)
		case setNavigation(selection: Bowler.ID?)
		case setFormSheet(isPresented: Bool)
		case bowlersResponse(TaskResult<[Bowler]>)
		case deleteBowlerResponse(TaskResult<Bool>)
		case bowlerForm(BowlerForm.Action)
		case leagues(LeaguesList.Action)
	}

	public init() {}

	@Dependency(\.bowlersDataProvider) var bowlersDataProvider

	public var body: some ReducerProtocol<State, Action> {
		Reduce { state, action in
			switch action {
			case .subscribeToBowlers:
				return .run { send in
					for try await bowlers in bowlersDataProvider.fetchAll(.init(ordering: .byLastModified)) {
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

			case let .edit(bowler):
				state.bowlerForm = .init(mode: .edit(bowler))
				return .none

			case let .delete(bowler):
				state.alert = self.alert(toDelete: bowler)
				return .none

			case .alert(.dismissed):
				state.alert = nil
				return .none

			case let .alert(.deleteButtonTapped(bowler)):
				return .task {
					return await .deleteBowlerResponse(TaskResult {
						try await bowlersDataProvider.delete(bowler)
						return true
					})
				}

			case .deleteBowlerResponse(.success):
				return .none

			case .deleteBowlerResponse(.failure):
				// TODO: handle failed delete bowler response
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

			case .bowlerForm(.deleteBowlerResult(.success)):
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
