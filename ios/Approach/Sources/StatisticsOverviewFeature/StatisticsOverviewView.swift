import AssetsLibrary
import ComposableArchitecture
import StatisticsDetailsFeature
import StringsLibrary
import SwiftUI
import SwiftUIExtensionsLibrary
import TipsLibrary
import ViewsLibrary

public struct StatisticsOverviewView: View {
	let store: StoreOf<StatisticsOverview>

	struct ViewState: Equatable {
		let isShowingOverviewTip: Bool
		let isShowingDetailsTip: Bool

		init(state: StatisticsOverview.State) {
			self.isShowingOverviewTip = state.isShowingOverviewTip
			self.isShowingDetailsTip = state.isShowingDetailsTip
		}
	}

	public init(store: StoreOf<StatisticsOverview>) {
		self.store = store
	}

	public var body: some View {
		WithViewStore(store, observe: ViewState.init, send: { .view($0) }, content: { viewStore in
			List {
				if viewStore.isShowingOverviewTip {
					Section {
						// TODO: URGENT Remove `isDismissable: false` to allow this tip to be dismissed
						BasicTipView(tip: .statisticsOverview, isDismissable: false) { viewStore.send(.didTapDismissOverviewTip, animation: .default) }
					}
				}

				if viewStore.isShowingDetailsTip {
					Section {
						// TODO: URGENT Remove `isDismissable: false` to allow this tip to be dismissed
						BasicTipView(tip: .statisticsDetails, isDismissable: false) { viewStore.send(.didTapDismissDetailsTip, animation: .default) }
					}
				}

				Section {
					Button { viewStore.send(.didTapViewDetailedStatistics) } label: {
						Text(Strings.Statistics.Overview.viewDetailedStatistics)
					}
					.buttonStyle(.navigation)
				}
			}
			.navigationTitle(Strings.Statistics.title)
			.sheet(
				store: store.scope(state: \.$destination, action: { .internal(.destination($0)) }),
				state: /StatisticsOverview.Destination.State.sourcePicker,
				action: StatisticsOverview.Destination.Action.sourcePicker,
				onDismiss: { viewStore.send(.sourcePickerDidDismiss) },
				content: { store in
					NavigationStack {
						StatisticsSourcePickerView(store: store)
					}
					.presentationDetents([.medium, .large])
				}
			)
		})
		.navigationDestination(
			store: store.scope(state: \.$destination, action: { .internal(.destination($0)) }),
			state: /StatisticsOverview.Destination.State.details,
			action: StatisticsOverview.Destination.Action.details
		) { store in
			StatisticsDetailsView(store: store)
		}
	}
}

#if DEBUG
struct StatisticsOverviewPreviews: PreviewProvider {
	static var previews: some View {
		NavigationStack {
			StatisticsOverviewView(
				store: .init(initialState: .init()) {
					StatisticsOverview()
				} withDependencies: {
					$0.preferences.getBool = { _ in false }
				}
			)
		}
	}
}
#endif
