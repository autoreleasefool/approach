import AssetsLibrary
import ComposableArchitecture
import ExtensionsLibrary
import FeatureActionLibrary
import ModelsLibrary
import SwiftUI

public struct AvatarEditor: Reducer {
	public struct State: Equatable {
		public let id: UUID
		public let initialAvatar: Avatar.Summary?

		@BindingState public var label: String
		@BindingState public var backgroundColor: Color

		public init(avatar: Avatar.Summary?) {
			@Dependency(\.uuid) var uuid
			self.id = avatar?.id ?? uuid()
			self.initialAvatar = avatar
			switch initialAvatar?.value {
			case .data, .url, .none:
				self.label = ""
				self.backgroundColor = Asset.Colors.Primary.default.swiftUIColor
			case let .text(label, background):
				self.label = label
				self.backgroundColor = Color(uiColor: background.uiColor)
			}
		}

		var avatar: Avatar.Summary {
			.init(id: id, value: .text(label, backgroundColor.avatarBackground))
		}

		var hasChanges: Bool {
			initialAvatar?.value != avatar.value
		}
	}

	public enum Action: FeatureAction, Equatable {
		public enum ViewAction: BindableAction, Equatable {
			case didTapCancel
			case didTapDone
			case didTapRandomColorButton
			case binding(BindingAction<State>)
		}
		public enum DelegateAction: Equatable {
			case didFinishEditing(Avatar.Summary?)
		}
		public enum InternalAction: Equatable {}

		case view(ViewAction)
		case delegate(DelegateAction)
		case `internal`(InternalAction)
	}

	public init() {}

	@Dependency(\.dismiss) var dismiss
	@Dependency(\.uuid) var uuid

	public var body: some ReducerOf<Self> {
		BindingReducer(action: /Action.view)

		Reduce<State, Action> { state, action in
			switch action {
			case let .view(viewAction):
				switch viewAction {
				case .didTapCancel:
					return .concatenate(
						.send(.delegate(.didFinishEditing(state.initialAvatar))),
						.run { _ in await dismiss() }
					)

				case .didTapDone:
					return .concatenate(
						.send(.delegate(.didFinishEditing(state.avatar))),
						.run { _ in await dismiss() }
					)

				case .didTapRandomColorButton:
					state.backgroundColor = Color(uiColor: .random)
					return .none

				case .binding:
					return .none
				}

			case let .internal(internalAction):
				switch internalAction {
				case .never:
					return .none
				}

			case .delegate:
				return .none
			}
		}
	}
}

extension Color {
	var avatarBackground: Avatar.Background {
		let (red, green, blue, _) = UIColor(self).rgba
		return .rgb(red, green, blue)
	}
}
