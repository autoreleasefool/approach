import BowlersRepositoryInterface
import ComposableArchitecture
import EquatablePackageLibrary
import ErrorsFeature
import FeatureActionLibrary
import LeaguesRepositoryInterface
import ModelsLibrary
import ModelsViewsLibrary
import PickableModelsLibrary
import ResourcePickerLibrary
import StatisticsLibrary
import StatisticsWidgetsLibrary
import StatisticsWidgetsRepositoryInterface
import StringsLibrary
import SwiftUI

@Reducer
public struct StatisticsWidgetConfigurationEditor: Reducer, Sendable {
	@ObservableState
	public struct State: Equatable {
		public let id: StatisticsWidget.ID
		public let initialSource: StatisticsWidget.Source?

		public var source: StatisticsWidget.Source?
		public var timeline: StatisticsWidget.Timeline
		public var statistic: String

		public var sources: StatisticsWidget.Sources?
		public var bowler: Bowler.Summary?
		public var league: League.Summary?
		var isShowingLeaguePicker: Bool { bowler != nil }

		public var isLoadingSources = false

		public var errors: Errors<ErrorID>.State = .init()

		@Presents public var destination: Destination.State?

		public var configuration: StatisticsWidget.Configuration? {
			guard let source else { return nil }
			return .init(
				id: id,
				bowlerId: source.bowlerId,
				leagueId: source.leagueId,
				timeline: timeline,
				statistic: statistic
			)
		}

		var isBowlerEditable: Bool {
			switch initialSource {
			case .bowler, .league: false
			case .none: true
			}
		}

		public init(
			source: StatisticsWidget.Source?,
			timeline: StatisticsWidget.Timeline = .past3Months,
			statistic: String = Statistics.GameAverage.title
		) {
			@Dependency(\.uuid) var uuid
			self.id = uuid()
			self.initialSource = source
			self.source = source
			self.timeline = timeline
			self.statistic = statistic
		}
	}

	public enum Action: FeatureAction, ViewAction, BindableAction {
		@CasePathable public enum View {
			case didFirstAppear
			case didTapBowler
			case didTapLeague
			case didTapStatistic
		}
		@CasePathable public enum Delegate {
			case didChangeConfiguration(StatisticsWidget.Configuration?)
		}
		@CasePathable public enum Internal {
			case didLoadSources(Result<StatisticsWidget.Sources, Error>)
			case didLoadDefaultSources(Result<StatisticsWidget.Sources?, Error>)

			case destination(PresentationAction<Destination.Action>)
			case errors(Errors<ErrorID>.Action)
		}

		case view(View)
		case delegate(Delegate)
		case `internal`(Internal)
		case binding(BindingAction<State>)
	}

	@Reducer
	public struct Destination: Reducer, Sendable {
		public enum State: Equatable {
			case bowlerPicker(ResourcePicker<Bowler.Summary, AlwaysEqual<Void>>.State)
			case leaguePicker(ResourcePicker<League.Summary, Bowler.ID>.State)
			case statisticPicker(StatisticPicker.State)
		}

		public enum Action {
			case bowlerPicker(ResourcePicker<Bowler.Summary, AlwaysEqual<Void>>.Action)
			case leaguePicker(ResourcePicker<League.Summary, Bowler.ID>.Action)
			case statisticPicker(StatisticPicker.Action)
		}

		@Dependency(BowlersRepository.self) var bowlers
		@Dependency(LeaguesRepository.self) var leagues

		public var body: some ReducerOf<Self> {
			Scope(state: \.bowlerPicker, action: \.bowlerPicker) {
				ResourcePicker { _ in bowlers.pickable() }
			}
			Scope(state: \.leaguePicker, action: \.leaguePicker) {
				ResourcePicker { bowler in leagues.pickable(bowledBy: bowler, ordering: .byName) }
			}
			Scope(state: \.statisticPicker, action: \.statisticPicker) {
				StatisticPicker()
			}
		}
	}

