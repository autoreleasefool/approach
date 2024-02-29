import AssetsLibrary
import ComposableArchitecture
import ErrorsFeature
import ModelsLibrary
import ReorderingLibrary
import StatisticsChartsLibrary
import StatisticsLibrary
import StatisticsRepositoryInterface
import StatisticsWidgetsLibrary
import StringsLibrary
import SwiftUI
import ViewsLibrary

@ViewAction(for: StatisticsWidgetLayoutBuilder.self)
public struct StatisticsWidgetLayoutBuilderView: View {
	@Perception.Bindable public var store: StoreOf<StatisticsWidgetLayoutBuilder>

	public init(store: StoreOf<StatisticsWidgetLayoutBuilder>) {
		self.store = store
	}

	public var body: some View {
		WithPerceptionTracking {
			ScrollView {
				LazyVGrid(
					columns: [.init(spacing: .largeSpacing), .init(spacing: .largeSpacing)],
					spacing: .largeSpacing
				) {
					ReorderableView(
						store: store.scope(state: \.reordering, action: \.internal.reordering)
					) { widget in
						MoveableWidget(
							configuration: widget,
							chartContent: store.widgetData[widget.id],
							isWiggling: $store.isAnimatingWidgets,
							isShowingDelete: $store.isDeleting,
							onDelete: { send(.didTapDeleteWidget(id: widget.id), animation: .easeInOut) }
						)
					}
				}
				.padding(.horizontal, .largeSpacing)

				if store.widgetData.isEmpty {
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
				if store.isDeleting {
					ToolbarItem(placement: .navigationBarTrailing) {
						Button(Strings.Action.done) { send(.didTapCancelDeleteButton) }
					}
				} else {
					if !store.widgetData.isEmpty {
						ToolbarItem(placement: .navigationBarTrailing) {
							DeleteButton { send(.didTapDeleteButton) }
						}
					}

					ToolbarItem(placement: .navigationBarTrailing) {
						AddButton { send(.didTapAddNew) }
					}

					ToolbarItem(placement: .navigationBarLeading) {
						Button(Strings.Action.done) { send(.didTapDoneButton) }
					}
				}
			}
			.navigationTitle(Strings.Widget.LayoutBuilder.title)
			.task { await send(.task).finish() }
			.onAppear { send(.onAppear) }
			.sheet(
				item: $store.scope(state: \.editor, action: \.internal.editor),
				onDismiss: { send(.didFinishDismissingEditor) },
				content: { store in
					NavigationStack {
						StatisticsWidgetEditorView(store: store)
					}
				}
			)
			.errors(store: store.scope(state: \.errors, action: \.internal.errors))
		}
	}
}
