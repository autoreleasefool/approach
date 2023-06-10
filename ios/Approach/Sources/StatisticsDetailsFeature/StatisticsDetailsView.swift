import AssetsLibrary
import ComposableArchitecture
import StatisticsLibrary
import StringsLibrary
import SwiftUI
import SwiftUIExtensionsLibrary
import ViewsLibrary

public struct StatisticsDetailsView: View {
	let store: StoreOf<StatisticsDetails>

	@State private var sectionHeaderContentSize: CGSize = .zero

	struct ViewState: Equatable {
		let sources: TrackableFilter.Sources?

		init(state: StatisticsDetails.State) {
			self.sources = state.sources
		}
	}

	enum ViewAction {
		case onAppear
		case didTapSourcePicker
	}

	public init(store: StoreOf<StatisticsDetails>) {
		self.store = store
	}

	public var body: some View {
		WithViewStore(store, observe: ViewState.init, send: StatisticsDetails.Action.init) { viewStore in
			StatisticsDetailsChartsView(
				store: store.scope(state: \.charts, action: /StatisticsDetails.Action.InternalAction.charts)
			)
			.toolbar(.hidden, for: .tabBar)
			.toolbar(.hidden, for: .navigationBar)
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
				.presentationDetents([.medium, .large])
				.interactiveDismissDisabled()
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
}

extension StatisticsDetails.Action {
	init(action: StatisticsDetailsView.ViewAction) {
		switch action {
		case .onAppear:
			self = .view(.onAppear)
		case .didTapSourcePicker:
			self = .view(.didTapSourcePicker)
		}
	}
}

private struct SectionHeaderContentSizeKey: PreferenceKey, CGSizePreferenceKey {}
