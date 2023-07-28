import AnalyticsServiceInterface
import ComposableArchitecture
import ConstantsLibrary
import FeatureActionLibrary
import MessageUI
import StringsLibrary

public struct HelpSettings: Reducer {
	public struct State: Equatable {
		@BindingState public var isShowingBugReportEmail: Bool = false
		@BindingState public var isShowingSendFeedbackEmail: Bool = false

		init() {}
	}

	public enum Action: FeatureAction, Equatable {
		public enum ViewAction: BindableAction, Equatable {
			case didTapReportBugButton
			case didTapSendFeedbackButton
			case didShowAcknowledgements
			case didShowDeveloperDetails
			case didTapViewSource
			case binding(BindingAction<State>)
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
		BindingReducer(action: /Action.view)

		Reduce<State, Action> { state, action in
			switch action {
			case let .view(viewAction):
				switch viewAction {
				case .didTapReportBugButton:
					if MFMailComposeViewController.canSendMail() {
						state.isShowingBugReportEmail = true
					} else {
						guard let mailto = URL(string: "mailto://\(Strings.Settings.Help.ReportBug.email)") else { return .none }
						return .run { _ in await openURL(mailto) }
					}
					return .run { _ in await analytics.trackEvent(Analytics.Settings.ReportedBug()) }

				case .didTapSendFeedbackButton:
					if MFMailComposeViewController.canSendMail() {
						state.isShowingSendFeedbackEmail = true
					} else {
						guard let mailto = URL(string: "mailto://\(Strings.Settings.Help.SendFeedback.email)") else { return .none }
						return .run { _ in await openURL(mailto) }
					}

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

				case .binding:
					return .none
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
