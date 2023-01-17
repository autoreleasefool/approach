import AssetsLibrary
import ComposableArchitecture
import StringsLibrary
import SwiftUI
import ViewsLibrary

public struct ResourceListView<
	R: ResourceListItem,
	Q: Equatable,
	Row: View,
	EmptyContent: View,
	ErrorContent: View
>: View {
	let store: StoreOf<ResourceList<R, Q>>

	struct ViewState: Equatable {
		let listContent: ListContent
		let features: [ResourceList<R, Q>.Feature]

		init(state: ResourceList<R, Q>.State) {
			self.features = state.features
			if let error = state.error {
				self.listContent = .error(error)
			} else if let resources = state.resources {
				self.listContent = .loaded(resources)
			} else {
				self.listContent = .loading
			}
		}
	}

	enum ViewAction {
		case didObserveData
		case didTapErrorButton
		case didTapAddButton
		case didSwipeToDelete(R)
		case didSwipeToEdit(R)
	}

	let content: (IdentifiedArrayOf<R>) -> Row
	let empty: () -> EmptyContent
	let error: (ResourceListError) -> ErrorContent

	public init(
		store: StoreOf<ResourceList<R, Q>>,
		@ViewBuilder content: @escaping (IdentifiedArrayOf<R>) -> Row,
		@ViewBuilder empty: @escaping () -> EmptyContent,
		@ViewBuilder error: @escaping (ResourceListError) -> ErrorContent
	) {
		self.store = store
		self.content = content
		self.empty = empty
		self.error = error
	}

	public var body: some View {
		WithViewStore(store, observe: ViewState.init, send: map(viewAction:)) { viewStore in
			Group {
				switch viewStore.listContent {
				case .notLoaded:
					List {
						EmptyView()
					}
					.listStyle(.insetGrouped)

				case .loading:
					List {
						ProgressView()
					}
					.listStyle(.insetGrouped)

				case let .loaded(elements):
					if elements.isEmpty {
						empty()
					} else {
						List {
							content(elements)
						}
						.listStyle(.insetGrouped)
					}

				case let .error(listError):
					error(listError)
				}
			}
		}
	}

	private func map(viewAction: ViewAction) -> ResourceList<R, Q>.Action {
		switch viewAction {
		case .didObserveData:
			return .view(.didObserveData)
		case .didTapAddButton:
			return .view(.didTapAddButton)
		case .didTapErrorButton:
			return .view(.didTapErrorButton)
		case let .didSwipeToEdit(r):
			return .view(.didSwipeToEdit(r))
		case let .didSwipeToDelete(r):
			return .view(.didSwipeToDelete(r))
		}
	}
}

extension ResourceListView {
	enum ListContent: Equatable {
		case notLoaded
		case loading
		case loaded(IdentifiedArrayOf<R>)
		case error(ResourceListError)
	}
}
