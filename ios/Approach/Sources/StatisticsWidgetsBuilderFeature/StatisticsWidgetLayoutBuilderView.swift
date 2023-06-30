import ComposableArchitecture
import StatisticsWidgetsLibrary
import StringsLibrary
import SwiftUI
import ViewsLibrary

public struct StatisticsWidgetLayoutBuilderView: View {
	let store: StoreOf<StatisticsWidgetLayoutBuilder>

	struct ViewState: Equatable {
		init(state: StatisticsWidgetLayoutBuilder.State) {
		}
	}

	enum ViewAction {
		case didTapAddNew
	}

	public init(store: StoreOf<StatisticsWidgetLayoutBuilder>) {
		self.store = store
	}

	public var body: some View {
		WithViewStore(store, observe: ViewState.init, send: StatisticsWidgetLayoutBuilder.Action.init) { viewStore in
			List {
				// TODO: add recently created widgets
			}
			.toolbar {
				ToolbarItem(placement: .navigationBarTrailing) {
					AddButton { viewStore.send(.didTapAddNew) }
				}
			}
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
		case .didTapAddNew:
			self = .view(.didTapAddNew)
		}
	}
}
