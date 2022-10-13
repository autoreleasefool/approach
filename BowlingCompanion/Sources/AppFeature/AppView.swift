import ComposableArchitecture
import SwiftUI

public struct AppView: View {
	let store: StoreOf<App>

	struct ViewState: Equatable {
		init(state: App.State) {}
	}

	enum ViewAction {
		case onAppear
	}

	public init(store: StoreOf<App>) {
		self.store = store
	}

	public var body: some View {
		WithViewStore(store, observe: ViewState.init, send: App.Action.init) { viewStore in
			Form {
				Text("5 Pin Bowling Companion for iOS")
			}
		}
	}
}

extension App.Action {
	init(action: AppView.ViewAction) {
		switch action {
		case .onAppear:
			self = .onAppear
		}
	}
}
