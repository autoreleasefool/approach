import AssetsLibrary
import ComposableArchitecture
import ErrorsFeature
import SharingFeature
import StatisticsLibrary
import StringsLibrary
import SwiftUI
import SwiftUIExtensionsPackageLibrary
import ViewsLibrary

@ViewAction(for: StatisticsDetails.self)
public struct StatisticsDetailsView: View {
	@Bindable public var store: StoreOf<StatisticsDetails>

	@Environment(\.continuousClock) private var clock
	@Environment(\.safeAreaInsets) private var safeAreaInsets
	@State private var sheetContentSize: CGSize = .zero
	@State private var windowContentSize: CGSize = .zero

	public init(store: StoreOf<StatisticsDetails>) {
		self.store = store
	}

	public var body: some View {
		VStack {
			VStack {
				if let sources = store.sources {
					StatisticsFilterView(
						sources: sources,
						filter: store.filter,
						size: store.filtersSize
					)
				}

				StatisticsDetailsChartsView(
					store: store.scope(state: \.charts, action: \.internal.charts)
				)
				.padding(.horizontal)
				.layoutPriority(1)
			}
			.frame(
				idealWidth: store.backdropSize.width,
				maxHeight: store.backdropSize.height == .zero ? nil : store.backdropSize.height
			)

			Spacer()
		}
		.measure(key: WindowContentSizeKey.self, to: $windowContentSize)
		.toolbar(.hidden, for: .tabBar)
		.navigationTitle(store.sources?.bowler.name ?? "")
		.navigationBarTitleDisplayMode(.inline)
		.toolbar {
			ToolbarItem(placement: .navigationBarTrailing) {
				ShareButton { send(.didTapShareButton) }
			}

			ToolbarItem(placement: .navigationBarTrailing) {
				FilterButton(isActive: false) { send(.didTapSourcePicker) }
			}
		}
		.sheet(
			item: $store.scope(state: \.destination?.list, action: \.internal.destination.list)
		) { store in
			NavigationStack {
				StatisticsDetailsListView(store: store)
					.toolbar(.hidden, for: .navigationBar)
			}
			.presentationBackgroundInteraction(.enabled(upThrough: .medium))
			.presentationDetents(
				[
					StatisticsDetails.defaultSheetDetent,
					.medium,
					.large,
				],
				selection: $store.sheetDetent
			)
			.interactiveDismissDisabled()
			.measure(key: SheetContentSizeKey.self, to: $sheetContentSize)
		}
		.onChange(of: store.willAdjustLaneLayoutAt) {
			send(
				.didAdjustChartSize(
					backdropSize: measuredBackdropSize,
					filtersSize: store.filterViewSize
				),
				animation: .easeInOut
			)
		}
		.onChange(of: sheetContentSize) {
			send(
				.didAdjustChartSize(
					backdropSize: measuredBackdropSize,
					filtersSize: store.filterViewSize
				),
				animation: .easeInOut
			)
		}
		.task { await send(.didFirstAppear).finish() }
		.onAppear { send(.onAppear) }
		.errors(store: store.scope(state: \.errors, action: \.internal.errors))
		.sheet(
			item: $store.scope(state: \.destination?.sourcePicker, action: \.internal.destination.sourcePicker)
		) { store in
			NavigationStack {
				StatisticsSourcePickerView(store: store)
			}
			.presentationDetents([.medium, .large])
		}
		.sourcePicker($store.scope(state: \.destination?.sourcePicker, action: \.internal.destination.sourcePicker))
		.sharing($store.scope(state: \.destination?.sharing, action: \.internal.destination.sharing))
	}

	private var measuredBackdropSize: CGSize {
		let sheetContentSize = store.ignoreSheetSizeForBackdrop ? .zero : self.sheetContentSize
		return .init(
			width: windowContentSize.width,
			height: windowContentSize.height - sheetContentSize.height - safeAreaInsets.bottom
		)
	}
}

private struct SheetContentSizeKey: PreferenceKey, CGSizePreferenceKey {}
private struct WindowContentSizeKey: PreferenceKey, CGSizePreferenceKey {}

@MainActor extension View {
	fileprivate func sharing(_ store: Binding<StoreOf<Sharing>?>) -> some View {
		sheet(item: store) { store in
			NavigationStack {
				SharingView(store: store)
			}
		}
	}

	fileprivate func sourcePicker(_ store: Binding<StoreOf<StatisticsSourcePicker>?>) -> some View {
		sheet(item: store) { store in
			NavigationStack {
				StatisticsSourcePickerView(store: store)
			}
			.presentationDetents([.medium, .large])
		}
	}
}
