import ComposableArchitecture
import FeatureActionLibrary
import ModelsLibrary
import StatisticsChartsLibrary
import StatisticsLibrary
import StatisticsWidgetsLibrary
import SwiftUI
import ViewsLibrary

public struct StatisticsWidgetLayout: Reducer {
	public struct State: Equatable {
		public let context: String
		public let newWidgetSource: StatisticsWidget.Source?

		public var widgets: IdentifiedArrayOf<StatisticsWidget.Configuration>?
		public var widgetData: [StatisticsWidget.ID: Statistics.ChartContent] = [:]

		@PresentationState public var layoutBuilder: StatisticsWidgetLayoutBuilder.State?

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

			case layoutBuilder(PresentationAction<StatisticsWidgetLayoutBuilder.Action>)
		}

		case view(ViewAction)
		case delegate(DelegateAction)
		case `internal`(InternalAction)
	}

	public init() {}

	@Dependency(\.statisticsWidgets) var statisticsWidgets

	public var body: some ReducerOf<Self> {
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
					state.layoutBuilder = .init(context: state.context, newWidgetSource: state.newWidgetSource)
					return .none

				case .didTapWidget:
					// TODO: handle tapped widget
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

				case .widgetsResponse(.failure):
					// TODO: handle failure loading widgets
					return .none

				case let .didLoadChartContent(id, .success(chartContent)):
					state.widgetData[id] = chartContent
					return .none

				case .didLoadChartContent(_, .failure):
					// TODO: handle failure loading chart
					return .none

				case let .layoutBuilder(.presented(.delegate(delegateAction))):
					switch delegateAction {
					case .never:
						return .none
					}

				case .layoutBuilder(.dismiss), .layoutBuilder(.presented(.internal)), .layoutBuilder(.presented(.view)):
					return .none
				}

			case .delegate:
				return .none
			}
		}
		.ifLet(\.$layoutBuilder, action: /Action.internal..Action.InternalAction.layoutBuilder) {
			StatisticsWidgetLayoutBuilder()
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
		.sheet(store: store.scope(state: \.$layoutBuilder, action: { .internal(.layoutBuilder($0)) })) { store in
			NavigationStack {
				StatisticsWidgetLayoutBuilderView(store: store)
			}
			.interactiveDismissDisabled()
		}
	}
}
