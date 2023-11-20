import BowlersRepositoryInterface
import ComposableArchitecture
import FeatureActionLibrary
import FormFeature
import ModelsLibrary
import StringsLibrary
import SwiftUI
import ViewsLibrary

public struct BowlerEditorView: View {
	let store: StoreOf<BowlerEditor>

	struct ViewState: Equatable {
		@BindingViewState var name: String
	}

	public init(store: StoreOf<BowlerEditor>) {
		self.store = store
	}

	public var body: some View {
		WithViewStore(store, observe: ViewState.init, send: { .view($0) }, content: { viewStore in
			FormView(store: store.scope(state: \.form, action: /BowlerEditor.Action.InternalAction.form)) {
				Section(Strings.Editor.Fields.Details.title) {
					TextField(
						Strings.Editor.Fields.Details.name,
						text: viewStore.$name
					)
					.textContentType(.name)
				}
			}
			.onAppear { viewStore.send(.onAppear) }
		})
	}
}

extension BowlerEditorView.ViewState {
	init(store: BindingViewStore<BowlerEditor.State>) {
		self._name = store.$name
	}
}

#if DEBUG
struct BowlerEditorViewPreviews: PreviewProvider {
	static var previews: some View {
		NavigationStack {
			BowlerEditorView(store:
				.init(
					initialState: .init(value: .create(.defaultBowler(withId: UUID()))),
					reducer: BowlerEditor.init
				)
			)
		}
	}
}
#endif
