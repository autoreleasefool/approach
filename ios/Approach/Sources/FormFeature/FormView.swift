import ComposableArchitecture
import ErrorsFeature
import FeatureActionLibrary
import StringsLibrary
import SwiftUI
import ViewsLibrary

public struct FormView<
	New: CreateableRecord,
	Existing: EditableRecord,
	Content: View
>: View where New.ID == Existing.ID {
	@Perception.Bindable public var store: StoreOf<Form<New, Existing>>
	let content: Content

	public init(
		store: StoreOf<Form<New, Existing>>,
		@ViewBuilder content: () -> Content
	) {
		self.store = store
		self.content = content()
	}

	public var body: some View {
		WithPerceptionTracking {
			SwiftUI.Form {
				content
					.disabled(store.isLoading)

				if store.isDeleteable {
					Section {
						DeleteButton { store.send(.view(.didTapDeleteButton)) }
					}
					.disabled(store.isLoading)
				}

				if store.isArchivable {
					Section {
						ArchiveButton { store.send(.view(.didTapArchiveButton)) }
					}
					.disabled(store.isLoading)
				}
			}
			.navigationTitle(store.title)
			.toolbar {
				ToolbarItem(placement: .navigationBarTrailing) {
					Button(store.saveButtonText) { store.send(.view(.didTapSaveButton)) }
						.disabled(!store.isSaveable)
				}

				if store.hasChanges {
					ToolbarItem(placement: .navigationBarLeading) {
						Button(Strings.Action.discard) { store.send(.view(.didTapDiscardButton)) }
					}
				}
			}
			// TODO: enable errors
	//		.errors(store: store.scope(state: \.errors, action: \.internal.errors))
			.alert($store.scope(state: \.alert, action: \.view.alert))
		}
	}
}
