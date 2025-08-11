import AnalyticsServiceInterface
import AssetsLibrary
import AvatarServiceInterface
import ComposableArchitecture
import EquatablePackageLibrary
import ExtensionsPackageLibrary
import FeatureActionLibrary
import FeatureFlagsLibrary
import FramesRepositoryInterface
import GamesRepositoryInterface
import GearRepositoryInterface
import LanesRepositoryInterface
import MatchPlaysRepositoryInterface
import ModelsLibrary
import PickableModelsLibrary
import ResourcePickerLibrary
import StatisticsDetailsFeature
import StatisticsRepositoryInterface
import StringsLibrary
import SwiftUI
import SwiftUIExtensionsPackageLibrary
import ViewsLibrary

@Reducer
public struct GameDetailsNext: Reducer, Sendable {
	@ObservableState
	public struct State: Equatable {
		@Shared(.bowlerGameIds) public var bowlerGameIds: [Bowler.ID: [Game.ID]]
		@Shared(.currentBowlerId) public var currentBowlerId: Bowler.ID

		@Shared(.game) public var game: Game.Edit?
		@Shared(.score) public var score: ScoredGame?

		@Shared(.isEditable) public var isEditable: Bool

		@Presents public var destination: Destination.State?

		public let isHighestScorePossibleEnabled: Bool

		public var gameDetailsHeader = GameDetailsHeaderNext.State()

		init() {
			@Dependency(\.featureFlags) var featureFlags
			self.isHighestScorePossibleEnabled = featureFlags.isFlagEnabled(.highestScorePossible)
		}

		var seriesGames: IdentifiedArrayOf<Game.Indexed> {
			.init(
				uniqueElements: bowlerGameIds[currentBowlerId]?
					.enumerated()
					.map { .init(id: $0.element, index: $0.offset) }
				?? []
			)
		}

		var isLocked: Bool { game?.locked == .locked }
		var isExcludedFromStatistics: Bool { game?.excludeFromStatistics == .exclude }
	}

	public enum Action: FeatureAction, ViewAction, BindableAction {
		@CasePathable
		public enum View {
			case didToggleLock(Bool)
			case didToggleExclude(Bool)
			case didTapMatchPlay
			case didTapScoring
			case didTapStrikeOut
			case didTapGear
			case didTapAlley
			case didTapSeriesStatisticsButton
			case didTapGameStatisticsButton
			case didMeasureMinimumSheetContentSize(CGSize)
			case didMeasureSectionHeaderContentSize(CGSize)
		}
		@CasePathable
		public enum Delegate {
			case didSelectLanes
			case didProceed(to: GameDetailsHeaderNext.State.NextElement)
			case didProvokeLock
			case didMeasureMinimumSheetContentSize(CGSize)
			case didMeasureSectionHeaderContentSize(CGSize)
			case didTapStrikeOut
		}
		@CasePathable
		public enum Internal {
			case gameDetailsHeader(GameDetailsHeaderNext.Action)
			case destination(PresentationAction<Destination.Action>)
		}

		case view(View)
		case delegate(Delegate)
		case `internal`(Internal)
		case binding(BindingAction<State>)
	}

	@Reducer
	public struct Destination: Reducer, Sendable {
		public enum State: Equatable {
			case lanePicker(ResourcePicker<Lane.Summary, Alley.ID>.State)
			case gearPicker(ResourcePicker<Gear.Summary, AlwaysEqual<Void>>.State)
			case matchPlay(MatchPlayEditor.State)
			case scoring(ScoringEditor.State)
			case statistics(MidGameStatisticsDetails.State)
		}
		public enum Action {
			case lanePicker(ResourcePicker<Lane.Summary, Alley.ID>.Action)
			case gearPicker(ResourcePicker<Gear.Summary, AlwaysEqual<Void>>.Action)
			case matchPlay(MatchPlayEditor.Action)
			case scoring(ScoringEditor.Action)
			case statistics(MidGameStatisticsDetails.Action)
		}

		@Dependency(GearRepository.self) var gear
		@Dependency(LanesRepository.self) var lanes

		public var body: some ReducerOf<Self> {
			Scope(state: \.lanePicker, action: \.lanePicker) {
				ResourcePicker { alley in lanes.list(alley) }
			}
			Scope(state: \.gearPicker, action: \.gearPicker) {
				ResourcePicker { _ in gear.list(ordered: .byName) }
			}
			Scope(state: \.matchPlay, action: \.matchPlay) {
				MatchPlayEditor()
			}
			Scope(state: \.scoring, action: \.scoring) {
				ScoringEditor()
			}
			Scope(state: \.statistics, action: \.statistics) {
				MidGameStatisticsDetails()
			}
		}
	}

