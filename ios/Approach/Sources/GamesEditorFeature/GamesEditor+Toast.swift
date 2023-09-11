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
	public enum EditorSelectionChange: Equatable {
		case didChangeRollIndex(from: Int, to: Int)
		case didChangeFrameIndex(from: Int, to: Int)
		case didChangeGameIndex(from: Int, to: Int)
		case didChangeBowler(from: String, to: String)
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
		self.toast = .init(
			content: .hud(.init(
				message: .init(Strings.Game.Editor.locked),
				icon: .lockSlash
			)),
			style: .error
		)
		return .none
	}

	mutating func presentToast(
			forSelectionChanges changes: [GamesEditor.EditorSelectionChange]
		) -> Effect<GamesEditor.Action> {
			guard !changes.isEmpty else { return .none }

			let items: [StackedNotificationContent.Item] = changes.map {
				switch $0 {
				case let .didChangeBowler(_, to):
					return .init(message: .init(to), icon: .figureBowling)
				case let .didChangeGameIndex(_, to):
					return .init(message: .init(Strings.Game.titleWithOrdinal(to + 1)), icon: .listBullet)
				case let .didChangeFrameIndex(_, to):
					return .init(message: .init(Strings.Frame.title(to + 1)), icon: .fSquare)
				case let .didChangeRollIndex(_, to):
					return .init(message: .init(Strings.Roll.title(to + 1)), icon: .poweroutletTypeH)
				}
			}

			// TODO: update state to reflect changes

			return .none
		}
}
