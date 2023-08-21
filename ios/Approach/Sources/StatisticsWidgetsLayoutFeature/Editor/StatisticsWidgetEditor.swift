import BowlersRepositoryInterface
import ComposableArchitecture
import EquatableLibrary
import ErrorsFeature
import FeatureActionLibrary
import Foundation
import LeaguesRepositoryInterface
import ModelsLibrary
import ResourcePickerLibrary
import StatisticsChartsLibrary
import StatisticsLibrary
import StatisticsWidgetsLibrary
import StatisticsWidgetsRepositoryInterface
import StringsLibrary

public struct StatisticsWidgetEditor: Reducer {
	static let chartLoadingAnimationTime: TimeInterval = 0.5

	public struct State: Equatable {
		public let id: StatisticsWidget.ID
		public let context: String
		public let priority: Int
		public let initialSource: StatisticsWidget.Source?

		public var source: StatisticsWidget.Source?
		@BindingState public var timeline: StatisticsWidget.Timeline = .past3Months
		@BindingState public var statistic: StatisticsWidget.Statistic = .average

		public var sources: StatisticsWidget.Sources?
		public var bowler: Bowler.Summary?
		public var league: League.Summary?

		public var isLoadingSources = false
		public var isLoadingPreview = false
		public var widgetPreviewData: Statistics.ChartContent?

		public var errors: Errors<ErrorID>.State = .init()

		@PresentationState public var destination: Destination.State?

		public init(context: String, priority: Int, source: StatisticsWidget.Source?) {
			@Dependency(\.uuid) var uuid
			self.id = uuid()
			self.context = context
			self.priority = priority
			self.initialSource = source
			self.source = source
		}
	}

	public enum Action: FeatureAction, Equatable {
		public enum ViewAction: BindableAction, Equatable {
			case didFirstAppear
			case didTapBowler
			case didTapLeague
			case didTapSaveButton
			case binding(BindingAction<State>)
		}
		public enum DelegateAction: Equatable {
			case didCreateConfiguration(StatisticsWidget.Configuration)
		}
		public enum InternalAction: Equatable {
			case destination(PresentationAction<Destination.Action>)
			case errors(Errors<ErrorID>.Action)

			case didStartLoadingPreview
			case didLoadSources(TaskResult<StatisticsWidget.Sources>)
			case didLoadChartContent(TaskResult<Statistics.ChartContent>)
			case didFinishSavingConfiguration(TaskResult<StatisticsWidget.Configuration>)
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

	public enum ErrorID: Hashable {
		case failedToLoadSources
		case failedToLoadChart
		case failedToSaveConfiguration
	}

	public init() {}

	@Dependency(\.continuousClock) var clock
	@Dependency(\.date) var date
	@Dependency(\.dismiss) var dismiss
	@Dependency(\.statisticsWidgets) var statisticsWidgets
	@Dependency(\.uuid) var uuid

	public var body: some ReducerOf<Self> {
		BindingReducer(action: /Action.view)

		Reduce<State, Action> { state, action in
			switch action {
			case let .view(viewAction):
				switch viewAction {
				case .didFirstAppear:
					return loadSources(&state)

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

				case .didTapSaveButton:
					guard let configuration = state.configuration else { return .none }
					return .run { [context = state.context, priority = state.priority] send in
						await send(.internal(.didFinishSavingConfiguration(TaskResult {
							try await self.statisticsWidgets.create(configuration.make(on: date(), context: context, priority: priority))
							return configuration
						})))
					}

				case .binding(\.$timeline):
					return refreshChart(withConfiguration: state.configuration, state: &state)

				case .binding(\.$statistic):
					return refreshChart(withConfiguration: state.configuration, state: &state)

				case .binding:
					return .none
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
					state.bowler = sources.bowler
					state.league = sources.league
					return .none

				case let .didLoadChartContent(.success(content)):
					state.widgetPreviewData = content
					return .none

				case let .didFinishSavingConfiguration(.success(configuration)):
					return .concatenate(
						.send(.delegate(.didCreateConfiguration(configuration))),
						.run { _ in await dismiss() }
					)

				case let .didLoadSources(.failure(error)):
					state.isLoadingSources = false
					return state.errors
						.enqueue(.failedToLoadSources, thrownError: error, toastMessage: Strings.Error.Toast.failedToLoad)
						.map { .internal(.errors($0)) }

				case let .didLoadChartContent(.failure(error)):
					return state.errors
						.enqueue(.failedToLoadChart, thrownError: error, toastMessage: Strings.Error.Toast.failedToLoad)
						.map { .internal(.errors($0)) }

				case let .didFinishSavingConfiguration(.failure(error)):
					return state.errors
						.enqueue(.failedToSaveConfiguration, thrownError: error, toastMessage: Strings.Error.Toast.failedToSave)
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

				case let .errors(.delegate(delegateAction)):
					switch delegateAction {
					case .never:
						return .none
					}

				case .destination(.dismiss),
						.destination(.presented(.bowlerPicker(.internal))),
						.destination(.presented(.bowlerPicker(.view))),
						.destination(.presented(.leaguePicker(.internal))),
						.destination(.presented(.leaguePicker(.view))),
						.errors(.internal), .errors(.view):
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
		guard let source = state.source else { return .none }
		state.isLoadingSources = true
		return .run { send in
			await send(.internal(.didLoadSources(TaskResult {
				try await statisticsWidgets.loadSources(source)
			})))
		}
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

	var isBowlerEditable: Bool {
		switch initialSource {
		case .bowler, .league: return false
		case .none: return true
		}
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
