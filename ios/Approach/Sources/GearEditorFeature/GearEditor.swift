import BowlersRepositoryInterface
import ComposableArchitecture
import EquatableLibrary
import ExtensionsLibrary
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
		@BindingState public var name: String
		@BindingState public var kind: Gear.Kind

		public var initialValue: GearForm.Value
		public var _form: GearForm.State

		public var bowlerPicker: ResourcePicker<Bowler.Summary, AlwaysEqual<Void>>.State
		public var isBowlerPickerPresented = false

		public var owner: Bowler.Summary? {
			get {
				switch _form.value {
				case let .create(new): return new.owner
				case let .edit(existing): return existing.owner
				}
			}
			set {
				switch _form.value {
				case let .create(new): _form.value = .create(.init(id: new.id, name: new.name, kind: new.kind, owner: newValue))
				case let .edit(existing): _form.value = .edit(.init(id: existing.id, name: existing.name, owner: newValue))
				}
			}
		}

		public init(value: GearForm.Value) {
			self.initialValue = value
			self._form = .init(initialValue: value, currentValue: value)
			let bowler: Bowler.Summary?

			switch value {
			case let .create(new):
				self.name = new.name
				self.kind = new.kind
				bowler = new.owner
			case let .edit(existing):
				self.name = existing.name
				self.kind = .other
				bowler = existing.owner
			}

			self.bowlerPicker = .init(
				selected: Set([bowler?.id].compactMap { $0 }),
				query: .init(()),
				limit: 1,
				showsCancelHeaderButton: false
			)

			@Dependency(\.featureFlags) var featureFlags: FeatureFlagsService
			self.hasAvatarsEnabled = featureFlags.isEnabled(.avatars)
		}
	}

	public enum Action: FeatureAction, BindableAction, Equatable {
		public enum ViewAction: Equatable {
			case setBowlerPicker(isPresented: Bool)
		}
		public enum DelegateAction: Equatable {
			case didFinishEditing
		}
		public enum InternalAction: Equatable {
			case form(GearForm.Action)
			case bowlerPicker(ResourcePicker<Bowler.Summary, AlwaysEqual<Void>>.Action)
		}

		case view(ViewAction)
		case delegate(DelegateAction)
		case `internal`(InternalAction)
		case binding(BindingAction<State>)
	}

	public init() {}

	@Dependency(\.bowlers) var bowlers
	@Dependency(\.dismiss) var dismiss
	@Dependency(\.gear) var gear
	@Dependency(\.uuid) var uuid

	public var body: some Reducer<State, Action> {
		BindingReducer()

		Scope(state: \.form, action: /Action.internal..Action.InternalAction.form) {
			GearForm()
				.dependency(\.records, .init(
					create: gear.create,
					update: gear.update,
					delete: gear.delete
				))
		}

		Scope(state: \.bowlerPicker, action: /Action.internal..Action.InternalAction.bowlerPicker) {
			ResourcePicker { _ in
				bowlers.playable(ordered: .byName)
			}
		}

		Reduce<State, Action> { state, action in
			switch action {
			case let .view(viewAction):
				switch viewAction {
				case let .setBowlerPicker(isPresented):
					state.isBowlerPickerPresented = isPresented
					return .none
				}

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

				case let .bowlerPicker(.delegate(delegateAction)):
					switch delegateAction {
					case .didFinishEditing:
						state.owner = state.bowlerPicker.selectedResources?.first
						state.isBowlerPickerPresented = false
						return .none
					}

				case .form(.view), .form(.internal):
					return .none

				case .bowlerPicker(.view), .bowlerPicker(.internal):
					return .none
				}

			case .binding, .delegate:
				return .none
			}
		}
	}
}

extension GearEditor.State {
	var form: GearForm.State {
		get {
			var form = _form
			switch initialValue {
			case let .create(new):
				form.value = .create(.init(id: new.id, name: name, kind: kind, owner: owner))
			case let .edit(existing):
				form.value = .edit(.init(id: existing.id, name: name, owner: owner))
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
