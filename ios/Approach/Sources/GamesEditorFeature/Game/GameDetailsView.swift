import AssetsLibrary
import AvatarServiceInterface
import ComposableArchitecture
import ModelsLibrary
import ResourcePickerLibrary
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
					.onAppear { viewStore.send(.didMeasureSectionHeaderContentSize(sectionHeaderContentSize), animation: .easeInOut) }
					.onDisappear { viewStore.send(.didMeasureSectionHeaderContentSize(.zero), animation: .easeInOut) }

					if let game = viewStore.game {
						if viewStore.isGearEnabled {
							GearSummarySection(gear: game.gear) {
								viewStore.send(.didTapGear)
							}
						}

						Section(Strings.MatchPlay.title) {
							NavigationButton { viewStore.send(.didTapMatchPlay) } content: {
								MatchPlaySummary(matchPlay: game.matchPlay)
							}
						}

						AlleySummarySection(
							alleyInfo: game.series.alley,
							lanes: game.lanes
						) {
							viewStore.send(.didTapAlley)
						}

						Section {
							Toggle(
								Strings.Game.Editor.Fields.ScoringMethod.label,
								isOn: viewStore.binding(get: { $0.game?.scoringMethod == .manual }, send: { _ in .didToggleScoringMethod })
							)
							.toggleStyle(CheckboxToggleStyle())

							if game.scoringMethod == .manual {
								Button { viewStore.send(.didTapManualScore) } label: {
									Text(String(game.score))
								}
							}
						} footer: {
							Text(Strings.Game.Editor.Fields.ScoringMethod.help)
						}
						.alert(
							Strings.Game.Editor.Fields.ManualScore.title,
							isPresented: viewStore.binding(get: \.isScoreAlertPresented, send: { _ in .didDismissScoreAlert })
						) {
							TextField(
								Strings.Game.Editor.Fields.ManualScore.prompt,
								text: viewStore.binding(
									get: { $0.alertScore > 0 ? String($0.alertScore) : "" },
									send: { .didSetAlertScore($0) }
								)
							)
							.keyboardType(.numberPad)
							Button(Strings.Action.save) { viewStore.send(.didTapSaveScore) }
							Button(Strings.Action.cancel, role: .cancel) { viewStore.send(.didTapCancelScore) }
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
			})
			.toolbar(.hidden)
			.navigationDestination(
				store: store.scope(state: \.$destination, action: { .internal(.destination($0)) }),
				state: /GameDetails.Destination.State.matchPlay,
				action: GameDetails.Destination.Action.matchPlay
			) {
				MatchPlayEditorView(store: $0)
			}
			.navigationDestination(
				store: store.scope(state: \.$destination, action: { .internal(.destination($0)) }),
				state: /GameDetails.Destination.State.gearPicker,
				action: GameDetails.Destination.Action.gearPicker
			) {
				ResourcePickerView(store: $0) {
					Gear.ViewWithAvatar($0)
				}
			}
			.navigationDestination(
				store: store.scope(state: \.$destination, action: { .internal(.destination($0)) }),
				state: /GameDetails.Destination.State.lanePicker,
				action: GameDetails.Destination.Action.lanePicker
			) {
				ResourcePickerView(store: $0) {
					Lane.View($0)
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
					Text(Strings.Series.Editor.Fields.ExcludeFromStatistics.help)
				}
			}
		}
	}
}

extension MatchPlay.Result: CustomStringConvertible {
	public var description: String {
		switch self {
		case .lost: return Strings.MatchPlay.Properties.Result.lost
		case .tied: return Strings.MatchPlay.Properties.Result.tied
		case .won: return Strings.MatchPlay.Properties.Result.won
		}
	}
}

private struct MinimumSheetContentSizeKey: PreferenceKey, CGSizePreferenceKey {}
private struct SectionHeaderContentSizeKey: PreferenceKey, CGSizePreferenceKey {}
