import ComposableArchitecture
import SwiftUI

public struct BaseToastView<Action: ToastableAction>: View {
	let toast: ToastState<Action>
	let store: Store<ToastState<Action>, Action>?

	public var body: some View {
		switch toast.content {
		case let .hud(content):
			HUDView(content: content, style: toast.style)
		case let .toast(content):
			ToastView(content: content, style: toast.style) {
				if let button = content.button {
					store?.send(button.action.action)
				}
			}
		case let .stackedNotification(content):
			StackedNotificationView(content: content, style: toast.style)
		}
	}
}
