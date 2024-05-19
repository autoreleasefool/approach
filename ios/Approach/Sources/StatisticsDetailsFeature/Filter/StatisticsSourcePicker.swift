import AnalyticsServiceInterface
import BowlersRepositoryInterface
import ComposableArchitecture
import DateTimeLibrary
import EquatablePackageLibrary
import ErrorsFeature
import ExtensionsPackageLibrary
import FeatureActionLibrary
import GamesRepositoryInterface
import LeaguesRepositoryInterface
import ModelsLibrary
import ModelsViewsLibrary
import PickableModelsLibrary
import ResourcePickerLibrary
import SeriesRepositoryInterface
import StatisticsLibrary
import StatisticsRepositoryInterface
import StringsLibrary
import SwiftUI
import SwiftUIExtensionsPackageLibrary
import ViewsLibrary

@Reducer
public struct StatisticsSourcePicker: Reducer {
	@ObservableState
	public struct State: Equatable {
		public var sourceToLoad: TrackableFilter.Source?

		public var bowler: Bowler.Summary?
		public var league: League.Summary?
		public var series: Series.Summary?
		public var game: Game.Summary?
		public var isLoadingSources = false

		public var errors: Errors<ErrorID>.State = .init()

		var isFilterApplyable: Bool { source != nil }
		var isShowingLeaguePicker: Bool { bowler?.name != nil }
		var isShowingSeriesPicker: Bool { league?.name != nil }
		var isShowingGamePicker: Bool { series?.date != nil }
		var gameIndex: String? {
			if let gameIndex = game?.index {
				Strings.Game.titleWithOrdinal(gameIndex + 1)
			} else {
				nil
			}
		}

		@Presents public var destination: Destination.State?

		public init(source: TrackableFilter.Source?) {
			self.sourceToLoad = source
		}
	}

	public enum Action: FeatureAction, ViewAction {
		@CasePathable public enum View {
			case didFirstAppear
			case didTapBowler
			case didTapLeague
			case didTapSeries
			case didTapGame
			case didTapConfirmButton
		}
		@CasePathable public enum Delegate {
			case didChangeSource(TrackableFilter.Source)
		}
		@CasePathable public enum Internal {
			case didLoadSources(Result<TrackableFilter.Sources?, Error>)
			case destination(PresentationAction<Destination.Action>)
			case errors(Errors<ErrorID>.Action)
		}

		case view(View)
		case delegate(Delegate)
		case `internal`(Internal)
	}

	@Reducer
	public struct Destination: Reducer {
		public enum State: Equatable {
			case bowlerPicker(ResourcePicker<Bowler.Summary, AlwaysEqual<Void>>.State)
			case leaguePicker(ResourcePicker<League.Summary, Bowler.ID>.State)
			case seriesPicker(ResourcePicker<Series.Summary, League.ID>.State)
			case gamePicker(ResourcePicker<Game.Summary, Series.ID>.State)
		}

		public enum Action {
			case bowlerPicker(ResourcePicker<Bowler.Summary, AlwaysEqual<Void>>.Action)
			case leaguePicker(ResourcePicker<League.Summary, Bowler.ID>.Action)
			case seriesPicker(ResourcePicker<Series.Summary, League.ID>.Action)
			case gamePicker(ResourcePicker<Game.Summary, Series.ID>.Action)
		}

		@Dependency(BowlersRepository.self) var bowlers
		@Dependency(GamesRepository.self) var games
		@Dependency(LeaguesRepository.self) var leagues
		@Dependency(SeriesRepository.self) var series

		public var body: some ReducerOf<Self> {
			Scope(state: \.bowlerPicker, action: \.bowlerPicker) {
				ResourcePicker { _ in bowlers.pickable() }
			}
			Scope(state: \.leaguePicker, action: \.leaguePicker) {
				ResourcePicker { bowler in leagues.pickable(bowledBy: bowler, ordering: .byName) }
			}
			Scope(state: \.seriesPicker, action: \.seriesPicker) {
				ResourcePicker { league in series.summaries(bowledIn: league) }
			}
			Scope(state: \.gamePicker, action: \.gamePicker) {
				ResourcePicker { series in games.seriesGamesSummaries(forId: series, ordering: .byIndex) }
			}
		}
	}

	public enum ErrorID: Hashable {
		case failedToLoadSources
	}

	public init() {}

	@Dependency(\.dismiss) var dismiss
	@Dependency(StatisticsRepository.self) var statistics

