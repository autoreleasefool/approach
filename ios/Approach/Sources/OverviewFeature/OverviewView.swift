import ComposableArchitecture
import SwiftUI

@ViewAction(for: Overview.self)
public struct OverviewView: View {
	@Bindable public var store: StoreOf<Overview>

	public init(store: StoreOf<Overview>) {
		self.store = store
	}

	public var body: some View {
		VStack {
			Text("Welcome to the Overview!")
				.font(.largeTitle)
				.padding()
		}
		.navigationTitle("Overview")
		.navigationBarTitleDisplayMode(.inline)
		.task { await send(.didStartTask).finish() }
	}
}
