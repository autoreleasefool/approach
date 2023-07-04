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
		let isDeleting: Bool
		let isAnimatingWidgets: Bool
		let widgetData: [StatisticsWidget.ID: Statistics.ChartContent]

		init(state: StatisticsWidgetLayoutBuilder.State) {
			self.isDeleting = state.isDeleting
			self.isAnimatingWidgets = state.isAnimatingWidgets
			self.widgetData = state.widgetData
		}
	}

	enum ViewAction {
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
						MoveableWidget(
							configuration: widget,
							chartContent: viewStore.widgetData[widget.id],
							isWiggling: viewStore.binding(get: \.isAnimatingWidgets, send: ViewAction.setAnimateWidgets),
							isShowingDelete: viewStore.binding(get: \.isDeleting, send: ViewAction.setDelete),
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
				onDismiss: { viewStore.send(.didFinishDismissingEditor) }
			) { store in
				NavigationStack {
					StatisticsWidgetEditorView(store: store)
				}
			}
		}
	}
}

extension StatisticsWidgetLayoutBuilder.Action {
	init(action: StatisticsWidgetLayoutBuilderView.ViewAction) {
		switch action {
		case .didTapDeleteButton:
			self = .view(.didTapDeleteButton)
		case .didTapCancelDeleteButton:
			self = .view(.didTapCancelDeleteButton)
		case let .didTapDeleteWidget(id):
			self = .view(.didTapDeleteWidget(id: id))
		case .didObserveData:
			self = .view(.didObserveData)
		case .didTapAddNew:
			self = .view(.didTapAddNew)
		case .didTapDoneButton:
			self = .view(.didTapDoneButton)
		case .didFinishDismissingEditor:
			self = .view(.didFinishDismissingEditor)
		case let .setAnimateWidgets(value):
			self = .view(.setAnimateWidgets(value))
		case let .setDelete(value):
			self = .view(.setDelete(value))
		}
	}
}
