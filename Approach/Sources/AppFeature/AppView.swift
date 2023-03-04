import AlleysListFeature
import AssetsLibrary
import BowlersListFeature
import ComposableArchitecture
import FeatureActionLibrary
import GearListFeature
import SettingsFeature
import StringsLibrary
import SwiftUI

public struct AppView: View {
	let store: StoreOf<App>

	@Environment(\.horizontalSizeClass) var horizontalSizeClass

	struct ViewState: Equatable {
		let tabs: [App.Tab]
		let selectedTab: App.Tab

		init(state: App.State) {
			self.tabs = state.tabs
			self.selectedTab = state.selectedTab
		}
	}

	enum ViewAction {
		case didAppear
		case didSelectTab(App.Tab)
	}

	public init(store: StoreOf<App>) {
		self.store = store
	}

	public var body: some View {
		WithViewStore(store, observe: ViewState.init, send: App.Action.init) { viewStore in
			if horizontalSizeClass == .compact {
				TabView(
					selection: viewStore.binding(get: \.selectedTab, send: ViewAction.didSelectTab)
				) {
					ForEach(viewStore.tabs) { tab in
						NavigationView {
							switch tab {
							case .alleys:
								AlleysListView(store: store.scope(state: \.alleysList, action: /App.Action.InternalAction.alleysList))
							case .bowlers:
								BowlersListView(store: store.scope(state: \.bowlersList, action: /App.Action.InternalAction.bowlersList))
							case .settings:
								SettingsView(store: store.scope(state: \.settings, action: /App.Action.InternalAction.settings))
							case .gear:
								GearListView(store: store.scope(state: \.gearList, action: /App.Action.InternalAction.gearList))
							}
						}
						.tag(tab)
						.tabItem {
							Label(tab.name, systemImage: tab.image)
						}
					}
				}
				.tint(.appAction)
				.task { await viewStore.send(.didAppear).finish() }
			} else {
				// TODO: create sidebar for ipad size devices
				EmptyView()
			}
		}
	}
}

extension App.Tab {
	var name: String {
		switch self {
		case .alleys:
			return Strings.App.Tabs.alley
		case .settings:
			return Strings.App.Tabs.settings
		case .bowlers:
			return Strings.App.Tabs.scoresheet
		case .gear:
			return Strings.App.Tabs.gear
		}
	}

	var image: String {
		switch self {
		case .alleys:
			return "building.columns"
		case .settings:
			return "gear"
		case .bowlers:
			return "figure.bowling"
		case .gear:
			return "bag"
		}
	}
}

extension App.Action {
	init(action: AppView.ViewAction) {
		switch action {
		case .didAppear:
			self = .view(.didAppear)
		case let .didSelectTab(tab):
			self = .view(.didSelectTab(tab))
		}
	}
}
