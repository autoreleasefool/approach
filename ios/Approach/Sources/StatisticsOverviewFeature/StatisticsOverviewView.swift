import AssetsLibrary
import ComposableArchitecture
import PreferenceServiceInterface
import StatisticsDetailsFeature
import StringsLibrary
import SwiftUI
import SwiftUIExtensionsPackageLibrary
import TipsLibrary
import ViewsLibrary

@ViewAction(for: StatisticsOverview.self)
public struct StatisticsOverviewView: View {
	@Bindable public var store: StoreOf<StatisticsOverview>

	public init(store: StoreOf<StatisticsOverview>) {
		self.store = store
	}

	public var body: some View {
		List {
			if store.isShowingOverviewTip {
				Section {
					BasicTipView(tip: .statisticsOverview) {
						send(.didTapDismissOverviewTip, animation: .default)
					}
				}
			}

			if store.isShowingDetailsTip {
				Section {
					BasicTipView(tip: .statisticsDetails) {
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

			if !store.recentlyUsedFilters.isEmpty {
				ForEach(store.recentlyUsedFilters.elements, id: \.key) { filter in
					Section {
						Button { send(.didTapFilter(filter.key)) } label: {
							TrackableFilterView(
								filter: filter.key,
								sources: filter.value
							)
						}
						.buttonStyle(.navigation)
					}
				}
			}
		}
		.navigationTitle(Strings.Statistics.title)
		.onAppear { send(.onAppear) }
		.task { await send(.task).finish() }
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
		.navigationDestination(
			item: $store.scope(state: \.destination?.details, action: \.internal.destination.details)
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
					$0.preferences.bool = { @Sendable _ in false }
				}
			)
		}
	}
}
#endif
