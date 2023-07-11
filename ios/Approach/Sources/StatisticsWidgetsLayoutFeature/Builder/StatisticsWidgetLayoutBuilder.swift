import AnalyticsServiceInterface
import ComposableArchitecture
import FeatureActionLibrary
import ModelsLibrary
import ReorderingLibrary
import StatisticsChartsLibrary
import StatisticsLibrary
import StatisticsWidgetsLibrary
import SwiftUI

public struct StatisticsWidgetLayoutBuilder: Reducer {
	public struct State: Equatable {
		public let context: String
		public let newWidgetSource: StatisticsWidget.Source?

		public var widgets: IdentifiedArrayOf<StatisticsWidget.Configuration> = []
		public var widgetData: [StatisticsWidget.ID: Statistics.ChartContent] = [:]
		public var _reordering: Reorderable<MoveableWidget, StatisticsWidget.Configuration>.State = .init(items: [])

		public var isDeleting = false
		public var isAnimatingWidgets = false

		@PresentationState public var editor: StatisticsWidgetEditor.State?

		public init(context: String, newWidgetSource: StatisticsWidget.Source?) {
			self.context = context
			self.newWidgetSource = newWidgetSource
		}
	}

	public enum Action: FeatureAction, Equatable {
		public enum ViewAction: Equatable {
			case didObserveData
			case didTapAddNew
			case didTapDeleteButton
			case didTapCancelDeleteButton
			case didTapDoneButton
			case didTapDeleteWidget(id: StatisticsWidget.ID)
			case didFinishDismissingEditor
			case setAnimateWidgets(Bool)
			case setDelete(Bool)
		}
		public enum DelegateAction: Equatable {}
		public enum InternalAction: Equatable {
			case widgetsResponse(TaskResult<[StatisticsWidget.Configuration]>)
			case didLoadChartContent(id: StatisticsWidget.ID, TaskResult<Statistics.ChartContent>)
			case didUpdatePriorities(TaskResult<Never>)
			case didDeleteWidget(TaskResult<Never>)

			case startAnimatingWidgets
			case stopAnimatingWidgets

			case editor(PresentationAction<StatisticsWidgetEditor.Action>)
			case reordering(Reorderable<MoveableWidget, StatisticsWidget.Configuration>.Action)
		}

		case view(ViewAction)
		case delegate(DelegateAction)
		case `internal`(InternalAction)
	}

	enum CancelID { case updatePriorities }

	public init() {}

	@Dependency(\.analytics) var analytics
	@Dependency(\.continuousClock) var clock
	@Dependency(\.dismiss) var dismiss
	@Dependency(\.statisticsWidgets) var statisticsWidgets

