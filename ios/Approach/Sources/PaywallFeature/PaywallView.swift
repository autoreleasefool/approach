import ComposableArchitecture
import ProductsLibrary
import SwiftUI

@ViewAction(for: Paywall.self)
public struct PaywallView<Content: View>: View {
	@Perception.Bindable public var store: StoreOf<Paywall>
	let content: Content

	public init(store: StoreOf<Paywall>, @ViewBuilder content: () -> Content) {
		self.store = store
		self.content = content()
	}

	public var body: some View {
		WithPerceptionTracking {
			content
				.task { await send(.didStartTask).finish() }
				.onAppear { send(.onAppear) }
				.sheet(isPresented: $store.isPaywallPresented) {
					if store.product == .proSubscription {
//						ProPaywallView(viewStore: viewStore)
					}
				}
		}
	}
}
