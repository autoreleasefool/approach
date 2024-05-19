import ComposableArchitecture
import FeatureActionLibrary
import OnboardingFeature
import SwiftUI
import SwiftUIExtensionsPackageLibrary

@ViewAction(for: App.self)
public struct AppView: View {
	public let store: StoreOf<App>

	public init(store: StoreOf<App>) {
		self.store = store
	}

	public var body: some View {
		content
			.onFirstAppear { send(.didFirstAppear) }
	}

	@MainActor @ViewBuilder private var content: some View {
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
}
