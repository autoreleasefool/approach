import AnalyticsServiceInterface
import AssetsLibrary
import ComposableArchitecture
import ErrorsFeature
import FeatureActionLibrary
import ModelsLibrary
import StatisticsChartsLibrary
import StatisticsDetailsFeature
import StatisticsLibrary
import StatisticsWidgetsLibrary
import StatisticsWidgetsRepositoryInterface
import StringsLibrary
import SwiftUI
import SwiftUIExtensionsPackageLibrary
import ViewsLibrary

@Reducer
public struct StatisticsWidgetLayout: Reducer, Sendable {
	@ObservableState
	public struct State: Equatable {
		public let context: String
		public let newWidgetSource: StatisticsWidget.Source?

		public var contentSize: CGSize = .zero

		public var widgets: IdentifiedArrayOf<StatisticsWidget.Configuration>?
		public var widgetData: [StatisticsWidget.ID: Statistics.ChartContent] = [:]

		public var errors: Errors<ErrorID>.State = .init()

		@Presents public var destination: Destination.State?

		public init(context: String, newWidgetSource: StatisticsWidget.Source?) {
			self.context = context
			self.newWidgetSource = newWidgetSource
		}
	}

	public enum Action: FeatureAction, ViewAction {
		@CasePathable
		public enum View {
			case task
			case didTapConfigureStatisticsButton
			case didTapWidget(id: StatisticsWidget.ID)
		}
		@CasePathable
		public enum Delegate { case doNothing }
		@CasePathable
		public enum Internal {
			case widgetsResponse(Result<[StatisticsWidget.Configuration], Error>)
			case didLoadChartContent(id: StatisticsWidget.ID, Result<Statistics.ChartContent, Error>)

			case errors(Errors<ErrorID>.Action)
			case destination(PresentationAction<Destination.Action>)
		}

		case view(View)
		case delegate(Delegate)
		case `internal`(Internal)
	}

	@Reducer(state: .equatable)
	public enum Destination {
		case details(StatisticsDetails)
		case layout(StatisticsWidgetLayoutBuilder)
		case help(StatisticsWidgetHelp)
	}

	public enum ErrorID: Hashable, Sendable {
		case widgetNotFound
		case failedToLoadWidgets
		case failedToLoadChart
	}

	public init() {}

	@Dependency(\.calendar) var calendar
	@Dependency(\.date) var date
	@Dependency(StatisticsWidgetsRepository.self) var statisticsWidgets

