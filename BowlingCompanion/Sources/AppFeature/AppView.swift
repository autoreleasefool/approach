import BowlersListFeature
import ComposableArchitecture
import SettingsFeature
import SwiftUI

public struct AppView: View {
	let store: StoreOf<App>

	@Environment(\.horizontalSizeClass) var horizontalSizeClass

	struct ViewState: Equatable {
		let selectedTab: App.Tab

		init(state: App.State) {
			self.selectedTab = state.selectedTab
		}
	}

	enum ViewAction {
		case selectedTab(App.Tab)
	}

	public init(store: StoreOf<App>) {
		self.store = store
	}

	public var body: some View {
		WithViewStore(store, observe: ViewState.init, send: App.Action.init) { viewStore in
			if horizontalSizeClass == .compact {
				TabView(
					selection: viewStore.binding(get: \.selectedTab, send: ViewAction.selectedTab)
				) {
					ForEach(App.Tab.allCases) { tab in
						tab.tabView(store: store)
							.tag(tab)
							.tabItem {
								Image(systemName: tab.image)
								Text(tab.name)
							}
					}
				}
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
		case .settings:
			return "Settings"
		case .bowlers:
			return "Scoresheet"
		}
	}

	var image: String {
		switch self {
		case .settings:
			return "gear"
		case .bowlers:
			return "figure.bowling"
		}
	}

	func tabView(store: StoreOf<App>) -> some View {
		NavigationView {
			switch self {
			case .bowlers:
				BowlersListView(store: store.scope(state: \.bowlersList, action: App.Action.bowlersList))
			case .settings:
				SettingsView(store: store.scope(state: \.settings, action: App.Action.settings))
			}
		}
	}
}

extension App.Action {
	init(action: AppView.ViewAction) {
		switch action {
		case let .selectedTab(tab):
			self = .selectedTab(tab)
		}
	}
}
