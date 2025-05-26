import AssetsLibrary
import ComposableArchitecture
import FeatureActionLibrary
import ModelsLibrary
import StringsLibrary
import SwiftUI

@Reducer
public struct TextAvatarEditor: Reducer, Sendable {
	@ObservableState
	public struct State: Equatable {
		public var label: String
		public var backgroundColor: Color
		public var secondaryBackgroundColor: Color
		public var backgroundStyle: AvatarBackgroundStyle

		var value: Avatar.Value {
			.text(label, background)
		}

		var background: Avatar.Background {
			switch backgroundStyle {
			case .solid: return .rgb(backgroundColor.rgb)
			case .gradient: return .gradient(backgroundColor.rgb, secondaryBackgroundColor.rgb)
			}
		}

		public init(avatar: Avatar.Summary?) {
			self.backgroundColor = Asset.Colors.Primary.default.swiftUIColor
			self.secondaryBackgroundColor = Asset.Colors.Primary.light.swiftUIColor
			switch avatar?.value {
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
	}

	public enum Action: FeatureAction, ViewAction, BindableAction {
		@CasePathable
		public enum View {
			case didTapRandomColorButton
			case didTapSwapColorsButton
		}
		@CasePathable
		public enum Delegate { case doNothing }
		@CasePathable
		public enum Internal { case doNothing }

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
			case .solid: Strings.Avatar.Editor.Properties.BackgroundColor.Style.solid
			case .gradient: Strings.Avatar.Editor.Properties.BackgroundColor.Style.gradient
			}
		}
	}

	public init() {}

	public var body: some ReducerOf<Self> {
		BindingReducer()

		Reduce<State, Action> { state, action in
			switch action {
			case let .view(viewAction):
				switch viewAction {
				case .didTapSwapColorsButton:
					let tempColor = state.backgroundColor
					state.backgroundColor = state.secondaryBackgroundColor
					state.secondaryBackgroundColor = tempColor
					return .none

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
	}
}

@ViewAction(for: TextAvatarEditor.self)
public struct TextAvatarEditorView: View {
	@Bindable public var store: StoreOf<TextAvatarEditor>

	public init(store: StoreOf<TextAvatarEditor>) {
		self.store = store
	}

	public var body: some View {
		Section(Strings.Avatar.Editor.Properties.Label.title) {
			TextField(Strings.Avatar.Editor.Properties.Label.title, text: $store.label)
		}

		Section(Strings.Avatar.Editor.Properties.BackgroundColor.title) {
			Picker(
				Strings.Avatar.Editor.Properties.BackgroundColor.Style.title,
				selection: $store.backgroundStyle
			) {
				ForEach(TextAvatarEditor.AvatarBackgroundStyle.allCases, id: \.rawValue) {
					Text(String(describing: $0)).tag($0)
				}
			}
			.pickerStyle(.segmented)

			colorPicker

			Button { send(.didTapRandomColorButton) } label: {
				Text(Strings.Avatar.Editor.Properties.BackgroundColor.randomColor)
					.frame(maxWidth: .infinity, alignment: .center)
			}
			.buttonStyle(.borderless)
		}
		.listRowSeparator(.hidden)
	}

	@ViewBuilder private var colorPicker: some View {
		HStack(spacing: .standardSpacing) {
			ColorPicker(
				Strings.Avatar.Editor.Properties.BackgroundColor.backgroundColor,
				selection: $store.backgroundColor
			)
			.labelsHidden()

			switch store.backgroundStyle {
			case .solid:
				Spacer()
					.frame(height: .largeSpacing)
					.background(store.backgroundColor)
					.clipShape(RoundedRectangle(cornerRadius: .standardRadius))
			case .gradient:
				Spacer()
					.frame(height: .largeSpacing)
					.background(LinearGradient(
						colors: [
							store.backgroundColor,
							store.secondaryBackgroundColor,
						],
						startPoint: .leading,
						endPoint: .trailing
					))
					.clipShape(RoundedRectangle(cornerRadius: .standardRadius))

				ColorPicker(
					Strings.Avatar.Editor.Properties.BackgroundColor.secondaryColor,
					selection: $store.secondaryBackgroundColor
				)
				.labelsHidden()
			}
		}
	}
}
