import AssetsLibrary
import AvatarServiceInterface
import ComposableArchitecture
import EquatableLibrary
import ModelsLibrary
import ResourcePickerLibrary
import StatisticsDetailsFeature
import StringsLibrary
import SwiftUI
import SwiftUIExtensionsLibrary
import ViewsLibrary

public struct GameDetailsView: View {
	let store: StoreOf<GameDetails>

	@State private var minimumSheetContentSize: CGSize = .zero
	@State private var sectionHeaderContentSize: CGSize = .zero

	public var body: some View {
		NavigationStack {
			WithViewStore(store, observe: { $0 }, send: { .view($0) }, content: { viewStore in
				Form {
					Section {
						GameDetailsHeaderView(
							store: store.scope(state: \.gameDetailsHeader, action: /GameDetails.Action.InternalAction.gameDetailsHeader)
						)
						.listRowInsets(EdgeInsets())
						.listRowBackground(Color.clear)
						.measure(key: MinimumSheetContentSizeKey.self, to: $minimumSheetContentSize)
					} header: {
						Color.clear
							.measure(key: SectionHeaderContentSizeKey.self, to: $sectionHeaderContentSize)
					}
					.onChange(of: minimumSheetContentSize) { viewStore.send(.didMeasureMinimumSheetContentSize($0)) }
					.onChange(of: sectionHeaderContentSize) { viewStore.send(.didMeasureSectionHeaderContentSize($0)) }
					// We force these extra measures on appear/disappear to let child navigation screens
					// take the full height of the screen.
					// This value is used as a negative top padding in `GamesEditor`
					.onAppear { viewStore.send(.didMeasureSectionHeaderContentSize(sectionHeaderContentSize), animation: .easeInOut) }
					.onDisappear { viewStore.send(.didMeasureSectionHeaderContentSize(.zero), animation: .easeInOut) }

					if let game = viewStore.game {
						StatisticsSummarySection(
							currentGameIndex: game.index,
							onTapSeries: { viewStore.send(.didTapSeriesStatisticsButton) },
							onTapGame: { viewStore.send(.didTapGameStatisticsButton) }
						)

						if viewStore.isGearEnabled {
							GearSummarySection(gear: game.gear) {
								viewStore.send(.didTapGear)
							}
						}

						MatchPlaySummarySection(matchPlay: game.matchPlay) {
							viewStore.send(.didTapMatchPlay)
						}

						AlleySummarySection(
							alleyInfo: game.series.alley,
							lanes: game.lanes
						) {
							viewStore.send(.didTapAlley)
						}

						ScoringSummarySection(scoringMethod: game.scoringMethod, score: game.score) {
							viewStore.send(.didTapScoring)
						}

						Section {
							Toggle(
								Strings.Game.Editor.Fields.Lock.label,
								isOn: viewStore.binding(get: { $0.game?.locked == .locked }, send: { _ in .didToggleLock })
							)
							.toggleStyle(CheckboxToggleStyle())
						} footer: {
							Text(Strings.Game.Editor.Fields.Lock.help)
						}

						Section {
							Toggle(
								Strings.Game.Editor.Fields.ExcludeFromStatistics.label,
								isOn: viewStore.binding(get: { $0.game?.excludeFromStatistics == .exclude }, send: { _ in .didToggleExclude })
							)
							.toggleStyle(CheckboxToggleStyle())
						} footer: {
							excludeFromStatisticsHelp(
								excludeLeagueFromStatistics: game.league.excludeFromStatistics,
								seriesPreBowl: game.series.preBowl,
								excludeSeriesFromStatistics: game.series.excludeFromStatistics
							)
						}
					}
				}
				.task { await viewStore.send(.didStartTask).finish() }
				.onAppear { viewStore.send(.onAppear) }
			})
			.toolbar(.hidden)
			.navigationDestination(
				store: store.scope(state: \.$destination, action: { .internal(.destination($0)) })
			) {
				SwitchStore($0) { state in
					switch state {
					case .gearPicker:
						CaseLet(
							/GameDetails.Destination.State.gearPicker,
							 action: GameDetails.Destination.Action.gearPicker
						) {
							ResourcePickerView(store: $0) {
								Gear.ViewWithAvatar($0)
							}
						}
					case .lanePicker:
						CaseLet(
							/GameDetails.Destination.State.lanePicker,
							 action: GameDetails.Destination.Action.lanePicker
						) {
							ResourcePickerView(store: $0) {
								Lane.View($0)
							}
						}
					case .matchPlay:
						CaseLet(
							/GameDetails.Destination.State.matchPlay,
							 action: GameDetails.Destination.Action.matchPlay
						) {
							MatchPlayEditorView(store: $0)
						}
					case .scoring:
						CaseLet(
							/GameDetails.Destination.State.scoring,
							 action: GameDetails.Destination.Action.scoring
						) {
							ScoringEditorView(store: $0)
						}
					case .statistics:
						CaseLet(
							/GameDetails.Destination.State.statistics,
							 action: GameDetails.Destination.Action.statistics
						) {
							MidGameStatisticsDetailsView(store: $0)
						}
					}
				}
			}
		}
	}

	@ViewBuilder private func excludeFromStatisticsHelp(
		excludeLeagueFromStatistics: League.ExcludeFromStatistics,
		seriesPreBowl: Series.PreBowl,
		excludeSeriesFromStatistics: Series.ExcludeFromStatistics
	) -> some View {
		switch excludeLeagueFromStatistics {
		case .exclude:
			Text(Strings.Game.Editor.Fields.ExcludeFromStatistics.excludedWhenLeagueExcluded)
				.foregroundColor(Asset.Colors.Warning.default)
		case .include:
			switch seriesPreBowl {
			case .preBowl:
				Text(Strings.Game.Editor.Fields.ExcludeFromStatistics.excludedWhenSeriesPreBowl)
					.foregroundColor(Asset.Colors.Warning.default)
			case .regular:
				switch excludeSeriesFromStatistics {
				case .exclude:
					Text(Strings.Game.Editor.Fields.ExcludeFromStatistics.excludedWhenSeriesExcluded)
						.foregroundColor(Asset.Colors.Warning.default)
				case .include:
					Text(Strings.Game.Editor.Fields.ExcludeFromStatistics.help)
				}
			}
		}
	}
}

private struct MinimumSheetContentSizeKey: PreferenceKey, CGSizePreferenceKey {}
private struct SectionHeaderContentSizeKey: PreferenceKey, CGSizePreferenceKey {}
