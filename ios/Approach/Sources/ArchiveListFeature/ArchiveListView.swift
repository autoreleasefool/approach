import ComposableArchitecture
import ErrorsFeature
import ModelsLibrary
import ModelsViewsLibrary
import StringsLibrary
import SwiftUI
import ViewsLibrary

@ViewAction(for: ArchiveList.self)
public struct ArchiveListView: View {
	@Perception.Bindable public var store: StoreOf<ArchiveList>

	public init(store: StoreOf<ArchiveList>) {
		self.store = store
	}

	public var body: some View {
		WithPerceptionTracking {
			List {
				Section {
					Text(Strings.Archive.List.description)
				}

				Section {
					if store.archived.isEmpty {
						Text(Strings.Archive.List.none)
					} else {
						ForEach(store.archived) { item in
							ArchiveItemView(item: item)
								.swipeActions(allowsFullSwipe: true) {
									UnarchiveButton { send(.didSwipe(item)) }
								}
						}
					}
				}
			}
			.navigationTitle(Strings.Archive.title)
			.task { await send(.observeData).finish() }
			.onAppear { send(.onAppear) }
			.errors(store: store.scope(state: \.errors, action: \.internal.errors))
			.alert($store.scope(state: \.alert, action: \.view.alert))
		}
	}
}
