import AssetsLibrary
import ComposableArchitecture
import ListContentLibrary
import StringsLibrary
import SwiftUI
import ViewsLibrary

public struct SectionResourceListView<
	R: ResourceListItem,
	Q: Equatable,
	Row: View,
	Header: View,
	Footer: View
>: View {
	public typealias SectionList = SectionResourceList<R, Q>
	let store: StoreOf<SectionList>

	struct ViewState: Equatable {
		let listContent: ListContent
		let listTitle: String?
		let features: [SectionList.Feature]

		init(state: SectionList.State) {
			self.features = state.features
			self.listTitle = state.listTitle
			if state.errorState != nil {
				self.listContent = .error
			} else if let sections = state.sections {
				self.listContent = .loaded(sections)
			} else {
				self.listContent = .notLoaded
			}
		}
	}

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
		WithViewStore(store, observe: ViewState.init, send: { .view($0) }, content: { viewStore in
			Group {
				switch viewStore.listContent {
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
							store: store.scope(state: \.emptyState, action: { SectionList.Action.internal(.empty($0)) })
						)
					} else {
						List {
							header()

							ForEach(sections) { section in
								Section {
									ForEach(section.items) { element in
										Group {
											if viewStore.features.contains(.tappable) {
												Button {
													viewStore.send(.didTap(element))
												} label: {
													row(section.id, element)
												}
											} else {
												row(section.id, element)
											}
										}
										.swipeActions(allowsFullSwipe: true) {
											if viewStore.features.contains(.swipeToEdit) {
												EditButton { viewStore.send(.didSwipe(.edit, element)) }
											}

											if viewStore.features.contains(.swipeToDelete) {
												DeleteButton { viewStore.send(.didSwipe(.delete, element)) }
											}

											if viewStore.features.contains(.swipeToArchive) {
												ArchiveButton { viewStore.send(.didSwipe(.archive, element)) }
											}
										}
									}
								} header: {
									if let title = viewStore.listTitle {
										Text(title)
									}
								}
							}

							footer()
						}
						.listStyle(.insetGrouped)
					}

				case .error:
					IfLetStore(
						store.scope(state: \.errorState, action: { SectionList.Action.internal(.error($0)) })
					) {
						ResourceListEmptyView(store: $0)
					}
				}
			}
			.toolbar {
				if viewStore.features.contains(.add) {
					ToolbarItem(placement: .navigationBarTrailing) {
						AddButton { viewStore.send(.didTapAddButton) }
					}
				}
			}
			.alert(store: store.scope(state: \.$alert, action: { .view(.alert($0)) }))
			.task { await viewStore.send(.didObserveData).finish() }
		})
	}
}

extension SectionResourceListView {
	enum ListContent: Equatable {
		case notLoaded
		case loading
		case loaded(IdentifiedArrayOf<SectionList.Section>)
		case error
	}
}
