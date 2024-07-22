import ComposableArchitecture
import ErrorsFeature
import FeatureActionLibrary
import StringsLibrary

public protocol FormRecord: Identifiable, Equatable {
	static var isSaveableWithoutChanges: Bool { get }
	var name: String { get }
	var isSaveable: Bool { get }
	var saveButtonText: String { get }
}

public protocol CreateableRecord: FormRecord {
	static var modelName: String { get }
}

public protocol EditableRecord: FormRecord {
	var isDeleteable: Bool { get }
	var isArchivable: Bool { get }
}

extension FormRecord {
	public var saveButtonText: String {
		Strings.Action.save
	}

	public static var isSaveableWithoutChanges: Bool {
		false
	}
}

@Reducer
public struct Form<
	New: CreateableRecord,
	Existing: EditableRecord
>: Reducer, Sendable where New.ID == Existing.ID {
	public enum Value: Equatable {
		case create(New)
		case edit(Existing)

		public var record: (any FormRecord)? {
			switch self {
			case let.create(new): return new
			case let .edit(existing): return existing
			}
		}
	}

	@ObservableState
	public struct State: Equatable {
		public let modelName: String
		public var isLoading = false
		@Presents public var alert: AlertState<AlertAction>?

		public let initialValue: Value
		public var value: Value

		public var errors: Errors<ErrorID>.State = .init()

		public var hasChanges: Bool {
			initialValue != value
		}

		var title: String {
			switch initialValue {
			case .create:
				Strings.Form.Prompt.add(New.modelName)
			case let .edit(existing):
				Strings.Form.Prompt.edit(existing.name)
			}
		}

		public var isSaveableWithoutChanges: Bool {
			switch value {
			case .create: return New.isSaveableWithoutChanges
			case .edit: return Existing.isSaveableWithoutChanges
			}
		}

		public var isSaveable: Bool {
			!isLoading && (hasChanges || isSaveableWithoutChanges) && (value.record?.isSaveable ?? false)
		}

		public var isDeleteable: Bool {
			switch value {
			case .create: return false
			case let .edit(existing): return existing.isDeleteable
			}
		}

		public var isArchivable: Bool {
			switch value {
			case .create: return false
			case let .edit(existing): return existing.isArchivable
			}
		}

		public var saveButtonText: String {
			value.record?.saveButtonText ?? Strings.Action.save
		}

		public init(initialValue: Value, modelName: String = New.modelName) {
			self.initialValue = initialValue
			self.value = initialValue
			self.modelName = modelName
		}

		mutating func discard() {
			self = .init(initialValue: initialValue, modelName: modelName)
		}

		public mutating func didFinishCreating(_ record: Result<New, Error>) -> Effect<Form.Action> {
			isLoading = false
			switch record {
			case let .success(new):
				return .send(.delegate(.didFinishCreating(new)))
			case let .failure(error):
				return errors
					.enqueue(.failedToCreate, thrownError: error, toastMessage: Strings.Error.Toast.itemNotCreated(New.modelName))
					.map { .internal(.errors($0)) }
			}
		}

		public mutating func didFinishUpdating(_ record: Result<Existing, Error>) -> Effect<Form.Action> {
			isLoading = false
			switch record {
			case let .success(existing):
				return .send(.delegate(.didFinishUpdating(existing)))
			case let .failure(error):
				return errors
					.enqueue(.failedToUpdate, thrownError: error, toastMessage: Strings.Error.Toast.itemNotUpdated(New.modelName))
					.map { .internal(.errors($0)) }
			}
		}

		public mutating func didFinishDeleting(_ record: Result<Existing, Error>) -> Effect<Form.Action> {
			isLoading = false
			switch record {
			case let .success(existing):
				return .send(.delegate(.didFinishDeleting(existing)))
			case let .failure(error):
				return errors
					.enqueue(.failedToDelete, thrownError: error, toastMessage: Strings.Error.Toast.failedToDelete)
					.map { .internal(.errors($0)) }
			}
		}

		public mutating func didFinishArchiving(_ record: Result<Existing, Error>) -> Effect<Form.Action> {
			isLoading = false
			switch record {
			case let .success(existing):
				return .send(.delegate(.didFinishArchiving(existing)))
			case let .failure(error):
				return errors
					.enqueue(.failedToArchive, thrownError: error, toastMessage: Strings.Error.Toast.failedToArchive)
					.map { .internal(.errors($0)) }
			}
		}
	}

	public enum Action: FeatureAction {
		@CasePathable public enum View {
			case didTapSaveButton
			case didTapDiscardButton
			case didTapDeleteButton
			case didTapArchiveButton
			case alert(PresentationAction<AlertAction>)
		}

		@CasePathable public enum Internal {
			case errors(Errors<ErrorID>.Action)
		}

		@CasePathable public enum Delegate {
			case didCreate(Result<New, Error>)
			case didUpdate(Result<Existing, Error>)
			case didDelete(Result<Existing, Error>)
			case didArchive(Result<Existing, Error>)
			case didFinishCreating(New)
			case didFinishUpdating(Existing)
			case didFinishDeleting(Existing)
			case didFinishArchiving(Existing)
			case didDiscard
		}

		case view(View)
		case delegate(Delegate)
		case `internal`(Internal)
	}

	public enum AlertAction: Equatable {
		case didTapDeleteButton
		case didTapArchiveButton
		case didTapDiscardButton
		case didTapCancelButton
	}

	public enum ErrorID: Hashable {
		case failedToCreate
		case failedToUpdate
		case failedToDelete
		case failedToArchive
	}

	public init() {}

	@Dependency(RecordPersistence.self) var records

	public var body: some ReducerOf<Self> {
		Scope(state: \.errors, action: \.internal.errors) {
			Errors()
		}

		Reduce<State, Action> { state, action in
			switch action {

			case let .view(viewAction):
				switch viewAction {
				case .didTapSaveButton:
					guard state.isSaveable else { return .none }
					state.isLoading = true

					switch state.value {
					case let .edit(existing):
						return .run { send in
							await send(.delegate(.didUpdate(Result {
								try await records.update?(existing)
								return existing
							})))
						}

					case let .create(new):
						return .run { send in
							await send(.delegate(.didCreate(Result {
								try await records.create?(new)
								return new
							})))
						}
					}

				case .didTapArchiveButton:
					guard case let .edit(existing) = state.initialValue, state.isArchivable else { return .none }
					state.alert = AlertState {
						TextState(Strings.Form.Prompt.archive(existing.name))
					} actions: {
						ButtonState(role: .destructive, action: .didTapArchiveButton) { TextState(Strings.Action.archive) }
						ButtonState(role: .cancel, action: .didTapCancelButton) { TextState(Strings.Action.cancel) }
					} message: {
						TextState(Strings.Form.Prompt.Archive.message)
					}
					return .none

				case .didTapDeleteButton:
					guard case let .edit(existing) = state.initialValue, state.isDeleteable else { return .none }
					state.alert = AlertState {
						TextState(Strings.Form.Prompt.delete(existing.name))
					} actions: {
						ButtonState(role: .destructive, action: .didTapDeleteButton) { TextState(Strings.Action.delete) }
						ButtonState(role: .cancel, action: .didTapCancelButton) { TextState(Strings.Action.cancel) }
					}
					return .none

				case .didTapDiscardButton:
					guard state.hasChanges else { return .none }
					state.alert = AlertState {
						TextState(Strings.Form.Prompt.discardChanges)
					} actions: {
						ButtonState(role: .destructive, action: .didTapDiscardButton) { TextState(Strings.Action.discard) }
						ButtonState(role: .cancel, action: .didTapCancelButton) { TextState(Strings.Action.cancel) }
					}
					return .none

				case let .alert(alertAction):
					switch alertAction {
					case .presented(.didTapDeleteButton):
						guard case let .edit(record) = state.initialValue else { return .none }
						state.isLoading = true
						return .run { send in
							await send(.delegate(.didDelete(Result {
								try await records.delete?(record.id)
								return record
							})))
						}

					case .presented(.didTapArchiveButton):
						guard case let .edit(record) = state.initialValue else { return .none }
						state.isLoading = true
						return .run { send in
							await send(.delegate(.didArchive(Result {
								try await records.archive?(record.id)
								return record
							})))
						}

					case .presented(.didTapDiscardButton):
						state.discard()
						return .send(.delegate(.didDiscard))

					case .presented(.didTapCancelButton), .dismiss:
						return .none
					}
				}

			case let .internal(internalAction):
				switch internalAction {
				case .errors(.delegate(.doNothing)):
					return .none

				case .errors(.internal), .errors(.view):
					return .none
				}

			case .delegate:
				return .none
			}
		}
		.ifLet(\.$alert, action: \.view.alert)
	}
}
