import ArchiveListFeature
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
			self.isShowingAppIcon = !state.isLoadingAppIcon
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

				Section {
					Button { viewStore.send(.didTapOpponents) } label: {
						Text(Strings.Opponent.List.title)
					}
					.buttonStyle(.navigation)
				} footer: {
					Text(Strings.Settings.Opponents.footer)
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

				Section {
					Button { viewStore.send(.didTapArchive) } label: {
						Text(Strings.Settings.Archive.title)
					}
					.buttonStyle(.navigation)
				} footer: {
					Text(Strings.Settings.Archive.footer)
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
			.onAppear { viewStore.send(.onAppear) }
		})
		.toast(store: store.scope(state: \.toast, action: { .internal(.toast($0)) }))
		.archive(store.scope(state: \.$destination, action: { .internal(.destination($0)) }))
		.appIconList(store.scope(state: \.$destination, action: { .internal(.destination($0)) }))
		.opponentsList(store.scope(state: \.$destination, action: { .internal(.destination($0)) }))
		.featureFlagsList(store.scope(state: \.$destination, action: { .internal(.destination($0)) }))
		.statisticsSettings(store.scope(state: \.$destination, action: { .internal(.destination($0)) }))
	}
}

@MainActor extension View {
	fileprivate typealias State = PresentationState<Settings.Destination.State>
	fileprivate typealias Action = PresentationAction<Settings.Destination.Action>

	fileprivate func archive(_ store: Store<State, Action>) -> some View {
		navigationDestination(
			store: store,
			state: /Settings.Destination.State.archive,
			action: Settings.Destination.Action.archive,
			destination: { ArchiveListView(store: $0) }
		)
	}

	fileprivate func appIconList(_ store: Store<State, Action>) -> some View {
		navigationDestination(
			store: store,
			state: /Settings.Destination.State.appIcon,
			action: Settings.Destination.Action.appIcon,
			destination: { AppIconListView(store: $0) }
		)
	}

	fileprivate func opponentsList(_ store: Store<State, Action>) -> some View {
		navigationDestination(
			store: store,
			state: /Settings.Destination.State.opponentsList,
			action: Settings.Destination.Action.opponentsList,
			destination: { OpponentsListView(store: $0) }
		)
	}

	fileprivate func featureFlagsList(_ store: Store<State, Action>) -> some View {
		navigationDestination(
			store: store,
			state: /Settings.Destination.State.featureFlags,
			action: Settings.Destination.Action.featureFlags,
			destination: { FeatureFlagsListView(store: $0) }
		)
	}

	fileprivate func statisticsSettings(_ store: Store<State, Action>) -> some View {
		navigationDestination(
			store: store,
			state: /Settings.Destination.State.statistics,
			action: Settings.Destination.Action.statistics,
			destination: { StatisticsSettingsView(store: $0) }
		)
	}
}