	public var body: some ReducerOf<Self> {
		Scope(state: \.errors, action: \.internal.errors) {
			Errors()
		}

		Reduce<State, Action> { state, action in
			switch action {
			case let .view(viewAction):
				switch viewAction {
				case .task:
					return .run { [context = state.context] send in
						for try await widgets in statisticsWidgets.fetchAll(forContext: context) {
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
							await send(.internal(.didLoadChartContent(id: widget.id, Result {
								try await statisticsWidgets.chart(widget)
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
						.destination(.presented(.layout(.internal))),
						.destination(.presented(.layout(.view))),
						.destination(.presented(.layout(.binding))),
						.destination(.presented(.details(.internal))),
						.destination(.presented(.details(.view))),
						.destination(.presented(.details(.binding))),
						.destination(.presented(.help(.internal))), .destination(.presented(.help(.view))),
						.errors(.internal), .errors(.view), .errors(.delegate(.doNothing)):
					return .none
				}

			case .delegate:
				return .none
			}
		}
		.ifLet(\.$destination, action: \.internal.destination)

		ErrorHandlerReducer<State, Action> { _, action in
			switch action {
			case let .internal(.widgetsResponse(.failure(error))),
				let .internal(.didLoadChartContent(_, .failure(error))):
				return error
			default:
				return nil
			}
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

@ViewAction(for: StatisticsWidgetLayout.self)
public struct StatisticsWidgetLayoutView: View {
	@Environment(\.horizontalSizeClass) private var horizontalSizeClass
	@Bindable public var store: StoreOf<StatisticsWidgetLayout>
	@State private var contentSize: CGSize = .zero

	public init(store: StoreOf<StatisticsWidgetLayout>) {
		self.store = store
	}

	public var body: some View {
		widgetRows
			.measure(key: ContentSizeKey.self, to: $contentSize)
			.task { await send(.task).finish() }
			.details($store.scope(state: \.destination?.details, action: \.internal.destination.details))
			.layoutBuilder($store.scope(state: \.destination?.layout, action: \.internal.destination.layout))
			.help($store.scope(state: \.destination?.help, action: \.internal.destination.help))
			.errors(store: store.scope(state: \.errors, action: \.internal.errors))
	}

	 @ViewBuilder private var widgetRows: some View {
		if let widgets = widgetRows(forScreenWidth: contentSize.width, inSizeClass: horizontalSizeClass) {
			let leftoverWidget = leftoverWidget(forScreenWidth: contentSize.width, inSizeClass: horizontalSizeClass)
			let numberOfWidgetsPerRow = numberOfWidgetsPerRow(
				forScreenWidth: contentSize.width,
				inSizeClass: horizontalSizeClass
			)

			if widgets.isEmpty && leftoverWidget == nil {
				Button { send(.didTapConfigureStatisticsButton) } label: {
					StatisticsWidget.PlaceholderWidget()
				}
				.buttonStyle(TappableElement())
			} else {
				VStack(spacing: 0) {
					LazyVGrid(
						columns: (0..<numberOfWidgetsPerRow).map { _ in GridItem(spacing: .standardSpacing) },
						spacing: .standardSpacing
					) {
						ForEach(widgets) { widget in
							SquareWidget(
								configuration: widget,
								chartContent: store.widgetData[widget.id]
							) {
								send(.didTapWidget(id: widget.id))
							}
						}
					}

					if let leftoverWidget {
						RectangleWidget(
							configuration: leftoverWidget,
							chartContent: store.widgetData[leftoverWidget.id]
						) {
							send(.didTapWidget(id: leftoverWidget.id))
						}
						.padding(.top, widgets.isEmpty ? .zero : .standardSpacing)
					}

					Button {
						send(.didTapConfigureStatisticsButton)
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
				.frame(maxWidth: .infinity)
		}
	}

	private func numberOfWidgetsPerRow(
		forScreenWidth screenWidth: CGFloat,
		inSizeClass sizeClass: UserInterfaceSizeClass?
	) -> Int {
		switch sizeClass {
		case .compact, .none:
			return 2
		case .regular:
			return Int(max((screenWidth / 240), 2))
		@unknown default:
			return 2
		}
	}

	private func widgetRows(
		forScreenWidth screenWidth: CGFloat,
		inSizeClass sizeClass: UserInterfaceSizeClass?
	) -> IdentifiedArrayOf<StatisticsWidget.Configuration>? {
		guard let widgets = store.widgets else { return nil }
		let widgetsPerRow = numberOfWidgetsPerRow(forScreenWidth: screenWidth, inSizeClass: sizeClass)

		if widgetsPerRow == 2 {
			return widgets.count % 2 == 0 ? widgets : .init(uniqueElements: widgets.dropLast())
		} else {
			return widgets
		}
	}

	private func leftoverWidget(
		forScreenWidth screenWidth: CGFloat,
		inSizeClass sizeClass: UserInterfaceSizeClass?
	) -> StatisticsWidget.Configuration? {
		guard let widgets = store.widgets else { return nil }
		let widgetsPerRow = numberOfWidgetsPerRow(forScreenWidth: screenWidth, inSizeClass: sizeClass)

		if widgetsPerRow == 2 {
			return widgets.count % 2 == 0 ? nil : widgets.last
		} else {
			return nil
		}
	}
}

extension View {
	fileprivate func details(_ store: Binding<StoreOf<StatisticsDetails>?>) -> some View {
		navigationDestination(item: store) { (store: StoreOf<StatisticsDetails>) in
			StatisticsDetailsView(store: store)
		}
	}

	fileprivate func layoutBuilder(_ store: Binding<StoreOf<StatisticsWidgetLayoutBuilder>?>) -> some View {
		sheet(item: store) { (store: StoreOf<StatisticsWidgetLayoutBuilder>) in
			NavigationStack {
				StatisticsWidgetLayoutBuilderView(store: store)
			}
			.interactiveDismissDisabled()
		}
	}

	fileprivate func help(_ store: Binding<StoreOf<StatisticsWidgetHelp>?>) -> some View {
		sheet(item: store) { (store: StoreOf<StatisticsWidgetHelp>) in
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

private struct ContentSizeKey: PreferenceKey, CGSizePreferenceKey {}

#if DEBUG
#Preview {
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
#endif
