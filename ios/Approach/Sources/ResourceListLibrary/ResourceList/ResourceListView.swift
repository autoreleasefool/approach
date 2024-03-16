import AssetsLibrary
import ComposableArchitecture
import ListContentLibrary
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
	public var store: StoreOf<ResourceList<R, Q>>

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
		SectionResourceListView(
			store: store.scope(state: \.sectionList, action: \.internal.sectionList),
			row: { _, element in row(element) },
			header: header,
			footer: footer
		)
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
