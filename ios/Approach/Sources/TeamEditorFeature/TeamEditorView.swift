import AssetsLibrary
import ComposableArchitecture
import FormFeature
import ModelsLibrary
import ModelsViewsLibrary
import StringsLibrary
import SwiftUI
import TeamsRepositoryInterface
import ViewsLibrary

@ViewAction(for: TeamEditor.self)
public struct TeamEditorView: View {
	@Bindable public var store: StoreOf<TeamEditor>

	public init(store: StoreOf<TeamEditor>) {
		self.store = store
	}

	public var body: some View {
		FormView(store: store.scope(state: \.form, action: \.internal.form)) {
			detailsSection
		}
		.onAppear { send(.onAppear) }
	}

	private var detailsSection: some View {
		Section(Strings.Editor.Fields.Details.title) {
			TextField(
				Strings.Editor.Fields.Details.name,
				text: $store.name
			)
		}
	}
}
