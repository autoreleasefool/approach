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

@ViewAction(for: GamesList.self)
public struct GamesListView: View {
	@Perception.Bindable public var store: StoreOf<GamesList>

	public init(store: StoreOf<GamesList>) {
		self.store = store
	}

	public var body: some View {
		WithPerceptionTracking {
			ResourceListView(
				store: store.scope(state: \.list, action: \.internal.list)
			) { game in
				Button { send(.didTapGame(game.id)) } label: {
					LabeledContent(Strings.Game.titleWithOrdinal(game.index + 1), value: "\(game.score)")
				}
				.if(store.list.editMode == .active) {
					$0.buttonStyle(.plain)
				}
				.if(store.list.editMode == .inactive) {
					$0.buttonStyle(.navigation)
				}
			} header: {
				GamesListHeaderView(scores: store.list.resources?.map { .init(index: $0.index, score: $0.score) } ?? [])
			} footer: {
				if store.isShowingArchiveTip {
					BasicTipView(tip: .gameArchiveTip) {
						send(.didTapArchiveTipDismissButton, animation: .default)
					}
				}
			}
			.toolbar {
				ToolbarItem(placement: .navigationBarTrailing) {
					EditButton { send(.didTapEditButton) }
				}

				if store.isSeriesSharingEnabled {
					ToolbarItem(placement: .navigationBarTrailing) {
						Button { send(.didTapShareButton) } label: {
							Image(systemSymbol: .squareAndArrowUp)
						}
					}
				}

				ToolbarItem(placement: .navigationBarTrailing) {
					AddButton { send(.didTapAddButton) }
				}
			}
			.navigationTitle(store.series.date.longFormat)
			.onAppear { send(.onAppear) }
			// TODO: enable errors
//			.errors(store: store.scope(state: \.errors, action: \.internal.errors))
			.sharing($store.scope(state: \.destination?.sharing, action: \.internal.destination.sharing))
			.gameEditor($store.scope(state: \.destination?.gameEditor, action: \.internal.destination.gameEditor))
			.seriesEditor($store.scope(state: \.destination?.seriesEditor, action: \.internal.destination.seriesEditor))
		}
	}
}

@MainActor extension View {
	fileprivate func sharing(_ store: Binding<StoreOf<Sharing>?>) -> some View {
		sheet(item: store) { store in
			NavigationStack {
				SharingView(store: store)
			}
		}
	}

	fileprivate func gameEditor(_ store: Binding<StoreOf<GamesEditor>?>) -> some View {
		navigationDestinationWrapper(item: store) { store in
			GamesEditorView(store: store)
		}
	}

	fileprivate func seriesEditor(_ store: Binding<StoreOf<SeriesEditor>?>) -> some View {
		sheet(item: store) { store in
			NavigationStack {
				SeriesEditorView(store: store)
			}
		}
	}
}
