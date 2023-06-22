import AssetsLibrary
import ComposableArchitecture
import StatisticsLibrary
import StringsLibrary
import SwiftUI
import SwiftUIExtensionsLibrary
import ViewsLibrary

public struct StatisticsDetailsView: View {
	let store: StoreOf<StatisticsDetails>

	@Environment(\.continuousClock) private var clock
	@Environment(\.safeAreaInsets) private var safeAreaInsets
	@State private var sheetContentSize: CGSize = .zero
	@State private var windowContentSize: CGSize = .zero

	struct ViewState: Equatable {
		let filter: TrackableFilter

		let sheetDetent: PresentationDetent
		let ignoreSheetSizeForBackdrop: Bool
		let sources: TrackableFilter.Sources?
		let willAdjustLaneLayoutAt: Date
		let backdropSize: CGSize
		let filtersSize: StatisticsFilterView.Size

		init(state: StatisticsDetails.State) {
			self.filter = state.filter
			self.sheetDetent = state.sheetDetent
			self.ignoreSheetSizeForBackdrop = state.destination == nil || state.sheetDetent == .large
			self.sources = state.sources
			self.willAdjustLaneLayoutAt = state.willAdjustLaneLayoutAt
			self.backdropSize = state.backdropSize
			self.filtersSize = state.filtersSize
		}
	}

	enum ViewAction {
		case onAppear
		case didTapSourcePicker
		case didChangeDetent(PresentationDetent)
		case didAdjustChartSize(backdropSize: CGSize, filtersSize: StatisticsFilterView.Size)
	}

	public init(store: StoreOf<StatisticsDetails>) {
		self.store = store
	}

	public var body: some View {
		WithViewStore(store, observe: ViewState.init, send: StatisticsDetails.Action.init) { viewStore in
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
					selection: viewStore.binding(get: \.sheetDetent, send: ViewAction.didChangeDetent)
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
		}
	}

	private func getFilterViewSize(_ viewStore: ViewStore<ViewState, ViewAction>) -> StatisticsFilterView.Size {
		viewStore.sheetDetent == .medium ? .compact : .regular
	}

	private func getMeasuredBackdropSize(_ viewStore: ViewStore<ViewState, ViewAction>) -> CGSize {
		let sheetContentSize = viewStore.ignoreSheetSizeForBackdrop ? .zero : self.sheetContentSize
		return .init(
			width: windowContentSize.width,
			height: windowContentSize.height - sheetContentSize.height - safeAreaInsets.bottom
		)
	}
}

extension StatisticsDetails.Action {
	init(action: StatisticsDetailsView.ViewAction) {
		switch action {
		case .onAppear:
			self = .view(.onAppear)
		case .didTapSourcePicker:
			self = .view(.didTapSourcePicker)
		case let .didChangeDetent(newDetent):
			self = .view(.didChangeDetent(newDetent))
		case let .didAdjustChartSize(backdropSize, filtersSize):
			self = .view(.didAdjustChartSize(backdropSize: backdropSize, filtersSize: filtersSize))
		}
	}
}

private struct SheetContentSizeKey: PreferenceKey, CGSizePreferenceKey {}
private struct WindowContentSizeKey: PreferenceKey, CGSizePreferenceKey {}
