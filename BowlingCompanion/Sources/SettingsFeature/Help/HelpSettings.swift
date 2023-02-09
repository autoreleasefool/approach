import ComposableArchitecture
import FeatureActionLibrary

public struct HelpSettings: ReducerProtocol {
	public struct State: Equatable {
		init() {}
	}

	public enum Action: FeatureAction, Equatable {
		public enum ViewAction: Equatable {
			case didTapReportBugButton
			case didTapSendFeedbackButton
		}
		public enum DelegateAction: Equatable {}
		public enum InternalAction: Equatable {}

		case view(ViewAction)
		case delegate(DelegateAction)
		case `internal`(InternalAction)
	}

	public var body: some ReducerProtocol<State, Action> {
		Reduce { _, action in
			switch action {
			case let .view(viewAction):
				switch viewAction {
				case .didTapReportBugButton:
					// TODO: send bug report email
					return .none

				case .didTapSendFeedbackButton:
					// TODO: send feedback email
					return .none
				}

			case .internal, .delegate:
				return .none
			}
		}
	}
}
