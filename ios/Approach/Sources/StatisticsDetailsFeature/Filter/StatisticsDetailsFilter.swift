import BowlersRepositoryInterface
import ComposableArchitecture
import EquatableLibrary
import FeatureActionLibrary
import GamesRepositoryInterface
import LeaguesRepositoryInterface
import ModelsLibrary
import ResourcePickerLibrary
import SeriesRepositoryInterface
import StatisticsLibrary
import StringsLibrary

public struct StatisticsDetailsFilter: Reducer {
	public struct State: Equatable {
		public var bowler: Bowler.Summary? = .init(id: .init(uuidString: "A08F876B-0100-0000-708D-876B01000000")!, name: "Joseph")
		// TODO: can be replaced with League.Summary
		public var league: League.List?
		public var series: Series.Summary?
		public var game: Game.List?

		@PresentationState public var destination: Destination.State?

		public init() {}
	}

	public enum Action: FeatureAction, Equatable {
		public enum ViewAction: Equatable {
			case didTapBowler
			case didTapLeague
			case didTapSeries
			case didTapGame
		}
		public enum DelegateAction: Equatable {}
		public enum InternalAction: Equatable {
			case destination(PresentationAction<Destination.Action>)
		}

		case view(ViewAction)
		case delegate(DelegateAction)
		case `internal`(InternalAction)
	}

	public struct Destination: Reducer {
		public enum State: Equatable {
			case bowlerPicker(ResourcePicker<Bowler.Summary, AlwaysEqual<Void>>.State)
			case leaguePicker(ResourcePicker<League.List, Bowler.ID>.State)
			case seriesPicker(ResourcePicker<Series.Summary, League.ID>.State)
			case gamePicker(ResourcePicker<Game.List, Series.ID>.State)
		}

		public enum Action: Equatable {
			case bowlerPicker(ResourcePicker<Bowler.Summary, AlwaysEqual<Void>>.Action)
			case leaguePicker(ResourcePicker<League.List, Bowler.ID>.Action)
			case seriesPicker(ResourcePicker<Series.Summary, League.ID>.Action)
			case gamePicker(ResourcePicker<Game.List, Series.ID>.Action)
		}

		@Dependency(\.bowlers) var bowlers
		@Dependency(\.leagues) var leagues
		@Dependency(\.series) var series
		@Dependency(\.games) var games

		public var body: some ReducerOf<Self> {
			Scope(state: /State.bowlerPicker, action: /Action.bowlerPicker) {
				ResourcePicker { _ in bowlers.pickable() }
			}
			Scope(state: /State.leaguePicker, action: /Action.leaguePicker) {
				ResourcePicker { bowler in leagues.list(bowledBy: bowler, ordering: .byName) }
			}
			Scope(state: /State.seriesPicker, action: /Action.seriesPicker) {
				ResourcePicker { league in series.list(bowledIn: league, ordering: .byDate) }
			}
			Scope(state: /State.gamePicker, action: /Action.gamePicker) {
				ResourcePicker { series in games.seriesGames(forId: series, ordering: .byIndex) }
			}
		}
	}

	public init() {}

	public var body: some ReducerOf<Self> {
		Reduce<State, Action> { state, action in
			switch action {
			case let .view(viewAction):
				switch viewAction {
				case .didTapBowler:
					state.destination = .bowlerPicker(.init(
						selected: Set([state.bowler?.id].compactMap { $0 }),
						query: .init(()),
						limit: 1,
						showsCancelHeaderButton: false
					))
					return .none

				case .didTapLeague:
					guard let bowler = state.bowler else { return .none }
					state.destination = .leaguePicker(.init(
						selected: Set([state.league?.id].compactMap { $0 }),
						query: bowler.id,
						limit: 1,
						showsCancelHeaderButton: false
					))
					return .none

				case .didTapSeries:
					guard let league = state.league else { return .none }
					state.destination = .seriesPicker(.init(
						selected: Set([state.series?.id].compactMap { $0 }),
						query: league.id,
						limit: 1,
						showsCancelHeaderButton: false
					))
					return .none

				case .didTapGame:
					guard let series = state.series else { return .none }
					state.destination = .seriesPicker(.init(
						selected: Set([state.series?.id].compactMap { $0 }),
						query: series.id,
						limit: 1,
						showsCancelHeaderButton: false
					))
					return .none
				}

			case let .internal(internalAction):
				switch internalAction {
				case let .destination(.presented(.bowlerPicker(.delegate(delegateAction)))):
					switch delegateAction {
					case let .didChangeSelection(bowler):
						state.bowler = bowler.first
						state.league = nil
						state.series = nil
						state.game = nil
						return .none
					}

				case let .destination(.presented(.leaguePicker(.delegate(delegateAction)))):
					switch delegateAction {
					case let .didChangeSelection(league):
						state.league = league.first
						state.series = nil
						state.game = nil
						return .none
					}

				case let .destination(.presented(.seriesPicker(.delegate(delegateAction)))):
					switch delegateAction {
					case let .didChangeSelection(series):
						state.series = series.first
						state.game = nil
						return .none
					}

				case let .destination(.presented(.gamePicker(.delegate(delegateAction)))):
					switch delegateAction {
					case let .didChangeSelection(game):
						state.game = game.first
						return .none
					}

				case .destination(.dismiss),
						.destination(.presented(.bowlerPicker(.internal))),
						.destination(.presented(.bowlerPicker(.view))),
						.destination(.presented(.leaguePicker(.internal))),
						.destination(.presented(.leaguePicker(.view))),
						.destination(.presented(.seriesPicker(.internal))),
						.destination(.presented(.seriesPicker(.view))),
						.destination(.presented(.gamePicker(.internal))),
						.destination(.presented(.gamePicker(.view))):
					return .none
				}

			case .delegate:
				return .none
			}
		}
	}
}

extension Bowler.Summary: PickableResource {
	static public func pickableModelName(forCount count: Int) -> String {
		count == 1 ? Strings.Bowler.title : Strings.Bowler.List.title
	}
}

extension League.List: PickableResource {
	static public func pickableModelName(forCount count: Int) -> String {
		count == 1 ? Strings.League.title : Strings.League.List.title
	}
}

extension Series.Summary: PickableResource {
	static public func pickableModelName(forCount count: Int) -> String {
		count == 1 ? Strings.Series.title : Strings.Series.List.title
	}
}

extension Game.List: PickableResource {
	static public func pickableModelName(forCount count: Int) -> String {
		count == 1 ? Strings.Game.title : Strings.Game.List.title
	}
}
