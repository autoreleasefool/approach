import AssetsLibrary
import ComposableArchitecture
import ErrorsFeature
import StatisticsLibrary
import StringsLibrary
import SwiftUI
import SwiftUIExtensionsLibrary
import ViewsLibrary

public struct MidGameStatisticsDetailsView: View {
	let store: StoreOf<MidGameStatisticsDetails>

	struct ViewState: Equatable {
//		let sources: TrackableFilter.Sources?

		init(state: MidGameStatisticsDetails.State) {

		}
	}

	public init(store: StoreOf<MidGameStatisticsDetails>) {
		self.store = store
	}

	public var body: some View {
		WithViewStore(store, observe: ViewState.init, send: { .view($0) }, content: { viewStore in
			VStack {
				StatisticsDetailsListView(
					store: store.scope(state: \.list, action: { .internal(.list($0)) })
				)
			}
//			.navigationTitle(viewStore.sources?.bowler.name ?? "")
			.navigationBarTitleDisplayMode(.inline)
			.task { await viewStore.send(.didFirstAppear).finish() }
		})
		.errors(store: store.scope(state: \.errors, action: { .internal(.errors($0)) }))
	}
}
