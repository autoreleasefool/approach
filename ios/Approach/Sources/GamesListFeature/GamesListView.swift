import AssetsLibrary
import ComposableArchitecture
import DateTimeLibrary
import ErrorsFeature
import ExtensionsLibrary
import FeatureActionLibrary
import GamesEditorFeature
import ModelsLibrary
import ResourceListLibrary
import SeriesEditorFeature
import SharingFeature
import StringsLibrary
import SwiftUI
import SwiftUIExtensionsLibrary
import TipsLibrary
import ViewsLibrary

public struct GamesListView: View {
	let store: StoreOf<GamesList>

	struct ViewState: Equatable {
		let title: String
		let editMode: EditMode
		let scores: [GamesListHeaderView.Score]
		let isSeriesSharingEnabled: Bool
		let isShowingArchiveTip: Bool

		init(state: GamesList.State) {
			self.title = state.series.date.longFormat
			self.editMode = state.list.editMode
			self.scores = state.list.resources?.map { .init(index: $0.index, score: $0.score) } ?? []
			self.isSeriesSharingEnabled = state.isSeriesSharingEnabled
			self.isShowingArchiveTip = state.isShowingArchiveTip
		}
	}

	public init(store: StoreOf<GamesList>) {
		self.store = store
	}

	public var body: some View {
		WithViewStore(store, observe: ViewState.init, send: { .view($0) }, content: { viewStore in
			ResourceListView(
				store: store.scope(state: \.list, action: \.internal.list)
			) { game in
				Button { viewStore.send(.didTapGame(game.id)) } label: {
					LabeledContent(Strings.Game.titleWithOrdinal(game.index + 1), value: "\(game.score)")
				}
				.if(viewStore.editMode == .active) {
					$0.buttonStyle(.plain)
				}
				.if(viewStore.editMode == .inactive) {
					$0.buttonStyle(.navigation)
				}
			} header: {
				GamesListHeaderView(scores: viewStore.scores)
			} footer: {
				if viewStore.isShowingArchiveTip {
					BasicTipView(tip: .gameArchiveTip) {
						viewStore.send(.didTapArchiveTipDismissButton, animation: .default)
					}
				}
			}
			.toolbar {
				ToolbarItem(placement: .navigationBarTrailing) {
					EditButton { viewStore.send(.didTapEditButton) }
				}

				if viewStore.isSeriesSharingEnabled {
					ToolbarItem(placement: .navigationBarTrailing) {
						Button { viewStore.send(.didTapShareButton) } label: {
							Image(systemSymbol: .squareAndArrowUp)
						}
					}
				}

				ToolbarItem(placement: .navigationBarTrailing) {
					AddButton { viewStore.send(.didTapAddButton) }
				}
			}
			.navigationTitle(viewStore.title)
			.onAppear { viewStore.send(.onAppear) }
		})
		.errors(store: store.scope(state: \.errors, action: \.internal.errors))
		.sharing(store.scope(state: \.$destination.sharing, action: \.internal.destination.sharing))
		.gameEditor(store.scope(state: \.$destination.gameEditor, action: \.internal.destination.gameEditor))
		.seriesEditor(store.scope(state: \.$destination.seriesEditor, action: \.internal.destination.seriesEditor))
	}
}

@MainActor extension View {
	fileprivate func sharing(_ store: PresentationStoreOf<Sharing>) -> some View {
		sheet(store: store) { store in
			NavigationStack {
				SharingView(store: store)
			}
		}
	}

	fileprivate func gameEditor(_ store: PresentationStoreOf<GamesEditor>) -> some View {
		navigationDestination(store: store) { store in
			GamesEditorView(store: store)
		}
	}

	fileprivate func seriesEditor(_ store: PresentationStoreOf<SeriesEditor>) -> some View {
		sheet(store: store) { store in
			NavigationStack {
				SeriesEditorView(store: store)
			}
		}
	}
}
