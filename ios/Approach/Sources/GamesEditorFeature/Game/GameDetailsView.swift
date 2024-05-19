import AssetsLibrary
import AvatarServiceInterface
import ComposableArchitecture
import EquatablePackageLibrary
import ExtensionsPackageLibrary
import ModelsLibrary
import ResourcePickerLibrary
import StatisticsDetailsFeature
import StringsLibrary
import SwiftUI
import SwiftUIExtensionsPackageLibrary
import ViewsLibrary

@ViewAction(for: GameDetails.self)
public struct GameDetailsView: View {
	@Bindable public var store: StoreOf<GameDetails>

	@State private var minimumSheetContentSize: CGSize = .zero
	@State private var sectionHeaderContentSize: CGSize = .zero

	public var body: some View {
		NavigationStack {
			Form {
				Section {
					GameDetailsHeaderView(
						store: store.scope(state: \.gameDetailsHeader, action: \.internal.gameDetailsHeader)
					)
					.listRowInsets(EdgeInsets())
					.listRowBackground(Color.clear)
					.measure(key: MinimumSheetContentSizeKey.self, to: $minimumSheetContentSize)
				} header: {
					Color.clear
						.measure(key: SectionHeaderContentSizeKey.self, to: $sectionHeaderContentSize)
				}
				.onChange(of: minimumSheetContentSize) { send(.didMeasureMinimumSheetContentSize(minimumSheetContentSize)) }
				.onChange(of: sectionHeaderContentSize) { send(.didMeasureSectionHeaderContentSize(sectionHeaderContentSize)) }
				// We force these extra measures on appear/disappear to let child navigation screens
				// take the full height of the screen.
				// This value is used as a negative top padding in `GamesEditor`
				.onAppear { send(.didMeasureSectionHeaderContentSize(sectionHeaderContentSize), animation: .easeInOut) }
				.onDisappear { send(.didMeasureSectionHeaderContentSize(.zero), animation: .easeInOut) }

				if let game = store.game {
					StatisticsSummarySection(
						currentGameIndex: game.index,
						onTapSeries: { send(.didTapSeriesStatisticsButton) },
						onTapGame: { send(.didTapGameStatisticsButton) }
					)

					GearSummarySection(gear: game.gear) {
						send(.didTapGear)
					}

					MatchPlaySummarySection(matchPlay: game.matchPlay) {
						send(.didTapMatchPlay)
					}

					AlleySummarySection(
						alleyInfo: game.series.alley,
						lanes: game.lanes
					) {
						send(.didTapAlley)
					}

					ScoringSummarySection(scoringMethod: game.scoringMethod, score: game.score) {
						send(.didTapScoring)
					}

					Section {
						Toggle(
							Strings.Game.Editor.Fields.Lock.label,
							isOn: $store.isLocked.sending(\.view.didToggleLock)
						)
						.toggleStyle(.checkboxToggle)
					} footer: {
						Text(Strings.Game.Editor.Fields.Lock.help)
					}

					Section {
						Toggle(
							Strings.Game.Editor.Fields.ExcludeFromStatistics.label,
							isOn: $store.isExcludedFromStatistics.sending(\.view.didToggleExclude)
						)
						.toggleStyle(.checkboxToggle)
					} footer: {
						excludeFromStatisticsHelp(
							excludeLeagueFromStatistics: game.league.excludeFromStatistics,
							seriesPreBowl: game.series.preBowl,
							excludeSeriesFromStatistics: game.series.excludeFromStatistics
						)
					}
				}
			}
			.task { await send(.task).finish() }
			.onAppear { send(.onAppear) }
			.onFirstAppear { send(.didFirstAppear) }
			.toolbar(.hidden)
			.gearPicker($store.scope(state: \.destination?.gearPicker, action: \.internal.destination.gearPicker))
			.lanePicker($store.scope(state: \.destination?.lanePicker, action: \.internal.destination.lanePicker))
			.matchPlay($store.scope(state: \.destination?.matchPlay, action: \.internal.destination.matchPlay))
			.scoring($store.scope(state: \.destination?.scoring, action: \.internal.destination.scoring))
			.statistics($store.scope(state: \.destination?.statistics, action: \.internal.destination.statistics))
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

@MainActor extension View {
	fileprivate func gearPicker(
		_ store: Binding<StoreOf<ResourcePicker<Gear.Summary, AlwaysEqual<Void>>>?>
	) -> some View {
		navigationDestination(item: store) {
			ResourcePickerView(store: $0) {
				Gear.ViewWithAvatar($0)
			}
		}
	}

	fileprivate func lanePicker(_ store: Binding<StoreOf<ResourcePicker<Lane.Summary, Alley.ID>>?>) -> some View {
		navigationDestination(item: store) {
			ResourcePickerView(store: $0) {
				Lane.View($0)
			}
		}
	}

	fileprivate func matchPlay(_ store: Binding<StoreOf<MatchPlayEditor>?>) -> some View {
		navigationDestination(item: store) {
			MatchPlayEditorView(store: $0)
		}
	}

	fileprivate func scoring(_ store: Binding<StoreOf<ScoringEditor>?>) -> some View {
		navigationDestination(item: store) {
			ScoringEditorView(store: $0)
		}
	}

	fileprivate func statistics(_ store: Binding<StoreOf<MidGameStatisticsDetails>?>) -> some View {
		navigationDestination(item: store) {
			MidGameStatisticsDetailsView(store: $0)
		}
	}
}

private struct MinimumSheetContentSizeKey: PreferenceKey, CGSizePreferenceKey {}
private struct SectionHeaderContentSizeKey: PreferenceKey, CGSizePreferenceKey {}
