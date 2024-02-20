import ArchiveListFeature
import AssetsLibrary
import ComposableArchitecture
import ConstantsLibrary
import ExtensionsLibrary
import FeatureActionLibrary
import FeatureFlagsListFeature
import ImportExportFeature
import OpponentsListFeature
import StringsLibrary
import SwiftUI
import SwiftUIExtensionsLibrary
import ViewsLibrary

@ViewAction(for: Settings.self)
public struct SettingsView: View {
	@Perception.Bindable public var store: StoreOf<Settings>

	public init(store: StoreOf<Settings>) {
		self.store = store
	}

	public var body: some View {
		WithPerceptionTracking {
			List {
				if store.isShowingDeveloperOptions {
					Section {
						Button { send(.didTapFeatureFlags) } label: {
							Text(Strings.Settings.FeatureFlags.title)
						}
						.buttonStyle(.navigation)

						Button { send(.didTapPopulateDatabase) } label: {
							Text(Strings.Settings.DeveloperOptions.populateDatabase)
						}
					}
				}

				Section {
					Button { send(.didTapOpponents) } label: {
						Text(Strings.Opponent.List.title)
					}
					.buttonStyle(.navigation)
				} footer: {
					Text(Strings.Settings.Opponents.footer)
				}

				Section {
					Button { send(.didTapStatistics) } label: {
						Text(Strings.Settings.Statistics.title)
					}
					.buttonStyle(.navigation)
				} footer: {
					Text(Strings.Settings.Statistics.footer)
				}

				if !store.isLoadingAppIcon {
					Section {
						Button { send(.didTapAppIcon) } label: {
							AppIconView(
								Strings.Settings.AppIcon.title,
								icon: .image(store.appIconImage),
								isCompact: true
							)
						}
						.buttonStyle(.navigation)
					}
				}

				Section {
					Button { send(.didTapArchive) } label: {
						Text(Strings.Settings.Archive.title)
					}
					.buttonStyle(.navigation)
				} footer: {
					Text(Strings.Settings.Archive.footer)
				}

				Section(Strings.Settings.Help.title) {
					Button(Strings.Settings.Help.reportBug) { send(.didTapReportBugButton) }
					Button(Strings.Settings.Help.sendFeedback) { send(.didTapSendFeedbackButton) }
					if store.isDeveloperOptionsEnabled {
						Button(Strings.Settings.Help.forceCrash) { send(.didTapForceCrashButton) }
					}
					NavigationLink(
						Strings.Settings.Help.acknowledgements,
						destination: AcknowledgementsView()
							.onFirstAppear { send(.didShowAcknowledgements) }
					)
					Button(Strings.Settings.Analytics.title) { send(.didTapAnalyticsButton) }
						.buttonStyle(.navigation)
				}

				Section(Strings.Settings.Data.title) {
					if store.isImportEnabled {
						Button(Strings.Settings.Data.import) { send(.didTapImportButton) }
							.buttonStyle(.navigation)
					}

					Button(Strings.Settings.Data.export) { send(.didTapExportButton) }
						.buttonStyle(.navigation)
				}

				Section {
					NavigationLink(
						Strings.Settings.Help.developer,
						destination: DeveloperDetailsView()
							.onFirstAppear { send(.didShowDeveloperDetails) }
					)
					Button(Strings.Settings.Help.viewSource) { send(.didTapViewSource) }
					// FIXME: enable tip jar
	//				NavigationLink("Tip Jar", destination: TipJarView())
				} header: {
					Text(Strings.Settings.Help.Development.title)
				} footer: {
					Text(Strings.Settings.Help.Development.help(AppConstants.appName))
				}
				.sheet(isPresented: $store.isShowingBugReportEmail) {
					EmailView(
						content: .init(
							recipients: [Strings.Settings.Help.ReportBug.email],
							subject: Strings.Settings.Help.ReportBug.subject(AppConstants.appVersionReadable)
						)
					)
				}
				.sheet(isPresented: $store.isShowingSendFeedbackEmail) {
					EmailView(
						content: .init(
							recipients: [Strings.Settings.Help.SendFeedback.email]
						)
					)
				}

				Section {
					Button {
						send(.didTapVersionNumber)
					} label: {
						LabeledContent(Strings.Settings.AppInfo.version, value: AppConstants.appVersionReadable)
							.contentShape(Rectangle())
					}
					.buttonStyle(.plain)
				} header: {
					Text(Strings.Settings.AppInfo.title)
				} footer: {
					Text(Strings.Settings.AppInfo.copyright)
						.font(.caption)
				}
			}
			.navigationTitle(Strings.Settings.title)
			.onFirstAppear { send(.didFirstAppear) }
			.onAppear { send(.onAppear) }
			.archive($store.scope(state: \.destination?.archive, action: \.internal.destination.archive))
			.appIconList($store.scope(state: \.destination?.appIcon, action: \.internal.destination.appIcon))
			.opponentsList($store.scope(state: \.destination?.opponentsList, action: \.internal.destination.opponentsList))
			.featureFlagsList($store.scope(state: \.destination?.featureFlags, action: \.internal.destination.featureFlags))
			.statisticsSettings($store.scope(state: \.destination?.statistics, action: \.internal.destination.statistics))
			.analytics($store.scope(state: \.destination?.analytics, action: \.internal.destination.analytics))
			.export($store.scope(state: \.destination?.export, action: \.internal.destination.export))
			.alert($store.scope(state: \.destination?.alert, action: \.internal.destination.alert))
		}
	}
}

@MainActor extension View {
	fileprivate func analytics(_ store: Binding<StoreOf<AnalyticsSettings>?>) -> some View {
		navigationDestinationWrapper(item: store) {
			AnalyticsSettingsView(store: $0)
		}
	}

	fileprivate func export(_ store: Binding<StoreOf<Export>?>) -> some View {
		navigationDestinationWrapper(item: store) {
			ExportView(store: $0)
		}
	}

	fileprivate func archive(_ store: Binding<StoreOf<ArchiveList>?>) -> some View {
		navigationDestinationWrapper(item: store) {
			ArchiveListView(store: $0)
		}
	}

	fileprivate func appIconList(_ store: Binding<StoreOf<AppIconList>?>) -> some View {
		navigationDestinationWrapper(item: store) {
			AppIconListView(store: $0)
		}
	}

	fileprivate func opponentsList(_ store: Binding<StoreOf<OpponentsList>?>) -> some View {
		navigationDestinationWrapper(item: store) {
			OpponentsListView(store: $0)
		}
	}

	fileprivate func featureFlagsList(_ store: Binding<StoreOf<FeatureFlagsList>?>) -> some View {
		navigationDestinationWrapper(item: store) {
			FeatureFlagsListView(store: $0)
		}
	}

	fileprivate func statisticsSettings(_ store: Binding<StoreOf<StatisticsSettings>?>) -> some View {
		navigationDestinationWrapper(item: store) {
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
