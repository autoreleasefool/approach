import ComposableArchitecture
import StringsLibrary
import ToastLibrary

extension GamesEditor {
	public enum ToastAction: ToastableAction, Equatable {
		case didDismiss
		case didFinishDismissing
	}
}

extension GamesEditor {
	func reduce(into state: inout State, toastAction: ToastAction) -> Effect<Action> {
		switch toastAction {
		case .didDismiss:
			state.toast = nil
			return .none

		case .didFinishDismissing:
			return .none
		}
	}
}

extension GamesEditor.State {
	mutating func presentLockedToast() -> Effect<GamesEditor.Action> {
		self.toast = .init(message: .init(Strings.Game.Editor.locked), icon: .lockSlash, style: .error, appearance: .hud)
		return .none
	}
}
