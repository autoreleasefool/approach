import AnalyticsServiceInterface
import AssetsLibrary
import ComposableArchitecture
import ExtensionsLibrary
import FeatureActionLibrary
import ModelsLibrary
import StringsLibrary
import SwiftUI

@Reducer
public struct AvatarEditor: Reducer {
	@ObservableState
	public struct State: Equatable {
		public let id: UUID
		public let initialAvatar: Avatar.Summary?

		public var label: String
		public var backgroundColor: Color
		public var secondaryBackgroundColor: Color
		public var backgroundStyle: AvatarBackgroundStyle

		public init(avatar: Avatar.Summary?) {
			@Dependency(\.uuid) var uuid
			self.id = avatar?.id ?? uuid()
			self.initialAvatar = avatar

			self.backgroundColor = Asset.Colors.Primary.default.swiftUIColor
			self.secondaryBackgroundColor = Asset.Colors.Primary.light.swiftUIColor
			switch initialAvatar?.value {
			case .data, .url, .none:
				self.label = ""
				self.backgroundStyle = .solid
			case let .text(label, background):
				self.label = label
				switch background {
				case let .rgb(solid):
					self.backgroundColor = solid.color
					self.backgroundStyle = .solid
				case let .gradient(first, second):
					self.backgroundColor = first.color
					self.secondaryBackgroundColor = second.color
					self.backgroundStyle = .gradient
				}
			}
		}

		var avatar: Avatar.Summary {
			.init(id: id, value: .text(label, avatarBackground))
		}

		var hasChanges: Bool {
			initialAvatar?.value != avatar.value
		}

		var avatarBackground: Avatar.Background {
			switch backgroundStyle {
			case .solid: return .rgb(backgroundColor.rgb)
			case .gradient: return .gradient(backgroundColor.rgb, secondaryBackgroundColor.rgb)
			}
		}
	}

	public enum Action: FeatureAction, ViewAction, BindableAction {
		@CasePathable public enum View {
			case onAppear
			case didTapCancel
			case didTapDone
			case didTapRandomColorButton
			case didTapSwapColorsButton
		}
		@CasePathable public enum Delegate {
			case didFinishEditing(Avatar.Summary?)
		}
		@CasePathable public enum Internal { case doNothing }

		case view(View)
		case delegate(Delegate)
		case `internal`(Internal)
		case binding(BindingAction<State>)
	}

	public enum AvatarBackgroundStyle: Int, CaseIterable, CustomStringConvertible {
		case solid
		case gradient

		public var description: String {
			switch self {
			case .solid: return Strings.Avatar.Editor.Properties.BackgroundColor.Style.solid
			case .gradient: return Strings.Avatar.Editor.Properties.BackgroundColor.Style.gradient
			}
		}
	}

	public init() {}

	@Dependency(\.dismiss) var dismiss
	@Dependency(\.uuid) var uuid

	public var body: some ReducerOf<Self> {
		BindingReducer()

		Reduce<State, Action> { state, action in
			switch action {
			case let .view(viewAction):
				switch viewAction {
				case .onAppear:
					return .none

				case .didTapSwapColorsButton:
					let tempColor = state.backgroundColor
					state.backgroundColor = state.secondaryBackgroundColor
					state.secondaryBackgroundColor = tempColor
					return .none

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
					state.secondaryBackgroundColor = Color(uiColor: .random)
					return .none
				}

			case .internal(.doNothing):
				return .none

			case .delegate, .binding:
				return .none
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

extension Color {
	var rgb: Avatar.Background.RGB {
		let (red, green, blue, _) = UIColor(self).rgba
		return .init(red, green, blue)
	}
}
