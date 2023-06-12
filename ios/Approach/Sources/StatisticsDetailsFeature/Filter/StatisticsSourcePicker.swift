import BowlersRepositoryInterface
import ComposableArchitecture
import DateTimeLibrary
import EquatableLibrary
import FeatureActionLibrary
import GamesRepositoryInterface
import LeaguesRepositoryInterface
import ModelsLibrary
import ResourcePickerLibrary
import SeriesRepositoryInterface
import StatisticsLibrary
import StatisticsRepositoryInterface
import StringsLibrary
import SwiftUI
import SwiftUIExtensionsLibrary
import ViewsLibrary

public struct StatisticsSourcePicker: Reducer {
	public struct State: Equatable {
		public var sourceToLoad: TrackableFilter.Source?
		public var bowler: Bowler.Summary?
		public var league: League.Summary?
		public var series: Series.Summary?
		public var game: Game.Summary?
		public var isLoadingSources = false

		@PresentationState public var destination: Destination.State?

		public init(source: TrackableFilter.Source?) {
			self.sourceToLoad = source
		}
	}

	public enum Action: FeatureAction, Equatable {
		public enum ViewAction: Equatable {
			case onAppear
			case didTapBowler
			case didTapLeague
			case didTapSeries
			case didTapGame
			case didTapConfirmButton
		}
		public enum DelegateAction: Equatable {
			case didChangeSource(TrackableFilter.Source)
		}
		public enum InternalAction: Equatable {
			case didLoadSources(TaskResult<TrackableFilter.Sources?>)
			case destination(PresentationAction<Destination.Action>)
		}

		case view(ViewAction)
		case delegate(DelegateAction)
		case `internal`(InternalAction)
	}

	public struct Destination: Reducer {
		public enum State: Equatable {
			case bowlerPicker(ResourcePicker<Bowler.Summary, AlwaysEqual<Void>>.State)
			case leaguePicker(ResourcePicker<League.Summary, Bowler.ID>.State)
			case seriesPicker(ResourcePicker<Series.Summary, League.ID>.State)
			case gamePicker(ResourcePicker<Game.Summary, Series.ID>.State)
		}

		public enum Action: Equatable {
			case bowlerPicker(ResourcePicker<Bowler.Summary, AlwaysEqual<Void>>.Action)
			case leaguePicker(ResourcePicker<League.Summary, Bowler.ID>.Action)
			case seriesPicker(ResourcePicker<Series.Summary, League.ID>.Action)
			case gamePicker(ResourcePicker<Game.Summary, Series.ID>.Action)
		}

		@Dependency(\.bowlers) var bowlers
		@Dependency(\.games) var games
		@Dependency(\.leagues) var leagues
		@Dependency(\.series) var series

		public var body: some ReducerOf<Self> {
			Scope(state: /State.bowlerPicker, action: /Action.bowlerPicker) {
				ResourcePicker { _ in bowlers.pickable() }
			}
			Scope(state: /State.leaguePicker, action: /Action.leaguePicker) {
				ResourcePicker { bowler in leagues.pickable(bowledBy: bowler, ordering: .byName) }
			}
			Scope(state: /State.seriesPicker, action: /Action.seriesPicker) {
				ResourcePicker { league in series.list(bowledIn: league, ordering: .byDate) }
			}
			Scope(state: /State.gamePicker, action: /Action.gamePicker) {
				ResourcePicker { series in games.seriesGamesSummaries(forId: series, ordering: .byIndex) }
			}
		}
	}

	public init() {}

	@Dependency(\.dismiss) var dismiss
	@Dependency(\.statistics) var statistics

