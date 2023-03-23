import AvatarEditorFeature
import BaseFormLibrary
import ComposableArchitecture
import FeatureActionLibrary
import FeatureFlagsLibrary
import FeatureFlagsServiceInterface
import Foundation
import PersistenceServiceInterface
import SharedModelsLibrary
import StringsLibrary

extension Bowler: BaseFormModel {
	static public var modelName = Strings.Bowler.title
}

public struct BowlerEditor: Reducer {
	public typealias Form = BaseForm<Bowler, Fields>

	public struct Fields: BaseFormState, Equatable {
		@BindingState public var name = ""
		public var avatar: Avatar = .text("", .red())

		public let isDeleteable = true
		public var isSaveable: Bool {
			!name.isEmpty
		}

		var avatarEditor: AvatarEditor.State {
			get {
				.init(name: name, avatar: avatar)
			}
			set {
				self.avatar = newValue.avatar
			}
		}
	}

	public struct State: Equatable {
		public var base: Form.State
		public var isAvatarEditorPresented = false
		public let hasAvatarsEnabled: Bool

		public init(mode: Form.Mode) {
			var fields = Fields()
			if case let .edit(bowler) = mode {
				fields.name = bowler.name
				fields.avatar = bowler.avatar
			}

			self.base = .init(mode: mode, form: fields)

			@Dependency(\.featureFlags) var featureFlags: FeatureFlagsService
			self.hasAvatarsEnabled = featureFlags.isEnabled(.avatars)
		}
	}

	public enum Action: FeatureAction, BindableAction, Equatable {
		public enum ViewAction: Equatable {
			case didTapAvatar
			case setAvatarEditorPresented(isPresented: Bool)
		}
		public enum DelegateAction: Equatable {
			case didFinishEditing
		}
		public enum InternalAction: Equatable {
			case form(Form.Action)
			case avatar(AvatarEditor.Action)
		}

		case view(ViewAction)
		case delegate(DelegateAction)
		case `internal`(InternalAction)
		case binding(BindingAction<State>)
	}

	public init() {}

	@Dependency(\.uuid) var uuid
	@Dependency(\.persistenceService) var persistenceService

	public var body: some Reducer<State, Action> {
		BindingReducer()

		Scope(state: \.base, action: /Action.internal..Action.InternalAction.form) {
			BaseForm()
				.dependency(\.modelPersistence, .init(
					save: persistenceService.saveBowler,
					delete: persistenceService.deleteBowler
				))
		}

		Scope(state: \.base.form.avatarEditor, action: /Action.internal..Action.InternalAction.avatar) {
			AvatarEditor()
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

				case let .avatar(.delegate(delegateAction)):
					switch delegateAction {
					case .didFinishEditing:
						state.isAvatarEditorPresented = false
						return .none
					}

				case .avatar(.view), .avatar(.internal):
					return .none

				case .form(.view), .form(.internal), .form(.callback):
					return .none
				}

			case let .view(viewAction):
				switch viewAction {
				case .didTapAvatar:
					state.isAvatarEditorPresented = true
					return .none

				case .setAvatarEditorPresented(let isPresented):
					state.isAvatarEditorPresented = isPresented
					return .none
				}

			case .binding, .delegate:
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
			name: name,
			avatar: avatar
		)
	}
}
