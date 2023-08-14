import AssetsLibrary
import ComposableArchitecture
import PopupView
import SwiftUI

public protocol ToastableAction {
	static var didDismiss: Self { get }
}

public struct ToastState<Action: ToastableAction> {
	public var message: TextState
	public var icon: SFSymbol?
	public var button: Button?
	public var style: Style

	public init(
		message: TextState,
		icon: SFSymbol? = nil,
		button: Button? = nil,
		style: Style = .primary
	) {
		self.button = button
		self.icon = icon
		self.message = message
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

extension ToastState {
	public enum Style: Equatable {
		case primary
		case error

		var foregroundColor: Color {
			switch self {
			case .primary: return Asset.Colors.Primary.default.swiftUIColor
			case .error: return .white
			}
		}

		var backgroundColor: Color {
			switch self {
			case .primary: return Asset.Colors.Primary.light.swiftUIColor
			case .error: return Asset.Colors.Error.default.swiftUIColor
			}
		}
	}
}

extension ToastState: Equatable where Action: Equatable {}
extension ToastState.Button: Equatable where Action: Equatable {}
extension ToastState.ToastAction: Equatable where Action: Equatable {}

extension View {
	public func toast<Action>(
		_ store: Store<ToastState<Action>?, Action>
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
					HStack(alignment: .center, spacing: .smallSpacing) {
						if let icon = toast.icon {
							Image(systemSymbol: icon)
								.resizable()
								.scaledToFit()
								.frame(width: .extraTinyIcon, height: .extraTinyIcon)
						}

						Text(toast.message)

						if let button = toast.button {
							Text(button.title)
								.font(.caption)
								.textCase(.uppercase)
								.padding(.smallSpacing)
								.background(
									Material.ultraThinMaterial,
									in: RoundedRectangle(cornerRadius: .smallRadius)
								)
						}
					}
					.padding(.horizontal, .standardSpacing)
					.padding(.vertical, .smallSpacing)
					.foregroundColor(toast.style.foregroundColor)
					.background(
						RoundedRectangle(cornerRadius: .standardRadius)
							.fill(toast.style.backgroundColor)
					)
					.padding(.horizontal, .standardSpacing)
					.padding(.bottom, .standardSpacing)
				},
				customize: {
					$0
						.type(.floater())
						.position(.bottom)
						.animation(.spring())
						.closeOnTapOutside(true)
						.autohideIn(2)
				}
			)
		})
	}
}
