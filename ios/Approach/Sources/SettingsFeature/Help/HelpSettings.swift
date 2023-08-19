import AnalyticsServiceInterface
import ComposableArchitecture
import ConstantsLibrary
import EmailServiceInterface
import FeatureActionLibrary
import StringsLibrary
import SwiftUI
import SwiftUIExtensionsLibrary
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

	@Dependency(\.email) var email
	@Dependency(\.openURL) var openURL

	public var body: some ReducerOf<Self> {
		BindingReducer(action: /Action.view)

		Reduce<State, Action> { state, action in
			switch action {
			case let .view(viewAction):
				switch viewAction {
				case .didTapReportBugButton:
					return .run { send in
						if await email.canSendEmail() {
							await send(.view(.binding(.set(\.$isShowingBugReportEmail, true))))
						} else {
							guard let mailto = URL(string: "mailto://\(Strings.Settings.Help.ReportBug.email)") else { return }
							await openURL(mailto)
						}
					}

				case .didTapSendFeedbackButton:
					return .run { send in
						if await email.canSendEmail() {
							await send(.view(.binding(.set(\.$isShowingSendFeedbackEmail, true))))
						} else {
							guard let mailto = URL(string: "mailto://\(Strings.Settings.Help.SendFeedback.email)") else { return }
							await openURL(mailto)
						}
					}

				case .didShowAcknowledgements:
					return .none

				case .didShowDeveloperDetails:
					return .none

				case .didTapViewSource:
					return .run { _ in await openURL(AppConstants.openSourceRepositoryUrl) }

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

		AnalyticsReducer<State, Action> { _, action in
			switch action {
			case .view(.didTapReportBugButton):
				return Analytics.Settings.ReportedBug()
			case .view(.didTapSendFeedbackButton):
				return Analytics.Settings.SentFeedback()
			case .view(.didShowAcknowledgements):
				return Analytics.Settings.ViewedAcknowledgements()
			case .view(.didShowDeveloperDetails):
				return Analytics.Settings.ViewedDeveloper()
			case .view(.didTapViewSource):
				return Analytics.Settings.ViewedSource()
			case .view(.didTapAnalyticsButton):
				return Analytics.Settings.ViewedAnalytics()
			default:
				return nil
			}
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
						.onFirstAppear { viewStore.send(.didShowAcknowledgements) }
				)
				Button(Strings.Settings.Analytics.title) { viewStore.send(.didTapAnalyticsButton) }
					.buttonStyle(.navigation)
			}

			Section {
				NavigationLink(
					Strings.Settings.Help.developer,
					destination: DeveloperDetailsView()
						.onFirstAppear { viewStore.send(.didShowDeveloperDetails) }
				)
				Button(Strings.Settings.Help.viewSource) { viewStore.send(.didTapViewSource) }
				// FIXME: enable tip jar
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
