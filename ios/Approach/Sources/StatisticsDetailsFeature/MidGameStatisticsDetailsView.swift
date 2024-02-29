import AssetsLibrary
import ComposableArchitecture
import ErrorsFeature
import ModelsLibrary
import StatisticsLibrary
import StringsLibrary
import SwiftUI
import SwiftUIExtensionsLibrary
import ViewsLibrary

@ViewAction(for: MidGameStatisticsDetails.self)
public struct MidGameStatisticsDetailsView: View {
	@Perception.Bindable public var store: StoreOf<MidGameStatisticsDetails>

	public init(store: StoreOf<MidGameStatisticsDetails>) {
		self.store = store
	}

	public var body: some View {
		WithPerceptionTracking {
			VStack {
				StatisticsDetailsListView(
					store: store.scope(state: \.list, action: \.internal.list)
				) {
					Section {
						Picker(
							Strings.Statistics.Filter.filterByGame,
							selection: $store.selectedGame
						) {
							Text(Strings.Statistics.Filter.allGames).tag(nil as Game.ID?)
							ForEach(store.games) {
								Text(Strings.Game.titleWithOrdinal($0.index + 1)).tag(Optional($0.id))
							}
						}
					}
				}
			}
			.navigationTitle(Strings.Statistics.title)
			.navigationBarTitleDisplayMode(.inline)
			.onAppear { send(.onAppear) }
			.onFirstAppear { send(.didFirstAppear) }
			.task { await send(.task).finish() }
			.errors(store: store.scope(state: \.errors, action: \.internal.errors))
		}
	}
}
