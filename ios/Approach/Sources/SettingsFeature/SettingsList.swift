import AchievementsFeature
import AchievementsRepositoryInterface
import AppIconServiceInterface
import AppInfoPackageServiceInterface
import AssetsLibrary
import AutomaticBackupsFeature
import ComposableArchitecture
import ConstantsLibrary
import DateTimeLibrary
import EmailServiceInterface
import FeatureActionLibrary
import FeatureFlagsLibrary
import FeatureFlagsListFeature
import ImportExportServiceInterface
import ImportExportFeature
import ModelsLibrary
import OpponentsListFeature
import PasteboardPackageServiceInterface
import StringsLibrary
import SwiftUI
import ViewsLibrary

@Reducer
public struct SettingsList: Reducer, Sendable {
	@ObservableState
	public struct State: Equatable {
		public let isAchievementsEnabled: Bool
		public let isAutomaticBackupsEnabled: Bool
		public let isDeveloperOptionsEnabled: Bool

		public let appName: String
		public let appVersion: String

		public var appIcon: Loadable<AppIcon, Never> = .notLoaded
		@Shared(.unseenAchievements) public var unseenAchievements: Int = 0

		public var selectedItem: SettingsItem?

		public var isShowingBugReportEmail: Bool = false
		public var isShowingSendFeedbackEmail: Bool = false

		public var daysSinceLastBackup: DaysSince
		public var daysSinceLastExport: DaysSince

		public var baseSettings = GeneralSettings.State()
		@Presents public var destination: Destination.State?
		@Presents public var alert: AlertState<AlertAction>?

		public init() {
			@Dependency(\.appInfo) var appInfo
			self.appVersion = appInfo.getFullAppVersion()
			self.appName = Strings.App.name

			@Dependency(\.featureFlags) var featureFlags
			self.isAchievementsEnabled = featureFlags.isFlagEnabled(.achievements)
			self.isAutomaticBackupsEnabled = featureFlags.isFlagEnabled(.automaticBackups)
			self.isDeveloperOptionsEnabled = featureFlags.isFlagEnabled(.developerOptions)

			@Dependency(\.date) var date
			@Dependency(ExportService.self) var export
			@Dependency(BackupsService.self) var backups
			daysSinceLastBackup = backups.lastSuccessfulBackupDate()?.daysSince(date()) ?? .never
			daysSinceLastExport = export.lastExportDate()?.daysSince(date()) ?? .never
		}
	}

	public enum Action: FeatureAction, ViewAction, BindableAction {
		@CasePathable
		public enum View {
			case onAppear
			case didTapVersionNumber
			case didTapReportBugButton
			case didTapSendFeedbackButton
			case didShowAcknowledgements
			case didShowDeveloperDetails
			case didTapViewSourceButton
		}
		@CasePathable
		public enum Internal {
			case didCopyToClipboard
			case didFetchIcon(Result<AppIcon?, Error>)

			case baseSettings(GeneralSettings.Action)
			case destination(PresentationAction<Destination.Action>)
			case alert(PresentationAction<AlertAction>)
		}
		@CasePathable
		public enum Delegate { case doNothing }

		case view(View)
		case `internal`(Internal)
		case delegate(Delegate)
		case binding(BindingAction<State>)
	}

	@Reducer(state: .equatable)
	public enum Destination {
		case achievements(AchievementsList)
		case acknowledgements
		case analytics(AnalyticsSettings)
		case appIcon(AppIconList)
		case backups(BackupsList)
		case `import`(Import)
		case developerOptions(DeveloperOptionsSettings)
		case development
		case export(Export)
		case featureFlags(FeatureFlagsList)
		case opponents(OpponentsList)
		case statistics(StatisticsSettings)
	}

	public enum SettingsItem: Hashable {
		case achievements
		case acknowledgements
		case analytics
		case appIcon
		case backups
		case developerOptions
		case development
		case export
		case featureFlags
		case `import`
		case opponents
		case statistics
	}

	public enum AlertAction: Equatable {
		case didTapDismissButton
	}

	public init() {}

	@Dependency(AppIconService.self) var appIcon
	@Dependency(\.appInfo) var appInfo
	@Dependency(BackupsService.self) var backups
	@Dependency(\.date) var date
	@Dependency(EmailService.self) var email
	@Dependency(ExportService.self) var export
	@Dependency(\.openURL) var openURL
	@Dependency(\.pasteboard) var pasteboard

