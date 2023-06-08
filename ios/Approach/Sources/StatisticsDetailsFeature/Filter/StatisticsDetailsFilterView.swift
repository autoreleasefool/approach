import ComposableArchitecture
import DateTimeLibrary
import ResourcePickerLibrary
import StringsLibrary
import SwiftUI
import SwiftUIExtensionsLibrary

public struct StatisticsDetailsFilterView: View {
	let store: StoreOf<StatisticsDetailsFilter>

	struct ViewState: Equatable {
		let selectedBowlerName: String?
		let selectedLeagueName: String?
		let selectedSeriesDate: String?
		let selectedGameIndex: String?

		init(state: StatisticsDetailsFilter.State) {
			self.selectedBowlerName = state.bowler?.name
			self.selectedLeagueName = state.league?.name
			self.selectedSeriesDate = state.series?.date.longFormat
			if let gameIndex = state.game?.index {
				self.selectedGameIndex = Strings.Game.titleWithOrdinal(gameIndex)
			} else {
				self.selectedGameIndex = nil
			}
		}
	}

	enum ViewAction {
		case didTapBowler
		case didTapLeague
		case didTapSeries
		case didTapGame
	}

	public init(store: StoreOf<StatisticsDetailsFilter>) {
		self.store = store
	}

	public var body: some View {
		WithViewStore(store, observe: ViewState.init, send: StatisticsDetailsFilter.Action.init) { viewStore in
			List {
				Section {
					Button { viewStore.send(.didTapBowler) } label: {
						LabeledContent(Strings.Bowler.title, value: viewStore.selectedBowlerName ?? Strings.none)
					}
					.buttonStyle(.navigation)

					if viewStore.selectedBowlerName != nil {
						Button { viewStore.send(.didTapLeague) } label: {
							LabeledContent(Strings.League.title, value: viewStore.selectedLeagueName ?? Strings.none)
						}
						.buttonStyle(.navigation)

						if viewStore.selectedLeagueName != nil {
							Button { viewStore.send(.didTapSeries) } label: {
								LabeledContent(Strings.Series.title, value: viewStore.selectedSeriesDate ?? Strings.none)
							}
							.buttonStyle(.navigation)

							if viewStore.selectedSeriesDate != nil {
								Button { viewStore.send(.didTapGame) } label: {
									LabeledContent(Strings.Game.title, value: viewStore.selectedGameIndex ?? Strings.none)
								}
								.buttonStyle(.navigation)
							}
						}
					}
				}
			}
			.navigationTitle("Load Statistics")
		}
		.navigationDestination(
			store: store.scope(state: \.$destination, action: { .internal(.destination($0)) }),
			state: /StatisticsDetailsFilter.Destination.State.bowlerPicker,
			action: StatisticsDetailsFilter.Destination.Action.bowlerPicker
		) { store in
			ResourcePickerView(store: store) { bowler in
				Text(bowler.name)
			}
		}
		.navigationDestination(
			store: store.scope(state: \.$destination, action: { .internal(.destination($0)) }),
			state: /StatisticsDetailsFilter.Destination.State.leaguePicker,
			action: StatisticsDetailsFilter.Destination.Action.leaguePicker
		) { store in
			ResourcePickerView(store: store) { league in
				Text(league.name)
			}
		}
		.navigationDestination(
			store: store.scope(state: \.$destination, action: { .internal(.destination($0)) }),
			state: /StatisticsDetailsFilter.Destination.State.seriesPicker,
			action: StatisticsDetailsFilter.Destination.Action.seriesPicker
		) { store in
			ResourcePickerView(store: store) { series in
				Text(series.date.longFormat)
			}
		}
		.navigationDestination(
			store: store.scope(state: \.$destination, action: { .internal(.destination($0)) }),
			state: /StatisticsDetailsFilter.Destination.State.gamePicker,
			action: StatisticsDetailsFilter.Destination.Action.gamePicker
		) { store in
			ResourcePickerView(store: store) { game in
				Text(Strings.Game.titleWithOrdinal(game.index))
			}
		}
	}
}

extension StatisticsDetailsFilter.Action {
	init(action: StatisticsDetailsFilterView.ViewAction) {
		switch action {
		case .didTapBowler:
			self = .view(.didTapBowler)
		case .didTapLeague:
			self = .view(.didTapLeague)
		case .didTapSeries:
			self = .view(.didTapSeries)
		case .didTapGame:
			self = .view(.didTapGame)
		}
	}
}
