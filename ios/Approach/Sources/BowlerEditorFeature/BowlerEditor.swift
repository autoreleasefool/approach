import BowlersRepositoryInterface
import ComposableArchitecture
import ExtensionsLibrary
import FeatureActionLibrary
import FormLibrary
import Foundation
import ModelsLibrary
import StringsLibrary

public typealias BowlerForm = Form<Bowler.Create, Bowler.Edit>

public struct BowlerEditor: Reducer {
	public struct State: Equatable {
		@BindingState public var name: String

		public var initialValue: BowlerForm.Value
		public var _form: BowlerForm.State

		public init(value: BowlerForm.Value) {
			let isCreatingOpponent = (value.record as? Bowler.Create)?.status == .opponent
			self.initialValue = value
			self._form = .init(
				initialValue: value,
				currentValue: value,
				modelName: isCreatingOpponent ? Strings.Opponent.title : Bowler.Create.modelName
			)

			switch value {
			case let .create(new):
				self.name = new.name
			case let .edit(existing):
				self.name = existing.name
			}
		}
	}

	public enum Action: FeatureAction, BindableAction, Equatable {
		public enum ViewAction: Equatable {}
		public enum DelegateAction: Equatable {
			case didFinishEditing
		}
		public enum InternalAction: Equatable {
			case form(BowlerForm.Action)
		}

		case view(ViewAction)
		case delegate(DelegateAction)
		case `internal`(InternalAction)
		case binding(BindingAction<State>)
	}

	public init() {}

	@Dependency(\.bowlers) var bowlers
	@Dependency(\.dismiss) var dismiss
	@Dependency(\.uuid) var uuid

	public var body: some Reducer<State, Action> {
		BindingReducer()

		Scope(state: \.form, action: /Action.internal..Action.InternalAction.form) {
			BowlerForm()
				.dependency(\.records, .init(
					create: bowlers.create,
					update: bowlers.update,
					delete: bowlers.delete
				))
		}

		Reduce<State, Action> { state, action in
			switch action {
			case let .internal(internalAction):
				switch internalAction {
				case let .form(.delegate(delegateAction)):
					switch delegateAction {
					case let .didCreate(result):
						return state.form.didFinishCreating(result)
							.map { .internal(.form($0)) }

					case let .didUpdate(result):
						return state.form.didFinishUpdating(result)
							.map { .internal(.form($0)) }

					case let .didDelete(result):
						return state.form.didFinishDeleting(result)
							.map { .internal(.form($0)) }

					case .didFinishCreating, .didFinishUpdating, .didFinishDeleting, .didDiscard:
						return .fireAndForget { await self.dismiss() }

					}

				case .form(.view), .form(.internal):
					return .none
				}

			case let .view(viewAction):
				switch viewAction {
				case .never:
					return .none
				}

			case .binding, .delegate:
				return .none
			}
		}
	}
}

extension BowlerEditor.State {
	var form: BowlerForm.State {
		get {
			var form = _form
			switch initialValue {
			case var .create(new):
				new.name = name
				form.value = .create(new)
			case var .edit(existing):
				existing.name = name
				form.value = .edit(existing)
			}
			return form
		}
		set {
			_form = newValue
		}
	}
}

extension Bowler.Create: CreateableRecord {
	public static var modelName = Strings.Bowler.title

	public var isSaveable: Bool {
		!name.isEmpty
	}
}

extension Bowler.Edit: EditableRecord {
	public var isDeleteable: Bool { true }
	public var isSaveable: Bool {
		!name.isEmpty
	}
}
