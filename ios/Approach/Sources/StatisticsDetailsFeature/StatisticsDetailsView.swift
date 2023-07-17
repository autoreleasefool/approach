import AssetsLibrary
import ComposableArchitecture
import StatisticsLibrary
import StringsLibrary
import SwiftUI
import SwiftUIExtensionsLibrary
import ViewsLibrary

public struct StatisticsDetailsView: View {
	let store: StoreOf<StatisticsDetails>
	typealias StatisticsDetailsViewStore = ViewStore<ViewState, StatisticsDetails.Action.ViewAction>

	@Environment(\.continuousClock) private var clock
	@Environment(\.safeAreaInsets) private var safeAreaInsets
	@State private var sheetContentSize: CGSize = .zero
	@State private var windowContentSize: CGSize = .zero

	struct ViewState: Equatable {
		let filter: TrackableFilter

		@BindingViewState var sheetDetent: PresentationDetent
		let ignoreSheetSizeForBackdrop: Bool
		let sources: TrackableFilter.Sources?
		let willAdjustLaneLayoutAt: Date
		let backdropSize: CGSize
		let filtersSize: StatisticsFilterView.Size
	}

	public init(store: StoreOf<StatisticsDetails>) {
		self.store = store
	}

	public var body: some View {
		WithViewStore(store, observe: ViewState.init, send: { .view($0) }, content: { viewStore in
			VStack {
				VStack {
					if let sources = viewStore.sources {
						StatisticsFilterView(
							sources: sources,
							filter: viewStore.filter,
							size: viewStore.filtersSize
						)
					}

					StatisticsDetailsChartsView(
						store: store.scope(state: \.charts, action: /StatisticsDetails.Action.InternalAction.charts)
					)
					.padding(.horizontal)
					.layoutPriority(1)
				}
				.frame(
					idealWidth: viewStore.backdropSize.width,
					maxHeight: viewStore.backdropSize.height == .zero ? nil : viewStore.backdropSize.height
				)

				Spacer()
			}
			.measure(key: WindowContentSizeKey.self, to: $windowContentSize)
			.toolbar(.hidden, for: .tabBar)
			.navigationTitle(viewStore.sources?.bowler.name ?? "")
			.navigationBarTitleDisplayMode(.inline)
			.toolbar {
				ToolbarItem(placement: .navigationBarTrailing) {
					FilterButton(isActive: false) { viewStore.send(.didTapSourcePicker) }
				}
			}
			.sheet(
				store: store.scope(state: \.$destination, action: { .internal(.destination($0)) }),
				state: /StatisticsDetails.Destination.State.list,
				action: StatisticsDetails.Destination.Action.list
			) { store in
				NavigationStack {
					List {
						StatisticsDetailsListView(store: store)
					}
					.toolbar(.hidden, for: .navigationBar)
				}
				.presentationBackgroundInteraction(.enabled(upThrough: .medium))
				.presentationDetents(
					[
						StatisticsDetails.defaultSheetDetent,
						.medium,
						.large,
					],
					selection: viewStore.$sheetDetent
				)
				.interactiveDismissDisabled()
				.measure(key: SheetContentSizeKey.self, to: $sheetContentSize)
			}
			.sheet(
				store: store.scope(state: \.$destination, action: { .internal(.destination($0)) }),
				state: /StatisticsDetails.Destination.State.sourcePicker,
				action: StatisticsDetails.Destination.Action.sourcePicker
			) { store in
				NavigationStack {
					StatisticsSourcePickerView(store: store)
				}
				.presentationDetents([.medium, .large])
			}
			.onChange(of: viewStore.willAdjustLaneLayoutAt) { _ in
				viewStore.send(
					.didAdjustChartSize(
						backdropSize: getMeasuredBackdropSize(viewStore),
						filtersSize: getFilterViewSize(viewStore)
					),
					animation: .easeInOut
				)
			}
			.onChange(of: sheetContentSize) { _ in
				viewStore.send(
					.didAdjustChartSize(
						backdropSize: getMeasuredBackdropSize(viewStore),
						filtersSize: getFilterViewSize(viewStore)
					),
					animation: .easeInOut
				)
			}
			.task { await viewStore.send(.onAppear).finish() }
		})
	}

	private func getFilterViewSize(_ viewStore: StatisticsDetailsViewStore) -> StatisticsFilterView.Size {
		viewStore.sheetDetent == .medium ? .compact : .regular
	}

	private func getMeasuredBackdropSize(_ viewStore: StatisticsDetailsViewStore) -> CGSize {
		let sheetContentSize = viewStore.ignoreSheetSizeForBackdrop ? .zero : self.sheetContentSize
		return .init(
			width: windowContentSize.width,
			height: windowContentSize.height - sheetContentSize.height - safeAreaInsets.bottom
		)
	}
}

extension StatisticsDetailsView.ViewState {
	init(store: BindingViewStore<StatisticsDetails.State>) {
		self._sheetDetent = store.$sheetDetent
		self.filter = store.filter
		self.ignoreSheetSizeForBackdrop = store.destination == nil || store.sheetDetent == .large
		self.sources = store.sources
		self.willAdjustLaneLayoutAt = store.willAdjustLaneLayoutAt
		self.backdropSize = store.backdropSize
		self.filtersSize = store.filtersSize
	}
}

private struct SheetContentSizeKey: PreferenceKey, CGSizePreferenceKey {}
private struct WindowContentSizeKey: PreferenceKey, CGSizePreferenceKey {}
