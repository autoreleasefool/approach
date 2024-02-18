import BowlersRepositoryInterface
import ComposableArchitecture
import FeatureActionLibrary
import FormFeature
import ModelsLibrary
import StringsLibrary
import SwiftUI
import ViewsLibrary

@ViewAction(for: BowlerEditor.self)
public struct BowlerEditorView: View {
	@Perception.Bindable public var store: StoreOf<BowlerEditor>

	public init(store: StoreOf<BowlerEditor>) {
		self.store = store
	}

	public var body: some View {
		WithPerceptionTracking {
			FormView(store: store.scope(state: \.form, action: \.internal.form)) {
				Section(Strings.Editor.Fields.Details.title) {
					TextField(
						Strings.Editor.Fields.Details.name,
						text: $store.name
					)
					.textContentType(.name)
				}
			}
			.onAppear { send(.onAppear) }
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
					reducer: BowlerEditor.init
				)
			)
		}
	}
}
#endif
