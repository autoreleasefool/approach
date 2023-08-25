import ComposableArchitecture
import SwiftUI

public struct BaseToastView<Action: ToastableAction>: View {
	let toast: ToastState<Action>
	let viewStore: ViewStore<ToastState<Action>?, Action>

	public var body: some View {
		switch toast.appearance {
		case .hud:
			HUDView(toast: toast, viewStore: viewStore)
		case .toast:
			ToastView(toast: toast, viewStore: viewStore)
		}
	}
}
