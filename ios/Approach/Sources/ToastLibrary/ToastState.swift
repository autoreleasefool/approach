import AssetsLibrary
import ComposableArchitecture
import SwiftUI
import ToastUI

public protocol ToastableAction {
	static var didDismiss: Self { get }
	static var didFinishDismissing: Self { get }
}

public struct ToastState<Action: ToastableAction>: Identifiable {
	public let id: UUID
	public let content: Content
	public let duration: Double
	public let isDimmedBackgroundEnabled: Bool
	public let style: ToastStyle

	public init(
		id: UUID = UUID(),
		content: Content,
		duration: Double = 3.0,
		isDimmedBackgroundEnabled: Bool = true,
		style: ToastStyle = .primary
	) {
		self.id = id
		self.content = content
		self.duration = duration
		self.isDimmedBackgroundEnabled = isDimmedBackgroundEnabled
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

public struct ToastStyle: Equatable, Sendable {
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
}

extension ToastStyle {
	public enum ID: Sendable {
		case primary
		case error
		case success
	}
}

extension ToastState {
	public enum Content {
		case toast(SnackContent<Action>)
		case hud(HUDContent<Action>)
		case badge(BadgeContent)
	}
}

public struct HUDContent<Action: ToastableAction> {
	public let title: String
	public let message: String?
	public let icon: SFSymbol?
	public let button: ToastState<Action>.Button?

	public init(title: String, message: String? = nil, icon: SFSymbol?, button: ToastState<Action>.Button? = nil) {
		self.title = title
		self.message = message
		self.icon = icon
		self.button = button
	}
}

public struct BadgeContent: Equatable {
	public let title: String

	public init(title: String) {
		self.title = title
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
extension HUDContent: Equatable where Action: Equatable {}

extension View {
	@ViewBuilder
	public func toast<Action>(
		_ item: Binding<Store<ToastState<Action>, Action>?>
	) -> some View where Action: Equatable {
		let store = item.wrappedValue
		let toastState = store?.withState { $0 }

		self.toast(
			isPresented: Binding(item),
			dismissAfter: toastState?.duration ?? 3.0,
			onDismiss: {
				store?.send(.didFinishDismissing)
			},
			content: {
				switch toastState?.content {
				case let .toast(content):
					ToastView(
						title: content.message,
						action: content.button?.title,
						style: toastState?.style ?? .primary
					) {
						guard let action = content.button?.action else { return }
						store?.send(action)
					} onDismiss: {
						store?.send(.didDismiss)
					}
				case let .hud(content):
					HUDView(
						title: content.title,
						message: content.message,
						action: content.button?.title,
						icon: content.icon,
						style: toastState?.style ?? .primary
					) {
						guard let action = content.button?.action else { return }
						store?.send(action)
					} onDismiss: {
						store?.send(.didDismiss)
					}
				case let .badge(content):
					BadgeView(
						title: content.title
					) {
						store?.send(.didDismiss)
					}
				case .none:
					EmptyView()
				}
			}
		)
		.toastDimmedBackground(toastState?.isDimmedBackgroundEnabled ?? true)
	}
}
