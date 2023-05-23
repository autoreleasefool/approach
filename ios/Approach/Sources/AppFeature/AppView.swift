import ComposableArchitecture
import FeatureActionLibrary
import OnboardingFeature
import SwiftUI

public struct AppView: View {
	let store: StoreOf<App>

	public init(store: StoreOf<App>) {
		self.store = store
	}

	public var body: some View {
		SwitchStore(store) { state in
			switch state {
			case .content:
				CaseLet(/App.State.content, action: { App.Action.internal(.content($0)) }, then: { store in
					TabbedContentView(store: store)
				})
			case .onboarding:
				CaseLet(/App.State.onboarding, action: { App.Action.internal(.onboarding($0)) }, then: { store in
					OnboardingView(store: store)
				})
			}
		}
	}
}
