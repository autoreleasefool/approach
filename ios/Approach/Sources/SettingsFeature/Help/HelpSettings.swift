import AnalyticsServiceInterface
import ComposableArchitecture
import ConstantsLibrary
import FeatureActionLibrary

public struct HelpSettings: Reducer {
	public struct State: Equatable {
		init() {}
	}

	public enum Action: FeatureAction, Equatable {
		public enum ViewAction: Equatable {
			case didTapReportBugButton
			case didTapSendFeedbackButton
			case didShowAcknowledgements
			case didShowDeveloperDetails
			case didTapViewSource
		}
		public enum DelegateAction: Equatable {}
		public enum InternalAction: Equatable {}

		case view(ViewAction)
		case delegate(DelegateAction)
		case `internal`(InternalAction)
	}

	@Dependency(\.analytics) var analytics
	@Dependency(\.openURL) var openURL

	public var body: some ReducerOf<Self> {
		Reduce<State, Action> { _, action in
			switch action {
			case let .view(viewAction):
				switch viewAction {
				case .didTapReportBugButton:
					// TODO: send bug report email
					return .run { _ in await analytics.trackEvent(Analytics.Settings.ReportedBug()) }

				case .didTapSendFeedbackButton:
					// TODO: send feedback email
					return .run { _ in await analytics.trackEvent(Analytics.Settings.SentFeedback()) }

				case .didShowAcknowledgements:
					return .run { _ in await analytics.trackEvent(Analytics.Settings.ViewedAcknowledgements()) }

				case .didShowDeveloperDetails:
					return .run { _ in await analytics.trackEvent(Analytics.Settings.ViewedDeveloper()) }

				case .didTapViewSource:
					return .merge(
						.run { _ in await openURL(AppConstants.openSourceRepositoryUrl) },
						.run { _ in await analytics.trackEvent(Analytics.Settings.ViewedSource()) }
					)
				}

			case let .internal(internalAction):
				switch internalAction {
				case .never:
					return .none
				}

			case .delegate:
				return .none
			}
		}
	}
}