	public var body: some ReducerOf<Self> {
		BindingReducer()

		Scope(state: \.baseSettings, action: \.internal.baseSettings) {
			GeneralSettings()
		}

		Reduce<State, Action> { state, action in
			switch action {
			case let .view(viewAction):
				switch viewAction {
				case .onAppear:
					state.daysSinceLastBackup = backups.lastSuccessfulBackupDate()?.daysSince(date()) ?? .never
					state.daysSinceLastExport = export.lastExportDate()?.daysSince(date()) ?? .never
					return refreshAppIcon()

				case .didTapVersionNumber:
					return .run { send in
						try await pasteboard.copyToClipboard(appInfo.getFullAppVersion())
						await send(.internal(.didCopyToClipboard))
					}

				case .didTapReportBugButton:
					return .run { send in
						if await email.canSendEmail() {
							await send(.binding(.set(\.isShowingBugReportEmail, true)))
						} else {
							guard let mailto = URL(string: "mailto://\(Strings.supportEmail)") else { return }
							await openURL(mailto)
						}
					}

				case .didTapSendFeedbackButton:
					return .run { send in
						if await email.canSendEmail() {
							await send(.binding(.set(\.isShowingSendFeedbackEmail, true)))
						} else {
							guard let mailto = URL(string: "mailto://\(Strings.supportEmail)") else { return }
							await openURL(mailto)
						}
					}

				case .didShowAcknowledgements:
					return .none

				case .didShowDeveloperDetails:
					return .none

				case .didTapViewSourceButton:
					return .run { _ in
						await openURL(AppConstants.openSourceRepositoryUrl)
					}
				}

			case let .internal(internalAction):
				switch internalAction {
				case .didCopyToClipboard:
					state.alert = AlertState {
						TextState(Strings.copiedToClipboard)
					}
					return .none

				case let .didFetchIcon(.success(icon)):
					state.appIcon = .loaded(icon ?? .primary)
					return .none

				case .didFetchIcon(.failure):
					state.appIcon = .failed(nil, nil)
					return .none

				case let .baseSettings(.delegate(delegateAction)):
					switch delegateAction {
					case .openAppIconSettings:
						state.selectedItem = .appIcon
						state.destination = .appIcon(.init())
						return .none
					}

				case .alert(.dismiss), .alert(.presented(.didTapDismissButton)):
					return .none

				case .destination(.dismiss):
					return .none

				case .baseSettings(.internal),
						.baseSettings(.view),
						.destination(.presented(.acknowledgements)),
						.destination(.presented(.achievements(.delegate(.doNothing)))),
						.destination(.presented(.achievements(.internal))),
						.destination(.presented(.achievements(.view))),
						.destination(.presented(.analytics(.delegate(.doNothing)))),
						.destination(.presented(.analytics(.internal))),
						.destination(.presented(.analytics(.view))),
						.destination(.presented(.analytics(.binding))),
						.destination(.presented(.appIcon(.delegate(.doNothing)))),
						.destination(.presented(.appIcon(.internal))),
						.destination(.presented(.appIcon(.view))),
						.destination(.presented(.backups(.delegate(.doNothing)))),
						.destination(.presented(.backups(.internal))),
						.destination(.presented(.backups(.view))),
						.destination(.presented(.backups(.binding))),
						.destination(.presented(.developerOptions(.delegate(.doNothing)))),
						.destination(.presented(.developerOptions(.internal))),
						.destination(.presented(.developerOptions(.view))),
						.destination(.presented(.export(.delegate(.doNothing)))),
						.destination(.presented(.export(.internal))),
						.destination(.presented(.export(.view))),
						.destination(.presented(.featureFlags(.delegate(.doNothing)))),
						.destination(.presented(.featureFlags(.internal))),
						.destination(.presented(.featureFlags(.view))),
						.destination(.presented(.import(.delegate(.doNothing)))),
						.destination(.presented(.import(.internal))),
						.destination(.presented(.import(.view))),
						.destination(.presented(.import(.binding))),
						.destination(.presented(.opponents(.delegate(.doNothing)))),
						.destination(.presented(.opponents(.internal))),
						.destination(.presented(.opponents(.view))),
						.destination(.presented(.statistics(.delegate(.doNothing)))),
						.destination(.presented(.statistics(.internal))),
						.destination(.presented(.statistics(.view))),
						.destination(.presented(.statistics(.binding))):
					return .none
				}

			case .binding(\.selectedItem):
				switch state.selectedItem {
				case .achievements:
					state.destination = .achievements(.init())
				case .acknowledgements:
					state.destination = .acknowledgements
				case .analytics:
					state.destination = .analytics(.init())
				case .appIcon:
					state.destination = .appIcon(.init())
				case .backups:
					state.destination = .backups(.init())
				case .developerOptions:
					state.destination = .developerOptions(.init())
				case .development:
					state.destination = .development
				case .export:
					state.destination = .export(.init())
				case .featureFlags:
					state.destination = .featureFlags(.init())
				case .import:
					state.destination = .import(.init())
				case .opponents:
					state.destination = .opponents(.init())
				case .statistics:
					state.destination = .statistics(.init())
				case .none:
					state.destination = nil
				}
				return .none

			case .delegate, .binding:
				return .none
			}
		}
		.ifLet(\.$destination, action: \.internal.destination)
		.ifLet(\.$alert, action: \.internal.alert)
	}

	private func refreshAppIcon() -> Effect<Action> {
		.run { send in
			await send(.internal(.didFetchIcon(Result {
				AppIcon(rawValue: await appIcon.getAppIconName() ?? "")
			})))
		}
	}
}

// MARK: - View

