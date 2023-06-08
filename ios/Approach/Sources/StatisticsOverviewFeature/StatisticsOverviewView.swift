import AssetsLibrary
import ComposableArchitecture
import StatisticsDetailsFeature
import StringsLibrary
import SwiftUI
import ViewsLibrary

public struct StatisticsOverviewView: View {
	let store: StoreOf<StatisticsOverview>

	struct ViewState: Equatable {
		let isShowingOverviewHint: Bool
		let isShowingDetailsHint: Bool

		init(state: StatisticsOverview.State) {
			self.isShowingOverviewHint = state.isShowingOverviewHint
			self.isShowingDetailsHint = state.isShowingDetailsHint
		}
	}

	enum ViewAction {
		case didTapDismissOverviewHint
		case didTapDismissDetailsHint
		case didTapViewDetailedStatistics
	}

	public init(store: StoreOf<StatisticsOverview>) {
		self.store = store
	}

	public var body: some View {
		WithViewStore(store, observe: ViewState.init, send: StatisticsOverview.Action.init) { viewStore in
			List {
				if viewStore.isShowingOverviewHint {
					hintView(
						title: Strings.Statistics.Overview.GetAnOverviewHint.title,
						message: Strings.Statistics.Overview.GetAnOverviewHint.message,
						onDismiss: { viewStore.send(.didTapDismissOverviewHint, animation: .default) }
					)
				}

				if viewStore.isShowingDetailsHint {
					hintView(
						title: Strings.Statistics.Overview.ViewMoreDetailsHint.title,
						message: Strings.Statistics.Overview.ViewMoreDetailsHint.message,
						onDismiss: { viewStore.send(.didTapDismissDetailsHint, animation: .default) }
					)
				}

				Section {
					Button { viewStore.send(.didTapViewDetailedStatistics) } label: {
						HStack {
							Text(Strings.Statistics.Overview.viewDetailedStatistics)
							Spacer()
							Image(systemName: "chevron.forward")
								.resizable()
								.scaledToFit()
								.frame(width: .tinyIcon, height: .tinyIcon)
								.foregroundColor(Color(uiColor: .secondaryLabel))
						}
						.contentShape(Rectangle())
					}
					.buttonStyle(TappableElement())
				}
			}
			.navigationTitle(Strings.Statistics.title)
		}
		.sheet(
			store: store.scope(state: \.$destination, action: { .internal(.destination($0)) }),
			state: /StatisticsOverview.Destination.State.filter,
			action: StatisticsOverview.Destination.Action.filter
		) { store in
			NavigationStack {
				StatisticsDetailsFilterView(store: store)
			}
			.presentationDetents([.medium, .large])
		}
		.navigationDestination(
			store: store.scope(state: \.$destination, action: { .internal(.destination($0)) }),
			state: /StatisticsOverview.Destination.State.details,
			action: StatisticsOverview.Destination.Action.details
		) { store in
			StatisticsDetailsView(store: store)
		}
	}

	private func hintView(title: String, message: String, onDismiss: @escaping () -> Void) -> some View {
		Section {
			VStack(alignment: .leading, spacing: 0) {
				HStack(alignment: .center, spacing: 0) {
					Text(title)
						.font(.headline)
						.frame(maxWidth: .infinity, alignment: .leading)
					Button(action: onDismiss) {
						Image(systemName: "xmark")
							.resizable()
							.scaledToFit()
							.frame(width: .smallIcon, height: .smallSpacing)
							.padding(.vertical)
							.padding(.leading)
					}
					.buttonStyle(TappableElement())
				}
				.frame(maxWidth: .infinity)

				Text(message)
					.frame(maxWidth: .infinity, alignment: .leading)
			}
			.padding(.horizontal)
			.padding(.bottom)
		}
		.listRowInsets(EdgeInsets())
	}
}

extension StatisticsOverview.Action {
	init(action: StatisticsOverviewView.ViewAction) {
		switch action {
		case .didTapDismissOverviewHint:
			self = .view(.didTapDismissOverviewHint)
		case .didTapDismissDetailsHint:
			self = .view(.didTapDismissDetailsHint)
		case .didTapViewDetailedStatistics:
			self = .view(.didTapViewDetailedStatistics)
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