	public var body: some ReducerOf<Self> {
		Scope(state: \.reordering, action: /Action.internal..Action.InternalAction.reordering) {
			Reorderable()
		}

		Reduce<State, Action> { state, action in
			switch action {
			case let .view(viewAction):
				switch viewAction {
				case .didObserveData:
					return .merge(
						.run { send in
							try await self.clock.sleep(for: .milliseconds(300))
							await send(.internal(.startAnimatingWidgets), animation: .easeInOut)
						},
						.run { [context = state.context] send in
							for try await widgets in self.statisticsWidgets.fetchAll(forContext: context) {
								await send(.internal(.widgetsResponse(.success(widgets))))
							}
						} catch: { error, send in
							await send(.internal(.widgetsResponse(.failure(error))))
						}
					)

				case .didTapDoneButton:
					return .merge(
						.run { _ in await dismiss() },
						.run { [context = state.context, numberOfWidgets = state.widgets.count] _ in
							await analytics.trackEvent(Analytics.Widget.LayoutUpdated(context: context, numberOfWidgets: numberOfWidgets))
						}
					)

				case .didTapAddNew:
					state.editor = .init(context: state.context, priority: state.widgets.count, source: state.newWidgetSource)
					return .send(.internal(.stopAnimatingWidgets), animation: .easeInOut)

				case .didTapDeleteButton:
					state.isDeleting = true
					return .none

				case .didTapCancelDeleteButton:
					state.isDeleting = false
					return .none

				case let .didTapDeleteWidget(id):
					guard state.isDeleting else { return .none }
					state.widgets.remove(id: id)
					state.widgetData.removeValue(forKey: id)
					return .merge(
						.run { _ in
							try await self.statisticsWidgets.delete(id)
						} catch: { error, send in
							await send(.internal(.didDeleteWidget(.failure(error))))
						},
						.run { [context = state.context] _ in await analytics.trackEvent(Analytics.Widget.Deleted(context: context)) }
					)

				case let .setAnimateWidgets(value):
					state.isAnimatingWidgets = value
					return .none

				case let .setDelete(value):
					state.isDeleting = value
					return .none

				case .didFinishDismissingEditor:
					return .send(.internal(.startAnimatingWidgets), animation: .easeInOut)
				}

			case let .internal(internalAction):
				switch internalAction {
				case .startAnimatingWidgets:
					state.isAnimatingWidgets = true
					return .none

				case .stopAnimatingWidgets:
					state.isAnimatingWidgets = false
					return .none

				case let .widgetsResponse(.success(widgets)):
					state.widgets = .init(uniqueElements: widgets)
					if state.widgets.isEmpty {
						state.isDeleting = false
					}

					let removed = state.widgetData.keys.filter { state.widgets[id: $0] == nil }
					for id in removed {
						state.widgetData.removeValue(forKey: id)
					}

					let chartTasks: [Effect<Action>] = state.widgets.map { widget in
						.run { send in
							await send(.internal(.didLoadChartContent(id: widget.id, TaskResult {
								try await self.statisticsWidgets.chart(widget)
							})))
						}
					}
					return .merge(chartTasks)

				case .widgetsResponse(.failure):
					// TODO: handle failure loading widgets
					return .none

				case let .didLoadChartContent(id, .success(chartContent)):
					state.widgetData[id] = chartContent
					return .none

				case .didLoadChartContent(_, .failure):
					// TODO: handle failure loading chart
					return .none

				case .didUpdatePriorities(.failure):
					// TODO: handle failure updating priorities
					return .none

				case .didDeleteWidget(.failure):
					// TODO: handle failure deleting widget
					return .none

				case let .editor(.presented(.delegate(delegateAction))):
					switch delegateAction {
					case let .didCreateConfiguration(configuration):
						return .run { [context = state.context] _ in
							await analytics.trackEvent(Analytics.Widget.Created(
								context: context,
								source: configuration.source.analyticsString,
								statistic: configuration.statistic.rawValue,
								timeline: configuration.timeline.rawValue
							))
						}
					}

				case let .reordering(.delegate(delegateAction)):
					switch delegateAction {
					case let .itemDidMove(from, to):
						state.widgets.move(fromOffsets: from, toOffset: to)
						return .none

					case .didStartReordering:
						return .none

					case .didFinishReordering:
						return .run { [widgets = state.widgets] send in
							do {
								try await clock.sleep(for: .nanoseconds(NSEC_PER_SEC / 3))
								try await statisticsWidgets.updatePriorities(widgets.map(\.id))
							} catch {
								await send(.internal(.didUpdatePriorities(.failure(error))))
							}
						}.cancellable(id: CancelID.updatePriorities, cancelInFlight: true)
					}

				case .editor(.dismiss), .editor(.presented(.internal)), .editor(.presented(.view)):
					return .none

				case .reordering(.internal), .reordering(.view):
					return .none
				}

			case .delegate:
				return .none
			}
		}
		.ifLet(\.$editor, action: /Action.internal..Action.InternalAction.editor) {
			StatisticsWidgetEditor()
		}
	}
}

extension StatisticsWidgetLayoutBuilder.State {
	var reordering: Reorderable<MoveableWidget, StatisticsWidget.Configuration>.State {
		get {
			var reordering = _reordering
			reordering.items = widgets
			return reordering
		}
		set {
			self.widgets = newValue.items
		}
	}
}

extension StatisticsWidget.Source {
	var analyticsString: String {
		switch self {
		case .bowler: return "bowler"
		case .league: return "league"
		}
	}
}
