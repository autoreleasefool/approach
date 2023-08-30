import ComposableArchitecture
import SharingFeature

extension GamesEditor {
	func reduce(into state: inout State, sharingAction: Sharing.Action) -> Effect<Action> {
		switch sharingAction {
		case let .delegate(delegateAction):
			switch delegateAction {
			case .never:
				return .none
			}

		case .view, .internal:
			return .none
		}
	}
}
