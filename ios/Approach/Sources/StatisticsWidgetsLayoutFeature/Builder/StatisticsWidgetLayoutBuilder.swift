import AnalyticsServiceInterface
import ComposableArchitecture
import ErrorsFeature
import FeatureActionLibrary
import ModelsLibrary
import ReorderingLibrary
import StatisticsChartsLibrary
import StatisticsLibrary
import StatisticsWidgetsLibrary
import StringsLibrary
import SwiftUI

@Reducer
public struct StatisticsWidgetLayoutBuilder: Reducer {
	@ObservableState
	public struct State: Equatable {
		public let context: String
		public let newWidgetSource: StatisticsWidget.Source?

		public var widgets: IdentifiedArrayOf<StatisticsWidget.Configuration> = []
		public var widgetData: [StatisticsWidget.ID: Statistics.ChartContent] = [:]
		public var reordering: Reorderable<MoveableWidget, StatisticsWidget.Configuration>.State = .init(items: [])

		public var isDeleting = false
		public var isAnimatingWidgets = false

		public var errors: Errors<ErrorID>.State = .init()

		@Presents public var editor: StatisticsWidgetEditor.State?

		public init(context: String, newWidgetSource: StatisticsWidget.Source?) {
			self.context = context
			self.newWidgetSource = newWidgetSource
		}

		mutating func syncReorderingSharedState() {
			reordering.items = widgets
		}
	}

	public enum Action: FeatureAction, ViewAction, BindableAction {
		@CasePathable public enum View {
			case onAppear
			case task
			case didTapAddNew
			case didTapDeleteButton
			case didTapCancelDeleteButton
			case didTapDoneButton
			case didTapDeleteWidget(id: StatisticsWidget.ID)
			case didFinishDismissingEditor
		}
		@CasePathable public enum Delegate { case doNothing }
		@CasePathable public enum Internal {
			case widgetsResponse(Result<[StatisticsWidget.Configuration], Error>)
			case didLoadChartContent(id: StatisticsWidget.ID, Result<Statistics.ChartContent, Error>)
			case didUpdatePriorities(Result<Never, Error>)
			case didDeleteWidget(Result<Never, Error>)

			case startAnimatingWidgets
			case stopAnimatingWidgets

			case errors(Errors<ErrorID>.Action)
			case editor(PresentationAction<StatisticsWidgetEditor.Action>)
			case reordering(Reorderable<MoveableWidget, StatisticsWidget.Configuration>.Action)
		}

		case view(View)
		case delegate(Delegate)
		case `internal`(Internal)
		case binding(BindingAction<State>)
	}

	enum CancelID { case updatePriorities }

	public enum ErrorID: Hashable {
		case failedToLoadWidgets
		case failedToLoadChart
		case failedToSaveOrder
		case failedToDeleteWidget
	}

	public init() {}

	@Dependency(\.continuousClock) var clock
	@Dependency(\.dismiss) var dismiss
	@Dependency(\.statisticsWidgets) var statisticsWidgets

	public var body: some ReducerOf<Self> {
		BindingReducer()

		Scope(state: \.errors, action: \.internal.errors) {
			Errors()
		}

		Scope(state: \.reordering, action: \.internal.reordering) {
			Reorderable()
		}

		Reduce<State, Action> { state, action in
			switch action {
			case let .view(viewAction):
				switch viewAction {
				case .onAppear:
					return .none

				case .task:
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
					return .run { _ in await dismiss() }

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
					return .run { _ in
						try await self.statisticsWidgets.delete(id)
					} catch: { error, send in
						await send(.internal(.didDeleteWidget(.failure(error))))
					}

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
					state.syncReorderingSharedState()
					if state.widgets.isEmpty {
						state.isDeleting = false
					}

					let removed = state.widgetData.keys.filter { state.widgets[id: $0] == nil }
					for id in removed {
						state.widgetData.removeValue(forKey: id)
					}

					let chartTasks: [Effect<Action>] = state.widgets.map { widget in
						.run { send in
							await send(.internal(.didLoadChartContent(id: widget.id, Result {
								try await self.statisticsWidgets.chart(widget)
							})))
						}
					}
					return .merge(chartTasks)

				case let .didLoadChartContent(id, .success(chartContent)):
					state.widgetData[id] = chartContent
					return .none

				case let .widgetsResponse(.failure(error)):
					return state.errors
						.enqueue(.failedToLoadWidgets, thrownError: error, toastMessage: Strings.Error.Toast.failedToLoad)
						.map { .internal(.errors($0)) }

				case let .didLoadChartContent(_, .failure(error)):
					return state.errors
						.enqueue(.failedToLoadChart, thrownError: error, toastMessage: Strings.Error.Toast.failedToLoad)
						.map { .internal(.errors($0)) }

				case let .didUpdatePriorities(.failure(error)):
					return state.errors
						.enqueue(.failedToSaveOrder, thrownError: error, toastMessage: Strings.Error.Toast.failedToSave)
						.map { .internal(.errors($0)) }

				case let .didDeleteWidget(.failure(error)):
					return state.errors
						.enqueue(.failedToDeleteWidget, thrownError: error, toastMessage: Strings.Error.Toast.failedToDelete)
						.map { .internal(.errors($0)) }

				case let .editor(.presented(.delegate(delegateAction))):
					switch delegateAction {
					case .didCreateConfiguration:
						return .none
					}

				case let .reordering(.delegate(delegateAction)):
					switch delegateAction {
					case let .itemDidMove(from, to):
						state.widgets.move(fromOffsets: from, toOffset: to)
						state.syncReorderingSharedState()
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

				case .editor(.dismiss),
						.editor(.presented(.internal)),
						.editor(.presented(.view)),
						.editor(.presented(.binding)),
						.reordering(.internal), .reordering(.view),
						.errors(.internal), .errors(.view), .errors(.delegate(.doNothing)):
					return .none
				}

			case .delegate, .binding:
				return .none
			}
		}
		.ifLet(\.$editor, action: \.internal.editor) {
			StatisticsWidgetEditor()
		}

		AnalyticsReducer<State, Action> { state, action in
			switch action {
			case .view(.didTapDoneButton):
				return Analytics.Widget.LayoutUpdated(context: state.context, numberOfWidgets: state.widgets.count)
			case .view(.didTapDeleteWidget):
				return Analytics.Widget.Deleted(context: state.context)
			case let .internal(.editor(.presented(.delegate(.didCreateConfiguration(configuration))))):
				return Analytics.Widget.Created(
					context: state.context,
					source: configuration.source?.analyticsString,
					statistic: configuration.statistic,
					timeline: configuration.timeline.rawValue
				)
			default:
				return nil
			}
		}

		BreadcrumbReducer<State, Action> { _, action in
			switch action {
			case .view(.onAppear): return .navigationBreadcrumb(type(of: self))
			default: return nil
			}
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
