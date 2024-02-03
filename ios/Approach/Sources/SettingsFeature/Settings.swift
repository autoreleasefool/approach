import AnalyticsServiceInterface
import AppIconServiceInterface
import ArchiveListFeature
import AssetsLibrary
import ComposableArchitecture
import ConstantsLibrary
import DatabaseMockingServiceInterface
import FeatureActionLibrary
import FeatureFlagsLibrary
import FeatureFlagsListFeature
import FeatureFlagsServiceInterface
import Foundation
import ImportExportFeature
import OpponentsListFeature
import StringsLibrary
import ToastLibrary

@Reducer
public struct Settings: Reducer {

	@ObservableState
	public struct State: Equatable {
		public var isShowingDeveloperOptions: Bool

		public var isLoadingAppIcon: Bool = true
		public var currentAppIcon: AppIcon?

		public var isShowingBugReportEmail: Bool = false
		public var isShowingSendFeedbackEmail: Bool = false

		public var toast: ToastState<ToastAction>?
		@Presents public var destination: Destination.State?

		public let isImportEnabled: Bool
		public let isDeveloperOptionsEnabled: Bool

		public init() {
			@Dependency(\.featureFlags) var featureFlags
			self.isShowingDeveloperOptions = featureFlags.isEnabled(.developerOptions)
			self.isImportEnabled = featureFlags.isEnabled(.dataImport)
			self.isDeveloperOptionsEnabled = featureFlags.isEnabled(.developerOptions)
		}

		public mutating func showAppIconList() -> Effect<Settings.Action> {
			self.destination = .appIcon(.init())
			return .none
		}
	}

	public enum Action: FeatureAction, ViewAction, BindableAction {
		@CasePathable public enum View {
			case onAppear
			case didFirstAppear
			case didTapPopulateDatabase
			case didTapFeatureFlags
			case didTapOpponents
			case didTapStatistics
			case didTapArchive
			case didTapAppIcon
			case didTapVersionNumber
			case didTapReportBugButton
			case didTapSendFeedbackButton
			case didShowAcknowledgements
			case didTapAnalyticsButton
			case didShowDeveloperDetails
			case didTapViewSource
			case didTapImportButton
			case didTapExportButton
			case didTapForceCrashButton
		}
		@CasePathable public enum Delegate { case doNothing }
		@CasePathable public enum Internal {
			case didFetchIcon(Result<AppIcon?, Error>)
			case didCopyToClipboard

			case toast(ToastAction)
			case destination(PresentationAction<Destination.Action>)
		}

		case view(View)
		case delegate(Delegate)
		case `internal`(Internal)
		case binding(BindingAction<State>)
	}

	@Reducer
	public struct Destination: Reducer {

		@ObservableState
		public enum State: Equatable {
			case archive(ArchiveList.State)
			case appIcon(AppIconList.State)
			case featureFlags(FeatureFlagsList.State)
			case opponentsList(OpponentsList.State)
			case statistics(StatisticsSettings.State)
			case analytics(AnalyticsSettings.State)
			case export(Export.State)
		}

		public enum Action {
			case archive(ArchiveList.Action)
			case appIcon(AppIconList.Action)
			case featureFlags(FeatureFlagsList.Action)
			case opponentsList(OpponentsList.Action)
			case statistics(StatisticsSettings.Action)
			case analytics(AnalyticsSettings.Action)
			case export(Export.Action)
		}

		public var body: some ReducerOf<Self> {
			Scope(state: \.archive, action: \.archive) {
				ArchiveList()
			}
			Scope(state: \.appIcon, action: \.appIcon) {
				AppIconList()
			}
			Scope(state: \.featureFlags, action: \.featureFlags) {
				FeatureFlagsList()
			}
			Scope(state: \.opponentsList, action: \.opponentsList) {
				OpponentsList()
			}
			Scope(state: \.statistics, action: \.statistics) {
				StatisticsSettings()
			}
			Scope(state: \.analytics, action: \.analytics) {
				AnalyticsSettings()
			}
			Scope(state: \.export, action: \.export) {
				Export()
			}
		}
	}

	public enum ToastAction: ToastableAction, Equatable {
		case didDismiss
		case didFinishDismissing
	}

	@Dependency(\.analytics) var analytics
	@Dependency(\.appIcon) var appIcon
	@Dependency(\.databaseMocking) var databaseMocking
	@Dependency(\.email) var email
	@Dependency(\.export) var export
	@Dependency(\.openURL) var openURL
	@Dependency(\.pasteboard) var pasteboard

	public init() {}

