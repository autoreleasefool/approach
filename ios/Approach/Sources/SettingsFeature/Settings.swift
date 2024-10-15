import AnalyticsServiceInterface
import AppIconServiceInterface
import AppInfoPackageServiceInterface
import ArchiveListFeature
import AssetsLibrary
import AutomaticBackupsFeature
import BundlePackageServiceInterface
import ComposableArchitecture
import ConstantsLibrary
import DatabaseMockingServiceInterface
import EmailServiceInterface
import FeatureActionLibrary
import FeatureFlagsLibrary
import FeatureFlagsListFeature
import Foundation
import ImportExportFeature
import ImportExportServiceInterface
import OpponentsListFeature
import PasteboardPackageServiceInterface
import StringsLibrary

@Reducer
public struct Settings: Reducer, Sendable {

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
			@Dependency(\.featureFlags) var featureFlags
			self.isShowingDeveloperOptions = featureFlags.isFlagEnabled(.developerOptions)
			self.isImportEnabled = featureFlags.isFlagEnabled(.dataImport)
			self.isDeveloperOptionsEnabled = featureFlags.isFlagEnabled(.developerOptions)

			@Dependency(\.appInfo) var appInfo
			self.appVersion = appInfo.getFullAppVersion()
			self.appName = Strings.App.name
		}

		public mutating func showAppIconList() -> Effect<Settings.Action> {
			self.destination = .appIcon(AppIconList.State())
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
		case `import`(Import)
		case export(Export)
		case alert(AlertState<AlertAction>)
	}

	public enum AlertAction: Equatable {
		case didTapDismissButton
	}

	@Dependency(\.analytics) var analytics
	@Dependency(AppIconService.self) var appIcon
	@Dependency(\.crash) var crash
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
					return refreshAppIcon()

				case .didTapPopulateDatabase:
					return .run { _ in try await databaseMocking.mockDatabase() }

				case .didTapOpponents:
					state.destination = .opponentsList(OpponentsList.State())
					return .none

				case .didTapFeatureFlags:
					state.destination = .featureFlags(FeatureFlagsList.State())
					return .none

				case .didTapStatistics:
					state.destination = .statistics(StatisticsSettings.State())
					return .none

				case .didTapAppIcon:
					state.destination = .appIcon(AppIconList.State())
					return .none

				case .didTapArchive:
					state.destination = .archive(ArchiveList.State())
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
					return .run { _ in crash() }

				case .didTapAnalyticsButton:
					state.destination = .analytics(AnalyticsSettings.State())
					return .none

				case .didTapImportButton:
					state.destination = .import(Import.State())
					return .none

				case .didTapExportButton:
					state.destination = .export(Export.State())
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

				case .destination(.presented(.archive(.delegate(.doNothing)))),
						.destination(.presented(.featureFlags(.delegate(.doNothing)))),
						.destination(.presented(.opponentsList(.delegate(.doNothing)))),
						.destination(.presented(.statistics(.delegate(.doNothing)))),
						.destination(.presented(.appIcon(.delegate(.doNothing)))),
						.destination(.presented(.analytics(.delegate(.doNothing)))),
						.destination(.presented(.export(.delegate(.doNothing)))),
						.destination(.presented(.import(.delegate(.doNothing)))):
					return .none

				case .destination(.dismiss):
					switch state.destination {
					case .export:
						return .run { _ in export.cleanUp() }
					case .appIcon:
						return refreshAppIcon()
					case .analytics, .archive, .featureFlags, .opponentsList, .statistics, .alert, .none, .import:
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
						.destination(.presented(.import(.internal))), .destination(.presented(.import(.view))),
						.destination(.presented(.import(.binding))),
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

	private func refreshAppIcon() -> Effect<Action> {
		return .run { send in
			await send(.internal(.didFetchIcon(Result {
				AppIcon(rawValue: await appIcon.getAppIconName() ?? "")
			})))
		}
	}
}