@ViewAction(for: SettingsList.self)
public struct SettingsListView: View {
	@Bindable public var store: StoreOf<SettingsList>

	public init(store: StoreOf<SettingsList>) {
		self.store = store
	}

	public var body: some View {
		NavigationSplitView {
			List(selection: $store.selectedItem) {
				if store.isDeveloperOptionsEnabled {
					DeveloperOptionsListSection()
				}

				UserSettingsListSection(
					isAchievementsEnabled: store.isAchievementsEnabled,
					unseenAchievements: store.unseenAchievements
				)

				DataListSection(
					isBackupsButtonVisible: store.isAutomaticBackupsEnabled,
					daysSinceLastBackup: store.daysSinceLastBackup,
					daysSinceLastExport: store.daysSinceLastExport
				)

				if let appIcon = store.appIcon.value?.image {
					AppIconListSection(appIcon: appIcon)
				}

				HelpListSection(
					onTapReportBugButton: { send(.didTapReportBugButton) },
					onTapSendFeedbackButton: { send(.didTapSendFeedbackButton) },
					onAcknowledgementsFirstAppear: { send(.didShowAcknowledgements) }
				)

				DevelopmentListSection()

				AppInfoSection(
					appVersion: store.appVersion,
					onTapVersionNumber: { send(.didTapVersionNumber) }
				)
			}
			.navigationTitle(Strings.Settings.title)
			.alert($store.scope(state: \.alert, action: \.internal.alert))
			.sheet(isPresented: $store.isShowingBugReportEmail) {
				EmailView(
					content: .init(
						recipients: [Strings.supportEmail],
						subject: Strings.Settings.Help.ReportBug.subject(store.appVersion)
					)
				)
			}
			.sheet(isPresented: $store.isShowingSendFeedbackEmail) {
				EmailView(
					content: .init(
						recipients: [Strings.supportEmail]
					)
				)
			}
		} detail: {
			NavigationStack {
				switch store.destination {
				case .achievements:
					achievementsListView
				case .acknowledgements:
					acknowledgementsView
				case .analytics:
					analyticsSettingsView
				case .appIcon:
					appIconListView
				case .backups:
					backupsListView
				case .developerOptions:
					developerOptionsSettingsView
				case .development:
					developmentView
				case .export:
					exportView
				case .featureFlags:
					featureFlagsListView
				case .import:
					importView
				case .opponents:
					opponentsListView
				case .statistics:
					statisticsSettingsView
				case .none:
					generalSettingsView
				}
			}
		}
		.onAppear { send(.onAppear) }
	}

	@ViewBuilder private var achievementsListView: some View {
		if let store = store.scope(state: \.destination?.achievements, action: \.internal.destination.achievements) {
			AchievementsListView(store: store)
		}
	}

	@ViewBuilder private var acknowledgementsView: some View {
		AcknowledgementsView()
			.onFirstAppear { send(.didShowAcknowledgements) }
	}

	@ViewBuilder private var analyticsSettingsView: some View {
		if let store = store.scope(state: \.destination?.analytics, action: \.internal.destination.analytics) {
			AnalyticsSettingsView(store: store)
		}
	}

	@ViewBuilder private var appIconListView: some View {
		if let store = store.scope(state: \.destination?.appIcon, action: \.internal.destination.appIcon) {
			AppIconListView(store: store)
		}
	}

	@ViewBuilder private var backupsListView: some View {
		if let store = store.scope(state: \.destination?.backups, action: \.internal.destination.backups) {
			BackupsListView(store: store)
		}
	}

	@ViewBuilder private var developerOptionsSettingsView: some View {
		if let store = store.scope(state: \.destination?.developerOptions, action: \.internal.destination.developerOptions) {
			DeveloperOptionsSettingsView(store: store)
		}
	}

	@ViewBuilder private var developmentView: some View {
		DevelopmentView(
			onTapViewSourceButton: { send(.didTapViewSourceButton) }
		)
	}

	@ViewBuilder private var exportView: some View {
		if let store = store.scope(state: \.destination?.export, action: \.internal.destination.export) {
			ExportView(store: store)
		}
	}

	@ViewBuilder private var featureFlagsListView: some View {
		if let store = store.scope(state: \.destination?.featureFlags, action: \.internal.destination.featureFlags) {
			FeatureFlagsListView(store: store)
		}
	}

	@ViewBuilder private var generalSettingsView: some View {
		GeneralSettingsView(store: store.scope(state: \.baseSettings, action: \.internal.baseSettings))
	}

	@ViewBuilder private var importView: some View {
		if let store = store.scope(state: \.destination?.import, action: \.internal.destination.import) {
			ImportView(store: store)
		}
	}

	@ViewBuilder private var opponentsListView: some View {
		if let store = store.scope(state: \.destination?.opponents, action: \.internal.destination.opponents) {
			OpponentsListView(store: store)
		}
	}

	@ViewBuilder private var statisticsSettingsView: some View {
		if let store = store.scope(state: \.destination?.statistics, action: \.internal.destination.statistics) {
			StatisticsSettingsView(store: store)
		}
	}
}
