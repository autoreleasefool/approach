import AssetsLibrary
import ComposableArchitecture
import PopupView
import SwiftUI

public protocol ToastableAction {
	static var didDismiss: Self { get }
	static var didFinishDismissing: Self { get }
}

public struct ToastState<Action: ToastableAction> {
	public let message: TextState
	public let icon: SFSymbol?
	public let button: Button?
	public let style: Style
	public let appearance: Appearance

	public init(
		message: TextState,
		icon: SFSymbol? = nil,
		button: Button? = nil,
		style: Style = .primary,
		appearance: Appearance = .toast
	) {
		self.button = button
		self.icon = icon
		self.message = message
		self.style = style
		self.appearance = appearance
	}
}

extension ToastState {
	public struct Button {
		public let title: TextState
		public let action: ToastAction

		public init(title: TextState, action: ToastAction) {
			self.title = title
			self.action = action
		}
	}
}

extension ToastState {
	public struct ToastAction {
		public let action: Action
		fileprivate let animation: Animation

		fileprivate enum Animation: Equatable {
			case inherited
			case explicit(SwiftUI.Animation?)
		}

		public init(action: Action, animation: SwiftUI.Animation?) {
			self.action = action
			self.animation = .explicit(animation)
		}

		public init(action: Action) {
			self.action = action
			self.animation = .inherited
		}
	}
}

extension ToastState {
	public enum Style: Equatable {
		case primary
		case error
		case success

		var foregroundColor: Color {
			switch self {
			case .primary: return Asset.Colors.Primary.default.swiftUIColor
			case .error: return .white
			case .success: return .black
			}
		}

		var backgroundColor: Color {
			switch self {
			case .primary: return Asset.Colors.Primary.light.swiftUIColor
			case .error: return Asset.Colors.Error.default.swiftUIColor
			case .success: return Asset.Colors.Success.default.swiftUIColor
			}
		}
	}
}

extension ToastState {
	public enum Appearance: Equatable {
		case toast
		case hud
	}
}

extension ToastState: Equatable where Action: Equatable {}
extension ToastState.Button: Equatable where Action: Equatable {}
extension ToastState.ToastAction: Equatable where Action: Equatable {}

extension View {
	public func toast<Action>(
		store: Store<ToastState<Action>?, Action>
	) -> some View where Action: Equatable {
		WithViewStore(store, observe: { $0 }, content: { viewStore in
			self.popup(
				item: Binding(
					get: {
						viewStore.state
					},
					set: { state, transaction in
						withAnimation(transaction.disablesAnimations ? nil : transaction.animation) {
							if state == nil {
								store.send(.didDismiss)
							}
						}
					}
				),
				itemView: { toast in
					BaseToastView(toast: toast, viewStore: viewStore)
				},
				customize: {
					let parameters = $0
						.autohideIn(1)
						.animation(.spring())
						.dismissCallback({ viewStore.send(.didFinishDismissing) })

					switch viewStore.state?.appearance {
					case .toast:
						return parameters
							.type(.floater())
							.position(.bottom)
							.autohideIn(2)
					case .hud:
						return parameters
							.isOpaque(true)
							.closeOnTap(true)
							.closeOnTapOutside(true)
					case .none:
						return parameters
					}
				}
			)
		})
	}
}
