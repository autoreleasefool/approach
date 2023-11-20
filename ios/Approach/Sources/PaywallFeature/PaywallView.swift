import ComposableArchitecture
import ProductsLibrary
import SwiftUI

public struct PaywallView<Content: View>: View {
	let store: StoreOf<Paywall>
	let content: Content

	public init(store: StoreOf<Paywall>, @ViewBuilder content: () -> Content) {
		self.store = store
		self.content = content()
	}

	public var body: some View {
		WithViewStore(store, observe: { $0 }, send: { .view($0) }, content: { viewStore in
			content
				.task { await viewStore.send(.didStartTask).finish() }
				.onAppear { viewStore.send(.onAppear) }
				.sheet(isPresented: viewStore.$isPaywallPresented) {
					if viewStore.product == .proSubscription {
						ProPaywallView(viewStore: viewStore)
					}
				}
		})
	}
}
