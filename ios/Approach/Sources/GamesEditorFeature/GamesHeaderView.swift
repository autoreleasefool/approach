import AssetsLibrary
import ComposableArchitecture
import StringsLibrary
import SwiftUI
import ViewsLibrary

public struct GamesHeaderView: View {
	let store: StoreOf<GamesHeader>

	enum ViewAction {
		case didTapClose
		case didTapSettings
	}

	init(store: StoreOf<GamesHeader>) {
		self.store = store
	}

	public var body: some View {
		WithViewStore(store, observe: { $0 }, send: GamesHeader.Action.init, content: { viewStore in
			HStack {
				headerButton(systemName: "chevron.backward") { viewStore.send(.didTapClose) }
				Spacer()
				headerButton(systemName: "gear") { viewStore.send(.didTapSettings) }
			}
		})
	}

	private func headerButton(systemName: String, action: @escaping () -> Void) -> some View {
		Button(action: action) {
			Image(systemName: systemName)
				.resizable()
				.scaledToFit()
				.frame(width: .smallIcon, height: .smallIcon)
				.foregroundColor(.white)
				.padding()
		}
	}
}

extension GamesHeader.Action {
	init(action: GamesHeaderView.ViewAction) {
		switch action {
		case .didTapClose:
			self = .delegate(.didCloseEditor)
		case .didTapSettings:
			self = .delegate(.didOpenSettings)
		}
	}
}
