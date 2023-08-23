import ComposableArchitecture
import ErrorsFeature
import FeatureActionLibrary
import ModelsLibrary
import StatisticsChartsLibrary
import StatisticsDetailsFeature
import StatisticsLibrary
import StatisticsWidgetsLibrary
import StringsLibrary
import SwiftUI
import ViewsLibrary

public struct StatisticsWidgetLayout: Reducer {
	public struct State: Equatable {
		public let context: String
		public let newWidgetSource: StatisticsWidget.Source?

		public var widgets: IdentifiedArrayOf<StatisticsWidget.Configuration>?
		public var widgetData: [StatisticsWidget.ID: Statistics.ChartContent] = [:]

		public var errors: Errors<ErrorID>.State = .init()

		@PresentationState public var destination: Destination.State?

		public init(context: String, newWidgetSource: StatisticsWidget.Source?) {
			self.context = context
			self.newWidgetSource = newWidgetSource
		}
	}

	public enum Action: FeatureAction, Equatable {
		public enum ViewAction: Equatable {
			case didObserveData
			case didTapConfigureStatisticsButton
			case didTapWidget(id: StatisticsWidget.ID)
		}
		public enum DelegateAction: Equatable {}
		public enum InternalAction: Equatable {
			case widgetsResponse(TaskResult<[StatisticsWidget.Configuration]>)
			case didLoadChartContent(id: StatisticsWidget.ID, TaskResult<Statistics.ChartContent>)

			case errors(Errors<ErrorID>.Action)
			case destination(PresentationAction<Destination.Action>)
		}

		case view(ViewAction)
		case delegate(DelegateAction)
		case `internal`(InternalAction)
	}

	public struct Destination: Reducer {
		public enum State: Equatable {
			case details(StatisticsDetails.State)
			case layout(StatisticsWidgetLayoutBuilder.State)
		}

		public enum Action: Equatable {
			case details(StatisticsDetails.Action)
			case layout(StatisticsWidgetLayoutBuilder.Action)
		}

		public var body: some ReducerOf<Self> {
			Scope(state: /State.details, action: /Action.details) {
				StatisticsDetails()
			}
			Scope(state: /State.layout, action: /Action.layout) {
				StatisticsWidgetLayoutBuilder()
			}
		}
	}

	public enum ErrorID: Hashable {
		case widgetNotFound
		case failedToLoadWidgets
		case failedToLoadChart
	}

	public init() {}

	@Dependency(\.calendar) var calendar
	@Dependency(\.date) var date
	@Dependency(\.statisticsWidgets) var statisticsWidgets

	public var body: some ReducerOf<Self> {
		Scope(state: \.errors, action: /Action.internal..Action.InternalAction.errors) {
			Errors()
		}

		Reduce<State, Action> { state, action in
			switch action {
			case let .view(viewAction):
				switch viewAction {
				case .didObserveData:
					return .run { [context = state.context] send in
						for try await widgets in self.statisticsWidgets.fetchAll(forContext: context) {
							await send(.internal(.widgetsResponse(.success(widgets))))
						}
					} catch: { error, send in
						await send(.internal(.widgetsResponse(.failure(error))))
					}

				case .didTapConfigureStatisticsButton:
					state.destination = .layout(.init(context: state.context, newWidgetSource: state.newWidgetSource))
					return .none

				case let .didTapWidget(id):
					guard let widget = state.widgets?[id: id] else {
						return state.errors
							.enqueue(
								.widgetNotFound,
								thrownError: StatisticsWidgetLayoutError.widgetNotFound(id),
								toastMessage: Strings.Error.Toast.dataNotFound
							)
							.map { .internal(.errors($0)) }
					}

					state.destination = .details(.init(
						filter: .init(widget: widget, relativeToDate: date(), inCalendar: calendar),
						withInitialStatistic: widget.statistic.type.title
					))
					return .none
				}

			case let .internal(internalAction):
				switch internalAction {
				case let .widgetsResponse(.success(widgets)):
					state.widgets = .init(uniqueElements: widgets)
					let chartTasks: [Effect<Action>]? = state.widgets?.map { widget in
						.run { send in
							await send(.internal(.didLoadChartContent(id: widget.id, TaskResult {
								try await self.statisticsWidgets.chart(widget)
							})))
						}
					}

					guard let chartTasks else { return .none }
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

				case let .destination(.presented(.details(.delegate(delegateAction)))):
					switch delegateAction {
					case .never:
						return .none
					}

				case let .destination(.presented(.layout(.delegate(delegateAction)))):
					switch delegateAction {
					case .never:
						return .none
					}

				case .destination(.dismiss),
						.destination(.presented(.layout(.internal))), .destination(.presented(.layout(.view))),
						.destination(.presented(.details(.internal))), .destination(.presented(.details(.view))),
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
}

public struct StatisticsWidgetLayoutView: View {
	let store: StoreOf<StatisticsWidgetLayout>

	struct ViewState: Equatable {
		let widgets: IdentifiedArrayOf<StatisticsWidget.Configuration>?
		let widgetData: [StatisticsWidget.ID: Statistics.ChartContent]

		init(state: StatisticsWidgetLayout.State) {
			self.widgets = state.widgets
			self.widgetData = state.widgetData
		}
	}

	public init(store: StoreOf<StatisticsWidgetLayout>) {
		self.store = store
	}

	public var body: some View {
		WithViewStore(store, observe: ViewState.init, send: { .view($0) }, content: { viewStore in
			Group {
				if let widgets = viewStore.widgets {
					if widgets.isEmpty {
						Button { viewStore.send(.didTapConfigureStatisticsButton) } label: {
							StatisticsWidget.PlaceholderWidget()
						}
						.buttonStyle(TappableElement())
					} else {
						LazyVGrid(
							columns: [.init(spacing: .standardSpacing), .init(spacing: .standardSpacing)],
							spacing: .standardSpacing
						) {
							ForEach(widgets) { widget in
								SquareWidget(
									configuration: widget,
									chartContent: viewStore.widgetData[widget.id]
								) {
									viewStore.send(.didTapWidget(id: widget.id))
								}
							}
						}
						.simultaneousGesture(LongPressGesture().onEnded { _ in viewStore.send(.didTapConfigureStatisticsButton) })
					}
				} else {
					Text("")
				}
			}
			.task { await viewStore.send(.didObserveData).finish() }
		})
		.errors(store: store.scope(state: \.errors, action: { .internal(.errors($0)) }))
		.navigationDestination(
			store: store.scope(state: \.$destination, action: { .internal(.destination($0)) }),
			state: /StatisticsWidgetLayout.Destination.State.details,
			action: StatisticsWidgetLayout.Destination.Action.details
		) { (store: StoreOf<StatisticsDetails>) in
			StatisticsDetailsView(store: store)
		}
		.sheet(
			store: store.scope(state: \.$destination, action: { .internal(.destination($0)) }),
			state: /StatisticsWidgetLayout.Destination.State.layout,
			action: StatisticsWidgetLayout.Destination.Action.layout
		) { (store: StoreOf<StatisticsWidgetLayoutBuilder>) in
			NavigationStack {
				StatisticsWidgetLayoutBuilderView(store: store)
			}
			.interactiveDismissDisabled()
		}
	}
}

public enum StatisticsWidgetLayoutError: LocalizedError {
	case widgetNotFound(StatisticsWidget.ID)

	public var errorDescription: String? {
		switch self {
		case let .widgetNotFound(id):
			return "Could not find StatisticsWidget with ID '\(id)'"
		}
	}
}
