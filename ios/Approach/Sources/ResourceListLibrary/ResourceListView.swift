import AssetsLibrary
import ComposableArchitecture
import StringsLibrary
import SwiftUI
import ViewsLibrary

public struct ResourceListView<
	R: ResourceListItem,
	Q: Equatable,
	Row: View,
	Header: View,
	Footer: View
>: View {
	let store: StoreOf<ResourceList<R, Q>>

	struct ViewState: Equatable {
		let listContent: ListContent
		let listTitle: String?
		let features: [ResourceList<R, Q>.Feature]
		let hasDeleteFeature: Bool

		init(state: ResourceList<R, Q>.State) {
			self.features = state.features
			self.listTitle = state.listTitle
			self.hasDeleteFeature = state.hasDeleteFeature
			if state.errorState != nil {
				self.listContent = .error
			} else if let resources = state.resources {
				self.listContent = .loaded(resources)
			} else {
				self.listContent = .notLoaded
			}
		}
	}

	let row: (R) -> Row
	let header: () -> Header
	let footer: () -> Footer

	public init(
		store: StoreOf<ResourceList<R, Q>>,
		@ViewBuilder row: @escaping (R) -> Row,
		@ViewBuilder header: @escaping () -> Header,
		@ViewBuilder footer: @escaping () -> Footer
	) {
		self.store = store
		self.row = row
		self.header = header
		self.footer = footer
	}

	public init(
		store: StoreOf<ResourceList<R, Q>>,
		@ViewBuilder row: @escaping (R) -> Row,
		@ViewBuilder footer: @escaping () -> Footer
	) where Header == EmptyView {
		self.init(store: store, row: row, header: { EmptyView() }, footer: footer)
	}

	public init(
		store: StoreOf<ResourceList<R, Q>>,
		@ViewBuilder row: @escaping (R) -> Row,
		@ViewBuilder header: @escaping () -> Header
	) where Footer == EmptyView {
		self.init(store: store, row: row, header: header, footer: { EmptyView() })
	}

	public init(
		store: StoreOf<ResourceList<R, Q>>,
		@ViewBuilder row: @escaping (R) -> Row
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

				case let .loaded(elements):
					if elements.isEmpty {
						ResourceListEmptyView(
							store: store.scope(state: \.emptyState, action: { ResourceList<R, Q>.Action.internal(.empty($0)) })
						)
					} else {
						List {
							header()
							Section {
								ForEach(elements) { element in
									Group {
										if viewStore.features.contains(.tappable) {
											Button {
												viewStore.send(.didTap(element))
											} label: {
												row(element)
											}
										} else {
											row(element)
										}
									}
									.swipeActions(allowsFullSwipe: true) {
										if viewStore.features.contains(.swipeToEdit) {
											EditButton { viewStore.send(.didSwipe(.edit, element)) }
										}

										if viewStore.hasDeleteFeature {
											DeleteButton { viewStore.send(.didSwipe(.delete, element)) }
										}
									}
								}
							} header: {
								if let title = viewStore.listTitle {
									Text(title)
								}
							}
							footer()
						}
						.listStyle(.insetGrouped)
					}

				case .error:
					IfLetStore(
						store.scope(state: \.errorState, action: { ResourceList<R, Q>.Action.internal(.error($0)) })
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

extension ResourceListView {
	enum ListContent: Equatable {
		case notLoaded
		case loading
		case loaded(IdentifiedArrayOf<R>)
		case error
	}
}
