import Algorithms
import ComposableArchitecture
import Foundation
import ModelsLibrary
import ScoreSheetFeature
import StringsLibrary
import SwiftUI
import ViewsLibrary

public struct SharingView: View {
	let store: StoreOf<Sharing>

	public init(store: StoreOf<Sharing>) {
		self.store = store
	}

	public var body: some View {
		WithViewStore(store, observe: { $0 }, send: { .view($0) }, content: { viewStore in
			VStack(spacing: 0) {
				List {
					preview(viewStore)
					styles(viewStore)
				}

				Divider()

				Button {
					viewStore.send(.didTapShareButton)
				} label: {
					Text(Strings.Action.share)
						.frame(maxWidth: .infinity)
				}
				.modifier(PrimaryButton())
				.padding()
			}
			.navigationTitle(viewStore.navigationTitle)
			.navigationBarTitleDisplayMode(.inline)
			.toolbar {
				ToolbarItem(placement: .navigationBarLeading) {
					Button(Strings.Action.done) { viewStore.send(.didTapDoneButton) }
				}
			}
//			.onFirstAppear { viewStore.send(.didFirstAppear) }
		})
		.errors(store: store.scope(state: \.errors, action: { .internal(.errors($0)) }))
	}

	private func preview(_ viewStore: ViewStore<Sharing.State, Sharing.Action.ViewAction>) -> some View {
		Section(Strings.Sharing.Preview.title) {
			if viewStore.scores.isEmpty {
				ListProgressView()
			} else {
				ShareableScoreSheetView(
					games: Array(viewStore.shareableGames.prefix(2)),
					style: viewStore.scoreSheetStyle
				)
			}
		}
		.cornerRadius(.standardRadius)
		.listRowBackground(Color.clear)
	}

	private func styles(_ viewStore: ViewStore<Sharing.State, Sharing.Action.ViewAction>) -> some View {
		Section(Strings.Sharing.Style.title) {
			Grid(horizontalSpacing: .standardSpacing, verticalSpacing: .standardSpacing) {
				ForEach(ShareableScoreSheetStyleGroup.scoreSheetStyles) { group in
					GridRow {
						ForEach(group.styles) { style in
							Button { viewStore.send(.didTapStyle(style)) } label: {
								PreviewingShareableScoreSheetView(style: style)
							}
							.buttonStyle(TappableElement())
						}
					}
				}
			}
		}
		.listRowBackground(Color.clear)
	}
}

extension ShareableScoreSheetView.Style: Identifiable {
	public var id: String { title }
}

struct ShareableScoreSheetStyleGroup: Identifiable {
	let id = UUID()
	let styles: [ShareableScoreSheetView.Style]

	static let scoreSheetStyles = ShareableScoreSheetView.Style
		.allStyles
		.chunks(ofCount: 3)
		.map { ShareableScoreSheetStyleGroup(styles: Array($0)) }
}

#if DEBUG
struct SharingViewPreview: PreviewProvider {
	static var previews: some View {
		NavigationStack {
			SharingView(store: .init(
				initialState: {
					var state = Sharing.State(dataSource: .games([UUID(0)]))
					state.games = .init(uniqueElements: [
						.init(
							id: UUID(0),
							index: 0,
							score: 123,
							scoringMethod: .byFrame,
							frames: [],
							bowler: .init(name: "Joseph"),
							league: .init(name: "Majors"),
							series: .init(
								date: Date(),
								alley: .init(name: "Skyview Lanes")
							)
						),
						.init(
							id: UUID(1),
							index: 1,
							score: 123,
							scoringMethod: .byFrame,
							frames: [],
							bowler: .init(name: "Joseph"),
							league: .init(name: "Majors"),
							series: .init(
								date: Date(),
								alley: .init(name: "Skyview Lanes")
							)
						),
					])

					state.scores = [
						UUID(0): Game.FRAME_INDICES.map {
							.init(
								index: $0,
								rolls: [
									.init(index: 0, display: "10", didFoul: true),
									.init(index: 1, display: "HP", didFoul: false),
									.init(index: 2, display: "3", didFoul: false),
								],
								score: 32
							)
						},
						UUID(1): Game.FRAME_INDICES.map {
							.init(
								index: $0,
								rolls: [
									.init(index: 0, display: "10", didFoul: true),
									.init(index: 1, display: "HP", didFoul: false),
									.init(index: 2, display: "3", didFoul: false),
								],
								score: 32
							)
						},
					]

					return state
				}(),
				reducer: Sharing.init
			))
		}
	}
}
#endif
