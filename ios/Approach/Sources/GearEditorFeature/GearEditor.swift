import AnalyticsServiceInterface
import AvatarEditorFeature
import AvatarServiceInterface
import BowlersRepositoryInterface
import ComposableArchitecture
import EquatableLibrary
import FeatureActionLibrary
import FeatureFlagsLibrary
import FeatureFlagsServiceInterface
import FormFeature
import Foundation
import GearRepositoryInterface
import ModelsLibrary
import PickableModelsLibrary
import ResourcePickerLibrary
import StringsLibrary
import UIKit

public typealias GearForm = Form<Gear.Create, Gear.Edit>

public struct AvatarImage: Equatable {
	public let id: Avatar.ID
	public let image: AlwaysEqual<UIImage>
}

public struct GearEditor: Reducer {
	public struct State: Equatable {
		@BindingState public var name: String
		@BindingState public var kind: Gear.Kind
		public var owner: Bowler.Summary?
		public var avatar: Avatar.Summary

		public var initialValue: GearForm.Value
		public var _form: GearForm.State

		@PresentationState var destination: Destination.State?

		public let isAvatarsEnabled: Bool

		public init(value: GearForm.Value) {
			self.initialValue = value
			self._form = .init(initialValue: value, currentValue: value)

			switch value {
			case let .create(new):
				self.name = new.name
				self.kind = new.kind
				self.owner = new.owner
				self.avatar = new.avatar
			case let .edit(existing):
				self.name = existing.name
				self.kind = existing.kind
				self.owner = existing.owner
				self.avatar = existing.avatar
			}

			@Dependency(\.featureFlags) var featureFlags
			self.isAvatarsEnabled = featureFlags.isEnabled(.avatars)
		}
	}

	public enum Action: FeatureAction, Equatable {
		public enum ViewAction: BindableAction, Equatable {
			case didTapOwner
			case didTapAvatar
			case binding(BindingAction<State>)
		}
		public enum DelegateAction: Equatable {}
		public enum InternalAction: Equatable {
			case form(GearForm.Action)
			case destination(PresentationAction<Destination.Action>)
		}

		case view(ViewAction)
		case delegate(DelegateAction)
		case `internal`(InternalAction)
	}

	public struct Destination: Reducer {
		public enum State: Equatable {
			case bowlerPicker(ResourcePicker<Bowler.Summary, AlwaysEqual<Void>>.State)
			case avatar(AvatarEditor.State)
		}

		public enum Action: Equatable {
			case bowlerPicker(ResourcePicker<Bowler.Summary, AlwaysEqual<Void>>.Action)
			case avatar(AvatarEditor.Action)
		}

		@Dependency(\.bowlers) var bowlers

		public var body: some ReducerOf<Self> {
			Scope(state: /State.bowlerPicker, action: /Action.bowlerPicker) {
				ResourcePicker { _ in bowlers.pickable() }
			}
			Scope(state: /State.avatar, action: /Action.avatar) {
				AvatarEditor()
			}
		}
	}

	public init() {}

	@Dependency(\.bowlers) var bowlers
	@Dependency(\.dismiss) var dismiss
	@Dependency(\.gear) var gear
	@Dependency(\.uuid) var uuid

	public var body: some ReducerOf<Self> {
		BindingReducer(action: /Action.view)

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
				case .didTapAvatar:
					state.destination = .avatar(.init(avatar: state.avatar))
					return .none

				case .didTapOwner:
					state.destination = .bowlerPicker(.init(
						selected: Set([state.owner?.id].compactMap { $0 }),
						query: .init(()),
						limit: 1,
						showsCancelHeaderButton: false
					))
					return .none

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

				case let .destination(.presented(.bowlerPicker(.delegate(delegateAction)))):
					switch delegateAction {
					case let .didChangeSelection(bowler):
						state.owner = bowler.first
						return .none
					}

				case let .destination(.presented(.avatar(.delegate(delegateAction)))):
					switch delegateAction {
					case let .didFinishEditing(avatar):
						state.avatar = avatar ?? state.avatar
						return .none
					}

				case .form(.view), .form(.internal):
					return .none

				case .destination(.dismiss),
						.destination(.presented(.bowlerPicker(.view))), .destination(.presented(.bowlerPicker(.internal))),
						.destination(.presented(.avatar(.view))), .destination(.presented(.avatar(.internal))):
					return .none
				}

			case .delegate:
				return .none
			}
		}
		.ifLet(\.$destination, action: /Action.internal..Action.InternalAction.destination) {
			Destination()
		}

		AnalyticsReducer<State, Action> { _, action in
			switch action {
			case let .internal(.form(.delegate(.didFinishCreating(gear)))):
				return Analytics.Gear.Created(kind: gear.kind.rawValue)
			case .internal(.form(.delegate(.didFinishUpdating))):
				return Analytics.Gear.Updated()
			case .internal(.form(.delegate(.didFinishDeleting))):
				return Analytics.Gear.Deleted()
			default:
				return nil
			}
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
				new.avatar = avatar
				form.value = .create(new)
			case var .edit(existing):
				existing.name = name
				existing.owner = owner
				existing.avatar = avatar
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
