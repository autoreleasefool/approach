import AssetsLibrary
import ComposableArchitecture
import StatisticsDetailsFeature
import StringsLibrary
import SwiftUI
import SwiftUIExtensionsLibrary
import TipsLibrary
import ViewsLibrary

@ViewAction(for: StatisticsOverview.self)
public struct StatisticsOverviewView: View {
	@Perception.Bindable public var store: StoreOf<StatisticsOverview>

	public init(store: StoreOf<StatisticsOverview>) {
		self.store = store
	}

	public var body: some View {
		WithPerceptionTracking {
			List {
				if store.isShowingOverviewTip {
					Section {
						// TODO: URGENT Remove `isDismissable: false` to allow this tip to be dismissed
						BasicTipView(
							tip: .statisticsOverview,
							isDismissable: false
						) {
							send(.didTapDismissOverviewTip, animation: .default)
						}
					}
				}

				if store.isShowingDetailsTip {
					Section {
						// TODO: URGENT Remove `isDismissable: false` to allow this tip to be dismissed
						BasicTipView(
							tip: .statisticsDetails,
							isDismissable: false
						) {
							send(.didTapDismissDetailsTip, animation: .default)
						}
					}
				}

				Section {
					Button { send(.didTapViewDetailedStatistics) } label: {
						Text(Strings.Statistics.Overview.viewDetailedStatistics)
					}
					.buttonStyle(.navigation)
				}
			}
			.navigationTitle(Strings.Statistics.title)
			.onAppear { send(.onAppear) }
			.sheet(
				item: $store.scope(state: \.destination?.sourcePicker, action: \.internal.destination.sourcePicker),
				onDismiss: { send(.sourcePickerDidDismiss) },
				content: { store in
					NavigationStack {
						StatisticsSourcePickerView(store: store)
					}
					.presentationDetents([.medium, .large])
				}
			)
			.navigationDestinationWrapper(
				item: $store.scope(state: \.destination?.details, action: \.internal.destination.details)
			) { store in
				StatisticsDetailsView(store: store)
			}
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
