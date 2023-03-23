import ComposableArchitecture
import FeatureActionLibrary
import StringsLibrary

public protocol BaseFormModel: Equatable {
	static var modelName: String { get }
	var name: String { get }
}

public protocol BaseFormState: Equatable {
	associatedtype Model: BaseFormModel

	var isSaveable: Bool { get }
	var isDeleteable: Bool { get }
	var saveButtonText: String { get }
	func model(fromExisting: Model?) -> Model
	func hasChanges(from: Self) -> Bool
}

extension BaseFormState {
	public var saveButtonText: String { Strings.Action.save }
	public func hasChanges(from other: Self) -> Bool {
		self != other
	}
}

public struct BaseForm<Model: BaseFormModel, FormState: BaseFormState>: Reducer where Model == FormState.Model {
	public struct State: Equatable {
		public var mode: Mode
		public var isLoading = false
		public var alert: AlertState<AlertAction>?

		public let initialForm: FormState
		public var form: FormState

		public var hasChanges: Bool {
			form.hasChanges(from: initialForm)
		}

		public var isSaveable: Bool {
			!isLoading && form.isSaveable && form.hasChanges(from: initialForm)
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

	public enum Action: FeatureAction, Equatable {
		public enum ViewAction: Equatable {
			case didTapSaveButton
			case didTapDiscardButton
			case didTapDeleteButton
			case alert(AlertAction)
		}

		public enum InternalAction: Equatable {
			case saveModelResult(TaskResult<Model>)
			case deleteModelResult(TaskResult<Model>)
		}

		public enum DelegateAction: Equatable {
			case didSaveModel(Model)
			case didDeleteModel(Model)
			case didFinishSaving
			case didFinishDeleting
			case didDiscard
		}

		public enum CallbackAction: Equatable {
			case didFinishSaving(TaskResult<Model>)
			case didFinishDeleting(TaskResult<Model>)
		}

		case view(ViewAction)
		case delegate(DelegateAction)
		case `internal`(InternalAction)
		case callback(CallbackAction)
	}

	public init() {}

	@Dependency(\.modelPersistence) var modelPersistence

	public var body: some Reducer<State, Action> {
		Reduce<State, Action> { state, action in
			switch action {
			case let .view(viewAction):
				switch viewAction {
				case .didTapSaveButton:
					guard state.isSaveable else { return .none }
					state.isLoading = true

					switch state.mode {
					case let .edit(original):
						let model = state.form.model(fromExisting: original)
						return .task {
							await .internal(.saveModelResult(TaskResult {
								try await modelPersistence.save(model)
								return model
							}))
						}
					case .create:
						let model = state.form.model(fromExisting: nil)
						return .task {
							await .internal(.saveModelResult(TaskResult {
								try await modelPersistence.save(model)
								return model
							}))
						}
					}
				case .didTapDeleteButton:
					guard case .edit = state.mode else { return .none }
					state.alert = state.deleteAlert
					return .none

				case .didTapDiscardButton:
					state.alert = state.discardAlert
					return .none

				case let .alert(alertAction):
					switch alertAction {
					case .didTapDeleteButton:
						state.alert = nil
						guard case let .edit(model) = state.mode else { return .none }
						state.isLoading = true
						return .task {
							await .internal(.deleteModelResult(TaskResult {
								try await modelPersistence.delete(model)
								return model
							}))
						}

					case .didTapDiscardButton:
						state = .init(mode: state.mode, form: state.initialForm)
						return .task { .delegate(.didDiscard) }

					case .didTapDismissButton:
						state.alert = nil
						return .none
					}
				}

			case let .callback(callbackAction):
				switch callbackAction {
				case .didFinishSaving(.success):
					state.isLoading = false
					return .task { .delegate(.didFinishSaving) }

				case .didFinishSaving(.failure):
					// TODO: handle failure saving model
					state.isLoading = false
					return .none

				case .didFinishDeleting(.success):
					state.isLoading = false
					return .task { .delegate(.didFinishDeleting) }

				case .didFinishDeleting(.failure):
					// TODO: handle failure deleting model
					state.isLoading = false
					return .none
				}

			case let .internal(internalAction):
				switch internalAction {
				case let .saveModelResult(.success(model)):
					return .task { .delegate(.didSaveModel(model)) }

				case .saveModelResult(.failure):
					state.isLoading = false
					// TODO: handle failure saving model
					return .none

				case let .deleteModelResult(.success(model)):
					return .task { .delegate(.didDeleteModel(model)) }

				case .deleteModelResult(.failure):
					state.isLoading = false
					// TODO: handle failure deleting model
					return .none
				}

			case .delegate:
				return .none
			}
		}
	}
}
