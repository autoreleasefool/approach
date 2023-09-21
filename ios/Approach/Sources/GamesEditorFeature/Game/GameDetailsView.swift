import AssetsLibrary
import AvatarServiceInterface
import ComposableArchitecture
import ModelsLibrary
import StringsLibrary
import SwiftUI
import SwiftUIExtensionsLibrary
import ViewsLibrary

public struct GameDetailsView: View {
	let store: StoreOf<GameDetails>

	@State private var minimumSheetContentSize: CGSize = .zero
	@State private var sectionHeaderContentSize: CGSize = .zero

	public var body: some View {
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

				if let game = viewStore.game {
					if viewStore.isGearEnabled {
						Section {
							if game.gear.isEmpty {
								Text(Strings.Game.Editor.Fields.Gear.help)
							} else {
								ForEach(game.gear) { gear in
									Gear.ViewWithAvatar(gear)
										.swipeActions(allowsFullSwipe: false) {
											DeleteButton { viewStore.send(.didSwipeGear(.delete, id: gear.id)) }
										}
								}
							}
						} header: {
							HStack(alignment: .firstTextBaseline) {
								Text(Strings.Gear.List.title)
								Spacer()
								Button { viewStore.send(.didTapGear) } label: {
									Text(Strings.Action.select)
										.font(.caption)
								}
							}
						} footer: {
							if !game.gear.isEmpty {
								Text(Strings.Game.Editor.Fields.Gear.help)
							}
						}
					}

					Section(Strings.MatchPlay.title) {
						Toggle(
							Strings.MatchPlay.record,
							isOn: viewStore.binding(get: { $0.game?.matchPlay != nil }, send: { _ in .didToggleMatchPlay })
						)

						if let matchPlay = game.matchPlay {
							if viewStore.isOpponentsEnabled {
								Button { viewStore.send(.didTapOpponent) } label: {
									HStack {
										LabeledContent(
											Strings.Opponent.title,
											value: game.matchPlay?.opponent?.name ?? Strings.none
										)
										// We don't use .navigation button style because it appears disabled in this context
										Image(systemSymbol: .chevronForward)
											.resizable()
											.scaledToFit()
											.frame(width: .tinyIcon, height: .tinyIcon)
											.foregroundColor(Color(uiColor: .secondaryLabel))
									}
									.contentShape(Rectangle())
								}
								.buttonStyle(TappableElement())
							}

							TextField(
								Strings.MatchPlay.Properties.opponentScore,
								text: viewStore.binding(
									get: {
										if let score = $0.game?.matchPlay?.opponentScore, score > 0 {
											return String(score)
										} else {
											return ""
										}
									},
									send: { .didSetMatchPlayScore($0) }
								)
							)
							.keyboardType(.numberPad)

							Picker(
								Strings.MatchPlay.Properties.result,
								selection: viewStore.binding(get: { _ in matchPlay.result }, send: { .didSetMatchPlayResult($0) })
							) {
								Text("").tag(nil as MatchPlay.Result?)
								ForEach(MatchPlay.Result.allCases) {
									Text(String(describing: $0)).tag(Optional($0))
								}
							}
						}
					}

					Section(Strings.Alley.title) {
						if let alley = game.series.alley?.name {
							LabeledContent(Strings.Alley.Title.bowlingAlley, value: alley)

							Toggle(
								Strings.Game.Editor.Fields.Alley.Lanes.selectLanes,
								isOn: viewStore.$isSelectingLanes
							)

							if viewStore.isSelectingLanes {
								Button { viewStore.send(.didTapManageLanes) } label: {
									HStack {
										LabeledContent(
											Strings.Game.Editor.Fields.Alley.Lanes.manageLanes,
											value: viewStore.laneLabels
										)
										// We don't use .navigation button style because it appears disabled in this context
										Image(systemSymbol: .chevronForward)
											.resizable()
											.scaledToFit()
											.frame(width: .tinyIcon, height: .tinyIcon)
											.foregroundColor(Color(uiColor: .secondaryLabel))
									}
									.contentShape(Rectangle())
								}
								.buttonStyle(TappableElement())
							}
						} else {
							Text(Strings.Game.Editor.Fields.Alley.noneSelected)
								.font(.caption)
						}
					}

					Section {
						Toggle(
							Strings.Game.Editor.Fields.ScoringMethod.label,
							isOn: viewStore.binding(get: { $0.game?.scoringMethod == .manual }, send: { _ in .didToggleScoringMethod })
						)

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
					} footer: {
						Text(Strings.Game.Editor.Fields.Lock.help)
					}

					Section {
						Toggle(
							Strings.Game.Editor.Fields.ExcludeFromStatistics.label,
							isOn: viewStore.binding(get: { $0.game?.excludeFromStatistics == .exclude }, send: { _ in .didToggleExclude })
						)
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
