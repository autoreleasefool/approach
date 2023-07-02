import BowlersRepositoryInterface
import ComposableArchitecture
import EquatableLibrary
import FeatureActionLibrary
import Foundation
import LeaguesRepositoryInterface
import ModelsLibrary
import ResourcePickerLibrary
import StatisticsLibrary
import StatisticsRepositoryInterface
import StatisticsWidgetsRepositoryInterface
import StatisticsWidgetsLibrary
import StringsLibrary

public struct StatisticsWidgetEditor: Reducer {
	static let chartLoadingAnimationTime: TimeInterval = 0.5

	public struct State: Equatable {
		public let id: StatisticsWidget.ID
		public var source: StatisticsWidget.Source?
		public var timeline: StatisticsWidget.Timeline = .past3Months
		public var statistic: StatisticsWidget.Statistic = .average

		public var sources: StatisticsWidget.Sources?
		public var bowler: Bowler.Summary?
		public var league: League.Summary?

		public var isLoadingSources = false
		public var isLoadingPreview = false
		public var widgetPreviewData: Statistics.ChartContent?

		@PresentationState public var destination: Destination.State?

		public init(existingConfiguration: StatisticsWidget.Configuration?) {
			if let existingConfiguration {
				self.id = existingConfiguration.id
				self.source = existingConfiguration.source
				self.timeline = existingConfiguration.timeline
				self.statistic = existingConfiguration.statistic
			} else {
				@Dependency(\.uuid) var uuid
				self.id = uuid()
			}
		}
	}

	public enum Action: FeatureAction, Equatable {
		public enum ViewAction: Equatable {
			case onAppear
			case didTapBowler
			case didTapLeague
			case didTapSaveButton
			case didChangeTimeline(StatisticsWidget.Timeline)
			case didChangeStatistic(StatisticsWidget.Statistic)
		}
		public enum DelegateAction: Equatable {
			case didCreateConfiguration(StatisticsWidget.Configuration)
		}
		public enum InternalAction: Equatable {
			case destination(PresentationAction<Destination.Action>)

			case didStartLoadingPreview
			case didLoadSources(TaskResult<StatisticsWidget.Sources?>)
			case didLoadChartContent(TaskResult<Statistics.ChartContent>)
			case hideChart
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

	public enum CancelID {
		case loadingPreview
	}

	public init() {}

	@Dependency(\.continuousClock) var clock
	@Dependency(\.date) var date
	@Dependency(\.dismiss) var dismiss
	@Dependency(\.statisticsWidgets) var statisticsWidgets
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
					guard let configuration = state.configuration else { return .none }
					// TODO: save configuration to database
					return .concatenate(
						.send(.delegate(.didCreateConfiguration(configuration))),
						.run { _ in await dismiss() }
					)

				case let .didChangeTimeline(timeline):
					state.timeline = timeline
					return refreshChart(withConfiguration: state.configuration, state: &state)

				case let .didChangeStatistic(statistic):
					state.statistic = statistic
					return refreshChart(withConfiguration: state.configuration, state: &state)
				}

			case let .internal(internalAction):
				switch internalAction {
				case .hideChart:
					state.isLoadingPreview = false
					state.widgetPreviewData = nil
					return .none

				case .didStartLoadingPreview:
					state.isLoadingPreview = true
					state.widgetPreviewData = nil
					return .none

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

				case let .didLoadChartContent(.success(content)):
					state.widgetPreviewData = content
					return .none

				case .didLoadChartContent(.failure):
					// TODO: handle failure loading chart preview
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
						return refreshChart(withConfiguration: state.configuration, state: &state)
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
						return refreshChart(withConfiguration: state.configuration, state: &state)
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

	private func refreshChart(
		withConfiguration configuration: StatisticsWidget.Configuration?,
		state: inout State
	) -> Effect<Action> {
		guard let configuration else {
			return .send(.internal(.hideChart), animation: .easeInOut)
		}

		return .concatenate(
			.run { send in await send(.internal(.didStartLoadingPreview), animation: .easeInOut) },
			.run { send in
				let startTime = date()

				let result = await TaskResult { try await statisticsWidgets.chart(configuration) }

				let timeSpent = date().timeIntervalSince(startTime)
				if timeSpent < Self.chartLoadingAnimationTime {
					try await clock.sleep(for: .milliseconds((Self.chartLoadingAnimationTime - timeSpent) * 1000))
				}

				await send(.internal(.didLoadChartContent(result)), animation: .easeInOut)
			}
		)
		.cancellable(id: CancelID.loadingPreview, cancelInFlight: true)
	}
}

extension StatisticsWidgetEditor.State {
	var configuration: StatisticsWidget.Configuration? {
		guard let source else { return nil }
		return .init(id: id, source: source, timeline: timeline, statistic: statistic)
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
