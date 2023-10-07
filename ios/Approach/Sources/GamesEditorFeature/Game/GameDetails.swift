import AnalyticsServiceInterface
import AssetsLibrary
import ComposableArchitecture
import DateTimeLibrary
import EquatableLibrary
import ExtensionsLibrary
import FeatureActionLibrary
import FeatureFlagsLibrary
import FeatureFlagsServiceInterface
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

// swiftlint:disable:next type_body_length
public struct GameDetails: Reducer {
	public struct State: Equatable {
		public var gameId: Game.ID
		public var game: Game.Edit?
		public var seriesGames: IdentifiedArrayOf<Game.Indexed>

		public var nextHeaderElement: GameDetailsHeader.State.NextElement?
		public var shouldHeaderShimmer: Bool

		public let isGearEnabled: Bool
		public let isOpponentsEnabled: Bool

		public var _gameDetailsHeader: GameDetailsHeader.State = .init()

		@PresentationState public var destination: Destination.State?

		var isEditable: Bool { game?.locked != .locked }

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
			self.isGearEnabled = featureFlags.isEnabled(.gear)
			self.isOpponentsEnabled = featureFlags.isEnabled(.opponents)
		}
	}

	public enum Action: FeatureAction, Equatable {
		public enum ViewAction: Equatable {
			case didStartTask
			case didToggleLock
			case didToggleExclude
			case didTapMatchPlay
			case didTapScoring
			case didTapGear
			case didTapAlley
			case didTapSeriesStatisticsButton
			case didTapGameStatisticsButton
			case didMeasureMinimumSheetContentSize(CGSize)
			case didMeasureSectionHeaderContentSize(CGSize)
		}
		public enum DelegateAction: Equatable {
			case didSelectLanes
			case didProceed(to: GameDetailsHeader.State.NextElement)
			case didEditMatchPlay(TaskResult<MatchPlay.Edit?>)
			case didClearManualScore
			case didProvokeLock
			case didEditGame(Game.Edit?)
			case didMeasureMinimumSheetContentSize(CGSize)
			case didMeasureSectionHeaderContentSize(CGSize)
		}
		public enum InternalAction: Equatable {
			case didLoadGame(TaskResult<Game.Edit?>)
			case gameDetailsHeader(GameDetailsHeader.Action)
			case destination(PresentationAction<Destination.Action>)
		}

		case view(ViewAction)
		case delegate(DelegateAction)
		case `internal`(InternalAction)
	}

	public struct Destination: Reducer {
		public enum State: Equatable {
			case lanePicker(ResourcePicker<Lane.Summary, Alley.ID>.State)
			case gearPicker(ResourcePicker<Gear.Summary, AlwaysEqual<Void>>.State)
			case matchPlay(MatchPlayEditor.State)
			case scoring(ScoringEditor.State)
			case statistics(MidGameStatisticsDetails.State)
		}
		public enum Action: Equatable {
			case lanePicker(ResourcePicker<Lane.Summary, Alley.ID>.Action)
			case gearPicker(ResourcePicker<Gear.Summary, AlwaysEqual<Void>>.Action)
			case matchPlay(MatchPlayEditor.Action)
			case scoring(ScoringEditor.Action)
			case statistics(MidGameStatisticsDetails.Action)
		}

		@Dependency(\.gear) var gear
		@Dependency(\.lanes) var lanes

		public var body: some ReducerOf<Self> {
			Scope(state: /State.lanePicker, action: /Action.lanePicker) {
				ResourcePicker { alley in lanes.list(alley) }
			}
			Scope(state: /State.gearPicker, action: /Action.gearPicker) {
				ResourcePicker { _ in gear.list(ordered: .byName) }
			}
			Scope(state: /State.matchPlay, action: /Action.matchPlay) {
				MatchPlayEditor()
			}
			Scope(state: /State.scoring, action: /Action.scoring) {
				ScoringEditor()
			}
			Scope(state: /State.statistics, action: /Action.statistics) {
				MidGameStatisticsDetails()
			}
		}
	}

	enum CancelID {
		case saveMatchPlay
		case observation
	}

	@Dependency(\.games) var games
	@Dependency(\.matchPlays) var matchPlays
	@Dependency(\.statistics) var statistics
	@Dependency(\.uuid) var uuid

	public var body: some ReducerOf<Self> {
		Scope(state: \.gameDetailsHeader, action: /Action.internal..Action.InternalAction.gameDetailsHeader) {
			GameDetailsHeader()
		}

		Reduce<State, Action> { state, action in
			switch action {
			case let .view(viewAction):
				switch viewAction {
				case .didStartTask:
					return .merge(
						.run { [gameId = state.gameId] send in
							for try await game in games.observe(gameId) {
								await send(.internal(.didLoadGame(.success(game))))
							}
						} catch: { error, send in
							await send(.internal(.didLoadGame(.failure(error))))
						}
						.cancellable(id: CancelID.observation, cancelInFlight: true),
						state.startShimmer()
					)

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
				case let .didLoadGame(.success(game)):
					guard let game, game.id == state.gameId else { return .none }
					state.game = game
					return .none

				case .didLoadGame(.failure):
					// TODO: Handle error observing game -- not actually sure we need to care about the error here
					return .none

				case let .destination(.presented(.statistics(.delegate(delegateAction)))):
					switch delegateAction {
					case .never:
						return .none
					}

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
						.destination(.presented(.scoring(.internal))), .destination(.presented(.scoring(.view))),
						.destination(.presented(.gearPicker(.internal))), .destination(.presented(.gearPicker(.view))),
						.destination(.presented(.lanePicker(.internal))), .destination(.presented(.lanePicker(.view))),
						.destination(.presented(.statistics(.internal))), .destination(.presented(.statistics(.view))),
						.gameDetailsHeader(.internal), .gameDetailsHeader(.view):
					return .none

				}

			case .delegate:
				return .none
			}
		}
		.ifLet(\.$destination, action: /Action.internal..Action.InternalAction.destination) {
			Destination()
		}

		AnalyticsReducer<State, Action> { state, action in
			switch action {
			case .internal(.destination(.presented(.scoring(.delegate(.didSetManualScore))))):
				guard let gameId = state.game?.id else { return nil }
				return Analytics.Game.ManualScoreSet(gameId: gameId)
			default:
				return nil
			}
		}
	}

	private func createMatchPlay(_ matchPlay: MatchPlay.Edit) -> Effect<Action> {
		return .run { send in
			await send(.delegate(.didEditMatchPlay(TaskResult {
				try await matchPlays.create(matchPlay)
				return matchPlay
			})))
		}.cancellable(id: CancelID.saveMatchPlay)
	}

	private func deleteMatchPlay(state: inout State) -> Effect<Action> {
		guard let matchPlay = state.game?.matchPlay else { return .none }
		state.game?.matchPlay = nil
		return .concatenate(
			.cancel(id: CancelID.saveMatchPlay),
			.run { send in
				await send(.delegate(.didEditMatchPlay(TaskResult {
					try await matchPlays.delete(matchPlay.id)
					return nil
				})))
			}
		)
	}
}

extension GameDetails.State {
	mutating func loadGameDetails(forGameId: Game.ID, didChangeBowler: Bool) -> Effect<GameDetails.Action> {
		gameId = forGameId
		shouldHeaderShimmer = didChangeBowler
		return .send(.view(.didStartTask))
	}

	mutating func startShimmer() -> Effect<GameDetails.Action> {
		guard shouldHeaderShimmer else { return .none }
		shouldHeaderShimmer = false
		return _gameDetailsHeader.shouldStartShimmering()
			.map { .internal(.gameDetailsHeader($0)) }
	}
}

extension GameDetails.State {
	var gameDetailsHeader: GameDetailsHeader.State {
		get {
			var gameDetailsHeader = _gameDetailsHeader
			gameDetailsHeader.currentBowlerName = game?.bowler.name ?? ""
			gameDetailsHeader.currentLeagueName = game?.league.name ?? ""
			gameDetailsHeader.next = nextHeaderElement
			return gameDetailsHeader
		}
		set {
			_gameDetailsHeader = newValue
		}
	}
}
