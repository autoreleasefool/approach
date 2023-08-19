import AnalyticsServiceInterface
import BowlersRepositoryInterface
import ComposableArchitecture
import FeatureActionLibrary
import FormFeature
import Foundation
import ModelsLibrary
import StringsLibrary

public typealias BowlerForm = Form<Bowler.Create, Bowler.Edit>

public struct BowlerEditor: Reducer {
	public struct State: Equatable {
		@BindingState public var name: String
		public let kind: Bowler.Kind

		public let initialValue: BowlerForm.Value
		public var _form: BowlerForm.State

		public init(value: BowlerForm.Value) {
			let isCreatingOpponent = (value.record as? Bowler.Create)?.kind == .opponent
			self.kind = isCreatingOpponent ? .opponent : .playable
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

	public enum Action: FeatureAction, Equatable {
		public enum ViewAction: BindableAction, Equatable {
			case binding(BindingAction<State>)
		}
		public enum DelegateAction: Equatable {}
		public enum InternalAction: Equatable {
			case form(BowlerForm.Action)
		}

		case view(ViewAction)
		case delegate(DelegateAction)
		case `internal`(InternalAction)
	}

	public init() {}

	@Dependency(\.bowlers) var bowlers
	@Dependency(\.dismiss) var dismiss
	@Dependency(\.uuid) var uuid

	public var body: some ReducerOf<Self> {
		BindingReducer(action: /Action.view)

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
			case let .view(viewAction):
				switch viewAction {
				case .binding:
					return .none
				}

			case let .internal(internalAction):
				switch internalAction {
				case let .form(.delegate(delegateAction)):
					switch delegateAction {
					case let .didCreate(result):
						return state._form.didFinishCreating(result)
							.map { .internal(.form($0)) }

					case let .didUpdate(result):
						return state._form.didFinishUpdating(result)
							.map { .internal(.form($0)) }

					case let .didDelete(result):
						return state._form.didFinishDeleting(result)
							.map { .internal(.form($0)) }

					case .didFinishCreating, .didFinishUpdating, .didFinishDeleting, .didDiscard:
						return .run { _ in await dismiss() }
					}

				case .form(.view), .form(.internal):
					return .none
				}

			case .delegate:
				return .none
			}
		}

		AnalyticsReducer<State, Action> { state, action in
			switch action {
			case .internal(.form(.delegate(.didFinishCreating))):
				return Analytics.Bowler.Created(kind: state.kind.rawValue)
			case .internal(.form(.delegate(.didFinishUpdating))):
				return Analytics.Bowler.Updated(kind: state.kind.rawValue)
			case .internal(.form(.delegate(.didFinishDeleting))):
				return Analytics.Bowler.Deleted(kind: state.kind.rawValue)
			default:
				return nil
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
