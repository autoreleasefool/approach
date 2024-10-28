import AnalyticsServiceInterface
import AssetsLibrary
import ComposableArchitecture
import ComposableExtensionsLibrary
import DateTimeLibrary
import EquatablePackageLibrary
import FeatureActionLibrary
import FeatureFlagsLibrary
import GamesRepositoryInterface
import GearRepositoryInterface
import LanesRepositoryInterface
import MatchPlaysRepositoryInterface
import ModelsLibrary
import ModelsViewsLibrary
import PickableModelsLibrary
import ResourcePickerLibrary
import StatisticsDetailsFeature
import StatisticsRepositoryInterface
import StringsLibrary
import SwiftUI
import ViewsLibrary

@Reducer
public struct GameDetails: Reducer, Sendable {
	@ObservableState
	public struct State: Equatable {
		public var gameId: Game.ID
		public var game: Game.Edit?
		public var seriesGames: IdentifiedArrayOf<Game.Indexed>

		public var nextHeaderElement: GameDetailsHeader.State.NextElement?
		public var shouldHeaderShimmer: Bool

		public var gameDetailsHeader: GameDetailsHeader.State = .init()

		var isLocked: Bool { game?.locked == .locked }
		var isExcludedFromStatistics: Bool { game?.excludeFromStatistics == .exclude }

		@Presents public var destination: Destination.State?

		var isEditable: Bool { game?.locked != .locked }

		public let isHighestScorePossibleEnabled: Bool

		init(
			gameId: Game.ID,
			seriesGames: IdentifiedArrayOf<Game.Indexed>,
			nextHeaderElement: GameDetailsHeader.State.NextElement?,
			didChangeBowler: Bool
		) {
			self.gameId = gameId
			self.seriesGames = seriesGames
			self.nextHeaderElement = nextHeaderElement
			self.shouldHeaderShimmer = didChangeBowler

			@Dependency(\.featureFlags) var featureFlags
			self.isHighestScorePossibleEnabled = featureFlags.isFlagEnabled(.highestScorePossible)

			syncHeader()
		}
	}

	public enum Action: FeatureAction, ViewAction {
		@CasePathable
		public enum View {
			case task
			case onAppear
			case didFirstAppear
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
			case didProceed(to: GameDetailsHeader.State.NextElement)
			case didEditMatchPlay(Result<MatchPlay.Edit?, Error>)
			case didClearManualScore
			case didProvokeLock
			case didEditGame(Game.Edit?)
			case didMeasureMinimumSheetContentSize(CGSize)
			case didMeasureSectionHeaderContentSize(CGSize)
			case didTapStrikeOut
		}
		@CasePathable
		public enum Internal {
			case refreshObservation
			case didLoadGame(Result<Game.Edit?, Error>)
			case gameDetailsHeader(GameDetailsHeader.Action)
			case destination(PresentationAction<Destination.Action>)
		}

		case view(View)
		case delegate(Delegate)
		case `internal`(Internal)
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

	enum CancelID: Sendable {
		case saveMatchPlay
		case observation
	}

	@Dependency(GamesRepository.self) var games
	@Dependency(MatchPlaysRepository.self) var matchPlays
	@Dependency(StatisticsRepository.self) var statistics
	@Dependency(\.uuid) var uuid

	public var body: some ReducerOf<Self> {
		Scope(state: \.gameDetailsHeader, action: \.internal.gameDetailsHeader) {
			GameDetailsHeader()
		}

		Reduce<State, Action> { state, action in
			switch action {
			case let .view(viewAction):
				switch viewAction {
				case .onAppear:
					return observeGame(gameId: state.gameId)

				case .task:
					return .cancelling(id: CancelID.observation)

				case .didFirstAppear:
					return state.startShimmer()

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
					state.game?.locked.toNext()
					return .send(.delegate(.didEditGame(state.game)))

				case .didToggleExclude:
					guard state.isEditable else { return .send(.delegate(.didProvokeLock)) }
					state.game?.excludeFromStatistics.toNext()
					return .send(.delegate(.didEditGame(state.game)))

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
						return .none
					} else {
						let matchPlay = MatchPlay.Edit(gameId: game.id, id: uuid())
						state.game?.matchPlay = matchPlay
						state.destination = .matchPlay(.init(matchPlay: matchPlay))
						return createMatchPlay(matchPlay)
					}

				case let .didMeasureMinimumSheetContentSize(size):
					return .send(.delegate(.didMeasureMinimumSheetContentSize(size)))

				case let .didMeasureSectionHeaderContentSize(size):
					return .send(.delegate(.didMeasureSectionHeaderContentSize(size)))
				}

			case let .internal(internalAction):
				switch internalAction {
				case .refreshObservation:
					return observeGame(gameId: state.gameId)

				case let .didLoadGame(.success(game)):
					guard let game, game.id == state.gameId else { return .none }
					state.game = game
					state.syncHeader()
					return .none

				case .didLoadGame(.failure):
					return .none

				case .destination(.presented(.statistics(.delegate(.doNothing)))):
					return .none

				case let .destination(.presented(.lanePicker(.delegate(delegateAction)))):
					switch delegateAction {
					case let .didChangeSelection(lanes):
						state.game?.lanes = .init(uniqueElements: lanes)
						return .send(.delegate(.didEditGame(state.game)))
					}

				case let .destination(.presented(.gearPicker(.delegate(delegateAction)))):
					switch delegateAction {
					case let .didChangeSelection(gear):
						state.game?.gear = .init(uniqueElements: gear)
						return .send(.delegate(.didEditGame(state.game)))
					}

				case let .destination(.presented(.matchPlay(.delegate(delegateAction)))):
					switch delegateAction {
					case let .didEditMatchPlay(matchPlay):
						if matchPlay == nil {
							return deleteMatchPlay(state: &state)
						} else {
							state.game?.matchPlay = matchPlay
							return .send(.delegate(.didEditMatchPlay(.success(state.game?.matchPlay))))
						}
					}

				case let .destination(.presented(.scoring(.delegate(delegateAction)))):
					switch delegateAction {
					case .didClearManualScore:
						state.game?.scoringMethod = .byFrame
						return .send(.delegate(.didClearManualScore))
					case let .didSetManualScore(score):
						state.game?.scoringMethod = .manual
						state.game?.score = score
						return .send(.delegate(.didEditGame(state.game)))
					}

				case let .gameDetailsHeader(.delegate(delegateAction)):
					switch delegateAction {
					case let .didProceed(next):
						return .send(.delegate(.didProceed(to: next)))
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
					case .gearPicker, .matchPlay, .scoring, .none:
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
						.gameDetailsHeader(.internal), .gameDetailsHeader(.view):
					return .none
				}

			case .delegate:
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

		BreadcrumbReducer<State, Action> { _, action in
			switch action {
			case .view(.onAppear): return .navigationBreadcrumb(type(of: self))
			default: return nil
			}
		}

		ErrorHandlerReducer<State, Action> { _, action in
			switch action {
			case let .internal(.didLoadGame(.failure(error))):
				return error
			default:
				return nil
			}
		}
	}
}
