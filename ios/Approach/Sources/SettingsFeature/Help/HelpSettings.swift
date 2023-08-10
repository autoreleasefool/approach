import AnalyticsServiceInterface
import ComposableArchitecture
import ConstantsLibrary
import FeatureActionLibrary
import MessageUI
import StringsLibrary
import SwiftUI
import ViewsLibrary

public struct HelpSettings: Reducer {
	public struct State: Equatable {
		@BindingState public var isShowingBugReportEmail: Bool = false
		@BindingState public var isShowingSendFeedbackEmail: Bool = false

		@PresentationState public var analytics: AnalyticsSettings.State?

		init() {}
	}

	public enum Action: FeatureAction, Equatable {
		public enum ViewAction: BindableAction, Equatable {
			case didTapReportBugButton
			case didTapSendFeedbackButton
			case didShowAcknowledgements
			case didTapAnalyticsButton
			case didShowDeveloperDetails
			case didTapViewSource
			case binding(BindingAction<State>)
		}
		public enum DelegateAction: Equatable {}
		public enum InternalAction: Equatable {
			case analytics(PresentationAction<AnalyticsSettings.Action>)
		}

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

				case .didTapAnalyticsButton:
					state.analytics = .init()
					return .none

				case .binding:
					return .none
				}

			case let .internal(internalAction):
				switch internalAction {
				case let .analytics(.presented(.delegate(delegateAction))):
					switch delegateAction {
					case .never:
						return .none
					}

				case .analytics(.dismiss), .analytics(.presented(.internal)), .analytics(.presented(.view)):
					return .none
				}

			case .delegate:
				return .none
			}
		}
		.ifLet(\.$analytics, action: /Action.internal..Action.InternalAction.analytics) {
			AnalyticsSettings()
		}
	}
}

public struct HelpSettingsView: View {
	let store: StoreOf<HelpSettings>

	struct ViewState: Equatable {
		@BindingState var isShowingBugReportEmail: Bool
		@BindingState var isShowingSendFeedbackEmail: Bool
	}

	public var body: some View {
		WithViewStore(store, observe: { $0 }, send: { .view($0) }, content: { viewStore in
			Section(Strings.Settings.Help.title) {
				Button(Strings.Settings.Help.reportBug) { viewStore.send(.didTapReportBugButton) }
				Button(Strings.Settings.Help.sendFeedback) { viewStore.send(.didTapSendFeedbackButton) }
				NavigationLink(
					Strings.Settings.Help.acknowledgements,
					destination: AcknowledgementsView()
						.onAppear { viewStore.send(.didShowAcknowledgements) }
				)
				Button(Strings.Settings.Analytics.title) { viewStore.send(.didTapAnalyticsButton) }
					.buttonStyle(.navigation)
			}

			Section {
				NavigationLink(
					Strings.Settings.Help.developer,
					destination: DeveloperDetailsView()
						.onAppear { viewStore.send(.didShowDeveloperDetails) }
				)
				Button(Strings.Settings.Help.viewSource) { viewStore.send(.didTapViewSource) }
				// TODO: enable tip jar
//				NavigationLink("Tip Jar", destination: TipJarView())
			} header: {
				Text(Strings.Settings.Help.Development.title)
			} footer: {
				Text(Strings.Settings.Help.Development.help(AppConstants.appName))
			}
			.sheet(isPresented: viewStore.$isShowingBugReportEmail) {
				EmailView(
					content: .init(
						recipients: [Strings.Settings.Help.ReportBug.email],
						subject: Strings.Settings.Help.ReportBug.subject(
							Strings.Settings.AppInfo.appVersion(Bundle.main.appVersionLong, Bundle.main.appBuild)
						)
					)
				)
			}
			.sheet(isPresented: viewStore.$isShowingSendFeedbackEmail) {
				EmailView(
					content: .init(
						recipients: [Strings.Settings.Help.SendFeedback.email]
					)
				)
			}
			.navigationDestination(store: store.scope(state: \.$analytics, action: { .internal(.analytics($0)) })) {
				AnalyticsSettingsView(store: $0)
			}
		})
	}
}

extension HelpSettingsView.ViewState {
	init(store: BindingViewStore<HelpSettings.State>) {
		self._isShowingBugReportEmail = store.$isShowingBugReportEmail
		self._isShowingSendFeedbackEmail = store.$isShowingSendFeedbackEmail
	}
}
