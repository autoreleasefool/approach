import AssetsLibrary
import ComposableArchitecture
import DateTimeLibrary
import ModelsLibrary
import StringsLibrary
import SwiftUI

public struct GameDetailsView: View {
	let store: StoreOf<GameDetails>

	struct ViewState: Equatable {
		let gameLock: Game.Lock
		let gameExcluded: Game.ExcludeFromStatistics

		init(state: GameDetails.State) {
			self.gameLock = state.game.locked
			self.gameExcluded = state.game.excludeFromStatistics
		}
	}

	enum ViewAction {
		case didToggleLock
		case didToggleExclude
	}

	init(store: StoreOf<GameDetails>) {
		self.store = store
	}

	public var body: some View {
		WithViewStore(store, observe: ViewState.init, send: GameDetails.Action.init) { viewStore in
			VStack {
				Toggle(
					Strings.Game.Editor.Fields.Lock.label,
					isOn: viewStore.binding(get: { $0.gameLock == .locked }, send: ViewAction.didToggleLock)
				)
				.modifier(ToggleModifier())

				Text(Strings.Game.Editor.Fields.Lock.help)
					.font(.caption)
					.padding(.top, .tinySpacing)
					.padding(.bottom, .smallSpacing)

				Toggle(
					Strings.Game.Editor.Fields.ExcludeFromStatistics.label,
					isOn: viewStore.binding(get: { $0.gameExcluded == .exclude }, send: ViewAction.didToggleExclude)
				)
				.modifier(ToggleModifier())

				// TODO: check if series or league is locked and display different help message
				Text(Strings.Game.Editor.Fields.ExcludeFromStatistics.help)
					.font(.caption)
					.padding(.top, .tinySpacing)
			}
			.padding(.horizontal)
		}
	}
}

extension GameDetails.Action {
	init(action: GameDetailsView.ViewAction) {
		switch action {
		case .didToggleLock:
			self = .view(.didToggleLock)
		case .didToggleExclude:
			self = .view(.didToggleExclude)
		}
	}
}

public struct ToggleModifier: ViewModifier {
	public func body(content: Content) -> some View {
		content
			.padding(.horizontal)
			.padding(.vertical, .smallSpacing)
			.background(
				RoundedRectangle(cornerRadius: .standardRadius)
					.foregroundColor(Color(uiColor: .secondarySystemBackground))
			)
	}
}
