import BowlersDataProviderInterface
import ComposableArchitecture
import SharedModelsLibrary

public struct BowlerForm: ReducerProtocol {
	public struct State: Equatable {
		public var mode: Mode
		public var name = ""
		public var isSaving = false

		public var hasChanges: Bool {
			switch mode {
			case .create:
				return !name.isEmpty
			case let .edit(bowler):
				return name != bowler.name
			}
		}

		public var canSave: Bool {
			switch mode {
			case .create:
				return !name.isEmpty
			case .edit:
				return hasChanges && !name.isEmpty
			}
		}

		public init(mode: Mode) {
			self.mode = mode
			if case let .edit(bowler) = mode {
				self.name = bowler.name
			}
		}
	}

	public enum Mode: Equatable {
		case edit(Bowler)
		case create
	}

	public enum Action: Equatable {
		case nameChange(String)
		case saveButtonTapped
		case saveBowlerResult(TaskResult<Bowler>)
	}

	public init() {}

	@Dependency(\.uuid) var uuid
	@Dependency(\.bowlersDataProvider) var bowlersDataProvider

	public var body: some ReducerProtocol<State, Action> {
		Reduce { state, action in
			switch action {
			case let .nameChange(name):
				state.name = name
				return .none

			case .saveButtonTapped:
				guard state.canSave else {
					return .none
				}

				state.isSaving = true
				let bowler = Bowler(id: uuid(), name: state.name)
				return .task {
					return await .saveBowlerResult(TaskResult {
						try await bowlersDataProvider.save(bowler)
						return bowler
					})
				}

			case .saveBowlerResult(.success):
				state.isSaving = false
				return .none

			case .saveBowlerResult(.failure):
				// TODO: show error to user for failed save to db
				state.isSaving = false
				return .none
			}
		}
	}
}