	@Dependency(StatisticsRepository.self) var statistics
	@Dependency(\.uuid) var uuid

	public var body: some ReducerOf<Self> {
		BindingReducer()

		Scope(state: \.gameDetailsHeader, action: \.internal.gameDetailsHeader) {
			GameDetailsHeaderNext()
		}

		Reduce<State, Action> { state, action in
			switch action {
			case let .view(viewAction):
				switch viewAction {
				case .didTapStrikeOut:
					return .send(.delegate(.didTapStrikeOut))

				case .didTapSeriesStatisticsButton:
					guard let seriesId = state.game?.series.id else { return .none }
					state.destination = .statistics(
						.init(filter: .init(source: .series(seriesId)), seriesId: seriesId, games: state.seriesGames)
					)
					return .none

				case .didTapGameStatisticsButton:
					guard let gameId = state.game?.id, let seriesId = state.game?.series.id else { return .none }
					state.destination = .statistics(
						.init(filter: .init(source: .game(gameId)), seriesId: seriesId, games: state.seriesGames)
					)
					return .none

				case .didToggleLock:
					state.$game.withLock { $0?.locked.toNext() }
					return .none

				case .didToggleExclude:
					guard state.isEditable else { return .send(.delegate(.didProvokeLock)) }
					state.$game.withLock { $0?.excludeFromStatistics.toNext() }
					return .none

				case .didTapGear:
					guard state.isEditable else { return .send(.delegate(.didProvokeLock)) }
					let gear = Set(state.game?.gear.map(\.id) ?? [])
					state.destination = .gearPicker(.init(
						selected: gear,
						query: .init(()),
						showsCancelHeaderButton: false
					))
					return .none

				case .didTapAlley:
					guard state.isEditable else { return .send(.delegate(.didProvokeLock)) }
					guard let alleyId = state.game?.series.alley?.id else { return .none }
					let lanes = Set(state.game?.lanes.map(\.id) ?? [])
					state.destination = .lanePicker(.init(
						selected: lanes,
						query: alleyId,
						showsCancelHeaderButton: false
					))
					return .none

				case .didTapScoring:
					guard state.isEditable else { return .send(.delegate(.didProvokeLock)) }
					guard let game = state.game else { return .none }
					state.destination = .scoring(.init(
						scoringMethod: game.scoringMethod,
						score: game.score
					))
					return .none

				case .didTapMatchPlay:
					guard state.isEditable else { return .send(.delegate(.didProvokeLock)) }
					guard let game = state.game else { return .none }
					if let matchPlay = game.matchPlay {
						state.destination = .matchPlay(.init(matchPlay: matchPlay))
					} else {
						let matchPlay = MatchPlay.Edit(gameId: game.id, id: uuid())
						state.destination = .matchPlay(.init(matchPlay: matchPlay))
					}
					return .none

				case let .didMeasureMinimumSheetContentSize(size):
					return .send(.delegate(.didMeasureMinimumSheetContentSize(size)))

				case let .didMeasureSectionHeaderContentSize(size):
					return .send(.delegate(.didMeasureSectionHeaderContentSize(size)))
				}

			case let .internal(internalAction):
				switch internalAction {
				case let .gameDetailsHeader(.delegate(delegateAction)):
					switch delegateAction {
					case let .didProceed(next):
						return .send(.delegate(.didProceed(to: next)))
					}

				case let .destination(.presented(.matchPlay(.delegate(delegateAction)))):
					switch delegateAction {
					case let .didEditMatchPlay(matchPlay):
						state.$game.withLock { $0?.matchPlay = matchPlay }
						return .none
					}

				case let .destination(.presented(.lanePicker(.delegate(delegateAction)))):
					switch delegateAction {
					case let .didChangeSelection(lanes):
						state.$game.withLock { $0?.lanes = .init(uniqueElements: lanes) }
						return .none
					}

				case let .destination(.presented(.gearPicker(.delegate(delegateAction)))):
					switch delegateAction {
					case let .didChangeSelection(gear):
						state.$game.withLock { $0?.gear = .init(uniqueElements: gear) }
						return .none
					}

				case let .destination(.presented(.scoring(.delegate(delegateAction)))):
					switch delegateAction {
					case .didClearManualScore:
						state.$game.withLock {
							$0?.scoringMethod = .byFrame
							$0?.score = state.score?.frames.gameScore() ?? 0
						}
						return .none
					case let .didSetManualScore(score):
						state.$game.withLock {
							$0?.scoringMethod = .manual
							$0?.score = score
						}
						return .none
					}

				case .destination(.dismiss):
					switch state.destination {
					case .lanePicker:
						if (state.game?.lanes.count ?? 0) > 0 {
							return .send(.delegate(.didSelectLanes))
						} else {
							return .none
						}
					case .statistics:
						return .run { _ in await statistics.hideNewStatisticLabels() }
					case .gearPicker, .scoring, .matchPlay, .none:
						return .none
					}

				case .destination(.presented(.matchPlay(.internal))), .destination(.presented(.matchPlay(.view))),
						.destination(.presented(.scoring(.internal))),
						.destination(.presented(.scoring(.view))),
						.destination(.presented(.scoring(.binding))),
						.destination(.presented(.gearPicker(.internal))), .destination(.presented(.gearPicker(.view))),
						.destination(.presented(.lanePicker(.internal))), .destination(.presented(.lanePicker(.view))),
						.destination(.presented(.statistics(.internal))),
						.destination(.presented(.statistics(.view))),
						.destination(.presented(.statistics(.binding))),
						.destination(.presented(.statistics(.delegate(.doNothing)))),
						.gameDetailsHeader(.internal), .gameDetailsHeader(.view):
					return .none
				}

			case .delegate, .binding:
				return .none
			}
		}
		.ifLet(\.$destination, action: \.internal.destination) {
			Destination()
		}

		GameAnalyticsReducer<State, Action> { state, action in
			switch action {
			case .internal(.destination(.presented(.scoring(.delegate(.didSetManualScore))))):
				guard let gameId = state.game?.id else { return nil }
				return Analytics.Game.ManualScoreSet(gameId: gameId)
			default:
				return nil
			}
		}
	}
}