	public enum ErrorID: Hashable {
		case failedToLoadSources
	}

	public init() {}

	@Dependency(StatisticsWidgetsRepository.self) var statisticsWidgets

	public var body: some ReducerOf<Self> {
		CombineReducers {
			BindingReducer()

			Scope(state: \.errors, action: \.internal.errors) {
				Errors()
			}

			Reduce<State, Action> { state, action in
				switch action {
				case let .view(viewAction):
					switch viewAction {
					case .didFirstAppear:
						return loadSources(&state)

					case .didTapStatistic:
						state.destination = .statisticPicker(.init(selected: state.statistic))
						return .none

					case .didTapBowler:
						guard state.isBowlerEditable else { return .none }
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
					}

				case let .internal(internalAction):
					switch internalAction {
					case let .didLoadSources(.success(sources)):
						state.isLoadingSources = false
						state.sources = sources
						state.bowler = sources.bowler
						state.league = sources.league
						return .none

					case let .didLoadDefaultSources(.success(sources)):
						state.isLoadingSources = false
						state.sources = sources
						state.bowler = sources?.bowler
						state.league = sources?.league
						if let league = sources?.league {
							state.source = .league(league.id)
						} else if let bowler = sources?.bowler {
							state.source = .bowler(bowler.id)
						}
						return .none

					case let .didLoadSources(.failure(error)), let .didLoadDefaultSources(.failure(error)):
						state.isLoadingSources = false
						return state.errors
							.enqueue(.failedToLoadSources, thrownError: error, toastMessage: Strings.Error.Toast.failedToLoad)
							.map { .internal(.errors($0)) }

					case let .destination(.presented(.bowlerPicker(.delegate(delegateAction)))):
						switch delegateAction {
						case let .didChangeSelection(bowler):
							state.bowler = bowler.first
							state.league = nil
							if let bowler = bowler.first {
								state.source = .bowler(bowler.id)
							} else {
								state.source = nil
							}
							return .none
						}

					case let .destination(.presented(.leaguePicker(.delegate(delegateAction)))):
						switch delegateAction {
						case let .didChangeSelection(league):
							state.league = league.first
							if let league = league.first {
								state.source = .league(league.id)
							} else if let bowler = state.bowler {
								state.source = .bowler(bowler.id)
							} else {
								state.source = nil
							}
							return .none
						}

					case let .destination(.presented(.statisticPicker(.delegate(delegateAction)))):
						switch delegateAction {
						case let .didSelectStatistic(statistic):
							state.statistic = statistic
							return .none
						}

					case .errors(.delegate(.doNothing)):
						return .none

					case .destination(.dismiss),
							.destination(.presented(.bowlerPicker(.internal))), .destination(.presented(.bowlerPicker(.view))),
							.destination(.presented(.leaguePicker(.internal))), .destination(.presented(.leaguePicker(.view))),
							.destination(.presented(.statisticPicker(.internal))), .destination(.presented(.statisticPicker(.view))),
							.errors(.internal), .errors(.view):
						return .none
					}

				case .delegate, .binding:
					return .none
				}
			}
			.ifLet(\.$destination, action: \.internal.destination) {
				Destination()
			}
		}
		.onChange(of: \.configuration) { _, configuration in
			Reduce<State, Action> { _, _ in
				return .send(.delegate(.didChangeConfiguration(configuration)))
			}
		}
	}

	private func loadSources(_ state: inout State) -> Effect<Action> {
		state.isLoadingSources = true
		if let source = state.source {
			return .run { send in
				await send(.internal(.didLoadSources(Result {
					try await statisticsWidgets.loadSources(source)
				})))
			}
		} else {
			return .run { send in
				await send(.internal(.didLoadDefaultSources(Result {
					try await statisticsWidgets.loadDefaultSources()
				})))
			}
		}
	}
}
