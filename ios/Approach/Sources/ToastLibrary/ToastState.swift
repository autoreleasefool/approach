import AlertToast
import AssetsLibrary
import ComposableArchitecture
import SwiftUI

public protocol ToastableAction {
	static var didDismiss: Self { get }
	static var didFinishDismissing: Self { get }
}

public struct ToastState<Action: ToastableAction>: Identifiable {
	public let id: UUID
	public let content: Content
	public let style: ToastStyle

	public init(
		id: UUID = UUID(),
		content: Content,
		style: ToastStyle = .primary
	) {
		self.id = id
		self.content = content
		self.style = style
	}
}

extension ToastState {
	public struct Button {
		public let title: String
		public let action: Action

		public init(title: String, action: Action) {
			self.title = title
			self.action = action
		}
	}
}

public struct ToastStyle: Equatable {
	public let id: ID
	public let foreground: ColorAsset
	public let background: ColorAsset

	public static let primary = Self(
		id: .primary,
		foreground: Asset.Colors.Text.onPrimary,
		background: Asset.Colors.Primary.default
	)

	public static let error = Self(
		id: .error,
		foreground: Asset.Colors.Text.onError,
		background: Asset.Colors.Error.default
	)

	public static let success = Self(
		id: .success,
		foreground: Asset.Colors.Text.onSuccess,
		background: Asset.Colors.Success.default
	)

	public static func == (lhs: Self, rhs: Self) -> Bool {
		lhs.id == rhs.id
	}

	var alertStyle: AlertToast.AlertStyle {
		.style(
			backgroundColor: background.swiftUIColor,
			titleColor: foreground.swiftUIColor,
			subTitleColor: foreground.swiftUIColor,
			titleFont: nil,
			subTitleFont: nil
		)
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
		case hud(HUDContent)
		case toast(SnackContent<Action>)
	}
}

public struct HUDContent: Equatable {
	public let message: String
	public let icon: SFSymbol?

	public init(message: String, icon: SFSymbol?) {
		self.message = message
		self.icon = icon
	}
}

public struct SnackContent<Action: ToastableAction> {
	public let message: String
	public let icon: SFSymbol?
	public let button: ToastState<Action>.Button?

	public init(message: String, icon: SFSymbol? = nil, button: ToastState<Action>.Button? = nil) {
		self.message = message
		self.icon = icon
		self.button = button
	}
}

extension ToastState: Equatable where Action: Equatable {}
extension ToastState.Button: Equatable where Action: Equatable {}
extension ToastState.Content: Equatable where Action: Equatable {}
extension SnackContent: Equatable where Action: Equatable {}

extension View {
	public func toast<Action>(
		_ item: Binding<Store<ToastState<Action>, Action>?>
	) -> some View where Action: Equatable {
		let store = item.wrappedValue
		let toastState = store?.withState { $0 }

		return self.toast(
			isPresenting: item.isPresent(),
			alert: {
				switch toastState?.content {
				case .none:
					return AlertToast(displayMode: .banner(.slide), type: .regular)
				case let .hud(content):
					return AlertToast(
						displayMode: .alert,
						type: .regular,
						title: content.message,
						style: toastState?.style.alertStyle
					)
				case let .toast(content):
					let type: AlertToast.AlertType
					if let icon = content.icon {
						type = .systemImage(
							icon.rawValue,
							toastState?.style.foreground.swiftUIColor ?? .black
						)
					} else {
						type = .regular
					}

					return AlertToast(
						displayMode: .banner(.pop),
						type: type,
						title: content.message,
						subTitle: content.button?.title,
						style: toastState?.style.alertStyle
					)
				}
			},
			onTap: {
				switch toastState?.content {
				case .none, .hud:
					break
				case .toast(let content):
					if let action = content.button?.action {
						store?.send(action)
					}
				}

				store?.send(.didDismiss)
			},
			completion: {
				store?.send(.didFinishDismissing)
			}
		)
	}
}
