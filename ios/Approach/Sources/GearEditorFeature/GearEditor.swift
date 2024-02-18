import AnalyticsServiceInterface
import AvatarEditorFeature
import AvatarServiceInterface
import BowlersRepositoryInterface
import ComposableArchitecture
import EquatableLibrary
import FeatureActionLibrary
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

@Reducer
public struct GearEditor: Reducer {
	@ObservableState
	public struct State: Equatable {
		public var name: String
		public var kind: Gear.Kind
		public var owner: Bowler.Summary?
		public var avatar: Avatar.Summary

		public var initialValue: GearForm.Value
		public var _form: GearForm.State

		var isEditing: Bool {
			switch initialValue {
			case .create: false
			case .edit: true
			}
		}

		@Presents var destination: Destination.State?

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
		}
	}

	public enum Action: FeatureAction, ViewAction, BindableAction {
		@CasePathable public enum View {
			case onAppear
			case didTapOwner
			case didTapAvatar
		}
		@CasePathable public enum Delegate { case doNothing }
		@CasePathable public enum Internal {
			case form(GearForm.Action)
			case destination(PresentationAction<Destination.Action>)
		}

		case view(View)
		case delegate(Delegate)
		case `internal`(Internal)
		case binding(BindingAction<State>)
	}

	@Reducer
	public struct Destination: Reducer {
		public enum State: Equatable {
			case bowlerPicker(ResourcePicker<Bowler.Summary, AlwaysEqual<Void>>.State)
			case avatar(AvatarEditor.State)
		}

		public enum Action {
			case bowlerPicker(ResourcePicker<Bowler.Summary, AlwaysEqual<Void>>.Action)
			case avatar(AvatarEditor.Action)
		}

		@Dependency(\.bowlers) var bowlers

		public var body: some ReducerOf<Self> {
			Scope(state: \.bowlerPicker, action: \.bowlerPicker) {
				ResourcePicker { _ in bowlers.pickable() }
			}
			Scope(state: \.avatar, action: \.avatar) {
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
		BindingReducer()

		Scope(state: \.form, action: \.internal.form) {
			GearForm()
				.dependency(\.records, .init(
					create: gear.create,
					update: gear.update,
					delete: gear.delete,
					archive: { _ in }
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

				case .onAppear:
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

					case .didFinishCreating, .didFinishUpdating, .didFinishDeleting, .didDiscard, .didArchive, .didFinishArchiving:
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
						.destination(.presented(.avatar(.view))),
						.destination(.presented(.avatar(.internal))),
						.destination(.presented(.avatar(.binding))):
					return .none
				}

			case .delegate, .binding:
				return .none
			}
		}
		.ifLet(\.$destination, action: \.internal.destination) {
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

		BreadcrumbReducer<State, Action> { _, action in
			switch action {
			case .view(.onAppear): return .navigationBreadcrumb(type(of: self))
			default: return nil
			}
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
	public var isArchivable: Bool { false }
	public var isSaveable: Bool {
		!name.isEmpty
	}
}
