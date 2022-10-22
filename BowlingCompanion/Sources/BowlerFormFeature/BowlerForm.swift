import BowlersDataProviderInterface
import ComposableArchitecture
import Foundation
import SharedModelsLibrary

public struct BowlerForm: ReducerProtocol {
	public struct State: Equatable {
		public var mode: Mode
		public var name = ""
		public var isLoading = false
		public var alert: AlertState<AlertAction>?

		public var hasChanges: Bool {
			self != .init(mode: mode)
		}

		public var canSave: Bool {
			!isLoading && hasChanges && !name.isEmpty
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
		case deleteBowlerResult(TaskResult<Bool>)
		case discardButtonTapped
		case deleteButtonTapped
		case alert(AlertAction)
	}

	public init() {}

	@Dependency(\.uuid) var uuid
	@Dependency(\.date) var date
	@Dependency(\.bowlersDataProvider) var bowlersDataProvider

	public var body: some ReducerProtocol<State, Action> {
		Reduce { state, action in
			switch action {
			case let .nameChange(name):
				state.name = name
				return .none

			case .saveButtonTapped:
				guard state.canSave else { return .none }
				state.isLoading = true

				switch state.mode {
				case let .edit(original):
					let bowler = state.bowler(id: original.id, createdAt: original.createdAt, lastModifiedAt: date())
					return .task {
						return await .saveBowlerResult(.init {
							try await bowlersDataProvider.update(bowler)
							return bowler
						})
					}
				case .create:
					let bowler = state.bowler(id: uuid(), createdAt: date(), lastModifiedAt: date())
					return .task {
						return await .saveBowlerResult(.init {
							try await bowlersDataProvider.create(bowler)
							return bowler
						})
					}
				}

			case .saveBowlerResult(.success):
				state.isLoading = false
				return .none

			case .saveBowlerResult(.failure):
				// TODO: show error to user for failed save to db
				state.isLoading = false
				return .none

			case .deleteButtonTapped:
				state.alert = self.buildDeleteAlert(state: state)
				return .none

			case .alert(.deleteButtonTapped):
				guard case let .edit(bowler) = state.mode else { return .none }
				state.isLoading = true
				return .task {
					await .deleteBowlerResult(TaskResult {
						try await bowlersDataProvider.delete(bowler)
						return true
					})
				}

			case .deleteBowlerResult(.success):
				return .none

			case .deleteBowlerResult(.failure):
				// TODO: show error to user for failed delete
				return .none

			case .discardButtonTapped:
				state.alert = self.discardAlert
				return .none

			case .alert(.discardButtonTapped):
				state = .init(mode: state.mode)
				return .none

			case .alert(.dismissed):
				state.alert = nil
				return .none
			}
		}
	}
}

extension BowlerForm.State {
	func bowler(id: UUID, createdAt: Date, lastModifiedAt: Date) -> Bowler {
		.init(
			id: id,
			name: name,
			createdAt: createdAt,
			lastModifiedAt: lastModifiedAt
		)
	}
}
