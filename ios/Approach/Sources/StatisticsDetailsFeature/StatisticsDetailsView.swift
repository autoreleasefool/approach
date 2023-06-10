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
	@State private var sectionHeaderContentSize: CGSize = .zero

	struct ViewState: Equatable {
		let sheetDetent: PresentationDetent
		let sources: TrackableFilter.Sources?
		let willAdjustLaneLayoutAt: Date
		let backdropSize: CGSize

		init(state: StatisticsDetails.State) {
			self.sheetDetent = state.sheetDetent
			self.sources = state.sources
			self.willAdjustLaneLayoutAt = state.willAdjustLaneLayoutAt
			self.backdropSize = state.backdropSize
		}
	}

	enum ViewAction {
		case onAppear
		case didTapSourcePicker
		case didChangeDetent(PresentationDetent)
		case didAdjustBackdropSize(CGSize)
	}

	public init(store: StoreOf<StatisticsDetails>) {
		self.store = store
	}

	public var body: some View {
		WithViewStore(store, observe: ViewState.init, send: StatisticsDetails.Action.init) { viewStore in
			VStack {
				StatisticsDetailsChartsView(
					store: store.scope(state: \.charts, action: /StatisticsDetails.Action.InternalAction.charts)
				)
				.frame(
					idealWidth: viewStore.backdropSize.width,
					maxHeight: viewStore.backdropSize.height == .zero ? nil : viewStore.backdropSize.height
				)

				Spacer()
			}
			.measure(key: WindowContentSizeKey.self, to: $windowContentSize)
			.toolbar(.hidden, for: .tabBar, .navigationBar)
			.sheet(
				store: store.scope(state: \.$destination, action: { .internal(.destination($0)) }),
				state: /StatisticsDetails.Destination.State.list,
				action: StatisticsDetails.Destination.Action.list
			) { store in
				List {
					if let sources = viewStore.sources {
						sourcesView(sources, viewStore)
					}

					StatisticsDetailsListView(store: store)
				}
				.padding(.top, -sectionHeaderContentSize.height)
				.presentationDragIndicator(.hidden)
				.presentationBackgroundInteraction(.enabled(upThrough: .medium))
				.presentationDetents(
					[
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
				viewStore.send(.didAdjustBackdropSize(getMeasuredBackdropSize(viewStore)), animation: .easeInOut)
			}
			.onChange(of: sheetContentSize) { _ in
				viewStore.send(.didAdjustBackdropSize(getMeasuredBackdropSize(viewStore)), animation: .easeInOut)
			}
			.task { await viewStore.send(.onAppear).finish() }
		}
	}

	private func sourcesView(
		_ sources: TrackableFilter.Sources,
		_ viewStore: ViewStore<ViewState, ViewAction>
	) -> some View {
		Section {
			HStack {
				VStack {
					if let bowler = sources.bowler {
						Text(bowler.name)
							.font(.headline)
					}

					if let league = sources.league {
						Text(league.name)
							.font(.subheadline)
					}
				}

				Button { viewStore.send(.didTapSourcePicker) } label: {
					Image(systemName: "chevron.down.circle")
						.resizable()
						.scaledToFit()
						.frame(width: .tinyIcon, height: .tinyIcon)
						.padding()
				}
				.buttonStyle(TappableElement())
			}
		} header: {
			Color.clear
				.measure(key: SectionHeaderContentSizeKey.self, to: $sectionHeaderContentSize)
		}
		.listRowInsets(EdgeInsets())
		.listRowBackground(Color.clear)
	}

	private func getMeasuredBackdropSize(_ viewStore: ViewStore<ViewState, ViewAction>) -> CGSize {
		let sheetContentSize = viewStore.sheetDetent == .large ? .zero : self.sheetContentSize
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
		case let .didAdjustBackdropSize(newSize):
			self = .view(.didAdjustBackdropSize(newSize))
		}
	}
}

private struct SectionHeaderContentSizeKey: PreferenceKey, CGSizePreferenceKey {}
private struct SheetContentSizeKey: PreferenceKey, CGSizePreferenceKey {}
private struct WindowContentSizeKey: PreferenceKey, CGSizePreferenceKey {}
