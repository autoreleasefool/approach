import BowlersRepositoryInterface
import ComposableArchitecture
import FeatureActionLibrary
import FormLibrary
import ModelsLibrary
import StringsLibrary
import SwiftUI
import ViewsLibrary

public struct BowlerEditorView: View {
	let store: StoreOf<BowlerEditor>

	struct ViewState: Equatable {
		@BindingState var name = ""

		init(state: BowlerEditor.State) {
			self.name = state.name
		}
	}

	enum ViewAction: BindableAction {
		case binding(BindingAction<ViewState>)
	}

	public init(store: StoreOf<BowlerEditor>) {
		self.store = store
	}

	public var body: some View {
		WithViewStore(store, observe: ViewState.init, send: BowlerEditor.Action.init) { viewStore in
			FormView(store: store.scope(state: \.form, action: /BowlerEditor.Action.InternalAction.form)) {
				Section(Strings.Editor.Fields.Details.title) {
					TextField(
						Strings.Editor.Fields.Details.name,
						text: viewStore.binding(\.$name)
					)
					.textContentType(.name)
				}
				.listRowBackground(Color(uiColor: .secondarySystemBackground))
			}
		}
	}
}

extension BowlerEditor.State {
	var view: BowlerEditorView.ViewState {
		get { .init(state: self) }
		set {
			self.name = newValue.name
		}
	}
}

extension BowlerEditor.Action {
	init(action: BowlerEditorView.ViewAction) {
		switch action {
		case let .binding(action):
			self = .binding(action.pullback(\BowlerEditor.State.view))
		}
	}
}

#if DEBUG
struct BowlerEditorViewPreviews: PreviewProvider {
	static var previews: some View {
		NavigationView {
			BowlerEditorView(store:
				.init(
					initialState: .init(value: .create(.defaultBowler(withId: UUID()))),
					reducer: BowlerEditor()
				)
			)
		}
	}
}
#endif