@ViewAction(for: GameDetailsNext.self)
public struct GameDetailsNextView: View {
	@Bindable public var store: StoreOf<GameDetailsNext>

	@State private var minimumSheetContentSize: CGSize = .zero
	@State private var sectionHeaderContentSize: CGSize = .zero

	public var body: some View {
		NavigationStack {
			Form {
				Section {
					GameDetailsHeaderNextView(
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

					if store.isHighestScorePossibleEnabled {
						StrikeOutSection {
							send(.didTapStrikeOut)
						}
					}

					Section {
						Toggle(
							Strings.Game.Editor.Fields.Lock.label,
							isOn: $store.isLocked.sending(\.view.didToggleLock)
						)
						.toggleStyle(.checkboxToggle)
					} header: {
						Text(Strings.other)
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
			.toolbar(.hidden)
			.gearPicker($store.scope(state: \.destination?.gearPicker, action: \.internal.destination.gearPicker))
			.lanePicker($store.scope(state: \.destination?.lanePicker, action: \.internal.destination.lanePicker))
			.matchPlay($store.scope(state: \.destination?.matchPlay, action: \.internal.destination.matchPlay))
			.scoring($store.scope(state: \.destination?.scoring, action: \.internal.destination.scoring))
			.statistics($store.scope(state: \.destination?.statistics, action: \.internal.destination.statistics))
		}
	}

	@ViewBuilder
	private func excludeFromStatisticsHelp(
		excludeLeagueFromStatistics: League.ExcludeFromStatistics,
		seriesPreBowl: Series.PreBowl,
		excludeSeriesFromStatistics: Series.ExcludeFromStatistics
	) -> some View {
		switch excludeLeagueFromStatistics {
		case .exclude:
			Text(Strings.Game.Editor.Fields.ExcludeFromStatistics.excludedWhenLeagueExcluded)
				.foregroundStyle(Asset.Colors.Warning.default)
		case .include:
			switch seriesPreBowl {
			case .preBowl:
				Text(Strings.Game.Editor.Fields.ExcludeFromStatistics.excludedWhenSeriesPreBowl)
					.foregroundStyle(Asset.Colors.Warning.default)
			case .regular:
				switch excludeSeriesFromStatistics {
				case .exclude:
					Text(Strings.Game.Editor.Fields.ExcludeFromStatistics.excludedWhenSeriesExcluded)
						.foregroundStyle(Asset.Colors.Warning.default)
				case .include:
					Text(Strings.Game.Editor.Fields.ExcludeFromStatistics.help)
				}
			}
		}
	}
}

extension View {
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
