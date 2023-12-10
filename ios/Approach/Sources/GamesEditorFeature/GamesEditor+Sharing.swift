import ComposableArchitecture
import SharingFeature

extension GamesEditor {
	func reduce(into state: inout State, sharingAction: Sharing.Action) -> Effect<Action> {
		switch sharingAction {
		case .delegate(.doNothing):
			return .none

		case .view, .internal:
			return .none
		}
	}
}
