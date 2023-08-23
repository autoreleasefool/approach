import AssetsLibrary
import ComposableArchitecture
import ConstantsLibrary
import FeatureActionLibrary
import FeatureFlagsListFeature
import OpponentsListFeature
import StringsLibrary
import SwiftUI
import SwiftUIExtensionsLibrary
import ViewsLibrary

public struct SettingsView: View {
	let store: StoreOf<Settings>

	struct ViewState: Equatable {
		let isShowingDeveloperOptions: Bool
		let showsOpponents: Bool

		let isShowingAppIcon: Bool
		let currentAppIcon: AppIcon?

		var appIconImage: UIImage {
			if let currentAppIcon {
				return UIImage(named: currentAppIcon.rawValue) ?? UIImage()
			} else {
				return UIImage(named: "AppIcon") ?? UIImage()
			}
		}

		init(state: Settings.State) {
			self.isShowingDeveloperOptions = state.isShowingDeveloperOptions
			self.showsOpponents = state.hasOpponentsEnabled
			self.isShowingAppIcon = state.hasAppIconConfigEnabled && !state.isLoadingAppIcon
			self.currentAppIcon = state.currentAppIcon
		}
	}

	public init(store: StoreOf<Settings>) {
		self.store = store
	}

	public var body: some View {
		WithViewStore(store, observe: ViewState.init, send: { .view($0) }, content: { viewStore in
			List {
				if viewStore.isShowingDeveloperOptions {
					Section {
						Button { viewStore.send(.didTapFeatureFlags) } label: {
							Text(Strings.Settings.FeatureFlags.title)
						}
						.buttonStyle(.navigation)

						Button { viewStore.send(.didTapPopulateDatabase) } label: {
							Text(Strings.Settings.DeveloperOptions.populateDatabase)
						}
					}
				}

				if viewStore.showsOpponents {
					Section {
						Button { viewStore.send(.didTapOpponents) } label: {
							Text(Strings.Opponent.List.title)
						}
						.buttonStyle(.navigation)
					} footer: {
						Text(Strings.Settings.Opponents.footer)
					}
				}

				Section {
					Button { viewStore.send(.didTapStatistics) } label: {
						Text(Strings.Settings.Statistics.title)
					}
					.buttonStyle(.navigation)
				} footer: {
					Text(Strings.Settings.Statistics.footer)
				}

				if viewStore.isShowingAppIcon {
					Section {
						Button { viewStore.send(.didTapAppIcon) } label: {
							AppIconView(
								Strings.Settings.AppIcon.title,
								icon: .image(viewStore.appIconImage),
								isCompact: true
							)
						}
						.buttonStyle(.navigation)
					}
				}

				HelpSettingsView(store: store.scope(state: \.helpSettings, action: /Settings.Action.InternalAction.helpSettings))

				Section {
					Button {
						viewStore.send(.didTapVersionNumber)
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
			.onFirstAppear { viewStore.send(.didFirstAppear) }
		})
		.toast(store: store.scope(state: \.toast, action: { .internal(.toast($0)) }))
		.navigationDestination(
			store: store.scope(state: \.$destination, action: { .internal(.destination($0)) }),
			state: /Settings.Destination.State.appIcon,
			action: Settings.Destination.Action.appIcon
		) { (store: StoreOf<AppIconList>) in
			AppIconListView(store: store)
		}
		.navigationDestination(
			store: store.scope(state: \.$destination, action: { .internal(.destination($0)) }),
			state: /Settings.Destination.State.opponentsList,
			action: Settings.Destination.Action.opponentsList
		) { (store: StoreOf<OpponentsList>) in
			OpponentsListView(store: store)
		}
		.navigationDestination(
			store: store.scope(state: \.$destination, action: { .internal(.destination($0)) }),
			state: /Settings.Destination.State.featureFlags,
			action: Settings.Destination.Action.featureFlags
		) { (store: StoreOf<FeatureFlagsList>) in
			FeatureFlagsListView(store: store)
		}
		.navigationDestination(
			store: store.scope(state: \.$destination, action: { .internal(.destination($0)) }),
			state: /Settings.Destination.State.statistics,
			action: Settings.Destination.Action.statistics
		) { (store: StoreOf<StatisticsSettings>) in
			StatisticsSettingsView(store: store)
		}
	}
}
