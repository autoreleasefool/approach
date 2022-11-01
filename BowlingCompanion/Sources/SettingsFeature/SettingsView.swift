import ComposableArchitecture
import SwiftUI

public struct SettingsView: View {
	let store: StoreOf<Settings>

	struct ViewState: Equatable {
		init(state: Settings.State) {}
	}

	enum ViewAction {
		case placeholder
	}

	public init(store: StoreOf<Settings>) {
		self.store = store
	}

	public var body: some View {
		WithViewStore(store, observe: ViewState.init, send: Settings.Action.init) { _ in
			List {
				Text("Settings")
					.navigationTitle("Settings")
			}
		}
	}
}

extension Settings.Action {
	init(action: SettingsView.ViewAction) {
		switch action {
		case .placeholder:
			self = .placeholder
		}
	}
}
