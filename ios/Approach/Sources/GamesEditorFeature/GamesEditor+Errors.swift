import ComposableArchitecture
import ErrorsFeature

extension GamesEditor {
	func reduce(
		into state: inout State,
		errorsAction: Errors<ErrorID>.Action
	) -> Effect<Action> {
		switch errorsAction {
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
