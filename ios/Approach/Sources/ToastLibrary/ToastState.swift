import AssetsLibrary
import ComposableArchitecture
import PopupView
import SwiftUI

public protocol ToastableAction {
	static var didDismiss: Self { get }
	static var didFinishDismissing: Self { get }
}

public struct ToastState<Action: ToastableAction> {
	public let content: Content
	public let style: ToastStyle

	public init(
		content: Content,
		style: ToastStyle = .primary
	) {
		self.content = content
		self.style = style
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

public struct ToastStyle: Equatable {
	public let id: ID
	public let foreground: ColorAsset
	public let background: ColorAsset
	public let border: ColorAsset

	public static let primary = Self(
		id: .primary,
		foreground: Asset.Colors.Text.onPrimary,
		background: Asset.Colors.Primary.default,
		border: Asset.Colors.Background.default
	)

	public static let error = Self(
		id: .error,
		foreground: Asset.Colors.Text.onError,
		background: Asset.Colors.Error.default,
		border: Asset.Colors.Background.default
	)

	public static let success = Self(
		id: .success,
		foreground: Asset.Colors.Text.onSuccess,
		background: Asset.Colors.Success.default,
		border: Asset.Colors.Background.default
	)

	public static func == (lhs: Self, rhs: Self) -> Bool {
		lhs.id == rhs.id
	}
}

extension ToastStyle {
	public enum ID {
		case primary
		case error
		case success
	}
}

extension ToastState {
	public enum Content {
		case toast(ToastContent<Action>)
		case hud(HUDContent)
		case stackedNotification(StackedNotificationContent)
	}
}

extension ToastState: Equatable where Action: Equatable {}
extension ToastState.Button: Equatable where Action: Equatable {}
extension ToastState.ToastAction: Equatable where Action: Equatable {}
extension ToastState.Content: Equatable where Action: Equatable {}

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

					switch viewStore.state?.content {
					case .stackedNotification:
						return parameters
							.type(.floater())
							.closeOnTap(true)
					case .toast:
						return parameters
							.position(.bottom)
							.type(.floater())
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
