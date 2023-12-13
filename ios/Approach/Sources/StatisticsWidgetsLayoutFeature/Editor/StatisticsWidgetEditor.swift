import AnalyticsServiceInterface
import BowlersRepositoryInterface
import ComposableArchitecture
import EquatableLibrary
import ErrorsFeature
import FeatureActionLibrary
import Foundation
import LeaguesRepositoryInterface
import ModelsLibrary
import PickableModelsLibrary
import ResourcePickerLibrary
import StatisticsChartsLibrary
import StatisticsDetailsFeature
import StatisticsLibrary
import StatisticsWidgetsLibrary
import StatisticsWidgetsRepositoryInterface
import StringsLibrary
import TipsLibrary
import TipsServiceInterface

@Reducer
// swiftlint:disable file_length
// swiftlint:disable:next type_body_length
public struct StatisticsWidgetEditor: Reducer {
	static let chartLoadingAnimationTime: TimeInterval = 0.5

	public struct State: Equatable {
		public let id: StatisticsWidget.ID
		public let context: String
		public let priority: Int
		public let initialSource: StatisticsWidget.Source?

		public var source: StatisticsWidget.Source?
		@BindingState public var timeline: StatisticsWidget.Timeline = .past3Months
		public var statistic: String = Statistics.GameAverage.title

		public var sources: StatisticsWidget.Sources?
		public var bowler: Bowler.Summary?
		public var league: League.Summary?

		public var isLoadingSources = false
		public var isLoadingPreview = false
		public var widgetPreviewData: Statistics.ChartContent?

		public var isShowingTapThroughTip: Bool

		public var errors: Errors<ErrorID>.State = .init()

		@PresentationState public var destination: Destination.State?

		var configuration: StatisticsWidget.Configuration? {
			guard let source else { return nil }
			return .init(id: id, bowlerId: source.bowlerId, leagueId: source.leagueId, timeline: timeline, statistic: statistic)
		}

		var isBowlerEditable: Bool {
			switch initialSource {
			case .bowler, .league: return false
			case .none: return true
			}
		}

		public init(context: String, priority: Int, source: StatisticsWidget.Source?) {
			@Dependency(\.uuid) var uuid
			self.id = uuid()
			self.context = context
			self.priority = priority
			self.initialSource = source
			self.source = source

			@Dependency(\.tips) var tips
			self.isShowingTapThroughTip = tips.shouldShow(tipFor: .tapThroughStatisticTip)
		}
	}

	public enum Action: FeatureAction {
		@CasePathable public enum ViewAction: BindableAction {
			case onAppear
			case didFirstAppear
			case didTapBowler
			case didTapLeague
			case didTapSaveButton
			case didTapWidget
			case didTapStatistic
			case didTapDismissTapThroughTip
			case binding(BindingAction<State>)
		}
		@CasePathable public enum DelegateAction {
			case didCreateConfiguration(StatisticsWidget.Configuration)
		}
		@CasePathable public enum InternalAction {
			case destination(PresentationAction<Destination.Action>)
			case errors(Errors<ErrorID>.Action)

			case didStartLoadingPreview
			case didLoadSources(Result<StatisticsWidget.Sources, Error>)
			case didLoadDefaultSources(Result<StatisticsWidget.Sources?, Error>)
			case didLoadChartContent(Result<Statistics.ChartContent, Error>)
			case didFinishSavingConfiguration(Result<StatisticsWidget.Configuration, Error>)
			case hideChart
		}

		case view(ViewAction)
		case delegate(DelegateAction)
		case `internal`(InternalAction)
	}

	@Reducer
	public struct Destination: Reducer {
		public enum State: Equatable {
			case bowlerPicker(ResourcePicker<Bowler.Summary, AlwaysEqual<Void>>.State)
			case leaguePicker(ResourcePicker<League.Summary, Bowler.ID>.State)
			case help(StatisticsWidgetHelp.State)
			case statisticPicker(StatisticPicker.State)
		}

