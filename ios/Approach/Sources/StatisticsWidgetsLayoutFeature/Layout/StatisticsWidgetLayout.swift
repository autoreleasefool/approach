import AssetsLibrary
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
		public enum DelegateAction: Equatable { case doNothing }
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
			case help(StatisticsWidgetHelp.State)
		}

		public enum Action: Equatable {
			case details(StatisticsDetails.Action)
			case layout(StatisticsWidgetLayoutBuilder.Action)
			case help(StatisticsWidgetHelp.Action)
		}

		public var body: some ReducerOf<Self> {
			Scope(state: /State.details, action: /Action.details) {
				StatisticsDetails()
			}
			Scope(state: /State.layout, action: /Action.layout) {
				StatisticsWidgetLayoutBuilder()
			}
			Scope(state: /State.help, action: /Action.help) {
				StatisticsWidgetHelp()
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

					return presentDetails(for: widget, in: &state)
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

				case .destination(.presented(.details(.delegate(.doNothing)))):
					return .none

				case .destination(.presented(.layout(.delegate(.doNothing)))):
					return .none

				case .destination(.presented(.help(.delegate(.doNothing)))):
					return .none

				case .destination(.dismiss),
						.destination(.presented(.layout(.internal))), .destination(.presented(.layout(.view))),
						.destination(.presented(.details(.internal))), .destination(.presented(.details(.view))),
						.destination(.presented(.help(.internal))), .destination(.presented(.help(.view))),
						.errors(.internal), .errors(.view), .errors(.delegate(.doNothing)):
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

	private func presentDetails(for widget: StatisticsWidget.Configuration, in state: inout State) -> Effect<Action> {
		switch state.widgetData[widget.id] {
		case .averaging, .counting, .percentage:
			guard let filter = TrackableFilter(widget: widget, relativeToDate: date(), inCalendar: calendar) else {
				return .none
			}

			state.destination = .details(.init(
				filter: filter,
				withInitialStatistic: widget.statistic
			))
		case .dataMissing, .chartUnavailable, .none:
			state.destination = .help(.init(missingStatistic: widget))
		}

		return .none
	}
}

public struct StatisticsWidgetLayoutView: View {
	let store: StoreOf<StatisticsWidgetLayout>

	struct ViewState: Equatable {
		let widgets: IdentifiedArrayOf<StatisticsWidget.Configuration>?
		let leftoverWidget: StatisticsWidget.Configuration?
		let widgetData: [StatisticsWidget.ID: Statistics.ChartContent]

		init(state: StatisticsWidgetLayout.State) {
			if let widgets = state.widgets {
				if widgets.count % 2 == 0 {
					self.widgets = widgets
					self.leftoverWidget = nil
				} else {
					self.widgets = .init(uniqueElements: widgets.dropLast())
					self.leftoverWidget = widgets.last
				}
			} else {
				self.widgets = nil
				self.leftoverWidget = nil
			}
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
					if widgets.isEmpty && viewStore.leftoverWidget == nil {
						Button { viewStore.send(.didTapConfigureStatisticsButton) } label: {
							StatisticsWidget.PlaceholderWidget()
						}
						.buttonStyle(TappableElement())
					} else {
						VStack(spacing: 0) {
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

							if let leftoverWidget = viewStore.leftoverWidget {
								RectangleWidget(
									configuration: leftoverWidget,
									chartContent: viewStore.widgetData[leftoverWidget.id]
								) {
									viewStore.send(.didTapWidget(id: leftoverWidget.id))
								}
								.padding(.top, widgets.isEmpty ? .zero : .standardSpacing)
							}

							Button {
								viewStore.send(.didTapConfigureStatisticsButton)
							} label: {
								Text(Strings.Widget.LayoutBuilder.tapToChange)
									.font(.caption)
									.opacity(0.7)
									.frame(maxWidth: .infinity, alignment: .trailing)
							}
							.buttonStyle(.plain)
							.padding(.top, .smallSpacing)
						}
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
		.sheet(
			store: store.scope(state: \.$destination, action: { .internal(.destination($0)) }),
			state: /StatisticsWidgetLayout.Destination.State.help,
			action: StatisticsWidgetLayout.Destination.Action.help
		) { (store: StoreOf<StatisticsWidgetHelp>) in
			NavigationStack {
				StatisticsWidgetHelpView(store: store)
			}
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

#if DEBUG
struct StatisticsWidgetLayoutPreview: PreviewProvider {
	static var previews: some View {
		List {
			Section {
				StatisticsWidgetLayoutView(store: .init(
					initialState: {
						var state = StatisticsWidgetLayout.State(context: "", newWidgetSource: .bowler(UUID()))
						state.widgets = [
							.init(id: UUID(0), bowlerId: UUID(0), leagueId: nil, timeline: .allTime, statistic: "Average"),
							.init(id: UUID(1), bowlerId: UUID(0), leagueId: nil, timeline: .allTime, statistic: "Average"),
							.init(id: UUID(2), bowlerId: UUID(0), leagueId: nil, timeline: .allTime, statistic: "Average"),
						]
						state.widgetData = [
							UUID(0): .averaging(AveragingChart.Data.bowlerAverageIncrementingMock),
							UUID(1): .chartUnavailable(statistic: ""),
							UUID(2): .dataMissing(statistic: ""),
						]
						return state
					}(),
					reducer: StatisticsWidgetLayout.init
				))
			}
			.listRowSeparator(.hidden)
			.listRowInsets(EdgeInsets())
			.listRowBackground(Color.clear)
		}
	}
}
#endif
