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

	static func strikeOut(withFinalScore: Int) -> Self {
		ToastState(
			content: .toast(SnackContent(
				message: Strings.Game.Editor.Fields.StrikeOut.ifYouStrikeOut(withFinalScore),
				icon: .infoCircle
			)),
			duration: 4.0,
			style: .primary
		)
	}
}

extension GamesEditor.State {
	mutating func presentLockedAlert() -> Effect<GamesEditor.Action> {
		toast = .locked
		return .none
	}

	mutating func presentStrikeOutAlert(withFinalScore: Int) -> Effect<GamesEditor.Action> {
		toast = .strikeOut(withFinalScore: withFinalScore)
		return .none
	}
}
