import AssetsLibrary
import ComposableArchitecture
import ListContentLibrary
import StringsLibrary
import SwiftUI
import SwiftUIExtensionsPackageLibrary
import ViewsLibrary

public struct SectionResourceListView<
	R: ResourceListItem,
	Q: Equatable & Sendable,
	Row: View,
	Header: View,
	Footer: View
>: View {
	public typealias SectionList = SectionResourceList<R, Q>
	@Bindable public var store: StoreOf<SectionList>

	let row: (SectionList.Section.ID, R) -> Row
	let header: () -> Header
	let footer: () -> Footer

	public init(
		store: StoreOf<SectionList>,
		@ViewBuilder row: @escaping (SectionList.Section.ID, R) -> Row,
		@ViewBuilder header: @escaping () -> Header,
		@ViewBuilder footer: @escaping () -> Footer
	) {
		self.store = store
		self.row = row
		self.header = header
		self.footer = footer
	}

	public init(
		store: StoreOf<SectionList>,
		@ViewBuilder row: @escaping (SectionList.Section.ID, R) -> Row,
		@ViewBuilder footer: @escaping () -> Footer
	) where Header == EmptyView {
		self.init(store: store, row: row, header: { EmptyView() }, footer: footer)
	}

	public init(
		store: StoreOf<SectionList>,
		@ViewBuilder row: @escaping (SectionList.Section.ID, R) -> Row,
		@ViewBuilder header: @escaping () -> Header
	) where Footer == EmptyView {
		self.init(store: store, row: row, header: header, footer: { EmptyView() })
	}

	public init(
		store: StoreOf<SectionList>,
		@ViewBuilder row: @escaping (SectionList.Section.ID, R) -> Row
	) where Header == EmptyView, Footer == EmptyView {
		self.init(store: store, row: row, header: { EmptyView() }, footer: { EmptyView() })
	}

	public var body: some View {
		listContent
			.toolbar {
				if store.features.contains(.add) {
					ToolbarItem(placement: .navigationBarTrailing) {
						AddButton { store.send(.view(.didTapAddButton)) }
					}
				}
			}
			.alert($store.scope(state: \.alert, action: \.view.alert))
			.onAppear { store.send(.view(.onAppear)) }
			.task { await store.send(.view(.task)).finish() }
	}

	@ViewBuilder private var listContent: some View {
		switch store.listContent {
		case .notLoaded:
			List {
				Color.clear
			}
			.listStyle(.insetGrouped)

		case .loading:
			List {
				ListProgressView()
			}
			.listStyle(.insetGrouped)

		case let .loaded(sections):
			if sections.isEmpty {
				ResourceListEmptyView(
					store: store.scope(state: \.emptyState, action: \.internal.empty)
				)
			} else {
				List {
					header()

					ForEach(sections) { section in
						Section {
							ForEach(section.items) { element in
								buildRow(forElement: element, inSection: section)
									.swipeActions(allowsFullSwipe: true) {
										if store.editMode != .active {
											if store.features.contains(.swipeToEdit) {
												EditButton { store.send(.view(.didSwipe(.edit, element))) }
											}

											if store.features.contains(.swipeToDelete) {
												DeleteButton { store.send(.view(.didSwipe(.delete, element))) }
											}

											if store.features.contains(.swipeToArchive) {
												ArchiveButton { store.send(.view(.didSwipe(.archive, element))) }
											}
										}
									}
									.moveDisabled(store.editMode != .active || !store.features.contains(.moveable))
							}
							.onMove { store.send(.view(.didMove(section: section.id, source: $0, destination: $1))) }
						} header: {
							if section.items.isEmpty {
								EmptyView()
							} else {
								if store.features.contains(.moveable) && section.items.count > 1 {
									reorderableHeader(title: store.listTitle, editMode: store.editMode) {
										store.send(.view(.didTapReorderButton))
									}
								} else if let title = section.title {
									Text(title)
								} else if sections.first == section, let title = store.listTitle {
									Text(title)
								}
							}
						}
					}

					footer()
				}
				.listStyle(.insetGrouped)
				.environment(\.editMode, $store.editMode)
			}

		case .error:
			if let childStore = store.scope(state: \.errorState, action: \.internal.error) {
				ResourceListEmptyView(store: childStore)
			}
		}
	}

	@ViewBuilder
	private func buildRow(
		forElement element: R,
		inSection section: SectionList.Section
	) -> some View {
		if store.features.contains(.tappable) && store.editMode != .active {
			Button {
				store.send(.view(.didTap(element)))
			} label: {
				row(section.id, element)
			}
		} else {
			row(section.id, element)
		}
	}

	private func reorderableHeader(title: String?, editMode: EditMode, perform: @escaping () -> Void) -> some View {
		HStack(alignment: .firstTextBaseline) {
			if let title {
				Text(title)
			}
			Spacer()
			Button(action: perform) {
				if editMode == .inactive {
					Label(Strings.Action.reorder, systemImage: "arrow.up.and.down.square")
						.font(.caption)
				} else if editMode == .active {
					Label(Strings.Action.finish, systemImage: "checkmark.circle")
						.font(.caption)
				}
			}
		}
	}
}