	public var body: some ReducerOf<Self> {
		BindingReducer()

		Reduce<State, Action> { state, action in
			switch action {
			case let .view(viewAction):
				switch viewAction {
				case .onAppear:
					return .none

				case .didFirstAppear:
					return .run { send in
						await send(.internal(.didFetchIcon(Result {
							AppIcon(rawValue: await appIcon.getAppIconName() ?? "")
						})))
					}

				case .didTapPopulateDatabase:
					return .run { _ in try await databaseMocking.mockDatabase() }

				case .didTapOpponents:
					state.destination = .opponentsList(.init())
					return .none

				case .didTapFeatureFlags:
					state.destination = .featureFlags(.init())
					return .none

				case .didTapStatistics:
					state.destination = .statistics(.init())
					return .none

				case .didTapAppIcon:
					state.destination = .appIcon(.init())
					return .none

				case .didTapArchive:
					state.destination = .archive(.init())
					return .none

				case .didTapVersionNumber:
					return .run { send in
						pasteboard.copyToClipboard(AppConstants.appVersionReadable)
						await send(.internal(.didCopyToClipboard))
					}

				case .didTapReportBugButton:
					return .run { send in
						if await email.canSendEmail() {
							await send(.binding(.set(\.isShowingBugReportEmail, true)))
						} else {
							guard let mailto = URL(string: "mailto://\(Strings.Settings.Help.ReportBug.email)") else { return }
							await openURL(mailto)
						}
					}

				case .didTapSendFeedbackButton:
					return .run { send in
						if await email.canSendEmail() {
							await send(.binding(.set(\.isShowingSendFeedbackEmail, true)))
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

				case .didTapForceCrashButton:
					return .run { _ in analytics.forceCrash() }

				case .didTapAnalyticsButton:
					state.destination = .analytics(.init())
					return .none

				case .didTapImportButton:
					// FIXME: Navigate to data import feature
					return .none

				case .didTapExportButton:
					state.destination = .export(.init())
					return .none
				}

			case let .internal(internalAction):
				switch internalAction {
				case .didCopyToClipboard:
					state.toast = .init(
						content: .toast(.init(
							message: .init(Strings.copiedToClipboard),
							icon: .checkmarkCircleFill
						)),
						style: .success
					)
					return .none

				case let .didFetchIcon(.success(icon)):
					state.isLoadingAppIcon = false
					state.currentAppIcon = icon
					return .none

				case .didFetchIcon(.failure):
					state.isLoadingAppIcon = false
					return .none

				case let .toast(toastAction):
					switch toastAction {
					case .didDismiss:
						state.toast = nil
						return .none

					case .didFinishDismissing:
						return .none
					}

				case .destination(.presented(.archive(.delegate(.doNothing)))):
					return .none

				case .destination(.presented(.featureFlags(.delegate(.doNothing)))):
					return .none

				case .destination(.presented(.opponentsList(.delegate(.doNothing)))):
					return .none

				case .destination(.presented(.statistics(.delegate(.doNothing)))):
					return .none

				case .destination(.presented(.appIcon(.delegate(.doNothing)))):
					return .none

				case .destination(.presented(.analytics(.delegate(.doNothing)))):
					return .none

				case .destination(.presented(.export(.delegate(.doNothing)))):
					return .none

				case .destination(.dismiss):
					switch state.destination {
					case .export:
						return .run { _ in export.cleanUp() }
					case .analytics, .appIcon, .archive, .featureFlags, .opponentsList, .statistics, .none:
						return .none
					}

				case .destination(.presented(.featureFlags(.internal))), .destination(.presented(.featureFlags(.view))),
						.destination(.presented(.statistics(.internal))), .destination(.presented(.statistics(.view))),
						.destination(.presented(.statistics(.binding))),
						.destination(.presented(.appIcon(.view))), .destination(.presented(.appIcon(.internal))),
						.destination(.presented(.opponentsList(.internal))), .destination(.presented(.opponentsList(.view))),
						.destination(.presented(.archive(.internal))), .destination(.presented(.archive(.view))),
						.destination(.presented(.analytics(.internal))), .destination(.presented(.analytics(.view))),
						.destination(.presented(.analytics(.binding))),
						.destination(.presented(.export(.internal))), .destination(.presented(.export(.view))):
					return .none
				}

			case .delegate, .binding:
				return .none
			}
		}
		.ifLet(\.$destination, action: \.internal.destination) {
			Destination()
		}

		AnalyticsReducer<State, Action> { _, action in
			switch action {
			case .view(.didTapOpponents):
				return Analytics.Settings.ViewedOpponents()
			case .view(.didTapStatistics):
				return Analytics.Settings.ViewedStatistics()
			case .view(.didTapAppIcon):
				return Analytics.Settings.ViewedAppIcons()
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
			case .view(.didTapExportButton):
				return Analytics.Settings.ViewedDataExport()
			default:
				return nil
			}
		}

		BreadcrumbReducer<State, Action> { _, action in
			switch action {
			case .view(.onAppear): return .navigationBreadcrumb(type(of: self))
			default: return nil
			}
		}
	}
}
