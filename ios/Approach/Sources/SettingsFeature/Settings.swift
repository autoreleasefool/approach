import AnalyticsServiceInterface
import AppIconServiceInterface
import AppInfoPackageServiceInterface
import ArchiveListFeature
import AssetsLibrary
import BundlePackageServiceInterface
import ComposableArchitecture
import ConstantsLibrary
import DatabaseMockingServiceInterface
import EmailServiceInterface
import FeatureActionLibrary
import FeatureFlagsLibrary
import FeatureFlagsListFeature
import FeatureFlagsServiceInterface
import Foundation
import ImportExportFeature
import ImportExportServiceInterface
import OpponentsListFeature
import PasteboardPackageServiceInterface
import StringsLibrary

@Reducer
public struct Settings: Reducer {

	@ObservableState
	public struct State: Equatable {
		public var isShowingDeveloperOptions: Bool

		public var isLoadingAppIcon: Bool = true
		public var currentAppIcon: AppIcon?

		public var isShowingBugReportEmail: Bool = false
		public var isShowingSendFeedbackEmail: Bool = false

		@Presents public var destination: Destination.State?

		public let appName: String
		public let appVersion: String

		public let isImportEnabled: Bool
		public let isDeveloperOptionsEnabled: Bool

		public init() {
			@Dependency(FeatureFlagsService.self) var featureFlags
			self.isShowingDeveloperOptions = featureFlags.isEnabled(.developerOptions)
			self.isImportEnabled = featureFlags.isEnabled(.dataImport)
			self.isDeveloperOptionsEnabled = featureFlags.isEnabled(.developerOptions)

			@Dependency(\.appInfo) var appInfo
			self.appVersion = appInfo.getFullAppVersion()
			self.appName = Strings.App.name
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

			case destination(PresentationAction<Destination.Action>)
		}

		case view(View)
		case delegate(Delegate)
		case `internal`(Internal)
		case binding(BindingAction<State>)
	}

	@Reducer(state: .equatable)
	public enum Destination {
		case archive(ArchiveList)
		case appIcon(AppIconList)
		case featureFlags(FeatureFlagsList)
		case opponentsList(OpponentsList)
		case statistics(StatisticsSettings)
		case analytics(AnalyticsSettings)
		case export(Export)
		case alert(AlertState<AlertAction>)
	}

	public enum AlertAction: Equatable {
		case didTapDismissButton
	}

	@Dependency(AnalyticsService.self) var analytics
	@Dependency(AppIconService.self) var appIcon
	@Dependency(DatabaseMockingService.self) var databaseMocking
	@Dependency(EmailService.self) var email
	@Dependency(ExportService.self) var export
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
						@Dependency(\.appInfo) var appInfo
						try await pasteboard.copyToClipboard(appInfo.getFullAppVersion())
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
					state.destination = .alert(AlertState {
						TextState(Strings.copiedToClipboard)
					})
					return .none

				case let .didFetchIcon(.success(icon)):
					state.isLoadingAppIcon = false
					state.currentAppIcon = icon
					return .none

				case .didFetchIcon(.failure):
					state.isLoadingAppIcon = false
					return .none

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
					case .analytics, .appIcon, .archive, .featureFlags, .opponentsList, .statistics, .alert, .none:
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
						.destination(.presented(.export(.internal))), .destination(.presented(.export(.view))),
						.destination(.presented(.alert(.didTapDismissButton))):
					return .none
				}

			case .delegate, .binding:
				return .none
			}
		}
		.ifLet(\.$destination, action: \.internal.destination)

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

		ErrorHandlerReducer<State, Action> { _, action in
			switch action {
			case let .internal(.didFetchIcon(.failure(error))):
				return error
			default:
				return nil
			}
		}
	}
}
