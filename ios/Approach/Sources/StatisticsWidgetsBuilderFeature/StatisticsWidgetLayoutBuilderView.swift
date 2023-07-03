import AssetsLibrary
import ComposableArchitecture
import ModelsLibrary
import ReorderingLibrary
import StatisticsChartsLibrary
import StatisticsLibrary
import StatisticsRepositoryInterface
import StatisticsWidgetsLibrary
import StringsLibrary
import SwiftUI
import ViewsLibrary

public struct StatisticsWidgetLayoutBuilderView: View {
	let store: StoreOf<StatisticsWidgetLayoutBuilder>

	struct ViewState: Equatable {
		public var widgetData: [StatisticsWidget.ID: Statistics.ChartContent]

		init(state: StatisticsWidgetLayoutBuilder.State) {
			self.widgetData = state.widgetData
		}
	}

	enum ViewAction {
		case didObserveData
		case didTapAddNew
	}

	public init(store: StoreOf<StatisticsWidgetLayoutBuilder>) {
		self.store = store
	}

	public var body: some View {
		WithViewStore(store, observe: ViewState.init, send: StatisticsWidgetLayoutBuilder.Action.init) { viewStore in
			ScrollView {
				LazyVGrid(
					columns: [.init(spacing: .largeSpacing), .init(spacing: .largeSpacing)],
					spacing: .largeSpacing
				) {
					ReorderableView(
						store: store.scope(state: \.reordering, action: { .internal(.reordering($0)) })
					) { widget in
						SquareWidget(configuration: widget, chartContent: viewStore.widgetData[widget.id])
					}
				}
				.padding(.horizontal, .largeSpacing)

				Text(Strings.Widget.LayoutBuilder.instructions)
					.font(.caption)
					.multilineTextAlignment(.center)
					.padding(.largeSpacing)
			}
			.toolbar {
				ToolbarItem(placement: .navigationBarTrailing) {
					AddButton { viewStore.send(.didTapAddNew) }
				}
			}
			.navigationTitle(Strings.Widget.LayoutBuilder.title)
			.task { await viewStore.send(.didObserveData).finish() }
		}
		.sheet(store: store.scope(state: \.$editor, action: { .internal(.editor($0)) })) { store in
			NavigationStack {
				StatisticsWidgetEditorView(store: store)
			}
		}
	}
}

extension StatisticsWidgetLayoutBuilder.Action {
	init(action: StatisticsWidgetLayoutBuilderView.ViewAction) {
		switch action {
		case .didObserveData:
			self = .view(.didObserveData)
		case .didTapAddNew:
			self = .view(.didTapAddNew)
		}
	}
}

public struct SquareWidget: View {
	let configuration: StatisticsWidget.Configuration
	let chartContent: Statistics.ChartContent?

	public init(configuration: StatisticsWidget.Configuration, chartContent: Statistics.ChartContent?) {
		self.configuration = configuration
		self.chartContent = chartContent
	}

	public var body: some View {
		StatisticsWidget.Widget(configuration: configuration, chartContent: chartContent)
			.aspectRatio(1, contentMode: .fit)
			.cornerRadius(.standardRadius)
	}
}
