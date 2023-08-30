import ComposableArchitecture
import SwiftUI

public struct SharingView: View {
	let store: StoreOf<Sharing>

	public init(store: StoreOf<Sharing>) {
		self.store = store
	}

	public var body: some View {
		WithViewStore(store, observe: { $0 }, send: { .view($0) }, content: { viewStore in
			EmptyView()
		})
		.errors(store: store.scope(state: \.errors, action: { .internal(.errors($0)) }))
	}
}