	public var body: some ReducerOf<Self> {
		Reduce<State, Action> { state, action in
			switch action {
			case let .view(viewAction):
				switch viewAction {
				case .onAppear:
					guard let source = state.sourceToLoad else { return .none }
					state.sourceToLoad = nil
					state.isLoadingSources = true
					return .run { send in
						await send(.internal(.didLoadSources(TaskResult {
							try await statistics.loadSources(source)
						})))
					}

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

				case .didTapConfirmButton:
					if let source = state.source {
						return .concatenate(
							.send(.delegate(.didChangeSource(source))),
							.run { _ in await dismiss() }
						)
					} else {
						return .run { _ in await dismiss() }
					}
				}

			case let .internal(internalAction):
				switch internalAction {
				case let .didLoadSources(.success(sources)):
					state.isLoadingSources = false
					state.bowler = sources?.bowler
					state.league = sources?.league
					state.series = sources?.series
					state.game = sources?.game
					return .none

				case .didLoadSources(.failure):
					// TODO: handle error loading sources
					return .none

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
		.ifLet(\.$destination, action: /Action.internal..Action.InternalAction.destination) {
			Destination()
		}
	}
}

extension StatisticsSourcePicker.State {
	var source: TrackableFilter.Source? {
		if let game {
			return .game(game.id)
		} else if let series {
			return .series(series.id)
		} else if let league {
			return .league(league.id)
		} else if let bowler {
			return .bowler(bowler.id)
		} else {
			return nil
		}
	}
}

// MARK: - View

public struct StatisticsSourcePickerView: View {
	let store: StoreOf<StatisticsSourcePicker>

	struct ViewState: Equatable {
		let selectedBowlerName: String?
		let selectedLeagueName: String?
		let selectedSeriesDate: String?
		let selectedGameIndex: String?

		let isFilterApplyable: Bool

		let isShowingLeaguePicker: Bool
		let isShowingSeriesPicker: Bool
		let isShowingGamePicker: Bool

		let isLoadingSources: Bool

		init(state: StatisticsSourcePicker.State) {
			self.isFilterApplyable = state.source != nil
			self.selectedBowlerName = state.bowler?.name
			self.selectedLeagueName = state.league?.name
			self.selectedSeriesDate = state.series?.date.longFormat
			if let gameIndex = state.game?.index {
				self.selectedGameIndex = Strings.Game.titleWithOrdinal(gameIndex + 1)
			} else {
				self.selectedGameIndex = nil
			}

			self.isShowingLeaguePicker = selectedBowlerName != nil
			self.isShowingSeriesPicker = selectedLeagueName != nil
			self.isShowingGamePicker = selectedSeriesDate != nil

			self.isLoadingSources = state.isLoadingSources
		}
	}

	enum ViewAction {
		case onAppear
		case didTapBowler
		case didTapLeague
		case didTapSeries
		case didTapGame
		case didTapConfirmButton
	}

	public init(store: StoreOf<StatisticsSourcePicker>) {
		self.store = store
	}

	public var body: some View {
		WithViewStore(store, observe: ViewState.init, send: StatisticsSourcePicker.Action.init) { viewStore in
			List {
				if viewStore.isLoadingSources {
					ListProgressView()
				} else {
					Section {
						Button { viewStore.send(.didTapBowler) } label: {
							LabeledContent(Strings.Bowler.title, value: viewStore.selectedBowlerName ?? Strings.none)
						}
						.buttonStyle(.navigation)

						if viewStore.isShowingLeaguePicker {
							Button { viewStore.send(.didTapLeague) } label: {
								LabeledContent(Strings.League.title, value: viewStore.selectedLeagueName ?? Strings.none)
							}
							.buttonStyle(.navigation)
						}

						if viewStore.isShowingSeriesPicker {
							Button { viewStore.send(.didTapSeries) } label: {
								LabeledContent(Strings.Series.title, value: viewStore.selectedSeriesDate ?? Strings.none)
							}
							.buttonStyle(.navigation)
						}

						if viewStore.isShowingGamePicker {
							Button { viewStore.send(.didTapGame) } label: {
								LabeledContent(Strings.Game.title, value: viewStore.selectedGameIndex ?? Strings.none)
							}
							.buttonStyle(.navigation)
						}
					}
				}
			}
			.toolbar {
				ToolbarItem(placement: .navigationBarTrailing) {
					Button(Strings.Action.apply) { viewStore.send(.didTapConfirmButton) }
						.disabled(!viewStore.isFilterApplyable)
				}
			}
			.navigationTitle(Strings.Statistics.Filter.title)
			.onAppear { viewStore.send(.onAppear) }
		}
		.navigationDestination(
			store: store.scope(state: \.$destination, action: { .internal(.destination($0)) }),
			state: /StatisticsSourcePicker.Destination.State.bowlerPicker,
			action: StatisticsSourcePicker.Destination.Action.bowlerPicker
		) { store in
			ResourcePickerView(store: store) { bowler in
				Text(bowler.name)
			}
		}
		.navigationDestination(
			store: store.scope(state: \.$destination, action: { .internal(.destination($0)) }),
			state: /StatisticsSourcePicker.Destination.State.leaguePicker,
			action: StatisticsSourcePicker.Destination.Action.leaguePicker
		) { store in
			ResourcePickerView(store: store) { league in
				Text(league.name)
			}
		}
		.navigationDestination(
			store: store.scope(state: \.$destination, action: { .internal(.destination($0)) }),
			state: /StatisticsSourcePicker.Destination.State.seriesPicker,
			action: StatisticsSourcePicker.Destination.Action.seriesPicker
		) { store in
			ResourcePickerView(store: store) { series in
				Text(series.date.longFormat)
			}
		}
		.navigationDestination(
			store: store.scope(state: \.$destination, action: { .internal(.destination($0)) }),
			state: /StatisticsSourcePicker.Destination.State.gamePicker,
			action: StatisticsSourcePicker.Destination.Action.gamePicker
		) { store in
			ResourcePickerView(store: store) { game in
				Text(Strings.Game.titleWithOrdinal(game.index + 1))
			}
		}
	}
}

extension StatisticsSourcePicker.Action {
	init(action: StatisticsSourcePickerView.ViewAction) {
		switch action {
		case .didTapConfirmButton:
			self = .view(.didTapConfirmButton)
		case .didTapBowler:
			self = .view(.didTapBowler)
		case .didTapLeague:
			self = .view(.didTapLeague)
		case .didTapSeries:
			self = .view(.didTapSeries)
		case .didTapGame:
			self = .view(.didTapGame)
		case .onAppear:
			self = .view(.onAppear)
		}
	}
}
