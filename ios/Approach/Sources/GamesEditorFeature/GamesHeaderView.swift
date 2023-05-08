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
		case didTapGameIndicator
	}

	init(store: StoreOf<GamesHeader>) {
		self.store = store
	}

	public var body: some View {
		WithViewStore(store, observe: { $0 }, send: GamesHeader.Action.init) { viewStore in
			HStack {
				headerButton(systemName: "chevron.backward") { viewStore.send(.didTapClose) }

				Spacer()

				Button { viewStore.send(.didTapGameIndicator) } label: {
					HStack(alignment: .center, spacing: .smallSpacing) {
						Text(Strings.Game.title(viewStore.currentGameOrdinal))
							.font(.caption)
							.foregroundColor(.white)
						if viewStore.numberOfGames > 1 {
							Image(systemName: "chevron.down.circle.fill")
								.resizable()
								.aspectRatio(contentMode: .fit)
								.foregroundColor(.white)
								.frame(width: .extraTinyIcon, height: .extraTinyIcon)
						}
					}
				}
				.buttonStyle(TappableElement())
				.disabled(viewStore.numberOfGames == 1)

				Spacer()

				headerButton(systemName: "gear") { viewStore.send(.didTapSettings) }
			}
		}
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
		case .didTapGameIndicator:
			self = .delegate(.didOpenGamePicker)
		}
	}
}
