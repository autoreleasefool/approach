import ComposableArchitecture
import StringsLibrary
import ToastLibrary

extension GamesEditor {
	public enum ToastAction: Equatable, ToastableAction {
		case didDismiss
		case didFinishDismissing
	}

	func reduce(
		into state: inout State,
		toastAction: ToastAction
	) -> Effect<Action> {
		switch toastAction {
		case .didDismiss:
			state.toast = nil
			return .none

		case .didFinishDismissing:
			state.toast = nil
			return .none
		}
	}
}

extension ToastState where Action == GamesEditor.ToastAction {
	static var locked: Self {
		ToastState(
			content: .hud(HUDContent(message: Strings.Game.Editor.locked, icon: .lock)),
			style: .error
		)
	}
}
