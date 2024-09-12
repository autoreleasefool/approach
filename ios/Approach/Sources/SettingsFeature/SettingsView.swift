import ArchiveListFeature
import AssetsLibrary
import ComposableArchitecture
import ConstantsLibrary
import ExtensionsPackageLibrary
import FeatureActionLibrary
import FeatureFlagsListFeature
import ImportExportFeature
import OpponentsListFeature
import StringsLibrary
import SwiftUI
import SwiftUIExtensionsPackageLibrary
import ViewsLibrary

@ViewAction(for: Settings.self)
public struct SettingsView: View {
	@Bindable public var store: StoreOf<Settings>

	public init(store: StoreOf<Settings>) {
		self.store = store
	}

	public var body: some View {
		List {
			if store.isShowingDeveloperOptions {
				DeveloperOptionsSection(
					onTapFeatureFlagsButton: { send(.didTapFeatureFlags) },
					onTapPopulateDatabaseButton: { send(.didTapPopulateDatabase) }
				)
			}

			OpponentsSection(onTapOpponentsButton: { send(.didTapOpponents) })

			StatisticsSection(onTapStatisticsButton: { send(.didTapStatistics) })

			if !store.isLoadingAppIcon {
				AppIconSection(
					appIconImage: store.appIconImage,
					onTapAppIconButton: { send(.didTapAppIcon) }
				)
			}

			ArchiveSection(onTapArchiveButton: { send(.didTapArchive) })

			HelpSection(
				isForceCrashButtonVisible: store.isDeveloperOptionsEnabled,
				onTapReportBugButton: { send(.didTapReportBugButton) },
				onTapSendFeedbackButton: { send(.didTapSendFeedbackButton) },
				onTapForceCrashButton: { send(.didTapForceCrashButton) },
				onTapAnalyticsButton: { send(.didTapAnalyticsButton) },
				onAcknowledgementsFirstAppear: { send(.didShowAcknowledgements) }
			)

			DataSection(
				isImportButtonVisible: store.isImportEnabled,
				onTapImportButton: { send(.didTapImportButton) },
				onTapExportButton: { send(.didTapExportButton) }
			)

			DevelopmentSection(
				appName: store.appName,
				appVersion: store.appVersion,
				onTapViewSourceButton: { send(.didTapViewSource) },
				onDeveloperDetailsFirstAppear: { send(.didShowDeveloperDetails) },
				isShowingBugReportEmail: $store.isShowingBugReportEmail,
				isShowingSendFeedbackEmail: $store.isShowingSendFeedbackEmail
			)

			AppInfoSection(
				appVersion: store.appVersion,
				onTapVersionNumber: { send(.didTapVersionNumber) }
			)
		}
		.navigationTitle(Strings.Settings.title)
		.onFirstAppear { send(.didFirstAppear) }
		.onAppear { send(.onAppear) }
		.destinations($store)
	}
}

@MainActor extension View {
	fileprivate func destinations(_ store: Bindable<StoreOf<Settings>>) -> some View {
		self
			.archive(store.scope(state: \.destination?.archive, action: \.internal.destination.archive))
			.appIconList(store.scope(state: \.destination?.appIcon, action: \.internal.destination.appIcon))
			.opponentsList(store.scope(state: \.destination?.opponentsList, action: \.internal.destination.opponentsList))
			.featureFlagsList(store.scope(state: \.destination?.featureFlags, action: \.internal.destination.featureFlags))
			.statisticsSettings(store.scope(state: \.destination?.statistics, action: \.internal.destination.statistics))
			.analytics(store.scope(state: \.destination?.analytics, action: \.internal.destination.analytics))
			.export(store.scope(state: \.destination?.export, action: \.internal.destination.export))
			.import(store.scope(state: \.destination?.import, action: \.internal.destination.import))
			.alert(store.scope(state: \.destination?.alert, action: \.internal.destination.alert))
	}

	fileprivate func analytics(_ store: Binding<StoreOf<AnalyticsSettings>?>) -> some View {
		navigationDestination(item: store) {
			AnalyticsSettingsView(store: $0)
		}
	}

	fileprivate func export(_ store: Binding<StoreOf<Export>?>) -> some View {
		navigationDestination(item: store) {
			ExportView(store: $0)
		}
	}

	fileprivate func `import`(_ store: Binding<StoreOf<Import>?>) -> some View {
		navigationDestination(item: store) {
			ImportView(store: $0)
		}
	}

	fileprivate func archive(_ store: Binding<StoreOf<ArchiveList>?>) -> some View {
		navigationDestination(item: store) {
			ArchiveListView(store: $0)
		}
	}

	fileprivate func appIconList(_ store: Binding<StoreOf<AppIconList>?>) -> some View {
		navigationDestination(item: store) {
			AppIconListView(store: $0)
		}
	}

	fileprivate func opponentsList(_ store: Binding<StoreOf<OpponentsList>?>) -> some View {
		navigationDestination(item: store) {
			OpponentsListView(store: $0)
		}
	}

	fileprivate func featureFlagsList(_ store: Binding<StoreOf<FeatureFlagsList>?>) -> some View {
		navigationDestination(item: store) {
			FeatureFlagsListView(store: $0)
		}
	}

	fileprivate func statisticsSettings(_ store: Binding<StoreOf<StatisticsSettings>?>) -> some View {
		navigationDestination(item: store) {
			StatisticsSettingsView(store: $0)
		}
	}
}

extension Settings.State {
	var appIconImage: UIImage {
		if let currentAppIcon {
			return UIImage(named: currentAppIcon.rawValue) ?? UIImage()
		} else {
			return UIImage(named: "AppIcon") ?? UIImage()
		}
	}
}
