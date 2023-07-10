import BowlersRepositoryInterface
import ComposableArchitecture
import EquatableLibrary
import FeatureActionLibrary
import FeatureFlagsLibrary
import FeatureFlagsServiceInterface
import FormLibrary
import Foundation
import GearRepositoryInterface
import ModelsLibrary
import ResourcePickerLibrary
import StringsLibrary

public typealias GearForm = Form<Gear.Create, Gear.Edit>

public struct GearEditor: Reducer {
	public struct State: Equatable {
		public let hasAvatarsEnabled: Bool

		public var name: String
		public var kind: Gear.Kind
		public var owner: Bowler.Summary?

		public var initialValue: GearForm.Value
		public var _form: GearForm.State

		@PresentationState var bowlerPicker: ResourcePicker<Bowler.Summary, AlwaysEqual<Void>>.State?

		public init(value: GearForm.Value) {
			self.initialValue = value
			self._form = .init(initialValue: value, currentValue: value)

			switch value {
			case let .create(new):
				self.name = new.name
				self.kind = new.kind
				self.owner = new.owner
			case let .edit(existing):
				self.name = existing.name
				self.kind = existing.kind
				self.owner = existing.owner
			}

			@Dependency(\.featureFlags) var featureFlags
			self.hasAvatarsEnabled = featureFlags.isEnabled(.avatars)
		}
	}

	public enum Action: FeatureAction, Equatable {
		public enum ViewAction: Equatable {
			case didTapOwner
			case didChangeName(String)
			case didChangeKind(Gear.Kind)
		}
		public enum DelegateAction: Equatable {}
		public enum InternalAction: Equatable {
			case form(GearForm.Action)
			case bowlerPicker(PresentationAction<ResourcePicker<Bowler.Summary, AlwaysEqual<Void>>.Action>)
		}

		case view(ViewAction)
		case delegate(DelegateAction)
		case `internal`(InternalAction)
	}

	public init() {}

	@Dependency(\.bowlers) var bowlers
	@Dependency(\.dismiss) var dismiss
	@Dependency(\.gear) var gear
	@Dependency(\.uuid) var uuid

	public var body: some ReducerOf<Self> {
		Scope(state: \.form, action: /Action.internal..Action.InternalAction.form) {
			GearForm()
				.dependency(\.records, .init(
					create: gear.create,
					update: gear.update,
					delete: gear.delete
				))
		}

		Reduce<State, Action> { state, action in
			switch action {
			case let .view(viewAction):
				switch viewAction {
				case let .didChangeKind(kind):
					state.kind = kind
					return .none

				case let .didChangeName(name):
					state.name = name
					return .none

				case .didTapOwner:
					state.bowlerPicker = .init(
						selected: Set([state.owner?.id].compactMap { $0 }),
						query: .init(()),
						limit: 1,
						showsCancelHeaderButton: false
					)
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
						return .run { _ in await self.dismiss() }
					}

				case let .bowlerPicker(.presented(.delegate(delegateAction))):
					switch delegateAction {
					case let .didChangeSelection(bowler):
						state.owner = bowler.first
						return .none
					}

				case .bowlerPicker(.dismiss):
					state.owner = state.bowlerPicker?.selectedResources?.first
					return .none

				case .form(.view), .form(.internal):
					return .none

				case .bowlerPicker(.presented(.view)), .bowlerPicker(.presented(.internal)):
					return .none
				}

			case .delegate:
				return .none
			}
		}
		.ifLet(\.$bowlerPicker, action: /Action.internal..Action.InternalAction.bowlerPicker) {
			ResourcePicker { _ in bowlers.pickable() }
		}
	}
}

extension GearEditor.State {
	var form: GearForm.State {
		get {
			var form = _form
			switch initialValue {
			case var .create(new):
				new.name = name
				new.kind = kind
				new.owner = owner
				form.value = .create(new)
			case var .edit(existing):
				existing.name = name
				existing.owner = owner
				form.value = .edit(existing)
			}
			return form
		}
		set {
			_form = newValue
		}
	}
}

extension Gear.Create: CreateableRecord {
	public static var modelName = Strings.Gear.title

	public var isSaveable: Bool {
		!name.isEmpty
	}
}

extension Gear.Edit: EditableRecord {
	public var isDeleteable: Bool { true }
	public var isSaveable: Bool {
		!name.isEmpty
	}
}

extension Bowler.Summary: PickableResource {
	static public func pickableModelName(forCount count: Int) -> String {
		count == 1 ? Strings.Bowler.title : Strings.Bowler.List.title
	}
}
