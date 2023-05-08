import AssetsLibrary
import ComposableArchitecture
import DateTimeLibrary
import ModelsLibrary
import StringsLibrary
import SwiftUI

public struct GameDetailsView: View {
	let store: StoreOf<GameDetails>

	enum ViewAction {
		case didToggleLock
		case didToggleExclude
	}

	init(store: StoreOf<GameDetails>) {
		self.store = store
	}

	public var body: some View {
		WithViewStore(store, observe: { $0 }, send: GameDetails.Action.init) { viewStore in
			if let alley = viewStore.game.series.alley?.name {
				Section(Strings.Alley.title) {
					LabeledContent(Strings.Alley.Title.bowlingAlley, value: alley)
					LabeledContent(Strings.Lane.List.title, value: viewStore.game.series.laneLabels)
				}
			}

			Section {
				Toggle(
					Strings.Game.Editor.Fields.Lock.label,
					isOn: viewStore.binding(get: { $0.game.locked == .locked }, send: ViewAction.didToggleLock)
				)
			} footer: {
				Text(Strings.Game.Editor.Fields.Lock.help)
			}

			Section {
				Toggle(
					Strings.Game.Editor.Fields.ExcludeFromStatistics.label,
					isOn: viewStore.binding(get: { $0.game.excludeFromStatistics == .exclude }, send: ViewAction.didToggleExclude)
				)
			} footer: {
				// TODO: check if series or league is locked and display different help message
				Text(Strings.Game.Editor.Fields.ExcludeFromStatistics.help)
			}
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

extension Game.Edit.SeriesInfo {
	var laneLabels: String {
		lanes.isEmpty ? Strings.none : lanes.map(\.label).joined(separator: ", ")
	}
}