		public enum Action {
			case bowlerPicker(ResourcePicker<Bowler.Summary, AlwaysEqual<Void>>.Action)
			case leaguePicker(ResourcePicker<League.Summary, Bowler.ID>.Action)
			case help(StatisticsWidgetHelp.Action)
			case statisticPicker(StatisticPicker.Action)
		}

		@Dependency(\.bowlers) var bowlers
		@Dependency(\.leagues) var leagues

		public var body: some ReducerOf<Self> {
			Scope(state: \.bowlerPicker, action: \.bowlerPicker) {
				ResourcePicker { _ in bowlers.pickable() }
			}
			Scope(state: \.leaguePicker, action: \.leaguePicker) {
				ResourcePicker { bowler in leagues.pickable(bowledBy: bowler, ordering: .byName) }
			}
			Scope(state: \.help, action: \.help) {
				StatisticsWidgetHelp()
			}
			Scope(state: \.statisticPicker, action: \.statisticPicker) {
				StatisticPicker()
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
	@Dependency(\.tips) var tips
	@Dependency(\.uuid) var uuid

	public var body: some ReducerOf<Self> {
		BindingReducer(action: \.view)

		Scope(state: \.errors, action: \.internal.errors) {
			Errors()
		}

		Reduce<State, Action> { state, action in
			switch action {
			case let .view(viewAction):
				switch viewAction {
				case .onAppear:
					return .none

				case .didFirstAppear:
					return loadSources(&state)

				case .didTapStatistic:
					state.destination = .statisticPicker(.init(selected: state.statistic))
					return .none

				case .didTapWidget:
					switch state.widgetPreviewData {
					case .averaging, .counting, .percentage:
						return .none
					case .dataMissing, .chartUnavailable, .none:
						state.destination = .help(.init(missingStatistic: state.configuration))
					}
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

				case .didTapSaveButton:
					guard let configuration = state.configuration,
								let widget = configuration.make(on: date(), context: state.context, priority: state.priority) else {
						return .none
					}
					return .run { send in
						await send(.internal(.didFinishSavingConfiguration(Result {
							try await self.statisticsWidgets.create(widget)
							return configuration
						})))
					}

				case .didTapDismissTapThroughTip:
					state.isShowingTapThroughTip = false
					return .run { _ in await tips.hide(tipFor: .tapThroughStatisticTip) }

				case .binding(\.$timeline):
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
					return refreshChart(withConfiguration: state.configuration, state: &state)

				case let .didLoadChartContent(.success(content)):
					state.widgetPreviewData = content
					return .none

				case let .didFinishSavingConfiguration(.success(configuration)):
					return .concatenate(
						.send(.delegate(.didCreateConfiguration(configuration))),
						.run { _ in await dismiss() }
					)

				case let .didLoadSources(.failure(error)), let .didLoadDefaultSources(.failure(error)):
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

				case .destination(.presented(.help(.delegate(.doNothing)))):
					return .none

				case let .destination(.presented(.statisticPicker(.delegate(delegateAction)))):
					switch delegateAction {
					case let .didSelectStatistic(statistic):
						state.statistic = statistic
						return refreshChart(withConfiguration: state.configuration, state: &state)
					}

				case .errors(.delegate(.doNothing)):
					return .none

				case .destination(.dismiss),
						.destination(.presented(.bowlerPicker(.internal))), .destination(.presented(.bowlerPicker(.view))),
						.destination(.presented(.leaguePicker(.internal))), .destination(.presented(.leaguePicker(.view))),
						.destination(.presented(.help(.internal))), .destination(.presented(.help(.view))),
						.destination(.presented(.statisticPicker(.internal))), .destination(.presented(.statisticPicker(.view))),
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

		BreadcrumbReducer<State, Action> { _, action in
			switch action {
			case .view(.onAppear): return .navigationBreadcrumb(type(of: self))
			default: return nil
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

				let result = await Result { try await statisticsWidgets.chart(configuration) }

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

extension Tip {
	static let tapThroughStatisticTip = Tip(
		id: "Widget.Builder.TapThrough",
		title: Strings.Widget.Builder.TapThrough.title,
		message: Strings.Widget.Builder.TapThrough.message
	)
}
