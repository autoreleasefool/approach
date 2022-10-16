import BowlersDataProviderInterface
import ComposableArchitecture
import SharedModelsLibrary

public struct BowlerForm: ReducerProtocol {
	public struct State: Sendable, Equatable {
		public var mode: Mode
		public var name = ""
		public var isSaving = false

		public init(mode: Mode) {
			self.mode = mode
			if case let .edit(bowler) = mode {
				self.name = bowler.name
			}
		}
	}

	public enum Mode: Sendable, Equatable {
		case edit(Bowler)
		case create
	}

	public enum Action: Sendable, Equatable {
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
