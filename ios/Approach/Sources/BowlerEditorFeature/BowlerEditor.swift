import BaseFormLibrary
import BowlersRepositoryInterface
import ComposableArchitecture
import ExtensionsLibrary
import FeatureActionLibrary
import Foundation
import ModelsLibrary
import StringsLibrary

extension Bowler.Editable: BaseFormModel {
	static public var modelName = Strings.Bowler.title
}

public struct BowlerEditor: Reducer {
	public typealias Form = BaseForm<Bowler.Editable, Fields>

	public struct Fields: BaseFormState, Equatable {
		@BindingState public var bowler: Bowler.Editable

		public var model: Bowler.Editable { bowler }
		public let isDeleteable = true
		public var isSaveable: Bool {
			!bowler.name.isEmpty
		}
	}

	public struct State: Equatable {
		public var base: Form.State

		public init(mode: Form.Mode) {
			var fields: Fields
			if case let .edit(bowler) = mode {
				fields = .init(bowler: bowler)
			} else {
				@Dependency(\.uuid) var uuid: UUIDGenerator
				fields = .init(bowler: .init(id: uuid(), name: "", status: .playable))
			}

			self.base = .init(mode: mode, form: fields)
		}
	}

	public enum Action: FeatureAction, BindableAction, Equatable {
		public enum ViewAction: Equatable {
			case onAppear
		}
		public enum DelegateAction: Equatable {
			case didFinishEditing
		}
		public enum InternalAction: Equatable {
			case form(Form.Action)
		}

		case view(ViewAction)
		case delegate(DelegateAction)
		case `internal`(InternalAction)
		case binding(BindingAction<State>)
	}

	public init() {}

	@Dependency(\.uuid) var uuid
	@Dependency(\.bowlers) var bowlers

	public var body: some Reducer<State, Action> {
		BindingReducer()

		Scope(state: \.base, action: /Action.internal..Action.InternalAction.form) {
			BaseForm()
				.dependency(\.modelPersistence, .init(
					save: bowlers.save,
					delete: { try await bowlers.delete($0.id) }
				))
		}

		Reduce<State, Action> { state, action in
			switch action {
			case let .internal(internalAction):
				switch internalAction {
				case let .form(.delegate(delegateAction)):
					switch delegateAction {
					case let .didSaveModel(bowler):
						return .task { .internal(.form(.callback(.didFinishSaving(.success(bowler))))) }

					case let .didDeleteModel(bowler):
						return .task { .internal(.form(.callback(.didFinishDeleting(.success(bowler))))) }

					case .didFinishSaving, .didFinishDeleting, .didDiscard:
						return .task { .delegate(.didFinishEditing) }
					}

				case .form(.view), .form(.internal), .form(.callback):
					return .none
				}

			case let .view(viewAction):
				switch viewAction {
				case .onAppear:
					return .none
				}

			case .binding, .delegate:
				return .none
			}
		}
	}
}
