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
		@BindingViewState var isDeleting: Bool
		@BindingViewState var isAnimatingWidgets: Bool
		let widgetData: [StatisticsWidget.ID: Statistics.ChartContent]
	}

	public init(store: StoreOf<StatisticsWidgetLayoutBuilder>) {
		self.store = store
	}

	public var body: some View {
		WithViewStore(store, observe: ViewState.init, send: { .view($0) }, content: { viewStore in
			ScrollView {
				LazyVGrid(
					columns: [.init(spacing: .largeSpacing), .init(spacing: .largeSpacing)],
					spacing: .largeSpacing
				) {
					ReorderableView(
						store: store.scope(state: \.reordering, action: { .internal(.reordering($0)) })
					) { widget in
						MoveableWidget(
							configuration: widget,
							chartContent: viewStore.widgetData[widget.id],
							isWiggling: viewStore.$isAnimatingWidgets,
							isShowingDelete: viewStore.$isDeleting,
							onDelete: { viewStore.send(.didTapDeleteWidget(id: widget.id), animation: .easeInOut) }
						)
					}
				}
				.padding(.horizontal, .largeSpacing)

				if viewStore.widgetData.isEmpty {
					Text(Strings.Widget.LayoutBuilder.addNewInstructions)
						.font(.body)
						.multilineTextAlignment(.center)
						.padding()
				} else {
					Text(Strings.Widget.LayoutBuilder.reorderInstructions)
						.font(.caption)
						.multilineTextAlignment(.center)
						.padding(.largeSpacing)
				}
			}
			.toolbar {
				if viewStore.isDeleting {
					ToolbarItem(placement: .navigationBarTrailing) {
						Button(Strings.Action.done) { viewStore.send(.didTapCancelDeleteButton) }
					}
				} else {
					if !viewStore.widgetData.isEmpty {
						ToolbarItem(placement: .navigationBarTrailing) {
							DeleteButton { viewStore.send(.didTapDeleteButton) }
						}
					}

					ToolbarItem(placement: .navigationBarTrailing) {
						AddButton { viewStore.send(.didTapAddNew) }
					}

					ToolbarItem(placement: .navigationBarLeading) {
						Button(Strings.Action.done) { viewStore.send(.didTapDoneButton) }
					}
				}
			}
			.navigationTitle(Strings.Widget.LayoutBuilder.title)
			.task { await viewStore.send(.didObserveData).finish() }
			.sheet(
				store: store.scope(state: \.$editor, action: { .internal(.editor($0)) }),
				onDismiss: { viewStore.send(.didFinishDismissingEditor) },
				content: { store in
					NavigationStack {
						StatisticsWidgetEditorView(store: store)
					}
				}
			)
		})
	}
}

extension StatisticsWidgetLayoutBuilderView.ViewState {
	init(store: BindingViewStore<StatisticsWidgetLayoutBuilder.State>) {
		self._isDeleting = store.$isDeleting
		self._isAnimatingWidgets = store.$isAnimatingWidgets
		self.widgetData = store.widgetData
	}
}
