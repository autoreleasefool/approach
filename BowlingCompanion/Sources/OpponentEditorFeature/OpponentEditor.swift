import BaseFormFeature
import ComposableArchitecture
import Foundation
import PersistenceServiceInterface
import SharedModelsLibrary
import StringsLibrary

extension Opponent: BaseFormModel {
	static public var modelName = Strings.Opponent.title
}

public struct OpponentEditor: ReducerProtocol {
	public typealias Form = BaseForm<Opponent, Fields>

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
			if case let .edit(opponent) = mode {
				fields.name = opponent.name
			}

			self.base = .init(mode: mode, form: fields)
		}
	}

	public enum Action: BindableAction, Equatable {
		case binding(BindingAction<State>)
		case form(Form.Action)
	}

	public init() {}

	@Dependency(\.uuid) var uuid
	@Dependency(\.persistenceService) var persistenceService

	public var body: some ReducerProtocol<State, Action> {
		BindingReducer()

		Scope(state: \.base, action: /Action.form) {
			BaseForm()
				.dependency(\.modelPersistence, .init(
					create: persistenceService.createOpponent,
					update: persistenceService.updateOpponent,
					delete: persistenceService.deleteOpponent
				))
		}

		Reduce { state, action in
			switch action {
			case .form(.saveModelResult(.success)):
				state.base.isLoading = false
				return .task { .form(.didFinishSaving) }

			case .form(.deleteModelResult(.success)):
				state.base.isLoading = false
				return .task { .form(.didFinishDeleting) }

			case .form(.deleteModelResult(.failure)), .form(.saveModelResult(.failure)):
				state.base.isLoading = false
				return .none

			case .binding, .form:
				return .none
			}
		}
	}
}

extension OpponentEditor.Fields {
	public func model(fromExisting existing: Opponent?) -> Opponent {
		@Dependency(\.uuid) var uuid: UUIDGenerator

		return .init(
			id: existing?.id ?? uuid(),
			name: name
		)
	}
}
