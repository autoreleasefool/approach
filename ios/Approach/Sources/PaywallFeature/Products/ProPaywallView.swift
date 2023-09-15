import ComposableArchitecture
import SwiftUI

public struct ProPaywallView: View {
	@ObservedObject var viewStore: ViewStore<Paywall.State, Paywall.Action.ViewAction>

	public var body: some View {
		EmptyView()
	}
}
