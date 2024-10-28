import ComposableArchitecture
import SharingFeature

extension GamesEditor {
	func reduce(into _: inout State, sharingAction: Sharing.Action) -> Effect<Action> {
		switch sharingAction {
		case .delegate(.doNothing):
			return .none

		case .view, .internal, .binding:
			return .none
		}
	}
}
