import ComposableArchitecture
import ModelsLibrary
import StringsLibrary
import SwiftUI
import ViewsLibrary

public struct GameDetailsView: View {
	let store: StoreOf<GameDetails>

	public var body: some View {
		WithViewStore(store, observe: { $0 }, send: { .view($0) }, content: { viewStore in
			if viewStore.isGearEnabled {
				Section {
					if viewStore.game.gear.isEmpty {
						Text(Strings.Game.Editor.Fields.Gear.help)
					} else {
						ForEach(viewStore.game.gear) { gear in
							Gear.View(gear: gear)
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
					if !viewStore.game.gear.isEmpty {
						Text(Strings.Game.Editor.Fields.Gear.help)
					}
				}
			}

			Section(Strings.MatchPlay.title) {
				Toggle(
					Strings.MatchPlay.record,
					isOn: viewStore.binding(get: { $0.game.matchPlay != nil }, send: { _ in .didToggleMatchPlay })
				)

				if let matchPlay = viewStore.game.matchPlay {
					if viewStore.isOpponentsEnabled {
						Button { viewStore.send(.didTapOpponent) } label: {
							HStack {
								LabeledContent(
									Strings.Opponent.title,
									value: viewStore.game.matchPlay?.opponent?.name ?? Strings.none
								)
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
								if let score = $0.game.matchPlay?.opponentScore, score > 0 {
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

			if let alley = viewStore.game.series.alley?.name {
				Section(Strings.Alley.title) {
					LabeledContent(Strings.Alley.Title.bowlingAlley, value: alley)
					// TODO: add lane picker for game
				}
			}

			Section {
				Toggle(
					Strings.Game.Editor.Fields.ScoringMethod.label,
					isOn: viewStore.binding(get: { $0.game.scoringMethod == .manual }, send: { _ in .didToggleScoringMethod })
				)

				if viewStore.game.scoringMethod == .manual {
					Button { viewStore.send(.didTapManualScore) } label: {
						Text(String(viewStore.game.score))
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
					isOn: viewStore.binding(get: { $0.game.locked == .locked }, send: { _ in .didToggleLock })
				)
			} footer: {
				Text(Strings.Game.Editor.Fields.Lock.help)
			}

			Section {
				Toggle(
					Strings.Game.Editor.Fields.ExcludeFromStatistics.label,
					isOn: viewStore.binding(get: { $0.game.excludeFromStatistics == .exclude }, send: { _ in .didToggleExclude })
				)
			} footer: {
				// TODO: check if series or league is locked and display different help message
				Text(Strings.Game.Editor.Fields.ExcludeFromStatistics.help)
			}
		})
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
