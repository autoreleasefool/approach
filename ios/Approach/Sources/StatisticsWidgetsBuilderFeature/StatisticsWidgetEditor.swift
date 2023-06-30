import BowlersRepositoryInterface
import ComposableArchitecture
import EquatableLibrary
import FeatureActionLibrary
import LeaguesRepositoryInterface
import ModelsLibrary
import ResourcePickerLibrary
import StatisticsRepositoryInterface
import StatisticsWidgetsLibrary
import StringsLibrary

public struct StatisticsWidgetEditor: Reducer {
	public struct State: Equatable {
		public var source: StatisticsWidget.Configuration.Source?
		public var timeline: StatisticsWidget.Configuration.Timeline = .past3Months
		public var statistic: StatisticsWidget.Configuration.Statistic = .average

		public var sources: StatisticsWidget.Configuration.Sources?
		public var bowler: Bowler.Summary?
		public var league: League.Summary?

		public var isLoadingSources = false

		@PresentationState public var destination: Destination.State?

		public init(existingConfiguration: StatisticsWidget.Configuration?) {
			if let existingConfiguration {
				self.source = existingConfiguration.source
				self.timeline = existingConfiguration.timeline
				self.statistic = existingConfiguration.statistic
			}
		}
	}

	public enum Action: FeatureAction, Equatable {
		public enum ViewAction: Equatable {
			case onAppear
			case didTapBowler
			case didTapLeague
			case didTapSaveButton
			case didChangeTimeline(StatisticsWidget.Configuration.Timeline)
			case didChangeStatistic(StatisticsWidget.Configuration.Statistic)
		}
		public enum DelegateAction: Equatable {
			case didCreateConfiguration(StatisticsWidget.Configuration)
		}
		public enum InternalAction: Equatable {
			case destination(PresentationAction<Destination.Action>)

			case didLoadSources(TaskResult<StatisticsWidget.Configuration.Sources?>)
		}

		case view(ViewAction)
		case delegate(DelegateAction)
		case `internal`(InternalAction)
	}

	public struct Destination: Reducer {
		public enum State: Equatable {
			case bowlerPicker(ResourcePicker<Bowler.Summary, AlwaysEqual<Void>>.State)
			case leaguePicker(ResourcePicker<League.Summary, Bowler.ID>.State)
		}

		public enum Action: Equatable {
			case bowlerPicker(ResourcePicker<Bowler.Summary, AlwaysEqual<Void>>.Action)
			case leaguePicker(ResourcePicker<League.Summary, Bowler.ID>.Action)
		}

		@Dependency(\.bowlers) var bowlers
		@Dependency(\.leagues) var leagues

		public var body: some ReducerOf<Self> {
			Scope(state: /State.bowlerPicker, action: /Action.bowlerPicker) {
				ResourcePicker { _ in bowlers.pickable() }
			}
			Scope(state: /State.leaguePicker, action: /Action.leaguePicker) {
				ResourcePicker { bowler in leagues.pickable(bowledBy: bowler, ordering: .byName) }
			}
		}
	}

	public init() {}

	@Dependency(\.dismiss) var dismiss
	@Dependency(\.statistics) var statistics
	@Dependency(\.uuid) var uuid

	public var body: some ReducerOf<Self> {
		Reduce<State, Action> { state, action in
			switch action {
			case let .view(viewAction):
				switch viewAction {
				case .onAppear:
					return loadSources(&state)

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

				case .didTapSaveButton:
					guard let source = state.source else { return .none }
					let configuration = StatisticsWidget.Configuration(
						source: source,
						timeline: state.timeline,
						statistic: state.statistic
					)
					// TODO: save configuration to database
					return .concatenate(
						.send(.delegate(.didCreateConfiguration(configuration))),
						.run { _ in await dismiss() }
					)

				case let .didChangeTimeline(timeline):
					state.timeline = timeline
					return .none

				case let .didChangeStatistic(statistic):
					state.statistic = statistic
					return .none
				}

			case let .internal(internalAction):
				switch internalAction {
				case let .didLoadSources(.success(sources)):
					state.isLoadingSources = false
					state.sources = sources
					state.bowler = sources?.bowler
					state.league = sources?.league
					return .none

				case .didLoadSources(.failure):
					// TODO: handle failure loading sources
					state.isLoadingSources = false
					return .none

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

				case .destination(.dismiss),
						.destination(.presented(.bowlerPicker(.internal))),
						.destination(.presented(.bowlerPicker(.view))),
						.destination(.presented(.leaguePicker(.internal))),
						.destination(.presented(.leaguePicker(.view))):
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

	private func loadSources(_ state: inout State) -> Effect<Action> {
		state.isLoadingSources = true
		return .none
	}
}

extension Bowler.Summary: PickableResource {
	static public func pickableModelName(forCount count: Int) -> String {
		count == 1 ? Strings.Bowler.title : Strings.Bowler.List.title
	}
}

extension League.Summary: PickableResource {
	static public func pickableModelName(forCount count: Int) -> String {
		count == 1 ? Strings.League.title : Strings.League.List.title
	}
}
