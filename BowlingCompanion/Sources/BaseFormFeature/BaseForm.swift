import ComposableArchitecture

public protocol BaseFormModel: Equatable {
	static var modelName: String { get }
	var name: String { get }
}

public protocol BaseFormState: Equatable {
	associatedtype Model: BaseFormModel

	var isSaveable: Bool { get }
	var isDeleteable: Bool { get }
	func model(fromExisting: Model?) -> Model
}

public struct BaseForm<Model: BaseFormModel, FormState: BaseFormState>: ReducerProtocol where Model == FormState.Model {
	public struct State: Equatable {
		public var mode: Mode
		public var isLoading = false
		public var alert: AlertState<AlertAction>?

		public let initialForm: FormState
		public var form: FormState

		public var hasChanges: Bool {
			form != initialForm
		}

		public var isSaveable: Bool {
			!isLoading && hasChanges && form.isSaveable
		}

		public init(mode: Mode, form: FormState) {
			self.mode = mode
			self.initialForm = form
			self.form = form
		}
	}

	public enum Mode: Equatable {
		case create
		case edit(Model)

		public var model: Model? {
			switch self {
			case .create: return nil
			case let .edit(model): return model
			}
		}
	}

	public enum Action: Equatable {
		case saveButtonTapped
		case discardButtonTapped
		case deleteButtonTapped
		case saveModelResult(TaskResult<Model>)
		case deleteModelResult(TaskResult<Model>)
		case didFinishSaving
		case didFinishDeleting
		case alert(AlertAction)
	}

	public init() {}

	@Dependency(\.modelPersistence) var modelPersistence

	public var body: some ReducerProtocol<State, Action> {
		Reduce { state, action in
			switch action {
			case .saveButtonTapped:
				guard state.isSaveable else { return .none }
				state.isLoading = true

				switch state.mode {
				case let .edit(original):
					let model = state.form.model(fromExisting: original)
					return .task {
						await .saveModelResult(TaskResult {
							try await modelPersistence.update(model)
							return model
						})
					}
				case .create:
					let model = state.form.model(fromExisting: nil)
					return .task {
						await .saveModelResult(TaskResult {
							try await modelPersistence.create(model)
							return model
						})
					}
				}

			case .saveModelResult(.failure):
				state.isLoading = false
				// TODO: show error
				return .none

			case .deleteButtonTapped:
				guard case .edit = state.mode else { return .none }
				state.alert = state.deleteAlert
				return .none

			case .alert(.deleteButtonTapped):
				state.alert = nil
				guard case let .edit(model) = state.mode else { return .none }
				state.isLoading = true
				return .task {
					await .deleteModelResult(TaskResult {
						try await modelPersistence.delete(model)
						return model
					})
				}

			case .deleteModelResult(.failure):
				state.isLoading = false
				// TODO: show error
				return .none

			case .discardButtonTapped:
				state.alert = state.discardAlert
				return .none

			case .alert(.discardButtonTapped):
				state = .init(mode: state.mode, form: state.initialForm)
				return .none

			case .alert(.dismissed):
				state.alert = nil
				return .none

			case .saveModelResult(.success),
					.deleteModelResult(.success),
					.didFinishSaving,
					.didFinishDeleting:
				return .none
			}
		}
	}
}
