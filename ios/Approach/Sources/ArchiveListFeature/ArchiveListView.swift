import ComposableArchitecture
import ErrorsFeature
import ModelsLibrary
import ModelsViewsLibrary
import StringsLibrary
import SwiftUI
import ViewsLibrary

public struct ArchiveListView: View {
	let store: StoreOf<ArchiveList>

	public init(store: StoreOf<ArchiveList>) {
		self.store = store
	}

	public var body: some View {
		WithViewStore(store, observe: { $0 }, send: { .view($0) }, content: { viewStore in
			List {
				Section {
					Text(Strings.Archive.List.description)
				}

				Section {
					if viewStore.archived.isEmpty {
						Text(Strings.Archive.List.none)
					} else {
						ForEach(viewStore.archived) { item in
							ArchiveItemView(item: item)
								.swipeActions(allowsFullSwipe: true) {
									UnarchiveButton { viewStore.send(.didSwipe(item)) }
								}
						}
					}
				}
			}
			.navigationTitle(Strings.Archive.title)
			.task { await viewStore.send(.observeData).finish() }
			.errors(store: store.scope(state: \.errors, action: { .internal(.errors($0)) }))
			.alert(store: store.scope(state: \.$alert, action: { .view(.alert($0)) }))
		})
	}
}
