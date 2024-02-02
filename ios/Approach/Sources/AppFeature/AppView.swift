import ComposableArchitecture
import FeatureActionLibrary
import OnboardingFeature
import SwiftUI
import SwiftUIExtensionsLibrary

@ViewAction(for: App.self)
public struct AppView: View {
	public let store: StoreOf<App>

	public init(store: StoreOf<App>) {
		self.store = store
	}

	public var body: some View {
		WithPerceptionTracking {
			switch store.state {
			case .content:
				if let store = store.scope(state: \.content, action: \.internal.content) {
					TabbedContentView(store: store)
				}
			case .onboarding:
				if let store = store.scope(state: \.onboarding, action: \.internal.onboarding) {
					OnboardingView(store: store)
				}
			}
		}
		.onFirstAppear { send(.didFirstAppear) }
	}
}
