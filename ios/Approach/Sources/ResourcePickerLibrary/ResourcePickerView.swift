import AssetsLibrary
import ComposableArchitecture
import ListContentLibrary
import StringsLibrary
import SwiftUI
import SwiftUIExtensionsPackageLibrary
import ViewsLibrary

public struct ResourcePickerView<Resource: PickableResource, Query: Equatable & Sendable, Row: View>: View {
	public var store: StoreOf<ResourcePicker<Resource, Query>>
	let row: (Resource) -> Row

	public init(store: StoreOf<ResourcePicker<Resource, Query>>, @ViewBuilder row: @escaping (Resource) -> Row) {
		self.store = store
		self.row = row
	}

	public var body: some View {
		ListContent(store.listState) { resources in
			Section {
				ForEach(resources) { resource in
					Button {
						store.send(.view(.didTapResource(resource)))
					} label: {
						HStack(alignment: .center, spacing: .standardSpacing) {
							Image(systemName: store.selected.contains(resource.id) ? "checkmark.circle.fill" : "circle")
								.resizable()
								.frame(width: .smallIcon, height: .smallIcon)
								.foregroundStyle(Asset.Colors.Action.default)
							row(resource)
								.frame(maxWidth: .infinity, alignment: .leading)
						}
						.frame(maxWidth: .infinity)
						.contentShape(Rectangle())
					}
					.buttonStyle(TappableElement())
				}
			}

			Section {
				Button(role: .destructive) {
					store.send(.view(.didTapDeselectAllButton))
				} label: {
					Label(Strings.Action.deselectAll, systemImage: "trash")
				}
				.disabled(store.selected.isEmpty)
			}
		} empty: {
			ListEmptyContent(
				Asset.Media.EmptyState.picker,
				title: Strings.Picker.Empty.title
			) {
				EmptyContentAction(title: Strings.Action.cancel) {
					store.send(.view(.didTapCancelButton))
				}
			}
		} error: { error in
			ListEmptyContent(
				Asset.Media.Error.notFound,
				title: error.title,
				message: error.message,
				style: .error
			) {
				EmptyContentAction(title: error.action) {
					store.send(.view(.didTapCancelButton))
				}
			}
		}
		.navigationTitle(Strings.Picker.title(Resource.pickableModelName(forCount: store.limit)))
		.toolbar {
			if store.isCancellable {
				ToolbarItem(placement: .navigationBarLeading) {
					Button(Strings.Action.cancel) {
						store.send(.view(.didTapCancelButton))
					}
				}
			}

			ToolbarItem(placement: .navigationBarTrailing) {
				Button(Strings.Action.save) {
					store.send(.view(.didTapSaveButton))
				}
			}
		}
		.navigationBarBackButtonHidden(store.isCancellable)
		.onAppear { store.send(.view(.onAppear)) }
		.task { await store.send(.view(.task)).finish() }
	}
}
