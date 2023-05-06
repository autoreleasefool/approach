import ComposableArchitecture
import DateTimeLibrary
import ModelsLibrary
import StringsLibrary
import SwiftUI

public struct GamesSettingsView: View {
	let store: StoreOf<GamesSettings>

	struct ViewState: Equatable {
		let bowler: String
		let league: String
		let series: String
		let gameOrdinal: Int
		let gameLock: Game.Lock
		let gameExcluded: Game.ExcludeFromStatistics

		init(state: GamesSettings.State) {
			self.bowler = state.game.bowler.name
			self.league = state.game.league.name
			self.series = state.game.series.date.shortFormat
			self.gameOrdinal = state.game.index + 1
			self.gameLock = state.game.locked
			self.gameExcluded = state.game.excludeFromStatistics
		}
	}

	enum ViewAction {
		case didToggleLock
		case didToggleExclude
		case didTapDone
	}

	init(store: StoreOf<GamesSettings>) {
		self.store = store
	}

	public var body: some View {
		WithViewStore(store, observe: ViewState.init, send: GamesSettings.Action.init) { viewStore in
			Form {
				Section(Strings.Game.Settings.current) {
					Grid(verticalSpacing: .tinySpacing) {
						GridRow {
							Text(viewStore.bowler)
								.font(.title3)
								.gridColumnAlignment(.leading)
							Text(Strings.Game.title(viewStore.gameOrdinal))
								.font(.title3)
								.gridColumnAlignment(.trailing)
						}

						Divider().hidden()

						GridRow {
							Text(viewStore.league)
								.gridColumnAlignment(.leading)
							Text(viewStore.series)
								.gridColumnAlignment(.trailing)
						}
					}
				}

				Section {
					Toggle(
						Strings.Game.Editor.Fields.Lock.label,
						isOn: viewStore.binding(get: { $0.gameLock == .locked }, send: ViewAction.didToggleLock)
					)
				} footer: {
					Text(Strings.Game.Editor.Fields.Lock.help)
				}

				Section {
					Toggle(
						Strings.Game.Editor.Fields.ExcludeFromStatistics.label,
						isOn: viewStore.binding(get: { $0.gameExcluded == .exclude }, send: ViewAction.didToggleExclude)
					)
				} footer: {
					// TODO: check if series or league is locked and display different help message
					Text(Strings.Game.Editor.Fields.ExcludeFromStatistics.help)
				}
			}
			.navigationTitle(Strings.Game.Settings.title)
			.toolbar {
				ToolbarItem(placement: .navigationBarLeading) {
					Button(Strings.Action.done) { viewStore.send(.didTapDone) }
				}
			}
		}
	}
}

extension GamesSettings.Action {
	init(action: GamesSettingsView.ViewAction) {
		switch action {
		case .didToggleLock:
			self = .view(.didToggleLock)
		case .didToggleExclude:
			self = .view(.didToggleExclude)
		case .didTapDone:
			self = .delegate(.didFinish)
		}
	}
}
