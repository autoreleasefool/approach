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
		let widgetData: [StatisticsWidget.ID: Statistics.ChartContent]

		init(state: StatisticsWidgetLayoutBuilder.State) {
			self.isDeleting = state.isDeleting
			self.widgetData = state.widgetData
		}
	}

	enum ViewAction {
		case didObserveData
		case didTapAddNew
		case didTapDeleteButton
		case didTapCancelDeleteButton
		case didTapWidget(id: StatisticsWidget.ID)
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
					) { state in
						MoveableWidget(
							configuration: state.item,
							chartContent: viewStore.widgetData[state.item.id],
							isWiggling: .constant(true),
							isShowingDelete: .constant(viewStore.isDeleting),
							onDelete: { viewStore.send(.didTapWidget(id: state.item.id)) }
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
					ToolbarItem(placement: .navigationBarTrailing) {
						AddButton { viewStore.send(.didTapAddNew) }
					}

					if !viewStore.widgetData.isEmpty {
						ToolbarItem(placement: .navigationBarLeading) {
							DeleteButton { viewStore.send(.didTapDeleteButton) }
						}
					}
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
		case .didTapDeleteButton:
			self = .view(.didTapDeleteButton)
		case .didTapCancelDeleteButton:
			self = .view(.didTapCancelDeleteButton)
		case let .didTapWidget(id):
			self = .view(.didTapWidget(id: id))
		case .didObserveData:
			self = .view(.didObserveData)
		case .didTapAddNew:
			self = .view(.didTapAddNew)
		}
	}
}
