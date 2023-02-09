import BaseFormLibrary
import ComposableArchitecture
import FeatureActionLibrary
import Foundation
import PersistenceServiceInterface
import SharedModelsLibrary
import StringsLibrary

extension Bowler: BaseFormModel {
	static public var modelName = Strings.Bowler.title
}

public struct BowlerEditor: ReducerProtocol {
	public typealias Form = BaseForm<Bowler, Fields>

	public struct Fields: BaseFormState, Equatable {
		@BindableState public var name = ""

		public let isDeleteable = true
		public var isSaveable: Bool {
			!name.isEmpty
		}
	}

	public struct State: Equatable {
		public var base: Form.State

		public init(mode: Form.Mode) {
			var fields = Fields()
			if case let .edit(bowler) = mode {
				fields.name = bowler.name
			}

			self.base = .init(mode: mode, form: fields)
		}
	}

	public enum Action: FeatureAction, BindableAction, Equatable {
		public enum ViewAction: Equatable {}
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
	@Dependency(\.persistenceService) var persistenceService

	public var body: some ReducerProtocol<State, Action> {
		BindingReducer()

		Scope(state: \.base, action: /Action.internal..Action.InternalAction.form) {
			BaseForm()
				.dependency(\.modelPersistence, .init(
					create: persistenceService.createBowler,
					update: persistenceService.updateBowler,
					delete: persistenceService.deleteBowler
				))
		}

		Reduce { _, action in
			switch action {
			case let .internal(internalAction):
				switch internalAction {
				case let .form(.delegate(delegateAction)):
					switch delegateAction {
					case let .didSaveModel(bowler):
						return .task { .internal(.form(.callback(.didFinishSaving(.success(bowler))))) }

					case let .didDeleteModel(bowler):
						return .task { .internal(.form(.callback(.didFinishDeleting(.success(bowler))))) }

					case .didFinishSaving, .didFinishDeleting:
						return .task { .delegate(.didFinishEditing) }
					}

				case .form(.view), .form(.internal), .form(.callback):
					return .none
				}

			case .binding, .view, .delegate:
				return .none
			}
		}
	}
}

extension BowlerEditor.Fields {
	public func model(fromExisting existing: Bowler?) -> Bowler {
		@Dependency(\.uuid) var uuid: UUIDGenerator

		return .init(
			id: existing?.id ?? uuid(),
			name: name
		)
	}
}
