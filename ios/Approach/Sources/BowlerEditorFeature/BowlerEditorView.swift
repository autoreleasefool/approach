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
		let name: String

		init(state: BowlerEditor.State) {
			self.name = state.name
		}
	}

	enum ViewAction {
		case didChangeName(String)
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
						text: viewStore.binding(get: \.name, send: ViewAction.didChangeName)
					)
					.textContentType(.name)
				}
			}
		}
	}
}

extension BowlerEditor.Action {
	init(action: BowlerEditorView.ViewAction) {
		switch action {
		case let .didChangeName(name):
			self = .view(.didChangeName(name))
		}
	}
}

#if DEBUG
struct BowlerEditorViewPreviews: PreviewProvider {
	static var previews: some View {
		NavigationStack {
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