	public var body: some ReducerOf<Self> {
		Scope(state: \.errors, action: \.internal.errors) {
			Errors()
		}

		Reduce<State, Action> { state, action in
			switch action {
			case let .view(viewAction):
				switch viewAction {
				case .didFirstAppear:
					state.isLoadingSources = true
					if let source = state.sourceToLoad {
						return .run { send in
							await send(.internal(.didLoadSources(Result {
								try await statistics.loadSources(source)
							})))
						}
					} else {
						return .run { send in
							await send(.internal(.didLoadSources(Result {
								try await statistics.loadDefaultSources()
							})))
						}
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
					state.destination = .gamePicker(.init(
						selected: Set([state.game?.id].compactMap { $0 }),
						query: series.id,
						limit: 1,
						showsCancelHeaderButton: false
					))
					return .none

				case .didTapConfirmButton:
					if let source = state.source {
						return .concatenate(
							.run { _ in await statistics.saveLastUsedSource(source) },
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

				case let .didLoadSources(.failure(error)):
					return state.errors
						.enqueue(.failedToLoadSources, thrownError: error, toastMessage: Strings.Error.Toast.failedToLoad)
						.map { .internal(.errors($0)) }

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

				case .errors(.delegate(.doNothing)):
					return .none

				case .destination(.dismiss),
						.destination(.presented(.bowlerPicker(.internal))),
						.destination(.presented(.bowlerPicker(.view))),
						.destination(.presented(.leaguePicker(.internal))),
						.destination(.presented(.leaguePicker(.view))),
						.destination(.presented(.seriesPicker(.internal))),
						.destination(.presented(.seriesPicker(.view))),
						.destination(.presented(.gamePicker(.internal))),
						.destination(.presented(.gamePicker(.view))),
						.errors(.internal), .errors(.view):
					return .none
				}

			case .delegate:
				return .none
			}
		}
		.ifLet(\.$destination, action: \.internal.destination) {
			Destination()
		}

		ErrorHandlerReducer<State, Action> { _, action in
			switch action {
			case let .internal(.didLoadSources(.failure(error))):
				return error
			default:
				return nil
			}
		}
	}
}

// MARK: - View

@ViewAction(for: StatisticsSourcePicker.self)
public struct StatisticsSourcePickerView: View {
	@Bindable public var store: StoreOf<StatisticsSourcePicker>

	public init(store: StoreOf<StatisticsSourcePicker>) {
		self.store = store
	}

	public var body: some View {
		List {
			if store.isLoadingSources {
				ListProgressView()
			} else {
				Section {
					Button { send(.didTapBowler) } label: {
						LabeledContent(Strings.Bowler.title, value: store.bowler?.name ?? Strings.none)
					}
					.buttonStyle(.navigation)

					if store.isShowingLeaguePicker {
						Button { send(.didTapLeague) } label: {
							LabeledContent(Strings.League.title, value: store.league?.name ?? Strings.none)
						}
						.buttonStyle(.navigation)
					}

					if store.isShowingSeriesPicker {
						Button { send(.didTapSeries) } label: {
							LabeledContent(Strings.Series.title, value: store.series?.date.longFormat ?? Strings.none)
						}
						.buttonStyle(.navigation)
					}

					if store.isShowingGamePicker {
						Button { send(.didTapGame) } label: {
							LabeledContent(Strings.Game.title, value: store.gameIndex ?? Strings.none)
						}
						.buttonStyle(.navigation)
					}
				}
			}
		}
		.toolbar {
			ToolbarItem(placement: .navigationBarTrailing) {
				Button(Strings.Action.apply) { send(.didTapConfirmButton) }
					.disabled(!store.isFilterApplyable)
			}
		}
		.navigationTitle(Strings.Statistics.Filter.title)
		.onFirstAppear { send(.didFirstAppear) }
		.errors(store: store.scope(state: \.errors, action: \.internal.errors))
		.bowlerPicker($store.scope(state: \.destination?.bowlerPicker, action: \.internal.destination.bowlerPicker))
		.leaguePicker($store.scope(state: \.destination?.leaguePicker, action: \.internal.destination.leaguePicker))
		.seriesPicker($store.scope(state: \.destination?.seriesPicker, action: \.internal.destination.seriesPicker))
		.gamePicker($store.scope(state: \.destination?.gamePicker, action: \.internal.destination.gamePicker))
	}
}

@MainActor extension View {
	fileprivate func bowlerPicker(
		_ store: Binding<StoreOf<ResourcePicker<Bowler.Summary, AlwaysEqual<Void>>>?>
	) -> some View {
		navigationDestination(item: store) { (store: StoreOf<ResourcePicker<Bowler.Summary, AlwaysEqual<Void>>>) in
			ResourcePickerView(store: store) { bowler in
				Bowler.View(bowler)
			}
		}
	}

	fileprivate func leaguePicker(_ store: Binding<StoreOf<ResourcePicker<League.Summary, Bowler.ID>>?>) -> some View {
		navigationDestination(item: store) { (store: StoreOf<ResourcePicker<League.Summary, Bowler.ID>>) in
			ResourcePickerView(store: store) { league in
				Text(league.name)
			}
		}
	}

	fileprivate func seriesPicker(_ store: Binding<StoreOf<ResourcePicker<Series.Summary, League.ID>>?>) -> some View {
		navigationDestination(item: store) { (store: StoreOf<ResourcePicker<Series.Summary, League.ID>>) in
			ResourcePickerView(store: store) { series in
				Text(series.date.longFormat)
			}
		}
	}

	fileprivate func gamePicker(_ store: Binding<StoreOf<ResourcePicker<Game.Summary, Series.ID>>?>) -> some View {
		navigationDestination(item: store) { (store: StoreOf<ResourcePicker<Game.Summary, Series.ID>>) in
			ResourcePickerView(store: store) { game in
				Text(Strings.Game.titleWithOrdinal(game.index + 1))
			}
		}
	}
}
